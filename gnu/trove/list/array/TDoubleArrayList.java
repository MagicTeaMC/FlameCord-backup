package gnu.trove.list.array;

import gnu.trove.TDoubleCollection;
import gnu.trove.function.TDoubleFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TDoubleIterator;
import gnu.trove.list.TDoubleList;
import gnu.trove.procedure.TDoubleProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Random;

public class TDoubleArrayList implements TDoubleList, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected static final int DEFAULT_CAPACITY = 10;
  
  protected double[] _data;
  
  protected int _pos;
  
  protected double no_entry_value;
  
  public TDoubleArrayList() {
    this(10, 0.0D);
  }
  
  public TDoubleArrayList(int capacity) {
    this(capacity, 0.0D);
  }
  
  public TDoubleArrayList(int capacity, double no_entry_value) {
    this._data = new double[capacity];
    this._pos = 0;
    this.no_entry_value = no_entry_value;
  }
  
  public TDoubleArrayList(TDoubleCollection collection) {
    this(collection.size());
    addAll(collection);
  }
  
  public TDoubleArrayList(double[] values) {
    this(values.length);
    add(values);
  }
  
  protected TDoubleArrayList(double[] values, double no_entry_value, boolean wrap) {
    if (!wrap)
      throw new IllegalStateException("Wrong call"); 
    if (values == null)
      throw new IllegalArgumentException("values can not be null"); 
    this._data = values;
    this._pos = values.length;
    this.no_entry_value = no_entry_value;
  }
  
  public static TDoubleArrayList wrap(double[] values) {
    return wrap(values, 0.0D);
  }
  
  public static TDoubleArrayList wrap(double[] values, double no_entry_value) {
    return new TDoubleArrayList(values, no_entry_value, true) {
        public void ensureCapacity(int capacity) {
          if (capacity > this._data.length)
            throw new IllegalStateException("Can not grow ArrayList wrapped external array"); 
        }
      };
  }
  
  public double getNoEntryValue() {
    return this.no_entry_value;
  }
  
  public void ensureCapacity(int capacity) {
    if (capacity > this._data.length) {
      int newCap = Math.max(this._data.length << 1, capacity);
      double[] tmp = new double[newCap];
      System.arraycopy(this._data, 0, tmp, 0, this._data.length);
      this._data = tmp;
    } 
  }
  
  public int size() {
    return this._pos;
  }
  
  public boolean isEmpty() {
    return (this._pos == 0);
  }
  
  public void trimToSize() {
    if (this._data.length > size()) {
      double[] tmp = new double[size()];
      toArray(tmp, 0, tmp.length);
      this._data = tmp;
    } 
  }
  
  public boolean add(double val) {
    ensureCapacity(this._pos + 1);
    this._data[this._pos++] = val;
    return true;
  }
  
  public void add(double[] vals) {
    add(vals, 0, vals.length);
  }
  
  public void add(double[] vals, int offset, int length) {
    ensureCapacity(this._pos + length);
    System.arraycopy(vals, offset, this._data, this._pos, length);
    this._pos += length;
  }
  
  public void insert(int offset, double value) {
    if (offset == this._pos) {
      add(value);
      return;
    } 
    ensureCapacity(this._pos + 1);
    System.arraycopy(this._data, offset, this._data, offset + 1, this._pos - offset);
    this._data[offset] = value;
    this._pos++;
  }
  
  public void insert(int offset, double[] values) {
    insert(offset, values, 0, values.length);
  }
  
  public void insert(int offset, double[] values, int valOffset, int len) {
    if (offset == this._pos) {
      add(values, valOffset, len);
      return;
    } 
    ensureCapacity(this._pos + len);
    System.arraycopy(this._data, offset, this._data, offset + len, this._pos - offset);
    System.arraycopy(values, valOffset, this._data, offset, len);
    this._pos += len;
  }
  
  public double get(int offset) {
    if (offset >= this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    return this._data[offset];
  }
  
  public double getQuick(int offset) {
    return this._data[offset];
  }
  
  public double set(int offset, double val) {
    if (offset >= this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    double prev_val = this._data[offset];
    this._data[offset] = val;
    return prev_val;
  }
  
  public double replace(int offset, double val) {
    if (offset >= this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    double old = this._data[offset];
    this._data[offset] = val;
    return old;
  }
  
  public void set(int offset, double[] values) {
    set(offset, values, 0, values.length);
  }
  
  public void set(int offset, double[] values, int valOffset, int length) {
    if (offset < 0 || offset + length > this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    System.arraycopy(values, valOffset, this._data, offset, length);
  }
  
  public void setQuick(int offset, double val) {
    this._data[offset] = val;
  }
  
  public void clear() {
    clearQuick();
    Arrays.fill(this._data, this.no_entry_value);
  }
  
  public void clearQuick() {
    this._pos = 0;
  }
  
  public boolean remove(double value) {
    for (int index = 0; index < this._pos; index++) {
      if (value == this._data[index]) {
        remove(index, 1);
        return true;
      } 
    } 
    return false;
  }
  
  public double removeAt(int offset) {
    double old = get(offset);
    remove(offset, 1);
    return old;
  }
  
  public void remove(int offset, int length) {
    if (length == 0)
      return; 
    if (offset < 0 || offset >= this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    if (offset == 0) {
      System.arraycopy(this._data, length, this._data, 0, this._pos - length);
    } else if (this._pos - length != offset) {
      System.arraycopy(this._data, offset + length, this._data, offset, this._pos - offset + length);
    } 
    this._pos -= length;
  }
  
  public TDoubleIterator iterator() {
    return new TDoubleArrayIterator(0);
  }
  
  public boolean containsAll(Collection<?> collection) {
    for (Object element : collection) {
      if (element instanceof Double) {
        double c = ((Double)element).doubleValue();
        if (!contains(c))
          return false; 
        continue;
      } 
      return false;
    } 
    return true;
  }
  
  public boolean containsAll(TDoubleCollection collection) {
    if (this == collection)
      return true; 
    TDoubleIterator iter = collection.iterator();
    while (iter.hasNext()) {
      double element = iter.next();
      if (!contains(element))
        return false; 
    } 
    return true;
  }
  
  public boolean containsAll(double[] array) {
    for (int i = array.length; i-- > 0;) {
      if (!contains(array[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean addAll(Collection<? extends Double> collection) {
    boolean changed = false;
    for (Double element : collection) {
      double e = element.doubleValue();
      if (add(e))
        changed = true; 
    } 
    return changed;
  }
  
  public boolean addAll(TDoubleCollection collection) {
    boolean changed = false;
    TDoubleIterator iter = collection.iterator();
    while (iter.hasNext()) {
      double element = iter.next();
      if (add(element))
        changed = true; 
    } 
    return changed;
  }
  
  public boolean addAll(double[] array) {
    boolean changed = false;
    for (double element : array) {
      if (add(element))
        changed = true; 
    } 
    return changed;
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
    double[] data = this._data;
    for (int i = this._pos; i-- > 0;) {
      if (Arrays.binarySearch(array, data[i]) < 0) {
        remove(i, 1);
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
    if (collection == this) {
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
  
  public void transformValues(TDoubleFunction function) {
    for (int i = 0; i < this._pos; i++)
      this._data[i] = function.execute(this._data[i]); 
  }
  
  public void reverse() {
    reverse(0, this._pos);
  }
  
  public void reverse(int from, int to) {
    if (from == to)
      return; 
    if (from > to)
      throw new IllegalArgumentException("from cannot be greater than to"); 
    for (int i = from, j = to - 1; i < j; i++, j--)
      swap(i, j); 
  }
  
  public void shuffle(Random rand) {
    for (int i = this._pos; i-- > 1;)
      swap(i, rand.nextInt(i)); 
  }
  
  private void swap(int i, int j) {
    double tmp = this._data[i];
    this._data[i] = this._data[j];
    this._data[j] = tmp;
  }
  
  public TDoubleList subList(int begin, int end) {
    if (end < begin)
      throw new IllegalArgumentException("end index " + end + " greater than begin index " + begin); 
    if (begin < 0)
      throw new IndexOutOfBoundsException("begin index can not be < 0"); 
    if (end > this._data.length)
      throw new IndexOutOfBoundsException("end index < " + this._data.length); 
    TDoubleArrayList list = new TDoubleArrayList(end - begin);
    for (int i = begin; i < end; i++)
      list.add(this._data[i]); 
    return list;
  }
  
  public double[] toArray() {
    return toArray(0, this._pos);
  }
  
  public double[] toArray(int offset, int len) {
    double[] rv = new double[len];
    toArray(rv, offset, len);
    return rv;
  }
  
  public double[] toArray(double[] dest) {
    int len = dest.length;
    if (dest.length > this._pos) {
      len = this._pos;
      dest[len] = this.no_entry_value;
    } 
    toArray(dest, 0, len);
    return dest;
  }
  
  public double[] toArray(double[] dest, int offset, int len) {
    if (len == 0)
      return dest; 
    if (offset < 0 || offset >= this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    System.arraycopy(this._data, offset, dest, 0, len);
    return dest;
  }
  
  public double[] toArray(double[] dest, int source_pos, int dest_pos, int len) {
    if (len == 0)
      return dest; 
    if (source_pos < 0 || source_pos >= this._pos)
      throw new ArrayIndexOutOfBoundsException(source_pos); 
    System.arraycopy(this._data, source_pos, dest, dest_pos, len);
    return dest;
  }
  
  public boolean equals(Object other) {
    if (other == this)
      return true; 
    if (!(other instanceof TDoubleList))
      return false; 
    if (other instanceof TDoubleArrayList) {
      TDoubleArrayList tDoubleArrayList = (TDoubleArrayList)other;
      if (tDoubleArrayList.size() != size())
        return false; 
      for (int j = this._pos; j-- > 0;) {
        if (this._data[j] != tDoubleArrayList._data[j])
          return false; 
      } 
      return true;
    } 
    TDoubleList that = (TDoubleList)other;
    if (that.size() != size())
      return false; 
    for (int i = 0; i < this._pos; i++) {
      if (this._data[i] != that.get(i))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int h = 0;
    for (int i = this._pos; i-- > 0;)
      h += HashFunctions.hash(this._data[i]); 
    return h;
  }
  
  public boolean forEach(TDoubleProcedure procedure) {
    for (int i = 0; i < this._pos; i++) {
      if (!procedure.execute(this._data[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachDescending(TDoubleProcedure procedure) {
    for (int i = this._pos; i-- > 0;) {
      if (!procedure.execute(this._data[i]))
        return false; 
    } 
    return true;
  }
  
  public void sort() {
    Arrays.sort(this._data, 0, this._pos);
  }
  
  public void sort(int fromIndex, int toIndex) {
    Arrays.sort(this._data, fromIndex, toIndex);
  }
  
  public void fill(double val) {
    Arrays.fill(this._data, 0, this._pos, val);
  }
  
  public void fill(int fromIndex, int toIndex, double val) {
    if (toIndex > this._pos) {
      ensureCapacity(toIndex);
      this._pos = toIndex;
    } 
    Arrays.fill(this._data, fromIndex, toIndex, val);
  }
  
  public int binarySearch(double value) {
    return binarySearch(value, 0, this._pos);
  }
  
  public int binarySearch(double value, int fromIndex, int toIndex) {
    if (fromIndex < 0)
      throw new ArrayIndexOutOfBoundsException(fromIndex); 
    if (toIndex > this._pos)
      throw new ArrayIndexOutOfBoundsException(toIndex); 
    int low = fromIndex;
    int high = toIndex - 1;
    while (low <= high) {
      int mid = low + high >>> 1;
      double midVal = this._data[mid];
      if (midVal < value) {
        low = mid + 1;
        continue;
      } 
      if (midVal > value) {
        high = mid - 1;
        continue;
      } 
      return mid;
    } 
    return -(low + 1);
  }
  
  public int indexOf(double value) {
    return indexOf(0, value);
  }
  
  public int indexOf(int offset, double value) {
    for (int i = offset; i < this._pos; i++) {
      if (this._data[i] == value)
        return i; 
    } 
    return -1;
  }
  
  public int lastIndexOf(double value) {
    return lastIndexOf(this._pos, value);
  }
  
  public int lastIndexOf(int offset, double value) {
    for (int i = offset; i-- > 0;) {
      if (this._data[i] == value)
        return i; 
    } 
    return -1;
  }
  
  public boolean contains(double value) {
    return (lastIndexOf(value) >= 0);
  }
  
  public TDoubleList grep(TDoubleProcedure condition) {
    TDoubleArrayList list = new TDoubleArrayList();
    for (int i = 0; i < this._pos; i++) {
      if (condition.execute(this._data[i]))
        list.add(this._data[i]); 
    } 
    return list;
  }
  
  public TDoubleList inverseGrep(TDoubleProcedure condition) {
    TDoubleArrayList list = new TDoubleArrayList();
    for (int i = 0; i < this._pos; i++) {
      if (!condition.execute(this._data[i]))
        list.add(this._data[i]); 
    } 
    return list;
  }
  
  public double max() {
    if (size() == 0)
      throw new IllegalStateException("cannot find maximum of an empty list"); 
    double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < this._pos; i++) {
      if (this._data[i] > max)
        max = this._data[i]; 
    } 
    return max;
  }
  
  public double min() {
    if (size() == 0)
      throw new IllegalStateException("cannot find minimum of an empty list"); 
    double min = Double.POSITIVE_INFINITY;
    for (int i = 0; i < this._pos; i++) {
      if (this._data[i] < min)
        min = this._data[i]; 
    } 
    return min;
  }
  
  public double sum() {
    double sum = 0.0D;
    for (int i = 0; i < this._pos; i++)
      sum += this._data[i]; 
    return sum;
  }
  
  public String toString() {
    StringBuilder buf = new StringBuilder("{");
    for (int i = 0, end = this._pos - 1; i < end; i++) {
      buf.append(this._data[i]);
      buf.append(", ");
    } 
    if (size() > 0)
      buf.append(this._data[this._pos - 1]); 
    buf.append("}");
    return buf.toString();
  }
  
  class TDoubleArrayIterator implements TDoubleIterator {
    private int cursor = 0;
    
    int lastRet = -1;
    
    TDoubleArrayIterator(int index) {
      this.cursor = index;
    }
    
    public boolean hasNext() {
      return (this.cursor < TDoubleArrayList.this.size());
    }
    
    public double next() {
      try {
        double next = TDoubleArrayList.this.get(this.cursor);
        this.lastRet = this.cursor++;
        return next;
      } catch (IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
      } 
    }
    
    public void remove() {
      if (this.lastRet == -1)
        throw new IllegalStateException(); 
      try {
        TDoubleArrayList.this.remove(this.lastRet, 1);
        if (this.lastRet < this.cursor)
          this.cursor--; 
        this.lastRet = -1;
      } catch (IndexOutOfBoundsException e) {
        throw new ConcurrentModificationException();
      } 
    }
  }
  
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeByte(0);
    out.writeInt(this._pos);
    out.writeDouble(this.no_entry_value);
    int len = this._data.length;
    out.writeInt(len);
    for (int i = 0; i < len; i++)
      out.writeDouble(this._data[i]); 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._pos = in.readInt();
    this.no_entry_value = in.readDouble();
    int len = in.readInt();
    this._data = new double[len];
    for (int i = 0; i < len; i++)
      this._data[i] = in.readDouble(); 
  }
}
