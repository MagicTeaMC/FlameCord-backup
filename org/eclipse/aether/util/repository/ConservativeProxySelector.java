package org.eclipse.aether.util.repository;

import java.util.Objects;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class ConservativeProxySelector implements ProxySelector {
  private final ProxySelector selector;
  
  public ConservativeProxySelector(ProxySelector selector) {
    this.selector = Objects.<ProxySelector>requireNonNull(selector, "proxy selector cannot be null");
  }
  
  public Proxy getProxy(RemoteRepository repository) {
    Proxy proxy = repository.getProxy();
    if (proxy != null)
      return proxy; 
    return this.selector.getProxy(repository);
  }
}
