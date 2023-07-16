package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import java.util.ListIterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class TransformedListIterator<F, T> extends TransformedIterator<F, T> implements ListIterator<T> {
  TransformedListIterator(ListIterator<? extends F> backingIterator) {
    super(backingIterator);
  }
  
  private ListIterator<? extends F> backingIterator() {
    return Iterators.cast(this.backingIterator);
  }
  
  public final boolean hasPrevious() {
    return backingIterator().hasPrevious();
  }
  
  @ParametricNullness
  public final T previous() {
    return transform(backingIterator().previous());
  }
  
  public final int nextIndex() {
    return backingIterator().nextIndex();
  }
  
  public final int previousIndex() {
    return backingIterator().previousIndex();
  }
  
  public void set(@ParametricNullness T element) {
    throw new UnsupportedOperationException();
  }
  
  public void add(@ParametricNullness T element) {
    throw new UnsupportedOperationException();
  }
}