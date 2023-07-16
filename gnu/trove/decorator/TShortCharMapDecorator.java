package gnu.trove.decorator;

import gnu.trove.iterator.TShortCharIterator;
import gnu.trove.map.TShortCharMap;
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

public class TShortCharMapDecorator extends AbstractMap<Short, Character> implements Map<Short, Character>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TShortCharMap _map;
  
  public TShortCharMapDecorator() {}
  
  public TShortCharMapDecorator(TShortCharMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TShortCharMap getMap() {
    return this._map;
  }
  
  public Character put(Short key, Character value) {
    short k;
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
    char v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Character remove(Object key) {
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
    char v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Short, Character>> entrySet() {
    return new AbstractSet<Map.Entry<Short, Character>>() {
        public int size() {
          return TShortCharMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TShortCharMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TShortCharMapDecorator.this.containsKey(k) && TShortCharMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Short, Character>> iterator() {
          return new Iterator<Map.Entry<Short, Character>>() {
              private final TShortCharIterator it = TShortCharMapDecorator.this._map.iterator();
              
              public Map.Entry<Short, Character> next() {
                this.it.advance();
                short ik = this.it.key();
                final Short key = (ik == TShortCharMapDecorator.this._map.getNoEntryKey()) ? null : TShortCharMapDecorator.this.wrapKey(ik);
                char iv = this.it.value();
                final Character v = (iv == TShortCharMapDecorator.this._map.getNoEntryValue()) ? null : TShortCharMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Short, Character>() {
                    private Character val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Short getKey() {
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
                      return TShortCharMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Short, Character> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Short key = (Short)((Map.Entry)o).getKey();
            TShortCharMapDecorator.this._map.remove(TShortCharMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Short, Character>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TShortCharMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Character && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Short, ? extends Character> map) {
    Iterator<? extends Map.Entry<? extends Short, ? extends Character>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Short, ? extends Character> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Short wrapKey(short k) {
    return Short.valueOf(k);
  }
  
  protected short unwrapKey(Object key) {
    return ((Short)key).shortValue();
  }
  
  protected Character wrapValue(char k) {
    return Character.valueOf(k);
  }
  
  protected char unwrapValue(Object value) {
    return ((Character)value).charValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TShortCharMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
