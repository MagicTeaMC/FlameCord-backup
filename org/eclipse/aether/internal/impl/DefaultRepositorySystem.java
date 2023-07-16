package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.collection.CollectResult;
import org.eclipse.aether.collection.DependencyCollectionException;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.deployment.DeployResult;
import org.eclipse.aether.deployment.DeploymentException;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.graph.DependencyVisitor;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.DependencyCollector;
import org.eclipse.aether.impl.Deployer;
import org.eclipse.aether.impl.Installer;
import org.eclipse.aether.impl.LocalRepositoryProvider;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.installation.InstallResult;
import org.eclipse.aether.installation.InstallationException;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResolutionException;
import org.eclipse.aether.resolution.DependencyResult;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.resolution.VersionRequest;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.eclipse.aether.resolution.VersionResult;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.aether.util.graph.visitor.FilteringDependencyVisitor;
import org.eclipse.aether.util.graph.visitor.TreeDependencyVisitor;

@Named
public class DefaultRepositorySystem implements RepositorySystem, Service {
  private VersionResolver versionResolver;
  
  private VersionRangeResolver versionRangeResolver;
  
  private ArtifactResolver artifactResolver;
  
  private MetadataResolver metadataResolver;
  
  private ArtifactDescriptorReader artifactDescriptorReader;
  
  private DependencyCollector dependencyCollector;
  
  private Installer installer;
  
  private Deployer deployer;
  
  private LocalRepositoryProvider localRepositoryProvider;
  
  private SyncContextFactory syncContextFactory;
  
  private RemoteRepositoryManager remoteRepositoryManager;
  
  public DefaultRepositorySystem() {}
  
  @Inject
  DefaultRepositorySystem(VersionResolver versionResolver, VersionRangeResolver versionRangeResolver, ArtifactResolver artifactResolver, MetadataResolver metadataResolver, ArtifactDescriptorReader artifactDescriptorReader, DependencyCollector dependencyCollector, Installer installer, Deployer deployer, LocalRepositoryProvider localRepositoryProvider, SyncContextFactory syncContextFactory, RemoteRepositoryManager remoteRepositoryManager) {
    setVersionResolver(versionResolver);
    setVersionRangeResolver(versionRangeResolver);
    setArtifactResolver(artifactResolver);
    setMetadataResolver(metadataResolver);
    setArtifactDescriptorReader(artifactDescriptorReader);
    setDependencyCollector(dependencyCollector);
    setInstaller(installer);
    setDeployer(deployer);
    setLocalRepositoryProvider(localRepositoryProvider);
    setSyncContextFactory(syncContextFactory);
    setRemoteRepositoryManager(remoteRepositoryManager);
  }
  
  public void initService(ServiceLocator locator) {
    setVersionResolver((VersionResolver)locator.getService(VersionResolver.class));
    setVersionRangeResolver((VersionRangeResolver)locator.getService(VersionRangeResolver.class));
    setArtifactResolver((ArtifactResolver)locator.getService(ArtifactResolver.class));
    setMetadataResolver((MetadataResolver)locator.getService(MetadataResolver.class));
    setArtifactDescriptorReader((ArtifactDescriptorReader)locator.getService(ArtifactDescriptorReader.class));
    setDependencyCollector((DependencyCollector)locator.getService(DependencyCollector.class));
    setInstaller((Installer)locator.getService(Installer.class));
    setDeployer((Deployer)locator.getService(Deployer.class));
    setLocalRepositoryProvider((LocalRepositoryProvider)locator.getService(LocalRepositoryProvider.class));
    setRemoteRepositoryManager((RemoteRepositoryManager)locator.getService(RemoteRepositoryManager.class));
    setSyncContextFactory((SyncContextFactory)locator.getService(SyncContextFactory.class));
  }
  
  @Deprecated
  public DefaultRepositorySystem setLoggerFactory(LoggerFactory loggerFactory) {
    return this;
  }
  
  public DefaultRepositorySystem setVersionResolver(VersionResolver versionResolver) {
    this.versionResolver = Objects.<VersionResolver>requireNonNull(versionResolver, "version resolver cannot be null");
    return this;
  }
  
  public DefaultRepositorySystem setVersionRangeResolver(VersionRangeResolver versionRangeResolver) {
    this.versionRangeResolver = Objects.<VersionRangeResolver>requireNonNull(versionRangeResolver, "version range resolver cannot be null");
    return this;
  }
  
  public DefaultRepositorySystem setArtifactResolver(ArtifactResolver artifactResolver) {
    this.artifactResolver = Objects.<ArtifactResolver>requireNonNull(artifactResolver, "artifact resolver cannot be null");
    return this;
  }
  
  public DefaultRepositorySystem setMetadataResolver(MetadataResolver metadataResolver) {
    this.metadataResolver = Objects.<MetadataResolver>requireNonNull(metadataResolver, "metadata resolver cannot be null");
    return this;
  }
  
  public DefaultRepositorySystem setArtifactDescriptorReader(ArtifactDescriptorReader artifactDescriptorReader) {
    this.artifactDescriptorReader = Objects.<ArtifactDescriptorReader>requireNonNull(artifactDescriptorReader, "artifact descriptor reader cannot be null");
    return this;
  }
  
  public DefaultRepositorySystem setDependencyCollector(DependencyCollector dependencyCollector) {
    this.dependencyCollector = Objects.<DependencyCollector>requireNonNull(dependencyCollector, "dependency collector cannot be null");
    return this;
  }
  
  public DefaultRepositorySystem setInstaller(Installer installer) {
    this.installer = Objects.<Installer>requireNonNull(installer, "installer cannot be null");
    return this;
  }
  
  public DefaultRepositorySystem setDeployer(Deployer deployer) {
    this.deployer = Objects.<Deployer>requireNonNull(deployer, "deployer cannot be null");
    return this;
  }
  
  public DefaultRepositorySystem setLocalRepositoryProvider(LocalRepositoryProvider localRepositoryProvider) {
    this.localRepositoryProvider = Objects.<LocalRepositoryProvider>requireNonNull(localRepositoryProvider, "local repository provider cannot be null");
    return this;
  }
  
  public DefaultRepositorySystem setSyncContextFactory(SyncContextFactory syncContextFactory) {
    this.syncContextFactory = Objects.<SyncContextFactory>requireNonNull(syncContextFactory, "sync context factory cannot be null");
    return this;
  }
  
  public DefaultRepositorySystem setRemoteRepositoryManager(RemoteRepositoryManager remoteRepositoryManager) {
    this.remoteRepositoryManager = Objects.<RemoteRepositoryManager>requireNonNull(remoteRepositoryManager, "remote repository provider cannot be null");
    return this;
  }
  
  public VersionResult resolveVersion(RepositorySystemSession session, VersionRequest request) throws VersionResolutionException {
    validateSession(session);
    return this.versionResolver.resolveVersion(session, request);
  }
  
  public VersionRangeResult resolveVersionRange(RepositorySystemSession session, VersionRangeRequest request) throws VersionRangeResolutionException {
    validateSession(session);
    return this.versionRangeResolver.resolveVersionRange(session, request);
  }
  
  public ArtifactDescriptorResult readArtifactDescriptor(RepositorySystemSession session, ArtifactDescriptorRequest request) throws ArtifactDescriptorException {
    validateSession(session);
    return this.artifactDescriptorReader.readArtifactDescriptor(session, request);
  }
  
  public ArtifactResult resolveArtifact(RepositorySystemSession session, ArtifactRequest request) throws ArtifactResolutionException {
    validateSession(session);
    return this.artifactResolver.resolveArtifact(session, request);
  }
  
  public List<ArtifactResult> resolveArtifacts(RepositorySystemSession session, Collection<? extends ArtifactRequest> requests) throws ArtifactResolutionException {
    validateSession(session);
    return this.artifactResolver.resolveArtifacts(session, requests);
  }
  
  public List<MetadataResult> resolveMetadata(RepositorySystemSession session, Collection<? extends MetadataRequest> requests) {
    validateSession(session);
    return this.metadataResolver.resolveMetadata(session, requests);
  }
  
  public CollectResult collectDependencies(RepositorySystemSession session, CollectRequest request) throws DependencyCollectionException {
    validateSession(session);
    return this.dependencyCollector.collectDependencies(session, request);
  }
  
  public DependencyResult resolveDependencies(RepositorySystemSession session, DependencyRequest request) throws DependencyResolutionException {
    List<ArtifactResult> results;
    validateSession(session);
    RequestTrace trace = RequestTrace.newChild(request.getTrace(), request);
    DependencyResult result = new DependencyResult(request);
    DependencyCollectionException dce = null;
    ArtifactResolutionException are = null;
    if (request.getRoot() != null) {
      result.setRoot(request.getRoot());
    } else if (request.getCollectRequest() != null) {
      CollectResult collectResult;
      try {
        request.getCollectRequest().setTrace(trace);
        collectResult = this.dependencyCollector.collectDependencies(session, request.getCollectRequest());
      } catch (DependencyCollectionException e) {
        dce = e;
        collectResult = e.getResult();
      } 
      result.setRoot(collectResult.getRoot());
      result.setCycles(collectResult.getCycles());
      result.setCollectExceptions(collectResult.getExceptions());
    } else {
      throw new NullPointerException("dependency node and collect request cannot be null");
    } 
    ArtifactRequestBuilder builder = new ArtifactRequestBuilder(trace);
    DependencyFilter filter = request.getFilter();
    DependencyVisitor visitor = (filter != null) ? (DependencyVisitor)new FilteringDependencyVisitor(builder, filter) : builder;
    TreeDependencyVisitor treeDependencyVisitor = new TreeDependencyVisitor(visitor);
    if (result.getRoot() != null)
      result.getRoot().accept((DependencyVisitor)treeDependencyVisitor); 
    List<ArtifactRequest> requests = builder.getRequests();
    try {
      results = this.artifactResolver.resolveArtifacts(session, requests);
    } catch (ArtifactResolutionException e) {
      are = e;
      results = e.getResults();
    } 
    result.setArtifactResults(results);
    updateNodesWithResolvedArtifacts(results);
    if (dce != null)
      throw new DependencyResolutionException(result, dce); 
    if (are != null)
      throw new DependencyResolutionException(result, are); 
    return result;
  }
  
  private void updateNodesWithResolvedArtifacts(List<ArtifactResult> results) {
    for (ArtifactResult result : results) {
      Artifact artifact = result.getArtifact();
      if (artifact != null)
        result.getRequest().getDependencyNode().setArtifact(artifact); 
    } 
  }
  
  public InstallResult install(RepositorySystemSession session, InstallRequest request) throws InstallationException {
    validateSession(session);
    return this.installer.install(session, request);
  }
  
  public DeployResult deploy(RepositorySystemSession session, DeployRequest request) throws DeploymentException {
    validateSession(session);
    return this.deployer.deploy(session, request);
  }
  
  public LocalRepositoryManager newLocalRepositoryManager(RepositorySystemSession session, LocalRepository localRepository) {
    try {
      return this.localRepositoryProvider.newLocalRepositoryManager(session, localRepository);
    } catch (NoLocalRepositoryManagerException e) {
      throw new IllegalArgumentException(e.getMessage(), e);
    } 
  }
  
  public SyncContext newSyncContext(RepositorySystemSession session, boolean shared) {
    validateSession(session);
    return this.syncContextFactory.newInstance(session, shared);
  }
  
  public List<RemoteRepository> newResolutionRepositories(RepositorySystemSession session, List<RemoteRepository> repositories) {
    validateSession(session);
    repositories = this.remoteRepositoryManager.aggregateRepositories(session, new ArrayList(), repositories, true);
    return repositories;
  }
  
  public RemoteRepository newDeploymentRepository(RepositorySystemSession session, RemoteRepository repository) {
    validateSession(session);
    RemoteRepository.Builder builder = new RemoteRepository.Builder(repository);
    Authentication auth = session.getAuthenticationSelector().getAuthentication(repository);
    builder.setAuthentication(auth);
    Proxy proxy = session.getProxySelector().getProxy(repository);
    builder.setProxy(proxy);
    return builder.build();
  }
  
  private void validateSession(RepositorySystemSession session) {
    Objects.requireNonNull(session, "repository system session cannot be null");
    invalidSession(session.getLocalRepositoryManager(), "local repository manager");
    invalidSession(session.getSystemProperties(), "system properties");
    invalidSession(session.getUserProperties(), "user properties");
    invalidSession(session.getConfigProperties(), "config properties");
    invalidSession(session.getMirrorSelector(), "mirror selector");
    invalidSession(session.getProxySelector(), "proxy selector");
    invalidSession(session.getAuthenticationSelector(), "authentication selector");
    invalidSession(session.getArtifactTypeRegistry(), "artifact type registry");
    invalidSession(session.getData(), "data");
  }
  
  private void invalidSession(Object obj, String name) {
    Objects.requireNonNull(obj, "repository system session's " + name + " cannot be null");
  }
}
