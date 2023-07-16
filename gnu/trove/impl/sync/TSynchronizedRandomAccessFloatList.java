package gnu.trove.impl.sync;

import gnu.trove.list.TFloatList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessFloatList extends TSynchronizedFloatList implements RandomAccess {
  static final long serialVersionUID = 1530674583602358482L;
  
  public TSynchronizedRandomAccessFloatList(TFloatList list) {
    super(list);
  }
  
  public TSynchronizedRandomAccessFloatList(TFloatList list, Object mutex) {
    super(list, mutex);
  }
  
  public TFloatList subList(int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return new TSynchronizedRandomAccessFloatList(this.list
          .subList(fromIndex, toIndex), this.mutex);
    } 
  }
  
  private Object writeReplace() {
    return new TSynchronizedFloatList(this.list);
  }
}
