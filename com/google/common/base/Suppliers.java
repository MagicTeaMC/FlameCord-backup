package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.VisibleForTesting;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Suppliers {
  public static <F, T> Supplier<T> compose(Function<? super F, T> function, Supplier<F> supplier) {
    return new SupplierComposition<>(function, supplier);
  }
  
  private static class SupplierComposition<F, T> implements Supplier<T>, Serializable {
    final Function<? super F, T> function;
    
    final Supplier<F> supplier;
    
    private static final long serialVersionUID = 0L;
    
    SupplierComposition(Function<? super F, T> function, Supplier<F> supplier) {
      this.function = Preconditions.<Function<? super F, T>>checkNotNull(function);
      this.supplier = Preconditions.<Supplier<F>>checkNotNull(supplier);
    }
    
    @ParametricNullness
    public T get() {
      return this.function.apply(this.supplier.get());
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof SupplierComposition) {
        SupplierComposition<?, ?> that = (SupplierComposition<?, ?>)obj;
        return (this.function.equals(that.function) && this.supplier.equals(that.supplier));
      } 
      return false;
    }
    
    public int hashCode() {
      return Objects.hashCode(new Object[] { this.function, this.supplier });
    }
    
    public String toString() {
      String str1 = String.valueOf(this.function), str2 = String.valueOf(this.supplier);
      return (new StringBuilder(21 + String.valueOf(str1).length() + String.valueOf(str2).length())).append("Suppliers.compose(").append(str1).append(", ").append(str2).append(")").toString();
    }
  }
  
  public static <T> Supplier<T> memoize(Supplier<T> delegate) {
    if (delegate instanceof NonSerializableMemoizingSupplier || delegate instanceof MemoizingSupplier)
      return delegate; 
    return (delegate instanceof Serializable) ? 
      new MemoizingSupplier<>(delegate) : 
      new NonSerializableMemoizingSupplier<>(delegate);
  }
  
  @VisibleForTesting
  static class MemoizingSupplier<T> implements Supplier<T>, Serializable {
    final Supplier<T> delegate;
    
    volatile transient boolean initialized;
    
    @CheckForNull
    transient T value;
    
    private static final long serialVersionUID = 0L;
    
    MemoizingSupplier(Supplier<T> delegate) {
      this.delegate = Preconditions.<Supplier<T>>checkNotNull(delegate);
    }
    
    @ParametricNullness
    public T get() {
      if (!this.initialized)
        synchronized (this) {
          if (!this.initialized) {
            T t = this.delegate.get();
            this.value = t;
            this.initialized = true;
            return t;
          } 
        }  
      return NullnessCasts.uncheckedCastNullableTToT(this.value);
    }
    
    public String toString() {
      String str = String.valueOf(this.value);
      str = String.valueOf(this.initialized ? (new StringBuilder(25 + String.valueOf(str).length())).append("<supplier that returned ").append(str).append(">").toString() : this.delegate);
      return (new StringBuilder(19 + String.valueOf(str).length())).append("Suppliers.memoize(").append(str).append(")").toString();
    }
  }
  
  @VisibleForTesting
  static class NonSerializableMemoizingSupplier<T> implements Supplier<T> {
    @CheckForNull
    volatile Supplier<T> delegate;
    
    volatile boolean initialized;
    
    @CheckForNull
    T value;
    
    NonSerializableMemoizingSupplier(Supplier<T> delegate) {
      this.delegate = Preconditions.<Supplier<T>>checkNotNull(delegate);
    }
    
    @ParametricNullness
    public T get() {
      if (!this.initialized)
        synchronized (this) {
          if (!this.initialized) {
            T t = ((Supplier<T>)Objects.<Supplier<T>>requireNonNull(this.delegate)).get();
            this.value = t;
            this.initialized = true;
            this.delegate = null;
            return t;
          } 
        }  
      return NullnessCasts.uncheckedCastNullableTToT(this.value);
    }
    
    public String toString() {
      Supplier<T> delegate = this.delegate;
      String str = String.valueOf(this.value);
      str = String.valueOf((delegate == null) ? (new StringBuilder(25 + String.valueOf(str).length())).append("<supplier that returned ").append(str).append(">").toString() : delegate);
      return (new StringBuilder(19 + String.valueOf(str).length())).append("Suppliers.memoize(").append(str).append(")").toString();
    }
  }
  
  public static <T> Supplier<T> memoizeWithExpiration(Supplier<T> delegate, long duration, TimeUnit unit) {
    return new ExpiringMemoizingSupplier<>(delegate, duration, unit);
  }
  
  @VisibleForTesting
  static class ExpiringMemoizingSupplier<T> implements Supplier<T>, Serializable {
    final Supplier<T> delegate;
    
    final long durationNanos;
    
    @CheckForNull
    volatile transient T value;
    
    volatile transient long expirationNanos;
    
    private static final long serialVersionUID = 0L;
    
    ExpiringMemoizingSupplier(Supplier<T> delegate, long duration, TimeUnit unit) {
      this.delegate = Preconditions.<Supplier<T>>checkNotNull(delegate);
      this.durationNanos = unit.toNanos(duration);
      Preconditions.checkArgument((duration > 0L), "duration (%s %s) must be > 0", duration, unit);
    }
    
    @ParametricNullness
    public T get() {
      long nanos = this.expirationNanos;
      long now = Platform.systemNanoTime();
      if (nanos == 0L || now - nanos >= 0L)
        synchronized (this) {
          if (nanos == this.expirationNanos) {
            T t = this.delegate.get();
            this.value = t;
            nanos = now + this.durationNanos;
            this.expirationNanos = (nanos == 0L) ? 1L : nanos;
            return t;
          } 
        }  
      return NullnessCasts.uncheckedCastNullableTToT(this.value);
    }
    
    public String toString() {
      String str = String.valueOf(this.delegate);
      long l = this.durationNanos;
      return (new StringBuilder(62 + String.valueOf(str).length())).append("Suppliers.memoizeWithExpiration(").append(str).append(", ").append(l).append(", NANOS)").toString();
    }
  }
  
  public static <T> Supplier<T> ofInstance(@ParametricNullness T instance) {
    return new SupplierOfInstance<>(instance);
  }
  
  private static class SupplierOfInstance<T> implements Supplier<T>, Serializable {
    @ParametricNullness
    final T instance;
    
    private static final long serialVersionUID = 0L;
    
    SupplierOfInstance(@ParametricNullness T instance) {
      this.instance = instance;
    }
    
    @ParametricNullness
    public T get() {
      return this.instance;
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof SupplierOfInstance) {
        SupplierOfInstance<?> that = (SupplierOfInstance)obj;
        return Objects.equal(this.instance, that.instance);
      } 
      return false;
    }
    
    public int hashCode() {
      return Objects.hashCode(new Object[] { this.instance });
    }
    
    public String toString() {
      String str = String.valueOf(this.instance);
      return (new StringBuilder(22 + String.valueOf(str).length())).append("Suppliers.ofInstance(").append(str).append(")").toString();
    }
  }
  
  public static <T> Supplier<T> synchronizedSupplier(Supplier<T> delegate) {
    return new ThreadSafeSupplier<>(delegate);
  }
  
  private static class ThreadSafeSupplier<T> implements Supplier<T>, Serializable {
    final Supplier<T> delegate;
    
    private static final long serialVersionUID = 0L;
    
    ThreadSafeSupplier(Supplier<T> delegate) {
      this.delegate = Preconditions.<Supplier<T>>checkNotNull(delegate);
    }
    
    @ParametricNullness
    public T get() {
      synchronized (this.delegate) {
        return this.delegate.get();
      } 
    }
    
    public String toString() {
      String str = String.valueOf(this.delegate);
      return (new StringBuilder(32 + String.valueOf(str).length())).append("Suppliers.synchronizedSupplier(").append(str).append(")").toString();
    }
  }
  
  public static <T> Function<Supplier<T>, T> supplierFunction() {
    SupplierFunction<T> sf = SupplierFunctionImpl.INSTANCE;
    return sf;
  }
  
  private static interface SupplierFunction<T> extends Function<Supplier<T>, T> {}
  
  private enum SupplierFunctionImpl implements SupplierFunction<Object> {
    INSTANCE;
    
    @CheckForNull
    public Object apply(Supplier<Object> input) {
      return input.get();
    }
    
    public String toString() {
      return "Suppliers.supplierFunction()";
    }
  }
}
