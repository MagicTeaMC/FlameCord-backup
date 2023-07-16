package org.eclipse.aether.internal.impl;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class SimpleDigest {
  private static final String[] HASH_ALGOS = new String[] { "SHA-1", "MD5" };
  
  private MessageDigest digest;
  
  private long hash;
  
  SimpleDigest() {
    for (String hashAlgo : HASH_ALGOS) {
      try {
        this.digest = MessageDigest.getInstance(hashAlgo);
        this.hash = 0L;
        break;
      } catch (NoSuchAlgorithmException ne) {
        this.digest = null;
        this.hash = 13L;
      } 
    } 
  }
  
  public void update(String data) {
    if (data == null || data.length() <= 0)
      return; 
    if (this.digest != null) {
      this.digest.update(data.getBytes(StandardCharsets.UTF_8));
    } else {
      this.hash = this.hash * 31L + data.hashCode();
    } 
  }
  
  public String digest() {
    if (this.digest != null) {
      StringBuilder buffer = new StringBuilder(64);
      byte[] bytes = this.digest.digest();
      for (byte aByte : bytes) {
        int b = aByte & 0xFF;
        if (b < 16)
          buffer.append('0'); 
        buffer.append(Integer.toHexString(b));
      } 
      return buffer.toString();
    } 
    return Long.toHexString(this.hash);
  }
}
