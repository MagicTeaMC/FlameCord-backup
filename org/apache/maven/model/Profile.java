package org.apache.maven.model;

import java.io.Serializable;

public class Profile extends ModelBase implements Serializable, Cloneable {
  private String id = "default";
  
  private Activation activation;
  
  private BuildBase build;
  
  public static final String SOURCE_POM = "pom";
  
  public static final String SOURCE_SETTINGS = "settings.xml";
  
  public Profile clone() {
    try {
      Profile copy = (Profile)super.clone();
      if (this.activation != null)
        copy.activation = this.activation.clone(); 
      if (this.build != null)
        copy.build = this.build.clone(); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public Activation getActivation() {
    return this.activation;
  }
  
  public BuildBase getBuild() {
    return this.build;
  }
  
  public String getId() {
    return this.id;
  }
  
  public void setActivation(Activation activation) {
    this.activation = activation;
  }
  
  public void setBuild(BuildBase build) {
    this.build = build;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  private String source = "pom";
  
  public void setSource(String source) {
    this.source = source;
  }
  
  public String getSource() {
    return this.source;
  }
  
  public String toString() {
    return "Profile {id: " + getId() + ", source: " + getSource() + "}";
  }
}
