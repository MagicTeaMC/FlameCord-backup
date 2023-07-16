package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class ActivationFile implements Serializable, Cloneable, InputLocationTracker {
  private String missing;
  
  private String exists;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation missingLocation;
  
  private InputLocation existsLocation;
  
  public ActivationFile clone() {
    try {
      ActivationFile copy = (ActivationFile)super.clone();
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public String getExists() {
    return this.exists;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "missing":
          return this.missingLocation;
        case "exists":
          return this.existsLocation;
      } 
      return getOtherLocation(key);
    } 
    return getOtherLocation(key);
  }
  
  public String getMissing() {
    return this.missing;
  }
  
  public void setLocation(Object key, InputLocation location) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          this.location = location;
          return;
        case "missing":
          this.missingLocation = location;
          return;
        case "exists":
          this.existsLocation = location;
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
  
  public void setExists(String exists) {
    this.exists = exists;
  }
  
  public void setMissing(String missing) {
    this.missing = missing;
  }
}
