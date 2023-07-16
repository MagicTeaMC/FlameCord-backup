package net.md_5.bungee.jni.zlib;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import java.util.zip.DataFormatException;

public class NativeZlib implements BungeeZlib {
  private final NativeCompressImpl nativeCompress = new NativeCompressImpl();
  
  private boolean compress;
  
  private long ctx;
  
  public NativeCompressImpl getNativeCompress() {
    return this.nativeCompress;
  }
  
  public void init(boolean compress, int level) {
    free();
    this.compress = compress;
    this.ctx = this.nativeCompress.init(compress, level);
  }
  
  public void free() {
    if (this.ctx != 0L) {
      this.nativeCompress.end(this.ctx, this.compress);
      this.ctx = 0L;
    } 
    this.nativeCompress.consumed = 0;
    this.nativeCompress.finished = false;
  }
  
  public void process(ByteBuf in, ByteBuf out) throws DataFormatException {
    in.memoryAddress();
    out.memoryAddress();
    Preconditions.checkState((this.ctx != 0L), "Invalid pointer to compress!");
    while (!this.nativeCompress.finished && (this.compress || in.isReadable())) {
      out.ensureWritable(8192);
      int processed = this.nativeCompress.process(this.ctx, in.memoryAddress() + in.readerIndex(), in.readableBytes(), out.memoryAddress() + out.writerIndex(), out.writableBytes(), this.compress);
      in.readerIndex(in.readerIndex() + this.nativeCompress.consumed);
      out.writerIndex(out.writerIndex() + processed);
    } 
    this.nativeCompress.reset(this.ctx, this.compress);
    this.nativeCompress.consumed = 0;
    this.nativeCompress.finished = false;
  }
}
