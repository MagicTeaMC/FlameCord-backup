package org.eclipse.aether.spi.locator;

import java.util.List;

public interface ServiceLocator {
  <T> T getService(Class<T> paramClass);
  
  <T> List<T> getServices(Class<T> paramClass);
}
