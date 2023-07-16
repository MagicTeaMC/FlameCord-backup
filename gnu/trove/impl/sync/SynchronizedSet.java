package gnu.trove.impl.sync;

import java.util.Set;

class SynchronizedSet<E> extends SynchronizedCollection<E> implements Set<E> {
  private static final long serialVersionUID = 487447009682186044L;
  
  SynchronizedSet(Set<E> s, Object mutex) {
    super(s, mutex);
  }
  
  public boolean equals(Object o) {
    synchronized (this.mutex) {
      return this.c.equals(o);
    } 
  }
  
  public int hashCode() {
    synchronized (this.mutex) {
      return this.c.hashCode();
    } 
  }
}
