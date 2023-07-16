package org.apache.maven.repository.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.FileModelSource;
import org.apache.maven.model.building.ModelSource;
import org.apache.maven.model.resolution.InvalidRepositoryException;
import org.apache.maven.model.resolution.ModelResolver;
import org.apache.maven.model.resolution.UnresolvableModelException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;

class DefaultModelResolver implements ModelResolver {
  private final RepositorySystemSession session;
  
  private final RequestTrace trace;
  
  private final String context;
  
  private List<RemoteRepository> repositories;
  
  private final List<RemoteRepository> externalRepositories;
  
  private final ArtifactResolver resolver;
  
  private final VersionRangeResolver versionRangeResolver;
  
  private final RemoteRepositoryManager remoteRepositoryManager;
  
  private final Set<String> repositoryIds;
  
  DefaultModelResolver(RepositorySystemSession session, RequestTrace trace, String context, ArtifactResolver resolver, VersionRangeResolver versionRangeResolver, RemoteRepositoryManager remoteRepositoryManager, List<RemoteRepository> repositories) {
    this.session = session;
    this.trace = trace;
    this.context = context;
    this.resolver = resolver;
    this.versionRangeResolver = versionRangeResolver;
    this.remoteRepositoryManager = remoteRepositoryManager;
    this.repositories = repositories;
    this.externalRepositories = Collections.unmodifiableList(new ArrayList<>(repositories));
    this.repositoryIds = new HashSet<>();
  }
  
  private DefaultModelResolver(DefaultModelResolver original) {
    this.session = original.session;
    this.trace = original.trace;
    this.context = original.context;
    this.resolver = original.resolver;
    this.versionRangeResolver = original.versionRangeResolver;
    this.remoteRepositoryManager = original.remoteRepositoryManager;
    this.repositories = new ArrayList<>(original.repositories);
    this.externalRepositories = original.externalRepositories;
    this.repositoryIds = new HashSet<>(original.repositoryIds);
  }
  
  public void addRepository(Repository repository) throws InvalidRepositoryException {
    addRepository(repository, false);
  }
  
  public void addRepository(Repository repository, boolean replace) throws InvalidRepositoryException {
    if (this.session.isIgnoreArtifactDescriptorRepositories())
      return; 
    if (!this.repositoryIds.add(repository.getId())) {
      if (!replace)
        return; 
      removeMatchingRepository(this.repositories, repository.getId());
    } 
    List<RemoteRepository> newRepositories = Collections.singletonList(ArtifactDescriptorUtils.toRemoteRepository(repository));
    this
      .repositories = this.remoteRepositoryManager.aggregateRepositories(this.session, this.repositories, newRepositories, true);
  }
  
  private static void removeMatchingRepository(Iterable<RemoteRepository> repositories, String id) {
    Iterator<RemoteRepository> iterator = repositories.iterator();
    while (iterator.hasNext()) {
      RemoteRepository remoteRepository = iterator.next();
      if (remoteRepository.getId().equals(id))
        iterator.remove(); 
    } 
  }
  
  public ModelResolver newCopy() {
    return new DefaultModelResolver(this);
  }
  
  public ModelSource resolveModel(String groupId, String artifactId, String version) throws UnresolvableModelException {
    Artifact artifact;
    DefaultArtifact defaultArtifact = new DefaultArtifact(groupId, artifactId, "", "pom", version);
    try {
      ArtifactRequest request = new ArtifactRequest((Artifact)defaultArtifact, this.repositories, this.context);
      request.setTrace(this.trace);
      artifact = this.resolver.resolveArtifact(this.session, request).getArtifact();
    } catch (ArtifactResolutionException e) {
      throw new UnresolvableModelException(e.getMessage(), groupId, artifactId, version, e);
    } 
    File pomFile = artifact.getFile();
    return (ModelSource)new FileModelSource(pomFile);
  }
  
  public ModelSource resolveModel(Parent parent) throws UnresolvableModelException {
    try {
      DefaultArtifact defaultArtifact = new DefaultArtifact(parent.getGroupId(), parent.getArtifactId(), "", "pom", parent.getVersion());
      VersionRangeRequest versionRangeRequest = new VersionRangeRequest((Artifact)defaultArtifact, this.repositories, this.context);
      versionRangeRequest.setTrace(this.trace);
      VersionRangeResult versionRangeResult = this.versionRangeResolver.resolveVersionRange(this.session, versionRangeRequest);
      if (versionRangeResult.getHighestVersion() == null)
        throw new UnresolvableModelException(
            String.format("No versions matched the requested parent version range '%s'", new Object[] { parent.getVersion() }), parent.getGroupId(), parent.getArtifactId(), parent.getVersion()); 
      if (versionRangeResult.getVersionConstraint() != null && versionRangeResult
        .getVersionConstraint().getRange() != null && versionRangeResult
        .getVersionConstraint().getRange().getUpperBound() == null)
        throw new UnresolvableModelException(
            String.format("The requested parent version range '%s' does not specify an upper bound", new Object[] { parent.getVersion() }), parent.getGroupId(), parent.getArtifactId(), parent.getVersion()); 
      parent.setVersion(versionRangeResult.getHighestVersion().toString());
      return resolveModel(parent.getGroupId(), parent.getArtifactId(), parent.getVersion());
    } catch (VersionRangeResolutionException e) {
      throw new UnresolvableModelException(e.getMessage(), parent.getGroupId(), parent.getArtifactId(), parent
          .getVersion(), e);
    } 
  }
  
  public ModelSource resolveModel(Dependency dependency) throws UnresolvableModelException {
    try {
      DefaultArtifact defaultArtifact = new DefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(), "", "pom", dependency.getVersion());
      VersionRangeRequest versionRangeRequest = new VersionRangeRequest((Artifact)defaultArtifact, this.repositories, this.context);
      versionRangeRequest.setTrace(this.trace);
      VersionRangeResult versionRangeResult = this.versionRangeResolver.resolveVersionRange(this.session, versionRangeRequest);
      if (versionRangeResult.getHighestVersion() == null)
        throw new UnresolvableModelException(
            String.format("No versions matched the requested dependency version range '%s'", new Object[] { dependency.getVersion() }), dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion()); 
      if (versionRangeResult.getVersionConstraint() != null && versionRangeResult
        .getVersionConstraint().getRange() != null && versionRangeResult
        .getVersionConstraint().getRange().getUpperBound() == null)
        throw new UnresolvableModelException(
            String.format("The requested dependency version range '%s' does not specify an upper bound", new Object[] { dependency.getVersion() }), dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion()); 
      dependency.setVersion(versionRangeResult.getHighestVersion().toString());
      return resolveModel(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion());
    } catch (VersionRangeResolutionException e) {
      throw new UnresolvableModelException(e.getMessage(), dependency.getGroupId(), dependency.getArtifactId(), dependency
          .getVersion(), e);
    } 
  }
}
