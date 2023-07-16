package org.eclipse.aether;

public interface SessionData {
  void set(Object paramObject1, Object paramObject2);
  
  boolean set(Object paramObject1, Object paramObject2, Object paramObject3);
  
  Object get(Object paramObject);
}
