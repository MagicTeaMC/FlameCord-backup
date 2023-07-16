package net.md_5.bungee.jni.cipher;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import javax.crypto.SecretKey;

public class NativeCipher implements BungeeCipher {
  private final NativeCipherImpl nativeCipher = new NativeCipherImpl();
  
  private long ctx;
  
  public NativeCipherImpl getNativeCipher() {
    return this.nativeCipher;
  }
  
  public void init(boolean forEncryption, SecretKey key) throws GeneralSecurityException {
    Preconditions.checkArgument(((key.getEncoded()).length == 16), "Invalid key size");
    free();
    this.ctx = this.nativeCipher.init(forEncryption, key.getEncoded());
  }
  
  public void free() {
    if (this.ctx != 0L) {
      this.nativeCipher.free(this.ctx);
      this.ctx = 0L;
    } 
  }
  
  public void cipher(ByteBuf in, ByteBuf out) throws GeneralSecurityException {
    in.memoryAddress();
    out.memoryAddress();
    Preconditions.checkState((this.ctx != 0L), "Invalid pointer to AES key!");
    int length = in.readableBytes();
    if (length <= 0)
      return; 
    out.ensureWritable(length);
    this.nativeCipher.cipher(this.ctx, in.memoryAddress() + in.readerIndex(), out.memoryAddress() + out.writerIndex(), length);
    in.readerIndex(in.writerIndex());
    out.writerIndex(out.writerIndex() + length);
  }
  
  public ByteBuf cipher(ChannelHandlerContext ctx, ByteBuf in) throws GeneralSecurityException {
    int readableBytes = in.readableBytes();
    ByteBuf heapOut = ctx.alloc().directBuffer(readableBytes);
    cipher(in, heapOut);
    return heapOut;
  }
}
