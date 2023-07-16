package gnu.trove.decorator;

import gnu.trove.iterator.TDoubleCharIterator;
import gnu.trove.map.TDoubleCharMap;
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

public class TDoubleCharMapDecorator extends AbstractMap<Double, Character> implements Map<Double, Character>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TDoubleCharMap _map;
  
  public TDoubleCharMapDecorator() {}
  
  public TDoubleCharMapDecorator(TDoubleCharMap map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TDoubleCharMap getMap() {
    return this._map;
  }
  
  public Character put(Double key, Character value) {
    double k;
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
    char v = this._map.get(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Character remove(Object key) {
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
    char v = this._map.remove(k);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<Double, Character>> entrySet() {
    return new AbstractSet<Map.Entry<Double, Character>>() {
        public int size() {
          return TDoubleCharMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TDoubleCharMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TDoubleCharMapDecorator.this.containsKey(k) && TDoubleCharMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<Double, Character>> iterator() {
          return new Iterator<Map.Entry<Double, Character>>() {
              private final TDoubleCharIterator it = TDoubleCharMapDecorator.this._map.iterator();
              
              public Map.Entry<Double, Character> next() {
                this.it.advance();
                double ik = this.it.key();
                final Double key = (ik == TDoubleCharMapDecorator.this._map.getNoEntryKey()) ? null : TDoubleCharMapDecorator.this.wrapKey(ik);
                char iv = this.it.value();
                final Character v = (iv == TDoubleCharMapDecorator.this._map.getNoEntryValue()) ? null : TDoubleCharMapDecorator.this.wrapValue(iv);
                return new Map.Entry<Double, Character>() {
                    private Character val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public Double getKey() {
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
                      return TDoubleCharMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<Double, Character> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            Double key = (Double)((Map.Entry)o).getKey();
            TDoubleCharMapDecorator.this._map.remove(TDoubleCharMapDecorator.this.unwrapKey(key));
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<Double, Character>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TDoubleCharMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Character && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends Double, ? extends Character> map) {
    Iterator<? extends Map.Entry<? extends Double, ? extends Character>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends Double, ? extends Character> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Double wrapKey(double k) {
    return Double.valueOf(k);
  }
  
  protected double unwrapKey(Object key) {
    return ((Double)key).doubleValue();
  }
  
  protected Character wrapValue(char k) {
    return Character.valueOf(k);
  }
  
  protected char unwrapValue(Object value) {
    return ((Character)value).charValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TDoubleCharMap)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
