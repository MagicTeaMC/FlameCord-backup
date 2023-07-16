package net.md_5.bungee.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

public class MinecraftEncoder extends MessageToByteEncoder<DefinedPacket> {
  private Protocol protocol;
  
  private boolean server;
  
  private int protocolVersion;
  
  public MinecraftEncoder(Protocol protocol, boolean server, int protocolVersion) {
    this.protocol = protocol;
    this.server = server;
    this.protocolVersion = protocolVersion;
  }
  
  public void setProtocol(Protocol protocol) {
    this.protocol = protocol;
  }
  
  public void setProtocolVersion(int protocolVersion) {
    this.protocolVersion = protocolVersion;
  }
  
  protected void encode(ChannelHandlerContext ctx, DefinedPacket msg, ByteBuf out) throws Exception {
    Protocol.DirectionData prot = this.server ? this.protocol.TO_CLIENT : this.protocol.TO_SERVER;
    DefinedPacket.writeVarInt(prot.getId((Class)msg.getClass(), this.protocolVersion), out);
    msg.write(out, prot.getDirection(), this.protocolVersion);
  }
}
