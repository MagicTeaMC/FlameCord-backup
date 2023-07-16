package com.mysql.cj.xdevapi;

import java.util.Iterator;
import java.util.List;

public interface FetchResult<T> extends Iterator<T>, Iterable<T> {
  default boolean hasData() {
    return true;
  }
  
  default T fetchOne() {
    if (hasNext())
      return next(); 
    return null;
  }
  
  default Iterator<T> iterator() {
    return fetchAll().iterator();
  }
  
  long count();
  
  List<T> fetchAll();
}
