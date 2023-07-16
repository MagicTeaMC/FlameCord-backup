package org.fusesource.jansi;

public enum AnsiMode {
  Strip("Strip all ansi sequences"),
  Default("Print ansi sequences if the stream is a terminal"),
  Force("Always print ansi sequences, even if the stream is redirected");
  
  private final String description;
  
  AnsiMode(String description) {
    this.description = description;
  }
  
  String getDescription() {
    return this.description;
  }
}
