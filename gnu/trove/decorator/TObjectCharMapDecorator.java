package gnu.trove.decorator;

import gnu.trove.iterator.TObjectCharIterator;
import gnu.trove.map.TObjectCharMap;
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

public class TObjectCharMapDecorator<K> extends AbstractMap<K, Character> implements Map<K, Character>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TObjectCharMap<K> _map;
  
  public TObjectCharMapDecorator() {}
  
  public TObjectCharMapDecorator(TObjectCharMap<K> map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TObjectCharMap<K> getMap() {
    return this._map;
  }
  
  public Character put(K key, Character value) {
    if (value == null)
      return wrapValue(this._map.put(key, this._map.getNoEntryValue())); 
    return wrapValue(this._map.put(key, unwrapValue(value)));
  }
  
  public Character get(Object key) {
    char v = this._map.get(key);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Character remove(Object key) {
    char v = this._map.remove(key);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<K, Character>> entrySet() {
    return new AbstractSet<Map.Entry<K, Character>>() {
        public int size() {
          return TObjectCharMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TObjectCharMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TObjectCharMapDecorator.this.containsKey(k) && TObjectCharMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<K, Character>> iterator() {
          return new Iterator<Map.Entry<K, Character>>() {
              private final TObjectCharIterator<K> it = TObjectCharMapDecorator.this._map.iterator();
              
              public Map.Entry<K, Character> next() {
                this.it.advance();
                final K key = (K)this.it.key();
                final Character v = TObjectCharMapDecorator.this.wrapValue(this.it.value());
                return new Map.Entry<K, Character>() {
                    private Character val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public K getKey() {
                      return (K)key;
                    }
                    
                    public Character getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Character setValue(Character value) {
                      this.val = value;
                      return TObjectCharMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<K, Character> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            K key = (K)((Map.Entry)o).getKey();
            TObjectCharMapDecorator.this._map.remove(key);
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<K, Character>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TObjectCharMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Character && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    return this._map.containsKey(key);
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (this._map.size() == 0);
  }
  
  public void putAll(Map<? extends K, ? extends Character> map) {
    Iterator<? extends Map.Entry<? extends K, ? extends Character>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends K, ? extends Character> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Character wrapValue(char k) {
    return Character.valueOf(k);
  }
  
  protected char unwrapValue(Object value) {
    return ((Character)value).charValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TObjectCharMap<K>)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
