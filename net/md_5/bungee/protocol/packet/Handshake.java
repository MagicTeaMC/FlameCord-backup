package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Handshake extends DefinedPacket {
  private int protocolVersion;
  
  private String host;
  
  private int port;
  
  private int requestedProtocol;
  
  public void setProtocolVersion(int protocolVersion) {
    this.protocolVersion = protocolVersion;
  }
  
  public void setHost(String host) {
    this.host = host;
  }
  
  public void setPort(int port) {
    this.port = port;
  }
  
  public void setRequestedProtocol(int requestedProtocol) {
    this.requestedProtocol = requestedProtocol;
  }
  
  public String toString() {
    return "Handshake(protocolVersion=" + getProtocolVersion() + ", host=" + getHost() + ", port=" + getPort() + ", requestedProtocol=" + getRequestedProtocol() + ")";
  }
  
  public Handshake() {}
  
  public Handshake(int protocolVersion, String host, int port, int requestedProtocol) {
    this.protocolVersion = protocolVersion;
    this.host = host;
    this.port = port;
    this.requestedProtocol = requestedProtocol;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Handshake))
      return false; 
    Handshake other = (Handshake)o;
    if (!other.canEqual(this))
      return false; 
    if (getProtocolVersion() != other.getProtocolVersion())
      return false; 
    if (getPort() != other.getPort())
      return false; 
    if (getRequestedProtocol() != other.getRequestedProtocol())
      return false; 
    Object this$host = getHost(), other$host = other.getHost();
    return !((this$host == null) ? (other$host != null) : !this$host.equals(other$host));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Handshake;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getProtocolVersion();
    result = result * 59 + getPort();
    result = result * 59 + getRequestedProtocol();
    Object $host = getHost();
    return result * 59 + (($host == null) ? 43 : $host.hashCode());
  }
  
  public int getProtocolVersion() {
    return this.protocolVersion;
  }
  
  public String getHost() {
    return this.host;
  }
  
  public int getPort() {
    return this.port;
  }
  
  public int getRequestedProtocol() {
    return this.requestedProtocol;
  }
  
  public void read(ByteBuf buf) {
    this.protocolVersion = readVarInt(buf);
    this.host = readString(buf, 255);
    this.port = buf.readUnsignedShort();
    this.requestedProtocol = readVarInt(buf);
  }
  
  public void write(ByteBuf buf) {
    writeVarInt(this.protocolVersion, buf);
    writeString(this.host, buf);
    buf.writeShort(this.port);
    writeVarInt(this.requestedProtocol, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public int expectedMaxLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    return 1292;
  }
  
  public int expectedMinLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    return 5;
  }
}
