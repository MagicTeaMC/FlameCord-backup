package org.eclipse.sisu.wire;

import com.google.inject.Singleton;
import java.util.AbstractMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.eclipse.sisu.Parameters;

@Singleton
final class StringProperties extends AbstractMap<String, String> {
  private final Map<?, ?> delegate;
  
  @Inject
  StringProperties(@Parameters Map<?, ?> delegate) {
    this.delegate = delegate;
  }
  
  public String get(Object key) {
    Object value = this.delegate.get(key);
    if (value instanceof String)
      return (String)value; 
    return null;
  }
  
  public boolean containsKey(Object key) {
    Object value = this.delegate.get(key);
    if (value == null)
      return this.delegate.containsKey(key); 
    return value instanceof String;
  }
  
  public Set<Map.Entry<String, String>> entrySet() {
    Set<Map.Entry> entries = new HashSet();
    for (Map.Entry<?, ?> e : this.delegate.entrySet()) {
      if (e.getKey() instanceof String) {
        Object value = e.getValue();
        if (value == null || value instanceof String)
          entries.add(e); 
      } 
    } 
    return (Set)entries;
  }
}
