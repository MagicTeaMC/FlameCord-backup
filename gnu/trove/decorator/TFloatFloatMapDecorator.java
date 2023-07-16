package gnu.trove.decorator;

import gnu.trove.iterator.TFloatFloatIterator;
import gnu.trove.map.TFloatFloatMap;
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

public class TFloatFloatMapDecorator extends AbstractMap<Float, Float> implements Map<Float, Float>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TFloatFloatMap _map;
  
  public TFloatFloatMapDecorator() {}
  
  public TFloatFloatMapDecorator(TFloatFloatMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TFloatFloatMap getMap() {
    return this._map;
  }
  
  public Float put(Float key, Float value) {
    float k, v;
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
    float retval = this._map.put(k, v);
    if (retval == this._map.getNoEntryValue())
      return null; 
    return wrapValue(retval);
  }
  
  public Float get(Object key) {
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
    float v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Float remove(Object key) {
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
    float v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Float, Float>> entrySet() {
    return new AbstractSet<Map.Entry<Float, Float>>() {
        public int size() {
          return TFloatFloatMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TFloatFloatMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TFloatFloatMapDecorator.this.containsKey(k) && TFloatFloatMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Float, Float>> iterator() {
          return new Iterator<Map.Entry<Float, Float>>() {
              private final TFloatFloatIterator it = TFloatFloatMapDecorator.this._map.iterator();
              
              public Map.Entry<Float, Float> next() {
                this.it.advance();
                float ik = this.it.key();
                final Float key = (ik == TFloatFloatMapDecorator.this._map.getNoEntryKey()) ? null : TFloatFloatMapDecorator.this.wrapKey(ik);
                float iv = this.it.value();
                final Float v = (iv == TFloatFloatMapDecorator.this._map.getNoEntryValue()) ? null : TFloatFloatMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Float, Float>() {
                    private Float val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Float getKey() {
                      return key;
                    }
                    
                    public Float getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Float setValue(Float value) {
                      this.val = value;
                      return TFloatFloatMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Float, Float> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Float key = (Float)((Map.Entry)o).getKey();
            TFloatFloatMapDecorator.this._map.remove(TFloatFloatMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Float, Float>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TFloatFloatMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Float && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Float, ? extends Float> map) {
    Iterator<? extends Map.Entry<? extends Float, ? extends Float>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Float, ? extends Float> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Float wrapKey(float k) {
    return Float.valueOf(k);
  }
  
  protected float unwrapKey(Object key) {
    return ((Float)key).floatValue();
  }
  
  protected Float wrapValue(float k) {
    return Float.valueOf(k);
  }
  
  protected float unwrapValue(Object value) {
    return ((Float)value).floatValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TFloatFloatMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
