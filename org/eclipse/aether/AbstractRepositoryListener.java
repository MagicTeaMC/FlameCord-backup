package org.eclipse.aether;

public abstract class AbstractRepositoryListener implements RepositoryListener {
  public void artifactDeployed(RepositoryEvent event) {}
  
  public void artifactDeploying(RepositoryEvent event) {}
  
  public void artifactDescriptorInvalid(RepositoryEvent event) {}
  
  public void artifactDescriptorMissing(RepositoryEvent event) {}
  
  public void artifactDownloaded(RepositoryEvent event) {}
  
  public void artifactDownloading(RepositoryEvent event) {}
  
  public void artifactInstalled(RepositoryEvent event) {}
  
  public void artifactInstalling(RepositoryEvent event) {}
  
  public void artifactResolved(RepositoryEvent event) {}
  
  public void artifactResolving(RepositoryEvent event) {}
  
  public void metadataDeployed(RepositoryEvent event) {}
  
  public void metadataDeploying(RepositoryEvent event) {}
  
  public void metadataDownloaded(RepositoryEvent event) {}
  
  public void metadataDownloading(RepositoryEvent event) {}
  
  public void metadataInstalled(RepositoryEvent event) {}
  
  public void metadataInstalling(RepositoryEvent event) {}
  
  public void metadataInvalid(RepositoryEvent event) {}
  
  public void metadataResolved(RepositoryEvent event) {}
  
  public void metadataResolving(RepositoryEvent event) {}
}
