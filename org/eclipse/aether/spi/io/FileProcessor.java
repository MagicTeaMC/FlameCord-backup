package org.eclipse.aether.spi.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public interface FileProcessor {
  boolean mkdirs(File paramFile);
  
  void write(File paramFile, String paramString) throws IOException;
  
  void write(File paramFile, InputStream paramInputStream) throws IOException;
  
  void move(File paramFile1, File paramFile2) throws IOException;
  
  void copy(File paramFile1, File paramFile2) throws IOException;
  
  long copy(File paramFile1, File paramFile2, ProgressListener paramProgressListener) throws IOException;
  
  public static interface ProgressListener {
    void progressed(ByteBuffer param1ByteBuffer) throws IOException;
  }
}
