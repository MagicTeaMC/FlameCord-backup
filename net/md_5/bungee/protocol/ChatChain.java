package net.md_5.bungee.protocol;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class ChatChain extends DefinedPacket {
  private List<ChainLink> seen;
  
  private List<ChainLink> received;
  
  public void setSeen(List<ChainLink> seen) {
    this.seen = seen;
  }
  
  public void setReceived(List<ChainLink> received) {
    this.received = received;
  }
  
  public String toString() {
    return "ChatChain(seen=" + getSeen() + ", received=" + getReceived() + ")";
  }
  
  public ChatChain() {}
  
  public ChatChain(List<ChainLink> seen, List<ChainLink> received) {
    this.seen = seen;
    this.received = received;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ChatChain))
      return false; 
    ChatChain other = (ChatChain)o;
    if (!other.canEqual(this))
      return false; 
    Object<ChainLink> this$seen = (Object<ChainLink>)getSeen(), other$seen = (Object<ChainLink>)other.getSeen();
    if ((this$seen == null) ? (other$seen != null) : !this$seen.equals(other$seen))
      return false; 
    Object<ChainLink> this$received = (Object<ChainLink>)getReceived(), other$received = (Object<ChainLink>)other.getReceived();
    return !((this$received == null) ? (other$received != null) : !this$received.equals(other$received));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ChatChain;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object<ChainLink> $seen = (Object<ChainLink>)getSeen();
    result = result * 59 + (($seen == null) ? 43 : $seen.hashCode());
    Object<ChainLink> $received = (Object<ChainLink>)getReceived();
    return result * 59 + (($received == null) ? 43 : $received.hashCode());
  }
  
  public List<ChainLink> getSeen() {
    return this.seen;
  }
  
  public List<ChainLink> getReceived() {
    return this.received;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    this.seen = readLinks(buf);
    if (buf.readBoolean())
      this.received = readLinks(buf); 
  }
  
  private static List<ChainLink> readLinks(ByteBuf buf) {
    int cnt = readVarInt(buf);
    Preconditions.checkArgument((cnt <= 5), "Too many entries");
    List<ChainLink> chain = new LinkedList<>();
    for (int i = 0; i < cnt; i++)
      chain.add(new ChainLink(readUUID(buf), readArray(buf))); 
    return chain;
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    writeLinks(this.seen, buf);
    if (this.received != null) {
      buf.writeBoolean(true);
      writeLinks(this.received, buf);
    } else {
      buf.writeBoolean(false);
    } 
  }
  
  private static void writeLinks(List<ChainLink> links, ByteBuf buf) {
    writeVarInt(links.size(), buf);
    for (ChainLink link : links) {
      writeUUID(link.sender, buf);
      writeArray(link.signature, buf);
    } 
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    throw new UnsupportedOperationException("Not supported.");
  }
  
  public static class ChainLink {
    private final UUID sender;
    
    private final byte[] signature;
    
    public ChainLink(UUID sender, byte[] signature) {
      this.sender = sender;
      this.signature = signature;
    }
    
    public boolean equals(Object o) {
      if (o == this)
        return true; 
      if (!(o instanceof ChainLink))
        return false; 
      ChainLink other = (ChainLink)o;
      if (!other.canEqual(this))
        return false; 
      Object this$sender = getSender(), other$sender = other.getSender();
      return ((this$sender == null) ? (other$sender != null) : !this$sender.equals(other$sender)) ? false : (!!Arrays.equals(getSignature(), other.getSignature()));
    }
    
    protected boolean canEqual(Object other) {
      return other instanceof ChainLink;
    }
    
    public int hashCode() {
      int PRIME = 59;
      result = 1;
      Object $sender = getSender();
      result = result * 59 + (($sender == null) ? 43 : $sender.hashCode());
      return result * 59 + Arrays.hashCode(getSignature());
    }
    
    public String toString() {
      return "ChatChain.ChainLink(sender=" + getSender() + ", signature=" + Arrays.toString(getSignature()) + ")";
    }
    
    public UUID getSender() {
      return this.sender;
    }
    
    public byte[] getSignature() {
      return this.signature;
    }
  }
}
