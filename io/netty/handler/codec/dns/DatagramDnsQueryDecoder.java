package io.netty.handler.codec.dns;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.net.InetSocketAddress;
import java.util.List;

@Sharable
public class DatagramDnsQueryDecoder extends MessageToMessageDecoder<DatagramPacket> {
  private final DnsRecordDecoder recordDecoder;
  
  public DatagramDnsQueryDecoder() {
    this(DnsRecordDecoder.DEFAULT);
  }
  
  public DatagramDnsQueryDecoder(DnsRecordDecoder recordDecoder) {
    this.recordDecoder = (DnsRecordDecoder)ObjectUtil.checkNotNull(recordDecoder, "recordDecoder");
  }
  
  protected void decode(ChannelHandlerContext ctx, final DatagramPacket packet, List<Object> out) throws Exception {
    DnsQuery query = DnsMessageUtil.decodeDnsQuery(this.recordDecoder, (ByteBuf)packet.content(), new DnsMessageUtil.DnsQueryFactory() {
          public DnsQuery newQuery(int id, DnsOpCode dnsOpCode) {
            return new DatagramDnsQuery((InetSocketAddress)packet.sender(), (InetSocketAddress)packet.recipient(), id, dnsOpCode);
          }
        });
    out.add(query);
  }
}
