package org.eclipse.aether.util.repository;

import java.util.HashMap;
import java.util.Map;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationSelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class DefaultAuthenticationSelector implements AuthenticationSelector {
  private final Map<String, Authentication> repos = new HashMap<>();
  
  public DefaultAuthenticationSelector add(String id, Authentication auth) {
    if (auth != null) {
      this.repos.put(id, auth);
    } else {
      this.repos.remove(id);
    } 
    return this;
  }
  
  public Authentication getAuthentication(RemoteRepository repository) {
    return this.repos.get(repository.getId());
  }
}
