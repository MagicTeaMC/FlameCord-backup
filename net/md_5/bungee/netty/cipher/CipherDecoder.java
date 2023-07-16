package net.md_5.bungee.netty.cipher;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import net.md_5.bungee.jni.cipher.BungeeCipher;

public class CipherDecoder extends MessageToMessageDecoder<ByteBuf> {
  private final BungeeCipher cipher;
  
  public CipherDecoder(BungeeCipher cipher) {
    this.cipher = cipher;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
    out.add(this.cipher.cipher(ctx, msg));
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    this.cipher.free();
  }
}
