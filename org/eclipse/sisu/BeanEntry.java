package org.eclipse.sisu;

import java.util.Map;
import javax.inject.Provider;

public interface BeanEntry<Q extends java.lang.annotation.Annotation, T> extends Map.Entry<Q, T> {
  Q getKey();
  
  T getValue();
  
  Provider<T> getProvider();
  
  String getDescription();
  
  Class<T> getImplementationClass();
  
  Object getSource();
  
  int getRank();
}
