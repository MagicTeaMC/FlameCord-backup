package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.UUID;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.Property;
import net.md_5.bungee.protocol.ProtocolConstants;

public class LoginSuccess extends DefinedPacket {
  private UUID uuid;
  
  private String username;
  
  private Property[] properties;
  
  public void setUuid(UUID uuid) {
    this.uuid = uuid;
  }
  
  public void setUsername(String username) {
    this.username = username;
  }
  
  public void setProperties(Property[] properties) {
    this.properties = properties;
  }
  
  public String toString() {
    return "LoginSuccess(uuid=" + getUuid() + ", username=" + getUsername() + ", properties=" + Arrays.deepToString((Object[])getProperties()) + ")";
  }
  
  public LoginSuccess() {}
  
  public LoginSuccess(UUID uuid, String username, Property[] properties) {
    this.uuid = uuid;
    this.username = username;
    this.properties = properties;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof LoginSuccess))
      return false; 
    LoginSuccess other = (LoginSuccess)o;
    if (!other.canEqual(this))
      return false; 
    Object this$uuid = getUuid(), other$uuid = other.getUuid();
    if ((this$uuid == null) ? (other$uuid != null) : !this$uuid.equals(other$uuid))
      return false; 
    Object this$username = getUsername(), other$username = other.getUsername();
    return ((this$username == null) ? (other$username != null) : !this$username.equals(other$username)) ? false : (!!Arrays.deepEquals((Object[])getProperties(), (Object[])other.getProperties()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof LoginSuccess;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $uuid = getUuid();
    result = result * 59 + (($uuid == null) ? 43 : $uuid.hashCode());
    Object $username = getUsername();
    result = result * 59 + (($username == null) ? 43 : $username.hashCode());
    return result * 59 + Arrays.deepHashCode((Object[])getProperties());
  }
  
  public UUID getUuid() {
    return this.uuid;
  }
  
  public String getUsername() {
    return this.username;
  }
  
  public Property[] getProperties() {
    return this.properties;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion <= 4) {
      this.uuid = readUndashedUUID(buf);
    } else if (protocolVersion >= 735) {
      this.uuid = readUUID(buf);
    } else {
      this.uuid = UUID.fromString(readString(buf));
    } 
    this.username = readString(buf);
    if (protocolVersion >= 759)
      this.properties = readProperties(buf); 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion <= 4) {
      writeUndashedUUID(this.uuid.toString(), buf);
    } else if (protocolVersion >= 735) {
      writeUUID(this.uuid, buf);
    } else {
      writeString(this.uuid.toString(), buf);
    } 
    writeString(this.username, buf);
    if (protocolVersion >= 759)
      writeProperties(this.properties, buf); 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  private static UUID readUndashedUUID(ByteBuf buf) {
    return UUID.fromString((new StringBuilder(readString(buf))).insert(20, '-').insert(16, '-').insert(12, '-').insert(8, '-').toString());
  }
  
  private static void writeUndashedUUID(String uuid, ByteBuf buf) {
    writeString((new StringBuilder(32)).append(uuid, 0, 8).append(uuid, 9, 13).append(uuid, 14, 18).append(uuid, 19, 23).append(uuid, 24, 36).toString(), buf);
  }
}
