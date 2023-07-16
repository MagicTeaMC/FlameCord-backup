package org.apache.maven.repository.internal;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.DistributionManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.Relocation;
import org.apache.maven.model.building.DefaultModelBuilderFactory;
import org.apache.maven.model.building.DefaultModelBuildingRequest;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelBuilder;
import org.apache.maven.model.building.ModelBuildingException;
import org.apache.maven.model.building.ModelBuildingRequest;
import org.apache.maven.model.building.ModelProblem;
import org.apache.maven.model.building.ModelSource;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicy;
import org.eclipse.aether.resolution.ArtifactDescriptorPolicyRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRequest;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.eclipse.aether.resolution.VersionResult;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;

@Named
@Singleton
public class DefaultArtifactDescriptorReader implements ArtifactDescriptorReader, Service {
  private RemoteRepositoryManager remoteRepositoryManager;
  
  private VersionResolver versionResolver;
  
  private VersionRangeResolver versionRangeResolver;
  
  private ArtifactResolver artifactResolver;
  
  private RepositoryEventDispatcher repositoryEventDispatcher;
  
  private ModelBuilder modelBuilder;
  
  public DefaultArtifactDescriptorReader() {}
  
  @Inject
  DefaultArtifactDescriptorReader(RemoteRepositoryManager remoteRepositoryManager, VersionResolver versionResolver, VersionRangeResolver versionRangeResolver, ArtifactResolver artifactResolver, ModelBuilder modelBuilder, RepositoryEventDispatcher repositoryEventDispatcher) {
    setRemoteRepositoryManager(remoteRepositoryManager);
    setVersionResolver(versionResolver);
    setVersionRangeResolver(versionRangeResolver);
    setArtifactResolver(artifactResolver);
    setModelBuilder(modelBuilder);
    setRepositoryEventDispatcher(repositoryEventDispatcher);
  }
  
  public void initService(ServiceLocator locator) {
    setRemoteRepositoryManager((RemoteRepositoryManager)locator.getService(RemoteRepositoryManager.class));
    setVersionResolver((VersionResolver)locator.getService(VersionResolver.class));
    setVersionRangeResolver((VersionRangeResolver)locator.getService(VersionRangeResolver.class));
    setArtifactResolver((ArtifactResolver)locator.getService(ArtifactResolver.class));
    this.modelBuilder = (ModelBuilder)locator.getService(ModelBuilder.class);
    if (this.modelBuilder == null)
      setModelBuilder((ModelBuilder)(new DefaultModelBuilderFactory()).newInstance()); 
    setRepositoryEventDispatcher((RepositoryEventDispatcher)locator.getService(RepositoryEventDispatcher.class));
  }
  
  public DefaultArtifactDescriptorReader setRemoteRepositoryManager(RemoteRepositoryManager remoteRepositoryManager) {
    this.remoteRepositoryManager = Objects.<RemoteRepositoryManager>requireNonNull(remoteRepositoryManager, "remoteRepositoryManager cannot be null");
    return this;
  }
  
  public DefaultArtifactDescriptorReader setVersionResolver(VersionResolver versionResolver) {
    this.versionResolver = Objects.<VersionResolver>requireNonNull(versionResolver, "versionResolver cannot be null");
    return this;
  }
  
  public DefaultArtifactDescriptorReader setVersionRangeResolver(VersionRangeResolver versionRangeResolver) {
    this
      .versionRangeResolver = Objects.<VersionRangeResolver>requireNonNull(versionRangeResolver, "versionRangeResolver cannot be null");
    return this;
  }
  
  public DefaultArtifactDescriptorReader setArtifactResolver(ArtifactResolver artifactResolver) {
    this.artifactResolver = Objects.<ArtifactResolver>requireNonNull(artifactResolver, "artifactResolver cannot be null");
    return this;
  }
  
  public DefaultArtifactDescriptorReader setRepositoryEventDispatcher(RepositoryEventDispatcher repositoryEventDispatcher) {
    this.repositoryEventDispatcher = Objects.<RepositoryEventDispatcher>requireNonNull(repositoryEventDispatcher, "repositoryEventDispatcher cannot be null");
    return this;
  }
  
  public DefaultArtifactDescriptorReader setModelBuilder(ModelBuilder modelBuilder) {
    this.modelBuilder = Objects.<ModelBuilder>requireNonNull(modelBuilder, "modelBuilder cannot be null");
    return this;
  }
  
  public ArtifactDescriptorResult readArtifactDescriptor(RepositorySystemSession session, ArtifactDescriptorRequest request) throws ArtifactDescriptorException {
    ArtifactDescriptorResult result = new ArtifactDescriptorResult(request);
    Model model = loadPom(session, request, result);
    if (model != null) {
      Map<String, Object> config = session.getConfigProperties();
      ArtifactDescriptorReaderDelegate delegate = (ArtifactDescriptorReaderDelegate)config.get(ArtifactDescriptorReaderDelegate.class.getName());
      if (delegate == null)
        delegate = new ArtifactDescriptorReaderDelegate(); 
      delegate.populateResult(session, result, model);
    } 
    return result;
  }
  
  private Model loadPom(RepositorySystemSession session, ArtifactDescriptorRequest request, ArtifactDescriptorResult result) throws ArtifactDescriptorException {
    Model model;
    RequestTrace trace = RequestTrace.newChild(request.getTrace(), request);
    Set<String> visited = new LinkedHashSet<>();
    Artifact a = request.getArtifact();
    while (true) {
      ArtifactResult resolveResult;
      Artifact pomArtifact = ArtifactDescriptorUtils.toPomArtifact(a);
      try {
        VersionRequest versionRequest = new VersionRequest(a, request.getRepositories(), request.getRequestContext());
        versionRequest.setTrace(trace);
        VersionResult versionResult = this.versionResolver.resolveVersion(session, versionRequest);
        a = a.setVersion(versionResult.getVersion());
        versionRequest = new VersionRequest(pomArtifact, request.getRepositories(), request.getRequestContext());
        versionRequest.setTrace(trace);
        versionResult = this.versionResolver.resolveVersion(session, versionRequest);
        pomArtifact = pomArtifact.setVersion(versionResult.getVersion());
      } catch (VersionResolutionException e) {
        result.addException((Exception)e);
        throw new ArtifactDescriptorException(result);
      } 
      if (!visited.add(a.getGroupId() + ':' + a.getArtifactId() + ':' + a.getBaseVersion())) {
        RepositoryException exception = new RepositoryException("Artifact relocations form a cycle: " + visited);
        invalidDescriptor(session, trace, a, (Exception)exception);
        if ((getPolicy(session, a, request) & 0x2) != 0)
          return null; 
        result.addException((Exception)exception);
        throw new ArtifactDescriptorException(result);
      } 
      try {
        ArtifactRequest resolveRequest = new ArtifactRequest(pomArtifact, request.getRepositories(), request.getRequestContext());
        resolveRequest.setTrace(trace);
        resolveResult = this.artifactResolver.resolveArtifact(session, resolveRequest);
        pomArtifact = resolveResult.getArtifact();
        result.setRepository(resolveResult.getRepository());
      } catch (ArtifactResolutionException e) {
        if (e.getCause() instanceof org.eclipse.aether.transfer.ArtifactNotFoundException) {
          missingDescriptor(session, trace, a, (Exception)e.getCause());
          if ((getPolicy(session, a, request) & 0x1) != 0)
            return null; 
        } 
        result.addException((Exception)e);
        throw new ArtifactDescriptorException(result);
      } 
      WorkspaceReader workspace = session.getWorkspaceReader();
      if (workspace instanceof MavenWorkspaceReader) {
        model = ((MavenWorkspaceReader)workspace).findModel(pomArtifact);
        if (model != null)
          return model; 
      } 
      try {
        DefaultModelBuildingRequest defaultModelBuildingRequest = new DefaultModelBuildingRequest();
        defaultModelBuildingRequest.setValidationLevel(0);
        defaultModelBuildingRequest.setProcessPlugins(false);
        defaultModelBuildingRequest.setTwoPhaseBuilding(false);
        defaultModelBuildingRequest.setSystemProperties(toProperties(session.getSystemProperties()));
        defaultModelBuildingRequest.setUserProperties(toProperties(session.getUserProperties()));
        defaultModelBuildingRequest.setModelCache(DefaultModelCache.newInstance(session));
        defaultModelBuildingRequest.setModelResolver(new DefaultModelResolver(session, trace.newChild(defaultModelBuildingRequest), request
              .getRequestContext(), this.artifactResolver, this.versionRangeResolver, this.remoteRepositoryManager, request
              
              .getRepositories()));
        if (resolveResult.getRepository() instanceof org.eclipse.aether.repository.WorkspaceRepository) {
          defaultModelBuildingRequest.setPomFile(pomArtifact.getFile());
        } else {
          defaultModelBuildingRequest.setModelSource((ModelSource)new FileModelSource(pomArtifact.getFile()));
        } 
        model = this.modelBuilder.build((ModelBuildingRequest)defaultModelBuildingRequest).getEffectiveModel();
      } catch (ModelBuildingException e) {
        for (ModelProblem problem : e.getProblems()) {
          if (problem.getException() instanceof org.apache.maven.model.resolution.UnresolvableModelException) {
            result.addException(problem.getException());
            throw new ArtifactDescriptorException(result);
          } 
        } 
        invalidDescriptor(session, trace, a, (Exception)e);
        if ((getPolicy(session, a, request) & 0x2) != 0)
          return null; 
        result.addException((Exception)e);
        throw new ArtifactDescriptorException(result);
      } 
      Relocation relocation = getRelocation(model);
      if (relocation != null) {
        result.addRelocation(a);
        RelocatedArtifact relocatedArtifact = new RelocatedArtifact(a, relocation.getGroupId(), relocation.getArtifactId(), relocation.getVersion(), relocation.getMessage());
        result.setArtifact((Artifact)relocatedArtifact);
        continue;
      } 
      break;
    } 
    return model;
  }
  
  private Properties toProperties(Map<String, String> map) {
    Properties props = new Properties();
    props.putAll(map);
    return props;
  }
  
  private Relocation getRelocation(Model model) {
    Relocation relocation = null;
    DistributionManagement distMgmt = model.getDistributionManagement();
    if (distMgmt != null)
      relocation = distMgmt.getRelocation(); 
    return relocation;
  }
  
  private void missingDescriptor(RepositorySystemSession session, RequestTrace trace, Artifact artifact, Exception exception) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.ARTIFACT_DESCRIPTOR_MISSING);
    event.setTrace(trace);
    event.setArtifact(artifact);
    event.setException(exception);
    this.repositoryEventDispatcher.dispatch(event.build());
  }
  
  private void invalidDescriptor(RepositorySystemSession session, RequestTrace trace, Artifact artifact, Exception exception) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.ARTIFACT_DESCRIPTOR_INVALID);
    event.setTrace(trace);
    event.setArtifact(artifact);
    event.setException(exception);
    this.repositoryEventDispatcher.dispatch(event.build());
  }
  
  private int getPolicy(RepositorySystemSession session, Artifact a, ArtifactDescriptorRequest request) {
    ArtifactDescriptorPolicy policy = session.getArtifactDescriptorPolicy();
    if (policy == null)
      return 0; 
    return policy.getPolicy(session, new ArtifactDescriptorPolicyRequest(a, request.getRequestContext()));
  }
}
