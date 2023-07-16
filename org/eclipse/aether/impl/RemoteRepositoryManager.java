package org.eclipse.aether.impl;

import java.util.List;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.repository.RepositoryPolicy;

public interface RemoteRepositoryManager {
  List<RemoteRepository> aggregateRepositories(RepositorySystemSession paramRepositorySystemSession, List<RemoteRepository> paramList1, List<RemoteRepository> paramList2, boolean paramBoolean);
  
  RepositoryPolicy getPolicy(RepositorySystemSession paramRepositorySystemSession, RemoteRepository paramRemoteRepository, boolean paramBoolean1, boolean paramBoolean2);
}
