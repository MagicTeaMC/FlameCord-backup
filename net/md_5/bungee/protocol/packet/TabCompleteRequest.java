package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class TabCompleteRequest extends DefinedPacket {
  private int transactionId;
  
  private String cursor;
  
  private boolean assumeCommand;
  
  private boolean hasPositon;
  
  private long position;
  
  public void setTransactionId(int transactionId) {
    this.transactionId = transactionId;
  }
  
  public void setCursor(String cursor) {
    this.cursor = cursor;
  }
  
  public void setAssumeCommand(boolean assumeCommand) {
    this.assumeCommand = assumeCommand;
  }
  
  public void setHasPositon(boolean hasPositon) {
    this.hasPositon = hasPositon;
  }
  
  public void setPosition(long position) {
    this.position = position;
  }
  
  public String toString() {
    return "TabCompleteRequest(transactionId=" + getTransactionId() + ", cursor=" + getCursor() + ", assumeCommand=" + isAssumeCommand() + ", hasPositon=" + isHasPositon() + ", position=" + getPosition() + ")";
  }
  
  public TabCompleteRequest() {}
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof TabCompleteRequest))
      return false; 
    TabCompleteRequest other = (TabCompleteRequest)o;
    if (!other.canEqual(this))
      return false; 
    if (getTransactionId() != other.getTransactionId())
      return false; 
    if (isAssumeCommand() != other.isAssumeCommand())
      return false; 
    if (isHasPositon() != other.isHasPositon())
      return false; 
    if (getPosition() != other.getPosition())
      return false; 
    Object this$cursor = getCursor(), other$cursor = other.getCursor();
    return !((this$cursor == null) ? (other$cursor != null) : !this$cursor.equals(other$cursor));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof TabCompleteRequest;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getTransactionId();
    result = result * 59 + (isAssumeCommand() ? 79 : 97);
    result = result * 59 + (isHasPositon() ? 79 : 97);
    long $position = getPosition();
    result = result * 59 + (int)($position >>> 32L ^ $position);
    Object $cursor = getCursor();
    return result * 59 + (($cursor == null) ? 43 : $cursor.hashCode());
  }
  
  public int getTransactionId() {
    return this.transactionId;
  }
  
  public String getCursor() {
    return this.cursor;
  }
  
  public boolean isAssumeCommand() {
    return this.assumeCommand;
  }
  
  public boolean isHasPositon() {
    return this.hasPositon;
  }
  
  public long getPosition() {
    return this.position;
  }
  
  public TabCompleteRequest(int transactionId, String cursor) {
    this.transactionId = transactionId;
    this.cursor = cursor;
  }
  
  public TabCompleteRequest(String cursor, boolean assumeCommand, boolean hasPosition, long position) {
    this.cursor = cursor;
    this.assumeCommand = assumeCommand;
    this.hasPositon = hasPosition;
    this.position = position;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 393)
      this.transactionId = readVarInt(buf); 
    this.cursor = readString(buf, (protocolVersion > 393) ? 32500 : ((protocolVersion == 393) ? 256 : 32767));
    if (protocolVersion >= 47 && 
      protocolVersion < 393) {
      if (protocolVersion >= 107)
        this.assumeCommand = buf.readBoolean(); 
      if (this.hasPositon = buf.readBoolean())
        this.position = buf.readLong(); 
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 393)
      writeVarInt(this.transactionId, buf); 
    writeString(this.cursor, buf);
    if (protocolVersion >= 47 && 
      protocolVersion < 393) {
      if (protocolVersion >= 107)
        buf.writeBoolean(this.assumeCommand); 
      buf.writeBoolean(this.hasPositon);
      if (this.hasPositon)
        buf.writeLong(this.position); 
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
