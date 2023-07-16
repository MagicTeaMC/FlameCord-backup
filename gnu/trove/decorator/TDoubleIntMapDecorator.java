package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleIntIterator;
import gnu.trove.map.TDoubleIntMap;
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

public class TDoubleIntMapDecorator extends AbstractMap<Double, Integer> implements Map<Double, Integer>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TDoubleIntMap _map;
  
  public TDoubleIntMapDecorator() {}
  
  public TDoubleIntMapDecorator(TDoubleIntMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TDoubleIntMap getMap() {
    return this._map;
  }
  
  public Integer put(Double key, Integer value) {
    double k;
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
    double k;
    if (key != null) {
      if (key instanceof Double) {
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
    double k;
    if (key != null) {
      if (key instanceof Double) {
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
  
  public Set<Map.Entry<Double, Integer>> entrySet() {
    return new AbstractSet<Map.Entry<Double, Integer>>() {
        public int size() {
          return TDoubleIntMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TDoubleIntMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TDoubleIntMapDecorator.this.containsKey(k) && TDoubleIntMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Double, Integer>> iterator() {
          return new Iterator<Map.Entry<Double, Integer>>() {
              private final TDoubleIntIterator it = TDoubleIntMapDecorator.this._map.iterator();
              
              public Map.Entry<Double, Integer> next() {
                this.it.advance();
                double ik = this.it.key();
                final Double key = (ik == TDoubleIntMapDecorator.this._map.getNoEntryKey()) ? null : TDoubleIntMapDecorator.this.wrapKey(ik);
                int iv = this.it.value();
                final Integer v = (iv == TDoubleIntMapDecorator.this._map.getNoEntryValue()) ? null : TDoubleIntMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Double, Integer>() {
                    private Integer val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Double getKey() {
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
                      return TDoubleIntMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Double, Integer> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Double key = (Double)((Map.Entry)o).getKey();
            TDoubleIntMapDecorator.this._map.remove(TDoubleIntMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Double, Integer>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TDoubleIntMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Integer && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Double && this._map.containsKey(unwrapKey(key)));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Double, ? extends Integer> map) {
    Iterator<? extends Map.Entry<? extends Double, ? extends Integer>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Double, ? extends Integer> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Double wrapKey(double k) {
    return Double.valueOf(k);
  }
  
  protected double unwrapKey(Object key) {
    return ((Double)key).doubleValue();
  }
  
  protected Integer wrapValue(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapValue(Object value) {
    return ((Integer)value).intValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TDoubleIntMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
