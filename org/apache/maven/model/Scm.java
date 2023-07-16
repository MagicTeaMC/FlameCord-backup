package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Scm implements Serializable, Cloneable, InputLocationTracker {
  private String connection;
  
  private String developerConnection;
  
  private String tag = "HEAD";
  
  private String url;
  
  private String childScmConnectionInheritAppendPath;
  
  private String childScmDeveloperConnectionInheritAppendPath;
  
  private String childScmUrlInheritAppendPath;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation connectionLocation;
  
  private InputLocation developerConnectionLocation;
  
  private InputLocation tagLocation;
  
  private InputLocation urlLocation;
  
  private InputLocation childScmConnectionInheritAppendPathLocation;
  
  private InputLocation childScmDeveloperConnectionInheritAppendPathLocation;
  
  private InputLocation childScmUrlInheritAppendPathLocation;
  
  public Scm clone() {
    try {
      Scm copy = (Scm)super.clone();
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public String getChildScmConnectionInheritAppendPath() {
    return this.childScmConnectionInheritAppendPath;
  }
  
  public String getChildScmDeveloperConnectionInheritAppendPath() {
    return this.childScmDeveloperConnectionInheritAppendPath;
  }
  
  public String getChildScmUrlInheritAppendPath() {
    return this.childScmUrlInheritAppendPath;
  }
  
  public String getConnection() {
    return this.connection;
  }
  
  public String getDeveloperConnection() {
    return this.developerConnection;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "connection":
          return this.connectionLocation;
        case "developerConnection":
          return this.developerConnectionLocation;
        case "tag":
          return this.tagLocation;
        case "url":
          return this.urlLocation;
        case "childScmConnectionInheritAppendPath":
          return this.childScmConnectionInheritAppendPathLocation;
        case "childScmDeveloperConnectionInheritAppendPath":
          return this.childScmDeveloperConnectionInheritAppendPathLocation;
        case "childScmUrlInheritAppendPath":
          return this.childScmUrlInheritAppendPathLocation;
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
        case "connection":
          this.connectionLocation = location;
          return;
        case "developerConnection":
          this.developerConnectionLocation = location;
          return;
        case "tag":
          this.tagLocation = location;
          return;
        case "url":
          this.urlLocation = location;
          return;
        case "childScmConnectionInheritAppendPath":
          this.childScmConnectionInheritAppendPathLocation = location;
          return;
        case "childScmDeveloperConnectionInheritAppendPath":
          this.childScmDeveloperConnectionInheritAppendPathLocation = location;
          return;
        case "childScmUrlInheritAppendPath":
          this.childScmUrlInheritAppendPathLocation = location;
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
  
  public String getTag() {
    return this.tag;
  }
  
  public String getUrl() {
    return this.url;
  }
  
  public void setChildScmConnectionInheritAppendPath(String childScmConnectionInheritAppendPath) {
    this.childScmConnectionInheritAppendPath = childScmConnectionInheritAppendPath;
  }
  
  public void setChildScmDeveloperConnectionInheritAppendPath(String childScmDeveloperConnectionInheritAppendPath) {
    this.childScmDeveloperConnectionInheritAppendPath = childScmDeveloperConnectionInheritAppendPath;
  }
  
  public void setChildScmUrlInheritAppendPath(String childScmUrlInheritAppendPath) {
    this.childScmUrlInheritAppendPath = childScmUrlInheritAppendPath;
  }
  
  public void setConnection(String connection) {
    this.connection = connection;
  }
  
  public void setDeveloperConnection(String developerConnection) {
    this.developerConnection = developerConnection;
  }
  
  public void setTag(String tag) {
    this.tag = tag;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public boolean isChildScmConnectionInheritAppendPath() {
    return (this.childScmConnectionInheritAppendPath != null) ? Boolean.parseBoolean(this.childScmConnectionInheritAppendPath) : true;
  }
  
  public void setChildScmConnectionInheritAppendPath(boolean childScmConnectionInheritAppendPath) {
    this.childScmConnectionInheritAppendPath = String.valueOf(childScmConnectionInheritAppendPath);
  }
  
  public boolean isChildScmDeveloperConnectionInheritAppendPath() {
    return (this.childScmDeveloperConnectionInheritAppendPath != null) ? Boolean.parseBoolean(this.childScmDeveloperConnectionInheritAppendPath) : true;
  }
  
  public void setChildScmDeveloperConnectionInheritAppendPath(boolean childScmDeveloperConnectionInheritAppendPath) {
    this.childScmDeveloperConnectionInheritAppendPath = String.valueOf(childScmDeveloperConnectionInheritAppendPath);
  }
  
  public boolean isChildScmUrlInheritAppendPath() {
    return (this.childScmUrlInheritAppendPath != null) ? Boolean.parseBoolean(this.childScmUrlInheritAppendPath) : true;
  }
  
  public void setChildScmUrlInheritAppendPath(boolean childScmUrlInheritAppendPath) {
    this.childScmUrlInheritAppendPath = String.valueOf(childScmUrlInheritAppendPath);
  }
}
