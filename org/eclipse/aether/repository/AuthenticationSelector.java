package org.eclipse.aether.repository;

public interface AuthenticationSelector {
  Authentication getAuthentication(RemoteRepository paramRemoteRepository);
}
