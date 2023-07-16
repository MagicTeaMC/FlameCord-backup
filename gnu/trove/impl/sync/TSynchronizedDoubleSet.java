package gnu.trove.impl.sync;

import gnu.trove.TDoubleCollection;
import gnu.trove.set.TDoubleSet;

public class TSynchronizedDoubleSet extends TSynchronizedDoubleCollection implements TDoubleSet {
  private static final long serialVersionUID = 487447009682186044L;
  
  public TSynchronizedDoubleSet(TDoubleSet s) {
    super((TDoubleCollection)s);
  }
  
  public TSynchronizedDoubleSet(TDoubleSet s, Object mutex) {
    super((TDoubleCollection)s, mutex);
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
