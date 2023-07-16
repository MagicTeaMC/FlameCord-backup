package gnu.trove.decorator;

import gnu.trove.iterator.TLongIntIterator;
import gnu.trove.map.TLongIntMap;
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

public class TLongIntMapDecorator extends AbstractMap<Long, Integer> implements Map<Long, Integer>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TLongIntMap _map;
  
  public TLongIntMapDecorator() {}
  
  public TLongIntMapDecorator(TLongIntMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TLongIntMap getMap() {
    return this._map;
  }
  
  public Integer put(Long key, Integer value) {
    long k;
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
    int v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Integer remove(Object key) {
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
    int v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Long, Integer>> entrySet() {
    return new AbstractSet<Map.Entry<Long, Integer>>() {
        public int size() {
          return TLongIntMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TLongIntMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TLongIntMapDecorator.this.containsKey(k) && TLongIntMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Long, Integer>> iterator() {
          return new Iterator<Map.Entry<Long, Integer>>() {
              private final TLongIntIterator it = TLongIntMapDecorator.this._map.iterator();
              
              public Map.Entry<Long, Integer> next() {
                this.it.advance();
                long ik = this.it.key();
                final Long key = (ik == TLongIntMapDecorator.this._map.getNoEntryKey()) ? null : TLongIntMapDecorator.this.wrapKey(ik);
                int iv = this.it.value();
                final Integer v = (iv == TLongIntMapDecorator.this._map.getNoEntryValue()) ? null : TLongIntMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Long, Integer>() {
                    private Integer val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Long getKey() {
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
                      return TLongIntMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Long, Integer> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Long key = (Long)((Map.Entry)o).getKey();
            TLongIntMapDecorator.this._map.remove(TLongIntMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Long, Integer>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TLongIntMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Integer && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Long, ? extends Integer> map) {
    Iterator<? extends Map.Entry<? extends Long, ? extends Integer>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Long, ? extends Integer> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Long wrapKey(long k) {
    return Long.valueOf(k);
  }
  
  protected long unwrapKey(Object key) {
    return ((Long)key).longValue();
  }
  
  protected Integer wrapValue(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapValue(Object value) {
    return ((Integer)value).intValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TLongIntMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
