package org.apache.maven.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public class Activation implements Serializable, Cloneable, InputLocationTracker {
  private boolean activeByDefault = false;
  
  private String jdk;
  
  private ActivationOS os;
  
  private ActivationProperty property;
  
  private ActivationFile file;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation activeByDefaultLocation;
  
  private InputLocation jdkLocation;
  
  private InputLocation osLocation;
  
  private InputLocation propertyLocation;
  
  private InputLocation fileLocation;
  
  public Activation clone() {
    try {
      Activation copy = (Activation)super.clone();
      if (this.os != null)
        copy.os = this.os.clone(); 
      if (this.property != null)
        copy.property = this.property.clone(); 
      if (this.file != null)
        copy.file = this.file.clone(); 
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public ActivationFile getFile() {
    return this.file;
  }
  
  public String getJdk() {
    return this.jdk;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "activeByDefault":
          return this.activeByDefaultLocation;
        case "jdk":
          return this.jdkLocation;
        case "os":
          return this.osLocation;
        case "property":
          return this.propertyLocation;
        case "file":
          return this.fileLocation;
      } 
      return getOtherLocation(key);
    } 
    return getOtherLocation(key);
  }
  
  public ActivationOS getOs() {
    return this.os;
  }
  
  public void setLocation(Object key, InputLocation location) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          this.location = location;
          return;
        case "activeByDefault":
          this.activeByDefaultLocation = location;
          return;
        case "jdk":
          this.jdkLocation = location;
          return;
        case "os":
          this.osLocation = location;
          return;
        case "property":
          this.propertyLocation = location;
          return;
        case "file":
          this.fileLocation = location;
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
  
  public ActivationProperty getProperty() {
    return this.property;
  }
  
  public boolean isActiveByDefault() {
    return this.activeByDefault;
  }
  
  public void setActiveByDefault(boolean activeByDefault) {
    this.activeByDefault = activeByDefault;
  }
  
  public void setFile(ActivationFile file) {
    this.file = file;
  }
  
  public void setJdk(String jdk) {
    this.jdk = jdk;
  }
  
  public void setOs(ActivationOS os) {
    this.os = os;
  }
  
  public void setProperty(ActivationProperty property) {
    this.property = property;
  }
}
