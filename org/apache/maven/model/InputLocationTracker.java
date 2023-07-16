package org.apache.maven.model;

public interface InputLocationTracker {
  InputLocation getLocation(Object paramObject);
  
  void setLocation(Object paramObject, InputLocation paramInputLocation);
}
