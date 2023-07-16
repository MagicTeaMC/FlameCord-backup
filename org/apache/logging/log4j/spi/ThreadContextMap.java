package org.apache.logging.log4j.spi;

import java.util.Map;

public interface ThreadContextMap {
  void clear();
  
  boolean containsKey(String paramString);
  
  String get(String paramString);
  
  Map<String, String> getCopy();
  
  Map<String, String> getImmutableMapOrNull();
  
  boolean isEmpty();
  
  void put(String paramString1, String paramString2);
  
  void remove(String paramString);
}
