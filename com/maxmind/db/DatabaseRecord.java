package com.maxmind.db;

import java.net.InetAddress;

public final class DatabaseRecord<T> {
  private final T data;
  
  private final Network network;
  
  public DatabaseRecord(T data, InetAddress ipAddress, int prefixLength) {
    this.data = data;
    this.network = new Network(ipAddress, prefixLength);
  }
  
  public T getData() {
    return this.data;
  }
  
  public Network getNetwork() {
    return this.network;
  }
}
