package org.eclipse.aether.repository;

import java.io.File;
import java.util.Objects;

public final class LocalRepository implements ArtifactRepository {
  private final File basedir;
  
  private final String type;
  
  public LocalRepository(String basedir) {
    this((basedir != null) ? new File(basedir) : null, "");
  }
  
  public LocalRepository(File basedir) {
    this(basedir, "");
  }
  
  public LocalRepository(File basedir, String type) {
    this.basedir = basedir;
    this.type = (type != null) ? type : "";
  }
  
  public String getContentType() {
    return this.type;
  }
  
  public String getId() {
    return "local";
  }
  
  public File getBasedir() {
    return this.basedir;
  }
  
  public String toString() {
    return getBasedir() + " (" + getContentType() + ")";
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    LocalRepository that = (LocalRepository)obj;
    return (Objects.equals(this.basedir, that.basedir) && Objects.equals(this.type, that.type));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + hash(this.basedir);
    hash = hash * 31 + hash(this.type);
    return hash;
  }
  
  private static int hash(Object obj) {
    return (obj != null) ? obj.hashCode() : 0;
  }
}
