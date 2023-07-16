package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CiManagement implements Serializable, Cloneable, InputLocationTracker {
  private String system;
  
  private String url;
  
  private List<Notifier> notifiers;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation systemLocation;
  
  private InputLocation urlLocation;
  
  private InputLocation notifiersLocation;
  
  public void addNotifier(Notifier notifier) {
    getNotifiers().add(notifier);
  }
  
  public CiManagement clone() {
    try {
      CiManagement copy = (CiManagement)super.clone();
      if (this.notifiers != null) {
        copy.notifiers = new ArrayList<>();
        for (Notifier item : this.notifiers)
          copy.notifiers.add(item.clone()); 
      } 
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "system":
          return this.systemLocation;
        case "url":
          return this.urlLocation;
        case "notifiers":
          return this.notifiersLocation;
      } 
      return getOtherLocation(key);
    } 
    return getOtherLocation(key);
  }
  
  public List<Notifier> getNotifiers() {
    if (this.notifiers == null)
      this.notifiers = new ArrayList<>(); 
    return this.notifiers;
  }
  
  public void setLocation(Object key, InputLocation location) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          this.location = location;
          return;
        case "system":
          this.systemLocation = location;
          return;
        case "url":
          this.urlLocation = location;
          return;
        case "notifiers":
          this.notifiersLocation = location;
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
  
  public String getSystem() {
    return this.system;
  }
  
  public String getUrl() {
    return this.url;
  }
  
  public void removeNotifier(Notifier notifier) {
    getNotifiers().remove(notifier);
  }
  
  public void setNotifiers(List<Notifier> notifiers) {
    this.notifiers = notifiers;
  }
  
  public void setSystem(String system) {
    this.system = system;
  }
  
  public void setUrl(String url) {
    this.url = url;
  }
}
