package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class Title extends DefinedPacket {
  private Action action;
  
  private String text;
  
  private int fadeIn;
  
  private int stay;
  
  private int fadeOut;
  
  public void setAction(Action action) {
    this.action = action;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public void setFadeIn(int fadeIn) {
    this.fadeIn = fadeIn;
  }
  
  public void setStay(int stay) {
    this.stay = stay;
  }
  
  public void setFadeOut(int fadeOut) {
    this.fadeOut = fadeOut;
  }
  
  public String toString() {
    return "Title(action=" + getAction() + ", text=" + getText() + ", fadeIn=" + getFadeIn() + ", stay=" + getStay() + ", fadeOut=" + getFadeOut() + ")";
  }
  
  public Title() {}
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof Title))
      return false; 
    Title other = (Title)o;
    if (!other.canEqual(this))
      return false; 
    if (getFadeIn() != other.getFadeIn())
      return false; 
    if (getStay() != other.getStay())
      return false; 
    if (getFadeOut() != other.getFadeOut())
      return false; 
    Object this$action = getAction(), other$action = other.getAction();
    if ((this$action == null) ? (other$action != null) : !this$action.equals(other$action))
      return false; 
    Object this$text = getText(), other$text = other.getText();
    return !((this$text == null) ? (other$text != null) : !this$text.equals(other$text));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof Title;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getFadeIn();
    result = result * 59 + getStay();
    result = result * 59 + getFadeOut();
    Object $action = getAction();
    result = result * 59 + (($action == null) ? 43 : $action.hashCode());
    Object $text = getText();
    return result * 59 + (($text == null) ? 43 : $text.hashCode());
  }
  
  public Action getAction() {
    return this.action;
  }
  
  public String getText() {
    return this.text;
  }
  
  public int getFadeIn() {
    return this.fadeIn;
  }
  
  public int getStay() {
    return this.stay;
  }
  
  public int getFadeOut() {
    return this.fadeOut;
  }
  
  public Title(Action action) {
    this.action = action;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 755) {
      this.text = readString(buf);
      return;
    } 
    int index = readVarInt(buf);
    if (protocolVersion <= 210 && index >= 2)
      index++; 
    this.action = Action.values()[index];
    switch (this.action) {
      case TITLE:
      case SUBTITLE:
      case ACTIONBAR:
        this.text = readString(buf);
        break;
      case TIMES:
        this.fadeIn = buf.readInt();
        this.stay = buf.readInt();
        this.fadeOut = buf.readInt();
        break;
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 755) {
      writeString(this.text, buf);
      return;
    } 
    int index = this.action.ordinal();
    if (protocolVersion <= 210 && index >= 2)
      index--; 
    writeVarInt(index, buf);
    switch (this.action) {
      case TITLE:
      case SUBTITLE:
      case ACTIONBAR:
        writeString(this.text, buf);
        break;
      case TIMES:
        buf.writeInt(this.fadeIn);
        buf.writeInt(this.stay);
        buf.writeInt(this.fadeOut);
        break;
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public enum Action {
    TITLE, SUBTITLE, ACTIONBAR, TIMES, CLEAR, RESET;
  }
}
