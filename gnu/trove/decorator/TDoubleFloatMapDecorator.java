package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleFloatIterator;
import gnu.trove.map.TDoubleFloatMap;
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

public class TDoubleFloatMapDecorator extends AbstractMap<Double, Float> implements Map<Double, Float>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TDoubleFloatMap _map;
  
  public TDoubleFloatMapDecorator() {}
  
  public TDoubleFloatMapDecorator(TDoubleFloatMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TDoubleFloatMap getMap() {
    return this._map;
  }
  
  public Float put(Double key, Float value) {
    double k;
    float v;
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
    double k;
    if (key != null) {
      if (key instanceof Double) {
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
    double k;
    if (key != null) {
      if (key instanceof Double) {
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
  
  public Set<Map.Entry<Double, Float>> entrySet() {
    return new AbstractSet<Map.Entry<Double, Float>>() {
        public int size() {
          return TDoubleFloatMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TDoubleFloatMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TDoubleFloatMapDecorator.this.containsKey(k) && TDoubleFloatMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Double, Float>> iterator() {
          return new Iterator<Map.Entry<Double, Float>>() {
              private final TDoubleFloatIterator it = TDoubleFloatMapDecorator.this._map.iterator();
              
              public Map.Entry<Double, Float> next() {
                this.it.advance();
                double ik = this.it.key();
                final Double key = (ik == TDoubleFloatMapDecorator.this._map.getNoEntryKey()) ? null : TDoubleFloatMapDecorator.this.wrapKey(ik);
                float iv = this.it.value();
                final Float v = (iv == TDoubleFloatMapDecorator.this._map.getNoEntryValue()) ? null : TDoubleFloatMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Double, Float>() {
                    private Float val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Double getKey() {
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
                      return TDoubleFloatMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Double, Float> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Double key = (Double)((Map.Entry)o).getKey();
            TDoubleFloatMapDecorator.this._map.remove(TDoubleFloatMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Double, Float>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TDoubleFloatMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Float && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Double && this._map.containsKey(unwrapKey(key)));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Double, ? extends Float> map) {
    Iterator<? extends Map.Entry<? extends Double, ? extends Float>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Double, ? extends Float> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Double wrapKey(double k) {
    return Double.valueOf(k);
  }
  
  protected double unwrapKey(Object key) {
    return ((Double)key).doubleValue();
  }
  
  protected Float wrapValue(float k) {
    return Float.valueOf(k);
  }
  
  protected float unwrapValue(Object value) {
    return ((Float)value).floatValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TDoubleFloatMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
