package gnu.trove.decorator;

import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class TShortObjectMapDecorator<V> extends AbstractMap<Short, V> implements Map<Short, V>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TShortObjectMap<V> _map;
  
  public TShortObjectMapDecorator() {}
  
  public TShortObjectMapDecorator(TShortObjectMap<V> map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TShortObjectMap<V> getMap() {
    return this._map;
  }
  
  public V put(Short key, V value) {
    short k;
    if (key == null) {
      k = this._map.getNoEntryKey();
    } else {
      k = unwrapKey(key);
    } 
    return (V)this._map.put(k, value);
  }
  
  public V get(Object key) {
    short k;
    if (key != null) {
      if (key instanceof Short) {
        k = unwrapKey((Short)key);
      } else {
        return null;
      } 
    } else {
      k = this._map.getNoEntryKey();
    } 
    return (V)this._map.get(k);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public V remove(Object key) {
    short k;
    if (key != null) {
      if (key instanceof Short) {
        k = unwrapKey((Short)key);
      } else {
        return null;
      } 
    } else {
      k = this._map.getNoEntryKey();
    } 
    return (V)this._map.remove(k);
  }
  
  public Set<Map.Entry<Short, V>> entrySet() {
    return new AbstractSet<Map.Entry<Short, V>>() {
        public int size() {
          return TShortObjectMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TShortObjectMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TShortObjectMapDecorator.this.containsKey(k) && TShortObjectMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Short, V>> iterator() {
          return new Iterator<Map.Entry<Short, V>>() {
              private final TShortObjectIterator<V> it = TShortObjectMapDecorator.this._map.iterator();
              
              public Map.Entry<Short, V> next() {
                this.it.advance();
                short k = this.it.key();
                final Short key = (k == TShortObjectMapDecorator.this._map.getNoEntryKey()) ? null : TShortObjectMapDecorator.this.wrapKey(k);
                final V v = (V)this.it.value();
                return new Map.Entry<Short, V>() {
                    private V val = (V)v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Short getKey() {
                      return key;
                    }
                    
                    public V getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public V setValue(V value) {
                      this.val = value;
                      return TShortObjectMapDecorator.this.put(key, value);
                    }
                  };
              }
              
              public boolean hasNext() {
                return this.it.hasNext();
              }
              
              public void remove() {
                this.it.remove();
              }
            };
        }
        
        public boolean add(Map.Entry<Short, V> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Short key = (Short)((Map.Entry)o).getKey();
            TShortObjectMapDecorator.this._map.remove(TShortObjectMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Short, V>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TShortObjectMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return this._map.containsValue(val);
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Short && this._map.containsKey(((Short)key).shortValue()));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Short, ? extends V> map) {
    Iterator<? extends Map.Entry<? extends Short, ? extends V>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Short, ? extends V> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Short wrapKey(short k) {
    return Short.valueOf(k);
  }
  
  protected short unwrapKey(Short key) {
    return key.shortValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TShortObjectMap<V>)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
