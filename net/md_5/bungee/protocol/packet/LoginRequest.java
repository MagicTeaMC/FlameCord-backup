package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.PlayerPublicKey;
import net.md_5.bungee.protocol.ProtocolConstants;

public class LoginRequest extends DefinedPacket {
  private String data;
  
  private PlayerPublicKey publicKey;
  
  private UUID uuid;
  
  public void setData(String data) {
    this.data = data;
  }
  
  public void setPublicKey(PlayerPublicKey publicKey) {
    this.publicKey = publicKey;
  }
  
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
  
  public String toString() {
    return "LoginRequest(data=" + getData() + ", publicKey=" + getPublicKey() + ", uuid=" + getUuid() + ")";
  }
  
  public LoginRequest() {}
  
  public LoginRequest(String data, PlayerPublicKey publicKey, UUID uuid) {
    this.data = data;
    this.publicKey = publicKey;
    this.uuid = uuid;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof LoginRequest))
      return false; 
    LoginRequest other = (LoginRequest)o;
    if (!other.canEqual(this))
      return false; 
    Object this$data = getData(), other$data = other.getData();
    if ((this$data == null) ? (other$data != null) : !this$data.equals(other$data))
      return false; 
    Object this$publicKey = getPublicKey(), other$publicKey = other.getPublicKey();
    if ((this$publicKey == null) ? (other$publicKey != null) : !this$publicKey.equals(other$publicKey))
      return false; 
    Object this$uuid = getUuid(), other$uuid = other.getUuid();
    return !((this$uuid == null) ? (other$uuid != null) : !this$uuid.equals(other$uuid));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof LoginRequest;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $data = getData();
    result = result * 59 + (($data == null) ? 43 : $data.hashCode());
    Object $publicKey = getPublicKey();
    result = result * 59 + (($publicKey == null) ? 43 : $publicKey.hashCode());
    Object $uuid = getUuid();
    return result * 59 + (($uuid == null) ? 43 : $uuid.hashCode());
  }
  
  public String getData() {
    return this.data;
  }
  
  public PlayerPublicKey getPublicKey() {
    return this.publicKey;
  }
  
  public UUID getUuid() {
    return this.uuid;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.data = readString(buf, 16);
    if (protocolVersion >= 759 && protocolVersion < 761)
      this.publicKey = readPublicKey(buf); 
    if (protocolVersion >= 760)
      if (buf.isReadable() && buf.readBoolean())
        this.uuid = readUUID(buf);  
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    writeString(this.data, buf);
    if (protocolVersion >= 759 && protocolVersion < 761)
      writePublicKey(this.publicKey, buf); 
    if (protocolVersion >= 760)
      if (this.uuid != null) {
        buf.writeBoolean(true);
        writeUUID(this.uuid, buf);
      } else {
        buf.writeBoolean(false);
      }  
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public int expectedMaxLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 759)
      return 1089; 
    return 65;
  }
}
