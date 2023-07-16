package gnu.trove.impl.sync;

import gnu.trove.TByteCollection;
import gnu.trove.set.TByteSet;

public class TSynchronizedByteSet extends TSynchronizedByteCollection implements TByteSet {
  private static final long serialVersionUID = 487447009682186044L;
  
  public TSynchronizedByteSet(TByteSet s) {
    super((TByteCollection)s);
  }
  
  public TSynchronizedByteSet(TByteSet s, Object mutex) {
    super((TByteCollection)s, mutex);
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
