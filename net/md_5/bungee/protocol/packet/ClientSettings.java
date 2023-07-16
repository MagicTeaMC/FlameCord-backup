package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ClientSettings extends DefinedPacket {
  private String locale;
  
  private byte viewDistance;
  
  private int chatFlags;
  
  private boolean chatColours;
  
  private byte difficulty;
  
  private byte skinParts;
  
  private int mainHand;
  
  private boolean disableTextFiltering;
  
  private boolean allowServerListing;
  
  public void setLocale(String locale) {
    this.locale = locale;
  }
  
  public void setViewDistance(byte viewDistance) {
    this.viewDistance = viewDistance;
  }
  
  public void setChatFlags(int chatFlags) {
    this.chatFlags = chatFlags;
  }
  
  public void setChatColours(boolean chatColours) {
    this.chatColours = chatColours;
  }
  
  public void setDifficulty(byte difficulty) {
    this.difficulty = difficulty;
  }
  
  public void setSkinParts(byte skinParts) {
    this.skinParts = skinParts;
  }
  
  public void setMainHand(int mainHand) {
    this.mainHand = mainHand;
  }
  
  public void setDisableTextFiltering(boolean disableTextFiltering) {
    this.disableTextFiltering = disableTextFiltering;
  }
  
  public void setAllowServerListing(boolean allowServerListing) {
    this.allowServerListing = allowServerListing;
  }
  
  public String toString() {
    return "ClientSettings(locale=" + getLocale() + ", viewDistance=" + getViewDistance() + ", chatFlags=" + getChatFlags() + ", chatColours=" + isChatColours() + ", difficulty=" + getDifficulty() + ", skinParts=" + getSkinParts() + ", mainHand=" + getMainHand() + ", disableTextFiltering=" + isDisableTextFiltering() + ", allowServerListing=" + isAllowServerListing() + ")";
  }
  
  public ClientSettings() {}
  
  public ClientSettings(String locale, byte viewDistance, int chatFlags, boolean chatColours, byte difficulty, byte skinParts, int mainHand, boolean disableTextFiltering, boolean allowServerListing) {
    this.locale = locale;
    this.viewDistance = viewDistance;
    this.chatFlags = chatFlags;
    this.chatColours = chatColours;
    this.difficulty = difficulty;
    this.skinParts = skinParts;
    this.mainHand = mainHand;
    this.disableTextFiltering = disableTextFiltering;
    this.allowServerListing = allowServerListing;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ClientSettings))
      return false; 
    ClientSettings other = (ClientSettings)o;
    if (!other.canEqual(this))
      return false; 
    if (getViewDistance() != other.getViewDistance())
      return false; 
    if (getChatFlags() != other.getChatFlags())
      return false; 
    if (isChatColours() != other.isChatColours())
      return false; 
    if (getDifficulty() != other.getDifficulty())
      return false; 
    if (getSkinParts() != other.getSkinParts())
      return false; 
    if (getMainHand() != other.getMainHand())
      return false; 
    if (isDisableTextFiltering() != other.isDisableTextFiltering())
      return false; 
    if (isAllowServerListing() != other.isAllowServerListing())
      return false; 
    Object this$locale = getLocale(), other$locale = other.getLocale();
    return !((this$locale == null) ? (other$locale != null) : !this$locale.equals(other$locale));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ClientSettings;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getViewDistance();
    result = result * 59 + getChatFlags();
    result = result * 59 + (isChatColours() ? 79 : 97);
    result = result * 59 + getDifficulty();
    result = result * 59 + getSkinParts();
    result = result * 59 + getMainHand();
    result = result * 59 + (isDisableTextFiltering() ? 79 : 97);
    result = result * 59 + (isAllowServerListing() ? 79 : 97);
    Object $locale = getLocale();
    return result * 59 + (($locale == null) ? 43 : $locale.hashCode());
  }
  
  public String getLocale() {
    return this.locale;
  }
  
  public byte getViewDistance() {
    return this.viewDistance;
  }
  
  public int getChatFlags() {
    return this.chatFlags;
  }
  
  public boolean isChatColours() {
    return this.chatColours;
  }
  
  public byte getDifficulty() {
    return this.difficulty;
  }
  
  public byte getSkinParts() {
    return this.skinParts;
  }
  
  public int getMainHand() {
    return this.mainHand;
  }
  
  public boolean isDisableTextFiltering() {
    return this.disableTextFiltering;
  }
  
  public boolean isAllowServerListing() {
    return this.allowServerListing;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.locale = readString(buf, 16);
    this.viewDistance = buf.readByte();
    this.chatFlags = (protocolVersion >= 107) ? DefinedPacket.readVarInt(buf) : buf.readUnsignedByte();
    this.chatColours = buf.readBoolean();
    this.skinParts = buf.readByte();
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5))
      this.difficulty = buf.readByte(); 
    if (protocolVersion >= 107)
      this.mainHand = DefinedPacket.readVarInt(buf); 
    if (protocolVersion >= 755)
      this.disableTextFiltering = buf.readBoolean(); 
    if (protocolVersion >= 757)
      this.allowServerListing = buf.readBoolean(); 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      writeString(this.locale, buf);
      buf.writeByte(this.viewDistance);
      buf.writeByte(this.chatFlags);
      buf.writeBoolean(this.chatColours);
      buf.writeByte(this.skinParts);
      buf.writeByte(this.difficulty);
      return;
    } 
    writeString(this.locale, buf);
    buf.writeByte(this.viewDistance);
    if (protocolVersion >= 107) {
      DefinedPacket.writeVarInt(this.chatFlags, buf);
    } else {
      buf.writeByte(this.chatFlags);
    } 
    buf.writeBoolean(this.chatColours);
    buf.writeByte(this.skinParts);
    if (protocolVersion >= 107)
      DefinedPacket.writeVarInt(this.mainHand, buf); 
    if (protocolVersion >= 755)
      buf.writeBoolean(this.disableTextFiltering); 
    if (protocolVersion >= 757)
      buf.writeBoolean(this.allowServerListing); 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
