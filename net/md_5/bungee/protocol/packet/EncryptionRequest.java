package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class EncryptionRequest extends DefinedPacket {
  private String serverId;
  
  private byte[] publicKey;
  
  private byte[] verifyToken;
  
  public void setServerId(String serverId) {
    this.serverId = serverId;
  }
  
  public void setPublicKey(byte[] publicKey) {
    this.publicKey = publicKey;
  }
  
  public void setVerifyToken(byte[] verifyToken) {
    this.verifyToken = verifyToken;
  }
  
  public String toString() {
    return "EncryptionRequest(serverId=" + getServerId() + ", publicKey=" + Arrays.toString(getPublicKey()) + ", verifyToken=" + Arrays.toString(getVerifyToken()) + ")";
  }
  
  public EncryptionRequest() {}
  
  public EncryptionRequest(String serverId, byte[] publicKey, byte[] verifyToken) {
    this.serverId = serverId;
    this.publicKey = publicKey;
    this.verifyToken = verifyToken;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof EncryptionRequest))
      return false; 
    EncryptionRequest other = (EncryptionRequest)o;
    if (!other.canEqual(this))
      return false; 
    Object this$serverId = getServerId(), other$serverId = other.getServerId();
    return ((this$serverId == null) ? (other$serverId != null) : !this$serverId.equals(other$serverId)) ? false : (!Arrays.equals(getPublicKey(), other.getPublicKey()) ? false : (!!Arrays.equals(getVerifyToken(), other.getVerifyToken())));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof EncryptionRequest;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $serverId = getServerId();
    result = result * 59 + (($serverId == null) ? 43 : $serverId.hashCode());
    result = result * 59 + Arrays.hashCode(getPublicKey());
    return result * 59 + Arrays.hashCode(getVerifyToken());
  }
  
  public String getServerId() {
    return this.serverId;
  }
  
  public byte[] getPublicKey() {
    return this.publicKey;
  }
  
  public byte[] getVerifyToken() {
    return this.verifyToken;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      this.serverId = readString(buf);
      this.publicKey = v17readArray(buf);
      this.verifyToken = v17readArray(buf);
      return;
    } 
    this.serverId = readString(buf);
    this.publicKey = readArray(buf);
    this.verifyToken = readArray(buf);
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      writeString(this.serverId, buf);
      v17writeArray(this.publicKey, buf, false);
      v17writeArray(this.verifyToken, buf, false);
      return;
    } 
    writeString(this.serverId, buf);
    writeArray(this.publicKey, buf);
    writeArray(this.verifyToken, buf);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public int expectedMaxLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    return 532;
  }
  
  public int expectedMinLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    return 22;
  }
}
