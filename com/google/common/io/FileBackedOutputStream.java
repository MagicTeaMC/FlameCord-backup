package com.google.common.io;

import com.google.common.annotations.Beta;
import com.google.common.annotations.GwtIncompatible;
import com.google.common.annotations.VisibleForTesting;
import com.google.errorprone.annotations.concurrent.GuardedBy;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@Beta
@GwtIncompatible
public final class FileBackedOutputStream extends OutputStream {
  private final int fileThreshold;
  
  private final boolean resetOnFinalize;
  
  private final ByteSource source;
  
  @CheckForNull
  private final File parentDirectory;
  
  @GuardedBy("this")
  private OutputStream out;
  
  @CheckForNull
  @GuardedBy("this")
  private MemoryOutput memory;
  
  @CheckForNull
  @GuardedBy("this")
  private File file;
  
  private static class MemoryOutput extends ByteArrayOutputStream {
    private MemoryOutput() {}
    
    byte[] getBuffer() {
      return this.buf;
    }
    
    int getCount() {
      return this.count;
    }
  }
  
  @CheckForNull
  @VisibleForTesting
  synchronized File getFile() {
    return this.file;
  }
  
  public FileBackedOutputStream(int fileThreshold) {
    this(fileThreshold, false);
  }
  
  public FileBackedOutputStream(int fileThreshold, boolean resetOnFinalize) {
    this(fileThreshold, resetOnFinalize, null);
  }
  
  private FileBackedOutputStream(int fileThreshold, boolean resetOnFinalize, @CheckForNull File parentDirectory) {
    this.fileThreshold = fileThreshold;
    this.resetOnFinalize = resetOnFinalize;
    this.parentDirectory = parentDirectory;
    this.memory = new MemoryOutput();
    this.out = this.memory;
    if (resetOnFinalize) {
      this.source = new ByteSource() {
          public InputStream openStream() throws IOException {
            return FileBackedOutputStream.this.openInputStream();
          }
          
          protected void finalize() {
            try {
              FileBackedOutputStream.this.reset();
            } catch (Throwable t) {
              t.printStackTrace(System.err);
            } 
          }
        };
    } else {
      this.source = new ByteSource() {
          public InputStream openStream() throws IOException {
            return FileBackedOutputStream.this.openInputStream();
          }
        };
    } 
  }
  
  public ByteSource asByteSource() {
    return this.source;
  }
  
  private synchronized InputStream openInputStream() throws IOException {
    if (this.file != null)
      return new FileInputStream(this.file); 
    Objects.requireNonNull(this.memory);
    return new ByteArrayInputStream(this.memory.getBuffer(), 0, this.memory.getCount());
  }
  
  public synchronized void reset() throws IOException {
    try {
      close();
    } finally {
      if (this.memory == null) {
        this.memory = new MemoryOutput();
      } else {
        this.memory.reset();
      } 
      this.out = this.memory;
      if (this.file != null) {
        File deleteMe = this.file;
        this.file = null;
        if (!deleteMe.delete()) {
          String str = String.valueOf(deleteMe);
          throw new IOException((new StringBuilder(18 + String.valueOf(str).length())).append("Could not delete: ").append(str).toString());
        } 
      } 
    } 
  }
  
  public synchronized void write(int b) throws IOException {
    update(1);
    this.out.write(b);
  }
  
  public synchronized void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }
  
  public synchronized void write(byte[] b, int off, int len) throws IOException {
    update(len);
    this.out.write(b, off, len);
  }
  
  public synchronized void close() throws IOException {
    this.out.close();
  }
  
  public synchronized void flush() throws IOException {
    this.out.flush();
  }
  
  @GuardedBy("this")
  private void update(int len) throws IOException {
    if (this.memory != null && this.memory.getCount() + len > this.fileThreshold) {
      File temp = File.createTempFile("FileBackedOutputStream", null, this.parentDirectory);
      if (this.resetOnFinalize)
        temp.deleteOnExit(); 
      try {
        FileOutputStream transfer = new FileOutputStream(temp);
        transfer.write(this.memory.getBuffer(), 0, this.memory.getCount());
        transfer.flush();
        this.out = transfer;
      } catch (IOException e) {
        temp.delete();
        throw e;
      } 
      this.file = temp;
      this.memory = null;
    } 
  }
}
