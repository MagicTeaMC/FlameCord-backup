package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;
import java.util.List;

@Sharable
public final class HAProxyMessageEncoder extends MessageToByteEncoder<HAProxyMessage> {
  private static final int V2_VERSION_BITMASK = 32;
  
  static final int UNIX_ADDRESS_BYTES_LENGTH = 108;
  
  static final int TOTAL_UNIX_ADDRESS_BYTES_LENGTH = 216;
  
  public static final HAProxyMessageEncoder INSTANCE = new HAProxyMessageEncoder();
  
  protected void encode(ChannelHandlerContext ctx, HAProxyMessage msg, ByteBuf out) throws Exception {
    switch (msg.protocolVersion()) {
      case AF_IPv4:
        encodeV1(msg, out);
        return;
      case AF_IPv6:
        encodeV2(msg, out);
        return;
    } 
    throw new HAProxyProtocolException("Unsupported version: " + msg.protocolVersion());
  }
  
  private static void encodeV1(HAProxyMessage msg, ByteBuf out) {
    out.writeBytes(HAProxyConstants.TEXT_PREFIX);
    out.writeByte(32);
    out.writeCharSequence(msg.proxiedProtocol().name(), CharsetUtil.US_ASCII);
    out.writeByte(32);
    out.writeCharSequence(msg.sourceAddress(), CharsetUtil.US_ASCII);
    out.writeByte(32);
    out.writeCharSequence(msg.destinationAddress(), CharsetUtil.US_ASCII);
    out.writeByte(32);
    out.writeCharSequence(String.valueOf(msg.sourcePort()), CharsetUtil.US_ASCII);
    out.writeByte(32);
    out.writeCharSequence(String.valueOf(msg.destinationPort()), CharsetUtil.US_ASCII);
    out.writeByte(13);
    out.writeByte(10);
  }
  
  private static void encodeV2(HAProxyMessage msg, ByteBuf out) {
    byte[] srcAddrBytes, dstAddrBytes;
    int srcAddrBytesWritten, dstAddrBytesWritten;
    out.writeBytes(HAProxyConstants.BINARY_PREFIX);
    out.writeByte(0x20 | msg.command().byteValue());
    out.writeByte(msg.proxiedProtocol().byteValue());
    switch (msg.proxiedProtocol().addressFamily()) {
      case AF_IPv4:
      case AF_IPv6:
        srcAddrBytes = NetUtil.createByteArrayFromIpAddressString(msg.sourceAddress());
        dstAddrBytes = NetUtil.createByteArrayFromIpAddressString(msg.destinationAddress());
        out.writeShort(srcAddrBytes.length + dstAddrBytes.length + 4 + msg.tlvNumBytes());
        out.writeBytes(srcAddrBytes);
        out.writeBytes(dstAddrBytes);
        out.writeShort(msg.sourcePort());
        out.writeShort(msg.destinationPort());
        encodeTlvs(msg.tlvs(), out);
        return;
      case AF_UNIX:
        out.writeShort(216 + msg.tlvNumBytes());
        srcAddrBytesWritten = out.writeCharSequence(msg.sourceAddress(), CharsetUtil.US_ASCII);
        out.writeZero(108 - srcAddrBytesWritten);
        dstAddrBytesWritten = out.writeCharSequence(msg.destinationAddress(), CharsetUtil.US_ASCII);
        out.writeZero(108 - dstAddrBytesWritten);
        encodeTlvs(msg.tlvs(), out);
        return;
      case AF_UNSPEC:
        out.writeShort(0);
        return;
    } 
    throw new HAProxyProtocolException("unexpected addrFamily");
  }
  
  private static void encodeTlv(HAProxyTLV haProxyTLV, ByteBuf out) {
    if (haProxyTLV instanceof HAProxySSLTLV) {
      HAProxySSLTLV ssltlv = (HAProxySSLTLV)haProxyTLV;
      out.writeByte(haProxyTLV.typeByteValue());
      out.writeShort(ssltlv.contentNumBytes());
      out.writeByte(ssltlv.client());
      out.writeInt(ssltlv.verify());
      encodeTlvs(ssltlv.encapsulatedTLVs(), out);
    } else {
      out.writeByte(haProxyTLV.typeByteValue());
      ByteBuf value = haProxyTLV.content();
      int readableBytes = value.readableBytes();
      out.writeShort(readableBytes);
      out.writeBytes(value.readSlice(readableBytes));
    } 
  }
  
  private static void encodeTlvs(List<HAProxyTLV> haProxyTLVs, ByteBuf out) {
    for (int i = 0; i < haProxyTLVs.size(); i++)
      encodeTlv(haProxyTLVs.get(i), out); 
  }
}
