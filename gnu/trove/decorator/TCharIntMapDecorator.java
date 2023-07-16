package gnu.trove.decorator;

import gnu.trove.iterator.TCharIntIterator;
import gnu.trove.map.TCharIntMap;
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

public class TCharIntMapDecorator extends AbstractMap<Character, Integer> implements Map<Character, Integer>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TCharIntMap _map;
  
  public TCharIntMapDecorator() {}
  
  public TCharIntMapDecorator(TCharIntMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TCharIntMap getMap() {
    return this._map;
  }
  
  public Integer put(Character key, Integer value) {
    char k;
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
    int v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Integer remove(Object key) {
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
    int v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Character, Integer>> entrySet() {
    return new AbstractSet<Map.Entry<Character, Integer>>() {
        public int size() {
          return TCharIntMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TCharIntMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TCharIntMapDecorator.this.containsKey(k) && TCharIntMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Character, Integer>> iterator() {
          return new Iterator<Map.Entry<Character, Integer>>() {
              private final TCharIntIterator it = TCharIntMapDecorator.this._map.iterator();
              
              public Map.Entry<Character, Integer> next() {
                this.it.advance();
                char ik = this.it.key();
                final Character key = (ik == TCharIntMapDecorator.this._map.getNoEntryKey()) ? null : TCharIntMapDecorator.this.wrapKey(ik);
                int iv = this.it.value();
                final Integer v = (iv == TCharIntMapDecorator.this._map.getNoEntryValue()) ? null : TCharIntMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Character, Integer>() {
                    private Integer val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Character getKey() {
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
                      return TCharIntMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Character, Integer> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Character key = (Character)((Map.Entry)o).getKey();
            TCharIntMapDecorator.this._map.remove(TCharIntMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Character, Integer>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TCharIntMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Integer && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Character, ? extends Integer> map) {
    Iterator<? extends Map.Entry<? extends Character, ? extends Integer>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Character, ? extends Integer> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Character wrapKey(char k) {
    return Character.valueOf(k);
  }
  
  protected char unwrapKey(Object key) {
    return ((Character)key).charValue();
  }
  
  protected Integer wrapValue(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapValue(Object value) {
    return ((Integer)value).intValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TCharIntMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
