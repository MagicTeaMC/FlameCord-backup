package gnu.trove.map.hash;

import gnu.trove.TCharCollection;
import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.impl.hash.TCharDoubleHash;
import gnu.trove.impl.hash.THashPrimitiveIterator;
import gnu.trove.impl.hash.TPrimitiveHash;
import gnu.trove.iterator.TCharDoubleIterator;
import gnu.trove.iterator.TCharIterator;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.map.TCharDoubleMap;
import gnu.trove.procedure.TCharDoubleProcedure;
import gnu.trove.procedure.TCharProcedure;
import gnu.trove.procedure.TDoubleProcedure;
import gnu.trove.set.TCharSet;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Map;

public class TCharDoubleHashMap extends TCharDoubleHash implements TCharDoubleMap, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected transient double[] _values;
  
  public TCharDoubleHashMap() {}
  
  public TCharDoubleHashMap(int initialCapacity) {
    super(initialCapacity);
  }
  
  public TCharDoubleHashMap(int initialCapacity, float loadFactor) {
    super(initialCapacity, loadFactor);
  }
  
  public TCharDoubleHashMap(int initialCapacity, float loadFactor, char noEntryKey, double noEntryValue) {
    super(initialCapacity, loadFactor, noEntryKey, noEntryValue);
  }
  
  public TCharDoubleHashMap(char[] keys, double[] values) {
    super(Math.max(keys.length, values.length));
    int size = Math.min(keys.length, values.length);
    for (int i = 0; i < size; i++)
      put(keys[i], values[i]); 
  }
  
  public TCharDoubleHashMap(TCharDoubleMap map) {
    super(map.size());
    if (map instanceof TCharDoubleHashMap) {
      TCharDoubleHashMap hashmap = (TCharDoubleHashMap)map;
      this._loadFactor = Math.abs(hashmap._loadFactor);
      this.no_entry_key = hashmap.no_entry_key;
      this.no_entry_value = hashmap.no_entry_value;
      if (this.no_entry_key != '\000')
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
    char[] oldKeys = this._set;
    double[] oldVals = this._values;
    byte[] oldStates = this._states;
    this._set = new char[newCapacity];
    this._values = new double[newCapacity];
    this._states = new byte[newCapacity];
    for (int i = oldCapacity; i-- > 0;) {
      if (oldStates[i] == 1) {
        char o = oldKeys[i];
        int index = insertKey(o);
        this._values[index] = oldVals[i];
      } 
    } 
  }
  
  public double put(char key, double value) {
    int index = insertKey(key);
    return doPut(key, value, index);
  }
  
  public double putIfAbsent(char key, double value) {
    int index = insertKey(key);
    if (index < 0)
      return this._values[-index - 1]; 
    return doPut(key, value, index);
  }
  
  private double doPut(char key, double value, int index) {
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
  
  public void putAll(Map<? extends Character, ? extends Double> map) {
    ensureCapacity(map.size());
    for (Map.Entry<? extends Character, ? extends Double> entry : map.entrySet())
      put(((Character)entry.getKey()).charValue(), ((Double)entry.getValue()).doubleValue()); 
  }
  
  public void putAll(TCharDoubleMap map) {
    ensureCapacity(map.size());
    TCharDoubleIterator iter = map.iterator();
    while (iter.hasNext()) {
      iter.advance();
      put(iter.key(), iter.value());
    } 
  }
  
  public double get(char key) {
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
  
  public double remove(char key) {
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
  
  public boolean containsKey(char key) {
    return contains(key);
  }
  
  public TCharDoubleIterator iterator() {
    return new TCharDoubleHashIterator(this);
  }
  
  public boolean forEachKey(TCharProcedure procedure) {
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
  
  public boolean forEachEntry(TCharDoubleProcedure procedure) {
    byte[] states = this._states;
    char[] keys = this._set;
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
  
  public boolean retainEntries(TCharDoubleProcedure procedure) {
    boolean modified = false;
    byte[] states = this._states;
    char[] keys = this._set;
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
  
  public boolean increment(char key) {
    return adjustValue(key, 1.0D);
  }
  
  public boolean adjustValue(char key, double amount) {
    int index = index(key);
    if (index < 0)
      return false; 
    this._values[index] = this._values[index] + amount;
    return true;
  }
  
  public double adjustOrPutValue(char key, double adjust_amount, double put_amount) {
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
  
  protected class TKeyView implements TCharSet {
    public TCharIterator iterator() {
      return new TCharDoubleHashMap.TCharDoubleKeyHashIterator((TPrimitiveHash)TCharDoubleHashMap.this);
    }
    
    public char getNoEntryValue() {
      return TCharDoubleHashMap.this.no_entry_key;
    }
    
    public int size() {
      return TCharDoubleHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TCharDoubleHashMap.this._size);
    }
    
    public boolean contains(char entry) {
      return TCharDoubleHashMap.this.contains(entry);
    }
    
    public char[] toArray() {
      return TCharDoubleHashMap.this.keys();
    }
    
    public char[] toArray(char[] dest) {
      return TCharDoubleHashMap.this.keys(dest);
    }
    
    public boolean add(char entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(char entry) {
      return (TCharDoubleHashMap.this.no_entry_value != TCharDoubleHashMap.this.remove(entry));
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Character) {
          char ele = ((Character)element).charValue();
          if (!TCharDoubleHashMap.this.containsKey(ele))
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
        if (!TCharDoubleHashMap.this.containsKey(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(char[] array) {
      for (char element : array) {
        if (!TCharDoubleHashMap.this.contains(element))
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
      char[] set = TCharDoubleHashMap.this._set;
      byte[] states = TCharDoubleHashMap.this._states;
      for (int i = set.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, set[i]) < 0) {
          TCharDoubleHashMap.this.removeAt(i);
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
      TCharDoubleHashMap.this.clear();
    }
    
    public boolean forEach(TCharProcedure procedure) {
      return TCharDoubleHashMap.this.forEachKey(procedure);
    }
    
    public boolean equals(Object other) {
      if (!(other instanceof TCharSet))
        return false; 
      TCharSet that = (TCharSet)other;
      if (that.size() != size())
        return false; 
      for (int i = TCharDoubleHashMap.this._states.length; i-- > 0;) {
        if (TCharDoubleHashMap.this._states[i] == 1 && 
          !that.contains(TCharDoubleHashMap.this._set[i]))
          return false; 
      } 
      return true;
    }
    
    public int hashCode() {
      int hashcode = 0;
      for (int i = TCharDoubleHashMap.this._states.length; i-- > 0;) {
        if (TCharDoubleHashMap.this._states[i] == 1)
          hashcode += HashFunctions.hash(TCharDoubleHashMap.this._set[i]); 
      } 
      return hashcode;
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TCharDoubleHashMap.this.forEachKey(new TCharProcedure() {
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
  
  protected class TValueView implements TDoubleCollection {
    public TDoubleIterator iterator() {
      return new TCharDoubleHashMap.TCharDoubleValueHashIterator((TPrimitiveHash)TCharDoubleHashMap.this);
    }
    
    public double getNoEntryValue() {
      return TCharDoubleHashMap.this.no_entry_value;
    }
    
    public int size() {
      return TCharDoubleHashMap.this._size;
    }
    
    public boolean isEmpty() {
      return (0 == TCharDoubleHashMap.this._size);
    }
    
    public boolean contains(double entry) {
      return TCharDoubleHashMap.this.containsValue(entry);
    }
    
    public double[] toArray() {
      return TCharDoubleHashMap.this.values();
    }
    
    public double[] toArray(double[] dest) {
      return TCharDoubleHashMap.this.values(dest);
    }
    
    public boolean add(double entry) {
      throw new UnsupportedOperationException();
    }
    
    public boolean remove(double entry) {
      double[] values = TCharDoubleHashMap.this._values;
      byte[] states = TCharDoubleHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] != 0 && states[i] != 2 && entry == values[i]) {
          TCharDoubleHashMap.this.removeAt(i);
          return true;
        } 
      } 
      return false;
    }
    
    public boolean containsAll(Collection<?> collection) {
      for (Object element : collection) {
        if (element instanceof Double) {
          double ele = ((Double)element).doubleValue();
          if (!TCharDoubleHashMap.this.containsValue(ele))
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
        if (!TCharDoubleHashMap.this.containsValue(iter.next()))
          return false; 
      } 
      return true;
    }
    
    public boolean containsAll(double[] array) {
      for (double element : array) {
        if (!TCharDoubleHashMap.this.containsValue(element))
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
      double[] values = TCharDoubleHashMap.this._values;
      byte[] states = TCharDoubleHashMap.this._states;
      for (int i = values.length; i-- > 0;) {
        if (states[i] == 1 && Arrays.binarySearch(array, values[i]) < 0) {
          TCharDoubleHashMap.this.removeAt(i);
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
      TCharDoubleHashMap.this.clear();
    }
    
    public boolean forEach(TDoubleProcedure procedure) {
      return TCharDoubleHashMap.this.forEachValue(procedure);
    }
    
    public String toString() {
      final StringBuilder buf = new StringBuilder("{");
      TCharDoubleHashMap.this.forEachValue(new TDoubleProcedure() {
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
  
  class TCharDoubleKeyHashIterator extends THashPrimitiveIterator implements TCharIterator {
    TCharDoubleKeyHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public char next() {
      moveToNextIndex();
      return TCharDoubleHashMap.this._set[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharDoubleHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TCharDoubleValueHashIterator extends THashPrimitiveIterator implements TDoubleIterator {
    TCharDoubleValueHashIterator(TPrimitiveHash hash) {
      super(hash);
    }
    
    public double next() {
      moveToNextIndex();
      return TCharDoubleHashMap.this._values[this._index];
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharDoubleHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  class TCharDoubleHashIterator extends THashPrimitiveIterator implements TCharDoubleIterator {
    TCharDoubleHashIterator(TCharDoubleHashMap map) {
      super((TPrimitiveHash)map);
    }
    
    public void advance() {
      moveToNextIndex();
    }
    
    public char key() {
      return TCharDoubleHashMap.this._set[this._index];
    }
    
    public double value() {
      return TCharDoubleHashMap.this._values[this._index];
    }
    
    public double setValue(double val) {
      double old = value();
      TCharDoubleHashMap.this._values[this._index] = val;
      return old;
    }
    
    public void remove() {
      if (this._expectedSize != this._hash.size())
        throw new ConcurrentModificationException(); 
      try {
        this._hash.tempDisableAutoCompaction();
        TCharDoubleHashMap.this.removeAt(this._index);
      } finally {
        this._hash.reenableAutoCompaction(false);
      } 
      this._expectedSize--;
    }
  }
  
  public boolean equals(Object other) {
    if (!(other instanceof TCharDoubleMap))
      return false; 
    TCharDoubleMap that = (TCharDoubleMap)other;
    if (that.size() != size())
      return false; 
    double[] values = this._values;
    byte[] states = this._states;
    double this_no_entry_value = getNoEntryValue();
    double that_no_entry_value = that.getNoEntryValue();
    for (int i = values.length; i-- > 0;) {
      if (states[i] == 1) {
        char key = this._set[i];
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
    forEachEntry(new TCharDoubleProcedure() {
          private boolean first = true;
          
          public boolean execute(char key, double value) {
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
      char key = in.readChar();
      double val = in.readDouble();
      put(key, val);
    } 
  }
}
