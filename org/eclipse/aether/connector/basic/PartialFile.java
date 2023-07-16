package org.eclipse.aether.connector.basic;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.Channel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PartialFile implements Closeable {
  static final String EXT_PART = ".part";
  
  static final String EXT_LOCK = ".lock";
  
  private final File partFile;
  
  private final LockFile lockFile;
  
  private final long threshold;
  
  static interface RemoteAccessChecker {
    void checkRemoteAccess() throws Exception;
  }
  
  static class LockFile {
    private final File lockFile;
    
    private final FileLock lock;
    
    private final AtomicBoolean concurrent;
    
    LockFile(File partFile, int requestTimeout, PartialFile.RemoteAccessChecker checker) throws Exception {
      this.lockFile = new File(partFile.getPath() + ".lock");
      this.concurrent = new AtomicBoolean(false);
      this.lock = lock(this.lockFile, partFile, requestTimeout, checker, this.concurrent);
    }
    
    private static FileLock lock(File lockFile, File partFile, int requestTimeout, PartialFile.RemoteAccessChecker checker, AtomicBoolean concurrent) throws Exception {
      boolean interrupted = false;
      try {
        long lastLength = -1L, lastTime = 0L;
        while (true) {
          FileLock lock = tryLock(lockFile);
          if (lock != null)
            return lock; 
          long currentLength = partFile.length();
          long currentTime = System.currentTimeMillis();
          if (currentLength != lastLength) {
            if (lastLength < 0L) {
              concurrent.set(true);
              checker.checkRemoteAccess();
              PartialFile.LOGGER.debug("Concurrent download of {} in progress, awaiting completion", partFile);
            } 
            lastLength = currentLength;
            lastTime = currentTime;
          } else if (requestTimeout > 0 && currentTime - lastTime > Math.max(requestTimeout, 3000)) {
            throw new IOException("Timeout while waiting for concurrent download of " + partFile + " to progress");
          } 
          try {
            Thread.sleep(100L);
          } catch (InterruptedException e) {
            interrupted = true;
          } 
        } 
      } finally {
        if (interrupted)
          Thread.currentThread().interrupt(); 
      } 
    }
    
    private static FileLock tryLock(File lockFile) throws IOException {
      RandomAccessFile raf = null;
      FileLock lock = null;
      try {
        raf = new RandomAccessFile(lockFile, "rw");
        lock = raf.getChannel().tryLock(0L, 1L, false);
        if (lock == null) {
          raf.close();
          raf = null;
        } 
      } catch (OverlappingFileLockException e) {
        close(raf);
        raf = null;
        lock = null;
      } catch (RuntimeException|IOException e) {
        close(raf);
        raf = null;
        if (!lockFile.delete())
          lockFile.deleteOnExit(); 
        throw e;
      } finally {
        try {
          if (lock == null && raf != null)
            raf.close(); 
        } catch (IOException iOException) {}
      } 
      return lock;
    }
    
    private static void close(Closeable file) {
      try {
        if (file != null)
          file.close(); 
      } catch (IOException iOException) {}
    }
    
    public boolean isConcurrent() {
      return this.concurrent.get();
    }
    
    public void close() throws IOException {
      Channel channel = null;
      try {
        channel = this.lock.channel();
        this.lock.release();
        channel.close();
        channel = null;
      } finally {
        try {
          if (channel != null)
            channel.close(); 
        } catch (IOException iOException) {
        
        } finally {
          if (!this.lockFile.delete())
            this.lockFile.deleteOnExit(); 
        } 
      } 
    }
    
    public String toString() {
      return this.lockFile + " - " + this.lock.isValid();
    }
  }
  
  static class Factory {
    private final boolean resume;
    
    private final long resumeThreshold;
    
    private final int requestTimeout;
    
    private static final Logger LOGGER = LoggerFactory.getLogger(Factory.class);
    
    Factory(boolean resume, long resumeThreshold, int requestTimeout) {
      this.resume = resume;
      this.resumeThreshold = resumeThreshold;
      this.requestTimeout = requestTimeout;
    }
    
    public PartialFile newInstance(File dstFile, PartialFile.RemoteAccessChecker checker) throws Exception {
      if (this.resume) {
        File partFile = new File(dstFile.getPath() + ".part");
        long reqTimestamp = System.currentTimeMillis();
        PartialFile.LockFile lockFile = new PartialFile.LockFile(partFile, this.requestTimeout, checker);
        if (lockFile.isConcurrent() && dstFile.lastModified() >= reqTimestamp - 100L) {
          lockFile.close();
          return null;
        } 
        try {
          if (!partFile.createNewFile() && !partFile.isFile())
            throw new IOException(partFile.exists() ? "Path exists but is not a file" : "Unknown error"); 
          return new PartialFile(partFile, lockFile, this.resumeThreshold);
        } catch (IOException e) {
          lockFile.close();
          LOGGER.debug("Cannot create resumable file {}", partFile.getAbsolutePath(), e);
        } 
      } 
      File tempFile = File.createTempFile(dstFile.getName() + '-' + UUID.randomUUID().toString().replace("-", ""), ".tmp", dstFile
          .getParentFile());
      return new PartialFile(tempFile);
    }
  }
  
  private static final Logger LOGGER = LoggerFactory.getLogger(PartialFile.class);
  
  private PartialFile(File partFile) {
    this(partFile, null, 0L);
  }
  
  private PartialFile(File partFile, LockFile lockFile, long threshold) {
    this.partFile = partFile;
    this.lockFile = lockFile;
    this.threshold = threshold;
  }
  
  public File getFile() {
    return this.partFile;
  }
  
  public boolean isResume() {
    return (this.lockFile != null && this.partFile.length() >= this.threshold);
  }
  
  public void close() throws IOException {
    if (this.partFile.exists() && !isResume())
      if (!this.partFile.delete() && this.partFile.exists())
        LOGGER.debug("Could not delete temporary file {}", this.partFile);  
    if (this.lockFile != null)
      this.lockFile.close(); 
  }
  
  public String toString() {
    return String.valueOf(getFile());
  }
}
