package gnu.trove.map.hash;

import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCustomObjectHash;
import gnu.trove.impl.hash.TObjectHash;
import gnu.trove.iterator.hash.TObjectHashIterator;
import gnu.trove.map.TMap;
import gnu.trove.procedure.TObjectObjectProcedure;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.strategy.HashingStrategy;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TCustomHashMap<K, V> extends TCustomObjectHash<K> implements TMap<K, V>, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient V[] _values;
  
  public TCustomHashMap() {}
  
  public TCustomHashMap(HashingStrategy<? super K> strategy) {
    super(strategy);
  }
  
  public TCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity) {
    super(strategy, initialCapacity);
  }
  
  public TCustomHashMap(HashingStrategy<? super K> strategy, int initialCapacity, float loadFactor) {
    super(strategy, initialCapacity, loadFactor);
  }
  
  public TCustomHashMap(HashingStrategy<? super K> strategy, Map<? extends K, ? extends V> map) {
    this(strategy, map.size());
    putAll(map);
  }
  
  public TCustomHashMap(HashingStrategy<? super K> strategy, TCustomHashMap<? extends K, ? extends V> map) {
    this(strategy, map.size());
    putAll((Map<? extends K, ? extends V>)map);
  }
  
  public int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = (V[])new Object[capacity];
    return capacity;
  }
  
  public V put(K key, V value) {
    int index = insertKey(key);
    return doPut(value, index);
  }
  
  public V putIfAbsent(K key, V value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(value, index);
  }
  
  private V doPut(V value, int index) {
    V previous = null;
    boolean isNewMapping = true;
    if (index < 0) {
      index = -index - 1;
      previous = this._values[index];
      isNewMapping = false;
    } 
    this._values[index] = value;
    if (isNewMapping)
      postInsertHook(this.consumeFreeSlot); 
    return previous;
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof Map))
      return false; 
    Map<K, V> that = (Map<K, V>)other;
    if (that.size() != size())
      return false; 
    return forEachEntry(new EqProcedure<K, V>(that));
  }
  
  public int hashCode() {
    HashProcedure p = new HashProcedure();
    forEachEntry(p);
    return p.getHashCode();
  }
  
  public String toString() {
    final StringBuilder buf = new StringBuilder("{");
    forEachEntry(new TObjectObjectProcedure<K, V>() {
          private boolean first = true;
          
          public boolean execute(K key, V value) {
            if (this.first) {
              this.first = false;
            } else {
              buf.append(", ");
            } 
            buf.append(key);
            buf.append("=");
            buf.append(value);
            return true;
          }
        });
    buf.append("}");
    return buf.toString();
  }
  
  private final class HashProcedure implements TObjectObjectProcedure<K, V> {
    private int h = 0;
    
    public int getHashCode() {
      return this.h;
    }
    
    public final boolean execute(K key, V value) {
      this.h += HashFunctions.hash(key) ^ ((value == null) ? 0 : value.hashCode());
      return true;
    }
    
    private HashProcedure() {}
  }
  
  private static final class EqProcedure<K, V> implements TObjectObjectProcedure<K, V> {
    private final Map<K, V> _otherMap;
    
    EqProcedure(Map<K, V> otherMap) {
      this._otherMap = otherMap;
    }
    
    public final boolean execute(K key, V value) {
      if (value == null && !this._otherMap.containsKey(key))
        return false; 
      V oValue = this._otherMap.get(key);
      return (oValue == value || (oValue != null && oValue.equals(value)));
    }
  }
  
  public boolean forEachKey(TObjectProcedure<? super K> procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    V[] values = this._values;
    Object[] set = this._set;
    for (int i = values.length; i-- > 0;) {
      if (set[i] != FREE && set[i] != REMOVED && 
        
        !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TObjectObjectProcedure<? super K, ? super V> procedure) {
    Object[] keys = this._set;
    V[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (keys[i] != FREE && keys[i] != REMOVED && 
        
        !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean retainEntries(TObjectObjectProcedure<? super K, ? super V> procedure) {
    boolean modified = false;
    Object[] keys = this._set;
    V[] values = this._values;
    tempDisableAutoCompaction();
    try {
      for (int i = keys.length; i-- > 0;) {
        if (keys[i] != FREE && keys[i] != REMOVED && 
          
          !procedure.execute(keys[i], values[i])) {
          removeAt(i);
          modified = true;
        } 
      } 
    } finally {
      reenableAutoCompaction(true);
    } 
    return modified;
  }
  
  public void transformValues(TObjectFunction<V, V> function) {
    V[] values = this._values;
    Object[] set = this._set;
    for (int i = values.length; i-- > 0;) {
      if (set[i] != FREE && set[i] != REMOVED)
        values[i] = (V)function.execute(values[i]); 
    } 
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    int oldSize = size();
    Object[] oldKeys = this._set;
    V[] oldVals = this._values;
    this._set = new Object[newCapacity];
    Arrays.fill(this._set, FREE);
    this._values = (V[])new Object[newCapacity];
    for (int i = oldCapacity; i-- > 0; ) {
      Object o = oldKeys[i];
      if (o == FREE || o == REMOVED)
        continue; 
      int index = insertKey(o);
      if (index < 0)
        throwObjectContractViolation(this._set[-index - 1], o, size(), oldSize, oldKeys); 
      this._values[index] = oldVals[i];
    } 
  }
  
  public V get(Object key) {
    int index = index(key);
    if (index < 0 || !this.strategy.equals(this._set[index], key))
      return null; 
    return this._values[index];
  }
  
  public void clear() {
    if (size() == 0)
      return; 
    super.clear();
    Arrays.fill(this._set, 0, this._set.length, FREE);
    Arrays.fill((Object[])this._values, 0, this._values.length, (Object)null);
  }
  
  public V remove(Object key) {
    V prev = null;
    int index = index(key);
    if (index >= 0) {
      prev = this._values[index];
      removeAt(index);
    } 
    return prev;
  }
  
  public void removeAt(int index) {
    this._values[index] = null;
    super.removeAt(index);
  }
  
  public Collection<V> values() {
    return new ValueView();
  }
  
  public Set<K> keySet() {
    return new KeyView();
  }
  
  public Set<Map.Entry<K, V>> entrySet() {
    return new EntryView();
  }
  
  public boolean containsValue(Object val) {
    Object[] set = this._set;
    V[] vals = this._values;
    if (null == val) {
      for (int i = vals.length; i-- > 0;) {
        if (set[i] != FREE && set[i] != REMOVED && val == vals[i])
          return true; 
      } 
    } else {
      for (int i = vals.length; i-- > 0;) {
        if (set[i] != FREE && set[i] != REMOVED && (val == vals[i] || this.strategy
          .equals(val, vals[i])))
          return true; 
      } 
    } 
    return false;
  }
  
  public boolean containsKey(Object key) {
    return contains(key);
  }
  
  public void putAll(Map<? extends K, ? extends V> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends K, ? extends V> e : map.entrySet())
      put(e.getKey(), e.getValue()); 
  }
  
  protected class ValueView extends MapBackedView<V> {
    public Iterator<V> iterator() {
      return (Iterator<V>)new TObjectHashIterator((TObjectHash)TCustomHashMap.this) {
          protected V objectAtIndex(int index) {
            return TCustomHashMap.this._values[index];
          }
        };
    }
    
    public boolean containsElement(V value) {
      return TCustomHashMap.this.containsValue(value);
    }
    
    public boolean removeElement(V value) {
      V[] arrayOfV = TCustomHashMap.this._values;
      Object[] set = TCustomHashMap.this._set;
      for (int i = arrayOfV.length; i-- > 0;) {
        if ((set[i] != TObjectHash.FREE && set[i] != TObjectHash.REMOVED && value == arrayOfV[i]) || (null != arrayOfV[i] && TCustomHashMap.this
          
          .strategy.equals(arrayOfV[i], value))) {
          TCustomHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
  }
  
  protected class EntryView extends MapBackedView<Map.Entry<K, V>> {
    private final class EntryIterator extends TObjectHashIterator {
      EntryIterator(TCustomHashMap<K, V> map) {
        super((TObjectHash)map);
      }
      
      public TCustomHashMap<K, V>.Entry objectAtIndex(int index) {
        return new TCustomHashMap.Entry((K)TCustomHashMap.this._set[index], TCustomHashMap.this._values[index], index);
      }
    }
    
    public Iterator<Map.Entry<K, V>> iterator() {
      return (Iterator<Map.Entry<K, V>>)new EntryIterator(TCustomHashMap.this);
    }
    
    public boolean removeElement(Map.Entry<K, V> entry) {
      K key = keyForEntry(entry);
      int index = TCustomHashMap.this.index(key);
      if (index >= 0) {
        Object val = valueForEntry(entry);
        if (val == TCustomHashMap.this._values[index] || (null != val && TCustomHashMap.this
          .strategy.equals(val, TCustomHashMap.this._values[index]))) {
          TCustomHashMap.this.removeAt(index);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsElement(Map.Entry<K, V> entry) {
      Object val = TCustomHashMap.this.get(keyForEntry(entry));
      Object entryValue = entry.getValue();
      return (entryValue == val || (null != val && TCustomHashMap.this
        .strategy.equals(val, entryValue)));
    }
    
    protected V valueForEntry(Map.Entry<K, V> entry) {
      return entry.getValue();
    }
    
    protected K keyForEntry(Map.Entry<K, V> entry) {
      return entry.getKey();
    }
  }
  
  private abstract class MapBackedView<E> extends AbstractSet<E> implements Set<E>, Iterable<E> {
    private MapBackedView() {}
    
    public boolean contains(Object key) {
      return containsElement((E)key);
    }
    
    public boolean remove(Object o) {
      return removeElement((E)o);
    }
    
    public void clear() {
      TCustomHashMap.this.clear();
    }
    
    public boolean add(E obj) {
      throw new UnsupportedOperationException();
    }
    
    public int size() {
      return TCustomHashMap.this.size();
    }
    
    public Object[] toArray() {
      Object[] result = new Object[size()];
      Iterator<E> e = iterator();
      for (int i = 0; e.hasNext(); i++)
        result[i] = e.next(); 
      return result;
    }
    
    public <T> T[] toArray(T[] a) {
      int size = size();
      if (a.length < size)
        a = (T[])Array.newInstance(a.getClass().getComponentType(), size); 
      Iterator<E> it = iterator();
      T[] arrayOfT = a;
      for (int i = 0; i < size; i++)
        arrayOfT[i] = (T)it.next(); 
      if (a.length > size)
        a[size] = null; 
      return a;
    }
    
    public boolean isEmpty() {
      return TCustomHashMap.this.isEmpty();
    }
    
    public boolean addAll(Collection<? extends E> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean changed = false;
      Iterator<E> i = iterator();
      while (i.hasNext()) {
        if (!collection.contains(i.next())) {
          i.remove();
          changed = true;
        } 
      } 
      return changed;
    }
    
    public String toString() {
      Iterator<E> i = iterator();
      if (!i.hasNext())
        return "{}"; 
      StringBuilder sb = new StringBuilder();
      sb.append('{');
      while (true) {
        E e = i.next();
        sb.append((e == this) ? "(this Collection)" : e);
        if (!i.hasNext())
          return sb.append('}').toString(); 
        sb.append(", ");
      } 
    }
    
    public abstract Iterator<E> iterator();
    
    public abstract boolean removeElement(E param1E);
    
    public abstract boolean containsElement(E param1E);
  }
  
  protected class KeyView extends MapBackedView<K> {
    public Iterator<K> iterator() {
      return (Iterator<K>)new TObjectHashIterator((TObjectHash)TCustomHashMap.this);
    }
    
    public boolean removeElement(K key) {
      return (null != TCustomHashMap.this.remove(key));
    }
    
    public boolean containsElement(K key) {
      return TCustomHashMap.this.contains(key);
    }
  }
  
  final class Entry implements Map.Entry<K, V> {
    private K key;
    
    private V val;
    
    private final int index;
    
    Entry(K key, V value, int index) {
      this.key = key;
      this.val = value;
      this.index = index;
    }
    
    public K getKey() {
      return this.key;
    }
    
    public V getValue() {
      return this.val;
    }
    
    public V setValue(V o) {
      if (TCustomHashMap.this._values[this.index] != this.val)
        throw new ConcurrentModificationException(); 
      V retval = this.val;
      TCustomHashMap.this._values[this.index] = o;
      this.val = o;
      return retval;
    }
    
    public boolean equals(Object o) {
      if (o instanceof Map.Entry) {
        Map.Entry<K, V> e1 = this;
        Map.Entry e2 = (Map.Entry)o;
        if ((e1.getKey() == null) ? (e2.getKey() == null) : TCustomHashMap.this
          .strategy.equals(e1.getKey(), e2.getKey()))
          if ((e1.getValue() == null) ? (e2.getValue() == null) : e1
            .getValue().equals(e2.getValue())); 
        return false;
      } 
      return false;
    }
    
    public int hashCode() {
      return ((getKey() == null) ? 0 : getKey().hashCode()) ^ ((getValue() == null) ? 0 : getValue().hashCode());
    }
    
    public String toString() {
      return (new StringBuilder()).append(this.key).append("=").append(this.val).toString();
    }
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(1);
    super.writeExternal(out);
    out.writeInt(this._size);
    for (int i = this._set.length; i-- > 0;) {
      if (this._set[i] != REMOVED && this._set[i] != FREE) {
        out.writeObject(this._set[i]);
        out.writeObject(this._values[i]);
      } 
    } 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    byte version = in.readByte();
    if (version != 0)
      super.readExternal(in); 
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      K key = (K)in.readObject();
      V val = (V)in.readObject();
      put(key, val);
    } 
  }
}
