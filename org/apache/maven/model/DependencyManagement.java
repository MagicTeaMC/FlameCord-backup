package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DependencyManagement implements Serializable, Cloneable, InputLocationTracker {
  private List<Dependency> dependencies;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  private InputLocation dependenciesLocation;
  
  public void addDependency(Dependency dependency) {
    getDependencies().add(dependency);
  }
  
  public DependencyManagement clone() {
    try {
      DependencyManagement copy = (DependencyManagement)super.clone();
      if (this.dependencies != null) {
        copy.dependencies = new ArrayList<>();
        for (Dependency item : this.dependencies)
          copy.dependencies.add(item.clone()); 
      } 
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public List<Dependency> getDependencies() {
    if (this.dependencies == null)
      this.dependencies = new ArrayList<>(); 
    return this.dependencies;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
        case "dependencies":
          return this.dependenciesLocation;
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
        case "dependencies":
          this.dependenciesLocation = location;
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
  
  public void removeDependency(Dependency dependency) {
    getDependencies().remove(dependency);
  }
  
  public void setDependencies(List<Dependency> dependencies) {
    this.dependencies = dependencies;
  }
}
