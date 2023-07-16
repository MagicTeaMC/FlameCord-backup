package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class LongArrayTag extends SpecificTag {
  public final long[] value;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      int length = in.readInt();
      long[] data = new long[length];
      for (int i = 0; i < length; i++)
        data[i] = in.readLong(); 
      return new LongArrayTag(data);
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_Long_Array:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    out.writeInt(this.value.length);
    for (int i = 0; i < this.value.length; i++)
      out.writeLong(this.value[i]); 
  }
  
  static void skip(DataInputStream in) {
    try {
      int length = in.readInt();
      in.skipBytes(length * 8);
    } catch (IOException iOException) {}
  }
  
  public LongArrayTag(long[] data) {
    this.value = data;
  }
  
  public long[] getData() {
    return this.value;
  }
  
  public String extraInfo() {
    return ": " + this.value.length;
  }
  
  public String tagName() {
    return "TAG_Long_Array";
  }
  
  public String type() {
    return "TAG_Long_Array";
  }
  
  public int tagType() {
    return 12;
  }
  
  public long[] longArray() {
    return this.value;
  }
  
  public long[] longArray(long[] defaultValue) {
    return this.value;
  }
  
  public boolean isLongArray(int size) {
    return (this.value.length >= size);
  }
  
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof LongArrayTag && 
      Arrays.equals(((LongArrayTag)obj).value, this.value)));
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.value);
  }
}
