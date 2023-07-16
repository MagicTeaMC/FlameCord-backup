package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;

public class Kick extends DefinedPacket {
  private String message;
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public String toString() {
    return "Kick(message=" + getMessage() + ")";
  }
  
  public Kick() {}
  
  public Kick(String message) {
    this.message = message;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Kick))
      return false; 
    Kick other = (Kick)o;
    if (!other.canEqual(this))
      return false; 
    Object this$message = getMessage(), other$message = other.getMessage();
    return !((this$message == null) ? (other$message != null) : !this$message.equals(other$message));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Kick;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $message = getMessage();
    return result * 59 + (($message == null) ? 43 : $message.hashCode());
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public void read(ByteBuf buf) {
    this.message = readString(buf);
  }
  
  public void write(ByteBuf buf) {
    writeString(this.message, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
