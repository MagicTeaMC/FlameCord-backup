package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class IntTag extends SpecificTag {
  public final int value;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      return new IntTag(in.readInt());
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_Int:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    out.writeInt(this.value);
  }
  
  public IntTag(boolean boolValue) {
    this(boolValue ? 1 : 0);
  }
  
  public IntTag(int value) {
    this.value = value;
  }
  
  public int getData() {
    return this.value;
  }
  
  public String extraInfo() {
    return ": " + getData();
  }
  
  public String type() {
    return "TAG_Int";
  }
  
  public String tagName() {
    return "TAG_Int";
  }
  
  public int tagType() {
    return 3;
  }
  
  public boolean boolValue() {
    return (getData() != 0);
  }
  
  public boolean boolValue(boolean defaultValue) {
    return (getData() != 0);
  }
  
  public int intValue() {
    return getData();
  }
  
  public int intValue(int defaultValue) {
    return getData();
  }
  
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof IntTag && ((IntTag)obj).value == this.value));
  }
  
  public int hashCode() {
    return this.value;
  }
}
