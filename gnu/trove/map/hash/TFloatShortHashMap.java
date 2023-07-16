package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TShortFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TFloatShortHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TFloatShortIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TFloatShortMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TFloatShortProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TFloatSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TFloatShortHashMap extends TFloatShortHash implements TFloatShortMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient short[] _values;
  
  public TFloatShortHashMap() {}
  
  public TFloatShortHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TFloatShortHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TFloatShortHashMap(int initialCapacity, float loadFactor, float noEntryKey, short noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TFloatShortHashMap(float[] keys, short[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TFloatShortHashMap(TFloatShortMap map) {
    super(map.size());
    if (map instanceof TFloatShortHashMap) {
      TFloatShortHashMap hashmap = (TFloatShortHashMap)map;
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
    this._values = new short[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    float[] oldKeys = this._set;
    short[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new float[newCapacity];
    this._values = new short[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        float o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public short put(float key, short value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public short putIfAbsent(float key, short value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private short doPut(float key, short value, int index) {
    short previous = this.no_entry_value;
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
  
  public void putAll(Map<? extends Float, ? extends Short> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Float, ? extends Short> entry : map.entrySet())
      put(((Float)entry.getKey()).floatValue(), ((Short)entry.getValue()).shortValue()); 
  }
  
  public void putAll(TFloatShortMap map) {
    ensureCapacity(map.size());
    TFloatShortIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public short get(float key) {
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
  
  public short remove(float key) {
    short prev = this.no_entry_value;
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
  
  public TShortCollection valueCollection() {
    return new TValueView();
  }
  
  public short[] values() {
    short[] vals = new short[size()];
    if (vals.length == 0)
      return vals; 
    short[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public short[] values(short[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new short[size]; 
    short[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = v[i]; 
    } 
    return array;
  }
  
  public boolean containsValue(short val) {
    byte[] states = this._states;
    short[] vals = this._values;
    for (int i = vals.length; i-- > 0;) {
      if (states[i] == 1 && val == vals[i])
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(float key) {
    return contains(key);
  }
  
  public TFloatShortIterator iterator() {
    return new TFloatShortHashIterator(this);
  }
  
  public boolean forEachKey(TFloatProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TShortProcedure procedure) {
    byte[] states = this._states;
    short[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TFloatShortProcedure procedure) {
    byte[] states = this._states;
    float[] keys = this._set;
    short[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public void transformValues(TShortFunction function) {
    byte[] states = this._states;
    short[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = function.execute(values[i]); 
    } 
  }
  
  public boolean retainEntries(TFloatShortProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    float[] keys = this._set;
    short[] values = this._values;
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
    return adjustValue(key, (short)1);
  }
  
  public boolean adjustValue(float key, short amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = (short)(this._values[index] + amount);
    return true;
  }
  
  public short adjustOrPutValue(float key, short adjust_amount, short put_amount) {
    boolean isNewMapping;
    short newValue;
    int index = insertKey(key);
    if (index < 0) {
      index = -index - 1;
      newValue = this._values[index] = (short)(this._values[index] + adjust_amount);
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
      return new TFloatShortHashMap.TFloatShortKeyHashIterator((TPrimitiveHash)TFloatShortHashMap.this);
    }
    
    public float getNoEntryValue() {
      return TFloatShortHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TFloatShortHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TFloatShortHashMap.this._size);
    }
    
    public boolean contains(float entry) {
      return TFloatShortHashMap.this.contains(entry);
    }
    
    public float[] toArray() {
      return TFloatShortHashMap.this.keys();
    }
    
    public float[] toArray(float[] dest) {
      return TFloatShortHashMap.this.keys(dest);
    }
    
    public boolean add(float entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(float entry) {
      return (TFloatShortHashMap.this.no_entry_value != TFloatShortHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Float) {
          float ele = ((Float)element).floatValue();
          if (!TFloatShortHashMap.this.containsKey(ele))
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
        if (!TFloatShortHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(float[] array) {
      for (float element : array) {
        if (!TFloatShortHashMap.this.contains(element))
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
      float[] set = TFloatShortHashMap.this._set;
      byte[] states = TFloatShortHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TFloatShortHashMap.this.removeAt(i);
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
      TFloatShortHashMap.this.clear();
    }
    
    public boolean forEach(TFloatProcedure procedure) {
      return TFloatShortHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TFloatSet))
        return false; 
      TFloatSet that = (TFloatSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TFloatShortHashMap.this._states.length; i-- > 0;) {
        if (TFloatShortHashMap.this._states[i] == 1 && 
          !that.contains(TFloatShortHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TFloatShortHashMap.this._states.length; i-- > 0;) {
        if (TFloatShortHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TFloatShortHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TFloatShortHashMap.this.forEachKey(new TFloatProcedure() {
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
  
  protected class TValueView implements TShortCollection {
    public TShortIterator iterator() {
      return new TFloatShortHashMap.TFloatShortValueHashIterator((TPrimitiveHash)TFloatShortHashMap.this);
    }
    
    public short getNoEntryValue() {
      return TFloatShortHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TFloatShortHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TFloatShortHashMap.this._size);
    }
    
    public boolean contains(short entry) {
      return TFloatShortHashMap.this.containsValue(entry);
    }
    
    public short[] toArray() {
      return TFloatShortHashMap.this.values();
    }
    
    public short[] toArray(short[] dest) {
      return TFloatShortHashMap.this.values(dest);
    }
    
    public boolean add(short entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(short entry) {
      short[] values = TFloatShortHashMap.this._values;
      byte[] states = TFloatShortHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TFloatShortHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Short) {
          short ele = ((Short)element).shortValue();
          if (!TFloatShortHashMap.this.containsValue(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TShortCollection collection) {
      TShortIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TFloatShortHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(short[] array) {
      for (short element : array) {
        if (!TFloatShortHashMap.this.containsValue(element))
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
      short[] values = TFloatShortHashMap.this._values;
      byte[] states = TFloatShortHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TFloatShortHashMap.this.removeAt(i);
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
      if (this == collection) {
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
      TFloatShortHashMap.this.clear();
    }
    
    public boolean forEach(TShortProcedure procedure) {
      return TFloatShortHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TFloatShortHashMap.this.forEachValue(new TShortProcedure() {
            private boolean first = true;
            
            public boolean execute(short value) {
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
  
  class TFloatShortKeyHashIterator extends THashPrimitiveIterator implements TFloatIterator {
    TFloatShortKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public float next() {
      moveToNextIndex();
      return TFloatShortHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TFloatShortHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TFloatShortValueHashIterator extends THashPrimitiveIterator implements TShortIterator {
    TFloatShortValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public short next() {
      moveToNextIndex();
      return TFloatShortHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TFloatShortHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TFloatShortHashIterator extends THashPrimitiveIterator implements TFloatShortIterator {
    TFloatShortHashIterator(TFloatShortHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public float key() {
      return TFloatShortHashMap.this._set[this._index];
    }
    
    public short value() {
      return TFloatShortHashMap.this._values[this._index];
    }
    
    public short setValue(short val) {
      short old = value();
      TFloatShortHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TFloatShortHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TFloatShortMap))
      return false; 
    TFloatShortMap that = (TFloatShortMap)other;
    if (that.size() != size())
      return false; 
    short[] values = this._values;
    byte[] states = this._states;
    short this_no_entry_value = getNoEntryValue();
    short that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        float key = this._set[i];
        if (!that.containsKey(key))
          return false; 
        short that_value = that.get(key);
        short this_value = values[i];
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
    forEachEntry(new TFloatShortProcedure() {
          private boolean first = true;
          
          public boolean execute(float key, short value) {
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
        out.writeShort(this._values[i]);
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
      short val = in.readShort();
      put(key, val);
    } 
  }
}
