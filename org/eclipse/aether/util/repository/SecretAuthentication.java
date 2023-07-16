package org.eclipse.aether.util.repository;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.AuthenticationContext;
import org.eclipse.aether.repository.AuthenticationDigest;

final class SecretAuthentication implements Authentication {
  private static final Object[] KEYS = new Object[16];
  
  private final String key;
  
  private final char[] value;
  
  private final int secretHash;
  
  static {
    for (int i = 0; i < KEYS.length; i++)
      KEYS[i] = new Object(); 
  }
  
  SecretAuthentication(String key, String value) {
    this((value != null) ? value.toCharArray() : null, key);
  }
  
  SecretAuthentication(String key, char[] value) {
    this(copy(value), key);
  }
  
  private SecretAuthentication(char[] value, String key) {
    this.key = Objects.<String>requireNonNull(key, "authentication key cannot be null");
    if (key.length() == 0)
      throw new IllegalArgumentException("authentication key cannot be empty"); 
    this.secretHash = Arrays.hashCode(value) ^ KEYS[0].hashCode();
    this.value = xor(value);
  }
  
  private static char[] copy(char[] chars) {
    return (chars != null) ? (char[])chars.clone() : null;
  }
  
  private char[] xor(char[] chars) {
    if (chars != null) {
      int mask = System.identityHashCode(this);
      for (int i = 0; i < chars.length; i++) {
        int key = KEYS[(i >> 1) % KEYS.length].hashCode();
        key ^= mask;
        chars[i] = (char)(chars[i] ^ (((i & 0x1) == 0) ? (key & 0xFFFF) : (key >>> 16)));
      } 
    } 
    return chars;
  }
  
  private static void clear(char[] chars) {
    if (chars != null)
      for (int i = 0; i < chars.length; i++)
        chars[i] = Character.MIN_VALUE;  
  }
  
  public void fill(AuthenticationContext context, String key, Map<String, String> data) {
    char[] secret = copy(this.value);
    xor(secret);
    context.put(this.key, secret);
  }
  
  public void digest(AuthenticationDigest digest) {
    char[] secret = copy(this.value);
    try {
      xor(secret);
      digest.update(new String[] { this.key });
      digest.update(secret);
    } finally {
      clear(secret);
    } 
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (obj == null || !getClass().equals(obj.getClass()))
      return false; 
    SecretAuthentication that = (SecretAuthentication)obj;
    if (!Objects.equals(this.key, that.key) || this.secretHash != that.secretHash)
      return false; 
    char[] secret = copy(this.value);
    char[] thatSecret = copy(that.value);
    try {
      xor(secret);
      that.xor(thatSecret);
      return Arrays.equals(secret, thatSecret);
    } finally {
      clear(secret);
      clear(thatSecret);
    } 
  }
  
  public int hashCode() {
    int hash = 17;
    hash = hash * 31 + this.key.hashCode();
    hash = hash * 31 + this.secretHash;
    return hash;
  }
  
  public String toString() {
    return this.key + "=" + ((this.value != null) ? "***" : "null");
  }
}
