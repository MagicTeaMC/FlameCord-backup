package gnu.trove.decorator;

import gnu.trove.iterator.TIntDoubleIterator;
import gnu.trove.map.TIntDoubleMap;
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

public class TIntDoubleMapDecorator extends AbstractMap<Integer, Double> implements Map<Integer, Double>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TIntDoubleMap _map;
  
  public TIntDoubleMapDecorator() {}
  
  public TIntDoubleMapDecorator(TIntDoubleMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TIntDoubleMap getMap() {
    return this._map;
  }
  
  public Double put(Integer key, Double value) {
    int k;
    double v;
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
    double retval = this._map.put(k, v);
    if (retval == this._map.getNoEntryValue())
      return null; 
    return wrapValue(retval);
  }
  
  public Double get(Object key) {
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
    double v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Double remove(Object key) {
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
    double v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Integer, Double>> entrySet() {
    return new AbstractSet<Map.Entry<Integer, Double>>() {
        public int size() {
          return TIntDoubleMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TIntDoubleMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TIntDoubleMapDecorator.this.containsKey(k) && TIntDoubleMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Integer, Double>> iterator() {
          return new Iterator<Map.Entry<Integer, Double>>() {
              private final TIntDoubleIterator it = TIntDoubleMapDecorator.this._map.iterator();
              
              public Map.Entry<Integer, Double> next() {
                this.it.advance();
                int ik = this.it.key();
                final Integer key = (ik == TIntDoubleMapDecorator.this._map.getNoEntryKey()) ? null : TIntDoubleMapDecorator.this.wrapKey(ik);
                double iv = this.it.value();
                final Double v = (iv == TIntDoubleMapDecorator.this._map.getNoEntryValue()) ? null : TIntDoubleMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Integer, Double>() {
                    private Double val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Integer getKey() {
                      return key;
                    }
                    
                    public Double getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Double setValue(Double value) {
                      this.val = value;
                      return TIntDoubleMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Integer, Double> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Integer key = (Integer)((Map.Entry)o).getKey();
            TIntDoubleMapDecorator.this._map.remove(TIntDoubleMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Integer, Double>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TIntDoubleMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Double && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Integer, ? extends Double> map) {
    Iterator<? extends Map.Entry<? extends Integer, ? extends Double>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Integer, ? extends Double> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Integer wrapKey(int k) {
    return Integer.valueOf(k);
  }
  
  protected int unwrapKey(Object key) {
    return ((Integer)key).intValue();
  }
  
  protected Double wrapValue(double k) {
    return Double.valueOf(k);
  }
  
  protected double unwrapValue(Object value) {
    return ((Double)value).doubleValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TIntDoubleMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
