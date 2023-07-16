package org.apache.commons.lang3.text;

import java.util.Map;

@Deprecated
public abstract class StrLookup<V> {
  private static final StrLookup<String> NONE_LOOKUP = new MapStrLookup<>(null);
  
  private static final StrLookup<String> SYSTEM_PROPERTIES_LOOKUP = new SystemPropertiesStrLookup();
  
  public static StrLookup<?> noneLookup() {
    return NONE_LOOKUP;
  }
  
  public static StrLookup<String> systemPropertiesLookup() {
    return SYSTEM_PROPERTIES_LOOKUP;
  }
  
  public static <V> StrLookup<V> mapLookup(Map<String, V> map) {
    return new MapStrLookup<>(map);
  }
  
  public abstract String lookup(String paramString);
  
  static class MapStrLookup<V> extends StrLookup<V> {
    private final Map<String, V> map;
    
    MapStrLookup(Map<String, V> map) {
      this.map = map;
    }
    
    public String lookup(String key) {
      if (this.map == null)
        return null; 
      Object obj = this.map.get(key);
      if (obj == null)
        return null; 
      return obj.toString();
    }
  }
  
  private static class SystemPropertiesStrLookup extends StrLookup<String> {
    private SystemPropertiesStrLookup() {}
    
    public String lookup(String key) {
      if (!key.isEmpty())
        try {
          return System.getProperty(key);
        } catch (SecurityException securityException) {} 
      return null;
    }
  }
}
