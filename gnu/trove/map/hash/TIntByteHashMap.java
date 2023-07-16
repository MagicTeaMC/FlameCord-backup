package gnu.trove.map.hash;

import gnu.trove.TByteCollection;
import gnu.trove.TIntCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TIntByteHash;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.iterator.TIntByteIterator;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.map.TIntByteMap;
import gnu.trove.procedure.TByteProcedure;
import gnu.trove.procedure.TIntByteProcedure;
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

public class TIntByteHashMap extends TIntByteHash implements TIntByteMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient byte[] _values;
  
  public TIntByteHashMap() {}
  
  public TIntByteHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TIntByteHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TIntByteHashMap(int initialCapacity, float loadFactor, int noEntryKey, byte noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TIntByteHashMap(int[] keys, byte[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TIntByteHashMap(TIntByteMap map) {
    super(map.size());
    if (map instanceof TIntByteHashMap) {
      TIntByteHashMap hashmap = (TIntByteHashMap)map;
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
    this._values = new byte[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    int[] oldKeys = this._set;
    byte[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new int[newCapacity];
    this._values = new byte[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        int o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public byte put(int key, byte value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public byte putIfAbsent(int key, byte value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private byte doPut(int key, byte value, int index) {
    byte previous = this.no_entry_value;
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
  
  public void putAll(Map<? extends Integer, ? extends Byte> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Integer, ? extends Byte> entry : map.entrySet())
      put(((Integer)entry.getKey()).intValue(), ((Byte)entry.getValue()).byteValue()); 
  }
  
  public void putAll(TIntByteMap map) {
    ensureCapacity(map.size());
    TIntByteIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public byte get(int key) {
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
  
  public byte remove(int key) {
    byte prev = this.no_entry_value;
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
  
  public TByteCollection valueCollection() {
    return new TValueView();
  }
  
  public byte[] values() {
    byte[] vals = new byte[size()];
    if (vals.length == 0)
      return vals; 
    byte[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public byte[] values(byte[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new byte[size]; 
    byte[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = v[i]; 
    } 
    return array;
  }
  
  public boolean containsValue(byte val) {
    byte[] states = this._states;
    byte[] vals = this._values;
    for (int i = vals.length; i-- > 0;) {
      if (states[i] == 1 && val == vals[i])
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(int key) {
    return contains(key);
  }
  
  public TIntByteIterator iterator() {
    return new TIntByteHashIterator(this);
  }
  
  public boolean forEachKey(TIntProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TByteProcedure procedure) {
    byte[] states = this._states;
    byte[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TIntByteProcedure procedure) {
    byte[] states = this._states;
    int[] keys = this._set;
    byte[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public void transformValues(TByteFunction function) {
    byte[] states = this._states;
    byte[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = function.execute(values[i]); 
    } 
  }
  
  public boolean retainEntries(TIntByteProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    int[] keys = this._set;
    byte[] values = this._values;
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
    return adjustValue(key, (byte)1);
  }
  
  public boolean adjustValue(int key, byte amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = (byte)(this._values[index] + amount);
    return true;
  }
  
  public byte adjustOrPutValue(int key, byte adjust_amount, byte put_amount) {
    boolean isNewMapping;
    byte newValue;
    int index = insertKey(key);
    if (index < 0) {
      index = -index - 1;
      newValue = this._values[index] = (byte)(this._values[index] + adjust_amount);
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
      return new TIntByteHashMap.TIntByteKeyHashIterator((TPrimitiveHash)TIntByteHashMap.this);
    }
    
    public int getNoEntryValue() {
      return TIntByteHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TIntByteHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TIntByteHashMap.this._size);
    }
    
    public boolean contains(int entry) {
      return TIntByteHashMap.this.contains(entry);
    }
    
    public int[] toArray() {
      return TIntByteHashMap.this.keys();
    }
    
    public int[] toArray(int[] dest) {
      return TIntByteHashMap.this.keys(dest);
    }
    
    public boolean add(int entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(int entry) {
      return (TIntByteHashMap.this.no_entry_value != TIntByteHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Integer) {
          int ele = ((Integer)element).intValue();
          if (!TIntByteHashMap.this.containsKey(ele))
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
        if (!TIntByteHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(int[] array) {
      for (int element : array) {
        if (!TIntByteHashMap.this.contains(element))
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
      int[] set = TIntByteHashMap.this._set;
      byte[] states = TIntByteHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TIntByteHashMap.this.removeAt(i);
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
      TIntByteHashMap.this.clear();
    }
    
    public boolean forEach(TIntProcedure procedure) {
      return TIntByteHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TIntSet))
        return false; 
      TIntSet that = (TIntSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TIntByteHashMap.this._states.length; i-- > 0;) {
        if (TIntByteHashMap.this._states[i] == 1 && 
          !that.contains(TIntByteHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TIntByteHashMap.this._states.length; i-- > 0;) {
        if (TIntByteHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TIntByteHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TIntByteHashMap.this.forEachKey(new TIntProcedure() {
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
  
  protected class TValueView implements TByteCollection {
    public TByteIterator iterator() {
      return new TIntByteHashMap.TIntByteValueHashIterator((TPrimitiveHash)TIntByteHashMap.this);
    }
    
    public byte getNoEntryValue() {
      return TIntByteHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TIntByteHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TIntByteHashMap.this._size);
    }
    
    public boolean contains(byte entry) {
      return TIntByteHashMap.this.containsValue(entry);
    }
    
    public byte[] toArray() {
      return TIntByteHashMap.this.values();
    }
    
    public byte[] toArray(byte[] dest) {
      return TIntByteHashMap.this.values(dest);
    }
    
    public boolean add(byte entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(byte entry) {
      byte[] values = TIntByteHashMap.this._values;
      byte[] states = TIntByteHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TIntByteHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Byte) {
          byte ele = ((Byte)element).byteValue();
          if (!TIntByteHashMap.this.containsValue(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TByteCollection collection) {
      TByteIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TIntByteHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(byte[] array) {
      for (byte element : array) {
        if (!TIntByteHashMap.this.containsValue(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Byte> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TByteCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(byte[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TByteIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Byte.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TByteCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TByteIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(byte[] array) {
      boolean changed = false;
      Arrays.sort(array);
      byte[] values = TIntByteHashMap.this._values;
      byte[] states = TIntByteHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TIntByteHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Byte) {
          byte c = ((Byte)element).byteValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TByteCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TByteIterator iter = collection.iterator();
      while (iter.hasNext()) {
        byte element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(byte[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TIntByteHashMap.this.clear();
    }
    
    public boolean forEach(TByteProcedure procedure) {
      return TIntByteHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TIntByteHashMap.this.forEachValue(new TByteProcedure() {
            private boolean first = true;
            
            public boolean execute(byte value) {
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
  
  class TIntByteKeyHashIterator extends THashPrimitiveIterator implements TIntIterator {
    TIntByteKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public int next() {
      moveToNextIndex();
      return TIntByteHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TIntByteHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TIntByteValueHashIterator extends THashPrimitiveIterator implements TByteIterator {
    TIntByteValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public byte next() {
      moveToNextIndex();
      return TIntByteHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TIntByteHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TIntByteHashIterator extends THashPrimitiveIterator implements TIntByteIterator {
    TIntByteHashIterator(TIntByteHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public int key() {
      return TIntByteHashMap.this._set[this._index];
    }
    
    public byte value() {
      return TIntByteHashMap.this._values[this._index];
    }
    
    public byte setValue(byte val) {
      byte old = value();
      TIntByteHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TIntByteHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TIntByteMap))
      return false; 
    TIntByteMap that = (TIntByteMap)other;
    if (that.size() != size())
      return false; 
    byte[] values = this._values;
    byte[] states = this._states;
    byte this_no_entry_value = getNoEntryValue();
    byte that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        int key = this._set[i];
        if (!that.containsKey(key))
          return false; 
        byte that_value = that.get(key);
        byte this_value = values[i];
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
    forEachEntry(new TIntByteProcedure() {
          private boolean first = true;
          
          public boolean execute(int key, byte value) {
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
        out.writeByte(this._values[i]);
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
      byte val = in.readByte();
      put(key, val);
    } 
  }
}
