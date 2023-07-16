package org.apache.maven.model;

import java.io.Serializable;

public class Repository extends RepositoryBase implements Serializable, Cloneable {
  private RepositoryPolicy releases;
  
  private RepositoryPolicy snapshots;
  
  public Repository clone() {
    try {
      Repository copy = (Repository)super.clone();
      if (this.releases != null)
        copy.releases = this.releases.clone(); 
      if (this.snapshots != null)
        copy.snapshots = this.snapshots.clone(); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public RepositoryPolicy getReleases() {
    return this.releases;
  }
  
  public RepositoryPolicy getSnapshots() {
    return this.snapshots;
  }
  
  public void setReleases(RepositoryPolicy releases) {
    this.releases = releases;
  }
  
  public void setSnapshots(RepositoryPolicy snapshots) {
    this.snapshots = snapshots;
  }
}
