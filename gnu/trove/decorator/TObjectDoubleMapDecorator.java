package gnu.trove.decorator;

import gnu.trove.iterator.TObjectDoubleIterator;
import gnu.trove.map.TObjectDoubleMap;
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

public class TObjectDoubleMapDecorator<K> extends AbstractMap<K, Double> implements Map<K, Double>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TObjectDoubleMap<K> _map;
  
  public TObjectDoubleMapDecorator() {}
  
  public TObjectDoubleMapDecorator(TObjectDoubleMap<K> map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TObjectDoubleMap<K> getMap() {
    return this._map;
  }
  
  public Double put(K key, Double value) {
    if (value == null)
      return wrapValue(this._map.put(key, this._map.getNoEntryValue())); 
    return wrapValue(this._map.put(key, unwrapValue(value)));
  }
  
  public Double get(Object key) {
    double v = this._map.get(key);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Double remove(Object key) {
    double v = this._map.remove(key);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<K, Double>> entrySet() {
    return new AbstractSet<Map.Entry<K, Double>>() {
        public int size() {
          return TObjectDoubleMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TObjectDoubleMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TObjectDoubleMapDecorator.this.containsKey(k) && TObjectDoubleMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<K, Double>> iterator() {
          return new Iterator<Map.Entry<K, Double>>() {
              private final TObjectDoubleIterator<K> it = TObjectDoubleMapDecorator.this._map.iterator();
              
              public Map.Entry<K, Double> next() {
                this.it.advance();
                final K key = (K)this.it.key();
                final Double v = TObjectDoubleMapDecorator.this.wrapValue(this.it.value());
                return new Map.Entry<K, Double>() {
                    private Double val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public K getKey() {
                      return (K)key;
                    }
                    
                    public Double getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Double setValue(Double value) {
                      this.val = value;
                      return TObjectDoubleMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<K, Double> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            K key = (K)((Map.Entry)o).getKey();
            TObjectDoubleMapDecorator.this._map.remove(key);
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<K, Double>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TObjectDoubleMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Double && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends K, ? extends Double> map) {
    Iterator<? extends Map.Entry<? extends K, ? extends Double>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends K, ? extends Double> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Double wrapValue(double k) {
    return Double.valueOf(k);
  }
  
  protected double unwrapValue(Object value) {
    return ((Double)value).doubleValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TObjectDoubleMap<K>)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
