package org.eclipse.aether.repository;

import java.util.Map;

public interface Authentication {
  void fill(AuthenticationContext paramAuthenticationContext, String paramString, Map<String, String> paramMap);
  
  void digest(AuthenticationDigest paramAuthenticationDigest);
}
