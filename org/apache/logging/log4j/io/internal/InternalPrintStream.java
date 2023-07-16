package org.apache.logging.log4j.io.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Locale;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.spi.ExtendedLogger;

public class InternalPrintStream extends PrintStream {
  public InternalPrintStream(ExtendedLogger logger, boolean autoFlush, Charset charset, String fqcn, Level level, Marker marker) throws UnsupportedEncodingException {
    super(new InternalOutputStream(logger, level, marker, ensureNonNull(charset), fqcn), autoFlush, 
        ensureNonNull(charset).name());
  }
  
  public InternalPrintStream(OutputStream out, boolean autoFlush, Charset charset, ExtendedLogger logger, String fqcn, Level level, Marker marker) throws UnsupportedEncodingException {
    super(new InternalFilterOutputStream(out, ensureNonNull(charset), logger, fqcn, level, marker), autoFlush, 
        ensureNonNull(charset).name());
  }
  
  private static Charset ensureNonNull(Charset charset) {
    return (charset == null) ? Charset.defaultCharset() : charset;
  }
  
  public InternalPrintStream append(char c) {
    super.append(c);
    return this;
  }
  
  public InternalPrintStream append(CharSequence csq) {
    super.append(csq);
    return this;
  }
  
  public InternalPrintStream append(CharSequence csq, int start, int end) {
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
  
  public InternalPrintStream format(Locale l, String format, Object... args) {
    super.format(l, format, args);
    return this;
  }
  
  public InternalPrintStream format(String format, Object... args) {
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
  
  public InternalPrintStream printf(Locale l, String format, Object... args) {
    super.printf(l, format, args);
    return this;
  }
  
  public InternalPrintStream printf(String format, Object... args) {
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
  
  public void write(byte[] b) throws IOException {
    super.write(b);
  }
  
  public void write(byte[] b, int off, int len) {
    super.write(b, off, len);
  }
  
  public void write(int b) {
    super.write(b);
  }
}
