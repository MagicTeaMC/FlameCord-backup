package gnu.trove.map.hash;

import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntIntHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TIntIntIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntIntMap;
import gnu.trove.procedure.TIntIntProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TIntSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TIntIntHashMap extends TIntIntHash implements TIntIntMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient int[] _values;
  
  public TIntIntHashMap() {}
  
  public TIntIntHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TIntIntHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TIntIntHashMap(int initialCapacity, float loadFactor, int noEntryKey, int noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TIntIntHashMap(int[] keys, int[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TIntIntHashMap(TIntIntMap map) {
    super(map.size());
    if (map instanceof TIntIntHashMap) {
      TIntIntHashMap hashmap = (TIntIntHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != 0)
        Arrays.fill(this._set, this.no_entry_key); 
      if (this.no_entry_value != 0)
        Arrays.fill(this._values, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    putAll(map);
  }
  
  protected int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = new int[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    int[] oldKeys = this._set;
    int[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new int[newCapacity];
    this._values = new int[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        int o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public int put(int key, int value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public int putIfAbsent(int key, int value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private int doPut(int key, int value, int index) {
    int previous = this.no_entry_value;
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
  
  public void putAll(Map<? extends Integer, ? extends Integer> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Integer, ? extends Integer> entry : map.entrySet())
      put(((Integer)entry.getKey()).intValue(), ((Integer)entry.getValue()).intValue()); 
  }
  
  public void putAll(TIntIntMap map) {
    ensureCapacity(map.size());
    TIntIntIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public int get(int key) {
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
  
  public int remove(int key) {
    int prev = this.no_entry_value;
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
  
  public TIntSet keySet() {
    return new TKeyView();
  }
  
  public int[] keys() {
    int[] keys = new int[size()];
    if (keys.length == 0)
      return keys; 
    int[] k = this._set;
    byte[] states = this._states;
    for (int i = k.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        keys[j++] = k[i]; 
    } 
    return keys;
  }
  
  public int[] keys(int[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new int[size]; 
    int[] keys = this._set;
    byte[] states = this._states;
    for (int i = keys.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = keys[i]; 
    } 
    return array;
  }
  
  public TIntCollection valueCollection() {
    return new TValueView();
  }
  
  public int[] values() {
    int[] vals = new int[size()];
    if (vals.length == 0)
      return vals; 
    int[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public int[] values(int[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new int[size]; 
    int[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = v[i]; 
    } 
    return array;
  }
  
  public boolean containsValue(int val) {
    byte[] states = this._states;
    int[] vals = this._values;
    for (int i = vals.length; i-- > 0;) {
      if (states[i] == 1 && val == vals[i])
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(int key) {
    return contains(key);
  }
  
  public TIntIntIterator iterator() {
    return new TIntIntHashIterator(this);
  }
  
  public boolean forEachKey(TIntProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TIntProcedure procedure) {
    byte[] states = this._states;
    int[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TIntIntProcedure procedure) {
    byte[] states = this._states;
    int[] keys = this._set;
    int[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public void transformValues(TIntFunction function) {
    byte[] states = this._states;
    int[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = function.execute(values[i]); 
    } 
  }
  
  public boolean retainEntries(TIntIntProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    int[] keys = this._set;
    int[] values = this._values;
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
  
  public boolean increment(int key) {
    return adjustValue(key, 1);
  }
  
  public boolean adjustValue(int key, int amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = this._values[index] + amount;
    return true;
  }
  
  public int adjustOrPutValue(int key, int adjust_amount, int put_amount) {
    boolean isNewMapping;
    int newValue, index = insertKey(key);
    if (index < 0) {
      index = -index - 1;
      newValue = this._values[index] = this._values[index] + adjust_amount;
      isNewMapping = false;
    } else {
      newValue = this._values[index] = put_amount;
      isNewMapping = true;
    } 
    byte previousState = this._states[index];
    if (isNewMapping)
      postInsertHook(this.consumeFreeSlot); 
    return newValue;
  }
  
  protected class TKeyView implements TIntSet {
    public TIntIterator iterator() {
      return new TIntIntHashMap.TIntIntKeyHashIterator((TPrimitiveHash)TIntIntHashMap.this);
    }
    
    public int getNoEntryValue() {
      return TIntIntHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TIntIntHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TIntIntHashMap.this._size);
    }
    
    public boolean contains(int entry) {
      return TIntIntHashMap.this.contains(entry);
    }
    
    public int[] toArray() {
      return TIntIntHashMap.this.keys();
    }
    
    public int[] toArray(int[] dest) {
      return TIntIntHashMap.this.keys(dest);
    }
    
    public boolean add(int entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(int entry) {
      return (TIntIntHashMap.this.no_entry_value != TIntIntHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Integer) {
          int ele = ((Integer)element).intValue();
          if (!TIntIntHashMap.this.containsKey(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TIntCollection collection) {
      TIntIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TIntIntHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(int[] array) {
      for (int element : array) {
        if (!TIntIntHashMap.this.contains(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Integer> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TIntCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(int[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TIntIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Integer.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TIntCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TIntIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(int[] array) {
      boolean changed = false;
      Arrays.sort(array);
      int[] set = TIntIntHashMap.this._set;
      byte[] states = TIntIntHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TIntIntHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Integer) {
          int c = ((Integer)element).intValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TIntCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TIntIterator iter = collection.iterator();
      while (iter.hasNext()) {
        int element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(int[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TIntIntHashMap.this.clear();
    }
    
    public boolean forEach(TIntProcedure procedure) {
      return TIntIntHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TIntSet))
        return false; 
      TIntSet that = (TIntSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TIntIntHashMap.this._states.length; i-- > 0;) {
        if (TIntIntHashMap.this._states[i] == 1 && 
          !that.contains(TIntIntHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TIntIntHashMap.this._states.length; i-- > 0;) {
        if (TIntIntHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TIntIntHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TIntIntHashMap.this.forEachKey(new TIntProcedure() {
            private boolean first = true;
            
            public boolean execute(int key) {
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
  
  protected class TValueView implements TIntCollection {
    public TIntIterator iterator() {
      return new TIntIntHashMap.TIntIntValueHashIterator((TPrimitiveHash)TIntIntHashMap.this);
    }
    
    public int getNoEntryValue() {
      return TIntIntHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TIntIntHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TIntIntHashMap.this._size);
    }
    
    public boolean contains(int entry) {
      return TIntIntHashMap.this.containsValue(entry);
    }
    
    public int[] toArray() {
      return TIntIntHashMap.this.values();
    }
    
    public int[] toArray(int[] dest) {
      return TIntIntHashMap.this.values(dest);
    }
    
    public boolean add(int entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(int entry) {
      int[] values = TIntIntHashMap.this._values;
      byte[] states = TIntIntHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TIntIntHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Integer) {
          int ele = ((Integer)element).intValue();
          if (!TIntIntHashMap.this.containsValue(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TIntCollection collection) {
      TIntIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TIntIntHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(int[] array) {
      for (int element : array) {
        if (!TIntIntHashMap.this.containsValue(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Integer> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TIntCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(int[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TIntIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Integer.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TIntCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TIntIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(int[] array) {
      boolean changed = false;
      Arrays.sort(array);
      int[] values = TIntIntHashMap.this._values;
      byte[] states = TIntIntHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TIntIntHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Integer) {
          int c = ((Integer)element).intValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TIntCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TIntIterator iter = collection.iterator();
      while (iter.hasNext()) {
        int element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(int[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TIntIntHashMap.this.clear();
    }
    
    public boolean forEach(TIntProcedure procedure) {
      return TIntIntHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TIntIntHashMap.this.forEachValue(new TIntProcedure() {
            private boolean first = true;
            
            public boolean execute(int value) {
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
  
  class TIntIntKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {
    TIntIntKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public int next() {
      moveToNextIndex();
      return TIntIntHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TIntIntHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TIntIntValueHashIterator extends THashPrimitiveIterator implements TIntIterator {
    TIntIntValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public int next() {
      moveToNextIndex();
      return TIntIntHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TIntIntHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TIntIntHashIterator extends THashPrimitiveIterator implements TIntIntIterator {
    TIntIntHashIterator(TIntIntHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public int key() {
      return TIntIntHashMap.this._set[this._index];
    }
    
    public int value() {
      return TIntIntHashMap.this._values[this._index];
    }
    
    public int setValue(int val) {
      int old = value();
      TIntIntHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TIntIntHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TIntIntMap))
      return false; 
    TIntIntMap that = (TIntIntMap)other;
    if (that.size() != size())
      return false; 
    int[] values = this._values;
    byte[] states = this._states;
    int this_no_entry_value = getNoEntryValue();
    int that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        int key = this._set[i];
        if (!that.containsKey(key))
          return false; 
        int that_value = that.get(key);
        int this_value = values[i];
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
    forEachEntry(new TIntIntProcedure() {
          private boolean first = true;
          
          public boolean execute(int key, int value) {
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
        out.writeInt(this._set[i]);
        out.writeInt(this._values[i]);
      } 
    } 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    super.readExternal(in);
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      int key = in.readInt();
      int val = in.readInt();
      put(key, val);
    } 
  }
}
