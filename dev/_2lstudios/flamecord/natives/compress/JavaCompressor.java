package dev._2lstudios.flamecord.natives.compress;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class JavaCompressor implements Compressor {
  private final byte[] buffer = new byte[8192];
  
  private final Deflater deflater;
  
  private final Inflater inflater;
  
  private boolean disposed = false;
  
  public JavaCompressor(int level) {
    this.deflater = new Deflater(level);
    this.inflater = new Inflater();
  }
  
  public void inflate(ByteBuf source, ByteBuf destination, int uncompressedSize) throws DataFormatException {
    ensureNotDisposed();
    Preconditions.checkArgument((source.nioBufferCount() == 1), "source has multiple backing buffers");
    Preconditions.checkArgument((destination.nioBufferCount() == 1), "destination has multiple backing buffers");
    try {
      byte[] inData = new byte[source.readableBytes()];
      source.readBytes(inData);
      this.inflater.setInput(inData);
      while (!this.inflater.finished() && this.inflater.getTotalIn() < inData.length) {
        int count = this.inflater.inflate(this.buffer);
        destination.writeBytes(this.buffer, 0, count);
      } 
    } finally {
      this.inflater.reset();
    } 
  }
  
  public void deflate(ByteBuf source, ByteBuf destination) throws DataFormatException {
    ensureNotDisposed();
    Preconditions.checkArgument((source.nioBufferCount() == 1), "source has multiple backing buffers");
    Preconditions.checkArgument((destination.nioBufferCount() == 1), "destination has multiple backing buffers");
    try {
      byte[] inData = new byte[source.readableBytes()];
      source.readBytes(inData);
      this.deflater.setInput(inData);
      this.deflater.finish();
      while (!this.deflater.finished()) {
        int count = this.deflater.deflate(this.buffer);
        destination.writeBytes(this.buffer, 0, count);
      } 
    } finally {
      this.deflater.reset();
    } 
  }
  
  public void close() {
    this.disposed = true;
    this.deflater.end();
    this.inflater.end();
  }
  
  private void ensureNotDisposed() {
    Preconditions.checkState(!this.disposed, "Object already disposed");
  }
  
  public boolean isNeedDirectBuffer() {
    return false;
  }
}
