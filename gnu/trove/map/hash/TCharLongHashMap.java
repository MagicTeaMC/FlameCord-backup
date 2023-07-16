package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TLongFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharLongHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TCharLongIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TCharLongMap;
import gnu.trove.procedure.TCharLongProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongProcedure;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TCharLongHashMap extends TCharLongHash implements TCharLongMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient long[] _values;
  
  public TCharLongHashMap() {}
  
  public TCharLongHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TCharLongHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TCharLongHashMap(int initialCapacity, float loadFactor, char noEntryKey, long noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TCharLongHashMap(char[] keys, long[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TCharLongHashMap(TCharLongMap map) {
    super(map.size());
    if (map instanceof TCharLongHashMap) {
      TCharLongHashMap hashmap = (TCharLongHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != '\000')
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
    char[] oldKeys = this._set;
    long[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new char[newCapacity];
    this._values = new long[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        char o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public long put(char key, long value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public long putIfAbsent(char key, long value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private long doPut(char key, long value, int index) {
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
  
  public void putAll(Map<? extends Character, ? extends Long> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Character, ? extends Long> entry : map.entrySet())
      put(((Character)entry.getKey()).charValue(), ((Long)entry.getValue()).longValue()); 
  }
  
  public void putAll(TCharLongMap map) {
    ensureCapacity(map.size());
    TCharLongIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public long get(char key) {
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
  
  public long remove(char key) {
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
  
  public TCharSet keySet() {
    return new TKeyView();
  }
  
  public char[] keys() {
    char[] keys = new char[size()];
    if (keys.length == 0)
      return keys; 
    char[] k = this._set;
    byte[] states = this._states;
    for (int i = k.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        keys[j++] = k[i]; 
    } 
    return keys;
  }
  
  public char[] keys(char[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new char[size]; 
    char[] keys = this._set;
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
  
  public boolean containsKey(char key) {
    return contains(key);
  }
  
  public TCharLongIterator iterator() {
    return new TCharLongHashIterator(this);
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
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
  
  public boolean forEachEntry(TCharLongProcedure procedure) {
    byte[] states = this._states;
    char[] keys = this._set;
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
  
  public boolean retainEntries(TCharLongProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    char[] keys = this._set;
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
  
  public boolean increment(char key) {
    return adjustValue(key, 1L);
  }
  
  public boolean adjustValue(char key, long amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = this._values[index] + amount;
    return true;
  }
  
  public long adjustOrPutValue(char key, long adjust_amount, long put_amount) {
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
  
  protected class TKeyView implements TCharSet {
    public TCharIterator iterator() {
      return new TCharLongHashMap.TCharLongKeyHashIterator((TPrimitiveHash)TCharLongHashMap.this);
    }
    
    public char getNoEntryValue() {
      return TCharLongHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TCharLongHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TCharLongHashMap.this._size);
    }
    
    public boolean contains(char entry) {
      return TCharLongHashMap.this.contains(entry);
    }
    
    public char[] toArray() {
      return TCharLongHashMap.this.keys();
    }
    
    public char[] toArray(char[] dest) {
      return TCharLongHashMap.this.keys(dest);
    }
    
    public boolean add(char entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(char entry) {
      return (TCharLongHashMap.this.no_entry_value != TCharLongHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Character) {
          char ele = ((Character)element).charValue();
          if (!TCharLongHashMap.this.containsKey(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TCharCollection collection) {
      TCharIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TCharLongHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(char[] array) {
      for (char element : array) {
        if (!TCharLongHashMap.this.contains(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Character> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TCharCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(char[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TCharIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Character.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TCharCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TCharIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(char[] array) {
      boolean changed = false;
      Arrays.sort(array);
      char[] set = TCharLongHashMap.this._set;
      byte[] states = TCharLongHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TCharLongHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Character) {
          char c = ((Character)element).charValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TCharCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TCharIterator iter = collection.iterator();
      while (iter.hasNext()) {
        char element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(char[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TCharLongHashMap.this.clear();
    }
    
    public boolean forEach(TCharProcedure procedure) {
      return TCharLongHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TCharSet))
        return false; 
      TCharSet that = (TCharSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TCharLongHashMap.this._states.length; i-- > 0;) {
        if (TCharLongHashMap.this._states[i] == 1 && 
          !that.contains(TCharLongHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TCharLongHashMap.this._states.length; i-- > 0;) {
        if (TCharLongHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TCharLongHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TCharLongHashMap.this.forEachKey(new TCharProcedure() {
            private boolean first = true;
            
            public boolean execute(char key) {
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
      return new TCharLongHashMap.TCharLongValueHashIterator((TPrimitiveHash)TCharLongHashMap.this);
    }
    
    public long getNoEntryValue() {
      return TCharLongHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TCharLongHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TCharLongHashMap.this._size);
    }
    
    public boolean contains(long entry) {
      return TCharLongHashMap.this.containsValue(entry);
    }
    
    public long[] toArray() {
      return TCharLongHashMap.this.values();
    }
    
    public long[] toArray(long[] dest) {
      return TCharLongHashMap.this.values(dest);
    }
    
    public boolean add(long entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(long entry) {
      long[] values = TCharLongHashMap.this._values;
      byte[] states = TCharLongHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TCharLongHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Long) {
          long ele = ((Long)element).longValue();
          if (!TCharLongHashMap.this.containsValue(ele))
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
        if (!TCharLongHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(long[] array) {
      for (long element : array) {
        if (!TCharLongHashMap.this.containsValue(element))
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
      long[] values = TCharLongHashMap.this._values;
      byte[] states = TCharLongHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TCharLongHashMap.this.removeAt(i);
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
      TCharLongHashMap.this.clear();
    }
    
    public boolean forEach(TLongProcedure procedure) {
      return TCharLongHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TCharLongHashMap.this.forEachValue(new TLongProcedure() {
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
  
  class TCharLongKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {
    TCharLongKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public char next() {
      moveToNextIndex();
      return TCharLongHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharLongHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TCharLongValueHashIterator extends THashPrimitiveIterator implements TLongIterator {
    TCharLongValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public long next() {
      moveToNextIndex();
      return TCharLongHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharLongHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TCharLongHashIterator extends THashPrimitiveIterator implements TCharLongIterator {
    TCharLongHashIterator(TCharLongHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public char key() {
      return TCharLongHashMap.this._set[this._index];
    }
    
    public long value() {
      return TCharLongHashMap.this._values[this._index];
    }
    
    public long setValue(long val) {
      long old = value();
      TCharLongHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharLongHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TCharLongMap))
      return false; 
    TCharLongMap that = (TCharLongMap)other;
    if (that.size() != size())
      return false; 
    long[] values = this._values;
    byte[] states = this._states;
    long this_no_entry_value = getNoEntryValue();
    long that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        char key = this._set[i];
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
    forEachEntry(new TCharLongProcedure() {
          private boolean first = true;
          
          public boolean execute(char key, long value) {
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
        out.writeChar(this._set[i]);
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
      char key = in.readChar();
      long val = in.readLong();
      put(key, val);
    } 
  }
}
