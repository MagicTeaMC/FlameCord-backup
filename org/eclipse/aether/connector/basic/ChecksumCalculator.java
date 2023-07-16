package org.eclipse.aether.connector.basic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.aether.spi.connector.layout.RepositoryLayout;
import org.eclipse.aether.util.ChecksumUtils;

final class ChecksumCalculator {
  private final List<Checksum> checksums;
  
  private final File targetFile;
  
  static class Checksum {
    final String algorithm;
    
    final MessageDigest digest;
    
    Exception error;
    
    Checksum(String algorithm) {
      this.algorithm = algorithm;
      MessageDigest digest = null;
      try {
        digest = MessageDigest.getInstance(algorithm);
      } catch (NoSuchAlgorithmException e) {
        this.error = e;
      } 
      this.digest = digest;
    }
    
    public void update(ByteBuffer buffer) {
      if (this.digest != null)
        this.digest.update(buffer); 
    }
    
    public void reset() {
      if (this.digest != null) {
        this.digest.reset();
        this.error = null;
      } 
    }
    
    public void error(Exception error) {
      if (this.digest != null)
        this.error = error; 
    }
    
    public Object get() {
      if (this.error != null)
        return this.error; 
      return ChecksumUtils.toHexString(this.digest.digest());
    }
  }
  
  public static ChecksumCalculator newInstance(File targetFile, Collection<RepositoryLayout.Checksum> checksums) {
    if (checksums == null || checksums.isEmpty())
      return null; 
    return new ChecksumCalculator(targetFile, checksums);
  }
  
  private ChecksumCalculator(File targetFile, Collection<RepositoryLayout.Checksum> checksums) {
    this.checksums = new ArrayList<>();
    Set<String> algos = new HashSet<>();
    for (RepositoryLayout.Checksum checksum : checksums) {
      String algo = checksum.getAlgorithm();
      if (algos.add(algo))
        this.checksums.add(new Checksum(algo)); 
    } 
    this.targetFile = targetFile;
  }
  
  public void init(long dataOffset) {
    for (Checksum checksum : this.checksums)
      checksum.reset(); 
    if (dataOffset <= 0L)
      return; 
    InputStream in = null;
    try {
      in = new FileInputStream(this.targetFile);
      long total = 0L;
      ByteBuffer buffer = ByteBuffer.allocate(32768);
      for (byte[] array = buffer.array(); total < dataOffset; ) {
        int read = in.read(array);
        if (read < 0)
          throw new IOException(this.targetFile + " contains only " + total + " bytes, cannot resume download from offset " + dataOffset); 
        total += read;
        if (total > dataOffset)
          read = (int)(read - total - dataOffset); 
        buffer.rewind();
        buffer.limit(read);
        update(buffer);
      } 
      in.close();
      in = null;
    } catch (IOException e) {
      for (Checksum checksum : this.checksums)
        checksum.error(e); 
    } finally {
      try {
        if (in != null)
          in.close(); 
      } catch (IOException iOException) {}
    } 
  }
  
  public void update(ByteBuffer data) {
    for (Checksum checksum : this.checksums) {
      data.mark();
      checksum.update(data);
      data.reset();
    } 
  }
  
  public Map<String, Object> get() {
    Map<String, Object> results = new HashMap<>();
    for (Checksum checksum : this.checksums)
      results.put(checksum.algorithm, checksum.get()); 
    return results;
  }
}
