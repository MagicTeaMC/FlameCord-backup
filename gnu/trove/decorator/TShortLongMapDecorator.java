package gnu.trove.decorator;

import gnu.trove.iterator.TShortLongIterator;
import gnu.trove.map.TShortLongMap;
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

public class TShortLongMapDecorator extends AbstractMap<Short, Long> implements Map<Short, Long>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TShortLongMap _map;
  
  public TShortLongMapDecorator() {}
  
  public TShortLongMapDecorator(TShortLongMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TShortLongMap getMap() {
    return this._map;
  }
  
  public Long put(Short key, Long value) {
    short k;
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
    long v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Long remove(Object key) {
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
    long v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Short, Long>> entrySet() {
    return new AbstractSet<Map.Entry<Short, Long>>() {
        public int size() {
          return TShortLongMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TShortLongMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TShortLongMapDecorator.this.containsKey(k) && TShortLongMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Short, Long>> iterator() {
          return new Iterator<Map.Entry<Short, Long>>() {
              private final TShortLongIterator it = TShortLongMapDecorator.this._map.iterator();
              
              public Map.Entry<Short, Long> next() {
                this.it.advance();
                short ik = this.it.key();
                final Short key = (ik == TShortLongMapDecorator.this._map.getNoEntryKey()) ? null : TShortLongMapDecorator.this.wrapKey(ik);
                long iv = this.it.value();
                final Long v = (iv == TShortLongMapDecorator.this._map.getNoEntryValue()) ? null : TShortLongMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Short, Long>() {
                    private Long val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Short getKey() {
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
                      return TShortLongMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Short, Long> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Short key = (Short)((Map.Entry)o).getKey();
            TShortLongMapDecorator.this._map.remove(TShortLongMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Short, Long>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TShortLongMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Long && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Short, ? extends Long> map) {
    Iterator<? extends Map.Entry<? extends Short, ? extends Long>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Short, ? extends Long> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Short wrapKey(short k) {
    return Short.valueOf(k);
  }
  
  protected short unwrapKey(Object key) {
    return ((Short)key).shortValue();
  }
  
  protected Long wrapValue(long k) {
    return Long.valueOf(k);
  }
  
  protected long unwrapValue(Object value) {
    return ((Long)value).longValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TShortLongMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
