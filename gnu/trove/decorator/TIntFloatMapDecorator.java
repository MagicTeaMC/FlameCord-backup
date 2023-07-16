package gnu.trove.decorator;

import gnu.trove.iterator.TIntFloatIterator;
import gnu.trove.map.TIntFloatMap;
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

public class TIntFloatMapDecorator extends AbstractMap<Integer, Float> implements Map<Integer, Float>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TIntFloatMap _map;
  
  public TIntFloatMapDecorator() {}
  
  public TIntFloatMapDecorator(TIntFloatMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TIntFloatMap getMap() {
    return this._map;
  }
  
  public Float put(Integer key, Float value) {
    int k;
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
    int k;
    if (key != null) {
      if (key instanceof Integer) {
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
    int k;
    if (key != null) {
      if (key instanceof Integer) {
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
  
  public Set<Map.Entry<Integer, Float>> entrySet() {
    return new AbstractSet<Map.Entry<Integer, Float>>() {
        public int size() {
          return TIntFloatMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TIntFloatMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TIntFloatMapDecorator.this.containsKey(k) && TIntFloatMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Integer, Float>> iterator() {
          return new Iterator<Map.Entry<Integer, Float>>() {
              private final TIntFloatIterator it = TIntFloatMapDecorator.this._map.iterator();
              
              public Map.Entry<Integer, Float> next() {
                this.it.advance();
                int ik = this.it.key();
                final Integer key = (ik == TIntFloatMapDecorator.this._map.getNoEntryKey()) ? null : TIntFloatMapDecorator.this.wrapKey(ik);
                float iv = this.it.value();
                final Float v = (iv == TIntFloatMapDecorator.this._map.getNoEntryValue()) ? null : TIntFloatMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Integer, Float>() {
                    private Float val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Integer getKey() {
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
                      return TIntFloatMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Integer, Float> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Integer key = (Integer)((Map.Entry)o).getKey();
            TIntFloatMapDecorator.this._map.remove(TIntFloatMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Integer, Float>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TIntFloatMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Float && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Integer && this._map.containsKey(unwrapKey(key)));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Integer, ? extends Float> map) {
    Iterator<? extends Map.Entry<? extends Integer, ? extends Float>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Integer, ? extends Float> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Integer wrapKey(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapKey(Object key) {
    return ((Integer)key).intValue();
  }
  
  protected Float wrapValue(float k) {
    return Float.valueOf(k);
  }
  
  protected float unwrapValue(Object value) {
    return ((Float)value).floatValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TIntFloatMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
