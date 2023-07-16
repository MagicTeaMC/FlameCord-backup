package net.md_5.bungee.protocol.packet;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.util.Arrays;
import java.util.Locale;
import net.md_5.bungee.protocol.AbstractPacketHandler;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.ProtocolConstants;

public class PluginMessage extends DefinedPacket {
  public void setTag(String tag) {
    this.tag = tag;
  }
  
  public void setAllowExtendedPacket(boolean allowExtendedPacket) {
    this.allowExtendedPacket = allowExtendedPacket;
  }
  
  public String toString() {
    return "PluginMessage(tag=" + getTag() + ", data=" + Arrays.toString(getData()) + ", allowExtendedPacket=" + isAllowExtendedPacket() + ")";
  }
  
  public PluginMessage() {
    this.allowExtendedPacket = false;
  }
  
  public PluginMessage(String tag, byte[] data, boolean allowExtendedPacket) {
    this.allowExtendedPacket = false;
    this.tag = tag;
    this.data = data;
    this.allowExtendedPacket = allowExtendedPacket;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PluginMessage))
      return false; 
    PluginMessage other = (PluginMessage)o;
    if (!other.canEqual(this))
      return false; 
    if (isAllowExtendedPacket() != other.isAllowExtendedPacket())
      return false; 
    Object this$tag = getTag(), other$tag = other.getTag();
    return ((this$tag == null) ? (other$tag != null) : !this$tag.equals(other$tag)) ? false : (!!Arrays.equals(getData(), other.getData()));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PluginMessage;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    result = result * 59 + (isAllowExtendedPacket() ? 79 : 97);
    Object $tag = getTag();
    result = result * 59 + (($tag == null) ? 43 : $tag.hashCode());
    return result * 59 + Arrays.hashCode(getData());
  }
  
  public static final Function<String, String> MODERNISE = new Function<String, String>() {
      public String apply(String tag) {
        if (tag.equals("BungeeCord"))
          return "bungeecord:main"; 
        if (tag.equals("bungeecord:main"))
          return "BungeeCord"; 
        if (tag.indexOf(':') != -1)
          return tag; 
        return "legacy:" + tag.toLowerCase(Locale.ROOT);
      }
    };
  
  private String tag;
  
  private byte[] data;
  
  private boolean allowExtendedPacket;
  
  public PluginMessage(String tag, ByteBuf data, boolean allowExtendedPacket) {
    this(tag, ByteBufUtil.getBytes(data), allowExtendedPacket);
  }
  
  public String getTag() {
    return this.tag;
  }
  
  public byte[] getData() {
    return this.data;
  }
  
  public void setData(byte[] data) {
    this.data = (byte[])Preconditions.checkNotNull(data, "Null data");
  }
  
  public void setData(ByteBuf buf) {
    Preconditions.checkNotNull(buf, "Null buffer");
    setData(ByteBufUtil.getBytes(buf));
  }
  
  public boolean isAllowExtendedPacket() {
    return this.allowExtendedPacket;
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      this.tag = readString(buf);
      this.data = v17readArray(buf);
      return;
    } 
    this.tag = (protocolVersion >= 393) ? (String)MODERNISE.apply(readString(buf)) : readString(buf, 20);
    int maxSize = (direction == ProtocolConstants.Direction.TO_SERVER) ? 32767 : 1048576;
    Preconditions.checkArgument((buf.readableBytes() <= maxSize), "Payload too large");
    this.data = new byte[buf.readableBytes()];
    buf.readBytes(this.data);
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    if (ProtocolConstants.isBeforeOrEq(protocolVersion, 5)) {
      writeString(this.tag, buf);
      v17writeArray(this.data, buf, this.allowExtendedPacket);
      return;
    } 
    writeString((protocolVersion >= 393) ? (String)MODERNISE.apply(this.tag) : this.tag, buf);
    buf.writeBytes(this.data);
  }
  
  public void handle(AbstractPacketHandler handler) throws Exception {
    handler.handle(this);
  }
  
  public DataInput getStream() {
    return new DataInputStream(new ByteArrayInputStream(this.data));
  }
}
