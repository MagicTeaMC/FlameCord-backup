package org.apache.logging.log4j.io.internal;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalPrintWriter extends PrintWriter {
  public InternalPrintWriter(ExtendedLogger logger, boolean autoFlush, String fqcn, Level level, Marker marker) {
    super(new InternalWriter(logger, fqcn, level, marker), autoFlush);
  }
  
  public InternalPrintWriter(Writer writer, boolean autoFlush, ExtendedLogger logger, String fqcn, Level level, Marker marker) {
    super(new InternalFilterWriter(writer, logger, fqcn, level, marker), autoFlush);
  }
  
  public InternalPrintWriter append(char c) {
    super.append(c);
    return this;
  }
  
  public InternalPrintWriter append(CharSequence csq) {
    super.append(csq);
    return this;
  }
  
  public InternalPrintWriter append(CharSequence csq, int start, int end) {
    super.append(csq, start, end);
    return this;
  }
  
  public boolean checkError() {
    return super.checkError();
  }
  
  public void close() {
    super.close();
  }
  
  public void flush() {
    super.flush();
  }
  
  public InternalPrintWriter format(Locale l, String format, Object... args) {
    super.format(l, format, args);
    return this;
  }
  
  public InternalPrintWriter format(String format, Object... args) {
    super.format(format, args);
    return this;
  }
  
  public void print(boolean b) {
    super.print(b);
  }
  
  public void print(char c) {
    super.print(c);
  }
  
  public void print(char[] s) {
    super.print(s);
  }
  
  public void print(double d) {
    super.print(d);
  }
  
  public void print(float f) {
    super.print(f);
  }
  
  public void print(int i) {
    super.print(i);
  }
  
  public void print(long l) {
    super.print(l);
  }
  
  public void print(Object obj) {
    super.print(obj);
  }
  
  public void print(String s) {
    super.print(s);
  }
  
  public InternalPrintWriter printf(Locale l, String format, Object... args) {
    super.printf(l, format, args);
    return this;
  }
  
  public InternalPrintWriter printf(String format, Object... args) {
    super.printf(format, args);
    return this;
  }
  
  public void println() {
    super.println();
  }
  
  public void println(boolean x) {
    super.println(x);
  }
  
  public void println(char x) {
    super.println(x);
  }
  
  public void println(char[] x) {
    super.println(x);
  }
  
  public void println(double x) {
    super.println(x);
  }
  
  public void println(float x) {
    super.println(x);
  }
  
  public void println(int x) {
    super.println(x);
  }
  
  public void println(long x) {
    super.println(x);
  }
  
  public void println(Object x) {
    super.println(x);
  }
  
  public void println(String x) {
    super.println(x);
  }
  
  public String toString() {
    return "{stream=" + this.out + '}';
  }
  
  public void write(char[] buf) {
    super.write(buf);
  }
  
  public void write(char[] buf, int off, int len) {
    super.write(buf, off, len);
  }
  
  public void write(int c) {
    super.write(c);
  }
  
  public void write(String s) {
    super.write(s);
  }
  
  public void write(String s, int off, int len) {
    super.write(s, off, len);
  }
}
