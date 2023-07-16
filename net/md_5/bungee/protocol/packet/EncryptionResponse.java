package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class EncryptionResponse extends DefinedPacket {
  private byte[] sharedSecret;
  
  private byte[] verifyToken;
  
  private EncryptionData encryptionData;
  
  public void setSharedSecret(byte[] sharedSecret) {
    this.sharedSecret = sharedSecret;
  }
  
  public void setVerifyToken(byte[] verifyToken) {
    this.verifyToken = verifyToken;
  }
  
  public void setEncryptionData(EncryptionData encryptionData) {
    this.encryptionData = encryptionData;
  }
  
  public String toString() {
    return "EncryptionResponse(sharedSecret=" + Arrays.toString(getSharedSecret()) + ", verifyToken=" + Arrays.toString(getVerifyToken()) + ", encryptionData=" + getEncryptionData() + ")";
  }
  
  public EncryptionResponse() {}
  
  public EncryptionResponse(byte[] sharedSecret, byte[] verifyToken, EncryptionData encryptionData) {
    this.sharedSecret = sharedSecret;
    this.verifyToken = verifyToken;
    this.encryptionData = encryptionData;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof EncryptionResponse))
      return false; 
    EncryptionResponse other = (EncryptionResponse)o;
    if (!other.canEqual(this))
      return false; 
    if (!Arrays.equals(getSharedSecret(), other.getSharedSecret()))
      return false; 
    if (!Arrays.equals(getVerifyToken(), other.getVerifyToken()))
      return false; 
    Object this$encryptionData = getEncryptionData(), other$encryptionData = other.getEncryptionData();
    return !((this$encryptionData == null) ? (other$encryptionData != null) : !this$encryptionData.equals(other$encryptionData));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof EncryptionResponse;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + Arrays.hashCode(getSharedSecret());
    result = result * 59 + Arrays.hashCode(getVerifyToken());
    Object $encryptionData = getEncryptionData();
    return result * 59 + (($encryptionData == null) ? 43 : $encryptionData.hashCode());
  }
  
  public byte[] getSharedSecret() {
    return this.sharedSecret;
  }
  
  public byte[] getVerifyToken() {
    return this.verifyToken;
  }
  
  public EncryptionData getEncryptionData() {
    return this.encryptionData;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      this.sharedSecret = v17readArray(buf);
      this.verifyToken = v17readArray(buf);
      return;
    } 
    this.sharedSecret = readArray(buf, 128);
    if (protocolVersion < 759 || protocolVersion >= 761 || buf.readBoolean()) {
      this.verifyToken = readArray(buf, 128);
    } else {
      this.encryptionData = new EncryptionData(buf.readLong(), readArray(buf));
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      v17writeArray(this.sharedSecret, buf, false);
      v17writeArray(this.verifyToken, buf, false);
      return;
    } 
    writeArray(this.sharedSecret, buf);
    if (this.verifyToken != null) {
      if (protocolVersion >= 759 && protocolVersion <= 761)
        buf.writeBoolean(true); 
      writeArray(this.verifyToken, buf);
    } else {
      buf.writeLong(this.encryptionData.getSalt());
      writeArray(this.encryptionData.getSignature(), buf);
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public static class EncryptionData {
    private final long salt;
    
    private final byte[] signature;
    
    public EncryptionData(long salt, byte[] signature) {
      this.salt = salt;
      this.signature = signature;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof EncryptionData))
        return false; 
      EncryptionData other = (EncryptionData)o;
      return !other.canEqual(this) ? false : ((getSalt() != other.getSalt()) ? false : (!!Arrays.equals(getSignature(), other.getSignature())));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof EncryptionData;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      long $salt = getSalt();
      result = result * 59 + (int)($salt >>> 32L ^ $salt);
      return result * 59 + Arrays.hashCode(getSignature());
    }
    
    public String toString() {
      return "EncryptionResponse.EncryptionData(salt=" + getSalt() + ", signature=" + Arrays.toString(getSignature()) + ")";
    }
    
    public long getSalt() {
      return this.salt;
    }
    
    public byte[] getSignature() {
      return this.signature;
    }
  }
  
  public int expectedMaxLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (protocolVersion >= 759)
      return -1; 
    return 260;
  }
  
  public int expectedMinLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    return expectedMaxLength(buf, direction, protocolVersion);
  }
}
