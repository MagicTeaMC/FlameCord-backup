package gnu.trove.decorator;

import gnu.trove.iterator.TLongShortIterator;
import gnu.trove.map.TLongShortMap;
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

public class TLongShortMapDecorator extends AbstractMap<Long, Short> implements Map<Long, Short>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TLongShortMap _map;
  
  public TLongShortMapDecorator() {}
  
  public TLongShortMapDecorator(TLongShortMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TLongShortMap getMap() {
    return this._map;
  }
  
  public Short put(Long key, Short value) {
    long k;
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
    short v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Short remove(Object key) {
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
    short v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Long, Short>> entrySet() {
    return new AbstractSet<Map.Entry<Long, Short>>() {
        public int size() {
          return TLongShortMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TLongShortMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TLongShortMapDecorator.this.containsKey(k) && TLongShortMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Long, Short>> iterator() {
          return new Iterator<Map.Entry<Long, Short>>() {
              private final TLongShortIterator it = TLongShortMapDecorator.this._map.iterator();
              
              public Map.Entry<Long, Short> next() {
                this.it.advance();
                long ik = this.it.key();
                final Long key = (ik == TLongShortMapDecorator.this._map.getNoEntryKey()) ? null : TLongShortMapDecorator.this.wrapKey(ik);
                short iv = this.it.value();
                final Short v = (iv == TLongShortMapDecorator.this._map.getNoEntryValue()) ? null : TLongShortMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Long, Short>() {
                    private Short val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Long getKey() {
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
                      return TLongShortMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Long, Short> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Long key = (Long)((Map.Entry)o).getKey();
            TLongShortMapDecorator.this._map.remove(TLongShortMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Long, Short>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TLongShortMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Short && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Long, ? extends Short> map) {
    Iterator<? extends Map.Entry<? extends Long, ? extends Short>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Long, ? extends Short> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Long wrapKey(long k) {
    return Long.valueOf(k);
  }
  
  protected long unwrapKey(Object key) {
    return ((Long)key).longValue();
  }
  
  protected Short wrapValue(short k) {
    return Short.valueOf(k);
  }
  
  protected short unwrapValue(Object value) {
    return ((Short)value).shortValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TLongShortMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
