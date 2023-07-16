package org.eclipse.aether.internal.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import javax.inject.Named;
import org.eclipse.aether.spi.io.FileProcessor;

@Named
public class DefaultFileProcessor implements FileProcessor {
  public boolean mkdirs(File directory) {
    File canonDir;
    if (directory == null)
      return false; 
    if (directory.exists())
      return false; 
    if (directory.mkdir())
      return true; 
    try {
      canonDir = directory.getCanonicalFile();
    } catch (IOException e) {
      return false;
    } 
    File parentDir = canonDir.getParentFile();
    return (parentDir != null && (mkdirs(parentDir) || parentDir.exists()) && canonDir.mkdir());
  }
  
  public void write(File target, String data) throws IOException {
    mkdirs(target.getAbsoluteFile().getParentFile());
    OutputStream out = null;
    try {
      out = new FileOutputStream(target);
      if (data != null)
        out.write(data.getBytes(StandardCharsets.UTF_8)); 
      out.close();
      out = null;
    } finally {
      try {
        if (out != null)
          out.close(); 
      } catch (IOException iOException) {}
    } 
  }
  
  public void write(File target, InputStream source) throws IOException {
    mkdirs(target.getAbsoluteFile().getParentFile());
    OutputStream out = null;
    try {
      out = new FileOutputStream(target);
      copy(out, source, (FileProcessor.ProgressListener)null);
      out.close();
      out = null;
    } finally {
      try {
        if (out != null)
          out.close(); 
      } catch (IOException iOException) {}
    } 
  }
  
  public void copy(File source, File target) throws IOException {
    copy(source, target, (FileProcessor.ProgressListener)null);
  }
  
  public long copy(File source, File target, FileProcessor.ProgressListener listener) throws IOException {
    long total = 0L;
    InputStream in = null;
    OutputStream out = null;
    try {
      in = new FileInputStream(source);
      mkdirs(target.getAbsoluteFile().getParentFile());
      out = new FileOutputStream(target);
      total = copy(out, in, listener);
      out.close();
      out = null;
      in.close();
      in = null;
    } finally {
      try {
        if (out != null)
          out.close(); 
      } catch (IOException iOException) {
        try {
          if (in != null)
            in.close(); 
        } catch (IOException iOException1) {}
      } finally {
        try {
          if (in != null)
            in.close(); 
        } catch (IOException iOException) {}
      } 
    } 
    return total;
  }
  
  private long copy(OutputStream os, InputStream is, FileProcessor.ProgressListener listener) throws IOException {
    long total = 0L;
    ByteBuffer buffer = ByteBuffer.allocate(32768);
    byte[] array = buffer.array();
    while (true) {
      int bytes = is.read(array);
      if (bytes < 0)
        break; 
      os.write(array, 0, bytes);
      total += bytes;
      if (listener != null && bytes > 0)
        try {
          buffer.rewind();
          buffer.limit(bytes);
          listener.progressed(buffer);
        } catch (Exception exception) {} 
    } 
    return total;
  }
  
  public void move(File source, File target) throws IOException {
    if (!source.renameTo(target)) {
      copy(source, target);
      target.setLastModified(source.lastModified());
      source.delete();
    } 
  }
}
