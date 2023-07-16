package gnu.trove.decorator;

import gnu.trove.iterator.TFloatLongIterator;
import gnu.trove.map.TFloatLongMap;
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

public class TFloatLongMapDecorator extends AbstractMap<Float, Long> implements Map<Float, Long>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TFloatLongMap _map;
  
  public TFloatLongMapDecorator() {}
  
  public TFloatLongMapDecorator(TFloatLongMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TFloatLongMap getMap() {
    return this._map;
  }
  
  public Long put(Float key, Long value) {
    float k;
    long v;
    if (key == null) {
      k = this._map.getNoEntryKey();
    } else {
      k = unwrapKey(key);
    } 
    if (value == null) {
      v = this._map.getNoEntryValue();
    } else {
      v = unwrapValue(value);
    } 
    long retval = this._map.put(k, v);
    if (retval == this._map.getNoEntryValue())
      return null; 
    return wrapValue(retval);
  }
  
  public Long get(Object key) {
    float k;
    if (key != null) {
      if (key instanceof Float) {
        k = unwrapKey(key);
      } else {
        return null;
      } 
    } else {
      k = this._map.getNoEntryKey();
    } 
    long v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Long remove(Object key) {
    float k;
    if (key != null) {
      if (key instanceof Float) {
        k = unwrapKey(key);
      } else {
        return null;
      } 
    } else {
      k = this._map.getNoEntryKey();
    } 
    long v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Float, Long>> entrySet() {
    return new AbstractSet<Map.Entry<Float, Long>>() {
        public int size() {
          return TFloatLongMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TFloatLongMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TFloatLongMapDecorator.this.containsKey(k) && TFloatLongMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Float, Long>> iterator() {
          return new Iterator<Map.Entry<Float, Long>>() {
              private final TFloatLongIterator it = TFloatLongMapDecorator.this._map.iterator();
              
              public Map.Entry<Float, Long> next() {
                this.it.advance();
                float ik = this.it.key();
                final Float key = (ik == TFloatLongMapDecorator.this._map.getNoEntryKey()) ? null : TFloatLongMapDecorator.this.wrapKey(ik);
                long iv = this.it.value();
                final Long v = (iv == TFloatLongMapDecorator.this._map.getNoEntryValue()) ? null : TFloatLongMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Float, Long>() {
                    private Long val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Float getKey() {
                      return key;
                    }
                    
                    public Long getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Long setValue(Long value) {
                      this.val = value;
                      return TFloatLongMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Float, Long> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Float key = (Float)((Map.Entry)o).getKey();
            TFloatLongMapDecorator.this._map.remove(TFloatLongMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Float, Long>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TFloatLongMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Long && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Float && this._map.containsKey(unwrapKey(key)));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Float, ? extends Long> map) {
    Iterator<? extends Map.Entry<? extends Float, ? extends Long>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Float, ? extends Long> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Float wrapKey(float k) {
    return Float.valueOf(k);
  }
  
  protected float unwrapKey(Object key) {
    return ((Float)key).floatValue();
  }
  
  protected Long wrapValue(long k) {
    return Long.valueOf(k);
  }
  
  protected long unwrapValue(Object value) {
    return ((Long)value).longValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TFloatLongMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
