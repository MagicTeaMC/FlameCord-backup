package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Queue;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingQueue<E> extends ForwardingCollection<E> implements Queue<E> {
  @CanIgnoreReturnValue
  public boolean offer(@ParametricNullness E o) {
    return delegate().offer(o);
  }
  
  @CheckForNull
  @CanIgnoreReturnValue
  public E poll() {
    return delegate().poll();
  }
  
  @ParametricNullness
  @CanIgnoreReturnValue
  public E remove() {
    return delegate().remove();
  }
  
  @CheckForNull
  public E peek() {
    return delegate().peek();
  }
  
  @ParametricNullness
  public E element() {
    return delegate().element();
  }
  
  protected boolean standardOffer(@ParametricNullness E e) {
    try {
      return add(e);
    } catch (IllegalStateException caught) {
      return false;
    } 
  }
  
  @CheckForNull
  protected E standardPeek() {
    try {
      return element();
    } catch (NoSuchElementException caught) {
      return null;
    } 
  }
  
  @CheckForNull
  protected E standardPoll() {
    try {
      return remove();
    } catch (NoSuchElementException caught) {
      return null;
    } 
  }
  
  protected abstract Queue<E> delegate();
}
