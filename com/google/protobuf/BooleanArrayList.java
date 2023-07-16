package com.google.protobuf;

import java.util.Arrays;
import java.util.Collection;
import java.util.RandomAccess;

final class BooleanArrayList extends AbstractProtobufList<Boolean> implements Internal.BooleanList, RandomAccess, PrimitiveNonBoxingCollection {
  private static final BooleanArrayList EMPTY_LIST = new BooleanArrayList(new boolean[0], 0);
  
  private boolean[] array;
  
  private int size;
  
  static {
    EMPTY_LIST.makeImmutable();
  }
  
  public static BooleanArrayList emptyList() {
    return EMPTY_LIST;
  }
  
  BooleanArrayList() {
    this(new boolean[10], 0);
  }
  
  private BooleanArrayList(boolean[] other, int size) {
    this.array = other;
    this.size = size;
  }
  
  protected void removeRange(int fromIndex, int toIndex) {
    ensureIsMutable();
    if (toIndex < fromIndex)
      throw new IndexOutOfBoundsException("toIndex < fromIndex"); 
    System.arraycopy(this.array, toIndex, this.array, fromIndex, this.size - toIndex);
    this.size -= toIndex - fromIndex;
    this.modCount++;
  }
  
  public boolean equals(Object o) {
    if (this == o)
      return true; 
    if (!(o instanceof BooleanArrayList))
      return super.equals(o); 
    BooleanArrayList other = (BooleanArrayList)o;
    if (this.size != other.size)
      return false; 
    boolean[] arr = other.array;
    for (int i = 0; i < this.size; i++) {
      if (this.array[i] != arr[i])
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int result = 1;
    for (int i = 0; i < this.size; i++)
      result = 31 * result + Internal.hashBoolean(this.array[i]); 
    return result;
  }
  
  public Internal.BooleanList mutableCopyWithCapacity(int capacity) {
    if (capacity < this.size)
      throw new IllegalArgumentException(); 
    return new BooleanArrayList(Arrays.copyOf(this.array, capacity), this.size);
  }
  
  public Boolean get(int index) {
    return Boolean.valueOf(getBoolean(index));
  }
  
  public boolean getBoolean(int index) {
    ensureIndexInRange(index);
    return this.array[index];
  }
  
  public int indexOf(Object element) {
    if (!(element instanceof Boolean))
      return -1; 
    boolean unboxedElement = ((Boolean)element).booleanValue();
    int numElems = size();
    for (int i = 0; i < numElems; i++) {
      if (this.array[i] == unboxedElement)
        return i; 
    } 
    return -1;
  }
  
  public boolean contains(Object element) {
    return (indexOf(element) != -1);
  }
  
  public int size() {
    return this.size;
  }
  
  public Boolean set(int index, Boolean element) {
    return Boolean.valueOf(setBoolean(index, element.booleanValue()));
  }
  
  public boolean setBoolean(int index, boolean element) {
    ensureIsMutable();
    ensureIndexInRange(index);
    boolean previousValue = this.array[index];
    this.array[index] = element;
    return previousValue;
  }
  
  public boolean add(Boolean element) {
    addBoolean(element.booleanValue());
    return true;
  }
  
  public void add(int index, Boolean element) {
    addBoolean(index, element.booleanValue());
  }
  
  public void addBoolean(boolean element) {
    ensureIsMutable();
    if (this.size == this.array.length) {
      int length = this.size * 3 / 2 + 1;
      boolean[] newArray = new boolean[length];
      System.arraycopy(this.array, 0, newArray, 0, this.size);
      this.array = newArray;
    } 
    this.array[this.size++] = element;
  }
  
  private void addBoolean(int index, boolean element) {
    ensureIsMutable();
    if (index < 0 || index > this.size)
      throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index)); 
    if (this.size < this.array.length) {
      System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
    } else {
      int length = this.size * 3 / 2 + 1;
      boolean[] newArray = new boolean[length];
      System.arraycopy(this.array, 0, newArray, 0, index);
      System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
      this.array = newArray;
    } 
    this.array[index] = element;
    this.size++;
    this.modCount++;
  }
  
  public boolean addAll(Collection<? extends Boolean> collection) {
    ensureIsMutable();
    Internal.checkNotNull(collection);
    if (!(collection instanceof BooleanArrayList))
      return super.addAll(collection); 
    BooleanArrayList list = (BooleanArrayList)collection;
    if (list.size == 0)
      return false; 
    int overflow = Integer.MAX_VALUE - this.size;
    if (overflow < list.size)
      throw new OutOfMemoryError(); 
    int newSize = this.size + list.size;
    if (newSize > this.array.length)
      this.array = Arrays.copyOf(this.array, newSize); 
    System.arraycopy(list.array, 0, this.array, this.size, list.size);
    this.size = newSize;
    this.modCount++;
    return true;
  }
  
  public Boolean remove(int index) {
    ensureIsMutable();
    ensureIndexInRange(index);
    boolean value = this.array[index];
    if (index < this.size - 1)
      System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1); 
    this.size--;
    this.modCount++;
    return Boolean.valueOf(value);
  }
  
  private void ensureIndexInRange(int index) {
    if (index < 0 || index >= this.size)
      throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index)); 
  }
  
  private String makeOutOfBoundsExceptionMessage(int index) {
    return "Index:" + index + ", Size:" + this.size;
  }
}
