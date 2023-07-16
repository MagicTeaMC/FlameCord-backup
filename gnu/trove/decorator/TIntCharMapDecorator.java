package gnu.trove.decorator;

import gnu.trove.iterator.TIntCharIterator;
import gnu.trove.map.TIntCharMap;
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

public class TIntCharMapDecorator extends AbstractMap<Integer, Character> implements Map<Integer, Character>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TIntCharMap _map;
  
  public TIntCharMapDecorator() {}
  
  public TIntCharMapDecorator(TIntCharMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TIntCharMap getMap() {
    return this._map;
  }
  
  public Character put(Integer key, Character value) {
    int k;
    char v;
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
    char retval = this._map.put(k, v);
    if (retval == this._map.getNoEntryValue())
      return null; 
    return wrapValue(retval);
  }
  
  public Character get(Object key) {
    int k;
    if (key != null) {
      if (key instanceof Integer) {
        k = unwrapKey(key);
      } else {
        return null;
      } 
    } else {
      k = this._map.getNoEntryKey();
    } 
    char v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Character remove(Object key) {
    int k;
    if (key != null) {
      if (key instanceof Integer) {
        k = unwrapKey(key);
      } else {
        return null;
      } 
    } else {
      k = this._map.getNoEntryKey();
    } 
    char v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Integer, Character>> entrySet() {
    return new AbstractSet<Map.Entry<Integer, Character>>() {
        public int size() {
          return TIntCharMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TIntCharMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TIntCharMapDecorator.this.containsKey(k) && TIntCharMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Integer, Character>> iterator() {
          return new Iterator<Map.Entry<Integer, Character>>() {
              private final TIntCharIterator it = TIntCharMapDecorator.this._map.iterator();
              
              public Map.Entry<Integer, Character> next() {
                this.it.advance();
                int ik = this.it.key();
                final Integer key = (ik == TIntCharMapDecorator.this._map.getNoEntryKey()) ? null : TIntCharMapDecorator.this.wrapKey(ik);
                char iv = this.it.value();
                final Character v = (iv == TIntCharMapDecorator.this._map.getNoEntryValue()) ? null : TIntCharMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Integer, Character>() {
                    private Character val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Integer getKey() {
                      return key;
                    }
                    
                    public Character getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Character setValue(Character value) {
                      this.val = value;
                      return TIntCharMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Integer, Character> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Integer key = (Integer)((Map.Entry)o).getKey();
            TIntCharMapDecorator.this._map.remove(TIntCharMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Integer, Character>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TIntCharMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Character && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Integer && this._map.containsKey(unwrapKey(key)));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Integer, ? extends Character> map) {
    Iterator<? extends Map.Entry<? extends Integer, ? extends Character>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Integer, ? extends Character> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Integer wrapKey(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapKey(Object key) {
    return ((Integer)key).intValue();
  }
  
  protected Character wrapValue(char k) {
    return Character.valueOf(k);
  }
  
  protected char unwrapValue(Object value) {
    return ((Character)value).charValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TIntCharMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
