package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public abstract class SpecificTag extends Tag {
  public static SpecificTag read(byte type, DataInputStream in) {
    switch (type) {
      case 1:
        return ByteTag.read(in);
      case 2:
        return ShortTag.read(in);
      case 3:
        return IntTag.read(in);
      case 4:
        return LongTag.read(in);
      case 5:
        return FloatTag.read(in);
      case 6:
        return DoubleTag.read(in);
      case 7:
        return ByteArrayTag.read(in);
      case 8:
        return StringTag.read(in);
      case 9:
        return ListTag.read(in);
      case 10:
        return CompoundTag.read(in);
      case 11:
        return IntArrayTag.read(in);
      case 12:
        return LongArrayTag.read(in);
    } 
    return new ErrorTag("Unknown tag type: " + type);
  }
  
  public void writeType(DataOutputStream out) throws IOException {
    out.writeByte(tagType());
  }
  
  static void skip(byte type, DataInputStream in) {
    try {
      switch (type) {
        case 1:
          in.skipBytes(1);
          break;
        case 2:
          in.skipBytes(2);
          break;
        case 3:
          in.skipBytes(4);
          break;
        case 4:
          in.skipBytes(8);
          break;
        case 5:
          in.skipBytes(4);
          break;
        case 6:
          in.skipBytes(8);
          break;
        case 7:
          ByteArrayTag.skip(in);
          break;
        case 8:
          StringTag.skip(in);
          break;
        case 9:
          ListTag.skip(in);
          break;
        case 10:
          CompoundTag.skip(in);
          break;
        case 11:
          IntArrayTag.skip(in);
          break;
        case 12:
          LongArrayTag.skip(in);
          break;
      } 
    } catch (IOException iOException) {}
  }
  
  public SpecificTag clone() throws CloneNotSupportedException {
    SpecificTag node = (SpecificTag)super.clone();
    return node;
  }
  
  public abstract int tagType();
}
