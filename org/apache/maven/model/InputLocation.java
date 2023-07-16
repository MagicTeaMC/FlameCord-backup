package org.apache.maven.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class InputLocation implements Serializable, Cloneable, InputLocationTracker {
  private int lineNumber = -1;
  
  private int columnNumber = -1;
  
  private InputSource source;
  
  private Map<Object, InputLocation> locations;
  
  private InputLocation location;
  
  public InputLocation(int lineNumber, int columnNumber) {
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
  }
  
  public InputLocation(int lineNumber, int columnNumber, InputSource source) {
    this.lineNumber = lineNumber;
    this.columnNumber = columnNumber;
    this.source = source;
  }
  
  public InputLocation clone() {
    try {
      InputLocation copy = (InputLocation)super.clone();
      if (copy.locations != null)
        copy.locations = new LinkedHashMap<>(copy.locations); 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public int getColumnNumber() {
    return this.columnNumber;
  }
  
  public int getLineNumber() {
    return this.lineNumber;
  }
  
  public InputLocation getLocation(Object key) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          return this.location;
      } 
      return getOtherLocation(key);
    } 
    return getOtherLocation(key);
  }
  
  public Map<Object, InputLocation> getLocations() {
    return this.locations;
  }
  
  public void setLocation(Object key, InputLocation location) {
    if (key instanceof String) {
      switch ((String)key) {
        case "":
          this.location = location;
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
  
  public InputSource getSource() {
    return this.source;
  }
  
  public static InputLocation merge(InputLocation target, InputLocation source, boolean sourceDominant) {
    Map<Object, InputLocation> locations;
    if (source == null)
      return target; 
    if (target == null)
      return source; 
    InputLocation result = new InputLocation(target.getLineNumber(), target.getColumnNumber(), target.getSource());
    Map<Object, InputLocation> sourceLocations = source.getLocations();
    Map<Object, InputLocation> targetLocations = target.getLocations();
    if (sourceLocations == null) {
      locations = targetLocations;
    } else if (targetLocations == null) {
      locations = sourceLocations;
    } else {
      locations = new LinkedHashMap<>();
      locations.putAll(sourceDominant ? targetLocations : sourceLocations);
      locations.putAll(sourceDominant ? sourceLocations : targetLocations);
    } 
    result.setLocations(locations);
    return result;
  }
  
  public static InputLocation merge(InputLocation target, InputLocation source, Collection<Integer> indices) {
    Map<Object, InputLocation> locations;
    if (source == null)
      return target; 
    if (target == null)
      return source; 
    InputLocation result = new InputLocation(target.getLineNumber(), target.getColumnNumber(), target.getSource());
    Map<Object, InputLocation> sourceLocations = source.getLocations();
    Map<Object, InputLocation> targetLocations = target.getLocations();
    if (sourceLocations == null) {
      locations = targetLocations;
    } else if (targetLocations == null) {
      locations = sourceLocations;
    } else {
      locations = new LinkedHashMap<>();
      for (Iterator<Integer> it = indices.iterator(); it.hasNext(); ) {
        InputLocation location;
        Integer index = it.next();
        if (index.intValue() < 0) {
          location = sourceLocations.get(Integer.valueOf(index.intValue() ^ 0xFFFFFFFF));
        } else {
          location = targetLocations.get(index);
        } 
        locations.put(Integer.valueOf(locations.size()), location);
      } 
    } 
    result.setLocations(locations);
    return result;
  }
  
  public void setLocations(Map<Object, InputLocation> locations) {
    this.locations = locations;
  }
  
  public static abstract class StringFormatter {
    public abstract String toString(InputLocation param1InputLocation);
  }
  
  public String toString() {
    return getLineNumber() + " : " + getColumnNumber() + ", " + getSource();
  }
}
