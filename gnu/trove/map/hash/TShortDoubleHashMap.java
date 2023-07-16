package gnu.trove.map.hash;

import gnu.trove.TDoubleCollection;
import gnu.trove.TShortCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.impl.hash.TShortDoubleHash;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.iterator.TShortDoubleIterator;
import gnu.trove.iterator.TShortIterator;
import gnu.trove.map.TShortDoubleMap;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.procedure.TShortDoubleProcedure;
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

public class TShortDoubleHashMap extends TShortDoubleHash implements TShortDoubleMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient double[] _values;
  
  public TShortDoubleHashMap() {}
  
  public TShortDoubleHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TShortDoubleHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TShortDoubleHashMap(int initialCapacity, float loadFactor, short noEntryKey, double noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TShortDoubleHashMap(short[] keys, double[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TShortDoubleHashMap(TShortDoubleMap map) {
    super(map.size());
    if (map instanceof TShortDoubleHashMap) {
      TShortDoubleHashMap hashmap = (TShortDoubleHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != 0)
        Arrays.fill(this._set, this.no_entry_key); 
      if (this.no_entry_value != 0.0D)
        Arrays.fill(this._values, this.no_entry_value); 
      setUp(saturatedCast(fastCeil(10.0D / this._loadFactor)));
    } 
    putAll(map);
  }
  
  protected int setUp(int initialCapacity) {
    int capacity = super.setUp(initialCapacity);
    this._values = new double[capacity];
    return capacity;
  }
  
  protected void rehash(int newCapacity) {
    int oldCapacity = this._set.length;
    short[] oldKeys = this._set;
    double[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new short[newCapacity];
    this._values = new double[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        short o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public double put(short key, double value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public double putIfAbsent(short key, double value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private double doPut(short key, double value, int index) {
    double previous = this.no_entry_value;
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
  
  public void putAll(Map<? extends Short, ? extends Double> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Short, ? extends Double> entry : map.entrySet())
      put(((Short)entry.getKey()).shortValue(), ((Double)entry.getValue()).doubleValue()); 
  }
  
  public void putAll(TShortDoubleMap map) {
    ensureCapacity(map.size());
    TShortDoubleIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public double get(short key) {
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
  
  public double remove(short key) {
    double prev = this.no_entry_value;
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
  
  public TDoubleCollection valueCollection() {
    return new TValueView();
  }
  
  public double[] values() {
    double[] vals = new double[size()];
    if (vals.length == 0)
      return vals; 
    double[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        vals[j++] = v[i]; 
    } 
    return vals;
  }
  
  public double[] values(double[] array) {
    int size = size();
    if (size == 0)
      return array; 
    if (array.length < size)
      array = new double[size]; 
    double[] v = this._values;
    byte[] states = this._states;
    for (int i = v.length, j = 0; i-- > 0;) {
      if (states[i] == 1)
        array[j++] = v[i]; 
    } 
    return array;
  }
  
  public boolean containsValue(double val) {
    byte[] states = this._states;
    double[] vals = this._values;
    for (int i = vals.length; i-- > 0;) {
      if (states[i] == 1 && val == vals[i])
        return true; 
    } 
    return false;
  }
  
  public boolean containsKey(short key) {
    return contains(key);
  }
  
  public TShortDoubleIterator iterator() {
    return new TShortDoubleHashIterator(this);
  }
  
  public boolean forEachKey(TShortProcedure procedure) {
    return forEach(procedure);
  }
  
  public boolean forEachValue(TDoubleProcedure procedure) {
    byte[] states = this._states;
    double[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(values[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachEntry(TShortDoubleProcedure procedure) {
    byte[] states = this._states;
    short[] keys = this._set;
    double[] values = this._values;
    for (int i = keys.length; i-- > 0;) {
      if (states[i] == 1 && !procedure.execute(keys[i], values[i]))
        return false; 
    } 
    return true;
  }
  
  public void transformValues(TDoubleFunction function) {
    byte[] states = this._states;
    double[] values = this._values;
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1)
        values[i] = function.execute(values[i]); 
    } 
  }
  
  public boolean retainEntries(TShortDoubleProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    short[] keys = this._set;
    double[] values = this._values;
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
    return adjustValue(key, 1.0D);
  }
  
  public boolean adjustValue(short key, double amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = this._values[index] + amount;
    return true;
  }
  
  public double adjustOrPutValue(short key, double adjust_amount, double put_amount) {
    int index = insertKey(key);
    index = -index - 1;
    double newValue = this._values[index] = this._values[index] + adjust_amount;
    boolean isNewMapping = false;
    newValue = this._values[index] = put_amount;
    isNewMapping = true;
    byte previousState = this._states[index];
    if (isNewMapping)
      postInsertHook(this.consumeFreeSlot); 
    return newValue;
  }
  
  protected class TKeyView implements TShortSet {
    public TShortIterator iterator() {
      return new TShortDoubleHashMap.TShortDoubleKeyHashIterator((TPrimitiveHash)TShortDoubleHashMap.this);
    }
    
    public short getNoEntryValue() {
      return TShortDoubleHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TShortDoubleHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TShortDoubleHashMap.this._size);
    }
    
    public boolean contains(short entry) {
      return TShortDoubleHashMap.this.contains(entry);
    }
    
    public short[] toArray() {
      return TShortDoubleHashMap.this.keys();
    }
    
    public short[] toArray(short[] dest) {
      return TShortDoubleHashMap.this.keys(dest);
    }
    
    public boolean add(short entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(short entry) {
      return (TShortDoubleHashMap.this.no_entry_value != TShortDoubleHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Short) {
          short ele = ((Short)element).shortValue();
          if (!TShortDoubleHashMap.this.containsKey(ele))
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
        if (!TShortDoubleHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(short[] array) {
      for (short element : array) {
        if (!TShortDoubleHashMap.this.contains(element))
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
      short[] set = TShortDoubleHashMap.this._set;
      byte[] states = TShortDoubleHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TShortDoubleHashMap.this.removeAt(i);
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
      TShortDoubleHashMap.this.clear();
    }
    
    public boolean forEach(TShortProcedure procedure) {
      return TShortDoubleHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TShortSet))
        return false; 
      TShortSet that = (TShortSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TShortDoubleHashMap.this._states.length; i-- > 0;) {
        if (TShortDoubleHashMap.this._states[i] == 1 && 
          !that.contains(TShortDoubleHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TShortDoubleHashMap.this._states.length; i-- > 0;) {
        if (TShortDoubleHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TShortDoubleHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TShortDoubleHashMap.this.forEachKey(new TShortProcedure() {
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
  
  protected class TValueView implements TDoubleCollection {
    public TDoubleIterator iterator() {
      return new TShortDoubleHashMap.TShortDoubleValueHashIterator((TPrimitiveHash)TShortDoubleHashMap.this);
    }
    
    public double getNoEntryValue() {
      return TShortDoubleHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TShortDoubleHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TShortDoubleHashMap.this._size);
    }
    
    public boolean contains(double entry) {
      return TShortDoubleHashMap.this.containsValue(entry);
    }
    
    public double[] toArray() {
      return TShortDoubleHashMap.this.values();
    }
    
    public double[] toArray(double[] dest) {
      return TShortDoubleHashMap.this.values(dest);
    }
    
    public boolean add(double entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(double entry) {
      double[] values = TShortDoubleHashMap.this._values;
      byte[] states = TShortDoubleHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TShortDoubleHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Double) {
          double ele = ((Double)element).doubleValue();
          if (!TShortDoubleHashMap.this.containsValue(ele))
            return false; 
          continue;
        } 
        return false;
      } 
      return true;
    }
    
    public boolean containsAll(TDoubleCollection collection) {
      TDoubleIterator iter = collection.iterator();
      while (iter.hasNext()) {
        if (!TShortDoubleHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(double[] array) {
      for (double element : array) {
        if (!TShortDoubleHashMap.this.containsValue(element))
          return false; 
      } 
      return true;
    }
    
    public boolean addAll(Collection<? extends Double> collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(TDoubleCollection collection) {
      throw new UnsupportedOperationException();
    }
    
    public boolean addAll(double[] array) {
      throw new UnsupportedOperationException();
    }
    
    public boolean retainAll(Collection<?> collection) {
      boolean modified = false;
      TDoubleIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(Double.valueOf(iter.next()))) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(TDoubleCollection collection) {
      if (this == collection)
        return false; 
      boolean modified = false;
      TDoubleIterator iter = iterator();
      while (iter.hasNext()) {
        if (!collection.contains(iter.next())) {
          iter.remove();
          modified = true;
        } 
      } 
      return modified;
    }
    
    public boolean retainAll(double[] array) {
      boolean changed = false;
      Arrays.sort(array);
      double[] values = TShortDoubleHashMap.this._values;
      byte[] states = TShortDoubleHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TShortDoubleHashMap.this.removeAt(i);
          changed = true;
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(Collection<?> collection) {
      boolean changed = false;
      for (Object element : collection) {
        if (element instanceof Double) {
          double c = ((Double)element).doubleValue();
          if (remove(c))
            changed = true; 
        } 
      } 
      return changed;
    }
    
    public boolean removeAll(TDoubleCollection collection) {
      if (this == collection) {
        clear();
        return true;
      } 
      boolean changed = false;
      TDoubleIterator iter = collection.iterator();
      while (iter.hasNext()) {
        double element = iter.next();
        if (remove(element))
          changed = true; 
      } 
      return changed;
    }
    
    public boolean removeAll(double[] array) {
      boolean changed = false;
      for (int i = array.length; i-- > 0;) {
        if (remove(array[i]))
          changed = true; 
      } 
      return changed;
    }
    
    public void clear() {
      TShortDoubleHashMap.this.clear();
    }
    
    public boolean forEach(TDoubleProcedure procedure) {
      return TShortDoubleHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TShortDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
            private boolean first = true;
            
            public boolean execute(double value) {
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
  
  class TShortDoubleKeyHashIterator extends THashPrimitiveIterator implements TShortIterator {
    TShortDoubleKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public short next() {
      moveToNextIndex();
      return TShortDoubleHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TShortDoubleHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TShortDoubleValueHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
    TShortDoubleValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public double next() {
      moveToNextIndex();
      return TShortDoubleHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TShortDoubleHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TShortDoubleHashIterator extends THashPrimitiveIterator implements TShortDoubleIterator {
    TShortDoubleHashIterator(TShortDoubleHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public short key() {
      return TShortDoubleHashMap.this._set[this._index];
    }
    
    public double value() {
      return TShortDoubleHashMap.this._values[this._index];
    }
    
    public double setValue(double val) {
      double old = value();
      TShortDoubleHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TShortDoubleHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TShortDoubleMap))
      return false; 
    TShortDoubleMap that = (TShortDoubleMap)other;
    if (that.size() != size())
      return false; 
    double[] values = this._values;
    byte[] states = this._states;
    double this_no_entry_value = getNoEntryValue();
    double that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        short key = this._set[i];
        if (!that.containsKey(key))
          return false; 
        double that_value = that.get(key);
        double this_value = values[i];
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
    forEachEntry(new TShortDoubleProcedure() {
          private boolean first = true;
          
          public boolean execute(short key, double value) {
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
        out.writeDouble(this._values[i]);
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
      double val = in.readDouble();
      put(key, val);
    } 
  }
}
