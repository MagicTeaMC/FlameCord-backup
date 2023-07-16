package gnu.trove.impl.sync;

import gnu.trove.TFloatCollection;
import gnu.trove.set.TFloatSet;

public class TSynchronizedFloatSet extends TSynchronizedFloatCollection implements TFloatSet {
  private static final long serialVersionUID = 487447009682186044L;
  
  public TSynchronizedFloatSet(TFloatSet s) {
    super((TFloatCollection)s);
  }
  
  public TSynchronizedFloatSet(TFloatSet s, Object mutex) {
    super((TFloatCollection)s, mutex);
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
