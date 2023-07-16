package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleByteIterator;
import gnu.trove.map.TDoubleByteMap;
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

public class TDoubleByteMapDecorator extends AbstractMap<Double, Byte> implements Map<Double, Byte>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TDoubleByteMap _map;
  
  public TDoubleByteMapDecorator() {}
  
  public TDoubleByteMapDecorator(TDoubleByteMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TDoubleByteMap getMap() {
    return this._map;
  }
  
  public Byte put(Double key, Byte value) {
    double k;
    byte v;
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
    byte retval = this._map.put(k, v);
    if (retval == this._map.getNoEntryValue())
      return null; 
    return wrapValue(retval);
  }
  
  public Byte get(Object key) {
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
    byte v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Byte remove(Object key) {
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
    byte v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Double, Byte>> entrySet() {
    return new AbstractSet<Map.Entry<Double, Byte>>() {
        public int size() {
          return TDoubleByteMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TDoubleByteMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TDoubleByteMapDecorator.this.containsKey(k) && TDoubleByteMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Double, Byte>> iterator() {
          return new Iterator<Map.Entry<Double, Byte>>() {
              private final TDoubleByteIterator it = TDoubleByteMapDecorator.this._map.iterator();
              
              public Map.Entry<Double, Byte> next() {
                this.it.advance();
                double ik = this.it.key();
                final Double key = (ik == TDoubleByteMapDecorator.this._map.getNoEntryKey()) ? null : TDoubleByteMapDecorator.this.wrapKey(ik);
                byte iv = this.it.value();
                final Byte v = (iv == TDoubleByteMapDecorator.this._map.getNoEntryValue()) ? null : TDoubleByteMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Double, Byte>() {
                    private Byte val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Double getKey() {
                      return key;
                    }
                    
                    public Byte getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Byte setValue(Byte value) {
                      this.val = value;
                      return TDoubleByteMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Double, Byte> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Double key = (Double)((Map.Entry)o).getKey();
            TDoubleByteMapDecorator.this._map.remove(TDoubleByteMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Double, Byte>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TDoubleByteMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Byte && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Double, ? extends Byte> map) {
    Iterator<? extends Map.Entry<? extends Double, ? extends Byte>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Double, ? extends Byte> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Double wrapKey(double k) {
    return Double.valueOf(k);
  }
  
  protected double unwrapKey(Object key) {
    return ((Double)key).doubleValue();
  }
  
  protected Byte wrapValue(byte k) {
    return Byte.valueOf(k);
  }
  
  protected byte unwrapValue(Object value) {
    return ((Byte)value).byteValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TDoubleByteMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
