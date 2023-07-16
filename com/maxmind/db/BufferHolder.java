package com.maxmind.db;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

final class BufferHolder {
  private final ByteBuffer buffer;
  
  BufferHolder(File database, Reader.FileMode mode) throws IOException {
    RandomAccessFile file = new RandomAccessFile(database, "r");
    try {
      FileChannel channel = file.getChannel();
      try {
        if (mode == Reader.FileMode.MEMORY) {
          ByteBuffer buf = ByteBuffer.wrap(new byte[(int)channel.size()]);
          if (channel.read(buf) != buf.capacity())
            throw new IOException("Unable to read " + database
                .getName() + " into memory. Unexpected end of stream."); 
          this.buffer = buf.asReadOnlyBuffer();
        } else {
          this.buffer = channel.map(FileChannel.MapMode.READ_ONLY, 0L, channel.size()).asReadOnlyBuffer();
        } 
        if (channel != null)
          channel.close(); 
      } catch (Throwable throwable) {
        if (channel != null)
          try {
            channel.close();
          } catch (Throwable throwable1) {
            throwable.addSuppressed(throwable1);
          }  
        throw throwable;
      } 
      file.close();
    } catch (Throwable throwable) {
      try {
        file.close();
      } catch (Throwable throwable1) {
        throwable.addSuppressed(throwable1);
      } 
      throw throwable;
    } 
  }
  
  BufferHolder(InputStream stream) throws IOException {
    if (null == stream)
      throw new NullPointerException("Unable to use a NULL InputStream"); 
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] bytes = new byte[16384];
    int br;
    while (-1 != (br = stream.read(bytes)))
      baos.write(bytes, 0, br); 
    this.buffer = ByteBuffer.wrap(baos.toByteArray()).asReadOnlyBuffer();
  }
  
  ByteBuffer get() {
    return this.buffer.duplicate();
  }
}
