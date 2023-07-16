package org.eclipse.sisu.inject;

import java.lang.ref.Reference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class Weak {
  public static <T> Collection<T> elements() {
    return elements(10);
  }
  
  public static <T> Collection<T> elements(int capacity) {
    return new MildElements<T>(new ArrayList<Reference<T>>(capacity), false);
  }
  
  public static <K, V> Map<K, V> keys() {
    return keys(16);
  }
  
  public static <K, V> Map<K, V> keys(int capacity) {
    return new MildKeys<K, V>(new HashMap<Reference<K>, V>(capacity), false);
  }
  
  public static <K, V> ConcurrentMap<K, V> concurrentKeys() {
    return concurrentKeys(16, 4);
  }
  
  public static <K, V> ConcurrentMap<K, V> concurrentKeys(int capacity, int concurrency) {
    return new MildConcurrentKeys<K, V>(new ConcurrentHashMap<Reference<K>, V>(capacity, 0.75F, concurrency), false);
  }
  
  public static <K, V> Map<K, V> values() {
    return values(16);
  }
  
  public static <K, V> Map<K, V> values(int capacity) {
    return new MildValues<K, V>(new HashMap<K, Reference<V>>(capacity), false);
  }
  
  public static <K, V> ConcurrentMap<K, V> concurrentValues() {
    return concurrentValues(16, 4);
  }
  
  public static <K, V> ConcurrentMap<K, V> concurrentValues(int capacity, int concurrency) {
    return new MildConcurrentValues<K, V>(new ConcurrentHashMap<K, Reference<V>>(capacity, 0.75F, concurrency), false);
  }
}
