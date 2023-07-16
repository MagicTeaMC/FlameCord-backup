package com.maxmind.db;

import java.net.InetAddress;
import java.net.UnknownHostException;

public final class Network {
  private final InetAddress ipAddress;
  
  private final int prefixLength;
  
  private InetAddress networkAddress = null;
  
  public Network(InetAddress ipAddress, int prefixLength) {
    this.ipAddress = ipAddress;
    this.prefixLength = prefixLength;
  }
  
  public InetAddress getNetworkAddress() {
    if (this.networkAddress != null)
      return this.networkAddress; 
    byte[] ipBytes = this.ipAddress.getAddress();
    byte[] networkBytes = new byte[ipBytes.length];
    int curPrefix = this.prefixLength;
    for (int i = 0; i < ipBytes.length && curPrefix > 0; i++) {
      byte b = ipBytes[i];
      if (curPrefix < 8) {
        int shiftN = 8 - curPrefix;
        b = (byte)(b >> shiftN << shiftN);
      } 
      networkBytes[i] = b;
      curPrefix -= 8;
    } 
    try {
      this.networkAddress = InetAddress.getByAddress(networkBytes);
    } catch (UnknownHostException e) {
      throw new RuntimeException("Illegal network address byte length of " + networkBytes.length);
    } 
    return this.networkAddress;
  }
  
  public int getPrefixLength() {
    return this.prefixLength;
  }
  
  public String toString() {
    return getNetworkAddress().getHostAddress() + "/" + this.prefixLength;
  }
}
