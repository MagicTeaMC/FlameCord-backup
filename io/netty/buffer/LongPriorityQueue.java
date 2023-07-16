package io.netty.buffer;

import java.util.Arrays;

final class LongPriorityQueue {
  public static final int NO_VALUE = -1;
  
  private long[] array = new long[9];
  
  private int size;
  
  public void offer(long handle) {
    if (handle == -1L)
      throw new IllegalArgumentException("The NO_VALUE (-1) cannot be added to the queue."); 
    this.size++;
    if (this.size == this.array.length)
      this.array = Arrays.copyOf(this.array, 1 + (this.array.length - 1) * 2); 
    this.array[this.size] = handle;
    lift(this.size);
  }
  
  public void remove(long value) {
    for (int i = 1; i <= this.size; i++) {
      if (this.array[i] == value) {
        this.array[i] = this.array[this.size--];
        lift(i);
        sink(i);
        return;
      } 
    } 
  }
  
  public long peek() {
    if (this.size == 0)
      return -1L; 
    return this.array[1];
  }
  
  public long poll() {
    if (this.size == 0)
      return -1L; 
    long val = this.array[1];
    this.array[1] = this.array[this.size];
    this.array[this.size] = 0L;
    this.size--;
    sink(1);
    return val;
  }
  
  public boolean isEmpty() {
    return (this.size == 0);
  }
  
  private void lift(int index) {
    int parentIndex;
    while (index > 1 && subord(parentIndex = index >> 1, index)) {
      swap(index, parentIndex);
      index = parentIndex;
    } 
  }
  
  private void sink(int index) {
    int child;
    while ((child = index << 1) <= this.size) {
      if (child < this.size && subord(child, child + 1))
        child++; 
      if (!subord(index, child))
        break; 
      swap(index, child);
      index = child;
    } 
  }
  
  private boolean subord(int a, int b) {
    return (this.array[a] > this.array[b]);
  }
  
  private void swap(int a, int b) {
    long value = this.array[a];
    this.array[a] = this.array[b];
    this.array[b] = value;
  }
}
