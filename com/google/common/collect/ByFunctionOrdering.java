package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
final class ByFunctionOrdering<F, T> extends Ordering<F> implements Serializable {
  final Function<F, ? extends T> function;
  
  final Ordering<T> ordering;
  
  private static final long serialVersionUID = 0L;
  
  ByFunctionOrdering(Function<F, ? extends T> function, Ordering<T> ordering) {
    this.function = (Function<F, ? extends T>)Preconditions.checkNotNull(function);
    this.ordering = (Ordering<T>)Preconditions.checkNotNull(ordering);
  }
  
  public int compare(@ParametricNullness F left, @ParametricNullness F right) {
    return this.ordering.compare((T)this.function.apply(left), (T)this.function.apply(right));
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object == this)
      return true; 
    if (object instanceof ByFunctionOrdering) {
      ByFunctionOrdering<?, ?> that = (ByFunctionOrdering<?, ?>)object;
      return (this.function.equals(that.function) && this.ordering.equals(that.ordering));
    } 
    return false;
  }
  
  public int hashCode() {
    return Objects.hashCode(new Object[] { this.function, this.ordering });
  }
  
  public String toString() {
    String str1 = String.valueOf(this.ordering), str2 = String.valueOf(this.function);
    return (new StringBuilder(13 + String.valueOf(str1).length() + String.valueOf(str2).length())).append(str1).append(".onResultOf(").append(str2).append(")").toString();
  }
}
