package org.apache.maven.repository.internal;

import org.apache.maven.model.building.ModelCache;
import org.eclipse.aether.RepositoryCache;
import org.eclipse.aether.RepositorySystemSession;

class DefaultModelCache implements ModelCache {
  private final RepositorySystemSession session;
  
  private final RepositoryCache cache;
  
  public static ModelCache newInstance(RepositorySystemSession session) {
    if (session.getCache() == null)
      return null; 
    return new DefaultModelCache(session);
  }
  
  private DefaultModelCache(RepositorySystemSession session) {
    this.session = session;
    this.cache = session.getCache();
  }
  
  public Object get(String groupId, String artifactId, String version, String tag) {
    return this.cache.get(this.session, new Key(groupId, artifactId, version, tag));
  }
  
  public void put(String groupId, String artifactId, String version, String tag, Object data) {
    this.cache.put(this.session, new Key(groupId, artifactId, version, tag), data);
  }
  
  static class Key {
    private final String groupId;
    
    private final String artifactId;
    
    private final String version;
    
    private final String tag;
    
    private final int hash;
    
    Key(String groupId, String artifactId, String version, String tag) {
      this.groupId = groupId;
      this.artifactId = artifactId;
      this.version = version;
      this.tag = tag;
      int h = 17;
      h = h * 31 + this.groupId.hashCode();
      h = h * 31 + this.artifactId.hashCode();
      h = h * 31 + this.version.hashCode();
      h = h * 31 + this.tag.hashCode();
      this.hash = h;
    }
    
    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (null == obj || !getClass().equals(obj.getClass()))
        return false; 
      Key that = (Key)obj;
      return (this.artifactId.equals(that.artifactId) && this.groupId.equals(that.groupId) && this.version
        .equals(that.version) && this.tag.equals(that.tag));
    }
    
    public int hashCode() {
      return this.hash;
    }
  }
}
