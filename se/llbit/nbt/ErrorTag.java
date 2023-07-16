package se.llbit.nbt;

import java.io.DataOutputStream;

public class ErrorTag extends SpecificTag {
  public final String message;
  
  public void write(DataOutputStream out) {
    throw new RuntimeException("Cannot write an error tag to NBT stream (" + getError() + ")");
  }
  
  public ErrorTag(String message) {
    this.message = message;
  }
  
  public String getError() {
    return (this.message != null) ? this.message : "";
  }
  
  public String extraInfo() {
    return ": \"" + getError() + '"';
  }
  
  public String type() {
    return "Tag.Error";
  }
  
  public int tagType() {
    throw new Error("Cannot write an error tag to NBT stream (" + getError() + ")");
  }
  
  public String tagName() {
    return "Tag.Error";
  }
  
  public boolean isEnd() {
    return true;
  }
  
  public boolean isError() {
    return true;
  }
  
  public String error() {
    return getError();
  }
  
  public boolean equals(Object obj) {
    return false;
  }
}
