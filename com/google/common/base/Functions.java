package com.google.common.base;

import com.google.common.annotations.GwtCompatible;
import java.io.Serializable;
import java.util.Map;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public final class Functions {
  public static Function<Object, String> toStringFunction() {
    return ToStringFunction.INSTANCE;
  }
  
  private enum ToStringFunction implements Function<Object, String> {
    INSTANCE;
    
    public String apply(Object o) {
      Preconditions.checkNotNull(o);
      return o.toString();
    }
    
    public String toString() {
      return "Functions.toStringFunction()";
    }
  }
  
  public static <E> Function<E, E> identity() {
    return IdentityFunction.INSTANCE;
  }
  
  private enum IdentityFunction implements Function<Object, Object> {
    INSTANCE;
    
    @CheckForNull
    public Object apply(@CheckForNull Object o) {
      return o;
    }
    
    public String toString() {
      return "Functions.identity()";
    }
  }
  
  public static <K, V> Function<K, V> forMap(Map<K, V> map) {
    return new FunctionForMapNoDefault<>(map);
  }
  
  public static <K, V> Function<K, V> forMap(Map<K, ? extends V> map, @ParametricNullness V defaultValue) {
    return new ForMapWithDefault<>(map, defaultValue);
  }
  
  private static class FunctionForMapNoDefault<K, V> implements Function<K, V>, Serializable {
    final Map<K, V> map;
    
    private static final long serialVersionUID = 0L;
    
    FunctionForMapNoDefault(Map<K, V> map) {
      this.map = Preconditions.<Map<K, V>>checkNotNull(map);
    }
    
    @ParametricNullness
    public V apply(@ParametricNullness K key) {
      V result = this.map.get(key);
      Preconditions.checkArgument((result != null || this.map.containsKey(key)), "Key '%s' not present in map", key);
      return NullnessCasts.uncheckedCastNullableTToT(result);
    }
    
    public boolean equals(@CheckForNull Object o) {
      if (o instanceof FunctionForMapNoDefault) {
        FunctionForMapNoDefault<?, ?> that = (FunctionForMapNoDefault<?, ?>)o;
        return this.map.equals(that.map);
      } 
      return false;
    }
    
    public int hashCode() {
      return this.map.hashCode();
    }
    
    public String toString() {
      String str = String.valueOf(this.map);
      return (new StringBuilder(18 + String.valueOf(str).length())).append("Functions.forMap(").append(str).append(")").toString();
    }
  }
  
  private static class ForMapWithDefault<K, V> implements Function<K, V>, Serializable {
    final Map<K, ? extends V> map;
    
    @ParametricNullness
    final V defaultValue;
    
    private static final long serialVersionUID = 0L;
    
    ForMapWithDefault(Map<K, ? extends V> map, @ParametricNullness V defaultValue) {
      this.map = Preconditions.<Map<K, ? extends V>>checkNotNull(map);
      this.defaultValue = defaultValue;
    }
    
    @ParametricNullness
    public V apply(@ParametricNullness K key) {
      V result = this.map.get(key);
      return (result != null || this.map.containsKey(key)) ? 
        NullnessCasts.<V>uncheckedCastNullableTToT(result) : 
        this.defaultValue;
    }
    
    public boolean equals(@CheckForNull Object o) {
      if (o instanceof ForMapWithDefault) {
        ForMapWithDefault<?, ?> that = (ForMapWithDefault<?, ?>)o;
        return (this.map.equals(that.map) && Objects.equal(this.defaultValue, that.defaultValue));
      } 
      return false;
    }
    
    public int hashCode() {
      return Objects.hashCode(new Object[] { this.map, this.defaultValue });
    }
    
    public String toString() {
      String str1 = String.valueOf(this.map), str2 = String.valueOf(this.defaultValue);
      return (new StringBuilder(33 + String.valueOf(str1).length() + String.valueOf(str2).length())).append("Functions.forMap(").append(str1).append(", defaultValue=").append(str2).append(")").toString();
    }
  }
  
  public static <A, B, C> Function<A, C> compose(Function<B, C> g, Function<A, ? extends B> f) {
    return new FunctionComposition<>(g, f);
  }
  
  private static class FunctionComposition<A, B, C> implements Function<A, C>, Serializable {
    private final Function<B, C> g;
    
    private final Function<A, ? extends B> f;
    
    private static final long serialVersionUID = 0L;
    
    public FunctionComposition(Function<B, C> g, Function<A, ? extends B> f) {
      this.g = Preconditions.<Function<B, C>>checkNotNull(g);
      this.f = Preconditions.<Function<A, ? extends B>>checkNotNull(f);
    }
    
    @ParametricNullness
    public C apply(@ParametricNullness A a) {
      return this.g.apply(this.f.apply(a));
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof FunctionComposition) {
        FunctionComposition<?, ?, ?> that = (FunctionComposition<?, ?, ?>)obj;
        return (this.f.equals(that.f) && this.g.equals(that.g));
      } 
      return false;
    }
    
    public int hashCode() {
      return this.f.hashCode() ^ this.g.hashCode();
    }
    
    public String toString() {
      String str1 = String.valueOf(this.g), str2 = String.valueOf(this.f);
      return (new StringBuilder(2 + String.valueOf(str1).length() + String.valueOf(str2).length())).append(str1).append("(").append(str2).append(")").toString();
    }
  }
  
  public static <T> Function<T, Boolean> forPredicate(Predicate<T> predicate) {
    return new PredicateFunction<>(predicate);
  }
  
  private static class PredicateFunction<T> implements Function<T, Boolean>, Serializable {
    private final Predicate<T> predicate;
    
    private static final long serialVersionUID = 0L;
    
    private PredicateFunction(Predicate<T> predicate) {
      this.predicate = Preconditions.<Predicate<T>>checkNotNull(predicate);
    }
    
    public Boolean apply(@ParametricNullness T t) {
      return Boolean.valueOf(this.predicate.apply(t));
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof PredicateFunction) {
        PredicateFunction<?> that = (PredicateFunction)obj;
        return this.predicate.equals(that.predicate);
      } 
      return false;
    }
    
    public int hashCode() {
      return this.predicate.hashCode();
    }
    
    public String toString() {
      String str = String.valueOf(this.predicate);
      return (new StringBuilder(24 + String.valueOf(str).length())).append("Functions.forPredicate(").append(str).append(")").toString();
    }
  }
  
  public static <E> Function<Object, E> constant(@ParametricNullness E value) {
    return new ConstantFunction<>(value);
  }
  
  private static class ConstantFunction<E> implements Function<Object, E>, Serializable {
    @ParametricNullness
    private final E value;
    
    private static final long serialVersionUID = 0L;
    
    public ConstantFunction(@ParametricNullness E value) {
      this.value = value;
    }
    
    @ParametricNullness
    public E apply(@CheckForNull Object from) {
      return this.value;
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof ConstantFunction) {
        ConstantFunction<?> that = (ConstantFunction)obj;
        return Objects.equal(this.value, that.value);
      } 
      return false;
    }
    
    public int hashCode() {
      return (this.value == null) ? 0 : this.value.hashCode();
    }
    
    public String toString() {
      String str = String.valueOf(this.value);
      return (new StringBuilder(20 + String.valueOf(str).length())).append("Functions.constant(").append(str).append(")").toString();
    }
  }
  
  public static <F, T> Function<F, T> forSupplier(Supplier<T> supplier) {
    return new SupplierFunction<>(supplier);
  }
  
  private static class SupplierFunction<F, T> implements Function<F, T>, Serializable {
    private final Supplier<T> supplier;
    
    private static final long serialVersionUID = 0L;
    
    private SupplierFunction(Supplier<T> supplier) {
      this.supplier = Preconditions.<Supplier<T>>checkNotNull(supplier);
    }
    
    @ParametricNullness
    public T apply(@ParametricNullness F input) {
      return this.supplier.get();
    }
    
    public boolean equals(@CheckForNull Object obj) {
      if (obj instanceof SupplierFunction) {
        SupplierFunction<?, ?> that = (SupplierFunction<?, ?>)obj;
        return this.supplier.equals(that.supplier);
      } 
      return false;
    }
    
    public int hashCode() {
      return this.supplier.hashCode();
    }
    
    public String toString() {
      String str = String.valueOf(this.supplier);
      return (new StringBuilder(23 + String.valueOf(str).length())).append("Functions.forSupplier(").append(str).append(")").toString();
    }
  }
}
