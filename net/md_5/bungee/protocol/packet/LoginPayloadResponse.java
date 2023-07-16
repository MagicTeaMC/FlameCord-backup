package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.OverflowPacketException;

public class LoginPayloadResponse extends DefinedPacket {
  private int id;
  
  private byte[] data;
  
  public void setId(int id) {
    this.id = id;
  }
  
  public void setData(byte[] data) {
    this.data = data;
  }
  
  public String toString() {
    return "LoginPayloadResponse(id=" + getId() + ", data=" + Arrays.toString(getData()) + ")";
  }
  
  public LoginPayloadResponse() {}
  
  public LoginPayloadResponse(int id, byte[] data) {
    this.id = id;
    this.data = data;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof LoginPayloadResponse))
      return false; 
    LoginPayloadResponse other = (LoginPayloadResponse)o;
    return !other.canEqual(this) ? false : ((getId() != other.getId()) ? false : (!!Arrays.equals(getData(), other.getData())));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof LoginPayloadResponse;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + getId();
    return result * 59 + Arrays.hashCode(getData());
  }
  
  public int getId() {
    return this.id;
  }
  
  public byte[] getData() {
    return this.data;
  }
  
  public void read(ByteBuf buf) {
    this.id = readVarInt(buf);
    if (buf.readBoolean()) {
      int len = buf.readableBytes();
      if (len > 1048576)
        throw new OverflowPacketException("Payload may not be larger than 1048576 bytes"); 
      this.data = new byte[len];
      buf.readBytes(this.data);
    } 
  }
  
  public void write(ByteBuf buf) {
    writeVarInt(this.id, buf);
    if (this.data != null) {
      buf.writeBoolean(true);
      buf.writeBytes(this.data);
    } else {
      buf.writeBoolean(false);
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
