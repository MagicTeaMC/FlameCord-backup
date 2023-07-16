package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class FloatTag extends SpecificTag {
  public final float value;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      return new FloatTag(in.readFloat());
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_Float:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    out.writeFloat(getData());
  }
  
  public FloatTag(float value) {
    this.value = value;
  }
  
  public float getData() {
    return this.value;
  }
  
  public String extraInfo() {
    return ": " + getData();
  }
  
  public String type() {
    return "TAG_Float";
  }
  
  public String tagName() {
    return "TAG_Float";
  }
  
  public int tagType() {
    return 5;
  }
  
  public float floatValue() {
    return getData();
  }
  
  public float floatValue(float defaultValue) {
    return getData();
  }
  
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof FloatTag && ((FloatTag)obj).value == this.value));
  }
  
  public int hashCode() {
    return Float.floatToRawIntBits(this.value);
  }
}
