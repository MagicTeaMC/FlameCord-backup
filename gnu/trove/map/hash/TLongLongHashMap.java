package gnu.trove.map.hash;

import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongLongHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.iterator.TLongLongIterator;
import gnu.trove.map.TLongLongMap;
import gnu.trove.procedure.TLongLongProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TLongSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TLongLongHashMap extends TLongLongHash implements TLongLongMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient long[] _values;
  
  public TLongLongHashMap() {}
  
  public TLongLongHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TLongLongHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TLongLongHashMap(int initialCapacity, float loadFactor, long noEntryKey, long noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TLongLongHashMap(long[] keys, long[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TLongLongHashMap(TLongLongMap map) {
    super(map.size());
    if (map instanceof TLongLongHashMap) {
      TLongLongHashMap hashmap = (TLongLongHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != 0L)
        Arrays.fill(this._set, this.no_entry_key); 
      if (this.no_entry_value != 0L)
        Arrays.fill(this._values, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    putAll(map);
  }
  
  protected int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = new long[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    long[] oldKeys = this._set;
    long[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new long[newCapacity];
    this._values = new long[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        long o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public long put(long key, long value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public long putIfAbsent(long key, long value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private long doPut(long key, long value, int index) {
    long previous = this.no_entry_value;
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
  
  public void putAll(Map<? extends Long, ? extends Long> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Long, ? extends Long> entry : map.entrySet())
      put(((Long)entry.getKey()).longValue(), ((Long)entry.getValue()).longValue()); 
  }
  
  public void putAll(TLongLongMap map) {
    ensureCapacity(map.size());
    TLongLongIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public long get(long key) {
    int index = index(key);
    return (index < 0) ? this.no_entry_value : this._values[index];
  }
  
  public void clear() {
    super.clear();
    Arrays.fill(this._set, 0, this._set.length, this.no_entry_key);
    Arrays.fill(this._values, 0, this._values.length, this.no_entry_value);
    Arrays.fill(this._states, 0, this._states.length, (byte)0);
  }
  
  public boolean isEmpty() {
    return (0 == this._size);
  }
  
  public long remove(long key) {
    long prev = this.no_entry_value;
    int index = index(key);
    if (index >= 0) {
      prev = this._values[index];
      removeAt(index);
    } 
    return prev;
  }
  
  protected void removeAt(int index) {
    this._values[index] = this.no_entry_value;
    super.removeAt(index);
  }
  
  public TLongSet keySet() {
    return new TKeyView();
  }
  
  public long[] keys() {
    long[] keys = new long[size()];
    if (keys.length == 0)
      return keys; 
    long[] k = this._set;
    byte[] states = this._states;
    for (int i = k.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        keys[j++] = k[i]; 
    } 
    return keys;
  }
  
  public long[] keys(long[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new long[size]; 
    long[] keys = this._set;
    byte[] states = this._states;
    for (int i = keys.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = keys[i]; 
    } 
    return array;
  }
  
  public TLongCollection valueCollection() {
    return new TValueView();
  }
  
  public long[] values() {
    long[] vals = new long[size()];
    if (vals.length == 0)
      return vals; 
    long[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public long[] values(long[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new long[size]; 
    long[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = v[i]; 
    } 
    return array;
  }
  
  public boolean containsValue(long val) {
    byte[] states = this._states;
    long[] vals = this._values;
    for (int i = vals.length; i-- > 0;) {
      if (states[i] == 1 && val == vals[i])
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(long key) {
    return contains(key);
  }
  
  public TLongLongIterator iterator() {
    return new TLongLongHashIterator(this);
  }
  
  public boolean forEachKey(TLongProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TLongProcedure procedure) {
    byte[] states = this._states;
    long[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TLongLongProcedure procedure) {
    byte[] states = this._states;
    long[] keys = this._set;
    long[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public void transformValues(TLongFunction function) {
    byte[] states = this._states;
    long[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = function.execute(values[i]); 
    } 
  }
  
  public boolean retainEntries(TLongLongProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    long[] keys = this._set;
    long[] values = this._values;
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
  
  public boolean increment(long key) {
    return adjustValue(key, 1L);
  }
  
  public boolean adjustValue(long key, long amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = this._values[index] + amount;
    return true;
  }
  
  public long adjustOrPutValue(long key, long adjust_amount, long put_amount) {
    int index = insertKey(key);
    index = -index - 1;
    long newValue = this._values[index] = this._values[index] + adjust_amount;
    boolean isNewMapping = false;
    newValue = this._values[index] = put_amount;
    isNewMapping = true;
    byte previousState = this._states[index];
    if (isNewMapping)
      postInsertHook(this.consumeFreeSlot); 
    return newValue;
  }
  
  protected class TKeyView implements TLongSet {
    public TLongIterator iterator() {
      return new TLongLongHashMap.TLongLongKeyHashIterator((TPrimitiveHash)TLongLongHashMap.this);
    }
    
    public long getNoEntryValue() {
      return TLongLongHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TLongLongHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TLongLongHashMap.this._size);
    }
    
    public boolean contains(long entry) {
      return TLongLongHashMap.this.contains(entry);
    }
    
    public long[] toArray() {
      return TLongLongHashMap.this.keys();
    }
    
    public long[] toArray(long[] dest) {
      return TLongLongHashMap.this.keys(dest);
    }
    
    public boolean add(long entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(long entry) {
      return (TLongLongHashMap.this.no_entry_value != TLongLongHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Long) {
          long ele = ((Long)element).longValue();
          if (!TLongLongHashMap.this.containsKey(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TLongCollection collection) {
      TLongIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TLongLongHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(long[] array) {
      for (long element : array) {
        if (!TLongLongHashMap.this.contains(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Long> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TLongCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(long[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TLongIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Long.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TLongCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TLongIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(long[] array) {
      boolean changed = false;
      Arrays.sort(array);
      long[] set = TLongLongHashMap.this._set;
      byte[] states = TLongLongHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TLongLongHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Long) {
          long c = ((Long)element).longValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TLongCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TLongIterator iter = collection.iterator();
      while (iter.hasNext()) {
        long element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(long[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TLongLongHashMap.this.clear();
    }
    
    public boolean forEach(TLongProcedure procedure) {
      return TLongLongHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TLongSet))
        return false; 
      TLongSet that = (TLongSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TLongLongHashMap.this._states.length; i-- > 0;) {
        if (TLongLongHashMap.this._states[i] == 1 && 
          !that.contains(TLongLongHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TLongLongHashMap.this._states.length; i-- > 0;) {
        if (TLongLongHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TLongLongHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TLongLongHashMap.this.forEachKey(new TLongProcedure() {
            private boolean first = true;
            
            public boolean execute(long key) {
              if (this.first) {
                this.first = false;
              } else {
                buf.append(", ");
              } 
              buf.append(key);
              return true;
            }
          });
      buf.append("}");
      return buf.toString();
    }
  }
  
  protected class TValueView implements TLongCollection {
    public TLongIterator iterator() {
      return new TLongLongHashMap.TLongLongValueHashIterator((TPrimitiveHash)TLongLongHashMap.this);
    }
    
    public long getNoEntryValue() {
      return TLongLongHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TLongLongHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TLongLongHashMap.this._size);
    }
    
    public boolean contains(long entry) {
      return TLongLongHashMap.this.containsValue(entry);
    }
    
    public long[] toArray() {
      return TLongLongHashMap.this.values();
    }
    
    public long[] toArray(long[] dest) {
      return TLongLongHashMap.this.values(dest);
    }
    
    public boolean add(long entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(long entry) {
      long[] values = TLongLongHashMap.this._values;
      byte[] states = TLongLongHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TLongLongHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Long) {
          long ele = ((Long)element).longValue();
          if (!TLongLongHashMap.this.containsValue(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TLongCollection collection) {
      TLongIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TLongLongHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(long[] array) {
      for (long element : array) {
        if (!TLongLongHashMap.this.containsValue(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Long> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TLongCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(long[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TLongIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Long.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TLongCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TLongIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(long[] array) {
      boolean changed = false;
      Arrays.sort(array);
      long[] values = TLongLongHashMap.this._values;
      byte[] states = TLongLongHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TLongLongHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Long) {
          long c = ((Long)element).longValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TLongCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TLongIterator iter = collection.iterator();
      while (iter.hasNext()) {
        long element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(long[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TLongLongHashMap.this.clear();
    }
    
    public boolean forEach(TLongProcedure procedure) {
      return TLongLongHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TLongLongHashMap.this.forEachValue(new TLongProcedure() {
            private boolean first = true;
            
            public boolean execute(long value) {
              if (this.first) {
                this.first = false;
              } else {
                buf.append(", ");
              } 
              buf.append(value);
              return true;
            }
          });
      buf.append("}");
      return buf.toString();
    }
  }
  
  class TLongLongKeyHashIterator extends THashPrimitiveIterator implements TLongIterator {
    TLongLongKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public long next() {
      moveToNextIndex();
      return TLongLongHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TLongLongHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TLongLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {
    TLongLongValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public long next() {
      moveToNextIndex();
      return TLongLongHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TLongLongHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TLongLongHashIterator extends THashPrimitiveIterator implements TLongLongIterator {
    TLongLongHashIterator(TLongLongHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public long key() {
      return TLongLongHashMap.this._set[this._index];
    }
    
    public long value() {
      return TLongLongHashMap.this._values[this._index];
    }
    
    public long setValue(long val) {
      long old = value();
      TLongLongHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TLongLongHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TLongLongMap))
      return false; 
    TLongLongMap that = (TLongLongMap)other;
    if (that.size() != size())
      return false; 
    long[] values = this._values;
    byte[] states = this._states;
    long this_no_entry_value = getNoEntryValue();
    long that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        long key = this._set[i];
        if (!that.containsKey(key))
          return false; 
        long that_value = that.get(key);
        long this_value = values[i];
        if (this_value != that_value && (this_value != this_no_entry_value || that_value != that_no_entry_value))
          return false; 
      } 
    } 
    return true;
  }
  
  public int hashCode() {
    int hashcode = 0;
    byte[] states = this._states;
    for (int i = this._values.length; i-- > 0;) {
      if (states[i] == 1)
        hashcode += HashFunctions.hash(this._set[i]) ^ 
          HashFunctions.hash(this._values[i]); 
    } 
    return hashcode;
  }
  
  public String toString() {
    final StringBuilder buf = new StringBuilder("{");
    forEachEntry(new TLongLongProcedure() {
          private boolean first = true;
          
          public boolean execute(long key, long value) {
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
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    super.writeExternal(out);
    out.writeInt(this._size);
    for (int i = this._states.length; i-- > 0;) {
      if (this._states[i] == 1) {
        out.writeLong(this._set[i]);
        out.writeLong(this._values[i]);
      } 
    } 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    super.readExternal(in);
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      long key = in.readLong();
      long val = in.readLong();
      put(key, val);
    } 
  }
}
