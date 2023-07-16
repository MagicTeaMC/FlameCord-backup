package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class SystemChat extends DefinedPacket {
  private String message;
  
  private int position;
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public void setPosition(int position) {
    this.position = position;
  }
  
  public String toString() {
    return "SystemChat(message=" + getMessage() + ", position=" + getPosition() + ")";
  }
  
  public SystemChat() {}
  
  public SystemChat(String message, int position) {
    this.message = message;
    this.position = position;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof SystemChat))
      return false; 
    SystemChat other = (SystemChat)o;
    if (!other.canEqual(this))
      return false; 
    if (getPosition() != other.getPosition())
      return false; 
    Object this$message = getMessage(), other$message = other.getMessage();
    return !((this$message == null) ? (other$message != null) : !this$message.equals(other$message));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof SystemChat;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getPosition();
    Object $message = getMessage();
    return result * 59 + (($message == null) ? 43 : $message.hashCode());
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public int getPosition() {
    return this.position;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.message = readString(buf, 262144);
    this.position = (protocolVersion >= 760) ? (buf.readBoolean() ? ChatMessageType.ACTION_BAR.ordinal() : 0) : readVarInt(buf);
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    writeString(this.message, buf, 262144);
    if (protocolVersion >= 760) {
      buf.writeBoolean((this.position == ChatMessageType.ACTION_BAR.ordinal()));
    } else {
      writeVarInt(this.position, buf);
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
