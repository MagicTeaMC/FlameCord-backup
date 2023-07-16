package org.apache.maven.model;

import java.io.Serializable;

public class DeploymentRepository extends Repository implements Serializable, Cloneable {
  private boolean uniqueVersion = true;
  
  public DeploymentRepository clone() {
    try {
      DeploymentRepository copy = (DeploymentRepository)super.clone();
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public boolean isUniqueVersion() {
    return this.uniqueVersion;
  }
  
  public void setUniqueVersion(boolean uniqueVersion) {
    this.uniqueVersion = uniqueVersion;
  }
}
