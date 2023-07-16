package org.apache.logging.log4j.spi;

import java.util.HashMap;
import java.util.Map;

public class NoOpThreadContextMap implements ThreadContextMap {
  public void clear() {}
  
  public boolean containsKey(String key) {
    return false;
  }
  
  public String get(String key) {
    return null;
  }
  
  public Map<String, String> getCopy() {
    return new HashMap<>();
  }
  
  public Map<String, String> getImmutableMapOrNull() {
    return null;
  }
  
  public boolean isEmpty() {
    return true;
  }
  
  public void put(String key, String value) {}
  
  public void remove(String key) {}
}
