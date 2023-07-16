package net.md_5.bungee.protocol;

import java.util.Arrays;

public class PlayerPublicKey {
  private final long expiry;
  
  private final byte[] key;
  
  private final byte[] signature;
  
  public PlayerPublicKey(long expiry, byte[] key, byte[] signature) {
    this.expiry = expiry;
    this.key = key;
    this.signature = signature;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PlayerPublicKey))
      return false; 
    PlayerPublicKey other = (PlayerPublicKey)o;
    return !other.canEqual(this) ? false : ((getExpiry() != other.getExpiry()) ? false : (!Arrays.equals(getKey(), other.getKey()) ? false : (!!Arrays.equals(getSignature(), other.getSignature()))));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PlayerPublicKey;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    long $expiry = getExpiry();
    result = result * 59 + (int)($expiry >>> 32L ^ $expiry);
    result = result * 59 + Arrays.hashCode(getKey());
    return result * 59 + Arrays.hashCode(getSignature());
  }
  
  public String toString() {
    return "PlayerPublicKey(expiry=" + getExpiry() + ", key=" + Arrays.toString(getKey()) + ", signature=" + Arrays.toString(getSignature()) + ")";
  }
  
  public long getExpiry() {
    return this.expiry;
  }
  
  public byte[] getKey() {
    return this.key;
  }
  
  public byte[] getSignature() {
    return this.signature;
  }
}
