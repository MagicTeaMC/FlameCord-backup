package gnu.trove.decorator;

import gnu.trove.iterator.TShortIntIterator;
import gnu.trove.map.TShortIntMap;
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

public class TShortIntMapDecorator extends AbstractMap<Short, Integer> implements Map<Short, Integer>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TShortIntMap _map;
  
  public TShortIntMapDecorator() {}
  
  public TShortIntMapDecorator(TShortIntMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TShortIntMap getMap() {
    return this._map;
  }
  
  public Integer put(Short key, Integer value) {
    short k;
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
    int v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Integer remove(Object key) {
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
    int v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Short, Integer>> entrySet() {
    return new AbstractSet<Map.Entry<Short, Integer>>() {
        public int size() {
          return TShortIntMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TShortIntMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TShortIntMapDecorator.this.containsKey(k) && TShortIntMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Short, Integer>> iterator() {
          return new Iterator<Map.Entry<Short, Integer>>() {
              private final TShortIntIterator it = TShortIntMapDecorator.this._map.iterator();
              
              public Map.Entry<Short, Integer> next() {
                this.it.advance();
                short ik = this.it.key();
                final Short key = (ik == TShortIntMapDecorator.this._map.getNoEntryKey()) ? null : TShortIntMapDecorator.this.wrapKey(ik);
                int iv = this.it.value();
                final Integer v = (iv == TShortIntMapDecorator.this._map.getNoEntryValue()) ? null : TShortIntMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Short, Integer>() {
                    private Integer val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Short getKey() {
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
                      return TShortIntMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Short, Integer> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Short key = (Short)((Map.Entry)o).getKey();
            TShortIntMapDecorator.this._map.remove(TShortIntMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Short, Integer>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TShortIntMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Integer && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Short, ? extends Integer> map) {
    Iterator<? extends Map.Entry<? extends Short, ? extends Integer>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Short, ? extends Integer> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Short wrapKey(short k) {
    return Short.valueOf(k);
  }
  
  protected short unwrapKey(Object key) {
    return ((Short)key).shortValue();
  }
  
  protected Integer wrapValue(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapValue(Object value) {
    return ((Integer)value).intValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TShortIntMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
