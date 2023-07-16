package org.jline.reader.impl;

import java.util.Objects;
import org.jline.reader.MaskingCallback;

public final class SimpleMaskingCallback implements MaskingCallback {
  private final Character mask;
  
  public SimpleMaskingCallback(Character mask) {
    this.mask = Objects.<Character>requireNonNull(mask, "mask must be a non null character");
  }
  
  public String display(String line) {
    if (this.mask.equals(Character.valueOf(false)))
      return ""; 
    StringBuilder sb = new StringBuilder(line.length());
    for (int i = line.length(); i-- > 0;)
      sb.append(this.mask.charValue()); 
    return sb.toString();
  }
  
  public String history(String line) {
    return null;
  }
}
