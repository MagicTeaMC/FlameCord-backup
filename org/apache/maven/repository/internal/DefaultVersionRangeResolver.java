package org.apache.maven.repository.internal;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.apache.maven.artifact.repository.metadata.io.xpp3.MetadataXpp3Reader;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.metadata.DefaultMetadata;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.util.version.GenericVersionScheme;
import org.eclipse.aether.version.InvalidVersionSpecificationException;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

@Named
@Singleton
public class DefaultVersionRangeResolver implements VersionRangeResolver, Service {
  private static final String MAVEN_METADATA_XML = "maven-metadata.xml";
  
  private MetadataResolver metadataResolver;
  
  private SyncContextFactory syncContextFactory;
  
  private RepositoryEventDispatcher repositoryEventDispatcher;
  
  public DefaultVersionRangeResolver() {}
  
  @Inject
  DefaultVersionRangeResolver(MetadataResolver metadataResolver, SyncContextFactory syncContextFactory, RepositoryEventDispatcher repositoryEventDispatcher) {
    setMetadataResolver(metadataResolver);
    setSyncContextFactory(syncContextFactory);
    setRepositoryEventDispatcher(repositoryEventDispatcher);
  }
  
  public void initService(ServiceLocator locator) {
    setMetadataResolver((MetadataResolver)locator.getService(MetadataResolver.class));
    setSyncContextFactory((SyncContextFactory)locator.getService(SyncContextFactory.class));
    setRepositoryEventDispatcher((RepositoryEventDispatcher)locator.getService(RepositoryEventDispatcher.class));
  }
  
  public DefaultVersionRangeResolver setMetadataResolver(MetadataResolver metadataResolver) {
    this.metadataResolver = Objects.<MetadataResolver>requireNonNull(metadataResolver, "metadataResolver cannot be null");
    return this;
  }
  
  public DefaultVersionRangeResolver setSyncContextFactory(SyncContextFactory syncContextFactory) {
    this.syncContextFactory = Objects.<SyncContextFactory>requireNonNull(syncContextFactory, "syncContextFactory cannot be null");
    return this;
  }
  
  public DefaultVersionRangeResolver setRepositoryEventDispatcher(RepositoryEventDispatcher repositoryEventDispatcher) {
    this.repositoryEventDispatcher = Objects.<RepositoryEventDispatcher>requireNonNull(repositoryEventDispatcher, "repositoryEventDispatcher cannot be null");
    return this;
  }
  
  public VersionRangeResult resolveVersionRange(RepositorySystemSession session, VersionRangeRequest request) throws VersionRangeResolutionException {
    VersionConstraint versionConstraint;
    VersionRangeResult result = new VersionRangeResult(request);
    GenericVersionScheme genericVersionScheme = new GenericVersionScheme();
    try {
      versionConstraint = genericVersionScheme.parseVersionConstraint(request.getArtifact().getVersion());
    } catch (InvalidVersionSpecificationException e) {
      result.addException((Exception)e);
      throw new VersionRangeResolutionException(result);
    } 
    result.setVersionConstraint(versionConstraint);
    if (versionConstraint.getRange() == null) {
      result.addVersion(versionConstraint.getVersion());
    } else {
      Map<String, ArtifactRepository> versionIndex = getVersions(session, result, request);
      List<Version> versions = new ArrayList<>();
      for (Map.Entry<String, ArtifactRepository> v : versionIndex.entrySet()) {
        try {
          Version ver = genericVersionScheme.parseVersion(v.getKey());
          if (versionConstraint.containsVersion(ver)) {
            versions.add(ver);
            result.setRepository(ver, v.getValue());
          } 
        } catch (InvalidVersionSpecificationException e) {
          result.addException((Exception)e);
        } 
      } 
      Collections.sort(versions);
      result.setVersions(versions);
    } 
    return result;
  }
  
  private Map<String, ArtifactRepository> getVersions(RepositorySystemSession session, VersionRangeResult result, VersionRangeRequest request) {
    RequestTrace trace = RequestTrace.newChild(request.getTrace(), request);
    Map<String, ArtifactRepository> versionIndex = new HashMap<>();
    DefaultMetadata defaultMetadata = new DefaultMetadata(request.getArtifact().getGroupId(), request.getArtifact().getArtifactId(), "maven-metadata.xml", Metadata.Nature.RELEASE_OR_SNAPSHOT);
    List<MetadataRequest> metadataRequests = new ArrayList<>(request.getRepositories().size());
    metadataRequests.add(new MetadataRequest((Metadata)defaultMetadata, null, request.getRequestContext()));
    for (RemoteRepository repository : request.getRepositories()) {
      MetadataRequest metadataRequest = new MetadataRequest((Metadata)defaultMetadata, repository, request.getRequestContext());
      metadataRequest.setDeleteLocalCopyIfMissing(true);
      metadataRequest.setTrace(trace);
      metadataRequests.add(metadataRequest);
    } 
    List<MetadataResult> metadataResults = this.metadataResolver.resolveMetadata(session, metadataRequests);
    WorkspaceReader workspace = session.getWorkspaceReader();
    if (workspace != null) {
      List<String> versions = workspace.findVersions(request.getArtifact());
      for (String version : versions)
        versionIndex.put(version, workspace.getRepository()); 
    } 
    for (MetadataResult metadataResult : metadataResults) {
      LocalRepository localRepository;
      result.addException(metadataResult.getException());
      RemoteRepository remoteRepository = metadataResult.getRequest().getRepository();
      if (remoteRepository == null)
        localRepository = session.getLocalRepository(); 
      Versioning versioning = readVersions(session, trace, metadataResult.getMetadata(), (ArtifactRepository)localRepository, result);
      for (String version : versioning.getVersions()) {
        if (!versionIndex.containsKey(version))
          versionIndex.put(version, localRepository); 
      } 
    } 
    return versionIndex;
  }
  
  private Versioning readVersions(RepositorySystemSession session, RequestTrace trace, Metadata metadata, ArtifactRepository repository, VersionRangeResult result) {
    Versioning versioning = null;
    try {
      if (metadata != null)
        try (SyncContext syncContext = this.syncContextFactory.newInstance(session, true)) {
          syncContext.acquire(null, Collections.singleton(metadata));
          if (metadata.getFile() != null && metadata.getFile().exists())
            try (InputStream in = new FileInputStream(metadata.getFile())) {
              versioning = (new MetadataXpp3Reader()).read(in, false).getVersioning();
            }  
        }  
    } catch (Exception e) {
      invalidMetadata(session, trace, metadata, repository, e);
      result.addException(e);
    } 
    return (versioning != null) ? versioning : new Versioning();
  }
  
  private void invalidMetadata(RepositorySystemSession session, RequestTrace trace, Metadata metadata, ArtifactRepository repository, Exception exception) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.METADATA_INVALID);
    event.setTrace(trace);
    event.setMetadata(metadata);
    event.setException(exception);
    event.setRepository(repository);
    this.repositoryEventDispatcher.dispatch(event.build());
  }
}
