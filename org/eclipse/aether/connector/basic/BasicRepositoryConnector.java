package org.eclipse.aether.connector.basic;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.ArtifactDownload;
import org.eclipse.aether.spi.connector.ArtifactTransfer;
import org.eclipse.aether.spi.connector.ArtifactUpload;
import org.eclipse.aether.spi.connector.MetadataDownload;
import org.eclipse.aether.spi.connector.MetadataTransfer;
import org.eclipse.aether.spi.connector.MetadataUpload;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicy;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicyProvider;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.spi.connector.transport.GetTask;
import org.eclipse.aether.spi.connector.transport.PeekTask;
import org.eclipse.aether.spi.connector.transport.PutTask;
import org.eclipse.aether.spi.connector.transport.Transporter;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.transfer.ChecksumFailureException;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import org.eclipse.aether.transfer.NoRepositoryLayoutException;
import org.eclipse.aether.transfer.NoTransporterException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferResource;
import org.eclipse.aether.transform.FileTransformer;
import org.eclipse.aether.util.ChecksumUtils;
import org.eclipse.aether.util.ConfigUtils;
import org.eclipse.aether.util.concurrency.RunnableErrorForwarder;
import org.eclipse.aether.util.concurrency.WorkerThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class BasicRepositoryConnector implements RepositoryConnector {
  private static final String CONFIG_PROP_THREADS = "aether.connector.basic.threads";
  
  private static final String CONFIG_PROP_RESUME = "aether.connector.resumeDownloads";
  
  private static final String CONFIG_PROP_RESUME_THRESHOLD = "aether.connector.resumeThreshold";
  
  private static final String CONFIG_PROP_SMART_CHECKSUMS = "aether.connector.smartChecksums";
  
  private static final Logger LOGGER = LoggerFactory.getLogger(BasicRepositoryConnector.class);
  
  private final FileProcessor fileProcessor;
  
  private final RemoteRepository repository;
  
  private final RepositorySystemSession session;
  
  private final Transporter transporter;
  
  private final RepositoryLayout layout;
  
  private final ChecksumPolicyProvider checksumPolicyProvider;
  
  private final PartialFile.Factory partialFileFactory;
  
  private final int maxThreads;
  
  private final boolean smartChecksums;
  
  private final boolean persistedChecksums;
  
  private Executor executor;
  
  private boolean closed;
  
  BasicRepositoryConnector(RepositorySystemSession session, RemoteRepository repository, TransporterProvider transporterProvider, RepositoryLayoutProvider layoutProvider, ChecksumPolicyProvider checksumPolicyProvider, FileProcessor fileProcessor) throws NoRepositoryConnectorException {
    try {
      this.layout = layoutProvider.newRepositoryLayout(session, repository);
    } catch (NoRepositoryLayoutException e) {
      throw new NoRepositoryConnectorException(repository, e.getMessage(), e);
    } 
    try {
      this.transporter = transporterProvider.newTransporter(session, repository);
    } catch (NoTransporterException e) {
      throw new NoRepositoryConnectorException(repository, e.getMessage(), e);
    } 
    this.checksumPolicyProvider = checksumPolicyProvider;
    this.session = session;
    this.repository = repository;
    this.fileProcessor = fileProcessor;
    this.maxThreads = ConfigUtils.getInteger(session, 5, new String[] { "aether.connector.basic.threads", "maven.artifact.threads" });
    this.smartChecksums = ConfigUtils.getBoolean(session, true, new String[] { "aether.connector.smartChecksums" });
    this
      .persistedChecksums = ConfigUtils.getBoolean(session, true, new String[] { "aether.connector.persistedChecksums" });
    boolean resumeDownloads = ConfigUtils.getBoolean(session, true, new String[] { "aether.connector.resumeDownloads." + repository.getId(), "aether.connector.resumeDownloads" });
    long resumeThreshold = ConfigUtils.getLong(session, 65536L, new String[] { "aether.connector.resumeThreshold." + repository.getId(), "aether.connector.resumeThreshold" });
    int requestTimeout = ConfigUtils.getInteger(session, 1800000, new String[] { "aether.connector.requestTimeout." + repository.getId(), "aether.connector.requestTimeout" });
    this.partialFileFactory = new PartialFile.Factory(resumeDownloads, resumeThreshold, requestTimeout);
  }
  
  private Executor getExecutor(Collection<?> artifacts, Collection<?> metadatas) {
    if (this.maxThreads <= 1)
      return DirectExecutor.INSTANCE; 
    int tasks = safe(artifacts).size() + safe(metadatas).size();
    if (tasks <= 1)
      return DirectExecutor.INSTANCE; 
    if (this.executor == null)
      this
        
        .executor = new ThreadPoolExecutor(this.maxThreads, this.maxThreads, 3L, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), (ThreadFactory)new WorkerThreadFactory(getClass().getSimpleName() + '-' + this.repository.getHost() + '-')); 
    return this.executor;
  }
  
  protected void finalize() throws Throwable {
    try {
      close();
    } finally {
      super.finalize();
    } 
  }
  
  public void close() {
    if (!this.closed) {
      this.closed = true;
      if (this.executor instanceof ExecutorService)
        ((ExecutorService)this.executor).shutdown(); 
      this.transporter.close();
    } 
  }
  
  public void get(Collection<? extends ArtifactDownload> artifactDownloads, Collection<? extends MetadataDownload> metadataDownloads) {
    if (this.closed)
      throw new IllegalStateException("connector closed"); 
    Executor executor = getExecutor(artifactDownloads, metadataDownloads);
    RunnableErrorForwarder errorForwarder = new RunnableErrorForwarder();
    for (MetadataDownload transfer : safe(metadataDownloads)) {
      URI location = this.layout.getLocation(transfer.getMetadata(), false);
      TransferResource resource = newTransferResource(location, transfer.getFile(), transfer.getTrace());
      TransferEvent.Builder builder = newEventBuilder(resource, false, false);
      MetadataTransportListener listener = new MetadataTransportListener((MetadataTransfer)transfer, this.repository, builder);
      ChecksumPolicy checksumPolicy = newChecksumPolicy(transfer.getChecksumPolicy(), resource);
      List<RepositoryLayout.Checksum> checksums = null;
      if (checksumPolicy != null)
        checksums = this.layout.getChecksums(transfer.getMetadata(), false, location); 
      Runnable task = new GetTaskRunner(location, transfer.getFile(), checksumPolicy, checksums, listener);
      executor.execute(errorForwarder.wrap(task));
    } 
    for (ArtifactDownload transfer : safe(artifactDownloads)) {
      Runnable task;
      URI location = this.layout.getLocation(transfer.getArtifact(), false);
      TransferResource resource = newTransferResource(location, transfer.getFile(), transfer.getTrace());
      TransferEvent.Builder builder = newEventBuilder(resource, false, transfer.isExistenceCheck());
      ArtifactTransportListener listener = new ArtifactTransportListener((ArtifactTransfer)transfer, this.repository, builder);
      if (transfer.isExistenceCheck()) {
        task = new PeekTaskRunner(location, listener);
      } else {
        ChecksumPolicy checksumPolicy = newChecksumPolicy(transfer.getChecksumPolicy(), resource);
        List<RepositoryLayout.Checksum> checksums = null;
        if (checksumPolicy != null)
          checksums = this.layout.getChecksums(transfer.getArtifact(), false, location); 
        task = new GetTaskRunner(location, transfer.getFile(), checksumPolicy, checksums, listener);
      } 
      executor.execute(errorForwarder.wrap(task));
    } 
    errorForwarder.await();
  }
  
  public void put(Collection<? extends ArtifactUpload> artifactUploads, Collection<? extends MetadataUpload> metadataUploads) {
    if (this.closed)
      throw new IllegalStateException("connector closed"); 
    for (ArtifactUpload transfer : safe(artifactUploads)) {
      URI location = this.layout.getLocation(transfer.getArtifact(), true);
      TransferResource resource = newTransferResource(location, transfer.getFile(), transfer.getTrace());
      TransferEvent.Builder builder = newEventBuilder(resource, true, false);
      ArtifactTransportListener listener = new ArtifactTransportListener((ArtifactTransfer)transfer, this.repository, builder);
      List<RepositoryLayout.Checksum> checksums = this.layout.getChecksums(transfer.getArtifact(), true, location);
      Runnable task = new PutTaskRunner(location, transfer.getFile(), transfer.getFileTransformer(), checksums, listener);
      task.run();
    } 
    for (MetadataUpload transfer : safe(metadataUploads)) {
      URI location = this.layout.getLocation(transfer.getMetadata(), true);
      TransferResource resource = newTransferResource(location, transfer.getFile(), transfer.getTrace());
      TransferEvent.Builder builder = newEventBuilder(resource, true, false);
      MetadataTransportListener listener = new MetadataTransportListener((MetadataTransfer)transfer, this.repository, builder);
      List<RepositoryLayout.Checksum> checksums = this.layout.getChecksums(transfer.getMetadata(), true, location);
      Runnable task = new PutTaskRunner(location, transfer.getFile(), checksums, listener);
      task.run();
    } 
  }
  
  private static <T> Collection<T> safe(Collection<T> items) {
    return (items != null) ? items : Collections.<T>emptyList();
  }
  
  private TransferResource newTransferResource(URI path, File file, RequestTrace trace) {
    return new TransferResource(this.repository.getId(), this.repository.getUrl(), path.toString(), file, trace);
  }
  
  private TransferEvent.Builder newEventBuilder(TransferResource resource, boolean upload, boolean peek) {
    TransferEvent.Builder builder = new TransferEvent.Builder(this.session, resource);
    if (upload) {
      builder.setRequestType(TransferEvent.RequestType.PUT);
    } else if (!peek) {
      builder.setRequestType(TransferEvent.RequestType.GET);
    } else {
      builder.setRequestType(TransferEvent.RequestType.GET_EXISTENCE);
    } 
    return builder;
  }
  
  private ChecksumPolicy newChecksumPolicy(String policy, TransferResource resource) {
    return this.checksumPolicyProvider.newChecksumPolicy(this.session, this.repository, resource, policy);
  }
  
  public String toString() {
    return String.valueOf(this.repository);
  }
  
  abstract class TaskRunner implements Runnable {
    protected final URI path;
    
    protected final TransferTransportListener<?> listener;
    
    TaskRunner(URI path, TransferTransportListener<?> listener) {
      this.path = path;
      this.listener = listener;
    }
    
    public void run() {
      try {
        this.listener.transferInitiated();
        runTask();
        this.listener.transferSucceeded();
      } catch (Exception e) {
        this.listener.transferFailed(e, BasicRepositoryConnector.this.transporter.classify(e));
      } 
    }
    
    protected abstract void runTask() throws Exception;
  }
  
  class PeekTaskRunner extends TaskRunner {
    PeekTaskRunner(URI path, TransferTransportListener<?> listener) {
      super(path, listener);
    }
    
    protected void runTask() throws Exception {
      BasicRepositoryConnector.this.transporter.peek(new PeekTask(this.path));
    }
  }
  
  class GetTaskRunner extends TaskRunner implements PartialFile.RemoteAccessChecker, ChecksumValidator.ChecksumFetcher {
    private final File file;
    
    private final ChecksumValidator checksumValidator;
    
    GetTaskRunner(URI path, File file, ChecksumPolicy checksumPolicy, List<RepositoryLayout.Checksum> checksums, TransferTransportListener<?> listener) {
      super(path, listener);
      this.file = Objects.<File>requireNonNull(file, "destination file cannot be null");
      this
        .checksumValidator = new ChecksumValidator(file, BasicRepositoryConnector.this.fileProcessor, this, checksumPolicy, (Collection)BasicRepositoryConnector.safe((Collection)checksums));
    }
    
    public void checkRemoteAccess() throws Exception {
      BasicRepositoryConnector.this.transporter.peek(new PeekTask(this.path));
    }
    
    public boolean fetchChecksum(URI remote, File local) throws Exception {
      try {
        BasicRepositoryConnector.this.transporter.get((new GetTask(remote)).setDataFile(local));
      } catch (Exception e) {
        if (BasicRepositoryConnector.this.transporter.classify(e) == 1)
          return false; 
        throw e;
      } 
      return true;
    }
    
    protected void runTask() throws Exception {
      BasicRepositoryConnector.this.fileProcessor.mkdirs(this.file.getParentFile());
      PartialFile partFile = BasicRepositoryConnector.this.partialFileFactory.newInstance(this.file, this);
      if (partFile == null) {
        BasicRepositoryConnector.LOGGER.debug("Concurrent download of {} just finished, skipping download", this.file);
        return;
      } 
      try {
        File tmp = partFile.getFile();
        this.listener.setChecksumCalculator(this.checksumValidator.newChecksumCalculator(tmp));
        int firstTrial = 0, lastTrial = 1, trial = firstTrial;
        while (true) {
          boolean resume = (partFile.isResume() && trial <= firstTrial);
          GetTask task = (new GetTask(this.path)).setDataFile(tmp, resume).setListener(this.listener);
          BasicRepositoryConnector.this.transporter.get(task);
          try {
            this.checksumValidator.validate(this.listener.getChecksums(), BasicRepositoryConnector.this.smartChecksums ? task.getChecksums() : null);
          } catch (ChecksumFailureException e) {
            boolean retry = (trial < lastTrial && e.isRetryWorthy());
            if (!retry && !this.checksumValidator.handle(e))
              throw e; 
            this.listener.transferCorrupted((Exception)e);
            if (retry) {
              this.checksumValidator.retry();
              trial++;
            } 
          } 
          break;
        } 
        BasicRepositoryConnector.this.fileProcessor.move(tmp, this.file);
        if (BasicRepositoryConnector.this.persistedChecksums)
          this.checksumValidator.commit(); 
      } finally {
        partFile.close();
        this.checksumValidator.close();
      } 
    }
  }
  
  class PutTaskRunner extends TaskRunner {
    private final File file;
    
    private final FileTransformer fileTransformer;
    
    private final Collection<RepositoryLayout.Checksum> checksums;
    
    PutTaskRunner(URI path, File file, List<RepositoryLayout.Checksum> checksums, TransferTransportListener<?> listener) {
      this(path, file, (FileTransformer)null, checksums, listener);
    }
    
    PutTaskRunner(URI path, File file, FileTransformer fileTransformer, List<RepositoryLayout.Checksum> checksums, TransferTransportListener<?> listener) {
      super(path, listener);
      this.file = Objects.<File>requireNonNull(file, "source file cannot be null");
      this.fileTransformer = fileTransformer;
      this.checksums = (Collection)BasicRepositoryConnector.safe((Collection)checksums);
    }
    
    protected void runTask() throws Exception {
      if (this.fileTransformer != null) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        try (InputStream transformData = this.fileTransformer.transformData(this.file)) {
          int read;
          while ((read = transformData.read(buffer, 0, buffer.length)) != -1)
            baos.write(buffer, 0, read); 
        } 
        byte[] bytes = baos.toByteArray();
        BasicRepositoryConnector.this.transporter.put((new PutTask(this.path)).setDataBytes(bytes).setListener(this.listener));
        uploadChecksums(this.file, bytes);
      } else {
        BasicRepositoryConnector.this.transporter.put((new PutTask(this.path)).setDataFile(this.file).setListener(this.listener));
        uploadChecksums(this.file, (byte[])null);
      } 
    }
    
    private void uploadChecksums(File file, byte[] bytes) {
      if (this.checksums.isEmpty())
        return; 
      try {
        Map<String, Object> sumsByAlgo;
        Set<String> algos = new HashSet<>();
        for (RepositoryLayout.Checksum checksum : this.checksums)
          algos.add(checksum.getAlgorithm()); 
        if (bytes != null) {
          sumsByAlgo = ChecksumUtils.calc(bytes, algos);
        } else {
          sumsByAlgo = ChecksumUtils.calc(file, algos);
        } 
        for (RepositoryLayout.Checksum checksum : this.checksums)
          uploadChecksum(checksum.getLocation(), sumsByAlgo.get(checksum.getAlgorithm())); 
      } catch (IOException e) {
        BasicRepositoryConnector.LOGGER.warn("Failed to upload checksums for {}", file, e);
      } 
    }
    
    private void uploadChecksum(URI location, Object checksum) {
      try {
        if (checksum instanceof Exception)
          throw (Exception)checksum; 
        BasicRepositoryConnector.this.transporter.put((new PutTask(location)).setDataString((String)checksum));
      } catch (Exception e) {
        BasicRepositoryConnector.LOGGER.warn("Failed to upload checksum to {}", location, e);
      } 
    }
  }
  
  private static class DirectExecutor implements Executor {
    static final Executor INSTANCE = new DirectExecutor();
    
    public void execute(Runnable command) {
      command.run();
    }
  }
}
