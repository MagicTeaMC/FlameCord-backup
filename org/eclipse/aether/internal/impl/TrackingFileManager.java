package org.eclipse.aether.internal.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TrackingFileManager {
  private static final Logger LOGGER = LoggerFactory.getLogger(TrackingFileManager.class);
  
  public Properties read(File file) {
    FileInputStream stream = null;
    try {
      if (!file.exists())
        return null; 
      stream = new FileInputStream(file);
      Properties props = new Properties();
      props.load(stream);
      return props;
    } catch (IOException e) {
      LOGGER.warn("Failed to read tracking file {}", file, e);
    } finally {
      close(stream, file);
    } 
    return null;
  }
  
  public Properties update(File file, Map<String, String> updates) {
    Properties props = new Properties();
    File directory = file.getParentFile();
    if (!directory.mkdirs() && !directory.exists()) {
      LOGGER.warn("Failed to create parent directories for tracking file {}", file);
      return props;
    } 
    RandomAccessFile raf = null;
    try {
      raf = new RandomAccessFile(file, "rw");
      if (file.canRead()) {
        byte[] buffer = new byte[(int)raf.length()];
        raf.readFully(buffer);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
        props.load(byteArrayInputStream);
      } 
      for (Map.Entry<String, String> update : updates.entrySet()) {
        if (update.getValue() == null) {
          props.remove(update.getKey());
          continue;
        } 
        props.setProperty(update.getKey(), update.getValue());
      } 
      ByteArrayOutputStream stream = new ByteArrayOutputStream(2048);
      LOGGER.debug("Writing tracking file {}", file);
      props.store(stream, "NOTE: This is a Maven Resolver internal implementation file, its format can be changed without prior notice.");
      raf.seek(0L);
      raf.write(stream.toByteArray());
      raf.setLength(raf.getFilePointer());
    } catch (IOException e) {
      LOGGER.warn("Failed to write tracking file {}", file, e);
    } finally {
      close(raf, file);
    } 
    return props;
  }
  
  private void close(Closeable closeable, File file) {
    if (closeable != null)
      try {
        closeable.close();
      } catch (IOException e) {
        LOGGER.warn("Error closing tracking file {}", file, e);
      }  
  }
}
