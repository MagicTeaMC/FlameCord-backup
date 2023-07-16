package org.fusesource.jansi;

public enum AnsiColors {
  Colors16("16 colors"),
  Colors256("256 colors"),
  TrueColor("24-bit colors");
  
  private final String description;
  
  AnsiColors(String description) {
    this.description = description;
  }
  
  String getDescription() {
    return this.description;
  }
}
