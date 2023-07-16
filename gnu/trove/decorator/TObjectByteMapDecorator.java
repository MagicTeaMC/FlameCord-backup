package gnu.trove.decorator;

import gnu.trove.iterator.TObjectByteIterator;
import gnu.trove.map.TObjectByteMap;
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

public class TObjectByteMapDecorator<K> extends AbstractMap<K, Byte> implements Map<K, Byte>, Externalizable, Cloneable {
  static final long serialVersionUID = 1L;
  
  protected TObjectByteMap<K> _map;
  
  public TObjectByteMapDecorator() {}
  
  public TObjectByteMapDecorator(TObjectByteMap<K> map) {
    Objects.requireNonNull(map);
    this._map = map;
  }
  
  public TObjectByteMap<K> getMap() {
    return this._map;
  }
  
  public Byte put(K key, Byte value) {
    if (value == null)
      return wrapValue(this._map.put(key, this._map.getNoEntryValue())); 
    return wrapValue(this._map.put(key, unwrapValue(value)));
  }
  
  public Byte get(Object key) {
    byte v = this._map.get(key);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public void clear() {
    this._map.clear();
  }
  
  public Byte remove(Object key) {
    byte v = this._map.remove(key);
    if (v == this._map.getNoEntryValue())
      return null; 
    return wrapValue(v);
  }
  
  public Set<Map.Entry<K, Byte>> entrySet() {
    return new AbstractSet<Map.Entry<K, Byte>>() {
        public int size() {
          return TObjectByteMapDecorator.this._map.size();
        }
        
        public boolean isEmpty() {
          return TObjectByteMapDecorator.this.isEmpty();
        }
        
        public boolean contains(Object o) {
          if (o instanceof Map.Entry) {
            Object k = ((Map.Entry)o).getKey();
            Object v = ((Map.Entry)o).getValue();
            return (TObjectByteMapDecorator.this.containsKey(k) && TObjectByteMapDecorator.this
              .get(k).equals(v));
          } 
          return false;
        }
        
        public Iterator<Map.Entry<K, Byte>> iterator() {
          return new Iterator<Map.Entry<K, Byte>>() {
              private final TObjectByteIterator<K> it = TObjectByteMapDecorator.this._map.iterator();
              
              public Map.Entry<K, Byte> next() {
                this.it.advance();
                final K key = (K)this.it.key();
                final Byte v = TObjectByteMapDecorator.this.wrapValue(this.it.value());
                return new Map.Entry<K, Byte>() {
                    private Byte val = v;
                    
                    public boolean equals(Object o) {
                      return (o instanceof Map.Entry && ((Map.Entry)o)
                        .getKey().equals(key) && ((Map.Entry)o)
                        .getValue().equals(this.val));
                    }
                    
                    public K getKey() {
                      return (K)key;
                    }
                    
                    public Byte getValue() {
                      return this.val;
                    }
                    
                    public int hashCode() {
                      return key.hashCode() + this.val.hashCode();
                    }
                    
                    public Byte setValue(Byte value) {
                      this.val = value;
                      return TObjectByteMapDecorator.this.put(key, value);
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
        
        public boolean add(Map.Entry<K, Byte> o) {
          throw new UnsupportedOperationException();
        }
        
        public boolean remove(Object o) {
          boolean modified = false;
          if (contains(o)) {
            K key = (K)((Map.Entry)o).getKey();
            TObjectByteMapDecorator.this._map.remove(key);
            modified = true;
          } 
          return modified;
        }
        
        public boolean addAll(Collection<? extends Map.Entry<K, Byte>> c) {
          throw new UnsupportedOperationException();
        }
        
        public void clear() {
          TObjectByteMapDecorator.this.clear();
        }
      };
  }
  
  public boolean containsValue(Object val) {
    return (val instanceof Byte && this._map.containsValue(unwrapValue(val)));
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
  
  public void putAll(Map<? extends K, ? extends Byte> map) {
    Iterator<? extends Map.Entry<? extends K, ? extends Byte>> it = map.entrySet().iterator();
    for (int i = map.size(); i-- > 0; ) {
      Map.Entry<? extends K, ? extends Byte> e = it.next();
      put(e.getKey(), e.getValue());
    } 
  }
  
  protected Byte wrapValue(byte k) {
    return Byte.valueOf(k);
  }
  
  protected byte unwrapValue(Object value) {
    return ((Byte)value).byteValue();
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._map = (TObjectByteMap<K>)in.readObject();
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeObject(this._map);
  }
}
