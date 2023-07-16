package gnu.trove.decorator;

import gnu.trove.iterator.TByteShortIterator;
import gnu.trove.map.TByteShortMap;
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

public class TByteShortMapDecorator extends AbstractMap<Byte, Short> implements Map<Byte, Short>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TByteShortMap _map;
  
  public TByteShortMapDecorator() {}
  
  public TByteShortMapDecorator(TByteShortMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TByteShortMap getMap() {
    return this._map;
  }
  
  public Short put(Byte key, Short value) {
    byte k;
    short v;
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
    short retval = this._map.put(k, v);
    if (retval == this._map.getNoEntryValue())
      return null; 
    return wrapValue(retval);
  }
  
  public Short get(Object key) {
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
    short v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Short remove(Object key) {
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
    short v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Byte, Short>> entrySet() {
    return new AbstractSet<Map.Entry<Byte, Short>>() {
        public int size() {
          return TByteShortMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TByteShortMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TByteShortMapDecorator.this.containsKey(k) && TByteShortMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Byte, Short>> iterator() {
          return new Iterator<Map.Entry<Byte, Short>>() {
              private final TByteShortIterator it = TByteShortMapDecorator.this._map.iterator();
              
              public Map.Entry<Byte, Short> next() {
                this.it.advance();
                byte ik = this.it.key();
                final Byte key = (ik == TByteShortMapDecorator.this._map.getNoEntryKey()) ? null : TByteShortMapDecorator.this.wrapKey(ik);
                short iv = this.it.value();
                final Short v = (iv == TByteShortMapDecorator.this._map.getNoEntryValue()) ? null : TByteShortMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Byte, Short>() {
                    private Short val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Byte getKey() {
                      return key;
                    }
                    
                    public Short getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Short setValue(Short value) {
                      this.val = value;
                      return TByteShortMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Byte, Short> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Byte key = (Byte)((Map.Entry)o).getKey();
            TByteShortMapDecorator.this._map.remove(TByteShortMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Byte, Short>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TByteShortMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Short && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Byte, ? extends Short> map) {
    Iterator<? extends Map.Entry<? extends Byte, ? extends Short>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Byte, ? extends Short> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Byte wrapKey(byte k) {
    return Byte.valueOf(k);
  }
  
  protected byte unwrapKey(Object key) {
    return ((Byte)key).byteValue();
  }
  
  protected Short wrapValue(short k) {
    return Short.valueOf(k);
  }
  
  protected short unwrapValue(Object value) {
    return ((Short)value).shortValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TByteShortMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
