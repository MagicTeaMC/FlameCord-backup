package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class ScoreboardScore extends DefinedPacket {
  private String itemName;
  
  private byte action;
  
  private String scoreName;
  
  private int value;
  
  public void setItemName(String itemName) {
    this.itemName = itemName;
  }
  
  public void setAction(byte action) {
    this.action = action;
  }
  
  public void setScoreName(String scoreName) {
    this.scoreName = scoreName;
  }
  
  public void setValue(int value) {
    this.value = value;
  }
  
  public String toString() {
    return "ScoreboardScore(itemName=" + getItemName() + ", action=" + getAction() + ", scoreName=" + getScoreName() + ", value=" + getValue() + ")";
  }
  
  public ScoreboardScore() {}
  
  public ScoreboardScore(String itemName, byte action, String scoreName, int value) {
    this.itemName = itemName;
    this.action = action;
    this.scoreName = scoreName;
    this.value = value;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ScoreboardScore))
      return false; 
    ScoreboardScore other = (ScoreboardScore)o;
    if (!other.canEqual(this))
      return false; 
    if (getAction() != other.getAction())
      return false; 
    if (getValue() != other.getValue())
      return false; 
    Object this$itemName = getItemName(), other$itemName = other.getItemName();
    if ((this$itemName == null) ? (other$itemName != null) : !this$itemName.equals(other$itemName))
      return false; 
    Object this$scoreName = getScoreName(), other$scoreName = other.getScoreName();
    return !((this$scoreName == null) ? (other$scoreName != null) : !this$scoreName.equals(other$scoreName));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ScoreboardScore;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getAction();
    result = result * 59 + getValue();
    Object $itemName = getItemName();
    result = result * 59 + (($itemName == null) ? 43 : $itemName.hashCode());
    Object $scoreName = getScoreName();
    return result * 59 + (($scoreName == null) ? 43 : $scoreName.hashCode());
  }
  
  public String getItemName() {
    return this.itemName;
  }
  
  public byte getAction() {
    return this.action;
  }
  
  public String getScoreName() {
    return this.scoreName;
  }
  
  public int getValue() {
    return this.value;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      this.itemName = readString(buf);
      this.action = buf.readByte();
      if (this.action != 1) {
        this.scoreName = readString(buf);
        this.value = buf.readInt();
      } 
      return;
    } 
    this.itemName = readString(buf);
    this.action = buf.readByte();
    this.scoreName = readString(buf);
    if (this.action != 1)
      this.value = readVarInt(buf); 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      writeString(this.itemName, buf);
      buf.writeByte(this.action);
      if (this.action != 1) {
        writeString(this.scoreName, buf);
        buf.writeInt(this.value);
      } 
      return;
    } 
    writeString(this.itemName, buf);
    buf.writeByte(this.action);
    writeString(this.scoreName, buf);
    if (this.action != 1)
      writeVarInt(this.value, buf); 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
