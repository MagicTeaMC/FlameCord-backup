package gnu.trove.decorator;

import gnu.trove.iterator.TIntShortIterator;
import gnu.trove.map.TIntShortMap;
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

public class TIntShortMapDecorator extends AbstractMap<Integer, Short> implements Map<Integer, Short>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TIntShortMap _map;
  
  public TIntShortMapDecorator() {}
  
  public TIntShortMapDecorator(TIntShortMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TIntShortMap getMap() {
    return this._map;
  }
  
  public Short put(Integer key, Short value) {
    int k;
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
    short v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Short remove(Object key) {
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
    short v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Integer, Short>> entrySet() {
    return new AbstractSet<Map.Entry<Integer, Short>>() {
        public int size() {
          return TIntShortMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TIntShortMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TIntShortMapDecorator.this.containsKey(k) && TIntShortMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Integer, Short>> iterator() {
          return new Iterator<Map.Entry<Integer, Short>>() {
              private final TIntShortIterator it = TIntShortMapDecorator.this._map.iterator();
              
              public Map.Entry<Integer, Short> next() {
                this.it.advance();
                int ik = this.it.key();
                final Integer key = (ik == TIntShortMapDecorator.this._map.getNoEntryKey()) ? null : TIntShortMapDecorator.this.wrapKey(ik);
                short iv = this.it.value();
                final Short v = (iv == TIntShortMapDecorator.this._map.getNoEntryValue()) ? null : TIntShortMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Integer, Short>() {
                    private Short val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Integer getKey() {
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
                      return TIntShortMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Integer, Short> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Integer key = (Integer)((Map.Entry)o).getKey();
            TIntShortMapDecorator.this._map.remove(TIntShortMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Integer, Short>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TIntShortMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Short && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Integer, ? extends Short> map) {
    Iterator<? extends Map.Entry<? extends Integer, ? extends Short>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Integer, ? extends Short> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Integer wrapKey(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapKey(Object key) {
    return ((Integer)key).intValue();
  }
  
  protected Short wrapValue(short k) {
    return Short.valueOf(k);
  }
  
  protected short unwrapValue(Object value) {
    return ((Short)value).shortValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TIntShortMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
