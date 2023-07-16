package org.jline.reader;

public interface MaskingCallback {
  String display(String paramString);
  
  String history(String paramString);
}
