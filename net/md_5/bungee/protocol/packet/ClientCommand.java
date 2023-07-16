package net.md_5.bungee.protocol.packet;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import java.util.HashMap;
import java.util.Map;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.ChatChain;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;
import net.md_5.bungee.protocol.SeenMessages;

public class ClientCommand extends DefinedPacket {
  private String command;
  
  private long timestamp;
  
  private long salt;
  
  private Map<String, byte[]> signatures;
  
  private boolean signedPreview;
  
  private ChatChain chain;
  
  private SeenMessages seenMessages;
  
  public void setCommand(String command) {
    this.command = command;
  }
  
  public void setTimestamp(long timestamp) {
    this.timestamp = timestamp;
  }
  
  public void setSalt(long salt) {
    this.salt = salt;
  }
  
  public void setSignatures(Map<String, byte[]> signatures) {
    this.signatures = signatures;
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
    return "ClientCommand(command=" + getCommand() + ", timestamp=" + getTimestamp() + ", salt=" + getSalt() + ", signatures=" + getSignatures() + ", signedPreview=" + isSignedPreview() + ", chain=" + getChain() + ", seenMessages=" + getSeenMessages() + ")";
  }
  
  public ClientCommand() {}
  
  public ClientCommand(String command, long timestamp, long salt, Map<String, byte[]> signatures, boolean signedPreview, ChatChain chain, SeenMessages seenMessages) {
    this.command = command;
    this.timestamp = timestamp;
    this.salt = salt;
    this.signatures = signatures;
    this.signedPreview = signedPreview;
    this.chain = chain;
    this.seenMessages = seenMessages;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ClientCommand))
      return false; 
    ClientCommand other = (ClientCommand)o;
    if (!other.canEqual(this))
      return false; 
    if (getTimestamp() != other.getTimestamp())
      return false; 
    if (getSalt() != other.getSalt())
      return false; 
    if (isSignedPreview() != other.isSignedPreview())
      return false; 
    Object this$command = getCommand(), other$command = other.getCommand();
    if ((this$command == null) ? (other$command != null) : !this$command.equals(other$command))
      return false; 
    Object<String, byte[]> this$signatures = (Object<String, byte[]>)getSignatures(), other$signatures = (Object<String, byte[]>)other.getSignatures();
    if ((this$signatures == null) ? (other$signatures != null) : !this$signatures.equals(other$signatures))
      return false; 
    Object this$chain = getChain(), other$chain = other.getChain();
    if ((this$chain == null) ? (other$chain != null) : !this$chain.equals(other$chain))
      return false; 
    Object this$seenMessages = getSeenMessages(), other$seenMessages = other.getSeenMessages();
    return !((this$seenMessages == null) ? (other$seenMessages != null) : !this$seenMessages.equals(other$seenMessages));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ClientCommand;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    long $timestamp = getTimestamp();
    result = result * 59 + (int)($timestamp >>> 32L ^ $timestamp);
    long $salt = getSalt();
    result = result * 59 + (int)($salt >>> 32L ^ $salt);
    result = result * 59 + (isSignedPreview() ? 79 : 97);
    Object $command = getCommand();
    result = result * 59 + (($command == null) ? 43 : $command.hashCode());
    Object<String, byte[]> $signatures = (Object<String, byte[]>)getSignatures();
    result = result * 59 + (($signatures == null) ? 43 : $signatures.hashCode());
    Object $chain = getChain();
    result = result * 59 + (($chain == null) ? 43 : $chain.hashCode());
    Object $seenMessages = getSeenMessages();
    return result * 59 + (($seenMessages == null) ? 43 : $seenMessages.hashCode());
  }
  
  public String getCommand() {
    return this.command;
  }
  
  public long getTimestamp() {
    return this.timestamp;
  }
  
  public long getSalt() {
    return this.salt;
  }
  
  public Map<String, byte[]> getSignatures() {
    return this.signatures;
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
    this.command = readString(buf);
    this.timestamp = buf.readLong();
    this.salt = buf.readLong();
    int cnt = readVarInt(buf);
    Preconditions.checkArgument((cnt <= 8), "Too many signatures");
    this.signatures = (Map)new HashMap<>(cnt);
    for (int i = 0; i < cnt; i++) {
      byte[] signature;
      String name = readString(buf, 16);
      if (protocolVersion >= 761) {
        signature = new byte[256];
        buf.readBytes(signature);
      } else {
        signature = readArray(buf);
      } 
      this.signatures.put(name, signature);
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
    writeString(this.command, buf);
    buf.writeLong(this.timestamp);
    buf.writeLong(this.salt);
    writeVarInt(this.signatures.size(), buf);
    for (Map.Entry<String, byte[]> entry : this.signatures.entrySet()) {
      writeString(entry.getKey(), buf);
      if (protocolVersion >= 761) {
        buf.writeBytes(entry.getValue());
        continue;
      } 
      writeArray(entry.getValue(), buf);
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
