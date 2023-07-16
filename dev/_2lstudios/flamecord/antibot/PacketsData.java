package dev._2lstudios.flamecord.antibot;

import dev._2lstudios.flamecord.FlameCord;
import dev._2lstudios.flamecord.enums.PacketsViolationReason;
import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class PacketsData {
  private SocketAddress address;
  
  private double packetsVls = 0.0D;
  
  private double packetsVlsSize = 0.0D;
  
  private double packetsVlsRate = 0.0D;
  
  private long lastVlsCalculated = System.currentTimeMillis();
  
  private Map<Integer, Double> vlsByPacketId = new HashMap<>();
  
  private boolean cancelPrinted = false;
  
  public PacketsData(SocketAddress address) {
    this.address = address;
  }
  
  public double simplify(double number) {
    return (int)(number * 1000.0D) / 1000.0D;
  }
  
  public void printKick() {
    if (FlameCord.getInstance().getFlameCordConfiguration().isAntibotPacketsLog())
      System.out
        .println("[FlameCord] [" + this.address + "] was kicked because of too many packets (Total: " + 
          simplify(this.packetsVls) + "vls Size: " + 
          simplify(this.packetsVlsSize) + "vls Rate: " + simplify(this.packetsVlsRate) + "vls)"); 
  }
  
  public void printCancel() {
    if (FlameCord.getInstance().getFlameCordConfiguration().isAntibotPacketsLog() && !this.cancelPrinted) {
      System.out
        .println("[FlameCord] [" + this.address + "] was cancelled because of too many packets (Total: " + 
          simplify(this.packetsVls) + "vls Size: " + 
          simplify(this.packetsVlsSize) + "vls Rate: " + simplify(this.packetsVlsRate) + "vls)");
      this.cancelPrinted = true;
    } 
  }
  
  public void printPackets() {
    if (FlameCord.getInstance().getFlameCordConfiguration().isAntibotPacketsDebug() && 
      simplify(this.packetsVls) > 0.0D) {
      System.out
        .println("[FlameCord] [" + this.address + "] debug is enabled, showing stats (Total: " + 
          simplify(this.packetsVls) + "vls Size: " + 
          simplify(this.packetsVlsSize) + "vls Rate: " + simplify(this.packetsVlsRate) + "vls)");
      for (Map.Entry<Integer, Double> entry : this.vlsByPacketId.entrySet())
        System.out.print((new StringBuilder()).append(entry.getKey()).append("-").append(simplify(((Double)entry.getValue()).doubleValue())).append("vls, ").toString()); 
      System.out.println("");
    } 
  }
  
  public double getPacketsVls() {
    if (System.currentTimeMillis() - this.lastVlsCalculated >= 1000L) {
      printPackets();
      this.cancelPrinted = false;
      this.packetsVls = 0.0D;
      this.packetsVlsSize = 0.0D;
      this.packetsVlsRate = 0.0D;
      this.vlsByPacketId.clear();
      this.lastVlsCalculated = System.currentTimeMillis();
    } 
    return this.packetsVls;
  }
  
  public void addVls(double packetsVls, PacketsViolationReason reason, int packetId) {
    this.packetsVls += packetsVls;
    this.vlsByPacketId.put(Integer.valueOf(packetId), Double.valueOf(((Double)this.vlsByPacketId.getOrDefault(Integer.valueOf(packetId), Double.valueOf(0.0D))).doubleValue() + packetsVls));
    switch (reason) {
      case SIZE:
        this.packetsVlsSize += packetsVls;
        break;
      case RATE:
        this.packetsVlsRate += packetsVls;
        break;
    } 
  }
  
  public double getPacketsVlsSize() {
    return this.packetsVlsSize;
  }
  
  public double getPacketsVlsRate() {
    return this.packetsVlsRate;
  }
}
