package dev._2lstudios.flamecord.natives.compress;

import com.google.common.base.Preconditions;
import com.velocitypowered.natives.compression.NativeZlibDeflate;
import com.velocitypowered.natives.compression.NativeZlibInflate;
import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;

public class LibdeflateCompressor implements Compressor {
  private final long inflateCtx;
  
  private final long deflateCtx;
  
  private boolean disposed = false;
  
  public LibdeflateCompressor(int level) {
    int correctedLevel = (level == -1) ? 6 : level;
    if (correctedLevel > 12 || correctedLevel < 1)
      throw new IllegalArgumentException("Invalid compression level " + level); 
    this.inflateCtx = NativeZlibInflate.init();
    this.deflateCtx = NativeZlibDeflate.init(correctedLevel);
  }
  
  public void inflate(ByteBuf source, ByteBuf destination, int uncompressedSize) throws DataFormatException {
    ensureNotDisposed();
    destination.ensureWritable(uncompressedSize);
    long sourceAddress = source.memoryAddress() + source.readerIndex();
    long destinationAddress = destination.memoryAddress() + destination.writerIndex();
    NativeZlibInflate.process(this.inflateCtx, sourceAddress, source.readableBytes(), destinationAddress, uncompressedSize);
    destination.writerIndex(destination.writerIndex() + uncompressedSize);
  }
  
  public void deflate(ByteBuf source, ByteBuf destination) throws DataFormatException {
    ensureNotDisposed();
    while (true) {
      long sourceAddress = source.memoryAddress() + source.readerIndex();
      long destinationAddress = destination.memoryAddress() + destination.writerIndex();
      int produced = NativeZlibDeflate.process(this.deflateCtx, sourceAddress, source.readableBytes(), destinationAddress, destination
          .writableBytes());
      if (produced > 0) {
        destination.writerIndex(destination.writerIndex() + produced);
        break;
      } 
      if (produced == 0) {
        destination.capacity(destination.capacity() * 2);
        continue;
      } 
      throw new DataFormatException("libdeflate returned unknown code " + produced);
    } 
  }
  
  private void ensureNotDisposed() {
    Preconditions.checkState(!this.disposed, "Object already disposed");
  }
  
  public void close() {
    if (!this.disposed) {
      NativeZlibInflate.free(this.inflateCtx);
      NativeZlibDeflate.free(this.deflateCtx);
    } 
    this.disposed = true;
  }
  
  public boolean isNeedDirectBuffer() {
    return true;
  }
}
