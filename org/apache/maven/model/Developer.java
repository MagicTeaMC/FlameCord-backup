package org.apache.maven.model;

import java.io.Serializable;

public class Developer extends Contributor implements Serializable, Cloneable {
  private String id;
  
  public Developer clone() {
    try {
      Developer copy = (Developer)super.clone();
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public String getId() {
    return this.id;
  }
  
  public void setId(String id) {
    this.id = id;
  }
}
