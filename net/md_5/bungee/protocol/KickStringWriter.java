package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

@Sharable
public class KickStringWriter extends MessageToByteEncoder<String> {
  protected void encode(ChannelHandlerContext ctx, String msg, ByteBuf out) throws Exception {
    out.writeByte(255);
    out.writeShort(msg.length());
    for (char c : msg.toCharArray())
      out.writeChar(c); 
  }
}
