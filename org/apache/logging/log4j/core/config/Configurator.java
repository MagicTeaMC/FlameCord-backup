package org.apache.logging.log4j.core.config;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.impl.Log4jContextFactory;
import org.apache.logging.log4j.core.util.NetUtils;
import org.apache.logging.log4j.spi.LoggerContextFactory;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Strings;

public final class Configurator {
  private static final String FQCN = Configurator.class.getName();
  
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private static Log4jContextFactory getFactory() {
    LoggerContextFactory factory = LogManager.getFactory();
    if (factory instanceof Log4jContextFactory)
      return (Log4jContextFactory)factory; 
    if (factory != null) {
      LOGGER.error("LogManager returned an instance of {} which does not implement {}. Unable to initialize Log4j.", factory
          .getClass().getName(), Log4jContextFactory.class.getName());
    } else {
      LOGGER.fatal("LogManager did not return a LoggerContextFactory. This indicates something has gone terribly wrong!");
    } 
    return null;
  }
  
  public static LoggerContext initialize(ClassLoader loader, ConfigurationSource source) {
    return initialize(loader, source, (Object)null);
  }
  
  public static LoggerContext initialize(ClassLoader loader, ConfigurationSource source, Object externalContext) {
    try {
      Log4jContextFactory factory = getFactory();
      return (factory == null) ? null : factory
        .getContext(FQCN, loader, externalContext, false, source);
    } catch (Exception ex) {
      LOGGER.error("There was a problem obtaining a LoggerContext using the configuration source [{}]", source, ex);
      return null;
    } 
  }
  
  public static LoggerContext initialize(String name, ClassLoader loader, String configLocation) {
    return initialize(name, loader, configLocation, (Object)null);
  }
  
  public static LoggerContext initialize(String name, ClassLoader loader, String configLocation, Object externalContext) {
    if (Strings.isBlank(configLocation))
      return initialize(name, loader, (URI)null, externalContext); 
    return configLocation.contains(",") ? initialize(name, loader, NetUtils.toURIs(configLocation), externalContext) : 
      initialize(name, loader, NetUtils.toURI(configLocation), externalContext);
  }
  
  public static LoggerContext initialize(String name, ClassLoader loader, URI configLocation) {
    return initialize(name, loader, configLocation, (Map.Entry<String, Object>)null);
  }
  
  public static LoggerContext initialize(String name, ClassLoader loader, URI configLocation, Object externalContext) {
    try {
      Log4jContextFactory factory = getFactory();
      return (factory == null) ? null : factory
        .getContext(FQCN, loader, externalContext, false, configLocation, name);
    } catch (Exception ex) {
      LOGGER.error("There was a problem initializing the LoggerContext [{}] using configuration at [{}].", name, configLocation, ex);
      return null;
    } 
  }
  
  public static LoggerContext initialize(String name, ClassLoader loader, URI configLocation, Map.Entry<String, Object> entry) {
    try {
      Log4jContextFactory factory = getFactory();
      return (factory == null) ? null : factory
        .getContext(FQCN, loader, entry, false, configLocation, name);
    } catch (Exception ex) {
      LOGGER.error("There was a problem initializing the LoggerContext [{}] using configuration at [{}].", name, configLocation, ex);
      return null;
    } 
  }
  
  public static LoggerContext initialize(String name, ClassLoader loader, List<URI> configLocations, Object externalContext) {
    try {
      Log4jContextFactory factory = getFactory();
      return (factory == null) ? null : factory
        
        .getContext(FQCN, loader, externalContext, false, configLocations, name);
    } catch (Exception ex) {
      LOGGER.error("There was a problem initializing the LoggerContext [{}] using configurations at [{}].", name, configLocations, ex);
      return null;
    } 
  }
  
  public static LoggerContext initialize(String name, String configLocation) {
    return initialize(name, (ClassLoader)null, configLocation);
  }
  
  public static LoggerContext initialize(Configuration configuration) {
    return initialize((ClassLoader)null, configuration, (Object)null);
  }
  
  public static LoggerContext initialize(ClassLoader loader, Configuration configuration) {
    return initialize(loader, configuration, (Object)null);
  }
  
  public static LoggerContext initialize(ClassLoader loader, Configuration configuration, Object externalContext) {
    try {
      Log4jContextFactory factory = getFactory();
      return (factory == null) ? null : factory
        .getContext(FQCN, loader, externalContext, false, configuration);
    } catch (Exception ex) {
      LOGGER.error("There was a problem initializing the LoggerContext using configuration {}", configuration
          .getName(), ex);
      return null;
    } 
  }
  
  public static void reconfigure(Configuration configuration) {
    try {
      Log4jContextFactory factory = getFactory();
      if (factory != null)
        factory.getContext(FQCN, null, null, false)
          .reconfigure(configuration); 
    } catch (Exception ex) {
      LOGGER.error("There was a problem initializing the LoggerContext using configuration {}", configuration
          .getName(), ex);
    } 
  }
  
  public static void reconfigure() {
    try {
      Log4jContextFactory factory = getFactory();
      if (factory != null) {
        factory.getSelector().getContext(FQCN, null, false).reconfigure();
      } else {
        LOGGER.warn("Unable to reconfigure - Log4j has not been initialized.");
      } 
    } catch (Exception ex) {
      LOGGER.error("Error encountered trying to reconfigure logging", ex);
    } 
  }
  
  public static void reconfigure(URI uri) {
    try {
      Log4jContextFactory factory = getFactory();
      if (factory != null) {
        factory.getSelector().getContext(FQCN, null, false).setConfigLocation(uri);
      } else {
        LOGGER.warn("Unable to reconfigure - Log4j has not been initialized.");
      } 
    } catch (Exception ex) {
      LOGGER.error("Error encountered trying to reconfigure logging", ex);
    } 
  }
  
  public static void setAllLevels(String parentLogger, Level level) {
    LoggerContext loggerContext = LoggerContext.getContext(StackLocatorUtil.getCallerClassLoader(2), false, null);
    Configuration config = loggerContext.getConfiguration();
    boolean set = setLevel(parentLogger, level, config);
    for (Map.Entry<String, LoggerConfig> entry : config.getLoggers().entrySet()) {
      if (((String)entry.getKey()).startsWith(parentLogger))
        set |= setLevel(entry.getValue(), level); 
    } 
    if (set)
      loggerContext.updateLoggers(); 
  }
  
  public static Logger setLevel(Logger logger, Level level) {
    setLevel(LoggerContext.getContext(StackLocatorUtil.getCallerClassLoader(2), false, null), logger.getName(), level);
    return logger;
  }
  
  public static void setLevel(Class<?> clazz, Level level) {
    String canonicalName = clazz.getCanonicalName();
    setLevel(LoggerContext.getContext(StackLocatorUtil.getCallerClassLoader(2), false, null), (canonicalName != null) ? canonicalName : clazz
        .getName(), level);
  }
  
  private static boolean setLevel(LoggerConfig loggerConfig, Level level) {
    boolean set = !loggerConfig.getLevel().equals(level);
    if (set)
      loggerConfig.setLevel(level); 
    return set;
  }
  
  private static void setLevel(LoggerContext loggerContext, String loggerName, Level level) {
    if (Strings.isEmpty(loggerName)) {
      setRootLevel(level, loggerContext);
    } else if (setLevel(loggerName, level, loggerContext.getConfiguration())) {
      loggerContext.updateLoggers();
    } 
  }
  
  public static void setLevel(Map<String, Level> levelMap) {
    LoggerContext loggerContext = LoggerContext.getContext(StackLocatorUtil.getCallerClassLoader(2), false, null);
    Configuration config = loggerContext.getConfiguration();
    boolean set = false;
    for (Map.Entry<String, Level> entry : levelMap.entrySet()) {
      String loggerName = entry.getKey();
      Level level = entry.getValue();
      set |= setLevel(loggerName, level, config);
    } 
    if (set)
      loggerContext.updateLoggers(); 
  }
  
  public static void setLevel(String loggerName, Level level) {
    setLevel(LoggerContext.getContext(StackLocatorUtil.getCallerClassLoader(2), false, null), loggerName, level);
  }
  
  public static void setLevel(String loggerName, String level) {
    setLevel(LoggerContext.getContext(StackLocatorUtil.getCallerClassLoader(2), false, null), loggerName, Level.toLevel(level));
  }
  
  private static boolean setLevel(String loggerName, Level level, Configuration config) {
    boolean set;
    LoggerConfig loggerConfig = config.getLoggerConfig(loggerName);
    if (!loggerName.equals(loggerConfig.getName())) {
      loggerConfig = new LoggerConfig(loggerName, level, true);
      config.addLogger(loggerName, loggerConfig);
      loggerConfig.setLevel(level);
      set = true;
    } else {
      set = setLevel(loggerConfig, level);
    } 
    return set;
  }
  
  public static void setRootLevel(Level level) {
    setRootLevel(level, LoggerContext.getContext(StackLocatorUtil.getCallerClassLoader(2), false, null));
  }
  
  private static void setRootLevel(Level level, LoggerContext loggerContext) {
    LoggerConfig loggerConfig = loggerContext.getConfiguration().getRootLogger();
    if (!loggerConfig.getLevel().equals(level)) {
      loggerConfig.setLevel(level);
      loggerContext.updateLoggers();
    } 
  }
  
  public static void shutdown(LoggerContext ctx) {
    if (ctx != null)
      ctx.stop(); 
  }
  
  public static boolean shutdown(LoggerContext ctx, long timeout, TimeUnit timeUnit) {
    if (ctx != null)
      return ctx.stop(timeout, timeUnit); 
    return true;
  }
}
