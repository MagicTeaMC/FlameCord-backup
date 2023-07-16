package org.apache.logging.log4j.status;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.MessageFactory;
import org.apache.logging.log4j.message.ParameterizedNoReferenceMessageFactory;

public class StatusConsoleListener implements StatusListener {
  private Level level;
  
  private String[] filters;
  
  private final PrintStream stream;
  
  private final Logger logger;
  
  public StatusConsoleListener(Level level) {
    this(level, System.out);
  }
  
  public StatusConsoleListener(Level level, PrintStream stream) {
    this(level, stream, SimpleLoggerFactory.getInstance());
  }
  
  StatusConsoleListener(Level level, PrintStream stream, SimpleLoggerFactory loggerFactory) {
    this.level = Objects.<Level>requireNonNull(level, "level");
    this.stream = Objects.<PrintStream>requireNonNull(stream, "stream");
    this
      
      .logger = (Logger)((SimpleLoggerFactory)Objects.<SimpleLoggerFactory>requireNonNull(loggerFactory, "loggerFactory")).createSimpleLogger("StatusConsoleListener", level, (MessageFactory)ParameterizedNoReferenceMessageFactory.INSTANCE, stream);
  }
  
  public void setLevel(Level level) {
    this.level = level;
  }
  
  public Level getStatusLevel() {
    return this.level;
  }
  
  public void log(StatusData data) {
    boolean filtered = filtered(data);
    if (!filtered)
      this.logger
        
        .atLevel(data.getLevel())
        .withThrowable(data.getThrowable())
        .withLocation(data.getStackTraceElement())
        .log(data.getMessage()); 
  }
  
  public void setFilters(String... filters) {
    this.filters = filters;
  }
  
  private boolean filtered(StatusData data) {
    if (this.filters == null)
      return false; 
    String caller = data.getStackTraceElement().getClassName();
    for (String filter : this.filters) {
      if (caller.startsWith(filter))
        return true; 
    } 
    return false;
  }
  
  public void close() throws IOException {
    if (this.stream != System.out && this.stream != System.err)
      this.stream.close(); 
  }
}
