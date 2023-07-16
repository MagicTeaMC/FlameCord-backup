package org.codehaus.plexus.util;

class Java7Detector {
  private static final boolean isJava7;
  
  static {
    boolean isJava7x = true;
    try {
      Class.forName("java.nio.file.Files");
    } catch (Exception e) {
      isJava7x = false;
    } 
    isJava7 = isJava7x;
  }
  
  public static boolean isJava7() {
    return isJava7;
  }
}
