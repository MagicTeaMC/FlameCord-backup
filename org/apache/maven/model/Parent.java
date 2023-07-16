package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Parent implements Serializable, Cloneable, InputLocationTracker {
  private String groupId;
  
  private String artifactId;
  
  private String version;
  
  private String relativePath = "../pom.xml";
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation groupIdLocation;
  
  private InputLocation artifactIdLocation;
  
  private InputLocation versionLocation;
  
  private InputLocation relativePathLocation;
  
  public Parent clone() {
    try {
      Parent copy = (Parent)super.clone();
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public String getArtifactId() {
    return this.artifactId;
  }
  
  public String getGroupId() {
    return this.groupId;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "groupId":
          return this.groupIdLocation;
        case "artifactId":
          return this.artifactIdLocation;
        case "version":
          return this.versionLocation;
        case "relativePath":
          return this.relativePathLocation;
      } 
      return getOtherLocation(key);
    } 
    return getOtherLocation(key);
  }
  
  public void setLocation(Object key, InputLocation location) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          this.location = location;
          return;
        case "groupId":
          this.groupIdLocation = location;
          return;
        case "artifactId":
          this.artifactIdLocation = location;
          return;
        case "version":
          this.versionLocation = location;
          return;
        case "relativePath":
          this.relativePathLocation = location;
          return;
      } 
      setOtherLocation(key, location);
      return;
    } 
    setOtherLocation(key, location);
  }
  
  public void setOtherLocation(Object key, InputLocation location) {
    if (location != null) {
      if (this.locations == null)
        this.locations = new LinkedHashMap<>(); 
      this.locations.put(key, location);
    } 
  }
  
  private InputLocation getOtherLocation(Object key) {
    return (this.locations != null) ? this.locations.get(key) : null;
  }
  
  public String getRelativePath() {
    return this.relativePath;
  }
  
  public String getVersion() {
    return this.version;
  }
  
  public void setArtifactId(String artifactId) {
    this.artifactId = artifactId;
  }
  
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
  
  public void setRelativePath(String relativePath) {
    this.relativePath = relativePath;
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  public String getId() {
    StringBuilder id = new StringBuilder(64);
    id.append(getGroupId());
    id.append(":");
    id.append(getArtifactId());
    id.append(":");
    id.append("pom");
    id.append(":");
    id.append(getVersion());
    return id.toString();
  }
  
  public String toString() {
    return getId();
  }
}
