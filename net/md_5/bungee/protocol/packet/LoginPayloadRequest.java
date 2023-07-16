package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.OverflowPacketException;

public class LoginPayloadRequest extends DefinedPacket {
  private int id;
  
  private String channel;
  
  private byte[] data;
  
  public void setId(int id) {
    this.id = id;
  }
  
  public void setChannel(String channel) {
    this.channel = channel;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  public String toString() {
    return "LoginPayloadRequest(id=" + getId() + ", channel=" + getChannel() + ", data=" + Arrays.toString(getData()) + ")";
  }
  
  public LoginPayloadRequest() {}
  
  public LoginPayloadRequest(int id, String channel, byte[] data) {
    this.id = id;
    this.channel = channel;
    this.data = data;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof LoginPayloadRequest))
      return false; 
    LoginPayloadRequest other = (LoginPayloadRequest)o;
    if (!other.canEqual(this))
      return false; 
    if (getId() != other.getId())
      return false; 
    Object this$channel = getChannel(), other$channel = other.getChannel();
    return ((this$channel == null) ? (other$channel != null) : !this$channel.equals(other$channel)) ? false : (!!Arrays.equals(getData(), other.getData()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof LoginPayloadRequest;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getId();
    Object $channel = getChannel();
    result = result * 59 + (($channel == null) ? 43 : $channel.hashCode());
    return result * 59 + Arrays.hashCode(getData());
  }
  
  public int getId() {
    return this.id;
  }
  
  public String getChannel() {
    return this.channel;
  }
  
  public byte[] getData() {
    return this.data;
  }
  
  public void read(ByteBuf buf) {
    this.id = readVarInt(buf);
    this.channel = readString(buf);
    int len = buf.readableBytes();
    if (len > 1048576)
      throw new OverflowPacketException("Payload may not be larger than 1048576 bytes"); 
    this.data = new byte[len];
    buf.readBytes(this.data);
  }
  
  public void write(ByteBuf buf) {
    writeVarInt(this.id, buf);
    writeString(this.channel, buf);
    buf.writeBytes(this.data);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
