package org.eclipse.aether.internal.impl;

import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;

@Named("simple")
public class SimpleLocalRepositoryManagerFactory implements LocalRepositoryManagerFactory {
  private float priority;
  
  public LocalRepositoryManager newInstance(RepositorySystemSession session, LocalRepository repository) throws NoLocalRepositoryManagerException {
    if ("".equals(repository.getContentType()) || "simple".equals(repository.getContentType()))
      return new SimpleLocalRepositoryManager(repository.getBasedir()); 
    throw new NoLocalRepositoryManagerException(repository);
  }
  
  public float getPriority() {
    return this.priority;
  }
  
  public SimpleLocalRepositoryManagerFactory setPriority(float priority) {
    this.priority = priority;
    return this;
  }
}
