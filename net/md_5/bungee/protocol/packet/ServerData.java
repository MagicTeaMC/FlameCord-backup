package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ServerData extends DefinedPacket {
  private String motd;
  
  private Object icon;
  
  private boolean preview;
  
  private boolean enforceSecure;
  
  public void setMotd(String motd) {
    this.motd = motd;
  }
  
  public void setIcon(Object icon) {
    this.icon = icon;
  }
  
  public void setPreview(boolean preview) {
    this.preview = preview;
  }
  
  public void setEnforceSecure(boolean enforceSecure) {
    this.enforceSecure = enforceSecure;
  }
  
  public String toString() {
    return "ServerData(motd=" + getMotd() + ", icon=" + getIcon() + ", preview=" + isPreview() + ", enforceSecure=" + isEnforceSecure() + ")";
  }
  
  public ServerData() {}
  
  public ServerData(String motd, Object icon, boolean preview, boolean enforceSecure) {
    this.motd = motd;
    this.icon = icon;
    this.preview = preview;
    this.enforceSecure = enforceSecure;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ServerData))
      return false; 
    ServerData other = (ServerData)o;
    if (!other.canEqual(this))
      return false; 
    if (isPreview() != other.isPreview())
      return false; 
    if (isEnforceSecure() != other.isEnforceSecure())
      return false; 
    Object this$motd = getMotd(), other$motd = other.getMotd();
    if ((this$motd == null) ? (other$motd != null) : !this$motd.equals(other$motd))
      return false; 
    Object this$icon = getIcon(), other$icon = other.getIcon();
    return !((this$icon == null) ? (other$icon != null) : !this$icon.equals(other$icon));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ServerData;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isPreview() ? 79 : 97);
    result = result * 59 + (isEnforceSecure() ? 79 : 97);
    Object $motd = getMotd();
    result = result * 59 + (($motd == null) ? 43 : $motd.hashCode());
    Object $icon = getIcon();
    return result * 59 + (($icon == null) ? 43 : $icon.hashCode());
  }
  
  public String getMotd() {
    return this.motd;
  }
  
  public Object getIcon() {
    return this.icon;
  }
  
  public boolean isPreview() {
    return this.preview;
  }
  
  public boolean isEnforceSecure() {
    return this.enforceSecure;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 762 || buf.readBoolean())
      this.motd = readString(buf, 262144); 
    if (buf.readBoolean())
      if (protocolVersion >= 762) {
        this.icon = DefinedPacket.readArray(buf);
      } else {
        this.icon = readString(buf);
      }  
    if (protocolVersion < 761)
      this.preview = buf.readBoolean(); 
    if (protocolVersion >= 760)
      this.enforceSecure = buf.readBoolean(); 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (this.motd != null) {
      if (protocolVersion >= 762)
        buf.writeBoolean(true); 
      writeString(this.motd, buf, 262144);
    } else {
      if (protocolVersion >= 762)
        throw new IllegalArgumentException("MOTD required for this version"); 
      buf.writeBoolean(false);
    } 
    if (this.icon != null) {
      buf.writeBoolean(true);
      if (protocolVersion >= 762) {
        DefinedPacket.writeArray((byte[])this.icon, buf);
      } else {
        writeString((String)this.icon, buf);
      } 
    } else {
      buf.writeBoolean(false);
    } 
    if (protocolVersion < 761)
      buf.writeBoolean(this.preview); 
    if (protocolVersion >= 760)
      buf.writeBoolean(this.enforceSecure); 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
