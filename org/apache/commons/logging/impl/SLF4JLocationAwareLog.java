package org.apache.commons.logging.impl;

import java.io.ObjectStreamException;
import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

public class SLF4JLocationAwareLog implements Log, Serializable {
  private static final long serialVersionUID = -2379157579039314822L;
  
  protected String name;
  
  private transient LocationAwareLogger logger;
  
  private static final String FQCN = SLF4JLocationAwareLog.class.getName();
  
  SLF4JLocationAwareLog(LocationAwareLogger logger) {
    this.logger = logger;
    this.name = logger.getName();
  }
  
  public boolean isTraceEnabled() {
    return this.logger.isTraceEnabled();
  }
  
  public boolean isDebugEnabled() {
    return this.logger.isDebugEnabled();
  }
  
  public boolean isInfoEnabled() {
    return this.logger.isInfoEnabled();
  }
  
  public boolean isWarnEnabled() {
    return this.logger.isWarnEnabled();
  }
  
  public boolean isErrorEnabled() {
    return this.logger.isErrorEnabled();
  }
  
  public boolean isFatalEnabled() {
    return this.logger.isErrorEnabled();
  }
  
  public void trace(Object message) {
    if (isTraceEnabled())
      this.logger.log(null, FQCN, 0, String.valueOf(message), null, null); 
  }
  
  public void trace(Object message, Throwable t) {
    if (isTraceEnabled())
      this.logger.log(null, FQCN, 0, String.valueOf(message), null, t); 
  }
  
  public void debug(Object message) {
    if (isDebugEnabled())
      this.logger.log(null, FQCN, 10, String.valueOf(message), null, null); 
  }
  
  public void debug(Object message, Throwable t) {
    if (isDebugEnabled())
      this.logger.log(null, FQCN, 10, String.valueOf(message), null, t); 
  }
  
  public void info(Object message) {
    if (isInfoEnabled())
      this.logger.log(null, FQCN, 20, String.valueOf(message), null, null); 
  }
  
  public void info(Object message, Throwable t) {
    if (isInfoEnabled())
      this.logger.log(null, FQCN, 20, String.valueOf(message), null, t); 
  }
  
  public void warn(Object message) {
    if (isWarnEnabled())
      this.logger.log(null, FQCN, 30, String.valueOf(message), null, null); 
  }
  
  public void warn(Object message, Throwable t) {
    if (isWarnEnabled())
      this.logger.log(null, FQCN, 30, String.valueOf(message), null, t); 
  }
  
  public void error(Object message) {
    if (isErrorEnabled())
      this.logger.log(null, FQCN, 40, String.valueOf(message), null, null); 
  }
  
  public void error(Object message, Throwable t) {
    if (isErrorEnabled())
      this.logger.log(null, FQCN, 40, String.valueOf(message), null, t); 
  }
  
  public void fatal(Object message) {
    if (isErrorEnabled())
      this.logger.log(null, FQCN, 40, String.valueOf(message), null, null); 
  }
  
  public void fatal(Object message, Throwable t) {
    if (isErrorEnabled())
      this.logger.log(null, FQCN, 40, String.valueOf(message), null, t); 
  }
  
  protected Object readResolve() throws ObjectStreamException {
    Logger logger = LoggerFactory.getLogger(this.name);
    return new SLF4JLocationAwareLog((LocationAwareLogger)logger);
  }
}
