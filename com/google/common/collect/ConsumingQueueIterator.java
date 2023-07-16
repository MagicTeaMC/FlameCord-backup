package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.util.Queue;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
final class ConsumingQueueIterator<T> extends AbstractIterator<T> {
  private final Queue<T> queue;
  
  ConsumingQueueIterator(Queue<T> queue) {
    this.queue = (Queue<T>)Preconditions.checkNotNull(queue);
  }
  
  @CheckForNull
  public T computeNext() {
    if (this.queue.isEmpty())
      return endOfData(); 
    return this.queue.remove();
  }
}
