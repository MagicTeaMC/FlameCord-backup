package org.eclipse.sisu.inject;

import com.google.inject.Provider;

public interface DeferredProvider<T> extends Provider<T> {
  DeferredClass<T> getImplementationClass();
}
