package org.eclipse.aether.internal.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.ArtifactResolver;
import org.eclipse.aether.impl.OfflineController;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.RepositoryConnectorProvider;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.UpdateCheck;
import org.eclipse.aether.impl.UpdateCheckManager;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.LocalArtifactRegistration;
import org.eclipse.aether.repository.LocalArtifactRequest;
import org.eclipse.aether.repository.LocalArtifactResult;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.repository.WorkspaceReader;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRequest;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.eclipse.aether.resolution.VersionResult;
import org.eclipse.aether.spi.connector.ArtifactDownload;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.spi.log.LoggerFactory;
import org.eclipse.aether.transfer.ArtifactNotFoundException;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import org.eclipse.aether.transfer.RepositoryOfflineException;
import org.eclipse.aether.util.ConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DefaultArtifactResolver implements ArtifactResolver, Service {
  private static final String CONFIG_PROP_SNAPSHOT_NORMALIZATION = "aether.artifactResolver.snapshotNormalization";
  
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultArtifactResolver.class);
  
  private FileProcessor fileProcessor;
  
  private RepositoryEventDispatcher repositoryEventDispatcher;
  
  private VersionResolver versionResolver;
  
  private UpdateCheckManager updateCheckManager;
  
  private RepositoryConnectorProvider repositoryConnectorProvider;
  
  private RemoteRepositoryManager remoteRepositoryManager;
  
  private SyncContextFactory syncContextFactory;
  
  private OfflineController offlineController;
  
  public DefaultArtifactResolver() {}
  
  @Inject
  DefaultArtifactResolver(FileProcessor fileProcessor, RepositoryEventDispatcher repositoryEventDispatcher, VersionResolver versionResolver, UpdateCheckManager updateCheckManager, RepositoryConnectorProvider repositoryConnectorProvider, RemoteRepositoryManager remoteRepositoryManager, SyncContextFactory syncContextFactory, OfflineController offlineController) {
    setFileProcessor(fileProcessor);
    setRepositoryEventDispatcher(repositoryEventDispatcher);
    setVersionResolver(versionResolver);
    setUpdateCheckManager(updateCheckManager);
    setRepositoryConnectorProvider(repositoryConnectorProvider);
    setRemoteRepositoryManager(remoteRepositoryManager);
    setSyncContextFactory(syncContextFactory);
    setOfflineController(offlineController);
  }
  
  public void initService(ServiceLocator locator) {
    setFileProcessor((FileProcessor)locator.getService(FileProcessor.class));
    setRepositoryEventDispatcher((RepositoryEventDispatcher)locator.getService(RepositoryEventDispatcher.class));
    setVersionResolver((VersionResolver)locator.getService(VersionResolver.class));
    setUpdateCheckManager((UpdateCheckManager)locator.getService(UpdateCheckManager.class));
    setRepositoryConnectorProvider((RepositoryConnectorProvider)locator.getService(RepositoryConnectorProvider.class));
    setRemoteRepositoryManager((RemoteRepositoryManager)locator.getService(RemoteRepositoryManager.class));
    setSyncContextFactory((SyncContextFactory)locator.getService(SyncContextFactory.class));
    setOfflineController((OfflineController)locator.getService(OfflineController.class));
  }
  
  @Deprecated
  public DefaultArtifactResolver setLoggerFactory(LoggerFactory loggerFactory) {
    return this;
  }
  
  public DefaultArtifactResolver setFileProcessor(FileProcessor fileProcessor) {
    this.fileProcessor = Objects.<FileProcessor>requireNonNull(fileProcessor, "file processor cannot be null");
    return this;
  }
  
  public DefaultArtifactResolver setRepositoryEventDispatcher(RepositoryEventDispatcher repositoryEventDispatcher) {
    this.repositoryEventDispatcher = Objects.<RepositoryEventDispatcher>requireNonNull(repositoryEventDispatcher, "repository event dispatcher cannot be null");
    return this;
  }
  
  public DefaultArtifactResolver setVersionResolver(VersionResolver versionResolver) {
    this.versionResolver = Objects.<VersionResolver>requireNonNull(versionResolver, "version resolver cannot be null");
    return this;
  }
  
  public DefaultArtifactResolver setUpdateCheckManager(UpdateCheckManager updateCheckManager) {
    this.updateCheckManager = Objects.<UpdateCheckManager>requireNonNull(updateCheckManager, "update check manager cannot be null");
    return this;
  }
  
  public DefaultArtifactResolver setRepositoryConnectorProvider(RepositoryConnectorProvider repositoryConnectorProvider) {
    this.repositoryConnectorProvider = Objects.<RepositoryConnectorProvider>requireNonNull(repositoryConnectorProvider, "repository connector provider cannot be null");
    return this;
  }
  
  public DefaultArtifactResolver setRemoteRepositoryManager(RemoteRepositoryManager remoteRepositoryManager) {
    this.remoteRepositoryManager = Objects.<RemoteRepositoryManager>requireNonNull(remoteRepositoryManager, "remote repository provider cannot be null");
    return this;
  }
  
  public DefaultArtifactResolver setSyncContextFactory(SyncContextFactory syncContextFactory) {
    this.syncContextFactory = Objects.<SyncContextFactory>requireNonNull(syncContextFactory, "sync context factory cannot be null");
    return this;
  }
  
  public DefaultArtifactResolver setOfflineController(OfflineController offlineController) {
    this.offlineController = Objects.<OfflineController>requireNonNull(offlineController, "offline controller cannot be null");
    return this;
  }
  
  public ArtifactResult resolveArtifact(RepositorySystemSession session, ArtifactRequest request) throws ArtifactResolutionException {
    return resolveArtifacts(session, Collections.singleton(request)).get(0);
  }
  
  public List<ArtifactResult> resolveArtifacts(RepositorySystemSession session, Collection<? extends ArtifactRequest> requests) throws ArtifactResolutionException {
    try (SyncContext syncContext = this.syncContextFactory.newInstance(session, false)) {
      Collection<Artifact> artifacts = new ArrayList<>(requests.size());
      for (ArtifactRequest request : requests) {
        if (request.getArtifact().getProperty("localPath", null) != null)
          continue; 
        artifacts.add(request.getArtifact());
      } 
      syncContext.acquire(artifacts, null);
      return resolve(session, requests);
    } 
  }
  
  private List<ArtifactResult> resolve(RepositorySystemSession session, Collection<? extends ArtifactRequest> requests) throws ArtifactResolutionException {
    List<ArtifactResult> results = new ArrayList<>(requests.size());
    boolean failures = false;
    LocalRepositoryManager lrm = session.getLocalRepositoryManager();
    WorkspaceReader workspace = session.getWorkspaceReader();
    List<ResolutionGroup> groups = new ArrayList<>();
    for (ArtifactRequest request : requests) {
      VersionResult versionResult;
      RequestTrace trace = RequestTrace.newChild(request.getTrace(), request);
      ArtifactResult result = new ArtifactResult(request);
      results.add(result);
      Artifact artifact = request.getArtifact();
      List<RemoteRepository> repos = request.getRepositories();
      artifactResolving(session, trace, artifact);
      String localPath = artifact.getProperty("localPath", null);
      if (localPath != null) {
        File file = new File(localPath);
        if (!file.isFile()) {
          failures = true;
          result.addException((Exception)new ArtifactNotFoundException(artifact, null));
          continue;
        } 
        artifact = artifact.setFile(file);
        result.setArtifact(artifact);
        artifactResolved(session, trace, artifact, null, result.getExceptions());
        continue;
      } 
      try {
        VersionRequest versionRequest = new VersionRequest(artifact, repos, request.getRequestContext());
        versionRequest.setTrace(trace);
        versionResult = this.versionResolver.resolveVersion(session, versionRequest);
      } catch (VersionResolutionException e) {
        result.addException((Exception)e);
        continue;
      } 
      artifact = artifact.setVersion(versionResult.getVersion());
      if (versionResult.getRepository() != null)
        if (versionResult.getRepository() instanceof RemoteRepository) {
          repos = Collections.singletonList((RemoteRepository)versionResult.getRepository());
        } else {
          repos = Collections.emptyList();
        }  
      if (workspace != null) {
        File file = workspace.findArtifact(artifact);
        if (file != null) {
          artifact = artifact.setFile(file);
          result.setArtifact(artifact);
          result.setRepository((ArtifactRepository)workspace.getRepository());
          artifactResolved(session, trace, artifact, result.getRepository(), null);
          continue;
        } 
      } 
      LocalArtifactResult local = lrm.find(session, new LocalArtifactRequest(artifact, repos, request.getRequestContext()));
      if (isLocallyInstalled(local, versionResult)) {
        if (local.getRepository() != null) {
          result.setRepository((ArtifactRepository)local.getRepository());
        } else {
          result.setRepository((ArtifactRepository)lrm.getRepository());
        } 
        try {
          artifact = artifact.setFile(getFile(session, artifact, local.getFile()));
          result.setArtifact(artifact);
          artifactResolved(session, trace, artifact, result.getRepository(), null);
        } catch (ArtifactTransferException e) {
          result.addException((Exception)e);
        } 
        if (!local.isAvailable())
          lrm.add(session, new LocalArtifactRegistration(artifact)); 
        continue;
      } 
      if (local.getFile() != null)
        LOGGER.debug("Verifying availability of {} from {}", local.getFile(), repos); 
      LOGGER.debug("Resolving artifact {} from {}", artifact, repos);
      AtomicBoolean resolved = new AtomicBoolean(false);
      Iterator<ResolutionGroup> groupIt = groups.iterator();
      for (RemoteRepository repo : repos) {
        if (!repo.getPolicy(artifact.isSnapshot()).isEnabled())
          continue; 
        try {
          Utils.checkOffline(session, this.offlineController, repo);
        } catch (RepositoryOfflineException e) {
          ArtifactNotFoundException artifactNotFoundException = new ArtifactNotFoundException(artifact, repo, "Cannot access " + repo.getId() + " (" + repo.getUrl() + ") in offline mode and the artifact " + artifact + " has not been downloaded from it before.", (Throwable)e);
          result.addException((Exception)artifactNotFoundException);
          continue;
        } 
        ResolutionGroup group = null;
        while (groupIt.hasNext()) {
          ResolutionGroup t = groupIt.next();
          if (t.matches(repo)) {
            group = t;
            break;
          } 
        } 
        if (group == null) {
          group = new ResolutionGroup(repo);
          groups.add(group);
          groupIt = Collections.<ResolutionGroup>emptyList().iterator();
        } 
        group.items.add(new ResolutionItem(trace, artifact, resolved, result, local, repo));
      } 
    } 
    for (ResolutionGroup group : groups)
      performDownloads(session, group); 
    for (ArtifactResult result : results) {
      ArtifactRequest request = result.getRequest();
      Artifact artifact = result.getArtifact();
      if (artifact == null || artifact.getFile() == null) {
        failures = true;
        if (result.getExceptions().isEmpty()) {
          ArtifactNotFoundException artifactNotFoundException = new ArtifactNotFoundException(request.getArtifact(), null);
          result.addException((Exception)artifactNotFoundException);
        } 
        RequestTrace trace = RequestTrace.newChild(request.getTrace(), request);
        artifactResolved(session, trace, request.getArtifact(), null, result.getExceptions());
      } 
    } 
    if (failures)
      throw new ArtifactResolutionException(results); 
    return results;
  }
  
  private boolean isLocallyInstalled(LocalArtifactResult lar, VersionResult vr) {
    if (lar.isAvailable())
      return true; 
    if (lar.getFile() != null) {
      if (vr.getRepository() instanceof org.eclipse.aether.repository.LocalRepository)
        return true; 
      if (vr.getRepository() == null && lar.getRequest().getRepositories().isEmpty())
        return true; 
    } 
    return false;
  }
  
  private File getFile(RepositorySystemSession session, Artifact artifact, File file) throws ArtifactTransferException {
    if (artifact.isSnapshot() && !artifact.getVersion().equals(artifact.getBaseVersion()) && 
      ConfigUtils.getBoolean(session, true, new String[] { "aether.artifactResolver.snapshotNormalization" })) {
      String name = file.getName().replace(artifact.getVersion(), artifact.getBaseVersion());
      File dst = new File(file.getParent(), name);
      boolean copy = (dst.length() != file.length() || dst.lastModified() != file.lastModified());
      if (copy)
        try {
          this.fileProcessor.copy(file, dst);
          dst.setLastModified(file.lastModified());
        } catch (IOException e) {
          throw new ArtifactTransferException(artifact, null, e);
        }  
      file = dst;
    } 
    return file;
  }
  
  private void performDownloads(RepositorySystemSession session, ResolutionGroup group) {
    List<ArtifactDownload> downloads = gatherDownloads(session, group);
    if (downloads.isEmpty())
      return; 
    for (ArtifactDownload download : downloads)
      artifactDownloading(session, download.getTrace(), download.getArtifact(), group.repository); 
    try {
      RemoteRepository repo = group.repository;
      if (repo.isBlocked()) {
        if (repo.getMirroredRepositories().isEmpty())
          throw new NoRepositoryConnectorException(repo, "Blocked repository: " + repo); 
        throw new NoRepositoryConnectorException(repo, "Blocked mirror for repositories: " + repo
            .getMirroredRepositories());
      } 
      try (RepositoryConnector connector = this.repositoryConnectorProvider.newRepositoryConnector(session, group.repository)) {
        connector.get(downloads, null);
      } 
    } catch (NoRepositoryConnectorException e) {
      for (ArtifactDownload download : downloads)
        download.setException(new ArtifactTransferException(download.getArtifact(), group.repository, (Throwable)e)); 
    } 
    evaluateDownloads(session, group);
  }
  
  private List<ArtifactDownload> gatherDownloads(RepositorySystemSession session, ResolutionGroup group) {
    LocalRepositoryManager lrm = session.getLocalRepositoryManager();
    List<ArtifactDownload> downloads = new ArrayList<>();
    for (ResolutionItem item : group.items) {
      Artifact artifact = item.artifact;
      if (item.resolved.get())
        continue; 
      ArtifactDownload download = new ArtifactDownload();
      download.setArtifact(artifact);
      download.setRequestContext(item.request.getRequestContext());
      download.setListener(SafeTransferListener.wrap(session));
      download.setTrace(item.trace);
      if (item.local.getFile() != null) {
        download.setFile(item.local.getFile());
        download.setExistenceCheck(true);
      } else {
        String path = lrm.getPathForRemoteArtifact(artifact, group.repository, item.request.getRequestContext());
        download.setFile(new File(lrm.getRepository().getBasedir(), path));
      } 
      boolean snapshot = artifact.isSnapshot();
      RepositoryPolicy policy = this.remoteRepositoryManager.getPolicy(session, group.repository, !snapshot, snapshot);
      int errorPolicy = Utils.getPolicy(session, artifact, group.repository);
      if ((errorPolicy & 0x3) != 0) {
        UpdateCheck<Artifact, ArtifactTransferException> check = new UpdateCheck();
        check.setItem(artifact);
        check.setFile(download.getFile());
        check.setFileValid(false);
        check.setRepository(group.repository);
        check.setPolicy(policy.getUpdatePolicy());
        item.updateCheck = check;
        this.updateCheckManager.checkArtifact(session, check);
        if (!check.isRequired()) {
          item.result.addException((Exception)check.getException());
          continue;
        } 
      } 
      download.setChecksumPolicy(policy.getChecksumPolicy());
      download.setRepositories(item.repository.getMirroredRepositories());
      downloads.add(download);
      item.download = download;
    } 
    return downloads;
  }
  
  private void evaluateDownloads(RepositorySystemSession session, ResolutionGroup group) {
    LocalRepositoryManager lrm = session.getLocalRepositoryManager();
    for (ResolutionItem item : group.items) {
      ArtifactDownload download = item.download;
      if (download == null)
        continue; 
      Artifact artifact = download.getArtifact();
      if (download.getException() == null) {
        item.resolved.set(true);
        item.result.setRepository((ArtifactRepository)group.repository);
        try {
          artifact = artifact.setFile(getFile(session, artifact, download.getFile()));
          item.result.setArtifact(artifact);
          lrm.add(session, new LocalArtifactRegistration(artifact, group.repository, download
                .getSupportedContexts()));
        } catch (ArtifactTransferException e) {
          download.setException(e);
          item.result.addException((Exception)e);
        } 
      } else {
        item.result.addException((Exception)download.getException());
      } 
      if (item.updateCheck != null) {
        item.updateCheck.setException((RepositoryException)download.getException());
        this.updateCheckManager.touchArtifact(session, item.updateCheck);
      } 
      artifactDownloaded(session, download.getTrace(), artifact, group.repository, (Exception)download.getException());
      if (download.getException() == null)
        artifactResolved(session, download.getTrace(), artifact, (ArtifactRepository)group.repository, null); 
    } 
  }
  
  private void artifactResolving(RepositorySystemSession session, RequestTrace trace, Artifact artifact) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.ARTIFACT_RESOLVING);
    event.setTrace(trace);
    event.setArtifact(artifact);
    this.repositoryEventDispatcher.dispatch(event.build());
  }
  
  private void artifactResolved(RepositorySystemSession session, RequestTrace trace, Artifact artifact, ArtifactRepository repository, List<Exception> exceptions) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.ARTIFACT_RESOLVED);
    event.setTrace(trace);
    event.setArtifact(artifact);
    event.setRepository(repository);
    event.setExceptions(exceptions);
    if (artifact != null)
      event.setFile(artifact.getFile()); 
    this.repositoryEventDispatcher.dispatch(event.build());
  }
  
  private void artifactDownloading(RepositorySystemSession session, RequestTrace trace, Artifact artifact, RemoteRepository repository) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.ARTIFACT_DOWNLOADING);
    event.setTrace(trace);
    event.setArtifact(artifact);
    event.setRepository((ArtifactRepository)repository);
    this.repositoryEventDispatcher.dispatch(event.build());
  }
  
  private void artifactDownloaded(RepositorySystemSession session, RequestTrace trace, Artifact artifact, RemoteRepository repository, Exception exception) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.ARTIFACT_DOWNLOADED);
    event.setTrace(trace);
    event.setArtifact(artifact);
    event.setRepository((ArtifactRepository)repository);
    event.setException(exception);
    if (artifact != null)
      event.setFile(artifact.getFile()); 
    this.repositoryEventDispatcher.dispatch(event.build());
  }
  
  static class ResolutionGroup {
    final RemoteRepository repository;
    
    final List<DefaultArtifactResolver.ResolutionItem> items = new ArrayList<>();
    
    ResolutionGroup(RemoteRepository repository) {
      this.repository = repository;
    }
    
    boolean matches(RemoteRepository repo) {
      return (this.repository.getUrl().equals(repo.getUrl()) && this.repository
        .getContentType().equals(repo.getContentType()) && this.repository
        .isRepositoryManager() == repo.isRepositoryManager());
    }
  }
  
  static class ResolutionItem {
    final RequestTrace trace;
    
    final ArtifactRequest request;
    
    final ArtifactResult result;
    
    final LocalArtifactResult local;
    
    final RemoteRepository repository;
    
    final Artifact artifact;
    
    final AtomicBoolean resolved;
    
    ArtifactDownload download;
    
    UpdateCheck<Artifact, ArtifactTransferException> updateCheck;
    
    ResolutionItem(RequestTrace trace, Artifact artifact, AtomicBoolean resolved, ArtifactResult result, LocalArtifactResult local, RemoteRepository repository) {
      this.trace = trace;
      this.artifact = artifact;
      this.resolved = resolved;
      this.result = result;
      this.request = result.getRequest();
      this.local = local;
      this.repository = repository;
    }
  }
}
