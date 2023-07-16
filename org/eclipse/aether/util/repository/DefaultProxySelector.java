package org.eclipse.aether.util.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.ProxySelector;
import org.eclipse.aether.repository.RemoteRepository;

public final class DefaultProxySelector implements ProxySelector {
  private List<ProxyDef> proxies = new ArrayList<>();
  
  public DefaultProxySelector add(Proxy proxy, String nonProxyHosts) {
    Objects.requireNonNull(proxy, "proxy cannot be null");
    this.proxies.add(new ProxyDef(proxy, nonProxyHosts));
    return this;
  }
  
  public Proxy getProxy(RemoteRepository repository) {
    Map<String, ProxyDef> candidates = new HashMap<>();
    String host = repository.getHost();
    for (ProxyDef proxyDef : this.proxies) {
      if (!proxyDef.nonProxyHosts.isNonProxyHost(host)) {
        String key = proxyDef.proxy.getType().toLowerCase(Locale.ENGLISH);
        if (!candidates.containsKey(key))
          candidates.put(key, proxyDef); 
      } 
    } 
    String protocol = repository.getProtocol().toLowerCase(Locale.ENGLISH);
    if ("davs".equals(protocol)) {
      protocol = "https";
    } else if ("dav".equals(protocol)) {
      protocol = "http";
    } else if (protocol.startsWith("dav:")) {
      protocol = protocol.substring("dav:".length());
    } 
    ProxyDef proxy = candidates.get(protocol);
    if (proxy == null && "https".equals(protocol))
      proxy = candidates.get("http"); 
    return (proxy != null) ? proxy.proxy : null;
  }
  
  static class NonProxyHosts {
    private final Pattern[] patterns;
    
    NonProxyHosts(String nonProxyHosts) {
      List<Pattern> patterns = new ArrayList<>();
      if (nonProxyHosts != null) {
        StringTokenizer tokenizer = new StringTokenizer(nonProxyHosts, "|");
        while (tokenizer.hasMoreTokens()) {
          String pattern = tokenizer.nextToken();
          pattern = pattern.replace(".", "\\.").replace("*", ".*");
          patterns.add(Pattern.compile(pattern, 2));
        } 
      } 
      this.patterns = patterns.<Pattern>toArray(new Pattern[patterns.size()]);
    }
    
    boolean isNonProxyHost(String host) {
      if (host != null)
        for (Pattern pattern : this.patterns) {
          if (pattern.matcher(host).matches())
            return true; 
        }  
      return false;
    }
  }
  
  static class ProxyDef {
    final Proxy proxy;
    
    final DefaultProxySelector.NonProxyHosts nonProxyHosts;
    
    ProxyDef(Proxy proxy, String nonProxyHosts) {
      this.proxy = proxy;
      this.nonProxyHosts = new DefaultProxySelector.NonProxyHosts(nonProxyHosts);
    }
  }
}
