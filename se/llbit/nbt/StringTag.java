package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class StringTag extends SpecificTag {
  public final String value;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      return new StringTag(in.readUTF());
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_String:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    write(out, this.value);
  }
  
  static void write(DataOutputStream out, String data) throws IOException {
    out.writeUTF(data);
  }
  
  static void skip(DataInputStream in) {
    try {
      short length = in.readShort();
      in.skipBytes(length);
    } catch (IOException iOException) {}
  }
  
  public StringTag(String value) {
    this.value = value;
  }
  
  public String getData() {
    return (this.value != null) ? this.value : "";
  }
  
  public String extraInfo() {
    return ": \"" + getData() + '"';
  }
  
  public String type() {
    return "TAG_String";
  }
  
  public String tagName() {
    return "TAG_String";
  }
  
  public int tagType() {
    return 8;
  }
  
  public String stringValue() {
    return getData();
  }
  
  public String stringValue(String defaultValue) {
    return getData();
  }
  
  public boolean same(String name) {
    return getData().equals(name);
  }
  
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof StringTag && ((StringTag)obj).value.equals(this.value)));
  }
  
  public int hashCode() {
    return this.value.hashCode();
  }
}
