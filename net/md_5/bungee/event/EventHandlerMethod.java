package net.md_5.bungee.event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class EventHandlerMethod {
  private final Object listener;
  
  private final Method method;
  
  public EventHandlerMethod(Object listener, Method method) {
    this.listener = listener;
    this.method = method;
  }
  
  public Object getListener() {
    return this.listener;
  }
  
  public Method getMethod() {
    return this.method;
  }
  
  public void invoke(Object event) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    this.method.invoke(this.listener, new Object[] { event });
  }
}
