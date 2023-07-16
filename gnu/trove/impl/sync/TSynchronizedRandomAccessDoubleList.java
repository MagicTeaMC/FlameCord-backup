package gnu.trove.impl.sync;

import gnu.trove.list.TDoubleList;
import java.util.RandomAccess;

public class TSynchronizedRandomAccessDoubleList extends TSynchronizedDoubleList implements RandomAccess {
  static final long serialVersionUID = 1530674583602358482L;
  
  public TSynchronizedRandomAccessDoubleList(TDoubleList list) {
    super(list);
  }
  
  public TSynchronizedRandomAccessDoubleList(TDoubleList list, Object mutex) {
    super(list, mutex);
  }
  
  public TDoubleList subList(int fromIndex, int toIndex) {
    synchronized (this.mutex) {
      return new TSynchronizedRandomAccessDoubleList(this.list
          .subList(fromIndex, toIndex), this.mutex);
    } 
  }
  
  private Object writeReplace() {
    return new TSynchronizedDoubleList(this.list);
  }
}
