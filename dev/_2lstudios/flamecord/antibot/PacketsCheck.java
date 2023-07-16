package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.configuration.FlameCordConfiguration;
import dev._2lstudios.flamecord.enums.PacketsCheckResult;
import dev._2lstudios.flamecord.enums.PacketsViolationReason;
import dev._2lstudios.flamecord.utils.ProtocolUtil;
import io.netty.buffer.ByteBuf;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class PacketsCheck {
  private Map<String, PacketsData> packetsData = new HashMap<>();
  
  public PacketsData getData(SocketAddress address) {
    String ip = address.toString();
    if (System.currentTimeMillis() % 60000L == 0L)
      this.packetsData.clear(); 
    if (this.packetsData.containsKey(ip))
      return this.packetsData.get(ip); 
    PacketsData data = new PacketsData(address);
    this.packetsData.put(ip, data);
    return data;
  }
  
  public PacketsCheckResult check(SocketAddress socketAddress, ByteBuf byteBuf) {
    FlameCordConfiguration config = FlameCord.getInstance().getFlameCordConfiguration();
    if (!config.isAntibotPacketsEnabled())
      return PacketsCheckResult.NONE; 
    PacketsData packetsData = getData(socketAddress);
    int length = byteBuf.readableBytes();
    int index = byteBuf.readerIndex();
    int packetId = ProtocolUtil.readVarInt(byteBuf);
    byteBuf.readerIndex(index);
    packetsData.addVls(length * config.getAntibotPacketsVlsPerByte(), PacketsViolationReason.SIZE, packetId);
    packetsData.addVls(config.getAntibotPacketsVlsPerPacket(), PacketsViolationReason.RATE, packetId);
    double vls = packetsData.getPacketsVls();
    if (vls >= config.getAntibotPacketsVlsToKick())
      return PacketsCheckResult.KICK; 
    if (vls >= config.getAntibotPacketsVlsToCancel())
      return PacketsCheckResult.CANCEL; 
    return PacketsCheckResult.NONE;
  }
}
