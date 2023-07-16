package org.apache.logging.log4j.jul;

import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.spi.AbstractLoggerAdapter;
import org.apache.logging.log4j.spi.LoggerContext;
import org.apache.logging.log4j.util.StackLocatorUtil;

public abstract class AbstractLoggerAdapter extends AbstractLoggerAdapter<Logger> {
  protected LoggerContext getContext() {
    return getContext(LogManager.getFactory().isClassLoaderDependent() ? 
        StackLocatorUtil.getCallerClass(LogManager.class) : null);
  }
}
