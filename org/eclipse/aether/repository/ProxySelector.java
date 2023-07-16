package org.eclipse.aether.repository;

public interface ProxySelector {
  Proxy getProxy(RemoteRepository paramRemoteRepository);
}
