package io.netty.util.concurrent;

import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

public class FastThreadLocal<V> {
  private final int index;
  
  public static void removeAll() {
    InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
    if (threadLocalMap == null)
      return; 
    try {
      Object v = threadLocalMap.indexedVariable(InternalThreadLocalMap.VARIABLES_TO_REMOVE_INDEX);
      if (v != null && v != InternalThreadLocalMap.UNSET) {
        Set<FastThreadLocal<?>> variablesToRemove = (Set<FastThreadLocal<?>>)v;
        FastThreadLocal[] arrayOfFastThreadLocal = variablesToRemove.<FastThreadLocal>toArray(new FastThreadLocal[0]);
        for (FastThreadLocal<?> tlv : arrayOfFastThreadLocal)
          tlv.remove(threadLocalMap); 
      } 
    } finally {
      InternalThreadLocalMap.remove();
    } 
  }
  
  public static int size() {
    InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
    if (threadLocalMap == null)
      return 0; 
    return threadLocalMap.size();
  }
  
  public static void destroy() {
    InternalThreadLocalMap.destroy();
  }
  
  private static void addToVariablesToRemove(InternalThreadLocalMap threadLocalMap, FastThreadLocal<?> variable) {
    Set<FastThreadLocal<?>> variablesToRemove;
    Object v = threadLocalMap.indexedVariable(InternalThreadLocalMap.VARIABLES_TO_REMOVE_INDEX);
    if (v == InternalThreadLocalMap.UNSET || v == null) {
      variablesToRemove = Collections.newSetFromMap(new IdentityHashMap<FastThreadLocal<?>, Boolean>());
      threadLocalMap.setIndexedVariable(InternalThreadLocalMap.VARIABLES_TO_REMOVE_INDEX, variablesToRemove);
    } else {
      variablesToRemove = (Set<FastThreadLocal<?>>)v;
    } 
    variablesToRemove.add(variable);
  }
  
  private static void removeFromVariablesToRemove(InternalThreadLocalMap threadLocalMap, FastThreadLocal<?> variable) {
    Object v = threadLocalMap.indexedVariable(InternalThreadLocalMap.VARIABLES_TO_REMOVE_INDEX);
    if (v == InternalThreadLocalMap.UNSET || v == null)
      return; 
    Set<FastThreadLocal<?>> variablesToRemove = (Set<FastThreadLocal<?>>)v;
    variablesToRemove.remove(variable);
  }
  
  public FastThreadLocal() {
    this.index = InternalThreadLocalMap.nextVariableIndex();
  }
  
  public final V get() {
    InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
    Object v = threadLocalMap.indexedVariable(this.index);
    if (v != InternalThreadLocalMap.UNSET)
      return (V)v; 
    return initialize(threadLocalMap);
  }
  
  public final V getIfExists() {
    InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.getIfSet();
    if (threadLocalMap != null) {
      Object v = threadLocalMap.indexedVariable(this.index);
      if (v != InternalThreadLocalMap.UNSET)
        return (V)v; 
    } 
    return null;
  }
  
  public final V get(InternalThreadLocalMap threadLocalMap) {
    Object v = threadLocalMap.indexedVariable(this.index);
    if (v != InternalThreadLocalMap.UNSET)
      return (V)v; 
    return initialize(threadLocalMap);
  }
  
  private V initialize(InternalThreadLocalMap threadLocalMap) {
    V v = null;
    try {
      v = initialValue();
      if (v == InternalThreadLocalMap.UNSET)
        throw new IllegalArgumentException("InternalThreadLocalMap.UNSET can not be initial value."); 
    } catch (Exception e) {
      PlatformDependent.throwException(e);
    } 
    threadLocalMap.setIndexedVariable(this.index, v);
    addToVariablesToRemove(threadLocalMap, this);
    return v;
  }
  
  public final void set(V value) {
    if (value != InternalThreadLocalMap.UNSET) {
      InternalThreadLocalMap threadLocalMap = InternalThreadLocalMap.get();
      setKnownNotUnset(threadLocalMap, value);
    } else {
      remove();
    } 
  }
  
  public final void set(InternalThreadLocalMap threadLocalMap, V value) {
    if (value != InternalThreadLocalMap.UNSET) {
      setKnownNotUnset(threadLocalMap, value);
    } else {
      remove(threadLocalMap);
    } 
  }
  
  private void setKnownNotUnset(InternalThreadLocalMap threadLocalMap, V value) {
    if (threadLocalMap.setIndexedVariable(this.index, value))
      addToVariablesToRemove(threadLocalMap, this); 
  }
  
  public final boolean isSet() {
    return isSet(InternalThreadLocalMap.getIfSet());
  }
  
  public final boolean isSet(InternalThreadLocalMap threadLocalMap) {
    return (threadLocalMap != null && threadLocalMap.isIndexedVariableSet(this.index));
  }
  
  public final void remove() {
    remove(InternalThreadLocalMap.getIfSet());
  }
  
  public final void remove(InternalThreadLocalMap threadLocalMap) {
    if (threadLocalMap == null)
      return; 
    Object v = threadLocalMap.removeIndexedVariable(this.index);
    if (v != InternalThreadLocalMap.UNSET) {
      removeFromVariablesToRemove(threadLocalMap, this);
      try {
        onRemoval((V)v);
      } catch (Exception e) {
        PlatformDependent.throwException(e);
      } 
    } 
  }
  
  protected V initialValue() throws Exception {
    return null;
  }
  
  protected void onRemoval(V value) throws Exception {}
}
