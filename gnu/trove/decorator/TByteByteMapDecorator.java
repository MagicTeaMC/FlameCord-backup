package gnu.trove.decorator;

import gnu.trove.iterator.TByteByteIterator;
import gnu.trove.map.TByteByteMap;
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

public class TByteByteMapDecorator extends AbstractMap<Byte, Byte> implements Map<Byte, Byte>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TByteByteMap _map;
  
  public TByteByteMapDecorator() {}
  
  public TByteByteMapDecorator(TByteByteMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TByteByteMap getMap() {
    return this._map;
  }
  
  public Byte put(Byte key, Byte value) {
    byte k, v;
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
    byte v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Byte remove(Object key) {
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
    byte v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Byte, Byte>> entrySet() {
    return new AbstractSet<Map.Entry<Byte, Byte>>() {
        public int size() {
          return TByteByteMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TByteByteMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TByteByteMapDecorator.this.containsKey(k) && TByteByteMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Byte, Byte>> iterator() {
          return new Iterator<Map.Entry<Byte, Byte>>() {
              private final TByteByteIterator it = TByteByteMapDecorator.this._map.iterator();
              
              public Map.Entry<Byte, Byte> next() {
                this.it.advance();
                byte ik = this.it.key();
                final Byte key = (ik == TByteByteMapDecorator.this._map.getNoEntryKey()) ? null : TByteByteMapDecorator.this.wrapKey(ik);
                byte iv = this.it.value();
                final Byte v = (iv == TByteByteMapDecorator.this._map.getNoEntryValue()) ? null : TByteByteMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Byte, Byte>() {
                    private Byte val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Byte getKey() {
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
                      return TByteByteMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Byte, Byte> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Byte key = (Byte)((Map.Entry)o).getKey();
            TByteByteMapDecorator.this._map.remove(TByteByteMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Byte, Byte>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TByteByteMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Byte && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Byte, ? extends Byte> map) {
    Iterator<? extends Map.Entry<? extends Byte, ? extends Byte>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Byte, ? extends Byte> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Byte wrapKey(byte k) {
    return Byte.valueOf(k);
  }
  
  protected byte unwrapKey(Object key) {
    return ((Byte)key).byteValue();
  }
  
  protected Byte wrapValue(byte k) {
    return Byte.valueOf(k);
  }
  
  protected byte unwrapValue(Object value) {
    return ((Byte)value).byteValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TByteByteMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
