package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.Pty;
import org.jline.utils.ClosedException;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingReader;

public class PosixPtyTerminal extends AbstractPosixTerminal {
  private final InputStream in;
  
  private final OutputStream out;
  
  private final InputStream masterInput;
  
  private final OutputStream masterOutput;
  
  private final NonBlockingInputStream input;
  
  private final OutputStream output;
  
  private final NonBlockingReader reader;
  
  private final PrintWriter writer;
  
  private final Object lock = new Object();
  
  private Thread inputPumpThread;
  
  private Thread outputPumpThread;
  
  private boolean paused = true;
  
  public PosixPtyTerminal(String name, String type, Pty pty, InputStream in, OutputStream out, Charset encoding) throws IOException {
    this(name, type, pty, in, out, encoding, Terminal.SignalHandler.SIG_DFL);
  }
  
  public PosixPtyTerminal(String name, String type, Pty pty, InputStream in, OutputStream out, Charset encoding, Terminal.SignalHandler signalHandler) throws IOException {
    this(name, type, pty, in, out, encoding, signalHandler, false);
  }
  
  public PosixPtyTerminal(String name, String type, Pty pty, InputStream in, OutputStream out, Charset encoding, Terminal.SignalHandler signalHandler, boolean paused) throws IOException {
    super(name, type, pty, encoding, signalHandler);
    this.in = Objects.<InputStream>requireNonNull(in);
    this.out = Objects.<OutputStream>requireNonNull(out);
    this.masterInput = pty.getMasterInput();
    this.masterOutput = pty.getMasterOutput();
    this.input = new InputStreamWrapper(NonBlocking.nonBlocking(name, pty.getSlaveInput()));
    this.output = pty.getSlaveOutput();
    this.reader = NonBlocking.nonBlocking(name, (InputStream)this.input, encoding());
    this.writer = new PrintWriter(new OutputStreamWriter(this.output, encoding()));
    parseInfoCmp();
    if (!paused)
      resume(); 
  }
  
  public InputStream input() {
    return (InputStream)this.input;
  }
  
  public NonBlockingReader reader() {
    return this.reader;
  }
  
  public OutputStream output() {
    return this.output;
  }
  
  public PrintWriter writer() {
    return this.writer;
  }
  
  protected void doClose() throws IOException {
    super.doClose();
    this.reader.close();
  }
  
  public boolean canPauseResume() {
    return true;
  }
  
  public void pause() {
    synchronized (this.lock) {
      this.paused = true;
    } 
  }
  
  public void pause(boolean wait) throws InterruptedException {
    Thread p1;
    Thread p2;
    synchronized (this.lock) {
      this.paused = true;
      p1 = this.inputPumpThread;
      p2 = this.outputPumpThread;
    } 
    if (p1 != null)
      p1.interrupt(); 
    if (p2 != null)
      p2.interrupt(); 
    if (p1 != null)
      p1.join(); 
    if (p2 != null)
      p2.join(); 
  }
  
  public void resume() {
    synchronized (this.lock) {
      this.paused = false;
      if (this.inputPumpThread == null) {
        this.inputPumpThread = new Thread(this::pumpIn, toString() + " input pump thread");
        this.inputPumpThread.setDaemon(true);
        this.inputPumpThread.start();
      } 
      if (this.outputPumpThread == null) {
        this.outputPumpThread = new Thread(this::pumpOut, toString() + " output pump thread");
        this.outputPumpThread.setDaemon(true);
        this.outputPumpThread.start();
      } 
    } 
  }
  
  public boolean paused() {
    synchronized (this.lock) {
      return this.paused;
    } 
  }
  
  private static class InputStreamWrapper extends NonBlockingInputStream {
    private final NonBlockingInputStream in;
    
    private final AtomicBoolean closed = new AtomicBoolean();
    
    protected InputStreamWrapper(NonBlockingInputStream in) {
      this.in = in;
    }
    
    public int read(long timeout, boolean isPeek) throws IOException {
      if (this.closed.get())
        throw new ClosedException(); 
      return this.in.read(timeout, isPeek);
    }
    
    public void close() throws IOException {
      this.closed.set(true);
    }
  }
  
  private void pumpIn() {
    try {
      while (true) {
        synchronized (this.lock) {
          if (this.paused) {
            this.inputPumpThread = null;
            return;
          } 
        } 
        int b = this.in.read();
        if (b < 0) {
          this.input.close();
          break;
        } 
        this.masterOutput.write(b);
        this.masterOutput.flush();
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      synchronized (this.lock) {
        this.inputPumpThread = null;
      } 
    } 
  }
  
  private void pumpOut() {
    try {
      while (true) {
        synchronized (this.lock) {
          if (this.paused) {
            this.outputPumpThread = null;
            return;
          } 
        } 
        int b = this.masterInput.read();
        if (b < 0) {
          this.input.close();
          break;
        } 
        this.out.write(b);
        this.out.flush();
      } 
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      synchronized (this.lock) {
        this.outputPumpThread = null;
      } 
    } 
    try {
      close();
    } catch (Throwable throwable) {}
  }
}
