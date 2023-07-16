package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Objects;
import java.util.Map;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
abstract class AbstractMapEntry<K, V> implements Map.Entry<K, V> {
  @ParametricNullness
  public abstract K getKey();
  
  @ParametricNullness
  public abstract V getValue();
  
  @ParametricNullness
  public V setValue(@ParametricNullness V value) {
    throw new UnsupportedOperationException();
  }
  
  public boolean equals(@CheckForNull Object object) {
    if (object instanceof Map.Entry) {
      Map.Entry<?, ?> that = (Map.Entry<?, ?>)object;
      return (Objects.equal(getKey(), that.getKey()) && 
        Objects.equal(getValue(), that.getValue()));
    } 
    return false;
  }
  
  public int hashCode() {
    K k = getKey();
    V v = getValue();
    return ((k == null) ? 0 : k.hashCode()) ^ ((v == null) ? 0 : v.hashCode());
  }
  
  public String toString() {
    String str1 = String.valueOf(getKey()), str2 = String.valueOf(getValue());
    return (new StringBuilder(1 + String.valueOf(str1).length() + String.valueOf(str2).length())).append(str1).append("=").append(str2).toString();
  }
}
