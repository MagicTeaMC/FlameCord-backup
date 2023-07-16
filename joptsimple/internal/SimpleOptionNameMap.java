package joptsimple.internal;

import java.util.HashMap;
import java.util.Map;

public class SimpleOptionNameMap<V> implements OptionNameMap<V> {
  private final Map<String, V> map = new HashMap<>();
  
  public boolean contains(String key) {
    return this.map.containsKey(key);
  }
  
  public V get(String key) {
    return this.map.get(key);
  }
  
  public void put(String key, V newValue) {
    this.map.put(key, newValue);
  }
  
  public void putAll(Iterable<String> keys, V newValue) {
    for (String each : keys)
      this.map.put(each, newValue); 
  }
  
  public void remove(String key) {
    this.map.remove(key);
  }
  
  public Map<String, V> toJavaUtilMap() {
    return new HashMap<>(this.map);
  }
}
