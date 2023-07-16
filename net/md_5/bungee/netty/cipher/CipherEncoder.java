package net.md_5.bungee.netty.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.md_5.bungee.jni.cipher.BungeeCipher;

public class CipherEncoder extends MessageToByteEncoder<ByteBuf> {
  private final BungeeCipher cipher;
  
  public CipherEncoder(BungeeCipher cipher) {
    this.cipher = cipher;
  }
  
  protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
    this.cipher.cipher(in, out);
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    this.cipher.free();
  }
}
