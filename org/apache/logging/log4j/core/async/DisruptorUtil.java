package org.apache.logging.log4j.core.async;

import com.lmax.disruptor.ExceptionHandler;
import com.lmax.disruptor.WaitStrategy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.util.Constants;
import org.apache.logging.log4j.core.util.Integers;
import org.apache.logging.log4j.core.util.Loader;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.PropertiesUtil;

final class DisruptorUtil {
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private static final int RINGBUFFER_MIN_SIZE = 128;
  
  private static final int RINGBUFFER_DEFAULT_SIZE = 262144;
  
  private static final int RINGBUFFER_NO_GC_DEFAULT_SIZE = 4096;
  
  static final boolean ASYNC_LOGGER_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL = PropertiesUtil.getProperties()
    .getBooleanProperty("AsyncLogger.SynchronizeEnqueueWhenQueueFull", true);
  
  static final boolean ASYNC_CONFIG_SYNCHRONIZE_ENQUEUE_WHEN_QUEUE_FULL = PropertiesUtil.getProperties()
    .getBooleanProperty("AsyncLoggerConfig.SynchronizeEnqueueWhenQueueFull", true);
  
  static WaitStrategy createWaitStrategy(String propertyName, AsyncWaitStrategyFactory asyncWaitStrategyFactory) {
    if (asyncWaitStrategyFactory == null) {
      LOGGER.debug("No AsyncWaitStrategyFactory was configured in the configuration, using default factory...");
      return (new DefaultAsyncWaitStrategyFactory(propertyName)).createWaitStrategy();
    } 
    LOGGER.debug("Using configured AsyncWaitStrategyFactory {}", asyncWaitStrategyFactory.getClass().getName());
    return asyncWaitStrategyFactory.createWaitStrategy();
  }
  
  static int calculateRingBufferSize(String propertyName) {
    int ringBufferSize = Constants.ENABLE_THREADLOCALS ? 4096 : 262144;
    String userPreferredRBSize = PropertiesUtil.getProperties().getStringProperty(propertyName, 
        String.valueOf(ringBufferSize));
    try {
      int size = Integers.parseInt(userPreferredRBSize);
      if (size < 128) {
        size = 128;
        LOGGER.warn("Invalid RingBufferSize {}, using minimum size {}.", userPreferredRBSize, 
            Integer.valueOf(128));
      } 
      ringBufferSize = size;
    } catch (Exception ex) {
      LOGGER.warn("Invalid RingBufferSize {}, using default size {}.", userPreferredRBSize, Integer.valueOf(ringBufferSize));
    } 
    return Integers.ceilingNextPowerOfTwo(ringBufferSize);
  }
  
  static ExceptionHandler<RingBufferLogEvent> getAsyncLoggerExceptionHandler() {
    String cls = PropertiesUtil.getProperties().getStringProperty("AsyncLogger.ExceptionHandler");
    if (cls == null)
      return new AsyncLoggerDefaultExceptionHandler(); 
    try {
      Class<? extends ExceptionHandler<RingBufferLogEvent>> klass = Loader.loadClass(cls);
      return klass.newInstance();
    } catch (Exception ignored) {
      LOGGER.debug("Invalid AsyncLogger.ExceptionHandler value: error creating {}: ", cls, ignored);
      return new AsyncLoggerDefaultExceptionHandler();
    } 
  }
  
  static ExceptionHandler<AsyncLoggerConfigDisruptor.Log4jEventWrapper> getAsyncLoggerConfigExceptionHandler() {
    String cls = PropertiesUtil.getProperties().getStringProperty("AsyncLoggerConfig.ExceptionHandler");
    if (cls == null)
      return new AsyncLoggerConfigDefaultExceptionHandler(); 
    try {
      Class<? extends ExceptionHandler<AsyncLoggerConfigDisruptor.Log4jEventWrapper>> klass = Loader.loadClass(cls);
      return klass.newInstance();
    } catch (Exception ignored) {
      LOGGER.debug("Invalid AsyncLoggerConfig.ExceptionHandler value: error creating {}: ", cls, ignored);
      return new AsyncLoggerConfigDefaultExceptionHandler();
    } 
  }
  
  public static long getExecutorThreadId(ExecutorService executor) {
    Future<Long> result = executor.submit(() -> Long.valueOf(Thread.currentThread().getId()));
    try {
      return ((Long)result.get()).longValue();
    } catch (Exception ex) {
      String msg = "Could not obtain executor thread Id. Giving up to avoid the risk of application deadlock.";
      throw new IllegalStateException("Could not obtain executor thread Id. Giving up to avoid the risk of application deadlock.", ex);
    } 
  }
}
