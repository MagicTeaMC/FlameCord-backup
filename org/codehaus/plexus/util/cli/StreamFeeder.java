package org.codehaus.plexus.util.cli;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StreamFeeder extends AbstractStreamHandler {
  private InputStream input;
  
  private OutputStream output;
  
  private volatile Throwable exception = null;
  
  public StreamFeeder(InputStream input, OutputStream output) {
    this.input = input;
    this.output = output;
  }
  
  public void run() {
    try {
      feed();
    } catch (Throwable ex) {
      if (this.exception == null)
        this.exception = ex; 
    } finally {
      close();
      synchronized (this) {
        setDone();
        notifyAll();
      } 
    } 
  }
  
  public void close() {
    if (this.input != null)
      synchronized (this.input) {
        try {
          this.input.close();
        } catch (IOException ex) {
          if (this.exception == null)
            this.exception = ex; 
        } 
        this.input = null;
      }  
    if (this.output != null)
      synchronized (this.output) {
        try {
          this.output.close();
        } catch (IOException ex) {
          if (this.exception == null)
            this.exception = ex; 
        } 
        this.output = null;
      }  
  }
  
  public Throwable getException() {
    return this.exception;
  }
  
  private void feed() throws IOException {
    boolean flush = false;
    int data = this.input.read();
    while (!isDone() && data != -1) {
      synchronized (this.output) {
        if (!isDisabled()) {
          this.output.write(data);
          flush = true;
        } 
        data = this.input.read();
      } 
    } 
    if (flush)
      this.output.flush(); 
  }
}
