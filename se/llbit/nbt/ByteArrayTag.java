package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class ByteArrayTag extends SpecificTag {
  public final byte[] value;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      int length = in.readInt();
      byte[] data = new byte[length];
      in.readFully(data, 0, length);
      return new ByteArrayTag(data);
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_Byte_Array:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    out.writeInt((getData()).length);
    out.write(getData());
  }
  
  static void skip(DataInputStream in) {
    try {
      int length = in.readInt();
      in.skipBytes(length);
    } catch (IOException iOException) {}
  }
  
  public ByteArrayTag(byte[] value) {
    this.value = value;
  }
  
  public byte[] getData() {
    return this.value;
  }
  
  public String extraInfo() {
    return ": " + this.value.length;
  }
  
  public String type() {
    return "TAG_Byte_Array";
  }
  
  public String tagName() {
    return "TAG_Byte_Array";
  }
  
  public int tagType() {
    return 7;
  }
  
  public byte[] byteArray() {
    return getData();
  }
  
  public byte[] byteArray(byte[] defaultValue) {
    return getData();
  }
  
  public boolean isByteArray(int size) {
    return ((getData()).length >= size);
  }
  
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof ByteArrayTag && 
      Arrays.equals(((ByteArrayTag)obj).value, this.value)));
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.value);
  }
}
