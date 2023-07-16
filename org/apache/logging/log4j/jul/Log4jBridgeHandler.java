package org.apache.logging.log4j.jul;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.status.StatusLogger;

public class Log4jBridgeHandler extends Handler implements PropertyChangeListener {
  private static final Logger SLOGGER = (Logger)StatusLogger.getLogger();
  
  private static final String FQCN = Logger.class.getName();
  
  private static final String UNKNOWN_LOGGER_NAME = "unknown.jul.logger";
  
  private static final Formatter julFormatter = new SimpleFormatter();
  
  private boolean doDebugOutput = false;
  
  private String julSuffixToAppend = null;
  
  private volatile boolean installAsLevelPropagator = false;
  
  private Set<Logger> julLoggerRefs;
  
  public static void install(boolean removeHandlersForRootLogger, String suffixToAppend, boolean propagateLevels) {
    Logger rootLogger = getJulRootLogger();
    if (removeHandlersForRootLogger)
      for (Handler hdl : rootLogger.getHandlers())
        rootLogger.removeHandler(hdl);  
    rootLogger.addHandler(new Log4jBridgeHandler(false, suffixToAppend, propagateLevels));
  }
  
  private static Logger getJulRootLogger() {
    return LogManager.getLogManager().getLogger("");
  }
  
  public Log4jBridgeHandler() {
    LogManager julLogMgr = LogManager.getLogManager();
    String className = getClass().getName();
    init(Boolean.parseBoolean(julLogMgr.getProperty(className + ".sysoutDebug")), julLogMgr
        .getProperty(className + ".appendSuffix"), 
        Boolean.parseBoolean(julLogMgr.getProperty(className + ".propagateLevels")));
  }
  
  public Log4jBridgeHandler(boolean debugOutput, String suffixToAppend, boolean propagateLevels) {
    init(debugOutput, suffixToAppend, propagateLevels);
  }
  
  protected void init(boolean debugOutput, String suffixToAppend, boolean propagateLevels) {
    this.doDebugOutput = debugOutput;
    if (debugOutput)
      (new Exception("DIAGNOSTIC ONLY (sysout):  Log4jBridgeHandler instance created (" + this + ")"))
        .printStackTrace(System.out); 
    if (suffixToAppend != null) {
      suffixToAppend = suffixToAppend.trim();
      if (suffixToAppend.isEmpty()) {
        suffixToAppend = null;
      } else if (suffixToAppend.charAt(0) != '.') {
        suffixToAppend = '.' + suffixToAppend;
      } 
    } 
    this.julSuffixToAppend = suffixToAppend;
    this.installAsLevelPropagator = propagateLevels;
    SLOGGER.debug("Log4jBridgeHandler init. with: suffix='{}', lvlProp={}, instance={}", suffixToAppend, 
        Boolean.valueOf(propagateLevels), this);
  }
  
  public void close() {
    this.julLoggerRefs = null;
    LoggerContext.getContext(false).removePropertyChangeListener(this);
    if (this.doDebugOutput)
      System.out.println("sysout:  Log4jBridgeHandler close(): " + this); 
  }
  
  public void publish(LogRecord record) {
    if (record == null)
      return; 
    if (this.installAsLevelPropagator)
      synchronized (this) {
        if (this.installAsLevelPropagator) {
          LoggerContext context = LoggerContext.getContext(false);
          context.addPropertyChangeListener(this);
          propagateLogLevels(context.getConfiguration());
          this.installAsLevelPropagator = false;
        } 
      }  
    Logger log4jLogger = getLog4jLogger(record);
    String msg = julFormatter.formatMessage(record);
    Level log4jLevel = LevelTranslator.toLevel(record.getLevel());
    Throwable thrown = record.getThrown();
    if (log4jLogger instanceof ExtendedLogger) {
      try {
        ((ExtendedLogger)log4jLogger).logIfEnabled(FQCN, log4jLevel, null, msg, thrown);
      } catch (NoClassDefFoundError e) {
        log4jLogger.warn("Log4jBridgeHandler: ignored exception when calling 'ExtendedLogger': {}", e.toString());
        log4jLogger.log(log4jLevel, msg, thrown);
      } 
    } else {
      log4jLogger.log(log4jLevel, msg, thrown);
    } 
  }
  
  public void flush() {}
  
  private Logger getLog4jLogger(LogRecord record) {
    String name = record.getLoggerName();
    if (name == null) {
      name = "unknown.jul.logger";
    } else if (this.julSuffixToAppend != null) {
      name = name + this.julSuffixToAppend;
    } 
    return LogManager.getLogger(name);
  }
  
  public void propertyChange(PropertyChangeEvent evt) {
    SLOGGER.debug("Log4jBridgeHandler.propertyChange(): {}", evt);
    if ("config".equals(evt.getPropertyName()) && evt.getNewValue() instanceof Configuration)
      propagateLogLevels((Configuration)evt.getNewValue()); 
  }
  
  private void propagateLogLevels(Configuration config) {
    SLOGGER.debug("Log4jBridgeHandler.propagateLogLevels(): {}", config);
    if (this.julLoggerRefs == null) {
      this.julLoggerRefs = new HashSet<>();
    } else {
      this.julLoggerRefs.clear();
    } 
    Map<String, LoggerConfig> log4jLoggers = config.getLoggers();
    for (LoggerConfig lcfg : log4jLoggers.values()) {
      Logger julLog = Logger.getLogger(lcfg.getName());
      Level julLevel = LevelTranslator.toJavaLevel(lcfg.getLevel());
      julLog.setLevel(julLevel);
      this.julLoggerRefs.add(julLog);
    } 
    LogManager julMgr = LogManager.getLogManager();
    for (Enumeration<String> en = julMgr.getLoggerNames(); en.hasMoreElements(); ) {
      Logger julLog = julMgr.getLogger(en.nextElement());
      if (julLog != null && julLog.getLevel() != null && !"".equals(julLog.getName()) && 
        !log4jLoggers.containsKey(julLog.getName()))
        julLog.setLevel(null); 
    } 
  }
}
