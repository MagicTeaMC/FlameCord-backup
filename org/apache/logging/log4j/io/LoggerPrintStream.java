package org.apache.logging.log4j.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalPrintStream;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerPrintStream extends PrintStream {
  private static final String FQCN = LoggerPrintStream.class.getName();
  
  private final InternalPrintStream psLogger;
  
  protected LoggerPrintStream(ExtendedLogger logger, boolean autoFlush, Charset charset, String fqcn, Level level, Marker marker) throws UnsupportedEncodingException {
    super(new PrintStream(new ByteArrayOutputStream()));
    this.psLogger = new InternalPrintStream(logger, autoFlush, charset, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  protected LoggerPrintStream(OutputStream out, boolean autoFlush, Charset charset, ExtendedLogger logger, String fqcn, Level level, Marker marker) throws UnsupportedEncodingException {
    super(new PrintStream(out));
    this.psLogger = new InternalPrintStream(out, autoFlush, charset, logger, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  private static Charset ensureNonNull(Charset charset) {
    return (charset == null) ? Charset.defaultCharset() : charset;
  }
  
  public LoggerPrintStream append(char c) {
    this.psLogger.append(c);
    return this;
  }
  
  public LoggerPrintStream append(CharSequence csq) {
    this.psLogger.append(csq);
    return this;
  }
  
  public LoggerPrintStream append(CharSequence csq, int start, int end) {
    this.psLogger.append(csq, start, end);
    return this;
  }
  
  public boolean checkError() {
    return this.psLogger.checkError();
  }
  
  public void close() {
    this.psLogger.close();
  }
  
  public void flush() {
    this.psLogger.flush();
  }
  
  public LoggerPrintStream format(Locale l, String format, Object... args) {
    this.psLogger.format(l, format, args);
    return this;
  }
  
  public LoggerPrintStream format(String format, Object... args) {
    this.psLogger.format(format, args);
    return this;
  }
  
  public void print(boolean b) {
    this.psLogger.print(b);
  }
  
  public void print(char c) {
    this.psLogger.print(c);
  }
  
  public void print(char[] s) {
    this.psLogger.print(s);
  }
  
  public void print(double d) {
    this.psLogger.print(d);
  }
  
  public void print(float f) {
    this.psLogger.print(f);
  }
  
  public void print(int i) {
    this.psLogger.print(i);
  }
  
  public void print(long l) {
    this.psLogger.print(l);
  }
  
  public void print(Object obj) {
    this.psLogger.print(obj);
  }
  
  public void print(String s) {
    this.psLogger.print(s);
  }
  
  public LoggerPrintStream printf(Locale l, String format, Object... args) {
    this.psLogger.printf(l, format, args);
    return this;
  }
  
  public LoggerPrintStream printf(String format, Object... args) {
    this.psLogger.printf(format, args);
    return this;
  }
  
  public void println() {
    this.psLogger.println();
  }
  
  public void println(boolean x) {
    this.psLogger.println(x);
  }
  
  public void println(char x) {
    this.psLogger.println(x);
  }
  
  public void println(char[] x) {
    this.psLogger.println(x);
  }
  
  public void println(double x) {
    this.psLogger.println(x);
  }
  
  public void println(float x) {
    this.psLogger.println(x);
  }
  
  public void println(int x) {
    this.psLogger.println(x);
  }
  
  public void println(long x) {
    this.psLogger.println(x);
  }
  
  public void println(Object x) {
    this.psLogger.println(x);
  }
  
  public void println(String x) {
    this.psLogger.println(x);
  }
  
  public String toString() {
    return LoggerPrintStream.class.getSimpleName() + this.psLogger.toString();
  }
  
  public void write(byte[] b) throws IOException {
    this.psLogger.write(b);
  }
  
  public void write(byte[] b, int off, int len) {
    this.psLogger.write(b, off, len);
  }
  
  public void write(int b) {
    this.psLogger.write(b);
  }
}
