package org.apache.logging.log4j.jul;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Set;
import java.util.function.Supplier;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public class LogManager extends LogManager {
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private final AbstractLoggerAdapter loggerAdapter;
  
  private final ThreadLocal<Set<String>> recursive = ThreadLocal.withInitial(java.util.HashSet::new);
  
  public LogManager() {
    AbstractLoggerAdapter adapter = null;
    String overrideAdaptorClassName = PropertiesUtil.getProperties().getStringProperty("log4j.jul.LoggerAdapter");
    if (overrideAdaptorClassName != null)
      try {
        LOGGER.info("Trying to use LoggerAdaptor [{}] specified by Log4j property.", overrideAdaptorClassName);
        adapter = (AbstractLoggerAdapter)LoaderUtil.newCheckedInstanceOf(overrideAdaptorClassName, AbstractLoggerAdapter.class);
      } catch (Exception e) {
        LOGGER.error("Specified LoggerAdapter [{}] is incompatible.", overrideAdaptorClassName, e);
      }  
    if (adapter == null) {
      String adapterClassName;
      try {
        LoaderUtil.loadClass("org.apache.logging.log4j.core.Logger");
        adapterClassName = "org.apache.logging.log4j.jul.CoreLoggerAdapter";
      } catch (ClassNotFoundException ignored) {
        adapterClassName = "org.apache.logging.log4j.jul.ApiLoggerAdapter";
      } 
      LOGGER.debug("Attempting to use {}", adapterClassName);
      try {
        adapter = (AbstractLoggerAdapter)LoaderUtil.newCheckedInstanceOf(adapterClassName, AbstractLoggerAdapter.class);
      } catch (Exception e) {
        throw (LoggingException)LOGGER.throwing(new LoggingException(e));
      } 
    } 
    this.loggerAdapter = adapter;
    LOGGER.info("Registered Log4j as the java.util.logging.LogManager.");
  }
  
  public boolean addLogger(Logger logger) {
    return false;
  }
  
  public Logger getLogger(String name) {
    LOGGER.trace("Call to LogManager.getLogger({})", name);
    Set<String> activeRequests = this.recursive.get();
    if (activeRequests.add(name))
      try {
        return (Logger)this.loggerAdapter.getLogger(name);
      } finally {
        activeRequests.remove(name);
      }  
    LOGGER.warn("Recursive call to getLogger for {} ignored.", name);
    return new NoOpLogger(name);
  }
  
  public Enumeration<String> getLoggerNames() {
    return Collections.enumeration(this.loggerAdapter.getLoggersInContext(this.loggerAdapter.getContext()).keySet());
  }
}
