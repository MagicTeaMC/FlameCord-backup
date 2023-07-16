package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.apache.logging.log4j.util.Strings;

class DefaultAsyncWaitStrategyFactory implements AsyncWaitStrategyFactory {
  static final String DEFAULT_WAIT_STRATEGY_CLASSNAME = TimeoutBlockingWaitStrategy.class.getName();
  
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private final String propertyName;
  
  public DefaultAsyncWaitStrategyFactory(String propertyName) {
    this.propertyName = propertyName;
  }
  
  public WaitStrategy createWaitStrategy() {
    long sleepTimeNs;
    String key;
    int retries;
    String strategy = PropertiesUtil.getProperties().getStringProperty(this.propertyName, "TIMEOUT");
    LOGGER.trace("DefaultAsyncWaitStrategyFactory property {}={}", this.propertyName, strategy);
    String strategyUp = Strings.toRootUpperCase(strategy);
    switch (strategyUp) {
      case "SLEEP":
        sleepTimeNs = parseAdditionalLongProperty(this.propertyName, "SleepTimeNs", 100L);
        key = getFullPropertyKey(this.propertyName, "Retries");
        retries = PropertiesUtil.getProperties().getIntegerProperty(key, 200);
        LOGGER.trace("DefaultAsyncWaitStrategyFactory creating SleepingWaitStrategy(retries={}, sleepTimeNs={})", Integer.valueOf(retries), Long.valueOf(sleepTimeNs));
        return (WaitStrategy)new SleepingWaitStrategy(retries, sleepTimeNs);
      case "YIELD":
        LOGGER.trace("DefaultAsyncWaitStrategyFactory creating YieldingWaitStrategy");
        return (WaitStrategy)new YieldingWaitStrategy();
      case "BLOCK":
        LOGGER.trace("DefaultAsyncWaitStrategyFactory creating BlockingWaitStrategy");
        return (WaitStrategy)new BlockingWaitStrategy();
      case "BUSYSPIN":
        LOGGER.trace("DefaultAsyncWaitStrategyFactory creating BusySpinWaitStrategy");
        return (WaitStrategy)new BusySpinWaitStrategy();
      case "TIMEOUT":
        return createDefaultWaitStrategy(this.propertyName);
    } 
    return createDefaultWaitStrategy(this.propertyName);
  }
  
  static WaitStrategy createDefaultWaitStrategy(String propertyName) {
    long timeoutMillis = parseAdditionalLongProperty(propertyName, "Timeout", 10L);
    LOGGER.trace("DefaultAsyncWaitStrategyFactory creating TimeoutBlockingWaitStrategy(timeout={}, unit=MILLIS)", Long.valueOf(timeoutMillis));
    return new TimeoutBlockingWaitStrategy(timeoutMillis, TimeUnit.MILLISECONDS);
  }
  
  private static String getFullPropertyKey(String strategyKey, String additionalKey) {
    if (strategyKey.startsWith("AsyncLogger."))
      return "AsyncLogger." + additionalKey; 
    if (strategyKey.startsWith("AsyncLoggerConfig."))
      return "AsyncLoggerConfig." + additionalKey; 
    return strategyKey + additionalKey;
  }
  
  private static long parseAdditionalLongProperty(String propertyName, String additionalKey, long defaultValue) {
    String key = getFullPropertyKey(propertyName, additionalKey);
    return PropertiesUtil.getProperties().getLongProperty(key, defaultValue);
  }
}
