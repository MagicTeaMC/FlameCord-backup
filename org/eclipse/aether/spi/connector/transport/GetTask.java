package org.eclipse.aether.spi.connector.transport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class GetTask extends TransportTask {
  private File dataFile;
  
  private boolean resume;
  
  private ByteArrayOutputStream dataBytes;
  
  private Map<String, String> checksums;
  
  public GetTask(URI location) {
    this.checksums = Collections.emptyMap();
    setLocation(location);
  }
  
  public OutputStream newOutputStream() throws IOException {
    return newOutputStream(false);
  }
  
  public OutputStream newOutputStream(boolean resume) throws IOException {
    if (this.dataFile != null)
      return new FileOutputStream(this.dataFile, (this.resume && resume)); 
    if (this.dataBytes == null) {
      this.dataBytes = new ByteArrayOutputStream(1024);
    } else if (!resume) {
      this.dataBytes.reset();
    } 
    return this.dataBytes;
  }
  
  public File getDataFile() {
    return this.dataFile;
  }
  
  public GetTask setDataFile(File dataFile) {
    return setDataFile(dataFile, false);
  }
  
  public GetTask setDataFile(File dataFile, boolean resume) {
    this.dataFile = dataFile;
    this.resume = resume;
    return this;
  }
  
  public long getResumeOffset() {
    if (this.resume) {
      if (this.dataFile != null)
        return this.dataFile.length(); 
      if (this.dataBytes != null)
        return this.dataBytes.size(); 
    } 
    return 0L;
  }
  
  public byte[] getDataBytes() {
    if (this.dataFile != null || this.dataBytes == null)
      return EMPTY; 
    return this.dataBytes.toByteArray();
  }
  
  public String getDataString() {
    if (this.dataFile != null || this.dataBytes == null)
      return ""; 
    return new String(this.dataBytes.toByteArray(), StandardCharsets.UTF_8);
  }
  
  public GetTask setListener(TransportListener listener) {
    super.setListener(listener);
    return this;
  }
  
  public Map<String, String> getChecksums() {
    return this.checksums;
  }
  
  public GetTask setChecksum(String algorithm, String value) {
    if (algorithm != null) {
      if (this.checksums.isEmpty())
        this.checksums = new HashMap<>(); 
      if (value != null && value.length() > 0) {
        this.checksums.put(algorithm, value);
      } else {
        this.checksums.remove(algorithm);
      } 
    } 
    return this;
  }
  
  public String toString() {
    return "<< " + getLocation();
  }
}
