package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class ShortTag extends SpecificTag {
  public final short value;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      return new ShortTag(in.readShort());
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_Short:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    out.writeShort(getData());
  }
  
  public ShortTag(short value) {
    this.value = value;
  }
  
  public short getData() {
    return this.value;
  }
  
  public String extraInfo() {
    return ": " + getData();
  }
  
  public String type() {
    return "TAG_Short";
  }
  
  public String tagName() {
    return "TAG_Short";
  }
  
  public int tagType() {
    return 2;
  }
  
  public boolean boolValue() {
    return (getData() != 0);
  }
  
  public boolean boolValue(boolean defaultValue) {
    return (getData() != 0);
  }
  
  public short shortValue() {
    return getData();
  }
  
  public short shortValue(short defaultValue) {
    return getData();
  }
  
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof ShortTag && ((ShortTag)obj).value == this.value));
  }
  
  public int hashCode() {
    return this.value;
  }
}
