package com.mysql.cj.log;

import com.mysql.cj.util.LogUtils;
import com.mysql.cj.util.Util;
import java.util.Date;

public class StandardLogger implements Log {
  private static final int FATAL = 0;
  
  private static final int ERROR = 1;
  
  private static final int WARN = 2;
  
  private static final int INFO = 3;
  
  private static final int DEBUG = 4;
  
  private static final int TRACE = 5;
  
  private boolean logLocationInfo = true;
  
  public StandardLogger(String name) {
    this(name, false);
  }
  
  public StandardLogger(String name, boolean logLocationInfo) {
    this.logLocationInfo = logLocationInfo;
  }
  
  public boolean isDebugEnabled() {
    return true;
  }
  
  public boolean isErrorEnabled() {
    return true;
  }
  
  public boolean isFatalEnabled() {
    return true;
  }
  
  public boolean isInfoEnabled() {
    return true;
  }
  
  public boolean isTraceEnabled() {
    return true;
  }
  
  public boolean isWarnEnabled() {
    return true;
  }
  
  public void logDebug(Object message) {
    logInternal(4, message, null);
  }
  
  public void logDebug(Object message, Throwable exception) {
    logInternal(4, message, exception);
  }
  
  public void logError(Object message) {
    logInternal(1, message, null);
  }
  
  public void logError(Object message, Throwable exception) {
    logInternal(1, message, exception);
  }
  
  public void logFatal(Object message) {
    logInternal(0, message, null);
  }
  
  public void logFatal(Object message, Throwable exception) {
    logInternal(0, message, exception);
  }
  
  public void logInfo(Object message) {
    logInternal(3, message, null);
  }
  
  public void logInfo(Object message, Throwable exception) {
    logInternal(3, message, exception);
  }
  
  public void logTrace(Object message) {
    logInternal(5, message, null);
  }
  
  public void logTrace(Object message, Throwable exception) {
    logInternal(5, message, exception);
  }
  
  public void logWarn(Object message) {
    logInternal(2, message, null);
  }
  
  public void logWarn(Object message, Throwable exception) {
    logInternal(2, message, exception);
  }
  
  protected String logInternal(int level, Object msg, Throwable exception) {
    StringBuilder msgBuf = new StringBuilder();
    msgBuf.append((new Date()).toString());
    msgBuf.append(" ");
    switch (level) {
      case 0:
        msgBuf.append("FATAL: ");
        break;
      case 1:
        msgBuf.append("ERROR: ");
        break;
      case 2:
        msgBuf.append("WARN: ");
        break;
      case 3:
        msgBuf.append("INFO: ");
        break;
      case 4:
        msgBuf.append("DEBUG: ");
        break;
      case 5:
        msgBuf.append("TRACE: ");
        break;
    } 
    if (msg instanceof ProfilerEvent) {
      msgBuf.append(msg.toString());
    } else {
      if (this.logLocationInfo && level != 5) {
        Throwable locationException = new Throwable();
        msgBuf.append(LogUtils.findCallingClassAndMethod(locationException));
        msgBuf.append(" ");
      } 
      if (msg != null)
        msgBuf.append(String.valueOf(msg)); 
    } 
    if (exception != null) {
      msgBuf.append("\n");
      msgBuf.append("\n");
      msgBuf.append("EXCEPTION STACK TRACE:");
      msgBuf.append("\n");
      msgBuf.append("\n");
      msgBuf.append(Util.stackTraceToString(exception));
    } 
    String messageAsString = msgBuf.toString();
    System.err.println(messageAsString);
    return messageAsString;
  }
}
