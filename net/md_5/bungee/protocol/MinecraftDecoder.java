package net.md_5.bungee.protocol;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.antibot.PacketsCheck;
import dev._2lstudios.flamecord.enums.PacketsCheckResult;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;

public class MinecraftDecoder extends MessageToMessageDecoder<ByteBuf> {
  private Protocol protocol;
  
  private final boolean server;
  
  private int protocolVersion;
  
  public MinecraftDecoder(Protocol protocol, boolean server, int protocolVersion, boolean supportsForge) {
    this.protocol = protocol;
    this.server = server;
    this.protocolVersion = protocolVersion;
    this.supportsForge = supportsForge;
  }
  
  public void setProtocol(Protocol protocol) {
    this.protocol = protocol;
  }
  
  public void setProtocolVersion(int protocolVersion) {
    this.protocolVersion = protocolVersion;
  }
  
  private boolean supportsForge = false;
  
  public void setSupportsForge(boolean supportsForge) {
    this.supportsForge = supportsForge;
  }
  
  public MinecraftDecoder(Protocol protocol, boolean server, int protocolVersion) {
    this.protocol = protocol;
    this.server = server;
    this.protocolVersion = protocolVersion;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (!ctx.channel().isActive() || !in.isReadable())
      return; 
    Protocol.DirectionData prot = this.server ? this.protocol.TO_SERVER : this.protocol.TO_CLIENT;
    if (prot == this.protocol.TO_SERVER) {
      int readableBytes = in.readableBytes();
      int capacity = in.capacity();
      if (readableBytes > 2097152)
        throw new FastDecoderException("Error decoding packet with too many readableBytes: " + readableBytes); 
      if (capacity > 2097152)
        throw new FastDecoderException("Error decoding packet with too big capacity: " + capacity); 
    } 
    if (prot == this.protocol.TO_SERVER) {
      PacketsCheck packetsCheck = FlameCord.getInstance().getCheckManager().getPacketsCheck();
      PacketsCheckResult result = packetsCheck.check(ctx.channel().remoteAddress(), in);
      switch (result) {
        case KICK:
          packetsCheck.getData(ctx.channel().remoteAddress()).printKick();
          in.skipBytes(in.readableBytes());
          ctx.close();
          return;
        case CANCEL:
          packetsCheck.getData(ctx.channel().remoteAddress()).printCancel();
          in.skipBytes(in.readableBytes());
          return;
      } 
    } 
    ByteBuf slice = in.duplicate();
    Object<?> packetTypeInfo = null;
    try {
      slice = in.duplicate();
      if (in.readableBytes() == 0 && !this.server)
        return; 
      int packetId = DefinedPacket.readVarInt(in);
      packetTypeInfo = (Object<?>)Integer.valueOf(packetId);
      if (packetId < 0 || packetId > 255)
        throw new FastDecoderException("[" + ctx.channel().remoteAddress() + "] <-> MinecraftDecoder received invalid packet #1, id " + packetId); 
      DefinedPacket packet = prot.createPacket(packetId, this.protocolVersion, this.supportsForge);
      if (packet != null) {
        packetTypeInfo = (Object<?>)packet.getClass();
        doLengthSanityChecks(in, packet, prot.getDirection(), packetId);
        packet.read(in, prot.getDirection(), this.protocolVersion);
        if (in.isReadable()) {
          if (!DEBUG)
            throw PACKET_NOT_READ_TO_END; 
          throw new BadPacketException("Packet " + this.protocol + ":" + prot.getDirection() + "/" + packetId + " (" + packet.getClass().getSimpleName() + ") larger than expected, extra bytes: " + in.readableBytes());
        } 
      } else {
        in.skipBytes(in.readableBytes());
      } 
      out.add(new PacketWrapper(packet, slice.retain()));
      slice = null;
    } catch (BadPacketException|IndexOutOfBoundsException e) {
      String packetTypeStr;
      if (!DEBUG)
        throw e; 
      if (packetTypeInfo instanceof Integer) {
        packetTypeStr = "id " + Integer.toHexString(((Integer)packetTypeInfo).intValue());
      } else if (packetTypeInfo instanceof Class) {
        packetTypeStr = "class " + ((Class)packetTypeInfo).getSimpleName();
      } else {
        packetTypeStr = "unknown";
      } 
      throw new FastDecoderException("Error decoding packet " + packetTypeStr + " with contents:\n" + ByteBufUtil.prettyHexDump(slice), e);
    } finally {
      if (slice != null)
        slice.release(); 
    } 
  }
  
  public static final boolean DEBUG = Boolean.getBoolean("waterfall.packet-decode-logging");
  
  private static final CorruptedFrameException PACKET_LENGTH_OVERSIZED = new CorruptedFrameException("A packet could not be decoded because it was too large. For more information, launch Waterfall with -Dwaterfall.packet-decode-logging=true");
  
  private static final CorruptedFrameException PACKET_LENGTH_UNDERSIZED = new CorruptedFrameException("A packet could not be decoded because it was smaller than allowed. For more information, launch Waterfall with -Dwaterfall.packet-decode-logging=true");
  
  private static final BadPacketException PACKET_NOT_READ_TO_END = new BadPacketException("Couldn't read all bytes from a packet. For more information, launch Waterfall with -Dwaterfall.packet-decode-logging=true");
  
  private void doLengthSanityChecks(ByteBuf buf, DefinedPacket packet, ProtocolConstants.Direction direction, int packetId) throws Exception {
    int expectedMinLen = packet.expectedMinLength(buf, direction, this.protocolVersion);
    int expectedMaxLen = packet.expectedMaxLength(buf, direction, this.protocolVersion);
    if (expectedMaxLen != -1 && buf.readableBytes() > expectedMaxLen)
      throw handleOverflow(packet, expectedMaxLen, buf.readableBytes(), packetId); 
    if (buf.readableBytes() < expectedMinLen)
      throw handleUnderflow(packet, expectedMaxLen, buf.readableBytes(), packetId); 
  }
  
  private Exception handleOverflow(DefinedPacket packet, int expected, int actual, int packetId) {
    if (DEBUG)
      throw new CorruptedFrameException("Packet " + packet.getClass() + " " + packetId + " Protocol " + this.protocolVersion + " was too big (expected " + expected + " bytes, got " + actual + " bytes)"); 
    return (Exception)PACKET_LENGTH_OVERSIZED;
  }
  
  private Exception handleUnderflow(DefinedPacket packet, int expected, int actual, int packetId) {
    if (DEBUG)
      throw new CorruptedFrameException("Packet " + packet.getClass() + " " + packetId + " Protocol " + this.protocolVersion + " was too small (expected " + expected + " bytes, got " + actual + " bytes)"); 
    return (Exception)PACKET_LENGTH_UNDERSIZED;
  }
}
