package org.eclipse.aether.internal.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.aether.RepositoryEvent;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.impl.MetadataResolver;
import org.eclipse.aether.impl.OfflineController;
import org.eclipse.aether.impl.RemoteRepositoryManager;
import org.eclipse.aether.impl.RepositoryConnectorProvider;
import org.eclipse.aether.impl.RepositoryEventDispatcher;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.impl.UpdateCheck;
import org.eclipse.aether.impl.UpdateCheckManager;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.LocalMetadataRegistration;
import org.eclipse.aether.repository.LocalMetadataRequest;
import org.eclipse.aether.repository.LocalMetadataResult;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;
import org.eclipse.aether.resolution.MetadataRequest;
import org.eclipse.aether.resolution.MetadataResult;
import org.eclipse.aether.spi.connector.MetadataDownload;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.eclipse.aether.transfer.MetadataTransferException;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import org.eclipse.aether.transfer.RepositoryOfflineException;
import org.eclipse.aether.util.ConfigUtils;
import org.eclipse.aether.util.concurrency.RunnableErrorForwarder;
import org.eclipse.aether.util.concurrency.WorkerThreadFactory;

@Named
public class DefaultMetadataResolver implements MetadataResolver, Service {
  private static final String CONFIG_PROP_THREADS = "aether.metadataResolver.threads";
  
  private RepositoryEventDispatcher repositoryEventDispatcher;
  
  private UpdateCheckManager updateCheckManager;
  
  private RepositoryConnectorProvider repositoryConnectorProvider;
  
  private RemoteRepositoryManager remoteRepositoryManager;
  
  private SyncContextFactory syncContextFactory;
  
  private OfflineController offlineController;
  
  public DefaultMetadataResolver() {}
  
  @Inject
  DefaultMetadataResolver(RepositoryEventDispatcher repositoryEventDispatcher, UpdateCheckManager updateCheckManager, RepositoryConnectorProvider repositoryConnectorProvider, RemoteRepositoryManager remoteRepositoryManager, SyncContextFactory syncContextFactory, OfflineController offlineController) {
    setRepositoryEventDispatcher(repositoryEventDispatcher);
    setUpdateCheckManager(updateCheckManager);
    setRepositoryConnectorProvider(repositoryConnectorProvider);
    setRemoteRepositoryManager(remoteRepositoryManager);
    setSyncContextFactory(syncContextFactory);
    setOfflineController(offlineController);
  }
  
  public void initService(ServiceLocator locator) {
    setRepositoryEventDispatcher((RepositoryEventDispatcher)locator.getService(RepositoryEventDispatcher.class));
    setUpdateCheckManager((UpdateCheckManager)locator.getService(UpdateCheckManager.class));
    setRepositoryConnectorProvider((RepositoryConnectorProvider)locator.getService(RepositoryConnectorProvider.class));
    setRemoteRepositoryManager((RemoteRepositoryManager)locator.getService(RemoteRepositoryManager.class));
    setSyncContextFactory((SyncContextFactory)locator.getService(SyncContextFactory.class));
    setOfflineController((OfflineController)locator.getService(OfflineController.class));
  }
  
  public DefaultMetadataResolver setRepositoryEventDispatcher(RepositoryEventDispatcher repositoryEventDispatcher) {
    this.repositoryEventDispatcher = Objects.<RepositoryEventDispatcher>requireNonNull(repositoryEventDispatcher, "repository event dispatcher cannot be null");
    return this;
  }
  
  public DefaultMetadataResolver setUpdateCheckManager(UpdateCheckManager updateCheckManager) {
    this.updateCheckManager = Objects.<UpdateCheckManager>requireNonNull(updateCheckManager, "update check manager cannot be null");
    return this;
  }
  
  public DefaultMetadataResolver setRepositoryConnectorProvider(RepositoryConnectorProvider repositoryConnectorProvider) {
    this.repositoryConnectorProvider = Objects.<RepositoryConnectorProvider>requireNonNull(repositoryConnectorProvider, "repository connector provider cannot be null");
    return this;
  }
  
  public DefaultMetadataResolver setRemoteRepositoryManager(RemoteRepositoryManager remoteRepositoryManager) {
    this.remoteRepositoryManager = Objects.<RemoteRepositoryManager>requireNonNull(remoteRepositoryManager, "remote repository provider cannot be null");
    return this;
  }
  
  public DefaultMetadataResolver setSyncContextFactory(SyncContextFactory syncContextFactory) {
    this.syncContextFactory = Objects.<SyncContextFactory>requireNonNull(syncContextFactory, "sync context factory cannot be null");
    return this;
  }
  
  public DefaultMetadataResolver setOfflineController(OfflineController offlineController) {
    this.offlineController = Objects.<OfflineController>requireNonNull(offlineController, "offline controller cannot be null");
    return this;
  }
  
  public List<MetadataResult> resolveMetadata(RepositorySystemSession session, Collection<? extends MetadataRequest> requests) {
    try (SyncContext syncContext = this.syncContextFactory.newInstance(session, false)) {
      Collection<Metadata> metadata = new ArrayList<>(requests.size());
      for (MetadataRequest request : requests)
        metadata.add(request.getMetadata()); 
      syncContext.acquire(null, metadata);
      return resolve(session, requests);
    } 
  }
  
  private List<MetadataResult> resolve(RepositorySystemSession session, Collection<? extends MetadataRequest> requests) {
    List<MetadataResult> results = new ArrayList<>(requests.size());
    List<ResolveTask> tasks = new ArrayList<>(requests.size());
    Map<File, Long> localLastUpdates = new HashMap<>();
    for (MetadataRequest request : requests) {
      RepositoryException repositoryException;
      RequestTrace trace = RequestTrace.newChild(request.getTrace(), request);
      MetadataResult result = new MetadataResult(request);
      results.add(result);
      Metadata metadata = request.getMetadata();
      RemoteRepository repository = request.getRepository();
      if (repository == null) {
        LocalRepository localRepo = session.getLocalRepositoryManager().getRepository();
        metadataResolving(session, trace, metadata, (ArtifactRepository)localRepo);
        File localFile = getLocalFile(session, metadata);
        if (localFile != null) {
          metadata = metadata.setFile(localFile);
          result.setMetadata(metadata);
        } else {
          result.setException((Exception)new MetadataNotFoundException(metadata, localRepo));
        } 
        metadataResolved(session, trace, metadata, (ArtifactRepository)localRepo, result.getException());
        continue;
      } 
      List<RemoteRepository> repositories = getEnabledSourceRepositories(repository, metadata.getNature());
      if (repositories.isEmpty())
        continue; 
      metadataResolving(session, trace, metadata, (ArtifactRepository)repository);
      LocalRepositoryManager lrm = session.getLocalRepositoryManager();
      LocalMetadataRequest localRequest = new LocalMetadataRequest(metadata, repository, request.getRequestContext());
      LocalMetadataResult lrmResult = lrm.find(session, localRequest);
      File metadataFile = lrmResult.getFile();
      try {
        Utils.checkOffline(session, this.offlineController, repository);
      } catch (RepositoryOfflineException e) {
        if (metadataFile != null) {
          metadata = metadata.setFile(metadataFile);
          result.setMetadata(metadata);
        } else {
          String msg = "Cannot access " + repository.getId() + " (" + repository.getUrl() + ") in offline mode and the metadata " + metadata + " has not been downloaded from it before";
          result.setException((Exception)new MetadataNotFoundException(metadata, repository, msg, (Throwable)e));
        } 
        metadataResolved(session, trace, metadata, (ArtifactRepository)repository, result.getException());
        continue;
      } 
      Long localLastUpdate = null;
      if (request.isFavorLocalRepository()) {
        File localFile = getLocalFile(session, metadata);
        localLastUpdate = localLastUpdates.get(localFile);
        if (localLastUpdate == null) {
          localLastUpdate = Long.valueOf((localFile != null) ? localFile.lastModified() : 0L);
          localLastUpdates.put(localFile, localLastUpdate);
        } 
      } 
      List<UpdateCheck<Metadata, MetadataTransferException>> checks = new ArrayList<>();
      Exception exception = null;
      for (RemoteRepository repo : repositories) {
        UpdateCheck<Metadata, MetadataTransferException> check = new UpdateCheck();
        check.setLocalLastUpdated((localLastUpdate != null) ? localLastUpdate.longValue() : 0L);
        check.setItem(metadata);
        File checkFile = new File(session.getLocalRepository().getBasedir(), session.getLocalRepositoryManager().getPathForRemoteMetadata(metadata, repository, request.getRequestContext()));
        check.setFile(checkFile);
        check.setRepository(repository);
        check.setAuthoritativeRepository(repo);
        check.setPolicy(getPolicy(session, repo, metadata.getNature()).getUpdatePolicy());
        if (lrmResult.isStale()) {
          checks.add(check);
          continue;
        } 
        this.updateCheckManager.checkMetadata(session, check);
        if (check.isRequired()) {
          checks.add(check);
          continue;
        } 
        if (exception == null)
          repositoryException = check.getException(); 
      } 
      if (!checks.isEmpty()) {
        RepositoryPolicy policy = getPolicy(session, repository, metadata.getNature());
        File installFile = new File(session.getLocalRepository().getBasedir(), session.getLocalRepositoryManager().getPathForRemoteMetadata(metadata, request
              .getRepository(), request.getRequestContext()));
        metadataDownloading(session, trace, result
            .getRequest().getMetadata(), (ArtifactRepository)result.getRequest().getRepository());
        ResolveTask task = new ResolveTask(session, trace, result, installFile, checks, policy.getChecksumPolicy());
        tasks.add(task);
        continue;
      } 
      result.setException((Exception)repositoryException);
      if (metadataFile != null) {
        metadata = metadata.setFile(metadataFile);
        result.setMetadata(metadata);
      } 
      metadataResolved(session, trace, metadata, (ArtifactRepository)repository, result.getException());
    } 
    if (!tasks.isEmpty()) {
      int threads = ConfigUtils.getInteger(session, 4, new String[] { "aether.metadataResolver.threads" });
      Executor executor = getExecutor(Math.min(tasks.size(), threads));
      try {
        RunnableErrorForwarder errorForwarder = new RunnableErrorForwarder();
        for (ResolveTask task : tasks)
          executor.execute(errorForwarder.wrap(task)); 
        errorForwarder.await();
        for (ResolveTask task : tasks) {
          for (UpdateCheck<Metadata, MetadataTransferException> check : task.checks)
            this.updateCheckManager.touchMetadata(task.session, check.setException((RepositoryException)task.exception)); 
          metadataDownloaded(session, task.trace, task.request.getMetadata(), (ArtifactRepository)task.request.getRepository(), task.metadataFile, (Exception)task.exception);
          task.result.setException((Exception)task.exception);
        } 
      } finally {
        shutdown(executor);
      } 
      for (ResolveTask task : tasks) {
        Metadata metadata = task.request.getMetadata();
        LocalMetadataRequest localRequest = new LocalMetadataRequest(metadata, task.request.getRepository(), task.request.getRequestContext());
        File metadataFile = session.getLocalRepositoryManager().find(session, localRequest).getFile();
        if (metadataFile != null) {
          metadata = metadata.setFile(metadataFile);
          task.result.setMetadata(metadata);
        } 
        if (task.result.getException() == null)
          task.result.setUpdated(true); 
        metadataResolved(session, task.trace, metadata, (ArtifactRepository)task.request.getRepository(), task.result
            .getException());
      } 
    } 
    return results;
  }
  
  private File getLocalFile(RepositorySystemSession session, Metadata metadata) {
    LocalRepositoryManager lrm = session.getLocalRepositoryManager();
    LocalMetadataResult localResult = lrm.find(session, new LocalMetadataRequest(metadata, null, null));
    return localResult.getFile();
  }
  
  private List<RemoteRepository> getEnabledSourceRepositories(RemoteRepository repository, Metadata.Nature nature) {
    List<RemoteRepository> repositories = new ArrayList<>();
    if (repository.isRepositoryManager()) {
      for (RemoteRepository repo : repository.getMirroredRepositories()) {
        if (isEnabled(repo, nature))
          repositories.add(repo); 
      } 
    } else if (isEnabled(repository, nature)) {
      repositories.add(repository);
    } 
    return repositories;
  }
  
  private boolean isEnabled(RemoteRepository repository, Metadata.Nature nature) {
    if (!Metadata.Nature.SNAPSHOT.equals(nature) && repository.getPolicy(false).isEnabled())
      return true; 
    if (!Metadata.Nature.RELEASE.equals(nature) && repository.getPolicy(true).isEnabled())
      return true; 
    return false;
  }
  
  private RepositoryPolicy getPolicy(RepositorySystemSession session, RemoteRepository repository, Metadata.Nature nature) {
    boolean releases = !Metadata.Nature.SNAPSHOT.equals(nature);
    boolean snapshots = !Metadata.Nature.RELEASE.equals(nature);
    return this.remoteRepositoryManager.getPolicy(session, repository, releases, snapshots);
  }
  
  private void metadataResolving(RepositorySystemSession session, RequestTrace trace, Metadata metadata, ArtifactRepository repository) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.METADATA_RESOLVING);
    event.setTrace(trace);
    event.setMetadata(metadata);
    event.setRepository(repository);
    this.repositoryEventDispatcher.dispatch(event.build());
  }
  
  private void metadataResolved(RepositorySystemSession session, RequestTrace trace, Metadata metadata, ArtifactRepository repository, Exception exception) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.METADATA_RESOLVED);
    event.setTrace(trace);
    event.setMetadata(metadata);
    event.setRepository(repository);
    event.setException(exception);
    event.setFile(metadata.getFile());
    this.repositoryEventDispatcher.dispatch(event.build());
  }
  
  private void metadataDownloading(RepositorySystemSession session, RequestTrace trace, Metadata metadata, ArtifactRepository repository) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.METADATA_DOWNLOADING);
    event.setTrace(trace);
    event.setMetadata(metadata);
    event.setRepository(repository);
    this.repositoryEventDispatcher.dispatch(event.build());
  }
  
  private void metadataDownloaded(RepositorySystemSession session, RequestTrace trace, Metadata metadata, ArtifactRepository repository, File file, Exception exception) {
    RepositoryEvent.Builder event = new RepositoryEvent.Builder(session, RepositoryEvent.EventType.METADATA_DOWNLOADED);
    event.setTrace(trace);
    event.setMetadata(metadata);
    event.setRepository(repository);
    event.setException(exception);
    event.setFile(file);
    this.repositoryEventDispatcher.dispatch(event.build());
  }
  
  private Executor getExecutor(int threads) {
    if (threads <= 1)
      return new Executor() {
          public void execute(Runnable command) {
            command.run();
          }
        }; 
    return new ThreadPoolExecutor(threads, threads, 3L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), (ThreadFactory)new WorkerThreadFactory(null));
  }
  
  private void shutdown(Executor executor) {
    if (executor instanceof ExecutorService)
      ((ExecutorService)executor).shutdown(); 
  }
  
  class ResolveTask implements Runnable {
    final RepositorySystemSession session;
    
    final RequestTrace trace;
    
    final MetadataResult result;
    
    final MetadataRequest request;
    
    final File metadataFile;
    
    final String policy;
    
    final List<UpdateCheck<Metadata, MetadataTransferException>> checks;
    
    volatile MetadataTransferException exception;
    
    ResolveTask(RepositorySystemSession session, RequestTrace trace, MetadataResult result, File metadataFile, List<UpdateCheck<Metadata, MetadataTransferException>> checks, String policy) {
      this.session = session;
      this.trace = trace;
      this.result = result;
      this.request = result.getRequest();
      this.metadataFile = metadataFile;
      this.policy = policy;
      this.checks = checks;
    }
    
    public void run() {
      Metadata metadata = this.request.getMetadata();
      RemoteRepository requestRepository = this.request.getRepository();
      try {
        List<RemoteRepository> repositories = new ArrayList<>();
        for (UpdateCheck<Metadata, MetadataTransferException> check : this.checks)
          repositories.add(check.getAuthoritativeRepository()); 
        MetadataDownload download = new MetadataDownload();
        download.setMetadata(metadata);
        download.setRequestContext(this.request.getRequestContext());
        download.setFile(this.metadataFile);
        download.setChecksumPolicy(this.policy);
        download.setRepositories(repositories);
        download.setListener(SafeTransferListener.wrap(this.session));
        download.setTrace(this.trace);
        try (RepositoryConnector connector = DefaultMetadataResolver.this.repositoryConnectorProvider.newRepositoryConnector(this.session, requestRepository)) {
          connector.get(null, Arrays.asList(new MetadataDownload[] { download }));
        } 
        this.exception = download.getException();
        if (this.exception == null) {
          List<String> contexts = Collections.singletonList(this.request.getRequestContext());
          LocalMetadataRegistration registration = new LocalMetadataRegistration(metadata, requestRepository, contexts);
          this.session.getLocalRepositoryManager().add(this.session, registration);
        } else if (this.request.isDeleteLocalCopyIfMissing() && this.exception instanceof MetadataNotFoundException) {
          download.getFile().delete();
        } 
      } catch (NoRepositoryConnectorException e) {
        this.exception = new MetadataTransferException(metadata, requestRepository, (Throwable)e);
      } 
    }
  }
}
