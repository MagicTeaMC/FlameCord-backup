package org.codehaus.plexus.util;

import java.util.ArrayList;

public class SweeperPool {
  private static final boolean DEBUG = false;
  
  private transient Sweeper sweeper;
  
  private transient int maxSize;
  
  private transient int minSize;
  
  private int triggerSize;
  
  private ArrayList<Object> pooledObjects;
  
  private boolean shuttingDown = false;
  
  public SweeperPool(int maxSize, int minSize, int intialCapacity, int sweepInterval, int triggerSize) {
    this.maxSize = saneConvert(maxSize);
    this.minSize = saneConvert(minSize);
    this.triggerSize = saneConvert(triggerSize);
    this.pooledObjects = new ArrayList(intialCapacity);
    if (sweepInterval > 0) {
      this.sweeper = new Sweeper(this, sweepInterval);
      this.sweeper.start();
    } 
  }
  
  private int saneConvert(int value) {
    if (value < 0)
      return 0; 
    return value;
  }
  
  public synchronized Object get() {
    if (this.pooledObjects.size() == 0 || this.shuttingDown)
      return null; 
    Object obj = this.pooledObjects.remove(0);
    objectRetrieved(obj);
    return obj;
  }
  
  public synchronized boolean put(Object obj) {
    objectAdded(obj);
    if (obj != null && this.pooledObjects.size() < this.maxSize && !this.shuttingDown) {
      this.pooledObjects.add(obj);
      return true;
    } 
    if (obj != null)
      objectDisposed(obj); 
    return false;
  }
  
  public synchronized int getSize() {
    return this.pooledObjects.size();
  }
  
  public void dispose() {
    this.shuttingDown = true;
    if (this.sweeper != null) {
      this.sweeper.stop();
      try {
        this.sweeper.join();
      } catch (InterruptedException e) {
        System.err.println("Unexpected exception occurred: ");
        e.printStackTrace();
      } 
    } 
    synchronized (this) {
      Object[] objects = this.pooledObjects.toArray();
      for (Object object : objects)
        objectDisposed(object); 
      this.pooledObjects.clear();
    } 
  }
  
  boolean isDisposed() {
    if (!this.shuttingDown)
      return false; 
    if (this.sweeper == null)
      return true; 
    return this.sweeper.hasStopped();
  }
  
  public synchronized void trim() {
    if ((this.triggerSize > 0 && this.pooledObjects.size() >= this.triggerSize) || (this.maxSize > 0 && this.pooledObjects.size() >= this.maxSize))
      while (this.pooledObjects.size() > this.minSize)
        objectDisposed(this.pooledObjects.remove(0));  
  }
  
  public void objectDisposed(Object obj) {}
  
  public void objectAdded(Object obj) {}
  
  public void objectRetrieved(Object obj) {}
  
  private static class Sweeper implements Runnable {
    private final transient SweeperPool pool;
    
    private transient boolean service = false;
    
    private final transient int sweepInterval;
    
    private transient Thread t = null;
    
    public Sweeper(SweeperPool pool, int sweepInterval) {
      this.sweepInterval = sweepInterval;
      this.pool = pool;
    }
    
    public void run() {
      debug("started");
      if (this.sweepInterval > 0)
        synchronized (this) {
          while (this.service) {
            try {
              wait((this.sweepInterval * 1000));
            } catch (InterruptedException interruptedException) {}
            runSweep();
          } 
        }  
      debug("stopped");
    }
    
    public void start() {
      if (!this.service) {
        this.service = true;
        this.t = new Thread(this);
        this.t.setName("Sweeper");
        this.t.start();
      } 
    }
    
    public synchronized void stop() {
      this.service = false;
      notifyAll();
    }
    
    void join() throws InterruptedException {
      this.t.join();
    }
    
    boolean hasStopped() {
      return (!this.service && !this.t.isAlive());
    }
    
    private final void debug(String msg) {}
    
    private void runSweep() {
      debug("runningSweep. time=" + System.currentTimeMillis());
      this.pool.trim();
    }
  }
}
