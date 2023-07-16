package gnu.trove.decorator;

import gnu.trove.iterator.TByteIntIterator;
import gnu.trove.map.TByteIntMap;
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

public class TByteIntMapDecorator extends AbstractMap<Byte, Integer> implements Map<Byte, Integer>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TByteIntMap _map;
  
  public TByteIntMapDecorator() {}
  
  public TByteIntMapDecorator(TByteIntMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TByteIntMap getMap() {
    return this._map;
  }
  
  public Integer put(Byte key, Integer value) {
    byte k;
    int v;
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
    int retval = this._map.put(k, v);
    if (retval == this._map.getNoEntryValue())
      return null; 
    return wrapValue(retval);
  }
  
  public Integer get(Object key) {
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
    int v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Integer remove(Object key) {
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
    int v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Byte, Integer>> entrySet() {
    return new AbstractSet<Map.Entry<Byte, Integer>>() {
        public int size() {
          return TByteIntMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TByteIntMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TByteIntMapDecorator.this.containsKey(k) && TByteIntMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Byte, Integer>> iterator() {
          return new Iterator<Map.Entry<Byte, Integer>>() {
              private final TByteIntIterator it = TByteIntMapDecorator.this._map.iterator();
              
              public Map.Entry<Byte, Integer> next() {
                this.it.advance();
                byte ik = this.it.key();
                final Byte key = (ik == TByteIntMapDecorator.this._map.getNoEntryKey()) ? null : TByteIntMapDecorator.this.wrapKey(ik);
                int iv = this.it.value();
                final Integer v = (iv == TByteIntMapDecorator.this._map.getNoEntryValue()) ? null : TByteIntMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Byte, Integer>() {
                    private Integer val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Byte getKey() {
                      return key;
                    }
                    
                    public Integer getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Integer setValue(Integer value) {
                      this.val = value;
                      return TByteIntMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Byte, Integer> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Byte key = (Byte)((Map.Entry)o).getKey();
            TByteIntMapDecorator.this._map.remove(TByteIntMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Byte, Integer>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TByteIntMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Integer && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Byte, ? extends Integer> map) {
    Iterator<? extends Map.Entry<? extends Byte, ? extends Integer>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Byte, ? extends Integer> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Byte wrapKey(byte k) {
    return Byte.valueOf(k);
  }
  
  protected byte unwrapKey(Object key) {
    return ((Byte)key).byteValue();
  }
  
  protected Integer wrapValue(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapValue(Object value) {
    return ((Integer)value).intValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TByteIntMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
