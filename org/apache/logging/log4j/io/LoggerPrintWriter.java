package org.apache.logging.log4j.io;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.io.internal.InternalPrintWriter;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class LoggerPrintWriter extends PrintWriter {
  private static final String FQCN = LoggerPrintWriter.class.getName();
  
  private final InternalPrintWriter writer;
  
  protected LoggerPrintWriter(ExtendedLogger logger, boolean autoFlush, String fqcn, Level level, Marker marker) {
    super(new StringWriter());
    this.writer = new InternalPrintWriter(logger, autoFlush, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  protected LoggerPrintWriter(Writer writer, boolean autoFlush, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(writer);
    this.writer = new InternalPrintWriter(writer, autoFlush, logger, (fqcn == null) ? FQCN : fqcn, level, marker);
  }
  
  public LoggerPrintWriter append(char c) {
    this.writer.append(c);
    return this;
  }
  
  public LoggerPrintWriter append(CharSequence csq) {
    this.writer.append(csq);
    return this;
  }
  
  public LoggerPrintWriter append(CharSequence csq, int start, int end) {
    this.writer.append(csq, start, end);
    return this;
  }
  
  public boolean checkError() {
    return this.writer.checkError();
  }
  
  public void close() {
    this.writer.close();
  }
  
  public void flush() {
    this.writer.flush();
  }
  
  public LoggerPrintWriter format(Locale l, String format, Object... args) {
    this.writer.format(l, format, args);
    return this;
  }
  
  public LoggerPrintWriter format(String format, Object... args) {
    this.writer.format(format, args);
    return this;
  }
  
  public void print(boolean b) {
    this.writer.print(b);
  }
  
  public void print(char c) {
    this.writer.print(c);
  }
  
  public void print(char[] s) {
    this.writer.print(s);
  }
  
  public void print(double d) {
    this.writer.print(d);
  }
  
  public void print(float f) {
    this.writer.print(f);
  }
  
  public void print(int i) {
    this.writer.print(i);
  }
  
  public void print(long l) {
    this.writer.print(l);
  }
  
  public void print(Object obj) {
    this.writer.print(obj);
  }
  
  public void print(String s) {
    this.writer.print(s);
  }
  
  public LoggerPrintWriter printf(Locale l, String format, Object... args) {
    this.writer.printf(l, format, args);
    return this;
  }
  
  public LoggerPrintWriter printf(String format, Object... args) {
    this.writer.printf(format, args);
    return this;
  }
  
  public void println() {
    this.writer.println();
  }
  
  public void println(boolean x) {
    this.writer.println(x);
  }
  
  public void println(char x) {
    this.writer.println(x);
  }
  
  public void println(char[] x) {
    this.writer.println(x);
  }
  
  public void println(double x) {
    this.writer.println(x);
  }
  
  public void println(float x) {
    this.writer.println(x);
  }
  
  public void println(int x) {
    this.writer.println(x);
  }
  
  public void println(long x) {
    this.writer.println(x);
  }
  
  public void println(Object x) {
    this.writer.println(x);
  }
  
  public void println(String x) {
    this.writer.println(x);
  }
  
  public String toString() {
    return LoggerPrintWriter.class.getSimpleName() + this.writer.toString();
  }
  
  public void write(char[] buf) {
    this.writer.write(buf);
  }
  
  public void write(char[] buf, int off, int len) {
    this.writer.write(buf, off, len);
  }
  
  public void write(int c) {
    this.writer.write(c);
  }
  
  public void write(String s) {
    this.writer.write(s);
  }
  
  public void write(String s, int off, int len) {
    this.writer.write(s, off, len);
  }
}
