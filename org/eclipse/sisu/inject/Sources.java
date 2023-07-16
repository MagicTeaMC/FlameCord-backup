package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import org.eclipse.sisu.Description;
import org.eclipse.sisu.Hidden;
import org.eclipse.sisu.Priority;

public final class Sources {
  public static Hidden hide() {
    return hide(null);
  }
  
  public static Hidden hide(Object source) {
    return new HiddenSource(source);
  }
  
  public static Description describe(String value) {
    return describe(null, value);
  }
  
  public static Description describe(Object source, String value) {
    return new DescriptionSource(source, value);
  }
  
  public static Priority prioritize(int value) {
    return prioritize(null, value);
  }
  
  public static Priority prioritize(Object source, int value) {
    return new PrioritySource(source, value);
  }
  
  public static <T extends java.lang.annotation.Annotation> T getAnnotation(Binding<?> binding, Class<T> annotationType) {
    T annotation = null;
    Object source = Guice4.getDeclaringSource(binding);
    if (source instanceof AnnotatedSource)
      annotation = ((AnnotatedSource)source).getAnnotation(binding, annotationType); 
    if (annotation == null)
      annotation = Implementations.getAnnotation(binding, annotationType); 
    return annotation;
  }
}
