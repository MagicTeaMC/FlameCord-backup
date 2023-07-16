package org.eclipse.aether.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ChecksumUtils {
  public static String read(File checksumFile) throws IOException {
    String checksum = "";
    try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(checksumFile), StandardCharsets.UTF_8), 512)) {
      while (true) {
        String line = br.readLine();
        if (line == null)
          break; 
        line = line.trim();
        if (line.length() > 0) {
          checksum = line;
          break;
        } 
      } 
    } 
    if (checksum.matches(".+= [0-9A-Fa-f]+")) {
      int lastSpacePos = checksum.lastIndexOf(' ');
      checksum = checksum.substring(lastSpacePos + 1);
    } else {
      int spacePos = checksum.indexOf(' ');
      if (spacePos != -1)
        checksum = checksum.substring(0, spacePos); 
    } 
    return checksum;
  }
  
  public static Map<String, Object> calc(File dataFile, Collection<String> algos) throws IOException {
    return calc(new FileInputStream(dataFile), algos);
  }
  
  public static Map<String, Object> calc(byte[] dataBytes, Collection<String> algos) throws IOException {
    return calc(new ByteArrayInputStream(dataBytes), algos);
  }
  
  private static Map<String, Object> calc(InputStream data, Collection<String> algos) throws IOException {
    Map<String, Object> results = new LinkedHashMap<>();
    Map<String, MessageDigest> digests = new LinkedHashMap<>();
    for (String algo : algos) {
      try {
        digests.put(algo, MessageDigest.getInstance(algo));
      } catch (NoSuchAlgorithmException e) {
        results.put(algo, e);
      } 
    } 
    try (InputStream in = data) {
      byte[] buffer = new byte[32768];
      while (true) {
        int read = in.read(buffer);
        if (read < 0)
          break; 
        for (MessageDigest digest : digests.values())
          digest.update(buffer, 0, read); 
      } 
    } 
    for (Map.Entry<String, MessageDigest> entry : digests.entrySet()) {
      byte[] bytes = ((MessageDigest)entry.getValue()).digest();
      results.put(entry.getKey(), toHexString(bytes));
    } 
    return results;
  }
  
  public static String toHexString(byte[] bytes) {
    if (bytes == null)
      return null; 
    StringBuilder buffer = new StringBuilder(bytes.length * 2);
    for (byte aByte : bytes) {
      int b = aByte & 0xFF;
      if (b < 16)
        buffer.append('0'); 
      buffer.append(Integer.toHexString(b));
    } 
    return buffer.toString();
  }
}
