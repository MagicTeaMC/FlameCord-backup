package gnu.trove.decorator;

import gnu.trove.iterator.TByteLongIterator;
import gnu.trove.map.TByteLongMap;
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

public class TByteLongMapDecorator extends AbstractMap<Byte, Long> implements Map<Byte, Long>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TByteLongMap _map;
  
  public TByteLongMapDecorator() {}
  
  public TByteLongMapDecorator(TByteLongMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TByteLongMap getMap() {
    return this._map;
  }
  
  public Long put(Byte key, Long value) {
    byte k;
    long v;
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
    long retval = this._map.put(k, v);
    if (retval == this._map.getNoEntryValue())
      return null; 
    return wrapValue(retval);
  }
  
  public Long get(Object key) {
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
    long v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Long remove(Object key) {
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
    long v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Byte, Long>> entrySet() {
    return new AbstractSet<Map.Entry<Byte, Long>>() {
        public int size() {
          return TByteLongMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TByteLongMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TByteLongMapDecorator.this.containsKey(k) && TByteLongMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Byte, Long>> iterator() {
          return new Iterator<Map.Entry<Byte, Long>>() {
              private final TByteLongIterator it = TByteLongMapDecorator.this._map.iterator();
              
              public Map.Entry<Byte, Long> next() {
                this.it.advance();
                byte ik = this.it.key();
                final Byte key = (ik == TByteLongMapDecorator.this._map.getNoEntryKey()) ? null : TByteLongMapDecorator.this.wrapKey(ik);
                long iv = this.it.value();
                final Long v = (iv == TByteLongMapDecorator.this._map.getNoEntryValue()) ? null : TByteLongMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Byte, Long>() {
                    private Long val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Byte getKey() {
                      return key;
                    }
                    
                    public Long getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Long setValue(Long value) {
                      this.val = value;
                      return TByteLongMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Byte, Long> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Byte key = (Byte)((Map.Entry)o).getKey();
            TByteLongMapDecorator.this._map.remove(TByteLongMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Byte, Long>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TByteLongMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Long && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Byte, ? extends Long> map) {
    Iterator<? extends Map.Entry<? extends Byte, ? extends Long>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Byte, ? extends Long> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Byte wrapKey(byte k) {
    return Byte.valueOf(k);
  }
  
  protected byte unwrapKey(Object key) {
    return ((Byte)key).byteValue();
  }
  
  protected Long wrapValue(long k) {
    return Long.valueOf(k);
  }
  
  protected long unwrapValue(Object value) {
    return ((Long)value).longValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TByteLongMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
