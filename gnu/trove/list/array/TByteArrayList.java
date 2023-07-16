package gnu.trove.list.array;

import gnu.trove.TByteCollection;
import gnu.trove.function.TByteFunction;
import gnu.trove.impl.HashFunctions;
import gnu.trove.iterator.TByteIterator;
import gnu.trove.list.TByteList;
import gnu.trove.procedure.TByteProcedure;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Arrays;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;
import java.util.Random;

public class TByteArrayList implements TByteList, Externalizable {
  static final long serialVersionUID = 1L;
  
  protected static final int DEFAULT_CAPACITY = 10;
  
  protected byte[] _data;
  
  protected int _pos;
  
  protected byte no_entry_value;
  
  public TByteArrayList() {
    this(10, (byte)0);
  }
  
  public TByteArrayList(int capacity) {
    this(capacity, (byte)0);
  }
  
  public TByteArrayList(int capacity, byte no_entry_value) {
    this._data = new byte[capacity];
    this._pos = 0;
    this.no_entry_value = no_entry_value;
  }
  
  public TByteArrayList(TByteCollection collection) {
    this(collection.size());
    addAll(collection);
  }
  
  public TByteArrayList(byte[] values) {
    this(values.length);
    add(values);
  }
  
  protected TByteArrayList(byte[] values, byte no_entry_value, boolean wrap) {
    if (!wrap)
      throw new IllegalStateException("Wrong call"); 
    if (values == null)
      throw new IllegalArgumentException("values can not be null"); 
    this._data = values;
    this._pos = values.length;
    this.no_entry_value = no_entry_value;
  }
  
  public static TByteArrayList wrap(byte[] values) {
    return wrap(values, (byte)0);
  }
  
  public static TByteArrayList wrap(byte[] values, byte no_entry_value) {
    return new TByteArrayList(values, no_entry_value, true) {
        public void ensureCapacity(int capacity) {
          if (capacity > this._data.length)
            throw new IllegalStateException("Can not grow ArrayList wrapped external array"); 
        }
      };
  }
  
  public byte getNoEntryValue() {
    return this.no_entry_value;
  }
  
  public void ensureCapacity(int capacity) {
    if (capacity > this._data.length) {
      int newCap = Math.max(this._data.length << 1, capacity);
      byte[] tmp = new byte[newCap];
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
      byte[] tmp = new byte[size()];
      toArray(tmp, 0, tmp.length);
      this._data = tmp;
    } 
  }
  
  public boolean add(byte val) {
    ensureCapacity(this._pos + 1);
    this._data[this._pos++] = val;
    return true;
  }
  
  public void add(byte[] vals) {
    add(vals, 0, vals.length);
  }
  
  public void add(byte[] vals, int offset, int length) {
    ensureCapacity(this._pos + length);
    System.arraycopy(vals, offset, this._data, this._pos, length);
    this._pos += length;
  }
  
  public void insert(int offset, byte value) {
    if (offset == this._pos) {
      add(value);
      return;
    } 
    ensureCapacity(this._pos + 1);
    System.arraycopy(this._data, offset, this._data, offset + 1, this._pos - offset);
    this._data[offset] = value;
    this._pos++;
  }
  
  public void insert(int offset, byte[] values) {
    insert(offset, values, 0, values.length);
  }
  
  public void insert(int offset, byte[] values, int valOffset, int len) {
    if (offset == this._pos) {
      add(values, valOffset, len);
      return;
    } 
    ensureCapacity(this._pos + len);
    System.arraycopy(this._data, offset, this._data, offset + len, this._pos - offset);
    System.arraycopy(values, valOffset, this._data, offset, len);
    this._pos += len;
  }
  
  public byte get(int offset) {
    if (offset >= this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    return this._data[offset];
  }
  
  public byte getQuick(int offset) {
    return this._data[offset];
  }
  
  public byte set(int offset, byte val) {
    if (offset >= this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    byte prev_val = this._data[offset];
    this._data[offset] = val;
    return prev_val;
  }
  
  public byte replace(int offset, byte val) {
    if (offset >= this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    byte old = this._data[offset];
    this._data[offset] = val;
    return old;
  }
  
  public void set(int offset, byte[] values) {
    set(offset, values, 0, values.length);
  }
  
  public void set(int offset, byte[] values, int valOffset, int length) {
    if (offset < 0 || offset + length > this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    System.arraycopy(values, valOffset, this._data, offset, length);
  }
  
  public void setQuick(int offset, byte val) {
    this._data[offset] = val;
  }
  
  public void clear() {
    clearQuick();
    Arrays.fill(this._data, this.no_entry_value);
  }
  
  public void clearQuick() {
    this._pos = 0;
  }
  
  public boolean remove(byte value) {
    for (int index = 0; index < this._pos; index++) {
      if (value == this._data[index]) {
        remove(index, 1);
        return true;
      } 
    } 
    return false;
  }
  
  public byte removeAt(int offset) {
    byte old = get(offset);
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
  
  public TByteIterator iterator() {
    return new TByteArrayIterator(0);
  }
  
  public boolean containsAll(Collection<?> collection) {
    for (Object element : collection) {
      if (element instanceof Byte) {
        byte c = ((Byte)element).byteValue();
        if (!contains(c))
          return false; 
        continue;
      } 
      return false;
    } 
    return true;
  }
  
  public boolean containsAll(TByteCollection collection) {
    if (this == collection)
      return true; 
    TByteIterator iter = collection.iterator();
    while (iter.hasNext()) {
      byte element = iter.next();
      if (!contains(element))
        return false; 
    } 
    return true;
  }
  
  public boolean containsAll(byte[] array) {
    for (int i = array.length; i-- > 0;) {
      if (!contains(array[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean addAll(Collection<? extends Byte> collection) {
    boolean changed = false;
    for (Byte element : collection) {
      byte e = element.byteValue();
      if (add(e))
        changed = true; 
    } 
    return changed;
  }
  
  public boolean addAll(TByteCollection collection) {
    boolean changed = false;
    TByteIterator iter = collection.iterator();
    while (iter.hasNext()) {
      byte element = iter.next();
      if (add(element))
        changed = true; 
    } 
    return changed;
  }
  
  public boolean addAll(byte[] array) {
    boolean changed = false;
    for (byte element : array) {
      if (add(element))
        changed = true; 
    } 
    return changed;
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
    byte[] data = this._data;
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
      if (element instanceof Byte) {
        byte c = ((Byte)element).byteValue();
        if (remove(c))
          changed = true; 
      } 
    } 
    return changed;
  }
  
  public boolean removeAll(TByteCollection collection) {
    if (collection == this) {
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
  
  public void transformValues(TByteFunction function) {
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
    byte tmp = this._data[i];
    this._data[i] = this._data[j];
    this._data[j] = tmp;
  }
  
  public TByteList subList(int begin, int end) {
    if (end < begin)
      throw new IllegalArgumentException("end index " + end + " greater than begin index " + begin); 
    if (begin < 0)
      throw new IndexOutOfBoundsException("begin index can not be < 0"); 
    if (end > this._data.length)
      throw new IndexOutOfBoundsException("end index < " + this._data.length); 
    TByteArrayList list = new TByteArrayList(end - begin);
    for (int i = begin; i < end; i++)
      list.add(this._data[i]); 
    return list;
  }
  
  public byte[] toArray() {
    return toArray(0, this._pos);
  }
  
  public byte[] toArray(int offset, int len) {
    byte[] rv = new byte[len];
    toArray(rv, offset, len);
    return rv;
  }
  
  public byte[] toArray(byte[] dest) {
    int len = dest.length;
    if (dest.length > this._pos) {
      len = this._pos;
      dest[len] = this.no_entry_value;
    } 
    toArray(dest, 0, len);
    return dest;
  }
  
  public byte[] toArray(byte[] dest, int offset, int len) {
    if (len == 0)
      return dest; 
    if (offset < 0 || offset >= this._pos)
      throw new ArrayIndexOutOfBoundsException(offset); 
    System.arraycopy(this._data, offset, dest, 0, len);
    return dest;
  }
  
  public byte[] toArray(byte[] dest, int source_pos, int dest_pos, int len) {
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
    if (!(other instanceof TByteList))
      return false; 
    if (other instanceof TByteArrayList) {
      TByteArrayList tByteArrayList = (TByteArrayList)other;
      if (tByteArrayList.size() != size())
        return false; 
      for (int j = this._pos; j-- > 0;) {
        if (this._data[j] != tByteArrayList._data[j])
          return false; 
      } 
      return true;
    } 
    TByteList that = (TByteList)other;
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
  
  public boolean forEach(TByteProcedure procedure) {
    for (int i = 0; i < this._pos; i++) {
      if (!procedure.execute(this._data[i]))
        return false; 
    } 
    return true;
  }
  
  public boolean forEachDescending(TByteProcedure procedure) {
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
  
  public void fill(byte val) {
    Arrays.fill(this._data, 0, this._pos, val);
  }
  
  public void fill(int fromIndex, int toIndex, byte val) {
    if (toIndex > this._pos) {
      ensureCapacity(toIndex);
      this._pos = toIndex;
    } 
    Arrays.fill(this._data, fromIndex, toIndex, val);
  }
  
  public int binarySearch(byte value) {
    return binarySearch(value, 0, this._pos);
  }
  
  public int binarySearch(byte value, int fromIndex, int toIndex) {
    if (fromIndex < 0)
      throw new ArrayIndexOutOfBoundsException(fromIndex); 
    if (toIndex > this._pos)
      throw new ArrayIndexOutOfBoundsException(toIndex); 
    int low = fromIndex;
    int high = toIndex - 1;
    while (low <= high) {
      int mid = low + high >>> 1;
      byte midVal = this._data[mid];
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
  
  public int indexOf(byte value) {
    return indexOf(0, value);
  }
  
  public int indexOf(int offset, byte value) {
    for (int i = offset; i < this._pos; i++) {
      if (this._data[i] == value)
        return i; 
    } 
    return -1;
  }
  
  public int lastIndexOf(byte value) {
    return lastIndexOf(this._pos, value);
  }
  
  public int lastIndexOf(int offset, byte value) {
    for (int i = offset; i-- > 0;) {
      if (this._data[i] == value)
        return i; 
    } 
    return -1;
  }
  
  public boolean contains(byte value) {
    return (lastIndexOf(value) >= 0);
  }
  
  public TByteList grep(TByteProcedure condition) {
    TByteArrayList list = new TByteArrayList();
    for (int i = 0; i < this._pos; i++) {
      if (condition.execute(this._data[i]))
        list.add(this._data[i]); 
    } 
    return list;
  }
  
  public TByteList inverseGrep(TByteProcedure condition) {
    TByteArrayList list = new TByteArrayList();
    for (int i = 0; i < this._pos; i++) {
      if (!condition.execute(this._data[i]))
        list.add(this._data[i]); 
    } 
    return list;
  }
  
  public byte max() {
    if (size() == 0)
      throw new IllegalStateException("cannot find maximum of an empty list"); 
    byte max = Byte.MIN_VALUE;
    for (int i = 0; i < this._pos; i++) {
      if (this._data[i] > max)
        max = this._data[i]; 
    } 
    return max;
  }
  
  public byte min() {
    if (size() == 0)
      throw new IllegalStateException("cannot find minimum of an empty list"); 
    byte min = Byte.MAX_VALUE;
    for (int i = 0; i < this._pos; i++) {
      if (this._data[i] < min)
        min = this._data[i]; 
    } 
    return min;
  }
  
  public byte sum() {
    byte sum = 0;
    for (int i = 0; i < this._pos; i++)
      sum = (byte)(sum + this._data[i]); 
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
  
  class TByteArrayIterator implements TByteIterator {
    private int cursor = 0;
    
    int lastRet = -1;
    
    TByteArrayIterator(int index) {
      this.cursor = index;
    }
    
    public boolean hasNext() {
      return (this.cursor < TByteArrayList.this.size());
    }
    
    public byte next() {
      try {
        byte next = TByteArrayList.this.get(this.cursor);
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
        TByteArrayList.this.remove(this.lastRet, 1);
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
    out.writeByte(this.no_entry_value);
    int len = this._data.length;
    out.writeInt(len);
    for (int i = 0; i < len; i++)
      out.writeByte(this._data[i]); 
  }
  
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    in.readByte();
    this._pos = in.readInt();
    this.no_entry_value = in.readByte();
    int len = in.readInt();
    this._data = new byte[len];
    for (int i = 0; i < len; i++)
      this._data[i] = in.readByte(); 
  }
}
