package net.md_5.bungee.protocol;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import se.llbit.nbt.NamedTag;
import se.llbit.nbt.Tag;

public abstract class DefinedPacket {
  private static final boolean PROCESS_TRACES = Boolean.getBoolean("waterfall.bad-packet-traces");
  
  private static final BadPacketException OVERSIZED_VAR_INT_EXCEPTION = new BadPacketException("VarInt too big");
  
  private static final BadPacketException NO_MORE_BYTES_EXCEPTION = new BadPacketException("No more bytes reading varint");
  
  public static void writeString(String s, ByteBuf buf) {
    writeString(s, buf, 32767);
  }
  
  private static final int[] VARINT_EXACT_BYTE_LENGTHS = new int[33];
  
  static {
    for (int i = 0; i <= 32; i++)
      VARINT_EXACT_BYTE_LENGTHS[i] = (int)Math.ceil((31.0D - (i - 1)) / 7.0D); 
    VARINT_EXACT_BYTE_LENGTHS[32] = 1;
  }
  
  public static int varIntBytes(int value) {
    return VARINT_EXACT_BYTE_LENGTHS[Integer.numberOfLeadingZeros(value)];
  }
  
  public static void writeString(String s, ByteBuf buf, int maxLength) {
    if (s.length() > maxLength)
      throw new OverflowPacketException("Cannot send string longer than " + maxLength + " (got " + s.length() + " characters)"); 
    byte[] b = s.getBytes(Charsets.UTF_8);
    if (b.length > maxLength * 3)
      throw new OverflowPacketException("Cannot send string longer than " + (maxLength * 3) + " (got " + b.length + " bytes)"); 
    writeVarInt(b.length, buf);
    buf.writeBytes(b);
  }
  
  public static String readString(ByteBuf buf) {
    return readString(buf, 32767);
  }
  
  public static String readString(ByteBuf buf, int maxLen) {
    int len = readVarInt(buf);
    if (len > maxLen * 3) {
      if (!MinecraftDecoder.DEBUG)
        throw STRING_TOO_MANY_BYTES_EXCEPTION; 
      throw new OverflowPacketException("Cannot receive string longer than " + (maxLen * 3) + " (got " + len + " bytes)");
    } 
    String s = buf.toString(buf.readerIndex(), len, Charsets.UTF_8);
    buf.readerIndex(buf.readerIndex() + len);
    if (s.length() > maxLen) {
      if (!MinecraftDecoder.DEBUG)
        throw STRING_TOO_LONG_EXCEPTION; 
      throw new OverflowPacketException("Cannot receive string longer than " + maxLen + " (got " + s.length() + " characters)");
    } 
    return s;
  }
  
  public static void writeArray(byte[] b, ByteBuf buf) {
    if (b.length > 32767)
      throw new OverflowPacketException("Cannot send byte array longer than Short.MAX_VALUE (got " + b.length + " bytes)"); 
    writeVarInt(b.length, buf);
    buf.writeBytes(b);
  }
  
  public static byte[] toArray(ByteBuf buf) {
    byte[] ret = new byte[buf.readableBytes()];
    buf.readBytes(ret);
    return ret;
  }
  
  public static byte[] readArray(ByteBuf buf) {
    return readArray(buf, buf.readableBytes());
  }
  
  public static byte[] readArray(ByteBuf buf, int limit) {
    int len = readVarInt(buf);
    if (len > limit)
      throw new OverflowPacketException("Cannot receive byte array longer than " + limit + " (got " + len + " bytes)"); 
    byte[] ret = new byte[len];
    buf.readBytes(ret);
    return ret;
  }
  
  public static int[] readVarIntArray(ByteBuf buf) {
    int len = readVarInt(buf);
    int[] ret = new int[len];
    for (int i = 0; i < len; i++)
      ret[i] = readVarInt(buf); 
    return ret;
  }
  
  public static void writeStringArray(List<String> s, ByteBuf buf) {
    writeVarInt(s.size(), buf);
    for (String str : s)
      writeString(str, buf); 
  }
  
  public static List<String> readStringArray(ByteBuf buf) {
    int len = readVarInt(buf);
    List<String> ret = new ArrayList<>(len);
    for (int i = 0; i < len; i++)
      ret.add(readString(buf)); 
    return ret;
  }
  
  public static int readVarInt(ByteBuf input) {
    return readVarInt(input, 5);
  }
  
  public static int readVarInt(ByteBuf input, int maxBytes) {
    byte in;
    int out = 0;
    int bytes = 0;
    do {
      if (input.readableBytes() == 0)
        throw PROCESS_TRACES ? new BadPacketException("No more bytes reading varint") : NO_MORE_BYTES_EXCEPTION; 
      in = input.readByte();
      out |= (in & Byte.MAX_VALUE) << bytes++ * 7;
      if (bytes > maxBytes)
        throw PROCESS_TRACES ? new BadPacketException("VarInt too big") : OVERSIZED_VAR_INT_EXCEPTION; 
    } while ((in & 0x80) == 128);
    return out;
  }
  
  public static void writeVarInt(int value, ByteBuf output) {
    do {
      int part = value & 0x7F;
      value >>>= 7;
      if (value != 0)
        part |= 0x80; 
      output.writeByte(part);
    } while (value != 0);
  }
  
  public static void write21BitVarInt(ByteBuf buf, int value) {
    int w = (value & 0x7F | 0x80) << 16 | (value >>> 7 & 0x7F | 0x80) << 8 | value >>> 14;
    buf.writeMedium(w);
  }
  
  public static int readVarShort(ByteBuf buf) {
    int low = buf.readUnsignedShort();
    int high = 0;
    if ((low & 0x8000) != 0) {
      low &= 0x7FFF;
      high = buf.readUnsignedByte();
    } 
    return (high & 0xFF) << 15 | low;
  }
  
  public static void writeVarShort(ByteBuf buf, int toWrite) {
    int low = toWrite & 0x7FFF;
    int high = (toWrite & 0x7F8000) >> 15;
    if (high != 0)
      low |= 0x8000; 
    buf.writeShort(low);
    if (high != 0)
      buf.writeByte(high); 
  }
  
  public static void writeUUID(UUID value, ByteBuf output) {
    output.writeLong(value.getMostSignificantBits());
    output.writeLong(value.getLeastSignificantBits());
  }
  
  public static UUID readUUID(ByteBuf input) {
    return new UUID(input.readLong(), input.readLong());
  }
  
  public static void writeProperties(Property[] properties, ByteBuf buf) {
    if (properties == null) {
      writeVarInt(0, buf);
      return;
    } 
    writeVarInt(properties.length, buf);
    for (Property prop : properties) {
      writeString(prop.getName(), buf);
      writeString(prop.getValue(), buf);
      if (prop.getSignature() != null) {
        buf.writeBoolean(true);
        writeString(prop.getSignature(), buf);
      } else {
        buf.writeBoolean(false);
      } 
    } 
  }
  
  public static Property[] readProperties(ByteBuf buf) {
    Property[] properties = new Property[readVarInt(buf)];
    for (int j = 0; j < properties.length; j++) {
      String name = readString(buf);
      String value = readString(buf);
      if (buf.readBoolean()) {
        properties[j] = new Property(name, value, readString(buf));
      } else {
        properties[j] = new Property(name, value);
      } 
    } 
    return properties;
  }
  
  public static void writePublicKey(PlayerPublicKey publicKey, ByteBuf buf) {
    if (publicKey != null) {
      buf.writeBoolean(true);
      buf.writeLong(publicKey.getExpiry());
      writeArray(publicKey.getKey(), buf);
      writeArray(publicKey.getSignature(), buf);
    } else {
      buf.writeBoolean(false);
    } 
  }
  
  public static PlayerPublicKey readPublicKey(ByteBuf buf) {
    if (buf.readBoolean())
      return new PlayerPublicKey(buf.readLong(), readArray(buf, 512), readArray(buf, 4096)); 
    return null;
  }
  
  public static Tag readTag(ByteBuf input) {
    Tag tag = NamedTag.read(new DataInputStream((InputStream)new ByteBufInputStream(input)));
    Preconditions.checkArgument(!tag.isError(), "Error reading tag: %s", tag.error());
    return tag;
  }
  
  public static void writeTag(Tag tag, ByteBuf output) {
    try {
      tag.write(new DataOutputStream((OutputStream)new ByteBufOutputStream(output)));
    } catch (IOException ex) {
      throw new RuntimeException("Exception writing tag", ex);
    } 
  }
  
  public static <E extends Enum<E>> void writeEnumSet(EnumSet<E> enumset, Class<E> oclass, ByteBuf buf) {
    Enum[] arrayOfEnum = (Enum[])oclass.getEnumConstants();
    BitSet bits = new BitSet(arrayOfEnum.length);
    for (int i = 0; i < arrayOfEnum.length; i++)
      bits.set(i, enumset.contains(arrayOfEnum[i])); 
    writeFixedBitSet(bits, arrayOfEnum.length, buf);
  }
  
  public static <E extends Enum<E>> EnumSet<E> readEnumSet(Class<E> oclass, ByteBuf buf) {
    Enum[] arrayOfEnum = (Enum[])oclass.getEnumConstants();
    BitSet bits = readFixedBitSet(arrayOfEnum.length, buf);
    EnumSet<E> set = EnumSet.noneOf(oclass);
    for (int i = 0; i < arrayOfEnum.length; i++) {
      if (bits.get(i))
        set.add((E)arrayOfEnum[i]); 
    } 
    return set;
  }
  
  public static BitSet readFixedBitSet(int i, ByteBuf buf) {
    byte[] bits = new byte[i + 8 >> 3];
    buf.readBytes(bits);
    return BitSet.valueOf(bits);
  }
  
  public static void writeFixedBitSet(BitSet bits, int size, ByteBuf buf) {
    if (bits.length() > size)
      throw new OverflowPacketException("BitSet too large (expected " + size + " got " + bits.size() + ")"); 
    buf.writeBytes(Arrays.copyOf(bits.toByteArray(), size + 8 >> 3));
  }
  
  public void read(ByteBuf buf) {
    throw new UnsupportedOperationException("Packet must implement read method");
  }
  
  public void read(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    read(buf);
  }
  
  public void write(ByteBuf buf) {
    throw new UnsupportedOperationException("Packet must implement write method");
  }
  
  public void write(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    write(buf);
  }
  
  private static final OverflowPacketException STRING_TOO_LONG_EXCEPTION = new OverflowPacketException("A string was longer than allowed. For more information, launch Waterfall with -Dwaterfall.packet-decode-logging=true");
  
  private static final OverflowPacketException STRING_TOO_MANY_BYTES_EXCEPTION = new OverflowPacketException("A string had more data than allowed. For more information, launch Waterfall with -Dwaterfall.packet-decode-logging=true");
  
  public int expectedMaxLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    return -1;
  }
  
  public int expectedMinLength(ByteBuf buf, ProtocolConstants.Direction direction, int protocolVersion) {
    return 0;
  }
  
  public static byte[] v17readArray(ByteBuf buf) {
    int len = readVarShort(buf);
    Preconditions.checkArgument((len <= 2097050), "Cannot receive array longer than 2097050 (got %s bytes)", len);
    byte[] ret = new byte[len];
    buf.readBytes(ret);
    return ret;
  }
  
  public static void v17writeArray(byte[] b, ByteBuf buf, boolean allowExtended) {
    if (allowExtended) {
      Preconditions.checkArgument((b.length <= 2097050), "Cannot send array longer than 2097050 (got %s bytes)", b.length);
    } else {
      Preconditions.checkArgument((b.length <= 32767), "Cannot send array longer than Short.MAX_VALUE (got %s bytes)", b.length);
    } 
    writeVarShort(buf, b.length);
    buf.writeBytes(b);
  }
  
  public abstract void handle(AbstractPacketHandler paramAbstractPacketHandler) throws Exception;
  
  public abstract boolean equals(Object paramObject);
  
  public abstract int hashCode();
  
  public abstract String toString();
}