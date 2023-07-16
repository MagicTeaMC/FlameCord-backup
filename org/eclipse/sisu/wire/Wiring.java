package org.eclipse.sisu.wire;

import com.google.inject.Key;

public interface Wiring {
  boolean wire(Key<?> paramKey);
}
