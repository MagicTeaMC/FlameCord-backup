package org.eclipse.aether.util.repository;

import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.AuthenticationDigest;

final class ComponentAuthentication implements Authentication {
  private final String key;
  
  private final Object value;
  
  ComponentAuthentication(String key, Object value) {
    this.key = Objects.<String>requireNonNull(key, "authentication key cannot be null");
    if (key.length() == 0)
      throw new IllegalArgumentException("authentication key cannot be empty"); 
    this.value = value;
  }
  
  public void fill(AuthenticationContext context, String key, Map<String, String> data) {
    context.put(this.key, this.value);
  }
  
  public void digest(AuthenticationDigest digest) {
    if (this.value != null)
      digest.update(new String[] { this.key, this.value.getClass().getName() }); 
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    ComponentAuthentication that = (ComponentAuthentication)obj;
    return (this.key.equals(that.key) && eqClass(this.value, that.value));
  }
  
  private static <T> boolean eqClass(T s1, T s2) {
    return (s1 == null) ? ((s2 == null)) : ((s2 != null && s1.getClass().equals(s2.getClass())));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + this.key.hashCode();
    hash = hash * 31 + ((this.value != null) ? this.value.getClass().hashCode() : 0);
    return hash;
  }
  
  public String toString() {
    return this.key + "=" + this.value;
  }
}
