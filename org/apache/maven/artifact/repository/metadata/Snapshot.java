package org.apache.maven.artifact.repository.metadata;

import java.io.Serializable;

public class Snapshot implements Serializable, Cloneable {
  private String timestamp;
  
  private int buildNumber = 0;
  
  private boolean localCopy = false;
  
  public Snapshot clone() {
    try {
      Snapshot copy = (Snapshot)super.clone();
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public int getBuildNumber() {
    return this.buildNumber;
  }
  
  public String getTimestamp() {
    return this.timestamp;
  }
  
  public boolean isLocalCopy() {
    return this.localCopy;
  }
  
  public void setBuildNumber(int buildNumber) {
    this.buildNumber = buildNumber;
  }
  
  public void setLocalCopy(boolean localCopy) {
    this.localCopy = localCopy;
  }
  
  public void setTimestamp(String timestamp) {
    this.timestamp = timestamp;
  }
}
