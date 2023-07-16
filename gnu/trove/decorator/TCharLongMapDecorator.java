package gnu.trove.decorator;

import gnu.trove.iterator.TCharLongIterator;
import gnu.trove.map.TCharLongMap;
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

public class TCharLongMapDecorator extends AbstractMap<Character, Long> implements Map<Character, Long>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TCharLongMap _map;
  
  public TCharLongMapDecorator() {}
  
  public TCharLongMapDecorator(TCharLongMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TCharLongMap getMap() {
    return this._map;
  }
  
  public Long put(Character key, Long value) {
    char k;
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
    char k;
    if (key != null) {
      if (key instanceof Character) {
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
    char k;
    if (key != null) {
      if (key instanceof Character) {
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
  
  public Set<Map.Entry<Character, Long>> entrySet() {
    return new AbstractSet<Map.Entry<Character, Long>>() {
        public int size() {
          return TCharLongMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TCharLongMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TCharLongMapDecorator.this.containsKey(k) && TCharLongMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Character, Long>> iterator() {
          return new Iterator<Map.Entry<Character, Long>>() {
              private final TCharLongIterator it = TCharLongMapDecorator.this._map.iterator();
              
              public Map.Entry<Character, Long> next() {
                this.it.advance();
                char ik = this.it.key();
                final Character key = (ik == TCharLongMapDecorator.this._map.getNoEntryKey()) ? null : TCharLongMapDecorator.this.wrapKey(ik);
                long iv = this.it.value();
                final Long v = (iv == TCharLongMapDecorator.this._map.getNoEntryValue()) ? null : TCharLongMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Character, Long>() {
                    private Long val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Character getKey() {
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
                      return TCharLongMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Character, Long> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Character key = (Character)((Map.Entry)o).getKey();
            TCharLongMapDecorator.this._map.remove(TCharLongMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Character, Long>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TCharLongMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Long && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Character && this._map.containsKey(unwrapKey(key)));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Character, ? extends Long> map) {
    Iterator<? extends Map.Entry<? extends Character, ? extends Long>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Character, ? extends Long> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Character wrapKey(char k) {
    return Character.valueOf(k);
  }
  
  protected char unwrapKey(Object key) {
    return ((Character)key).charValue();
  }
  
  protected Long wrapValue(long k) {
    return Long.valueOf(k);
  }
  
  protected long unwrapValue(Object value) {
    return ((Long)value).longValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TCharLongMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
