package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Comparator;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
final class ComparatorOrdering<T> extends Ordering<T> implements Serializable {
  final Comparator<T> comparator;
  
  private static final long serialVersionUID = 0L;
  
  ComparatorOrdering(Comparator<T> comparator) {
    this.comparator = (Comparator<T>)Preconditions.checkNotNull(comparator);
  }
  
  public int compare(@ParametricNullness T a, @ParametricNullness T b) {
    return this.comparator.compare(a, b);
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object == this)
      return true; 
    if (object instanceof ComparatorOrdering) {
      ComparatorOrdering<?> that = (ComparatorOrdering)object;
      return this.comparator.equals(that.comparator);
    } 
    return false;
  }
  
  public int hashCode() {
    return this.comparator.hashCode();
  }
  
  public String toString() {
    return this.comparator.toString();
  }
}
