package org.apache.logging.log4j;

import java.io.Serializable;

public interface Marker extends Serializable {
  Marker addParents(Marker... paramVarArgs);
  
  boolean equals(Object paramObject);
  
  String getName();
  
  Marker[] getParents();
  
  int hashCode();
  
  boolean hasParents();
  
  boolean isInstanceOf(Marker paramMarker);
  
  boolean isInstanceOf(String paramString);
  
  boolean remove(Marker paramMarker);
  
  Marker setParents(Marker... paramVarArgs);
}
