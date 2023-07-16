package org.eclipse.aether.util.repository;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.AuthenticationDigest;

final class ChainedAuthentication implements Authentication {
  private final Authentication[] authentications;
  
  ChainedAuthentication(Authentication... authentications) {
    if (authentications != null && authentications.length > 0) {
      this.authentications = (Authentication[])authentications.clone();
    } else {
      this.authentications = new Authentication[0];
    } 
  }
  
  ChainedAuthentication(Collection<? extends Authentication> authentications) {
    if (authentications != null && !authentications.isEmpty()) {
      this.authentications = authentications.<Authentication>toArray(new Authentication[authentications.size()]);
    } else {
      this.authentications = new Authentication[0];
    } 
  }
  
  public void fill(AuthenticationContext context, String key, Map<String, String> data) {
    for (Authentication authentication : this.authentications)
      authentication.fill(context, key, data); 
  }
  
  public void digest(AuthenticationDigest digest) {
    for (Authentication authentication : this.authentications)
      authentication.digest(digest); 
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    ChainedAuthentication that = (ChainedAuthentication)obj;
    return Arrays.equals((Object[])this.authentications, (Object[])that.authentications);
  }
  
  public int hashCode() {
    return Arrays.hashCode((Object[])this.authentications);
  }
  
  public String toString() {
    StringBuilder buffer = new StringBuilder(256);
    for (Authentication authentication : this.authentications) {
      if (buffer.length() > 0)
        buffer.append(", "); 
      buffer.append(authentication);
    } 
    return buffer.toString();
  }
}
