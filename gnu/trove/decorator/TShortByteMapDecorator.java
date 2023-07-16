package gnu.trove.decorator;

import gnu.trove.iterator.TShortByteIterator;
import gnu.trove.map.TShortByteMap;
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

public class TShortByteMapDecorator extends AbstractMap<Short, Byte> implements Map<Short, Byte>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TShortByteMap _map;
  
  public TShortByteMapDecorator() {}
  
  public TShortByteMapDecorator(TShortByteMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TShortByteMap getMap() {
    return this._map;
  }
  
  public Byte put(Short key, Byte value) {
    short k;
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
    short k;
    if (key != null) {
      if (key instanceof Short) {
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
    short k;
    if (key != null) {
      if (key instanceof Short) {
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
  
  public Set<Map.Entry<Short, Byte>> entrySet() {
    return new AbstractSet<Map.Entry<Short, Byte>>() {
        public int size() {
          return TShortByteMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TShortByteMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TShortByteMapDecorator.this.containsKey(k) && TShortByteMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Short, Byte>> iterator() {
          return new Iterator<Map.Entry<Short, Byte>>() {
              private final TShortByteIterator it = TShortByteMapDecorator.this._map.iterator();
              
              public Map.Entry<Short, Byte> next() {
                this.it.advance();
                short ik = this.it.key();
                final Short key = (ik == TShortByteMapDecorator.this._map.getNoEntryKey()) ? null : TShortByteMapDecorator.this.wrapKey(ik);
                byte iv = this.it.value();
                final Byte v = (iv == TShortByteMapDecorator.this._map.getNoEntryValue()) ? null : TShortByteMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Short, Byte>() {
                    private Byte val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Short getKey() {
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
                      return TShortByteMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Short, Byte> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Short key = (Short)((Map.Entry)o).getKey();
            TShortByteMapDecorator.this._map.remove(TShortByteMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Short, Byte>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TShortByteMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Byte && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Short && this._map.containsKey(unwrapKey(key)));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Short, ? extends Byte> map) {
    Iterator<? extends Map.Entry<? extends Short, ? extends Byte>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Short, ? extends Byte> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Short wrapKey(short k) {
    return Short.valueOf(k);
  }
  
  protected short unwrapKey(Object key) {
    return ((Short)key).shortValue();
  }
  
  protected Byte wrapValue(byte k) {
    return Byte.valueOf(k);
  }
  
  protected byte unwrapValue(Object value) {
    return ((Byte)value).byteValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TShortByteMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
