package org.apache.logging.log4j.io;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Objects;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LoggingException;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;
import org.apache.logging.log4j.util.StackLocatorUtil;

public class IoBuilder {
  private final ExtendedLogger logger;
  
  private Level level;
  
  private Marker marker;
  
  private String fqcn;
  
  private boolean autoFlush;
  
  private boolean buffered;
  
  private int bufferSize;
  
  private Charset charset;
  
  private Reader reader;
  
  private Writer writer;
  
  private InputStream inputStream;
  
  private OutputStream outputStream;
  
  public static IoBuilder forLogger(Logger logger) {
    return new IoBuilder(logger);
  }
  
  public static IoBuilder forLogger(String loggerName) {
    return new IoBuilder(LogManager.getLogger(loggerName));
  }
  
  public static IoBuilder forLogger(Class<?> clazz) {
    return new IoBuilder(LogManager.getLogger(clazz));
  }
  
  public static IoBuilder forLogger() {
    return new IoBuilder(LogManager.getLogger(StackLocatorUtil.getCallerClass(2)));
  }
  
  protected IoBuilder(Logger logger) {
    if (!(logger instanceof ExtendedLogger))
      throw new UnsupportedOperationException("The provided Logger [" + String.valueOf(logger) + "] does not implement " + ExtendedLogger.class
          .getName()); 
    this.logger = (ExtendedLogger)logger;
  }
  
  public IoBuilder setLevel(Level level) {
    this.level = level;
    return this;
  }
  
  public IoBuilder setMarker(Marker marker) {
    this.marker = marker;
    return this;
  }
  
  public IoBuilder setWrapperClassName(String fqcn) {
    this.fqcn = fqcn;
    return this;
  }
  
  public IoBuilder setAutoFlush(boolean autoFlush) {
    this.autoFlush = autoFlush;
    return this;
  }
  
  public IoBuilder setBuffered(boolean buffered) {
    this.buffered = buffered;
    return this;
  }
  
  public IoBuilder setBufferSize(int bufferSize) {
    this.bufferSize = bufferSize;
    return this;
  }
  
  public IoBuilder setCharset(Charset charset) {
    this.charset = charset;
    return this;
  }
  
  public IoBuilder filter(Reader reader) {
    this.reader = reader;
    return this;
  }
  
  public IoBuilder filter(Writer writer) {
    this.writer = writer;
    return this;
  }
  
  public IoBuilder filter(InputStream inputStream) {
    this.inputStream = inputStream;
    return this;
  }
  
  public IoBuilder filter(OutputStream outputStream) {
    this.outputStream = outputStream;
    return this;
  }
  
  public Reader buildReader() {
    Reader in = Objects.<Reader>requireNonNull(this.reader, "reader");
    if (this.buffered) {
      if (this.bufferSize > 0)
        return new LoggerBufferedReader(in, this.bufferSize, this.logger, this.fqcn, this.level, this.marker); 
      return new LoggerBufferedReader(in, this.logger, this.fqcn, this.level, this.marker);
    } 
    return new LoggerReader(in, this.logger, this.fqcn, this.level, this.marker);
  }
  
  public Writer buildWriter() {
    if (this.writer == null)
      return new LoggerWriter(this.logger, this.fqcn, this.level, this.marker); 
    return new LoggerFilterWriter(this.writer, this.logger, this.fqcn, this.level, this.marker);
  }
  
  public PrintWriter buildPrintWriter() {
    if (this.writer == null)
      return new LoggerPrintWriter(this.logger, this.autoFlush, this.fqcn, this.level, this.marker); 
    return new LoggerPrintWriter(this.writer, this.autoFlush, this.logger, this.fqcn, this.level, this.marker);
  }
  
  public InputStream buildInputStream() {
    InputStream in = Objects.<InputStream>requireNonNull(this.inputStream, "inputStream");
    if (this.buffered) {
      if (this.bufferSize > 0)
        return new LoggerBufferedInputStream(in, this.charset, this.bufferSize, this.logger, this.fqcn, this.level, this.marker); 
      return new LoggerBufferedInputStream(in, this.charset, this.logger, this.fqcn, this.level, this.marker);
    } 
    return new LoggerInputStream(in, this.charset, this.logger, this.fqcn, this.level, this.marker);
  }
  
  public OutputStream buildOutputStream() {
    if (this.outputStream == null)
      return new LoggerOutputStream(this.logger, this.level, this.marker, this.charset, this.fqcn); 
    return new LoggerFilterOutputStream(this.outputStream, this.charset, this.logger, this.fqcn, this.level, this.marker);
  }
  
  public PrintStream buildPrintStream() {
    try {
      if (this.outputStream == null)
        return new LoggerPrintStream(this.logger, this.autoFlush, this.charset, this.fqcn, this.level, this.marker); 
      return new LoggerPrintStream(this.outputStream, this.autoFlush, this.charset, this.logger, this.fqcn, this.level, this.marker);
    } catch (UnsupportedEncodingException e) {
      throw new LoggingException(e);
    } 
  }
}
