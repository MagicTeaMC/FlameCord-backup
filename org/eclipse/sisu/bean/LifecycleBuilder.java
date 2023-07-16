package org.eclipse.sisu.bean;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

final class LifecycleBuilder {
  private final List<Method> startMethods = new ArrayList<Method>();
  
  private final List<Method> stopMethods = new ArrayList<Method>();
  
  private final List<Class<?>> hierarchy = new ArrayList<Class<?>>();
  
  public synchronized BeanLifecycle build(Class<?> clazz) {
    try {
      for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass())
        addLifecycleMethods(c); 
      if (this.startMethods.isEmpty() && this.stopMethods.isEmpty())
        return BeanLifecycle.NO_OP; 
      return new BeanLifecycle(this.startMethods, this.stopMethods);
    } finally {
      this.startMethods.clear();
      this.stopMethods.clear();
      this.hierarchy.clear();
    } 
  }
  
  private void addLifecycleMethods(Class<?> clazz) {
    boolean foundStartMethod = false, foundStopMethod = false;
    byte b;
    int i;
    Method[] arrayOfMethod;
    for (i = (arrayOfMethod = clazz.getDeclaredMethods()).length, b = 0; b < i; ) {
      Method m = arrayOfMethod[b];
      if (isCandidateMethod(m)) {
        if (m.isAnnotationPresent((Class)PostConstruct.class)) {
          foundStartMethod = true;
          if (!isOverridden(m))
            this.startMethods.add(m); 
        } else if (m.isAnnotationPresent((Class)PreDestroy.class)) {
          foundStopMethod = true;
          if (!isOverridden(m))
            this.stopMethods.add(m); 
        } 
        if (foundStartMethod && foundStopMethod)
          break; 
      } 
      b++;
    } 
    this.hierarchy.add(clazz);
  }
  
  private boolean isOverridden(Method method) {
    String name = method.getName();
    for (int i = this.hierarchy.size() - 1; i >= 0; i--) {
      byte b;
      int j;
      Method[] arrayOfMethod;
      for (j = (arrayOfMethod = ((Class)this.hierarchy.get(i)).getDeclaredMethods()).length, b = 0; b < j; ) {
        Method m = arrayOfMethod[b];
        if (name.equals(m.getName()) && isCandidateMethod(m)) {
          int modifiers = m.getModifiers();
          if (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers) || (
            !Modifier.isPrivate(modifiers) && samePackage(method, m)))
            return true; 
          break;
        } 
        b++;
      } 
    } 
    return false;
  }
  
  private static boolean isCandidateMethod(Method method) {
    if (method.getReturnType() == void.class) {
      int modifiers = method.getModifiers();
      if (!Modifier.isStatic(modifiers) && !Modifier.isAbstract(modifiers) && !method.isSynthetic())
        return ((method.getParameterTypes()).length == 0); 
    } 
    return false;
  }
  
  private static boolean samePackage(Method lhs, Method rhs) {
    return lhs.getDeclaringClass().getPackage().equals(rhs.getDeclaringClass().getPackage());
  }
}
