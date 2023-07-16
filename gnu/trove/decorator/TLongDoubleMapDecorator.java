package gnu.trove.decorator;

import gnu.trove.iterator.TLongDoubleIterator;
import gnu.trove.map.TLongDoubleMap;
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

public class TLongDoubleMapDecorator extends AbstractMap<Long, Double> implements Map<Long, Double>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TLongDoubleMap _map;
  
  public TLongDoubleMapDecorator() {}
  
  public TLongDoubleMapDecorator(TLongDoubleMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TLongDoubleMap getMap() {
    return this._map;
  }
  
  public Double put(Long key, Double value) {
    long k;
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
    long k;
    if (key != null) {
      if (key instanceof Long) {
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
    long k;
    if (key != null) {
      if (key instanceof Long) {
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
  
  public Set<Map.Entry<Long, Double>> entrySet() {
    return new AbstractSet<Map.Entry<Long, Double>>() {
        public int size() {
          return TLongDoubleMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TLongDoubleMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TLongDoubleMapDecorator.this.containsKey(k) && TLongDoubleMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Long, Double>> iterator() {
          return new Iterator<Map.Entry<Long, Double>>() {
              private final TLongDoubleIterator it = TLongDoubleMapDecorator.this._map.iterator();
              
              public Map.Entry<Long, Double> next() {
                this.it.advance();
                long ik = this.it.key();
                final Long key = (ik == TLongDoubleMapDecorator.this._map.getNoEntryKey()) ? null : TLongDoubleMapDecorator.this.wrapKey(ik);
                double iv = this.it.value();
                final Double v = (iv == TLongDoubleMapDecorator.this._map.getNoEntryValue()) ? null : TLongDoubleMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Long, Double>() {
                    private Double val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Long getKey() {
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
                      return TLongDoubleMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Long, Double> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Long key = (Long)((Map.Entry)o).getKey();
            TLongDoubleMapDecorator.this._map.remove(TLongDoubleMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Long, Double>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TLongDoubleMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Double && this._map.containsValue(unwrapValue(val)));
  }
  
  public boolean containsKey(Object key) {
    if (key == null)
      return this._map.containsKey(this._map.getNoEntryKey()); 
    return (key instanceof Long && this._map.containsKey(unwrapKey(key)));
  }
  
  public int size() {
    return this._map.size();
  }
  
  public boolean isEmpty() {
    return (size() == 0);
  }
  
  public void putAll(Map<? extends Long, ? extends Double> map) {
    Iterator<? extends Map.Entry<? extends Long, ? extends Double>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Long, ? extends Double> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Long wrapKey(long k) {
    return Long.valueOf(k);
  }
  
  protected long unwrapKey(Object key) {
    return ((Long)key).longValue();
  }
  
  protected Double wrapValue(double k) {
    return Double.valueOf(k);
  }
  
  protected double unwrapValue(Object value) {
    return ((Double)value).doubleValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TLongDoubleMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
