package org.eclipse.aether.spi.localrepo;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.LocalRepositoryManager;
import org.eclipse.aether.repository.NoLocalRepositoryManagerException;

public interface LocalRepositoryManagerFactory {
  LocalRepositoryManager newInstance(RepositorySystemSession paramRepositorySystemSession, LocalRepository paramLocalRepository) throws NoLocalRepositoryManagerException;
  
  float getPriority();
}
