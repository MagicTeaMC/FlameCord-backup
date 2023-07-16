package org.eclipse.sisu.bean;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.List;
import org.eclipse.sisu.inject.Logs;

final class BeanLifecycle implements PrivilegedAction<Void> {
  private static final Method[] NO_METHODS = new Method[0];
  
  static final BeanLifecycle NO_OP = new BeanLifecycle(null, null);
  
  private final Method[] startMethods;
  
  private final Method[] stopMethods;
  
  BeanLifecycle(List<Method> startMethods, List<Method> stopMethods) {
    this.startMethods = toArray(startMethods);
    this.stopMethods = toArray(stopMethods);
    AccessController.doPrivileged(this);
  }
  
  public boolean isStartable() {
    return (this.startMethods.length > 0);
  }
  
  public boolean isStoppable() {
    return (this.stopMethods.length > 0);
  }
  
  public void start(Object bean) {
    Logs.trace("PostConstruct: <>", bean, null);
    int i = this.startMethods.length - 1;
    try {
      for (; i >= 0; i--)
        this.startMethods[i].invoke(bean, new Object[0]); 
    } catch (Throwable e) {
      Throwable cause = (e instanceof java.lang.reflect.InvocationTargetException) ? e.getCause() : e;
      Logs.catchThrowable(cause);
      try {
        Logs.warn("Error starting: {}", this.startMethods[i], cause);
      } finally {
        Logs.throwUnchecked(cause);
      } 
    } 
  }
  
  public void stop(Object bean) {
    Logs.trace("PreDestroy: <>", bean, null);
    for (int i = 0; i < this.stopMethods.length; i++) {
      try {
        this.stopMethods[i].invoke(bean, new Object[0]);
      } catch (Throwable e) {
        Throwable cause = (e instanceof java.lang.reflect.InvocationTargetException) ? e.getCause() : e;
        Logs.catchThrowable(cause);
        try {
          Logs.warn("Problem stopping: {}", this.stopMethods[i], cause);
        } finally {}
      } 
    } 
  }
  
  public Void run() {
    AccessibleObject.setAccessible((AccessibleObject[])this.startMethods, true);
    AccessibleObject.setAccessible((AccessibleObject[])this.stopMethods, true);
    return null;
  }
  
  private static Method[] toArray(List<Method> methods) {
    return (methods != null && !methods.isEmpty()) ? methods.<Method>toArray(new Method[methods.size()]) : NO_METHODS;
  }
}
