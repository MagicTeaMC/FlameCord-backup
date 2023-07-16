package org.apache.logging.log4j.core.async;

import java.net.URI;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.selector.ClassLoaderContextSelector;
import org.apache.logging.log4j.util.PropertiesUtil;

public class AsyncLoggerContextSelector extends ClassLoaderContextSelector {
  public static boolean isSelected() {
    return AsyncLoggerContextSelector.class.getName().equals(
        PropertiesUtil.getProperties().getStringProperty("Log4jContextSelector"));
  }
  
  protected LoggerContext createContext(String name, URI configLocation) {
    return new AsyncLoggerContext(name, null, configLocation);
  }
  
  protected String toContextMapKey(ClassLoader loader) {
    return "AsyncContext@" + Integer.toHexString(System.identityHashCode(loader));
  }
  
  protected String defaultContextName() {
    return "DefaultAsyncContext@" + Thread.currentThread().getName();
  }
}
