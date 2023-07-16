package gnu.trove.decorator;

import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
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

public class TCharDoubleMapDecorator extends AbstractMap<Character, Double> implements Map<Character, Double>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TCharDoubleMap _map;
  
  public TCharDoubleMapDecorator() {}
  
  public TCharDoubleMapDecorator(TCharDoubleMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TCharDoubleMap getMap() {
    return this._map;
  }
  
  public Double put(Character key, Double value) {
    char k;
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
    double v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Double remove(Object key) {
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
    double v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Character, Double>> entrySet() {
    return new AbstractSet<Map.Entry<Character, Double>>() {
        public int size() {
          return TCharDoubleMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TCharDoubleMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TCharDoubleMapDecorator.this.containsKey(k) && TCharDoubleMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Character, Double>> iterator() {
          return new Iterator<Map.Entry<Character, Double>>() {
              private final TCharDoubleIterator it = TCharDoubleMapDecorator.this._map.iterator();
              
              public Map.Entry<Character, Double> next() {
                this.it.advance();
                char ik = this.it.key();
                final Character key = (ik == TCharDoubleMapDecorator.this._map.getNoEntryKey()) ? null : TCharDoubleMapDecorator.this.wrapKey(ik);
                double iv = this.it.value();
                final Double v = (iv == TCharDoubleMapDecorator.this._map.getNoEntryValue()) ? null : TCharDoubleMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Character, Double>() {
                    private Double val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Character getKey() {
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
                      return TCharDoubleMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Character, Double> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Character key = (Character)((Map.Entry)o).getKey();
            TCharDoubleMapDecorator.this._map.remove(TCharDoubleMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Character, Double>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TCharDoubleMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Double && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Character, ? extends Double> map) {
    Iterator<? extends Map.Entry<? extends Character, ? extends Double>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Character, ? extends Double> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Character wrapKey(char k) {
    return Character.valueOf(k);
  }
  
  protected char unwrapKey(Object key) {
    return ((Character)key).charValue();
  }
  
  protected Double wrapValue(double k) {
    return Double.valueOf(k);
  }
  
  protected double unwrapValue(Object value) {
    return ((Double)value).doubleValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TCharDoubleMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
