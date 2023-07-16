package gnu.trove.decorator;

import gnu.trove.iterator.TByteFloatIterator;
import gnu.trove.map.TByteFloatMap;
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

public class TByteFloatMapDecorator extends AbstractMap<Byte, Float> implements Map<Byte, Float>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TByteFloatMap _map;
  
  public TByteFloatMapDecorator() {}
  
  public TByteFloatMapDecorator(TByteFloatMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TByteFloatMap getMap() {
    return this._map;
  }
  
  public Float put(Byte key, Float value) {
    byte k;
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
    byte k;
    if (key != null) {
      if (key instanceof Byte) {
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
    byte k;
    if (key != null) {
      if (key instanceof Byte) {
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
  
  public Set<Map.Entry<Byte, Float>> entrySet() {
    return new AbstractSet<Map.Entry<Byte, Float>>() {
        public int size() {
          return TByteFloatMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TByteFloatMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TByteFloatMapDecorator.this.containsKey(k) && TByteFloatMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Byte, Float>> iterator() {
          return new Iterator<Map.Entry<Byte, Float>>() {
              private final TByteFloatIterator it = TByteFloatMapDecorator.this._map.iterator();
              
              public Map.Entry<Byte, Float> next() {
                this.it.advance();
                byte ik = this.it.key();
                final Byte key = (ik == TByteFloatMapDecorator.this._map.getNoEntryKey()) ? null : TByteFloatMapDecorator.this.wrapKey(ik);
                float iv = this.it.value();
                final Float v = (iv == TByteFloatMapDecorator.this._map.getNoEntryValue()) ? null : TByteFloatMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Byte, Float>() {
                    private Float val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Byte getKey() {
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
                      return TByteFloatMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Byte, Float> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Byte key = (Byte)((Map.Entry)o).getKey();
            TByteFloatMapDecorator.this._map.remove(TByteFloatMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Byte, Float>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TByteFloatMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Float && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Byte && this._map.containsKey(unwrapKey(key)));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Byte, ? extends Float> map) {
    Iterator<? extends Map.Entry<? extends Byte, ? extends Float>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Byte, ? extends Float> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Byte wrapKey(byte k) {
    return Byte.valueOf(k);
  }
  
  protected byte unwrapKey(Object key) {
    return ((Byte)key).byteValue();
  }
  
  protected Float wrapValue(float k) {
    return Float.valueOf(k);
  }
  
  protected float unwrapValue(Object value) {
    return ((Float)value).floatValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TByteFloatMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
