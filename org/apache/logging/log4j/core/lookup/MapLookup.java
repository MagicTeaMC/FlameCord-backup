package org.apache.logging.log4j.core.lookup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.message.MapMessage;
import org.apache.logging.log4j.util.Strings;

@Plugin(name = "map", category = "Lookup")
public class MapLookup implements StrLookup {
  private final Map<String, String> map;
  
  public MapLookup() {
    this.map = null;
  }
  
  public MapLookup(Map<String, String> map) {
    this.map = map;
  }
  
  static Map<String, String> initMap(String[] srcArgs, Map<String, String> destMap) {
    for (int i = 0; i < srcArgs.length; i++) {
      int next = i + 1;
      String value = srcArgs[i];
      destMap.put(Integer.toString(i), value);
      destMap.put(value, (next < srcArgs.length) ? srcArgs[next] : null);
    } 
    return destMap;
  }
  
  static HashMap<String, String> newMap(int initialCapacity) {
    return new HashMap<>(initialCapacity);
  }
  
  @Deprecated
  public static void setMainArguments(String... args) {
    MainMapLookup.setMainArguments(args);
  }
  
  static Map<String, String> toMap(List<String> args) {
    if (args == null)
      return null; 
    int size = args.size();
    return initMap(args.<String>toArray(Strings.EMPTY_ARRAY), newMap(size));
  }
  
  static Map<String, String> toMap(String[] args) {
    if (args == null)
      return null; 
    return initMap(args, newMap(args.length));
  }
  
  protected Map<String, String> getMap() {
    return this.map;
  }
  
  public String lookup(LogEvent event, String key) {
    boolean isMapMessage = (event != null && event.getMessage() instanceof MapMessage);
    if (isMapMessage) {
      String obj = ((MapMessage)event.getMessage()).get(key);
      if (obj != null)
        return obj; 
    } 
    if (this.map != null)
      return this.map.get(key); 
    return null;
  }
  
  public String lookup(String key) {
    if (key == null || this.map == null)
      return null; 
    return this.map.get(key);
  }
}
