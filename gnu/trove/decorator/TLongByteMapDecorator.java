package gnu.trove.decorator;

import gnu.trove.iterator.TLongByteIterator;
import gnu.trove.map.TLongByteMap;
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

public class TLongByteMapDecorator extends AbstractMap<Long, Byte> implements Map<Long, Byte>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TLongByteMap _map;
  
  public TLongByteMapDecorator() {}
  
  public TLongByteMapDecorator(TLongByteMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TLongByteMap getMap() {
    return this._map;
  }
  
  public Byte put(Long key, Byte value) {
    long k;
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
    long k;
    if (key != null) {
      if (key instanceof Long) {
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
    long k;
    if (key != null) {
      if (key instanceof Long) {
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
  
  public Set<Map.Entry<Long, Byte>> entrySet() {
    return new AbstractSet<Map.Entry<Long, Byte>>() {
        public int size() {
          return TLongByteMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TLongByteMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TLongByteMapDecorator.this.containsKey(k) && TLongByteMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Long, Byte>> iterator() {
          return new Iterator<Map.Entry<Long, Byte>>() {
              private final TLongByteIterator it = TLongByteMapDecorator.this._map.iterator();
              
              public Map.Entry<Long, Byte> next() {
                this.it.advance();
                long ik = this.it.key();
                final Long key = (ik == TLongByteMapDecorator.this._map.getNoEntryKey()) ? null : TLongByteMapDecorator.this.wrapKey(ik);
                byte iv = this.it.value();
                final Byte v = (iv == TLongByteMapDecorator.this._map.getNoEntryValue()) ? null : TLongByteMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Long, Byte>() {
                    private Byte val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Long getKey() {
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
                      return TLongByteMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Long, Byte> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Long key = (Long)((Map.Entry)o).getKey();
            TLongByteMapDecorator.this._map.remove(TLongByteMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Long, Byte>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TLongByteMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Byte && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Long && this._map.containsKey(unwrapKey(key)));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Long, ? extends Byte> map) {
    Iterator<? extends Map.Entry<? extends Long, ? extends Byte>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Long, ? extends Byte> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Long wrapKey(long k) {
    return Long.valueOf(k);
  }
  
  protected long unwrapKey(Object key) {
    return ((Long)key).longValue();
  }
  
  protected Byte wrapValue(byte k) {
    return Byte.valueOf(k);
  }
  
  protected byte unwrapValue(Object value) {
    return ((Byte)value).byteValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TLongByteMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
