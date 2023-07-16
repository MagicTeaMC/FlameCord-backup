package org.eclipse.sisu.wire;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

final class MergedProperties extends AbstractMap<Object, Object> {
  private volatile transient Set<Map.Entry<Object, Object>> entrySet;
  
  final Map<?, ?>[] properties;
  
  final class MergedEntries extends AbstractSet<Map.Entry<Object, Object>> {
    public Iterator<Map.Entry<Object, Object>> iterator() {
      return new Iterator<Map.Entry<Object, Object>>() {
          private Iterator<? extends Map.Entry> itr;
          
          private int index;
          
          public boolean hasNext() {
            while (this.itr == null || !this.itr.hasNext()) {
              if (this.index >= (MergedProperties.MergedEntries.access$0(MergedProperties.MergedEntries.this)).properties.length)
                return false; 
              this.itr = (MergedProperties.MergedEntries.access$0(MergedProperties.MergedEntries.this)).properties[this.index++].entrySet().iterator();
            } 
            return true;
          }
          
          public Map.Entry<Object, Object> next() {
            if (hasNext())
              return this.itr.next(); 
            throw new NoSuchElementException();
          }
          
          public void remove() {
            throw new UnsupportedOperationException();
          }
        };
    }
    
    public int size() {
      int size = 0;
      byte b;
      int i;
      Map<?, ?>[] arrayOfMap;
      for (i = (arrayOfMap = MergedProperties.this.properties).length, b = 0; b < i; ) {
        Map<?, ?> p = arrayOfMap[b];
        size += p.size();
        b++;
      } 
      return size;
    }
  }
  
  MergedProperties(List<Map<?, ?>> properties) {
    this.properties = properties.<Map<?, ?>>toArray((Map<?, ?>[])new Map[properties.size()]);
  }
  
  public Object get(Object key) {
    byte b;
    int i;
    Map<?, ?>[] arrayOfMap;
    for (i = (arrayOfMap = this.properties).length, b = 0; b < i; ) {
      Map<?, ?> p = arrayOfMap[b];
      Object value = p.get(key);
      if (value != null)
        return value; 
      b++;
    } 
    return null;
  }
  
  public boolean containsKey(Object key) {
    byte b;
    int i;
    Map<?, ?>[] arrayOfMap;
    for (i = (arrayOfMap = this.properties).length, b = 0; b < i; ) {
      Map<?, ?> p = arrayOfMap[b];
      if (p.containsKey(key))
        return true; 
      b++;
    } 
    return false;
  }
  
  public Set<Map.Entry<Object, Object>> entrySet() {
    if (this.entrySet == null)
      this.entrySet = new MergedEntries(); 
    return this.entrySet;
  }
}
