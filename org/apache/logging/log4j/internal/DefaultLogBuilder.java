package org.apache.logging.log4j.internal;

import java.util.Arrays;
import org.apache.logging.log4j.BridgeAware;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogBuilder;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.message.Message;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LambdaUtil;
import org.apache.logging.log4j.util.StackLocatorUtil;
import org.apache.logging.log4j.util.Supplier;

public class DefaultLogBuilder implements BridgeAware, LogBuilder {
  private static Message EMPTY_MESSAGE = (Message)new SimpleMessage("");
  
  private static final String FQCN = DefaultLogBuilder.class.getName();
  
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private ExtendedLogger logger;
  
  private Level level;
  
  private Marker marker;
  
  private Throwable throwable;
  
  private StackTraceElement location;
  
  private volatile boolean inUse;
  
  private long threadId;
  
  private String fqcn = FQCN;
  
  public DefaultLogBuilder(ExtendedLogger logger, Level level) {
    this.logger = logger;
    this.level = level;
    this.threadId = Thread.currentThread().getId();
    this.inUse = (level != null);
  }
  
  public DefaultLogBuilder() {
    this(null, null);
  }
  
  public void setEntryPoint(String fqcn) {
    this.fqcn = fqcn;
  }
  
  public LogBuilder reset(ExtendedLogger logger, Level level) {
    this.logger = logger;
    this.level = level;
    this.marker = null;
    this.throwable = null;
    this.location = null;
    this.inUse = true;
    return this;
  }
  
  public LogBuilder withMarker(Marker marker) {
    this.marker = marker;
    return this;
  }
  
  public LogBuilder withThrowable(Throwable throwable) {
    this.throwable = throwable;
    return this;
  }
  
  public LogBuilder withLocation() {
    this.location = StackLocatorUtil.getStackTraceElement(2);
    return this;
  }
  
  public LogBuilder withLocation(StackTraceElement location) {
    this.location = location;
    return this;
  }
  
  public boolean isInUse() {
    return this.inUse;
  }
  
  public void log(Message message) {
    if (isValid() && isEnabled(message))
      logMessage(message); 
  }
  
  public Message logAndGet(Supplier<Message> messageSupplier) {
    Message message = null;
    if (isValid() && isEnabled(message = (Message)messageSupplier.get()))
      logMessage(message); 
    return message;
  }
  
  public void log(CharSequence message) {
    if (isValid() && isEnabled(message))
      logMessage(this.logger.getMessageFactory().newMessage(message)); 
  }
  
  public void log(String message) {
    if (isValid() && isEnabled(message))
      logMessage(this.logger.getMessageFactory().newMessage(message)); 
  }
  
  public void log(String message, Object... params) {
    if (isValid() && isEnabled(message, params))
      logMessage(this.logger.getMessageFactory().newMessage(message, params)); 
  }
  
  public void log(String message, Supplier<?>... params) {
    Object[] objs;
    if (isValid() && isEnabled(message, objs = LambdaUtil.getAll((Supplier[])params)))
      logMessage(this.logger.getMessageFactory().newMessage(message, objs)); 
  }
  
  public void log(Supplier<Message> messageSupplier) {
    logAndGet(messageSupplier);
  }
  
  public void log(Object message) {
    if (isValid() && isEnabled(message))
      logMessage(this.logger.getMessageFactory().newMessage(message)); 
  }
  
  public void log(String message, Object p0) {
    if (isValid() && isEnabled(message, p0))
      logMessage(this.logger.getMessageFactory().newMessage(message, new Object[] { p0 })); 
  }
  
  public void log(String message, Object p0, Object p1) {
    if (isValid() && isEnabled(message, p0, p1))
      logMessage(this.logger.getMessageFactory().newMessage(message, new Object[] { p0, p1 })); 
  }
  
  public void log(String message, Object p0, Object p1, Object p2) {
    if (isValid() && isEnabled(message, p0, p1, p2))
      logMessage(this.logger.getMessageFactory().newMessage(message, new Object[] { p0, p1, p2 })); 
  }
  
  public void log(String message, Object p0, Object p1, Object p2, Object p3) {
    if (isValid() && isEnabled(message, p0, p1, p2, p3))
      logMessage(this.logger.getMessageFactory().newMessage(message, new Object[] { p0, p1, p2, p3 })); 
  }
  
  public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
    if (isValid() && isEnabled(message, p0, p1, p2, p3, p4))
      logMessage(this.logger.getMessageFactory().newMessage(message, new Object[] { p0, p1, p2, p3, p4 })); 
  }
  
  public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
    if (isValid() && isEnabled(message, p0, p1, p2, p3, p4, p5))
      logMessage(this.logger.getMessageFactory().newMessage(message, new Object[] { p0, p1, p2, p3, p4, p5 })); 
  }
  
  public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
    if (isValid() && isEnabled(message, p0, p1, p2, p3, p4, p5, p6))
      logMessage(this.logger.getMessageFactory().newMessage(message, new Object[] { p0, p1, p2, p3, p4, p5, p6 })); 
  }
  
  public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
    if (isValid() && isEnabled(message, p0, p1, p2, p3, p4, p5, p6, p7))
      logMessage(this.logger.getMessageFactory().newMessage(message, new Object[] { p0, p1, p2, p3, p4, p5, p6, p7 })); 
  }
  
  public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
    if (isValid() && isEnabled(message, p0, p1, p2, p3, p4, p5, p6, p7, p8))
      logMessage(this.logger.getMessageFactory().newMessage(message, new Object[] { p0, p1, p2, p3, p4, p5, p6, p7, p8 })); 
  }
  
  public void log(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
    if (isValid() && isEnabled(message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9))
      logMessage(this.logger.getMessageFactory().newMessage(message, new Object[] { p0, p1, p2, p3, p4, p5, p6, p7, p8, p9 })); 
  }
  
  public void log() {
    if (isValid() && isEnabled(EMPTY_MESSAGE))
      logMessage(EMPTY_MESSAGE); 
  }
  
  private void logMessage(Message message) {
    try {
      this.logger.logMessage(this.level, this.marker, this.fqcn, this.location, message, this.throwable);
    } finally {
      this.inUse = false;
    } 
  }
  
  private boolean isValid() {
    if (!this.inUse) {
      LOGGER.warn("Attempt to reuse LogBuilder was ignored. {}", 
          StackLocatorUtil.getCallerClass(2));
      return false;
    } 
    if (this.threadId != Thread.currentThread().getId()) {
      LOGGER.warn("LogBuilder can only be used on the owning thread. {}", 
          StackLocatorUtil.getCallerClass(2));
      return false;
    } 
    return true;
  }
  
  protected boolean isEnabled(Message message) {
    return this.logger.isEnabled(this.level, this.marker, message, this.throwable);
  }
  
  protected boolean isEnabled(CharSequence message) {
    return this.logger.isEnabled(this.level, this.marker, message, this.throwable);
  }
  
  protected boolean isEnabled(String message) {
    return this.logger.isEnabled(this.level, this.marker, message, this.throwable);
  }
  
  protected boolean isEnabled(String message, Object... params) {
    Object[] newParams;
    if (this.throwable != null) {
      newParams = Arrays.copyOf(params, params.length + 1);
      newParams[params.length] = this.throwable;
    } else {
      newParams = params;
    } 
    return this.logger.isEnabled(this.level, this.marker, message, newParams);
  }
  
  protected boolean isEnabled(Object message) {
    return this.logger.isEnabled(this.level, this.marker, message, this.throwable);
  }
  
  protected boolean isEnabled(String message, Object p0) {
    return (this.throwable != null) ? this.logger.isEnabled(this.level, this.marker, message, p0, this.throwable) : this.logger
      .isEnabled(this.level, this.marker, message, p0);
  }
  
  protected boolean isEnabled(String message, Object p0, Object p1) {
    return (this.throwable != null) ? this.logger.isEnabled(this.level, this.marker, message, p0, p1, this.throwable) : this.logger
      .isEnabled(this.level, this.marker, message, p0, p1);
  }
  
  protected boolean isEnabled(String message, Object p0, Object p1, Object p2) {
    return (this.throwable != null) ? this.logger.isEnabled(this.level, this.marker, message, p0, p1, p2, this.throwable) : this.logger
      .isEnabled(this.level, this.marker, message, p0, p1, p2);
  }
  
  protected boolean isEnabled(String message, Object p0, Object p1, Object p2, Object p3) {
    return (this.throwable != null) ? this.logger.isEnabled(this.level, this.marker, message, p0, p1, p2, p3, this.throwable) : this.logger
      .isEnabled(this.level, this.marker, message, p0, p1, p2, p3);
  }
  
  protected boolean isEnabled(String message, Object p0, Object p1, Object p2, Object p3, Object p4) {
    return (this.throwable != null) ? this.logger.isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4, this.throwable) : this.logger
      .isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4);
  }
  
  protected boolean isEnabled(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
    return (this.throwable != null) ? this.logger.isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4, p5, this.throwable) : this.logger
      .isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4, p5);
  }
  
  protected boolean isEnabled(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
    return (this.throwable != null) ? this.logger.isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4, p5, p6, this.throwable) : this.logger
      .isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4, p5, p6);
  }
  
  protected boolean isEnabled(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
    return (this.throwable != null) ? this.logger.isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4, p5, p6, p7, this.throwable) : this.logger
      .isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4, p5, p6, p7);
  }
  
  protected boolean isEnabled(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
    return (this.throwable != null) ? this.logger
      .isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, this.throwable) : this.logger
      .isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8);
  }
  
  protected boolean isEnabled(String message, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
    return (this.throwable != null) ? this.logger
      .isEnabled(this.level, this.marker, message, new Object[] { 
          p0, p1, p2, p3, p4, p5, p6, p7, p8, p9, 
          this.throwable }) : this.logger
      .isEnabled(this.level, this.marker, message, p0, p1, p2, p3, p4, p5, p6, p7, p8, p9);
  }
}
