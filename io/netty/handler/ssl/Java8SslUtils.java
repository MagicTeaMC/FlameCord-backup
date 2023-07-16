package io.netty.handler.ssl;

import io.netty.util.CharsetUtil;
import io.netty.util.internal.SuppressJava6Requirement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.net.ssl.SNIHostName;
import javax.net.ssl.SNIMatcher;
import javax.net.ssl.SNIServerName;
import javax.net.ssl.SSLParameters;

@SuppressJava6Requirement(reason = "Usage guarded by java version check")
final class Java8SslUtils {
  static List<String> getSniHostNames(SSLParameters sslParameters) {
    List<SNIServerName> names = sslParameters.getServerNames();
    if (names == null || names.isEmpty())
      return Collections.emptyList(); 
    List<String> strings = new ArrayList<String>(names.size());
    for (SNIServerName serverName : names) {
      if (serverName instanceof SNIHostName) {
        strings.add(((SNIHostName)serverName).getAsciiName());
        continue;
      } 
      throw new IllegalArgumentException("Only " + SNIHostName.class.getName() + " instances are supported, but found: " + serverName);
    } 
    return strings;
  }
  
  static void setSniHostNames(SSLParameters sslParameters, List<String> names) {
    sslParameters.setServerNames(getSniHostNames(names));
  }
  
  static boolean isValidHostNameForSNI(String hostname) {
    try {
      new SNIHostName(hostname);
      return true;
    } catch (IllegalArgumentException illegal) {
      return false;
    } 
  }
  
  static List getSniHostNames(List<String> names) {
    if (names == null || names.isEmpty())
      return Collections.emptyList(); 
    List<SNIServerName> sniServerNames = new ArrayList<SNIServerName>(names.size());
    for (String name : names)
      sniServerNames.add(new SNIHostName(name.getBytes(CharsetUtil.UTF_8))); 
    return sniServerNames;
  }
  
  static List getSniHostName(byte[] hostname) {
    if (hostname == null || hostname.length == 0)
      return Collections.emptyList(); 
    return Collections.singletonList(new SNIHostName(hostname));
  }
  
  static boolean getUseCipherSuitesOrder(SSLParameters sslParameters) {
    return sslParameters.getUseCipherSuitesOrder();
  }
  
  static void setUseCipherSuitesOrder(SSLParameters sslParameters, boolean useOrder) {
    sslParameters.setUseCipherSuitesOrder(useOrder);
  }
  
  static void setSNIMatchers(SSLParameters sslParameters, Collection<?> matchers) {
    sslParameters.setSNIMatchers((Collection)matchers);
  }
  
  static boolean checkSniHostnameMatch(Collection<?> matchers, byte[] hostname) {
    if (matchers != null && !matchers.isEmpty()) {
      SNIHostName name = new SNIHostName(hostname);
      Iterator<SNIMatcher> matcherIt = (Iterator)matchers.iterator();
      while (matcherIt.hasNext()) {
        SNIMatcher matcher = matcherIt.next();
        if (matcher.getType() == 0 && matcher.matches(name))
          return true; 
      } 
      return false;
    } 
    return true;
  }
}
