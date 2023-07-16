package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TIntFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatIntHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TFloatIntIterator;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TFloatIntMap;
import gnu.trove.procedure.TFloatIntProcedure;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TIntProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TFloatIntHashMap extends TFloatIntHash implements TFloatIntMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient int[] _values;
  
  public TFloatIntHashMap() {}
  
  public TFloatIntHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TFloatIntHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TFloatIntHashMap(int initialCapacity, float loadFactor, float noEntryKey, int noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TFloatIntHashMap(float[] keys, int[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TFloatIntHashMap(TFloatIntMap map) {
    super(map.size());
    if (map instanceof TFloatIntHashMap) {
      TFloatIntHashMap hashmap = (TFloatIntHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != 0.0F)
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
    float[] oldKeys = this._set;
    int[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new float[newCapacity];
    this._values = new int[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        float o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public int put(float key, int value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public int putIfAbsent(float key, int value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private int doPut(float key, int value, int index) {
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
  
  public void putAll(Map<? extends Float, ? extends Integer> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Float, ? extends Integer> entry : map.entrySet())
      put(((Float)entry.getKey()).floatValue(), ((Integer)entry.getValue()).intValue()); 
  }
  
  public void putAll(TFloatIntMap map) {
    ensureCapacity(map.size());
    TFloatIntIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public int get(float key) {
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
  
  public int remove(float key) {
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
  
  public TFloatSet keySet() {
    return new TKeyView();
  }
  
  public float[] keys() {
    float[] keys = new float[size()];
    if (keys.length == 0)
      return keys; 
    float[] k = this._set;
    byte[] states = this._states;
    for (int i = k.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        keys[j++] = k[i]; 
    } 
    return keys;
  }
  
  public float[] keys(float[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new float[size]; 
    float[] keys = this._set;
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
  
  public boolean containsKey(float key) {
    return contains(key);
  }
  
  public TFloatIntIterator iterator() {
    return new TFloatIntHashIterator(this);
  }
  
  public boolean forEachKey(TFloatProcedure procedure) {
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
  
  public boolean forEachEntry(TFloatIntProcedure procedure) {
    byte[] states = this._states;
    float[] keys = this._set;
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
  
  public boolean retainEntries(TFloatIntProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    float[] keys = this._set;
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
  
  public boolean increment(float key) {
    return adjustValue(key, 1);
  }
  
  public boolean adjustValue(float key, int amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = this._values[index] + amount;
    return true;
  }
  
  public int adjustOrPutValue(float key, int adjust_amount, int put_amount) {
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
  
  protected class TKeyView implements TFloatSet {
    public TFloatIterator iterator() {
      return new TFloatIntHashMap.TFloatIntKeyHashIterator((TPrimitiveHash)TFloatIntHashMap.this);
    }
    
    public float getNoEntryValue() {
      return TFloatIntHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TFloatIntHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TFloatIntHashMap.this._size);
    }
    
    public boolean contains(float entry) {
      return TFloatIntHashMap.this.contains(entry);
    }
    
    public float[] toArray() {
      return TFloatIntHashMap.this.keys();
    }
    
    public float[] toArray(float[] dest) {
      return TFloatIntHashMap.this.keys(dest);
    }
    
    public boolean add(float entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(float entry) {
      return (TFloatIntHashMap.this.no_entry_value != TFloatIntHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Float) {
          float ele = ((Float)element).floatValue();
          if (!TFloatIntHashMap.this.containsKey(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TFloatCollection collection) {
      TFloatIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TFloatIntHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(float[] array) {
      for (float element : array) {
        if (!TFloatIntHashMap.this.contains(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Float> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TFloatCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(float[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TFloatIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Float.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TFloatCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TFloatIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(float[] array) {
      boolean changed = false;
      Arrays.sort(array);
      float[] set = TFloatIntHashMap.this._set;
      byte[] states = TFloatIntHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TFloatIntHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Float) {
          float c = ((Float)element).floatValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TFloatCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TFloatIterator iter = collection.iterator();
      while (iter.hasNext()) {
        float element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(float[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TFloatIntHashMap.this.clear();
    }
    
    public boolean forEach(TFloatProcedure procedure) {
      return TFloatIntHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TFloatSet))
        return false; 
      TFloatSet that = (TFloatSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TFloatIntHashMap.this._states.length; i-- > 0;) {
        if (TFloatIntHashMap.this._states[i] == 1 && 
          !that.contains(TFloatIntHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TFloatIntHashMap.this._states.length; i-- > 0;) {
        if (TFloatIntHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TFloatIntHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TFloatIntHashMap.this.forEachKey(new TFloatProcedure() {
            private boolean first = true;
            
            public boolean execute(float key) {
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
      return new TFloatIntHashMap.TFloatIntValueHashIterator((TPrimitiveHash)TFloatIntHashMap.this);
    }
    
    public int getNoEntryValue() {
      return TFloatIntHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TFloatIntHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TFloatIntHashMap.this._size);
    }
    
    public boolean contains(int entry) {
      return TFloatIntHashMap.this.containsValue(entry);
    }
    
    public int[] toArray() {
      return TFloatIntHashMap.this.values();
    }
    
    public int[] toArray(int[] dest) {
      return TFloatIntHashMap.this.values(dest);
    }
    
    public boolean add(int entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(int entry) {
      int[] values = TFloatIntHashMap.this._values;
      byte[] states = TFloatIntHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TFloatIntHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Integer) {
          int ele = ((Integer)element).intValue();
          if (!TFloatIntHashMap.this.containsValue(ele))
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
        if (!TFloatIntHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(int[] array) {
      for (int element : array) {
        if (!TFloatIntHashMap.this.containsValue(element))
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
      int[] values = TFloatIntHashMap.this._values;
      byte[] states = TFloatIntHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TFloatIntHashMap.this.removeAt(i);
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
      TFloatIntHashMap.this.clear();
    }
    
    public boolean forEach(TIntProcedure procedure) {
      return TFloatIntHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TFloatIntHashMap.this.forEachValue(new TIntProcedure() {
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
  
  class TFloatIntKeyHashIterator extends THashPrimitiveIterator implements TFloatIterator {
    TFloatIntKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public float next() {
      moveToNextIndex();
      return TFloatIntHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TFloatIntHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TFloatIntValueHashIterator extends THashPrimitiveIterator implements TIntIterator {
    TFloatIntValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public int next() {
      moveToNextIndex();
      return TFloatIntHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TFloatIntHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TFloatIntHashIterator extends THashPrimitiveIterator implements TFloatIntIterator {
    TFloatIntHashIterator(TFloatIntHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public float key() {
      return TFloatIntHashMap.this._set[this._index];
    }
    
    public int value() {
      return TFloatIntHashMap.this._values[this._index];
    }
    
    public int setValue(int val) {
      int old = value();
      TFloatIntHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TFloatIntHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TFloatIntMap))
      return false; 
    TFloatIntMap that = (TFloatIntMap)other;
    if (that.size() != size())
      return false; 
    int[] values = this._values;
    byte[] states = this._states;
    int this_no_entry_value = getNoEntryValue();
    int that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        float key = this._set[i];
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
    forEachEntry(new TFloatIntProcedure() {
          private boolean first = true;
          
          public boolean execute(float key, int value) {
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
        out.writeFloat(this._set[i]);
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
      float key = in.readFloat();
      int val = in.readInt();
      put(key, val);
    } 
  }
}
