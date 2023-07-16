package org.apache.maven.building;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class FileSource implements Source {
  private final File file;
  
  public FileSource(File file) {
    this.file = ((File)Objects.<File>requireNonNull(file, "file cannot be null")).getAbsoluteFile();
  }
  
  public InputStream getInputStream() throws IOException {
    return new FileInputStream(this.file);
  }
  
  public String getLocation() {
    return this.file.getPath();
  }
  
  public File getFile() {
    return this.file;
  }
  
  public String toString() {
    return getLocation();
  }
}
