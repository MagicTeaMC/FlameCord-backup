package gnu.trove.decorator;

import gnu.trove.iterator.TCharCharIterator;
import gnu.trove.map.TCharCharMap;
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

public class TCharCharMapDecorator extends AbstractMap<Character, Character> implements Map<Character, Character>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TCharCharMap _map;
  
  public TCharCharMapDecorator() {}
  
  public TCharCharMapDecorator(TCharCharMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TCharCharMap getMap() {
    return this._map;
  }
  
  public Character put(Character key, Character value) {
    char k, v;
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
    char v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Character remove(Object key) {
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
    char v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Character, Character>> entrySet() {
    return new AbstractSet<Map.Entry<Character, Character>>() {
        public int size() {
          return TCharCharMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TCharCharMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TCharCharMapDecorator.this.containsKey(k) && TCharCharMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Character, Character>> iterator() {
          return new Iterator<Map.Entry<Character, Character>>() {
              private final TCharCharIterator it = TCharCharMapDecorator.this._map.iterator();
              
              public Map.Entry<Character, Character> next() {
                this.it.advance();
                char ik = this.it.key();
                final Character key = (ik == TCharCharMapDecorator.this._map.getNoEntryKey()) ? null : TCharCharMapDecorator.this.wrapKey(ik);
                char iv = this.it.value();
                final Character v = (iv == TCharCharMapDecorator.this._map.getNoEntryValue()) ? null : TCharCharMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Character, Character>() {
                    private Character val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Character getKey() {
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
                      return TCharCharMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Character, Character> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Character key = (Character)((Map.Entry)o).getKey();
            TCharCharMapDecorator.this._map.remove(TCharCharMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Character, Character>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TCharCharMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Character && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Character, ? extends Character> map) {
    Iterator<? extends Map.Entry<? extends Character, ? extends Character>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Character, ? extends Character> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Character wrapKey(char k) {
    return Character.valueOf(k);
  }
  
  protected char unwrapKey(Object key) {
    return ((Character)key).charValue();
  }
  
  protected Character wrapValue(char k) {
    return Character.valueOf(k);
  }
  
  protected char unwrapValue(Object value) {
    return ((Character)value).charValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TCharCharMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
