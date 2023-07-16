package net.md_5.bungee.jni.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.IvParameterSpec;

public class JavaCipher implements BungeeCipher {
  private final Cipher cipher;
  
  private static final ThreadLocal<byte[]> heapInLocal = new EmptyByteThreadLocal();
  
  private static final ThreadLocal<byte[]> heapOutLocal = new EmptyByteThreadLocal();
  
  private static class EmptyByteThreadLocal extends ThreadLocal<byte[]> {
    private EmptyByteThreadLocal() {}
    
    protected byte[] initialValue() {
      return new byte[0];
    }
  }
  
  public JavaCipher() {
    try {
      this.cipher = Cipher.getInstance("AES/CFB8/NoPadding");
    } catch (GeneralSecurityException ex) {
      throw new RuntimeException(ex);
    } 
  }
  
  public void init(boolean forEncryption, SecretKey key) throws GeneralSecurityException {
    int mode = forEncryption ? 1 : 2;
    this.cipher.init(mode, key, new IvParameterSpec(key.getEncoded()));
  }
  
  public void cipher(ByteBuf in, ByteBuf out) throws ShortBufferException {
    int readableBytes = in.readableBytes();
    byte[] heapIn = bufToByte(in);
    byte[] heapOut = heapOutLocal.get();
    int outputSize = this.cipher.getOutputSize(readableBytes);
    if (heapOut.length < outputSize) {
      heapOut = new byte[outputSize];
      heapOutLocal.set(heapOut);
    } 
    out.writeBytes(heapOut, 0, this.cipher.update(heapIn, 0, readableBytes, heapOut));
  }
  
  public ByteBuf cipher(ChannelHandlerContext ctx, ByteBuf in) throws ShortBufferException {
    int readableBytes = in.readableBytes();
    byte[] heapIn = bufToByte(in);
    ByteBuf heapOut = ctx.alloc().heapBuffer(this.cipher.getOutputSize(readableBytes));
    heapOut.writerIndex(this.cipher.update(heapIn, 0, readableBytes, heapOut.array(), heapOut.arrayOffset()));
    return heapOut;
  }
  
  public void free() {}
  
  private byte[] bufToByte(ByteBuf in) {
    byte[] heapIn = heapInLocal.get();
    int readableBytes = in.readableBytes();
    if (heapIn.length < readableBytes) {
      heapIn = new byte[readableBytes];
      heapInLocal.set(heapIn);
    } 
    in.readBytes(heapIn, 0, readableBytes);
    return heapIn;
  }
}
