package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ByteTag extends SpecificTag {
  public final int value;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      return new ByteTag(in.readByte());
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_Byte:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    out.writeByte(getData());
  }
  
  public ByteTag(int value) {
    this.value = value;
  }
  
  public int getData() {
    return this.value;
  }
  
  public String extraInfo() {
    return ": " + getData();
  }
  
  public String type() {
    return "TAG_Byte";
  }
  
  public String tagName() {
    return "TAG_Byte";
  }
  
  public int tagType() {
    return 1;
  }
  
  public boolean boolValue() {
    return (getData() != 0);
  }
  
  public boolean boolValue(boolean defaultValue) {
    return (getData() != 0);
  }
  
  public int byteValue() {
    return getData();
  }
  
  public int byteValue(int defaultValue) {
    return getData();
  }
  
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof ByteTag && ((ByteTag)obj).value == this.value));
  }
  
  public int hashCode() {
    return this.value;
  }
}
