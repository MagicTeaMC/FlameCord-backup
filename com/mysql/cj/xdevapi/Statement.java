package com.mysql.cj.xdevapi;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

public interface Statement<STMT_T, RES_T> {
  public enum LockContention {
    DEFAULT, NOWAIT, SKIP_LOCKED;
  }
  
  default STMT_T clearBindings() {
    throw new UnsupportedOperationException("This statement doesn't support bound parameters");
  }
  
  default STMT_T bind(String argName, Object value) {
    throw new UnsupportedOperationException("This statement doesn't support bound parameters");
  }
  
  default STMT_T bind(Map<String, Object> values) {
    clearBindings();
    values.entrySet().forEach(e -> bind((String)e.getKey(), e.getValue()));
    return (STMT_T)this;
  }
  
  default STMT_T bind(List<Object> values) {
    clearBindings();
    IntStream.range(0, values.size()).forEach(i -> bind(String.valueOf(i), values.get(i)));
    return (STMT_T)this;
  }
  
  STMT_T bind(Object... values) {
    return bind(Arrays.asList(values));
  }
  
  RES_T execute();
  
  CompletableFuture<RES_T> executeAsync();
}
