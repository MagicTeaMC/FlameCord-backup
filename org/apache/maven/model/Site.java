package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Site implements Serializable, Cloneable, InputLocationTracker {
  private String id;
  
  private String name;
  
  private String url;
  
  private String childSiteUrlInheritAppendPath;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation idLocation;
  
  private InputLocation nameLocation;
  
  private InputLocation urlLocation;
  
  private InputLocation childSiteUrlInheritAppendPathLocation;
  
  public Site clone() {
    try {
      Site copy = (Site)super.clone();
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public String getChildSiteUrlInheritAppendPath() {
    return this.childSiteUrlInheritAppendPath;
  }
  
  public String getId() {
    return this.id;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "id":
          return this.idLocation;
        case "name":
          return this.nameLocation;
        case "url":
          return this.urlLocation;
        case "childSiteUrlInheritAppendPath":
          return this.childSiteUrlInheritAppendPathLocation;
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
        case "id":
          this.idLocation = location;
          return;
        case "name":
          this.nameLocation = location;
          return;
        case "url":
          this.urlLocation = location;
          return;
        case "childSiteUrlInheritAppendPath":
          this.childSiteUrlInheritAppendPathLocation = location;
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
  
  public void setChildSiteUrlInheritAppendPath(String childSiteUrlInheritAppendPath) {
    this.childSiteUrlInheritAppendPath = childSiteUrlInheritAppendPath;
  }
  
  public void setId(String id) {
    this.id = id;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
  
  public boolean isChildSiteUrlInheritAppendPath() {
    return (this.childSiteUrlInheritAppendPath != null) ? Boolean.parseBoolean(this.childSiteUrlInheritAppendPath) : true;
  }
  
  public void setChildSiteUrlInheritAppendPath(boolean childSiteUrlInheritAppendPath) {
    this.childSiteUrlInheritAppendPath = String.valueOf(childSiteUrlInheritAppendPath);
  }
}
