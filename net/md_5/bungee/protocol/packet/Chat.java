package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Chat extends DefinedPacket {
  public void setMessage(String message) {
    this.message = message;
  }
  
  public void setPosition(byte position) {
    this.position = position;
  }
  
  public void setSender(UUID sender) {
    this.sender = sender;
  }
  
  public String toString() {
    return "Chat(message=" + getMessage() + ", position=" + getPosition() + ", sender=" + getSender() + ")";
  }
  
  public Chat() {}
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Chat))
      return false; 
    Chat other = (Chat)o;
    if (!other.canEqual(this))
      return false; 
    if (getPosition() != other.getPosition())
      return false; 
    Object this$message = getMessage(), other$message = other.getMessage();
    if ((this$message == null) ? (other$message != null) : !this$message.equals(other$message))
      return false; 
    Object this$sender = getSender(), other$sender = other.getSender();
    return !((this$sender == null) ? (other$sender != null) : !this$sender.equals(other$sender));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Chat;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getPosition();
    Object $message = getMessage();
    result = result * 59 + (($message == null) ? 43 : $message.hashCode());
    Object $sender = getSender();
    return result * 59 + (($sender == null) ? 43 : $sender.hashCode());
  }
  
  private static final UUID EMPTY_UUID = new UUID(0L, 0L);
  
  private String message;
  
  private byte position;
  
  private UUID sender;
  
  public String getMessage() {
    return this.message;
  }
  
  public byte getPosition() {
    return this.position;
  }
  
  public UUID getSender() {
    return this.sender;
  }
  
  public Chat(String message) {
    this(message, (byte)0);
  }
  
  public Chat(String message, byte position) {
    this(message, position, EMPTY_UUID);
  }
  
  public Chat(String message, byte position, UUID sender) {
    this.message = message;
    this.position = position;
    this.sender = (sender == null) ? EMPTY_UUID : sender;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.message = readString(buf, (direction == ProtocolConstants.Direction.TO_CLIENT) ? 262144 : ((protocolVersion >= 315) ? 256 : 100));
    if (ProtocolConstants.isAfterOrEq(protocolVersion, 47) && 
      direction == ProtocolConstants.Direction.TO_CLIENT) {
      this.position = buf.readByte();
      if (protocolVersion >= 735)
        this.sender = readUUID(buf); 
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    writeString(this.message, buf, (direction == ProtocolConstants.Direction.TO_CLIENT) ? 262144 : ((protocolVersion >= 315) ? 256 : 100));
    if (ProtocolConstants.isAfterOrEq(protocolVersion, 47) && 
      direction == ProtocolConstants.Direction.TO_CLIENT) {
      buf.writeByte(this.position);
      if (protocolVersion >= 735)
        writeUUID(this.sender, buf); 
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
