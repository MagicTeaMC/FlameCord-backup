package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class License implements Serializable, Cloneable, InputLocationTracker {
  private String name;
  
  private String url;
  
  private String distribution;
  
  private String comments;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation nameLocation;
  
  private InputLocation urlLocation;
  
  private InputLocation distributionLocation;
  
  private InputLocation commentsLocation;
  
  public License clone() {
    try {
      License copy = (License)super.clone();
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public String getComments() {
    return this.comments;
  }
  
  public String getDistribution() {
    return this.distribution;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "name":
          return this.nameLocation;
        case "url":
          return this.urlLocation;
        case "distribution":
          return this.distributionLocation;
        case "comments":
          return this.commentsLocation;
      } 
      return getOtherLocation(key);
    } 
    return getOtherLocation(key);
  }
  
  public String getName() {
    return this.name;
  }
  
  public void setLocation(Object key, InputLocation location) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          this.location = location;
          return;
        case "name":
          this.nameLocation = location;
          return;
        case "url":
          this.urlLocation = location;
          return;
        case "distribution":
          this.distributionLocation = location;
          return;
        case "comments":
          this.commentsLocation = location;
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
  
  public String getUrl() {
    return this.url;
  }
  
  public void setComments(String comments) {
    this.comments = comments;
  }
  
  public void setDistribution(String distribution) {
    this.distribution = distribution;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
}