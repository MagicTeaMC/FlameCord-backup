package org.eclipse.aether.util.repository;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.AuthenticationDigest;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class JreProxySelector implements ProxySelector {
  public Proxy getProxy(RemoteRepository repository) {
    List<Proxy> proxies = null;
    try {
      URI uri = (new URI(repository.getUrl())).parseServerAuthority();
      proxies = ProxySelector.getDefault().select(uri);
    } catch (Exception exception) {}
    if (proxies != null)
      for (Proxy proxy : proxies) {
        if (Proxy.Type.DIRECT.equals(proxy.type()))
          break; 
        if (Proxy.Type.HTTP.equals(proxy.type()) && isValid(proxy.address())) {
          InetSocketAddress addr = (InetSocketAddress)proxy.address();
          return new Proxy("http", addr.getHostName(), addr.getPort(), JreProxyAuthentication.INSTANCE);
        } 
      }  
    return null;
  }
  
  private static boolean isValid(SocketAddress address) {
    if (address instanceof InetSocketAddress) {
      InetSocketAddress addr = (InetSocketAddress)address;
      if (addr.getPort() <= 0)
        return false; 
      if (addr.getHostName() == null || addr.getHostName().length() <= 0)
        return false; 
      return true;
    } 
    return false;
  }
  
  private static final class JreProxyAuthentication implements Authentication {
    public static final Authentication INSTANCE = new JreProxyAuthentication();
    
    public void fill(AuthenticationContext context, String key, Map<String, String> data) {
      Proxy proxy = context.getProxy();
      if (proxy == null)
        return; 
      if (!"username".equals(key) && !"password".equals(key))
        return; 
      try {
        URL url;
        try {
          url = new URL(context.getRepository().getUrl());
        } catch (Exception e) {
          url = null;
        } 
        PasswordAuthentication auth = Authenticator.requestPasswordAuthentication(proxy.getHost(), null, proxy.getPort(), "http", "Credentials for proxy " + proxy, null, url, Authenticator.RequestorType.PROXY);
        if (auth != null) {
          context.put("username", auth.getUserName());
          context.put("password", auth.getPassword());
        } else {
          context.put("username", System.getProperty("http.proxyUser"));
          context.put("password", System.getProperty("http.proxyPassword"));
        } 
      } catch (SecurityException securityException) {}
    }
    
    public void digest(AuthenticationDigest digest) {
      digest.update(new String[] { UUID.randomUUID().toString() });
    }
    
    public boolean equals(Object obj) {
      return (this == obj || (obj != null && getClass().equals(obj.getClass())));
    }
    
    public int hashCode() {
      return getClass().hashCode();
    }
  }
}
