package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class IntArrayTag extends SpecificTag {
  public final int[] value;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      int length = in.readInt();
      int[] data = new int[length];
      for (int i = 0; i < length; i++)
        data[i] = in.readInt(); 
      return new IntArrayTag(data);
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_Int_Array:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    out.writeInt(this.value.length);
    for (int i = 0; i < this.value.length; i++)
      out.writeInt(this.value[i]); 
  }
  
  static void skip(DataInputStream in) {
    try {
      int length = in.readInt();
      in.skipBytes(length * 4);
    } catch (IOException iOException) {}
  }
  
  public IntArrayTag(int[] data) {
    this.value = data;
  }
  
  public int[] getData() {
    return this.value;
  }
  
  public String extraInfo() {
    return ": " + this.value.length;
  }
  
  public String tagName() {
    return "TAG_Int_Array";
  }
  
  public String type() {
    return "TAG_Int_Array";
  }
  
  public int tagType() {
    return 11;
  }
  
  public int[] intArray() {
    return this.value;
  }
  
  public int[] intArray(int[] defaultValue) {
    return this.value;
  }
  
  public boolean isIntArray(int size) {
    return (this.value.length >= size);
  }
  
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof IntArrayTag && 
      Arrays.equals(((IntArrayTag)obj).value, this.value)));
  }
  
  public int hashCode() {
    return Arrays.hashCode(this.value);
  }
}
