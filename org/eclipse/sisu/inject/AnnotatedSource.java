package org.eclipse.sisu.inject;

import com.google.inject.Binding;

public interface AnnotatedSource {
  <T extends java.lang.annotation.Annotation> T getAnnotation(Binding<?> paramBinding, Class<T> paramClass);
}
