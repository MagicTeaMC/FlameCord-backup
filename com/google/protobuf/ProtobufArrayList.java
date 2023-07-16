package com.google.protobuf;

import java.util.Arrays;
import java.util.RandomAccess;

final class ProtobufArrayList<E> extends AbstractProtobufList<E> implements RandomAccess {
  private static final ProtobufArrayList<Object> EMPTY_LIST = new ProtobufArrayList((E[])new Object[0], 0);
  
  private E[] array;
  
  private int size;
  
  static {
    EMPTY_LIST.makeImmutable();
  }
  
  public static <E> ProtobufArrayList<E> emptyList() {
    return (ProtobufArrayList)EMPTY_LIST;
  }
  
  ProtobufArrayList() {
    this((E[])new Object[10], 0);
  }
  
  private ProtobufArrayList(E[] array, int size) {
    this.array = array;
    this.size = size;
  }
  
  public ProtobufArrayList<E> mutableCopyWithCapacity(int capacity) {
    if (capacity < this.size)
      throw new IllegalArgumentException(); 
    E[] newArray = Arrays.copyOf(this.array, capacity);
    return new ProtobufArrayList(newArray, this.size);
  }
  
  public boolean add(E element) {
    ensureIsMutable();
    if (this.size == this.array.length) {
      int length = this.size * 3 / 2 + 1;
      E[] newArray = Arrays.copyOf(this.array, length);
      this.array = newArray;
    } 
    this.array[this.size++] = element;
    this.modCount++;
    return true;
  }
  
  public void add(int index, E element) {
    ensureIsMutable();
    if (index < 0 || index > this.size)
      throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index)); 
    if (this.size < this.array.length) {
      System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
    } else {
      int length = this.size * 3 / 2 + 1;
      E[] newArray = createArray(length);
      System.arraycopy(this.array, 0, newArray, 0, index);
      System.arraycopy(this.array, index, newArray, index + 1, this.size - index);
      this.array = newArray;
    } 
    this.array[index] = element;
    this.size++;
    this.modCount++;
  }
  
  public E get(int index) {
    ensureIndexInRange(index);
    return this.array[index];
  }
  
  public E remove(int index) {
    ensureIsMutable();
    ensureIndexInRange(index);
    E value = this.array[index];
    if (index < this.size - 1)
      System.arraycopy(this.array, index + 1, this.array, index, this.size - index - 1); 
    this.size--;
    this.modCount++;
    return value;
  }
  
  public E set(int index, E element) {
    ensureIsMutable();
    ensureIndexInRange(index);
    E toReturn = this.array[index];
    this.array[index] = element;
    this.modCount++;
    return toReturn;
  }
  
  public int size() {
    return this.size;
  }
  
  private static <E> E[] createArray(int capacity) {
    return (E[])new Object[capacity];
  }
  
  private void ensureIndexInRange(int index) {
    if (index < 0 || index >= this.size)
      throw new IndexOutOfBoundsException(makeOutOfBoundsExceptionMessage(index)); 
  }
  
  private String makeOutOfBoundsExceptionMessage(int index) {
    return "Index:" + index + ", Size:" + this.size;
  }
}
