package gnu.trove.impl.sync;

import gnu.trove.TLongCollection;
import gnu.trove.set.TLongSet;

public class TSynchronizedLongSet extends TSynchronizedLongCollection implements TLongSet {
  private static final long serialVersionUID = 487447009682186044L;
  
  public TSynchronizedLongSet(TLongSet s) {
    super((TLongCollection)s);
  }
  
  public TSynchronizedLongSet(TLongSet s, Object mutex) {
    super((TLongCollection)s, mutex);
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
