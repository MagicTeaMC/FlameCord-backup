package gnu.trove.decorator;

import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.map.TIntIntMap;
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

public class TIntIntMapDecorator extends AbstractMap<Integer, Integer> implements Map<Integer, Integer>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TIntIntMap _map;
  
  public TIntIntMapDecorator() {}
  
  public TIntIntMapDecorator(TIntIntMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TIntIntMap getMap() {
    return this._map;
  }
  
  public Integer put(Integer key, Integer value) {
    int k, v;
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
    int v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Integer remove(Object key) {
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
    int v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Integer, Integer>> entrySet() {
    return new AbstractSet<Map.Entry<Integer, Integer>>() {
        public int size() {
          return TIntIntMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TIntIntMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TIntIntMapDecorator.this.containsKey(k) && TIntIntMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Integer, Integer>> iterator() {
          return new Iterator<Map.Entry<Integer, Integer>>() {
              private final TIntIntIterator it = TIntIntMapDecorator.this._map.iterator();
              
              public Map.Entry<Integer, Integer> next() {
                this.it.advance();
                int ik = this.it.key();
                final Integer key = (ik == TIntIntMapDecorator.this._map.getNoEntryKey()) ? null : TIntIntMapDecorator.this.wrapKey(ik);
                int iv = this.it.value();
                final Integer v = (iv == TIntIntMapDecorator.this._map.getNoEntryValue()) ? null : TIntIntMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Integer, Integer>() {
                    private Integer val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Integer getKey() {
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
                      return TIntIntMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Integer, Integer> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Integer key = (Integer)((Map.Entry)o).getKey();
            TIntIntMapDecorator.this._map.remove(TIntIntMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Integer, Integer>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TIntIntMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Integer && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Integer, ? extends Integer> map) {
    Iterator<? extends Map.Entry<? extends Integer, ? extends Integer>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Integer, ? extends Integer> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Integer wrapKey(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapKey(Object key) {
    return ((Integer)key).intValue();
  }
  
  protected Integer wrapValue(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapValue(Object value) {
    return ((Integer)value).intValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TIntIntMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
