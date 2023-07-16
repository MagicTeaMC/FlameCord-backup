package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.errorprone.annotations.DoNotCall;
import java.util.Iterator;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class UnmodifiableIterator<E> implements Iterator<E> {
  @Deprecated
  @DoNotCall("Always throws UnsupportedOperationException")
  public final void remove() {
    throw new UnsupportedOperationException();
  }
}
