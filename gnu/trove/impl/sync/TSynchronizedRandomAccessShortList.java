package gnu.trove.impl.sync;

import gnu.trove.list.TShortList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessShortList extends TSynchronizedShortList implements RandomAccess {
  static final long serialVersionUID = 1530674583602358482L;
  
  public TSynchronizedRandomAccessShortList(TShortList list) {
    super(list);
  }
  
  public TSynchronizedRandomAccessShortList(TShortList list, Object mutex) {
    super(list, mutex);
  }
  
  public TShortList subList(int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return new TSynchronizedRandomAccessShortList(this.list
          .subList(fromIndex, toIndex), this.mutex);
    } 
  }
  
  private Object writeReplace() {
    return new TSynchronizedShortList(this.list);
  }
}
