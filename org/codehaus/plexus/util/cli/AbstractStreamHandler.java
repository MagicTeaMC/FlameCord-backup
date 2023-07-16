package org.codehaus.plexus.util.cli;

public class AbstractStreamHandler extends Thread {
  private boolean done;
  
  private volatile boolean disabled;
  
  public boolean isDone() {
    return this.done;
  }
  
  public synchronized void waitUntilDone() throws InterruptedException {
    while (!isDone())
      wait(); 
  }
  
  protected boolean isDisabled() {
    return this.disabled;
  }
  
  public void disable() {
    this.disabled = true;
  }
  
  public void setDone() {
    this.done = true;
  }
}
