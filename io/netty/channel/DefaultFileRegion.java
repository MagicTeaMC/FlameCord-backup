package io.netty.channel;

import io.netty.util.AbstractReferenceCounted;
import io.netty.util.IllegalReferenceCountException;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;

public class DefaultFileRegion extends AbstractReferenceCounted implements FileRegion {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultFileRegion.class);
  
  private final File f;
  
  private final long position;
  
  private final long count;
  
  private long transferred;
  
  private FileChannel file;
  
  public DefaultFileRegion(FileChannel fileChannel, long position, long count) {
    this.file = (FileChannel)ObjectUtil.checkNotNull(fileChannel, "fileChannel");
    this.position = ObjectUtil.checkPositiveOrZero(position, "position");
    this.count = ObjectUtil.checkPositiveOrZero(count, "count");
    this.f = null;
  }
  
  public DefaultFileRegion(File file, long position, long count) {
    this.f = (File)ObjectUtil.checkNotNull(file, "file");
    this.position = ObjectUtil.checkPositiveOrZero(position, "position");
    this.count = ObjectUtil.checkPositiveOrZero(count, "count");
  }
  
  public boolean isOpen() {
    return (this.file != null);
  }
  
  public void open() throws IOException {
    if (!isOpen() && refCnt() > 0)
      this.file = (new RandomAccessFile(this.f, "r")).getChannel(); 
  }
  
  public long position() {
    return this.position;
  }
  
  public long count() {
    return this.count;
  }
  
  @Deprecated
  public long transfered() {
    return this.transferred;
  }
  
  public long transferred() {
    return this.transferred;
  }
  
  public long transferTo(WritableByteChannel target, long position) throws IOException {
    long count = this.count - position;
    if (count < 0L || position < 0L)
      throw new IllegalArgumentException("position out of range: " + position + " (expected: 0 - " + (this.count - 1L) + ')'); 
    if (count == 0L)
      return 0L; 
    if (refCnt() == 0)
      throw new IllegalReferenceCountException(0); 
    open();
    long written = this.file.transferTo(this.position + position, count, target);
    if (written > 0L) {
      this.transferred += written;
    } else if (written == 0L) {
      validate(this, position);
    } 
    return written;
  }
  
  protected void deallocate() {
    FileChannel file = this.file;
    if (file == null)
      return; 
    this.file = null;
    try {
      file.close();
    } catch (IOException e) {
      logger.warn("Failed to close a file.", e);
    } 
  }
  
  public FileRegion retain() {
    super.retain();
    return this;
  }
  
  public FileRegion retain(int increment) {
    super.retain(increment);
    return this;
  }
  
  public FileRegion touch() {
    return this;
  }
  
  public FileRegion touch(Object hint) {
    return this;
  }
  
  static void validate(DefaultFileRegion region, long position) throws IOException {
    long size = region.file.size();
    long count = region.count - position;
    if (region.position + count + position > size)
      throw new IOException("Underlying file size " + size + " smaller then requested count " + region.count); 
  }
}