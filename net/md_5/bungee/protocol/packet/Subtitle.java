package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Subtitle extends DefinedPacket {
  private String text;
  
  public void setText(String text) {
    this.text = text;
  }
  
  public String toString() {
    return "Subtitle(text=" + getText() + ")";
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Subtitle))
      return false; 
    Subtitle other = (Subtitle)o;
    if (!other.canEqual(this))
      return false; 
    Object this$text = getText(), other$text = other.getText();
    return !((this$text == null) ? (other$text != null) : !this$text.equals(other$text));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Subtitle;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $text = getText();
    return result * 59 + (($text == null) ? 43 : $text.hashCode());
  }
  
  public String getText() {
    return this.text;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.text = readString(buf);
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    writeString(this.text, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
