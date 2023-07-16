package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import java.lang.annotation.Annotation;
import org.eclipse.sisu.Priority;

final class PrioritySource implements Priority, AnnotatedSource {
  private final Object source;
  
  private final int value;
  
  PrioritySource(Object source, int value) {
    this.source = source;
    this.value = value;
  }
  
  public int value() {
    return this.value;
  }
  
  public Class<? extends Annotation> annotationType() {
    return (Class)Priority.class;
  }
  
  public int hashCode() {
    return 127 * "value".hashCode() ^ Integer.valueOf(this.value).hashCode();
  }
  
  public boolean equals(Object rhs) {
    return !(this != rhs && (!(rhs instanceof Priority) || this.value != ((Priority)rhs).value()));
  }
  
  public String toString() {
    return (this.source != null) ? this.source.toString() : ("@" + Priority.class.getName() + "(value=" + this.value + ")");
  }
  
  public <T extends Annotation> T getAnnotation(Binding<?> binding, Class<T> annotationType) {
    if (Priority.class.equals(annotationType))
      return (T)this; 
    if (this.source instanceof AnnotatedSource)
      return ((AnnotatedSource)this.source).getAnnotation(binding, annotationType); 
    return null;
  }
}
