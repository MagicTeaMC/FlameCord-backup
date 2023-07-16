package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DoubleTag extends SpecificTag {
  public final double value;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      return new DoubleTag(in.readDouble());
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_Double:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    out.writeDouble(getData());
  }
  
  public DoubleTag(double value) {
    this.value = value;
  }
  
  public double getData() {
    return this.value;
  }
  
  public String extraInfo() {
    return ": " + getData();
  }
  
  public String type() {
    return "TAG_Double";
  }
  
  public String tagName() {
    return "TAG_Double";
  }
  
  public int tagType() {
    return 6;
  }
  
  public double doubleValue() {
    return getData();
  }
  
  public double doubleValue(double defaultValue) {
    return getData();
  }
  
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof DoubleTag && ((DoubleTag)obj).value == this.value));
  }
  
  public int hashCode() {
    long x = Double.doubleToRawLongBits(this.value);
    return (int)(x >> 32L ^ x);
  }
}
