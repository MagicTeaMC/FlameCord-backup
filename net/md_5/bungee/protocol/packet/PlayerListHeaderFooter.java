package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PlayerListHeaderFooter extends DefinedPacket {
  private String header;
  
  private String footer;
  
  public void setHeader(String header) {
    this.header = header;
  }
  
  public void setFooter(String footer) {
    this.footer = footer;
  }
  
  public String toString() {
    return "PlayerListHeaderFooter(header=" + getHeader() + ", footer=" + getFooter() + ")";
  }
  
  public PlayerListHeaderFooter() {}
  
  public PlayerListHeaderFooter(String header, String footer) {
    this.header = header;
    this.footer = footer;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PlayerListHeaderFooter))
      return false; 
    PlayerListHeaderFooter other = (PlayerListHeaderFooter)o;
    if (!other.canEqual(this))
      return false; 
    Object this$header = getHeader(), other$header = other.getHeader();
    if ((this$header == null) ? (other$header != null) : !this$header.equals(other$header))
      return false; 
    Object this$footer = getFooter(), other$footer = other.getFooter();
    return !((this$footer == null) ? (other$footer != null) : !this$footer.equals(other$footer));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PlayerListHeaderFooter;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $header = getHeader();
    result = result * 59 + (($header == null) ? 43 : $header.hashCode());
    Object $footer = getFooter();
    return result * 59 + (($footer == null) ? 43 : $footer.hashCode());
  }
  
  public String getHeader() {
    return this.header;
  }
  
  public String getFooter() {
    return this.footer;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.header = readString(buf);
    this.footer = readString(buf);
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    writeString(this.header, buf);
    writeString(this.footer, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
