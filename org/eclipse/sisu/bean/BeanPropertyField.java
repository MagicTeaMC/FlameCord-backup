package org.eclipse.sisu.bean;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class BeanPropertyField<T> implements BeanProperty<T>, PrivilegedAction<Void> {
  private final Field field;
  
  BeanPropertyField(Field field) {
    this.field = field;
  }
  
  public <A extends java.lang.annotation.Annotation> A getAnnotation(Class<A> annotationType) {
    return this.field.getAnnotation(annotationType);
  }
  
  public TypeLiteral<T> getType() {
    return TypeLiteral.get(this.field.getGenericType());
  }
  
  public String getName() {
    return this.field.getName();
  }
  
  public <B> void set(B bean, T value) {
    if (!this.field.isAccessible())
      AccessController.doPrivileged((PrivilegedAction<T>)this); 
    BeanScheduler.detectCycle(value);
    try {
      this.field.set(bean, value);
    } catch (Exception e) {
      throw new ProvisionException("Error injecting: " + this.field, e);
    } catch (LinkageError e) {
      throw new ProvisionException("Error injecting: " + this.field, e);
    } 
  }
  
  public int hashCode() {
    return this.field.hashCode();
  }
  
  public boolean equals(Object rhs) {
    if (this == rhs)
      return true; 
    if (rhs instanceof BeanPropertyField)
      return this.field.equals(((BeanPropertyField)rhs).field); 
    return false;
  }
  
  public String toString() {
    return this.field.toString();
  }
  
  public Void run() {
    this.field.setAccessible(true);
    return null;
  }
}
