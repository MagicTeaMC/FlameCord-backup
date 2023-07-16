package org.eclipse.aether;

public interface RepositoryListener {
  void artifactDescriptorInvalid(RepositoryEvent paramRepositoryEvent);
  
  void artifactDescriptorMissing(RepositoryEvent paramRepositoryEvent);
  
  void metadataInvalid(RepositoryEvent paramRepositoryEvent);
  
  void artifactResolving(RepositoryEvent paramRepositoryEvent);
  
  void artifactResolved(RepositoryEvent paramRepositoryEvent);
  
  void metadataResolving(RepositoryEvent paramRepositoryEvent);
  
  void metadataResolved(RepositoryEvent paramRepositoryEvent);
  
  void artifactDownloading(RepositoryEvent paramRepositoryEvent);
  
  void artifactDownloaded(RepositoryEvent paramRepositoryEvent);
  
  void metadataDownloading(RepositoryEvent paramRepositoryEvent);
  
  void metadataDownloaded(RepositoryEvent paramRepositoryEvent);
  
  void artifactInstalling(RepositoryEvent paramRepositoryEvent);
  
  void artifactInstalled(RepositoryEvent paramRepositoryEvent);
  
  void metadataInstalling(RepositoryEvent paramRepositoryEvent);
  
  void metadataInstalled(RepositoryEvent paramRepositoryEvent);
  
  void artifactDeploying(RepositoryEvent paramRepositoryEvent);
  
  void artifactDeployed(RepositoryEvent paramRepositoryEvent);
  
  void metadataDeploying(RepositoryEvent paramRepositoryEvent);
  
  void metadataDeployed(RepositoryEvent paramRepositoryEvent);
}
