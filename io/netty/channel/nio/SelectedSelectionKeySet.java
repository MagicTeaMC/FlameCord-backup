package io.netty.channel.nio;

import java.nio.channels.SelectionKey;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class SelectedSelectionKeySet extends AbstractSet<SelectionKey> {
  SelectionKey[] keys = new SelectionKey[1024];
  
  int size;
  
  public boolean add(SelectionKey o) {
    if (o == null)
      return false; 
    if (this.size == this.keys.length)
      increaseCapacity(); 
    this.keys[this.size++] = o;
    return true;
  }
  
  public boolean remove(Object o) {
    return false;
  }
  
  public boolean contains(Object o) {
    SelectionKey[] array = this.keys;
    for (int i = 0, s = this.size; i < s; i++) {
      SelectionKey k = array[i];
      if (k.equals(o))
        return true; 
    } 
    return false;
  }
  
  public int size() {
    return this.size;
  }
  
  public Iterator<SelectionKey> iterator() {
    return new Iterator<SelectionKey>() {
        private int idx;
        
        public boolean hasNext() {
          return (this.idx < SelectedSelectionKeySet.this.size);
        }
        
        public SelectionKey next() {
          if (!hasNext())
            throw new NoSuchElementException(); 
          return SelectedSelectionKeySet.this.keys[this.idx++];
        }
        
        public void remove() {
          throw new UnsupportedOperationException();
        }
      };
  }
  
  void reset() {
    reset(0);
  }
  
  void reset(int start) {
    Arrays.fill((Object[])this.keys, start, this.size, (Object)null);
    this.size = 0;
  }
  
  private void increaseCapacity() {
    SelectionKey[] newKeys = new SelectionKey[this.keys.length << 1];
    System.arraycopy(this.keys, 0, newKeys, 0, this.size);
    this.keys = newKeys;
  }
}
