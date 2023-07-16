package net.md_5.bungee.protocol.packet;

import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ChatChain;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.SeenMessages;

public class ClientChat extends DefinedPacket {
  private String message;
  
  private long timestamp;
  
  private long salt;
  
  private byte[] signature;
  
  private boolean signedPreview;
  
  private ChatChain chain;
  
  private SeenMessages seenMessages;
  
  public void setMessage(String message) {
    this.message = message;
  }
  
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
  public void setSalt(long salt) {
    this.salt = salt;
  }
  
  public void setSignature(byte[] signature) {
    this.signature = signature;
  }
  
  public void setSignedPreview(boolean signedPreview) {
    this.signedPreview = signedPreview;
  }
  
  public void setChain(ChatChain chain) {
    this.chain = chain;
  }
  
  public void setSeenMessages(SeenMessages seenMessages) {
    this.seenMessages = seenMessages;
  }
  
  public String toString() {
    return "ClientChat(message=" + getMessage() + ", timestamp=" + getTimestamp() + ", salt=" + getSalt() + ", signature=" + Arrays.toString(getSignature()) + ", signedPreview=" + isSignedPreview() + ", chain=" + getChain() + ", seenMessages=" + getSeenMessages() + ")";
  }
  
  public ClientChat() {}
  
  public ClientChat(String message, long timestamp, long salt, byte[] signature, boolean signedPreview, ChatChain chain, SeenMessages seenMessages) {
    this.message = message;
    this.timestamp = timestamp;
    this.salt = salt;
    this.signature = signature;
    this.signedPreview = signedPreview;
    this.chain = chain;
    this.seenMessages = seenMessages;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ClientChat))
      return false; 
    ClientChat other = (ClientChat)o;
    if (!other.canEqual(this))
      return false; 
    if (getTimestamp() != other.getTimestamp())
      return false; 
    if (getSalt() != other.getSalt())
      return false; 
    if (isSignedPreview() != other.isSignedPreview())
      return false; 
    Object this$message = getMessage(), other$message = other.getMessage();
    if ((this$message == null) ? (other$message != null) : !this$message.equals(other$message))
      return false; 
    if (!Arrays.equals(getSignature(), other.getSignature()))
      return false; 
    Object this$chain = getChain(), other$chain = other.getChain();
    if ((this$chain == null) ? (other$chain != null) : !this$chain.equals(other$chain))
      return false; 
    Object this$seenMessages = getSeenMessages(), other$seenMessages = other.getSeenMessages();
    return !((this$seenMessages == null) ? (other$seenMessages != null) : !this$seenMessages.equals(other$seenMessages));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ClientChat;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    long $timestamp = getTimestamp();
    result = result * 59 + (int)($timestamp >>> 32L ^ $timestamp);
    long $salt = getSalt();
    result = result * 59 + (int)($salt >>> 32L ^ $salt);
    result = result * 59 + (isSignedPreview() ? 79 : 97);
    Object $message = getMessage();
    result = result * 59 + (($message == null) ? 43 : $message.hashCode());
    result = result * 59 + Arrays.hashCode(getSignature());
    Object $chain = getChain();
    result = result * 59 + (($chain == null) ? 43 : $chain.hashCode());
    Object $seenMessages = getSeenMessages();
    return result * 59 + (($seenMessages == null) ? 43 : $seenMessages.hashCode());
  }
  
  public String getMessage() {
    return this.message;
  }
  
  public long getTimestamp() {
    return this.timestamp;
  }
  
  public long getSalt() {
    return this.salt;
  }
  
  public byte[] getSignature() {
    return this.signature;
  }
  
  public boolean isSignedPreview() {
    return this.signedPreview;
  }
  
  public ChatChain getChain() {
    return this.chain;
  }
  
  public SeenMessages getSeenMessages() {
    return this.seenMessages;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.message = readString(buf, 256);
    this.timestamp = buf.readLong();
    this.salt = buf.readLong();
    if (protocolVersion >= 761) {
      if (buf.readBoolean()) {
        this.signature = new byte[256];
        buf.readBytes(this.signature);
      } 
    } else {
      this.signature = readArray(buf);
    } 
    if (protocolVersion < 761)
      this.signedPreview = buf.readBoolean(); 
    if (protocolVersion >= 761) {
      this.seenMessages = new SeenMessages();
      this.seenMessages.read(buf, direction, protocolVersion);
    } else if (protocolVersion >= 760) {
      this.chain = new ChatChain();
      this.chain.read(buf, direction, protocolVersion);
    } 
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    writeString(this.message, buf);
    buf.writeLong(this.timestamp);
    buf.writeLong(this.salt);
    if (protocolVersion >= 761) {
      buf.writeBoolean((this.signature != null));
      if (this.signature != null)
        buf.writeBytes(this.signature); 
    } else {
      writeArray(this.signature, buf);
    } 
    if (protocolVersion < 761)
      buf.writeBoolean(this.signedPreview); 
    if (protocolVersion >= 761) {
      this.seenMessages.write(buf, direction, protocolVersion);
    } else if (protocolVersion >= 760) {
      this.chain.write(buf, direction, protocolVersion);
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
}
