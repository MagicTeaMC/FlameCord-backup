package gnu.trove.map.hash;

import gnu.trove.TShortCollection;
import gnu.trove.function.TObjectFunction;
import gnu.trove.impl.Constants;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortHash;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.iterator.TShortObjectIterator;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.procedure.TObjectProcedure;
import gnu.trove.procedure.TShortObjectProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Array;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TShortObjectHashMap<V> extends TShortHash implements TShortObjectMap<V>, Externalizable {
  static final long serialVersionUID = 1L;
  
  private final TShortObjectProcedure<V> PUT_ALL_PROC = new TShortObjectProcedure<V>() {
      public boolean execute(short key, V value) {
        TShortObjectHashMap.this.put(key, value);
        return true;
      }
    };
  
  protected transient V[] _values;
  
  protected short no_entry_key;
  
  public TShortObjectHashMap(int initialCapacity) {
    super(initialCapacity);
    this.no_entry_key = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
  }
  
  public TShortObjectHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
    this.no_entry_key = Constants.DEFAULT_SHORT_NO_ENTRY_VALUE;
  }
  
  public TShortObjectHashMap(int initialCapacity, float loadFactor, short noEntryKey) {
    super(initialCapacity, loadFactor);
    this.no_entry_key = noEntryKey;
  }
  
  public TShortObjectHashMap(TShortObjectMap<? extends V> map) {
    this(map.size(), 0.5F, map.getNoEntryKey());
    putAll(map);
  }
  
  protected int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = (V[])new Object[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    short[] oldKeys = this._set;
    V[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new short[newCapacity];
    this._values = (V[])new Object[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        short o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public short getNoEntryKey() {
    return this.no_entry_key;
  }
  
  public boolean containsKey(short key) {
    return contains(key);
  }
  
  public boolean containsValue(Object val) {
    byte[] states = this._states;
    V[] vals = this._values;
    if (null == val) {
      for (int i = vals.length; i-- > 0;) {
        if (states[i] == 1 && null == vals[i])
          return true; 
      } 
    } else {
      for (int i = vals.length; i-- > 0;) {
        if (states[i] == 1 && (val == vals[i] || val
          .equals(vals[i])))
          return true; 
      } 
    } 
    return false;
  }
  
  public V get(short key) {
    int index = index(key);
    return (index < 0) ? null : this._values[index];
  }
  
  public V put(short key, V value) {
    int index = insertKey(key);
    return doPut(value, index);
  }
  
  public V putIfAbsent(short key, V value) {
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
  
  public V remove(short key) {
    V prev = null;
    int index = index(key);
    if (index >= 0) {
      prev = this._values[index];
      removeAt(index);
    } 
    return prev;
  }
  
  protected void removeAt(int index) {
    this._values[index] = null;
    super.removeAt(index);
  }
  
  public void putAll(Map<? extends Short, ? extends V> map) {
    Set<? extends Map.Entry<? extends Short, ? extends V>> set = map.entrySet();
    for (Map.Entry<? extends Short, ? extends V> entry : set)
      put(((Short)entry.getKey()).shortValue(), entry.getValue()); 
  }
  
  public void putAll(TShortObjectMap<? extends V> map) {
    map.forEachEntry(this.PUT_ALL_PROC);
  }
  
  public void clear() {
    super.clear();
    Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
    Arrays.fill(this._states, 0, this._states.length, (byte)0);
    Arrays.fill((Object[])this._values, 0, this._values.length, (Object)null);
  }
  
  public TShortSet keySet() {
    return new KeyView();
  }
  
  public short[] keys() {
    short[] keys = new short[size()];
    short[] k = this._set;
    byte[] states = this._states;
    for (int i = k.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        keys[j++] = k[i]; 
    } 
    return keys;
  }
  
  public short[] keys(short[] dest) {
    if (dest.length < this._size)
      dest = new short[this._size]; 
    short[] k = this._set;
    byte[] states = this._states;
    for (int i = k.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        dest[j++] = k[i]; 
    } 
    return dest;
  }
  
  public Collection<V> valueCollection() {
    return new ValueView();
  }
  
  public Object[] values() {
    Object[] vals = new Object[size()];
    V[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public V[] values(V[] dest) {
    if (dest.length < this._size)
      dest = (V[])Array.newInstance(dest
          .getClass().getComponentType(), this._size); 
    V[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        dest[j++] = v[i]; 
    } 
    return dest;
  }
  
  public TShortObjectIterator<V> iterator() {
    return new TShortObjectHashIterator<V>(this);
  }
  
  public boolean forEachKey(TShortProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TObjectProcedure<? super V> procedure) {
    byte[] states = this._states;
    V[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TShortObjectProcedure<? super V> procedure) {
    byte[] states = this._states;
    short[] keys = this._set;
    V[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean retainEntries(TShortObjectProcedure<? super V> procedure) {
    boolean modified = false;
    byte[] states = this._states;
    short[] keys = this._set;
    V[] values = this._values;
    tempDisableAutoCompaction();
    try {
      for (int i = keys.length; i-- > 0;) {
        if (states[i] == 1 && !procedure.execute(keys[i], values[i])) {
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
    byte[] states = this._states;
    V[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = (V)function.execute(values[i]); 
    } 
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TShortObjectMap))
      return false; 
    TShortObjectMap that = (TShortObjectMap)other;
    if (that.size() != size())
      return false; 
    try {
      TShortObjectIterator<V> iter = iterator();
      while (iter.hasNext()) {
        iter.advance();
        short key = iter.key();
        Object value = iter.value();
        if (value == null) {
          if (that.get(key) != null || !that.containsKey(key))
            return false; 
          continue;
        } 
        if (!value.equals(that.get(key)))
          return false; 
      } 
    } catch (ClassCastException classCastException) {}
    return true;
  }
  
  public int hashCode() {
    int hashcode = 0;
    V[] values = this._values;
    byte[] states = this._states;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        hashcode += HashFunctions.hash(this._set[i]) ^ ((values[i] == null) ? 0 : values[i]
          .hashCode()); 
    } 
    return hashcode;
  }
  
  class KeyView implements TShortSet {
    public short getNoEntryValue() {
      return TShortObjectHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TShortObjectHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (TShortObjectHashMap.this._size == 0);
    }
    
    public boolean contains(short entry) {
      return TShortObjectHashMap.this.containsKey(entry);
    }
    
    public TShortIterator iterator() {
      return new TShortHashIterator(TShortObjectHashMap.this);
    }
    
    public short[] toArray() {
      return TShortObjectHashMap.this.keys();
    }
    
    public short[] toArray(short[] dest) {
      return TShortObjectHashMap.this.keys(dest);
    }
    
    public boolean add(short entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(short entry) {
      return (null != TShortObjectHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (!TShortObjectHashMap.this.containsKey(((Short)element)
            .shortValue()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(TShortCollection collection) {
      if (collection == this)
        return true; 
      TShortIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TShortObjectHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(short[] array) {
      for (short element : array) {
        if (!TShortObjectHashMap.this.containsKey(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Short> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TShortCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(short[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TShortIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Short.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TShortCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TShortIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(short[] array) {
      boolean changed = false;
      Arrays.sort(array);
      short[] set = TShortObjectHashMap.this._set;
      byte[] states = TShortObjectHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TShortObjectHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Short) {
          short c = ((Short)element).shortValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TShortCollection collection) {
      if (collection == this) {
        clear();
        return true;
      } 
      boolean changed = false;
      TShortIterator iter = collection.iterator();
      while (iter.hasNext()) {
        short element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(short[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TShortObjectHashMap.this.clear();
    }
    
    public boolean forEach(TShortProcedure procedure) {
      return TShortObjectHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TShortSet))
        return false; 
      TShortSet that = (TShortSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TShortObjectHashMap.this._states.length; i-- > 0;) {
        if (TShortObjectHashMap.this._states[i] == 1 && 
          !that.contains(TShortObjectHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TShortObjectHashMap.this._states.length; i-- > 0;) {
        if (TShortObjectHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TShortObjectHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      StringBuilder buf = new StringBuilder("{");
      boolean first = true;
      for (int i = TShortObjectHashMap.this._states.length; i-- > 0;) {
        if (TShortObjectHashMap.this._states[i] == 1) {
          if (first) {
            first = false;
          } else {
            buf.append(",");
          } 
          buf.append(TShortObjectHashMap.this._set[i]);
        } 
      } 
      return buf.toString();
    }
    
    class TShortHashIterator extends THashPrimitiveIterator implements TShortIterator {
      private final TShortHash _hash;
      
      public TShortHashIterator(TShortHash hash) {
        super((TPrimitiveHash)hash);
        this._hash = hash;
      }
      
      public short next() {
        moveToNextIndex();
        return this._hash._set[this._index];
      }
    }
  }
  
  protected class ValueView extends MapBackedView<V> {
    public Iterator<V> iterator() {
      return new TShortObjectValueHashIterator(TShortObjectHashMap.this) {
          protected V objectAtIndex(int index) {
            return TShortObjectHashMap.this._values[index];
          }
        };
    }
    
    public boolean containsElement(V value) {
      return TShortObjectHashMap.this.containsValue(value);
    }
    
    public boolean removeElement(V value) {
      V[] values = TShortObjectHashMap.this._values;
      byte[] states = TShortObjectHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && (
          value == values[i] || (null != values[i] && values[i]
          .equals(value)))) {
          TShortObjectHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    class TShortObjectValueHashIterator extends THashPrimitiveIterator implements Iterator<V> {
      protected final TShortObjectHashMap _map;
      
      public TShortObjectValueHashIterator(TShortObjectHashMap map) {
        super((TPrimitiveHash)map);
        this._map = map;
      }
      
      protected V objectAtIndex(int index) {
        byte[] states = TShortObjectHashMap.this._states;
        Object value = this._map._values[index];
        if (states[index] != 1)
          return null; 
        return (V)value;
      }
      
      public V next() {
        moveToNextIndex();
        return this._map._values[this._index];
      }
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
      TShortObjectHashMap.this.clear();
    }
    
    public boolean add(E obj) {
      throw new UnsupportedOperationException();
    }
    
    public int size() {
      return TShortObjectHashMap.this.size();
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
        a = (T[])Array.newInstance(a
            .getClass().getComponentType(), size); 
      Iterator<E> it = iterator();
      T[] arrayOfT = a;
      for (int i = 0; i < size; i++)
        arrayOfT[i] = (T)it.next(); 
      if (a.length > size)
        a[size] = null; 
      return a;
    }
    
    public boolean isEmpty() {
      return TShortObjectHashMap.this.isEmpty();
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
    
    public abstract Iterator<E> iterator();
    
    public abstract boolean removeElement(E param1E);
    
    public abstract boolean containsElement(E param1E);
  }
  
  class TShortObjectHashIterator<V> extends THashPrimitiveIterator implements TShortObjectIterator<V> {
    private final TShortObjectHashMap<V> _map;
    
    public TShortObjectHashIterator(TShortObjectHashMap<V> map) {
      super((TPrimitiveHash)map);
      this._map = map;
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public short key() {
      return this._map._set[this._index];
    }
    
    public V value() {
      return this._map._values[this._index];
    }
    
    public V setValue(V val) {
      V old = value();
      this._map._values[this._index] = val;
      return old;
    }
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    super.writeExternal(out);
    out.writeShort(this.no_entry_key);
    out.writeInt(this._size);
    for (int i = this._states.length; i-- > 0;) {
      if (this._states[i] == 1) {
        out.writeShort(this._set[i]);
        out.writeObject(this._values[i]);
      } 
    } 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    super.readExternal(in);
    this.no_entry_key = in.readShort();
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      short key = in.readShort();
      V val = (V)in.readObject();
      put(key, val);
    } 
  }
  
  public String toString() {
    final StringBuilder buf = new StringBuilder("{");
    forEachEntry(new TShortObjectProcedure<V>() {
          private boolean first = true;
          
          public boolean execute(short key, Object value) {
            if (this.first) {
              this.first = false;
            } else {
              buf.append(",");
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
  
  public TShortObjectHashMap() {}
}
