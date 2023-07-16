package se.llbit.nbt;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class LongTag extends SpecificTag {
  public final long value;
  
  public static SpecificTag read(DataInputStream in) {
    try {
      return new LongTag(in.readLong());
    } catch (IOException e) {
      return new ErrorTag("IOException while reading TAG_Long:\n" + e.getMessage());
    } 
  }
  
  public void write(DataOutputStream out) throws IOException {
    out.writeLong(getData());
  }
  
  public LongTag(long value) {
    this.value = value;
  }
  
  public long getData() {
    return this.value;
  }
  
  public String extraInfo() {
    return ": " + getData();
  }
  
  public String type() {
    return "TAG_Long";
  }
  
  public String tagName() {
    return "TAG_Long";
  }
  
  public int tagType() {
    return 4;
  }
  
  public boolean boolValue() {
    return (getData() != 0L);
  }
  
  public boolean boolValue(boolean defaultValue) {
    return (getData() != 0L);
  }
  
  public long longValue() {
    return getData();
  }
  
  public long longValue(long defaultValue) {
    return getData();
  }
  
  public boolean equals(Object obj) {
    return (this == obj || (obj instanceof LongTag && ((LongTag)obj).value == this.value));
  }
  
  public int hashCode() {
    return (int)this.value;
  }
}
