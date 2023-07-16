package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import java.lang.annotation.Annotation;
import org.eclipse.sisu.Description;

final class DescriptionSource implements Description, AnnotatedSource {
  private final Object source;
  
  private final String value;
  
  DescriptionSource(Object source, String value) {
    this.source = source;
    this.value = value;
  }
  
  public String value() {
    return this.value;
  }
  
  public Class<? extends Annotation> annotationType() {
    return (Class)Description.class;
  }
  
  public int hashCode() {
    return 127 * "value".hashCode() ^ this.value.hashCode();
  }
  
  public boolean equals(Object rhs) {
    return !(this != rhs && (!(rhs instanceof Description) || !this.value.equals(((Description)rhs).value())));
  }
  
  public String toString() {
    return (this.source != null) ? this.source.toString() : ("@" + Description.class.getName() + "(value=" + this.value + ")");
  }
  
  public <T extends Annotation> T getAnnotation(Binding<?> binding, Class<T> annotationType) {
    if (Description.class.equals(annotationType))
      return (T)this; 
    if (this.source instanceof AnnotatedSource)
      return ((AnnotatedSource)this.source).getAnnotation(binding, annotationType); 
    return null;
  }
}
