package org.eclipse.aether.util.repository;

import java.util.Objects;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class ConservativeAuthenticationSelector implements AuthenticationSelector {
  private final AuthenticationSelector selector;
  
  public ConservativeAuthenticationSelector(AuthenticationSelector selector) {
    this.selector = Objects.<AuthenticationSelector>requireNonNull(selector, "authentication selector cannot be null");
  }
  
  public Authentication getAuthentication(RemoteRepository repository) {
    Authentication auth = repository.getAuthentication();
    if (auth != null)
      return auth; 
    return this.selector.getAuthentication(repository);
  }
}
