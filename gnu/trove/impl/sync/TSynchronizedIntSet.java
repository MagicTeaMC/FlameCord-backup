package gnu.trove.impl.sync;

import gnu.trove.TIntCollection;
import gnu.trove.set.TIntSet;

public class TSynchronizedIntSet extends TSynchronizedIntCollection implements TIntSet {
  private static final long serialVersionUID = 487447009682186044L;
  
  public TSynchronizedIntSet(TIntSet s) {
    super((TIntCollection)s);
  }
  
  public TSynchronizedIntSet(TIntSet s, Object mutex) {
    super((TIntCollection)s, mutex);
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
