package org.eclipse.aether.util.repository;

import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.AuthenticationDigest;

final class StringAuthentication implements Authentication {
  private final String key;
  
  private final String value;
  
  StringAuthentication(String key, String value) {
    this.key = Objects.<String>requireNonNull(key, "authentication key cannot be null");
    if (key.length() == 0)
      throw new IllegalArgumentException("authentication key cannot be empty"); 
    this.value = value;
  }
  
  public void fill(AuthenticationContext context, String key, Map<String, String> data) {
    context.put(this.key, this.value);
  }
  
  public void digest(AuthenticationDigest digest) {
    digest.update(new String[] { this.key, this.value });
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    StringAuthentication that = (StringAuthentication)obj;
    return (Objects.equals(this.key, that.key) && 
      Objects.equals(this.value, that.value));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + this.key.hashCode();
    hash = hash * 31 + ((this.value != null) ? this.value.hashCode() : 0);
    return hash;
  }
  
  public String toString() {
    return this.key + "=" + this.value;
  }
}
