package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import java.lang.annotation.Annotation;
import org.eclipse.sisu.Hidden;

final class HiddenSource implements Hidden, AnnotatedSource {
  private final Object source;
  
  HiddenSource(Object source) {
    this.source = source;
  }
  
  public Class<? extends Annotation> annotationType() {
    return (Class)Hidden.class;
  }
  
  public int hashCode() {
    return 0;
  }
  
  public boolean equals(Object rhs) {
    return rhs instanceof Hidden;
  }
  
  public String toString() {
    return (this.source != null) ? this.source.toString() : ("@" + Hidden.class.getName());
  }
  
  public <T extends Annotation> T getAnnotation(Binding<?> binding, Class<T> annotationType) {
    if (Hidden.class.equals(annotationType))
      return (T)this; 
    if (this.source instanceof AnnotatedSource)
      return ((AnnotatedSource)this.source).getAnnotation(binding, annotationType); 
    return null;
  }
}
