package gnu.trove.map.hash;

import gnu.trove.TFloatCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TFloatFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortFloatHash;
import gnu.trove.iterator.TFloatIterator;
import gnu.trove.iterator.TShortFloatIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TShortFloatMap;
import gnu.trove.procedure.TFloatProcedure;
import gnu.trove.procedure.TShortFloatProcedure;
import gnu.trove.procedure.TShortProcedure;
import gnu.trove.set.TShortSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TShortFloatHashMap extends TShortFloatHash implements TShortFloatMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient float[] _values;
  
  public TShortFloatHashMap() {}
  
  public TShortFloatHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TShortFloatHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TShortFloatHashMap(int initialCapacity, float loadFactor, short noEntryKey, float noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TShortFloatHashMap(short[] keys, float[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TShortFloatHashMap(TShortFloatMap map) {
    super(map.size());
    if (map instanceof TShortFloatHashMap) {
      TShortFloatHashMap hashmap = (TShortFloatHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != 0)
        Arrays.fill(this._set, this.no_entry_key); 
      if (this.no_entry_value != 0.0F)
        Arrays.fill(this._values, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    putAll(map);
  }
  
  protected int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = new float[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    short[] oldKeys = this._set;
    float[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new short[newCapacity];
    this._values = new float[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        short o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public float put(short key, float value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public float putIfAbsent(short key, float value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private float doPut(short key, float value, int index) {
    float previous = this.no_entry_value;
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
  
  public void putAll(Map<? extends Short, ? extends Float> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Short, ? extends Float> entry : map.entrySet())
      put(((Short)entry.getKey()).shortValue(), ((Float)entry.getValue()).floatValue()); 
  }
  
  public void putAll(TShortFloatMap map) {
    ensureCapacity(map.size());
    TShortFloatIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public float get(short key) {
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
  
  public float remove(short key) {
    float prev = this.no_entry_value;
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
  
  public TShortSet keySet() {
    return new TKeyView();
  }
  
  public short[] keys() {
    short[] keys = new short[size()];
    if (keys.length == 0)
      return keys; 
    short[] k = this._set;
    byte[] states = this._states;
    for (int i = k.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        keys[j++] = k[i]; 
    } 
    return keys;
  }
  
  public short[] keys(short[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new short[size]; 
    short[] keys = this._set;
    byte[] states = this._states;
    for (int i = keys.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = keys[i]; 
    } 
    return array;
  }
  
  public TFloatCollection valueCollection() {
    return new TValueView();
  }
  
  public float[] values() {
    float[] vals = new float[size()];
    if (vals.length == 0)
      return vals; 
    float[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public float[] values(float[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new float[size]; 
    float[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = v[i]; 
    } 
    return array;
  }
  
  public boolean containsValue(float val) {
    byte[] states = this._states;
    float[] vals = this._values;
    for (int i = vals.length; i-- > 0;) {
      if (states[i] == 1 && val == vals[i])
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(short key) {
    return contains(key);
  }
  
  public TShortFloatIterator iterator() {
    return new TShortFloatHashIterator(this);
  }
  
  public boolean forEachKey(TShortProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TFloatProcedure procedure) {
    byte[] states = this._states;
    float[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TShortFloatProcedure procedure) {
    byte[] states = this._states;
    short[] keys = this._set;
    float[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public void transformValues(TFloatFunction function) {
    byte[] states = this._states;
    float[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = function.execute(values[i]); 
    } 
  }
  
  public boolean retainEntries(TShortFloatProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    short[] keys = this._set;
    float[] values = this._values;
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
  
  public boolean increment(short key) {
    return adjustValue(key, 1.0F);
  }
  
  public boolean adjustValue(short key, float amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = this._values[index] + amount;
    return true;
  }
  
  public float adjustOrPutValue(short key, float adjust_amount, float put_amount) {
    boolean isNewMapping;
    float newValue;
    int index = insertKey(key);
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
  
  protected class TKeyView implements TShortSet {
    public TShortIterator iterator() {
      return new TShortFloatHashMap.TShortFloatKeyHashIterator((TPrimitiveHash)TShortFloatHashMap.this);
    }
    
    public short getNoEntryValue() {
      return TShortFloatHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TShortFloatHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TShortFloatHashMap.this._size);
    }
    
    public boolean contains(short entry) {
      return TShortFloatHashMap.this.contains(entry);
    }
    
    public short[] toArray() {
      return TShortFloatHashMap.this.keys();
    }
    
    public short[] toArray(short[] dest) {
      return TShortFloatHashMap.this.keys(dest);
    }
    
    public boolean add(short entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(short entry) {
      return (TShortFloatHashMap.this.no_entry_value != TShortFloatHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Short) {
          short ele = ((Short)element).shortValue();
          if (!TShortFloatHashMap.this.containsKey(ele))
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
        if (!TShortFloatHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(short[] array) {
      for (short element : array) {
        if (!TShortFloatHashMap.this.contains(element))
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
      short[] set = TShortFloatHashMap.this._set;
      byte[] states = TShortFloatHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TShortFloatHashMap.this.removeAt(i);
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
      TShortFloatHashMap.this.clear();
    }
    
    public boolean forEach(TShortProcedure procedure) {
      return TShortFloatHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TShortSet))
        return false; 
      TShortSet that = (TShortSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TShortFloatHashMap.this._states.length; i-- > 0;) {
        if (TShortFloatHashMap.this._states[i] == 1 && 
          !that.contains(TShortFloatHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TShortFloatHashMap.this._states.length; i-- > 0;) {
        if (TShortFloatHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TShortFloatHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TShortFloatHashMap.this.forEachKey(new TShortProcedure() {
            private boolean first = true;
            
            public boolean execute(short key) {
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
  
  protected class TValueView implements TFloatCollection {
    public TFloatIterator iterator() {
      return new TShortFloatHashMap.TShortFloatValueHashIterator((TPrimitiveHash)TShortFloatHashMap.this);
    }
    
    public float getNoEntryValue() {
      return TShortFloatHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TShortFloatHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TShortFloatHashMap.this._size);
    }
    
    public boolean contains(float entry) {
      return TShortFloatHashMap.this.containsValue(entry);
    }
    
    public float[] toArray() {
      return TShortFloatHashMap.this.values();
    }
    
    public float[] toArray(float[] dest) {
      return TShortFloatHashMap.this.values(dest);
    }
    
    public boolean add(float entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(float entry) {
      float[] values = TShortFloatHashMap.this._values;
      byte[] states = TShortFloatHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TShortFloatHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Float) {
          float ele = ((Float)element).floatValue();
          if (!TShortFloatHashMap.this.containsValue(ele))
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
        if (!TShortFloatHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(float[] array) {
      for (float element : array) {
        if (!TShortFloatHashMap.this.containsValue(element))
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
      float[] values = TShortFloatHashMap.this._values;
      byte[] states = TShortFloatHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TShortFloatHashMap.this.removeAt(i);
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
      TShortFloatHashMap.this.clear();
    }
    
    public boolean forEach(TFloatProcedure procedure) {
      return TShortFloatHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TShortFloatHashMap.this.forEachValue(new TFloatProcedure() {
            private boolean first = true;
            
            public boolean execute(float value) {
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
  
  class TShortFloatKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {
    TShortFloatKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public short next() {
      moveToNextIndex();
      return TShortFloatHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TShortFloatHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TShortFloatValueHashIterator extends THashPrimitiveIterator implements TFloatIterator {
    TShortFloatValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public float next() {
      moveToNextIndex();
      return TShortFloatHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TShortFloatHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TShortFloatHashIterator extends THashPrimitiveIterator implements TShortFloatIterator {
    TShortFloatHashIterator(TShortFloatHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public short key() {
      return TShortFloatHashMap.this._set[this._index];
    }
    
    public float value() {
      return TShortFloatHashMap.this._values[this._index];
    }
    
    public float setValue(float val) {
      float old = value();
      TShortFloatHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TShortFloatHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TShortFloatMap))
      return false; 
    TShortFloatMap that = (TShortFloatMap)other;
    if (that.size() != size())
      return false; 
    float[] values = this._values;
    byte[] states = this._states;
    float this_no_entry_value = getNoEntryValue();
    float that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        short key = this._set[i];
        if (!that.containsKey(key))
          return false; 
        float that_value = that.get(key);
        float this_value = values[i];
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
    forEachEntry(new TShortFloatProcedure() {
          private boolean first = true;
          
          public boolean execute(short key, float value) {
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
        out.writeShort(this._set[i]);
        out.writeFloat(this._values[i]);
      } 
    } 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    super.readExternal(in);
    int size = in.readInt();
    setUp(size);
    while (size-- > 0) {
      short key = in.readShort();
      float val = in.readFloat();
      put(key, val);
    } 
  }
}
