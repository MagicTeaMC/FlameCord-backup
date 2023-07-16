package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActivationProperty implements Serializable, Cloneable, InputLocationTracker {
  private String name;
  
  private String value;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation nameLocation;
  
  private InputLocation valueLocation;
  
  public ActivationProperty clone() {
    try {
      ActivationProperty copy = (ActivationProperty)super.clone();
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
        case "name":
          return this.nameLocation;
        case "value":
          return this.valueLocation;
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
        case "value":
          this.valueLocation = location;
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
  
  public String getValue() {
    return this.value;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setValue(String value) {
    this.value = value;
  }
}
