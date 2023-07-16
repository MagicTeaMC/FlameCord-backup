package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractIterator<T> implements Iterator<T> {
  private State state = State.NOT_READY;
  
  @CheckForNull
  private T next;
  
  @CheckForNull
  protected abstract T computeNext();
  
  private enum State {
    READY, NOT_READY, DONE, FAILED;
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  protected final T endOfData() {
    this.state = State.DONE;
    return null;
  }
  
  public final boolean hasNext() {
    Preconditions.checkState((this.state != State.FAILED));
    switch (this.state) {
      case DONE:
        return false;
      case READY:
        return true;
    } 
    return tryToComputeNext();
  }
  
  private boolean tryToComputeNext() {
    this.state = State.FAILED;
    this.next = computeNext();
    if (this.state != State.DONE) {
      this.state = State.READY;
      return true;
    } 
    return false;
  }
  
  @ParametricNullness
  public final T next() {
    if (!hasNext())
      throw new NoSuchElementException(); 
    this.state = State.NOT_READY;
    T result = NullnessCasts.uncheckedCastNullableTToT(this.next);
    this.next = null;
    return result;
  }
  
  public final void remove() {
    throw new UnsupportedOperationException();
  }
}
