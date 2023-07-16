package org.eclipse.sisu.inject;

public interface DeferredClass<T> {
  Class<T> load() throws TypeNotPresentException;
  
  String getName();
  
  DeferredProvider<T> asProvider();
}
