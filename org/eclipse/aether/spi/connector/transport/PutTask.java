package org.eclipse.aether.spi.connector.transport;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public final class PutTask extends TransportTask {
  private File dataFile;
  
  private byte[] dataBytes = EMPTY;
  
  public PutTask(URI location) {
    setLocation(location);
  }
  
  public InputStream newInputStream() throws IOException {
    if (this.dataFile != null)
      return new FileInputStream(this.dataFile); 
    return new ByteArrayInputStream(this.dataBytes);
  }
  
  public long getDataLength() {
    if (this.dataFile != null)
      return this.dataFile.length(); 
    return this.dataBytes.length;
  }
  
  public File getDataFile() {
    return this.dataFile;
  }
  
  public PutTask setDataFile(File dataFile) {
    this.dataFile = dataFile;
    this.dataBytes = EMPTY;
    return this;
  }
  
  public PutTask setDataBytes(byte[] bytes) {
    this.dataBytes = (bytes != null) ? bytes : EMPTY;
    this.dataFile = null;
    return this;
  }
  
  public PutTask setDataString(String str) {
    return setDataBytes((str != null) ? str.getBytes(StandardCharsets.UTF_8) : null);
  }
  
  public PutTask setListener(TransportListener listener) {
    super.setListener(listener);
    return this;
  }
  
  public String toString() {
    return ">> " + getLocation();
  }
}
