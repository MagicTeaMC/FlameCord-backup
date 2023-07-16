package org.eclipse.aether.repository;

import java.util.UUID;

public final class WorkspaceRepository implements ArtifactRepository {
  private final String type;
  
  private final Object key;
  
  public WorkspaceRepository() {
    this("workspace");
  }
  
  public WorkspaceRepository(String type) {
    this(type, null);
  }
  
  public WorkspaceRepository(String type, Object key) {
    this.type = (type != null) ? type : "";
    this.key = (key != null) ? key : UUID.randomUUID().toString().replace("-", "");
  }
  
  public String getContentType() {
    return this.type;
  }
  
  public String getId() {
    return "workspace";
  }
  
  public Object getKey() {
    return this.key;
  }
  
  public String toString() {
    return "(" + getContentType() + ")";
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    WorkspaceRepository that = (WorkspaceRepository)obj;
    return (getContentType().equals(that.getContentType()) && getKey().equals(that.getKey()));
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + getKey().hashCode();
    hash = hash * 31 + getContentType().hashCode();
    return hash;
  }
}
