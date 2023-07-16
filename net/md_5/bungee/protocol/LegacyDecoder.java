package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.util.List;
import net.md_5.bungee.protocol.packet.LegacyHandshake;
import net.md_5.bungee.protocol.packet.LegacyPing;

public class LegacyDecoder extends ByteToMessageDecoder {
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (!ctx.channel().isActive()) {
      in.skipBytes(in.readableBytes());
      return;
    } 
    if (!in.isReadable())
      return; 
    in.markReaderIndex();
    short packetID = in.readUnsignedByte();
    if (packetID == 254) {
      out.add(new PacketWrapper((DefinedPacket)new LegacyPing((in.isReadable() && in.readUnsignedByte() == 1)), Unpooled.EMPTY_BUFFER));
      return;
    } 
    if (packetID == 2 && in.isReadable()) {
      in.skipBytes(in.readableBytes());
      out.add(new PacketWrapper((DefinedPacket)new LegacyHandshake(), Unpooled.EMPTY_BUFFER));
      return;
    } 
    in.resetReaderIndex();
    ctx.pipeline().remove((ChannelHandler)this);
  }
}
