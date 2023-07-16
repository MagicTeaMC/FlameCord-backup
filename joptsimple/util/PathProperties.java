package joptsimple.util;

import java.nio.file.Files;
import java.nio.file.Path;

public enum PathProperties {
  FILE_EXISTING("file.existing") {
    boolean accept(Path path) {
      return Files.isRegularFile(path, new java.nio.file.LinkOption[0]);
    }
  },
  DIRECTORY_EXISTING("directory.existing") {
    boolean accept(Path path) {
      return Files.isDirectory(path, new java.nio.file.LinkOption[0]);
    }
  },
  NOT_EXISTING("file.not.existing") {
    boolean accept(Path path) {
      return Files.notExists(path, new java.nio.file.LinkOption[0]);
    }
  },
  FILE_OVERWRITABLE("file.overwritable") {
    boolean accept(Path path) {
      return (FILE_EXISTING.accept(path) && WRITABLE.accept(path));
    }
  },
  READABLE("file.readable") {
    boolean accept(Path path) {
      return Files.isReadable(path);
    }
  },
  WRITABLE("file.writable") {
    boolean accept(Path path) {
      return Files.isWritable(path);
    }
  };
  
  private final String messageKey;
  
  PathProperties(String messageKey) {
    this.messageKey = messageKey;
  }
  
  String getMessageKey() {
    return this.messageKey;
  }
  
  abstract boolean accept(Path paramPath);
}
