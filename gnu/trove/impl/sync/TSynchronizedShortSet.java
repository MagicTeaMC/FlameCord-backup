package gnu.trove.impl.sync;

import gnu.trove.TShortCollection;
import gnu.trove.set.TShortSet;

public class TSynchronizedShortSet extends TSynchronizedShortCollection implements TShortSet {
  private static final long serialVersionUID = 487447009682186044L;
  
  public TSynchronizedShortSet(TShortSet s) {
    super((TShortCollection)s);
  }
  
  public TSynchronizedShortSet(TShortSet s, Object mutex) {
    super((TShortCollection)s, mutex);
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
