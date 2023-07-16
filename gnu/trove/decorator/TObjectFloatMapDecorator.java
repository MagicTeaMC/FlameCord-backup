package gnu.trove.decorator;

import gnu.trove.iterator.TObjectFloatIterator;
import gnu.trove.map.TObjectFloatMap;
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

public class TObjectFloatMapDecorator<K> extends AbstractMap<K, Float> implements Map<K, Float>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TObjectFloatMap<K> _map;
  
  public TObjectFloatMapDecorator() {}
  
  public TObjectFloatMapDecorator(TObjectFloatMap<K> map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TObjectFloatMap<K> getMap() {
    return this._map;
  }
  
  public Float put(K key, Float value) {
    if (value == null)
      return wrapValue(this._map.put(key, this._map.getNoEntryValue())); 
    return wrapValue(this._map.put(key, unwrapValue(value)));
  }
  
  public Float get(Object key) {
    float v = this._map.get(key);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Float remove(Object key) {
    float v = this._map.remove(key);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<K, Float>> entrySet() {
    return new AbstractSet<Map.Entry<K, Float>>() {
        public int size() {
          return TObjectFloatMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TObjectFloatMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TObjectFloatMapDecorator.this.containsKey(k) && TObjectFloatMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<K, Float>> iterator() {
          return new Iterator<Map.Entry<K, Float>>() {
              private final TObjectFloatIterator<K> it = TObjectFloatMapDecorator.this._map.iterator();
              
              public Map.Entry<K, Float> next() {
                this.it.advance();
                final K key = (K)this.it.key();
                final Float v = TObjectFloatMapDecorator.this.wrapValue(this.it.value());
                return new Map.Entry<K, Float>() {
                    private Float val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public K getKey() {
                      return (K)key;
                    }
                    
                    public Float getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Float setValue(Float value) {
                      this.val = value;
                      return TObjectFloatMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<K, Float> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            K key = (K)((Map.Entry)o).getKey();
            TObjectFloatMapDecorator.this._map.remove(key);
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<K, Float>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TObjectFloatMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Float && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    return this._map.containsKey(key);
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (this._map.size() == 0);
  }
  
  public void putAll(Map<? extends K, ? extends Float> map) {
    Iterator<? extends Map.Entry<? extends K, ? extends Float>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends K, ? extends Float> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Float wrapValue(float k) {
    return Float.valueOf(k);
  }
  
  protected float unwrapValue(Object value) {
    return ((Float)value).floatValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TObjectFloatMap<K>)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
