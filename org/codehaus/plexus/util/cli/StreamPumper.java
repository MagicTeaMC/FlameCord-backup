package org.codehaus.plexus.util.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class StreamPumper extends AbstractStreamHandler {
  private final BufferedReader in;
  
  private final StreamConsumer consumer;
  
  private final PrintWriter out;
  
  private volatile Exception exception = null;
  
  private static final int SIZE = 1024;
  
  public StreamPumper(InputStream in) {
    this(in, (StreamConsumer)null);
  }
  
  public StreamPumper(InputStream in, StreamConsumer consumer) {
    this(in, (PrintWriter)null, consumer);
  }
  
  public StreamPumper(InputStream in, PrintWriter writer) {
    this(in, writer, (StreamConsumer)null);
  }
  
  public StreamPumper(InputStream in, PrintWriter writer, StreamConsumer consumer) {
    this.in = new BufferedReader(new InputStreamReader(in), 1024);
    this.out = writer;
    this.consumer = consumer;
  }
  
  public void run() {
    boolean outError = (this.out != null) ? this.out.checkError() : false;
    try {
      for (String line = this.in.readLine(); line != null; line = this.in.readLine()) {
        try {
          if (this.exception == null && this.consumer != null && !isDisabled())
            this.consumer.consumeLine(line); 
        } catch (Exception t) {
          this.exception = t;
        } 
        if (this.out != null && !outError) {
          this.out.println(line);
          this.out.flush();
          if (this.out.checkError()) {
            outError = true;
            try {
              throw new IOException(String.format("Failure printing line '%s'.", new Object[] { line }));
            } catch (IOException e) {
              this.exception = e;
            } 
          } 
        } 
      } 
    } catch (IOException e) {
      this.exception = e;
    } finally {
      try {
        this.in.close();
      } catch (IOException e2) {
        if (this.exception == null)
          this.exception = e2; 
      } 
      synchronized (this) {
        setDone();
        notifyAll();
      } 
    } 
  }
  
  public void flush() {
    if (this.out != null) {
      this.out.flush();
      if (this.out.checkError() && this.exception == null)
        try {
          throw new IOException("Failure flushing output.");
        } catch (IOException e) {
          this.exception = e;
        }  
    } 
  }
  
  public void close() {
    if (this.out != null) {
      this.out.close();
      if (this.out.checkError() && this.exception == null)
        try {
          throw new IOException("Failure closing output.");
        } catch (IOException e) {
          this.exception = e;
        }  
    } 
  }
  
  public Exception getException() {
    return this.exception;
  }
}
