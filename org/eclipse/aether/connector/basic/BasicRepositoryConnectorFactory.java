package org.eclipse.aether.connector.basic;

import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicyProvider;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;

@Named("basic")
public final class BasicRepositoryConnectorFactory implements RepositoryConnectorFactory, Service {
  private TransporterProvider transporterProvider;
  
  private RepositoryLayoutProvider layoutProvider;
  
  private ChecksumPolicyProvider checksumPolicyProvider;
  
  private FileProcessor fileProcessor;
  
  private float priority;
  
  public BasicRepositoryConnectorFactory() {}
  
  @Inject
  BasicRepositoryConnectorFactory(TransporterProvider transporterProvider, RepositoryLayoutProvider layoutProvider, ChecksumPolicyProvider checksumPolicyProvider, FileProcessor fileProcessor) {
    setTransporterProvider(transporterProvider);
    setRepositoryLayoutProvider(layoutProvider);
    setChecksumPolicyProvider(checksumPolicyProvider);
    setFileProcessor(fileProcessor);
  }
  
  public void initService(ServiceLocator locator) {
    setTransporterProvider((TransporterProvider)locator.getService(TransporterProvider.class));
    setRepositoryLayoutProvider((RepositoryLayoutProvider)locator.getService(RepositoryLayoutProvider.class));
    setChecksumPolicyProvider((ChecksumPolicyProvider)locator.getService(ChecksumPolicyProvider.class));
    setFileProcessor((FileProcessor)locator.getService(FileProcessor.class));
  }
  
  public BasicRepositoryConnectorFactory setTransporterProvider(TransporterProvider transporterProvider) {
    this.transporterProvider = Objects.<TransporterProvider>requireNonNull(transporterProvider, "transporter provider cannot be null");
    return this;
  }
  
  public BasicRepositoryConnectorFactory setRepositoryLayoutProvider(RepositoryLayoutProvider layoutProvider) {
    this.layoutProvider = Objects.<RepositoryLayoutProvider>requireNonNull(layoutProvider, "repository layout provider cannot be null");
    return this;
  }
  
  public BasicRepositoryConnectorFactory setChecksumPolicyProvider(ChecksumPolicyProvider checksumPolicyProvider) {
    this.checksumPolicyProvider = Objects.<ChecksumPolicyProvider>requireNonNull(checksumPolicyProvider, "checksum policy provider cannot be null");
    return this;
  }
  
  public BasicRepositoryConnectorFactory setFileProcessor(FileProcessor fileProcessor) {
    this.fileProcessor = Objects.<FileProcessor>requireNonNull(fileProcessor, "file processor cannot be null");
    return this;
  }
  
  public float getPriority() {
    return this.priority;
  }
  
  public BasicRepositoryConnectorFactory setPriority(float priority) {
    this.priority = priority;
    return this;
  }
  
  public RepositoryConnector newInstance(RepositorySystemSession session, RemoteRepository repository) throws NoRepositoryConnectorException {
    Objects.requireNonNull("session", "session cannot be null");
    Objects.requireNonNull("repository", "repository cannot be null");
    return new BasicRepositoryConnector(session, repository, this.transporterProvider, this.layoutProvider, this.checksumPolicyProvider, this.fileProcessor);
  }
}
