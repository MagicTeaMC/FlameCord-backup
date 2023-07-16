package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.codehaus.plexus.util.xml.Xpp3Dom;

public class ConfigurationContainer implements Serializable, Cloneable, InputLocationTracker {
  private String inherited;
  
  private Object configuration;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation inheritedLocation;
  
  private InputLocation configurationLocation;
  
  public ConfigurationContainer clone() {
    try {
      ConfigurationContainer copy = (ConfigurationContainer)super.clone();
      if (this.configuration != null)
        copy.configuration = new Xpp3Dom((Xpp3Dom)this.configuration); 
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public Object getConfiguration() {
    return this.configuration;
  }
  
  public String getInherited() {
    return this.inherited;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "inherited":
          return this.inheritedLocation;
        case "configuration":
          return this.configurationLocation;
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
        case "inherited":
          this.inheritedLocation = location;
          return;
        case "configuration":
          this.configurationLocation = location;
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
  
  public void setConfiguration(Object configuration) {
    this.configuration = configuration;
  }
  
  public void setInherited(String inherited) {
    this.inherited = inherited;
  }
  
  public boolean isInherited() {
    return (this.inherited != null) ? Boolean.parseBoolean(this.inherited) : true;
  }
  
  public void setInherited(boolean inherited) {
    this.inherited = String.valueOf(inherited);
  }
  
  private boolean inheritanceApplied = true;
  
  public void unsetInheritanceApplied() {
    this.inheritanceApplied = false;
  }
  
  public boolean isInheritanceApplied() {
    return this.inheritanceApplied;
  }
}
