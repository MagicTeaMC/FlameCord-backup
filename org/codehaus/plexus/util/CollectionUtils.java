package org.codehaus.plexus.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class CollectionUtils {
  public static <K, V> Map<K, V> mergeMaps(Map<K, V> dominantMap, Map<K, V> recessiveMap) {
    if (dominantMap == null && recessiveMap == null)
      return null; 
    if (dominantMap != null && recessiveMap == null)
      return dominantMap; 
    if (dominantMap == null)
      return recessiveMap; 
    Map<K, V> result = new HashMap<K, V>();
    Set<K> dominantMapKeys = dominantMap.keySet();
    Set<K> recessiveMapKeys = recessiveMap.keySet();
    Collection<K> contributingRecessiveKeys = subtract(recessiveMapKeys, intersection(dominantMapKeys, recessiveMapKeys));
    result.putAll(dominantMap);
    for (K key : contributingRecessiveKeys)
      result.put(key, recessiveMap.get(key)); 
    return result;
  }
  
  public static <K, V> Map<K, V> mergeMaps(Map<K, V>[] maps) {
    Map<K, V> result;
    if (maps.length == 0) {
      result = null;
    } else if (maps.length == 1) {
      result = maps[0];
    } else {
      result = mergeMaps(maps[0], maps[1]);
      for (int i = 2; i < maps.length; i++)
        result = mergeMaps(result, maps[i]); 
    } 
    return result;
  }
  
  public static <E> Collection<E> intersection(Collection<E> a, Collection<E> b) {
    ArrayList<E> list = new ArrayList<E>();
    Map<E, Integer> mapa = getCardinalityMap(a);
    Map<E, Integer> mapb = getCardinalityMap(b);
    Set<E> elts = new HashSet<E>(a);
    elts.addAll(b);
    for (E obj : elts) {
      for (int i = 0, m = Math.min(getFreq(obj, mapa), getFreq(obj, mapb)); i < m; i++)
        list.add(obj); 
    } 
    return list;
  }
  
  public static <T> Collection<T> subtract(Collection<T> a, Collection<T> b) {
    ArrayList<T> list = new ArrayList<T>(a);
    for (T aB : b)
      list.remove(aB); 
    return list;
  }
  
  public static <E> Map<E, Integer> getCardinalityMap(Collection<E> col) {
    HashMap<E, Integer> count = new HashMap<E, Integer>();
    for (E obj : col) {
      Integer c = count.get(obj);
      if (null == c) {
        count.put(obj, Integer.valueOf(1));
        continue;
      } 
      count.put(obj, Integer.valueOf(c.intValue() + 1));
    } 
    return count;
  }
  
  public static <E> List<E> iteratorToList(Iterator<E> it) {
    if (it == null)
      throw new NullPointerException("it cannot be null."); 
    List<E> list = new ArrayList<E>();
    while (it.hasNext())
      list.add(it.next()); 
    return list;
  }
  
  private static <E> int getFreq(E obj, Map<E, Integer> freqMap) {
    try {
      Integer o = freqMap.get(obj);
      if (o != null)
        return o.intValue(); 
    } catch (NullPointerException nullPointerException) {
    
    } catch (NoSuchElementException noSuchElementException) {}
    return 0;
  }
}
