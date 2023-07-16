package io.netty.handler.ssl;

import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class SupportedCipherSuiteFilter implements CipherSuiteFilter {
  public static final SupportedCipherSuiteFilter INSTANCE = new SupportedCipherSuiteFilter();
  
  public String[] filterCipherSuites(Iterable<String> ciphers, List<String> defaultCiphers, Set<String> supportedCiphers) {
    List<String> newCiphers;
    ObjectUtil.checkNotNull(defaultCiphers, "defaultCiphers");
    ObjectUtil.checkNotNull(supportedCiphers, "supportedCiphers");
    if (ciphers == null) {
      newCiphers = new ArrayList<String>(defaultCiphers.size());
      ciphers = defaultCiphers;
    } else {
      newCiphers = new ArrayList<String>(supportedCiphers.size());
    } 
    for (String c : ciphers) {
      if (c == null)
        break; 
      if (supportedCiphers.contains(c))
        newCiphers.add(c); 
    } 
    return newCiphers.<String>toArray(new String[0]);
  }
}
