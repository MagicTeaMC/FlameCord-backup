package org.apache.http.impl.conn;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.logging.Log;
import org.apache.http.annotation.Contract;
import org.apache.http.annotation.ThreadingBehavior;
import org.apache.http.util.Args;

@Contract(threading = ThreadingBehavior.IMMUTABLE)
public class Wire {
  private final Log log;
  
  private final String id;
  
  public Wire(Log log, String id) {
    this.log = log;
    this.id = id;
  }
  
  public Wire(Log log) {
    this(log, "");
  }
  
  private void wire(String header, InputStream inStream) throws IOException {
    StringBuilder buffer = new StringBuilder();
    int ch;
    while ((ch = inStream.read()) != -1) {
      if (ch == 13) {
        buffer.append("[\\r]");
        continue;
      } 
      if (ch == 10) {
        buffer.append("[\\n]\"");
        buffer.insert(0, "\"");
        buffer.insert(0, header);
        this.log.debug(this.id + " " + buffer.toString());
        buffer.setLength(0);
        continue;
      } 
      if (ch < 32 || ch > 127) {
        buffer.append("[0x");
        buffer.append(Integer.toHexString(ch));
        buffer.append("]");
        continue;
      } 
      buffer.append((char)ch);
    } 
    if (buffer.length() > 0) {
      buffer.append('"');
      buffer.insert(0, '"');
      buffer.insert(0, header);
      this.log.debug(this.id + " " + buffer.toString());
    } 
  }
  
  public boolean enabled() {
    return this.log.isDebugEnabled();
  }
  
  public void output(InputStream outStream) throws IOException {
    Args.notNull(outStream, "Output");
    wire(">> ", outStream);
  }
  
  public void input(InputStream inStream) throws IOException {
    Args.notNull(inStream, "Input");
    wire("<< ", inStream);
  }
  
  public void output(byte[] b, int off, int len) throws IOException {
    Args.notNull(b, "Output");
    wire(">> ", new ByteArrayInputStream(b, off, len));
  }
  
  public void input(byte[] b, int off, int len) throws IOException {
    Args.notNull(b, "Input");
    wire("<< ", new ByteArrayInputStream(b, off, len));
  }
  
  public void output(byte[] b) throws IOException {
    Args.notNull(b, "Output");
    wire(">> ", new ByteArrayInputStream(b));
  }
  
  public void input(byte[] b) throws IOException {
    Args.notNull(b, "Input");
    wire("<< ", new ByteArrayInputStream(b));
  }
  
  public void output(int b) throws IOException {
    output(new byte[] { (byte)b });
  }
  
  public void input(int b) throws IOException {
    input(new byte[] { (byte)b });
  }
  
  public void output(String s) throws IOException {
    Args.notNull(s, "Output");
    output(s.getBytes());
  }
  
  public void input(String s) throws IOException {
    Args.notNull(s, "Input");
    input(s.getBytes());
  }
}
