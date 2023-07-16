package org.jline.reader;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Iterator;
import java.util.ListIterator;

public interface History extends Iterable<History.Entry> {
  default boolean isEmpty() {
    return (size() == 0);
  }
  
  default void add(String line) {
    add(Instant.now(), line);
  }
  
  default boolean isPersistable(Entry entry) {
    return true;
  }
  
  default ListIterator<Entry> iterator() {
    return iterator(first());
  }
  
  default Iterator<Entry> reverseIterator() {
    return reverseIterator(last());
  }
  
  default Iterator<Entry> reverseIterator(final int index) {
    return new Iterator<Entry>() {
        private final ListIterator<History.Entry> it = History.this.iterator(index + 1);
        
        public boolean hasNext() {
          return this.it.hasPrevious();
        }
        
        public History.Entry next() {
          return this.it.previous();
        }
        
        public void remove() {
          this.it.remove();
          History.this.resetIndex();
        }
      };
  }
  
  void attach(LineReader paramLineReader);
  
  void load() throws IOException;
  
  void save() throws IOException;
  
  void write(Path paramPath, boolean paramBoolean) throws IOException;
  
  void append(Path paramPath, boolean paramBoolean) throws IOException;
  
  void read(Path paramPath, boolean paramBoolean) throws IOException;
  
  void purge() throws IOException;
  
  int size();
  
  int index();
  
  int first();
  
  int last();
  
  String get(int paramInt);
  
  void add(Instant paramInstant, String paramString);
  
  ListIterator<Entry> iterator(int paramInt);
  
  String current();
  
  boolean previous();
  
  boolean next();
  
  boolean moveToFirst();
  
  boolean moveToLast();
  
  boolean moveTo(int paramInt);
  
  void moveToEnd();
  
  void resetIndex();
  
  public static interface Entry {
    int index();
    
    Instant time();
    
    String line();
  }
}
