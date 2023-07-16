package org.eclipse.aether.util.graph.visitor;

import java.util.AbstractList;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

class Stack<E> extends AbstractList<E> implements RandomAccess {
  private E[] elements = (E[])new Object[96];
  
  private int size;
  
  public void push(E element) {
    if (this.size >= this.elements.length) {
      E[] tmp = (E[])new Object[this.size + 64];
      System.arraycopy(this.elements, 0, tmp, 0, this.elements.length);
      this.elements = tmp;
    } 
    this.elements[this.size++] = element;
  }
  
  public E pop() {
    if (this.size <= 0)
      throw new NoSuchElementException(); 
    return this.elements[--this.size];
  }
  
  public E peek() {
    if (this.size <= 0)
      return null; 
    return this.elements[this.size - 1];
  }
  
  public E get(int index) {
    if (index < 0 || index >= this.size)
      throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + this.size); 
    return this.elements[this.size - index - 1];
  }
  
  public int size() {
    return this.size;
  }
}
