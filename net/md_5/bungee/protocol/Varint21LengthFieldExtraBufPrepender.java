package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import java.util.List;

@Sharable
public class Varint21LengthFieldExtraBufPrepender extends MessageToMessageEncoder<ByteBuf> {
  protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
    int bodyLen = msg.readableBytes();
    ByteBuf lenBuf = ctx.alloc().ioBuffer(Varint21LengthFieldPrepender.varintSize(bodyLen));
    DefinedPacket.writeVarInt(bodyLen, lenBuf);
    out.add(lenBuf);
    out.add(msg.retain());
  }
}
