package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import java.io.Serializable;
import java.util.Comparator;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable = true)
final class GeneralRange<T> implements Serializable {
  private final Comparator<? super T> comparator;
  
  private final boolean hasLowerBound;
  
  @CheckForNull
  private final T lowerEndpoint;
  
  private final BoundType lowerBoundType;
  
  private final boolean hasUpperBound;
  
  @CheckForNull
  private final T upperEndpoint;
  
  private final BoundType upperBoundType;
  
  @CheckForNull
  private transient GeneralRange<T> reverse;
  
  static <T extends Comparable> GeneralRange<T> from(Range<T> range) {
    C c1 = range.hasLowerBound() ? (C)range.lowerEndpoint() : null;
    BoundType lowerBoundType = range.hasLowerBound() ? range.lowerBoundType() : BoundType.OPEN;
    C c2 = range.hasUpperBound() ? (C)range.upperEndpoint() : null;
    BoundType upperBoundType = range.hasUpperBound() ? range.upperBoundType() : BoundType.OPEN;
    return new GeneralRange<>(
        Ordering.natural(), range
        .hasLowerBound(), (T)c1, lowerBoundType, range
        
        .hasUpperBound(), (T)c2, upperBoundType);
  }
  
  static <T> GeneralRange<T> all(Comparator<? super T> comparator) {
    return new GeneralRange<>(comparator, false, null, BoundType.OPEN, false, null, BoundType.OPEN);
  }
  
  static <T> GeneralRange<T> downTo(Comparator<? super T> comparator, @ParametricNullness T endpoint, BoundType boundType) {
    return new GeneralRange<>(comparator, true, endpoint, boundType, false, null, BoundType.OPEN);
  }
  
  static <T> GeneralRange<T> upTo(Comparator<? super T> comparator, @ParametricNullness T endpoint, BoundType boundType) {
    return new GeneralRange<>(comparator, false, null, BoundType.OPEN, true, endpoint, boundType);
  }
  
  static <T> GeneralRange<T> range(Comparator<? super T> comparator, @ParametricNullness T lower, BoundType lowerType, @ParametricNullness T upper, BoundType upperType) {
    return new GeneralRange<>(comparator, true, lower, lowerType, true, upper, upperType);
  }
  
  private GeneralRange(Comparator<? super T> comparator, boolean hasLowerBound, @CheckForNull T lowerEndpoint, BoundType lowerBoundType, boolean hasUpperBound, @CheckForNull T upperEndpoint, BoundType upperBoundType) {
    this.comparator = (Comparator<? super T>)Preconditions.checkNotNull(comparator);
    this.hasLowerBound = hasLowerBound;
    this.hasUpperBound = hasUpperBound;
    this.lowerEndpoint = lowerEndpoint;
    this.lowerBoundType = (BoundType)Preconditions.checkNotNull(lowerBoundType);
    this.upperEndpoint = upperEndpoint;
    this.upperBoundType = (BoundType)Preconditions.checkNotNull(upperBoundType);
    if (hasLowerBound)
      comparator.compare(
          NullnessCasts.uncheckedCastNullableTToT(lowerEndpoint), NullnessCasts.uncheckedCastNullableTToT(lowerEndpoint)); 
    if (hasUpperBound)
      comparator.compare(
          NullnessCasts.uncheckedCastNullableTToT(upperEndpoint), NullnessCasts.uncheckedCastNullableTToT(upperEndpoint)); 
    if (hasLowerBound && hasUpperBound) {
      int cmp = comparator.compare(
          NullnessCasts.uncheckedCastNullableTToT(lowerEndpoint), NullnessCasts.uncheckedCastNullableTToT(upperEndpoint));
      Preconditions.checkArgument((cmp <= 0), "lowerEndpoint (%s) > upperEndpoint (%s)", lowerEndpoint, upperEndpoint);
      if (cmp == 0)
        Preconditions.checkArgument((lowerBoundType != BoundType.OPEN || upperBoundType != BoundType.OPEN)); 
    } 
  }
  
  Comparator<? super T> comparator() {
    return this.comparator;
  }
  
  boolean hasLowerBound() {
    return this.hasLowerBound;
  }
  
  boolean hasUpperBound() {
    return this.hasUpperBound;
  }
  
  boolean isEmpty() {
    return ((hasUpperBound() && tooLow(NullnessCasts.uncheckedCastNullableTToT(getUpperEndpoint()))) || (
      hasLowerBound() && tooHigh(NullnessCasts.uncheckedCastNullableTToT(getLowerEndpoint()))));
  }
  
  boolean tooLow(@ParametricNullness T t) {
    if (!hasLowerBound())
      return false; 
    T lbound = NullnessCasts.uncheckedCastNullableTToT(getLowerEndpoint());
    int cmp = this.comparator.compare(t, lbound);
    return ((cmp < 0) ? 1 : 0) | ((cmp == 0)) & ((getLowerBoundType() == BoundType.OPEN));
  }
  
  boolean tooHigh(@ParametricNullness T t) {
    if (!hasUpperBound())
      return false; 
    T ubound = NullnessCasts.uncheckedCastNullableTToT(getUpperEndpoint());
    int cmp = this.comparator.compare(t, ubound);
    return ((cmp > 0) ? 1 : 0) | ((cmp == 0)) & ((getUpperBoundType() == BoundType.OPEN));
  }
  
  boolean contains(@ParametricNullness T t) {
    return (!tooLow(t) && !tooHigh(t));
  }
  
  GeneralRange<T> intersect(GeneralRange<T> other) {
    Preconditions.checkNotNull(other);
    Preconditions.checkArgument(this.comparator.equals(other.comparator));
    boolean hasLowBound = this.hasLowerBound;
    T lowEnd = getLowerEndpoint();
    BoundType lowType = getLowerBoundType();
    if (!hasLowerBound()) {
      hasLowBound = other.hasLowerBound;
      lowEnd = other.getLowerEndpoint();
      lowType = other.getLowerBoundType();
    } else if (other.hasLowerBound()) {
      int cmp = this.comparator.compare(getLowerEndpoint(), other.getLowerEndpoint());
      if (cmp < 0 || (cmp == 0 && other.getLowerBoundType() == BoundType.OPEN)) {
        lowEnd = other.getLowerEndpoint();
        lowType = other.getLowerBoundType();
      } 
    } 
    boolean hasUpBound = this.hasUpperBound;
    T upEnd = getUpperEndpoint();
    BoundType upType = getUpperBoundType();
    if (!hasUpperBound()) {
      hasUpBound = other.hasUpperBound;
      upEnd = other.getUpperEndpoint();
      upType = other.getUpperBoundType();
    } else if (other.hasUpperBound()) {
      int cmp = this.comparator.compare(getUpperEndpoint(), other.getUpperEndpoint());
      if (cmp > 0 || (cmp == 0 && other.getUpperBoundType() == BoundType.OPEN)) {
        upEnd = other.getUpperEndpoint();
        upType = other.getUpperBoundType();
      } 
    } 
    if (hasLowBound && hasUpBound) {
      int cmp = this.comparator.compare(lowEnd, upEnd);
      if (cmp > 0 || (cmp == 0 && lowType == BoundType.OPEN && upType == BoundType.OPEN)) {
        lowEnd = upEnd;
        lowType = BoundType.OPEN;
        upType = BoundType.CLOSED;
      } 
    } 
    return new GeneralRange(this.comparator, hasLowBound, lowEnd, lowType, hasUpBound, upEnd, upType);
  }
  
  public boolean equals(@CheckForNull Object obj) {
    if (obj instanceof GeneralRange) {
      GeneralRange<?> r = (GeneralRange)obj;
      return (this.comparator.equals(r.comparator) && this.hasLowerBound == r.hasLowerBound && this.hasUpperBound == r.hasUpperBound && 
        
        getLowerBoundType().equals(r.getLowerBoundType()) && 
        getUpperBoundType().equals(r.getUpperBoundType()) && 
        Objects.equal(getLowerEndpoint(), r.getLowerEndpoint()) && 
        Objects.equal(getUpperEndpoint(), r.getUpperEndpoint()));
    } 
    return false;
  }
  
  public int hashCode() {
    return Objects.hashCode(new Object[] { this.comparator, 
          
          getLowerEndpoint(), 
          getLowerBoundType(), 
          getUpperEndpoint(), 
          getUpperBoundType() });
  }
  
  GeneralRange<T> reverse() {
    GeneralRange<T> result = this.reverse;
    if (result == null) {
      result = new GeneralRange(Ordering.<T>from(this.comparator).reverse(), this.hasUpperBound, getUpperEndpoint(), getUpperBoundType(), this.hasLowerBound, getLowerEndpoint(), getLowerBoundType());
      result.reverse = this;
      return this.reverse = result;
    } 
    return result;
  }
  
  public String toString() {
    String str1 = String.valueOf(this.comparator);
    byte b1 = (this.lowerBoundType == BoundType.CLOSED) ? 91 : 40;
    String str2 = String.valueOf(this.hasLowerBound ? this.lowerEndpoint : "-∞");
    String str3 = String.valueOf(this.hasUpperBound ? this.upperEndpoint : "∞");
    byte b2 = (this.upperBoundType == BoundType.CLOSED) ? 93 : 41;
    return (new StringBuilder(4 + String.valueOf(str1).length() + String.valueOf(str2).length() + String.valueOf(str3).length())).append(str1).append(":").append(b1).append(str2).append(',').append(str3).append(b2).toString();
  }
  
  @CheckForNull
  T getLowerEndpoint() {
    return this.lowerEndpoint;
  }
  
  BoundType getLowerBoundType() {
    return this.lowerBoundType;
  }
  
  @CheckForNull
  T getUpperEndpoint() {
    return this.upperEndpoint;
  }
  
  BoundType getUpperBoundType() {
    return this.upperBoundType;
  }
}
