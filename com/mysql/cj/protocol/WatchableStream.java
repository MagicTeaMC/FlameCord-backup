package com.mysql.cj.protocol;

public interface WatchableStream {
  void setWatcher(OutputStreamWatcher paramOutputStreamWatcher);
  
  int size();
  
  byte[] toByteArray();
  
  void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2);
}
