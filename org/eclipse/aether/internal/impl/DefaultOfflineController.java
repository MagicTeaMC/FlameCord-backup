package org.eclipse.aether.internal.impl;

import java.util.regex.Pattern;
import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.OfflineController;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.RepositoryOfflineException;
import org.eclipse.aether.util.ConfigUtils;

@Named
public class DefaultOfflineController implements OfflineController {
  static final String CONFIG_PROP_OFFLINE_PROTOCOLS = "aether.offline.protocols";
  
  static final String CONFIG_PROP_OFFLINE_HOSTS = "aether.offline.hosts";
  
  private static final Pattern SEP = Pattern.compile("\\s*,\\s*");
  
  public void checkOffline(RepositorySystemSession session, RemoteRepository repository) throws RepositoryOfflineException {
    if (isOfflineProtocol(session, repository) || isOfflineHost(session, repository))
      return; 
    throw new RepositoryOfflineException(repository);
  }
  
  private boolean isOfflineProtocol(RepositorySystemSession session, RemoteRepository repository) {
    String[] protocols = getConfig(session, "aether.offline.protocols");
    if (protocols != null) {
      String protocol = repository.getProtocol();
      if (protocol.length() > 0)
        for (String p : protocols) {
          if (p.equalsIgnoreCase(protocol))
            return true; 
        }  
    } 
    return false;
  }
  
  private boolean isOfflineHost(RepositorySystemSession session, RemoteRepository repository) {
    String[] hosts = getConfig(session, "aether.offline.hosts");
    if (hosts != null) {
      String host = repository.getHost();
      if (host.length() > 0)
        for (String h : hosts) {
          if (h.equalsIgnoreCase(host))
            return true; 
        }  
    } 
    return false;
  }
  
  private String[] getConfig(RepositorySystemSession session, String key) {
    String value = ConfigUtils.getString(session, "", new String[] { key }).trim();
    if (value.length() <= 0)
      return null; 
    return SEP.split(value);
  }
}
