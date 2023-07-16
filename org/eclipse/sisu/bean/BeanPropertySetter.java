package org.eclipse.sisu.bean;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class BeanPropertySetter<T> implements BeanProperty<T>, PrivilegedAction<Void> {
  private final Method method;
  
  BeanPropertySetter(Method method) {
    this.method = method;
  }
  
  public <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> annotationType) {
    return this.method.getAnnotation(annotationType);
  }
  
  public TypeLiteral<T> getType() {
    return TypeLiteral.get(this.method.getGenericParameterTypes()[0]);
  }
  
  public String getName() {
    String name = this.method.getName();
    return String.valueOf(Character.toLowerCase(name.charAt(3))) + name.substring(4);
  }
  
  public <B> void set(B bean, T value) {
    if (!this.method.isAccessible())
      AccessController.doPrivileged((PrivilegedAction<T>)this); 
    BeanScheduler.detectCycle(value);
    try {
      this.method.invoke(bean, new Object[] { value });
    } catch (Exception e) {
      Throwable cause = (e instanceof java.lang.reflect.InvocationTargetException) ? e.getCause() : e;
      throw new ProvisionException("Error injecting: " + this.method, cause);
    } catch (LinkageError e) {
      throw new ProvisionException("Error injecting: " + this.method, e);
    } 
  }
  
  public int hashCode() {
    return this.method.hashCode();
  }
  
  public boolean equals(Object rhs) {
    if (this == rhs)
      return true; 
    if (rhs instanceof BeanPropertySetter)
      return this.method.equals(((BeanPropertySetter)rhs).method); 
    return false;
  }
  
  public String toString() {
    return this.method.toString();
  }
  
  public Void run() {
    this.method.setAccessible(true);
    return null;
  }
}
