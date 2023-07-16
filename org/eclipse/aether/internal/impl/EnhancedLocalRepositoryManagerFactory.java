package org.eclipse.aether.internal.impl;

import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;

@Named("enhanced")
public class EnhancedLocalRepositoryManagerFactory implements LocalRepositoryManagerFactory {
  private float priority = 10.0F;
  
  public LocalRepositoryManager newInstance(RepositorySystemSession session, LocalRepository repository) throws NoLocalRepositoryManagerException {
    if ("".equals(repository.getContentType()) || "default".equals(repository.getContentType()))
      return new EnhancedLocalRepositoryManager(repository.getBasedir(), session); 
    throw new NoLocalRepositoryManagerException(repository);
  }
  
  public float getPriority() {
    return this.priority;
  }
  
  public EnhancedLocalRepositoryManagerFactory setPriority(float priority) {
    this.priority = priority;
    return this;
  }
}
