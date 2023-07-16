package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TLongCollection;
import gnu.trove.function.TCharFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TLongCharHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TLongCharIterator;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.map.TLongCharMap;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TLongCharProcedure;
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

public class TLongCharHashMap extends TLongCharHash implements TLongCharMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient char[] _values;
  
  public TLongCharHashMap() {}
  
  public TLongCharHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TLongCharHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TLongCharHashMap(int initialCapacity, float loadFactor, long noEntryKey, char noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TLongCharHashMap(long[] keys, char[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TLongCharHashMap(TLongCharMap map) {
    super(map.size());
    if (map instanceof TLongCharHashMap) {
      TLongCharHashMap hashmap = (TLongCharHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != 0L)
        Arrays.fill(this._set, this.no_entry_key); 
      if (this.no_entry_value != '\000')
        Arrays.fill(this._values, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    putAll(map);
  }
  
  protected int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = new char[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    long[] oldKeys = this._set;
    char[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new long[newCapacity];
    this._values = new char[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        long o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public char put(long key, char value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public char putIfAbsent(long key, char value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private char doPut(long key, char value, int index) {
    char previous = this.no_entry_value;
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
  
  public void putAll(Map<? extends Long, ? extends Character> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Long, ? extends Character> entry : map.entrySet())
      put(((Long)entry.getKey()).longValue(), ((Character)entry.getValue()).charValue()); 
  }
  
  public void putAll(TLongCharMap map) {
    ensureCapacity(map.size());
    TLongCharIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public char get(long key) {
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
  
  public char remove(long key) {
    char prev = this.no_entry_value;
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
  
  public TCharCollection valueCollection() {
    return new TValueView();
  }
  
  public char[] values() {
    char[] vals = new char[size()];
    if (vals.length == 0)
      return vals; 
    char[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public char[] values(char[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new char[size]; 
    char[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = v[i]; 
    } 
    return array;
  }
  
  public boolean containsValue(char val) {
    byte[] states = this._states;
    char[] vals = this._values;
    for (int i = vals.length; i-- > 0;) {
      if (states[i] == 1 && val == vals[i])
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(long key) {
    return contains(key);
  }
  
  public TLongCharIterator iterator() {
    return new TLongCharHashIterator(this);
  }
  
  public boolean forEachKey(TLongProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TCharProcedure procedure) {
    byte[] states = this._states;
    char[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TLongCharProcedure procedure) {
    byte[] states = this._states;
    long[] keys = this._set;
    char[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public void transformValues(TCharFunction function) {
    byte[] states = this._states;
    char[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = function.execute(values[i]); 
    } 
  }
  
  public boolean retainEntries(TLongCharProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    long[] keys = this._set;
    char[] values = this._values;
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
    return adjustValue(key, '\001');
  }
  
  public boolean adjustValue(long key, char amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = (char)(this._values[index] + amount);
    return true;
  }
  
  public char adjustOrPutValue(long key, char adjust_amount, char put_amount) {
    boolean isNewMapping;
    char newValue;
    int index = insertKey(key);
    if (index < 0) {
      index = -index - 1;
      newValue = this._values[index] = (char)(this._values[index] + adjust_amount);
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
  
  protected class TKeyView implements TLongSet {
    public TLongIterator iterator() {
      return new TLongCharHashMap.TLongCharKeyHashIterator((TPrimitiveHash)TLongCharHashMap.this);
    }
    
    public long getNoEntryValue() {
      return TLongCharHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TLongCharHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TLongCharHashMap.this._size);
    }
    
    public boolean contains(long entry) {
      return TLongCharHashMap.this.contains(entry);
    }
    
    public long[] toArray() {
      return TLongCharHashMap.this.keys();
    }
    
    public long[] toArray(long[] dest) {
      return TLongCharHashMap.this.keys(dest);
    }
    
    public boolean add(long entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(long entry) {
      return (TLongCharHashMap.this.no_entry_value != TLongCharHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Long) {
          long ele = ((Long)element).longValue();
          if (!TLongCharHashMap.this.containsKey(ele))
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
        if (!TLongCharHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(long[] array) {
      for (long element : array) {
        if (!TLongCharHashMap.this.contains(element))
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
      long[] set = TLongCharHashMap.this._set;
      byte[] states = TLongCharHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TLongCharHashMap.this.removeAt(i);
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
      TLongCharHashMap.this.clear();
    }
    
    public boolean forEach(TLongProcedure procedure) {
      return TLongCharHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TLongSet))
        return false; 
      TLongSet that = (TLongSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TLongCharHashMap.this._states.length; i-- > 0;) {
        if (TLongCharHashMap.this._states[i] == 1 && 
          !that.contains(TLongCharHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TLongCharHashMap.this._states.length; i-- > 0;) {
        if (TLongCharHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TLongCharHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TLongCharHashMap.this.forEachKey(new TLongProcedure() {
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
  
  protected class TValueView implements TCharCollection {
    public TCharIterator iterator() {
      return new TLongCharHashMap.TLongCharValueHashIterator((TPrimitiveHash)TLongCharHashMap.this);
    }
    
    public char getNoEntryValue() {
      return TLongCharHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TLongCharHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TLongCharHashMap.this._size);
    }
    
    public boolean contains(char entry) {
      return TLongCharHashMap.this.containsValue(entry);
    }
    
    public char[] toArray() {
      return TLongCharHashMap.this.values();
    }
    
    public char[] toArray(char[] dest) {
      return TLongCharHashMap.this.values(dest);
    }
    
    public boolean add(char entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(char entry) {
      char[] values = TLongCharHashMap.this._values;
      byte[] states = TLongCharHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TLongCharHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Character) {
          char ele = ((Character)element).charValue();
          if (!TLongCharHashMap.this.containsValue(ele))
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
        if (!TLongCharHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(char[] array) {
      for (char element : array) {
        if (!TLongCharHashMap.this.containsValue(element))
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
      char[] values = TLongCharHashMap.this._values;
      byte[] states = TLongCharHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TLongCharHashMap.this.removeAt(i);
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
      TLongCharHashMap.this.clear();
    }
    
    public boolean forEach(TCharProcedure procedure) {
      return TLongCharHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TLongCharHashMap.this.forEachValue(new TCharProcedure() {
            private boolean first = true;
            
            public boolean execute(char value) {
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
  
  class TLongCharKeyHashIterator extends THashPrimitiveIterator implements TLongIterator {
    TLongCharKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public long next() {
      moveToNextIndex();
      return TLongCharHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TLongCharHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TLongCharValueHashIterator extends THashPrimitiveIterator implements TCharIterator {
    TLongCharValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public char next() {
      moveToNextIndex();
      return TLongCharHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TLongCharHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TLongCharHashIterator extends THashPrimitiveIterator implements TLongCharIterator {
    TLongCharHashIterator(TLongCharHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public long key() {
      return TLongCharHashMap.this._set[this._index];
    }
    
    public char value() {
      return TLongCharHashMap.this._values[this._index];
    }
    
    public char setValue(char val) {
      char old = value();
      TLongCharHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TLongCharHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TLongCharMap))
      return false; 
    TLongCharMap that = (TLongCharMap)other;
    if (that.size() != size())
      return false; 
    char[] values = this._values;
    byte[] states = this._states;
    char this_no_entry_value = getNoEntryValue();
    char that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        long key = this._set[i];
        if (!that.containsKey(key))
          return false; 
        char that_value = that.get(key);
        char this_value = values[i];
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
    forEachEntry(new TLongCharProcedure() {
          private boolean first = true;
          
          public boolean execute(long key, char value) {
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
        out.writeChar(this._values[i]);
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
      char val = in.readChar();
      put(key, val);
    } 
  }
}
