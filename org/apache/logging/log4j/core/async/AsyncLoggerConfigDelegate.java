package org.apache.logging.log4j.core.async;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.impl.LogEventFactory;
import org.apache.logging.log4j.core.jmx.RingBufferAdmin;

public interface AsyncLoggerConfigDelegate {
  RingBufferAdmin createRingBufferAdmin(String paramString1, String paramString2);
  
  EventRoute getEventRoute(Level paramLevel);
  
  void enqueueEvent(LogEvent paramLogEvent, AsyncLoggerConfig paramAsyncLoggerConfig);
  
  boolean tryEnqueue(LogEvent paramLogEvent, AsyncLoggerConfig paramAsyncLoggerConfig);
  
  void setLogEventFactory(LogEventFactory paramLogEventFactory);
}
