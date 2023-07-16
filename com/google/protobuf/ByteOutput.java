package com.google.protobuf;

import java.io.IOException;
import java.nio.ByteBuffer;

public abstract class ByteOutput {
  public abstract void write(byte paramByte) throws IOException;
  
  public abstract void write(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  public abstract void writeLazy(byte[] paramArrayOfbyte, int paramInt1, int paramInt2) throws IOException;
  
  public abstract void write(ByteBuffer paramByteBuffer) throws IOException;
  
  public abstract void writeLazy(ByteBuffer paramByteBuffer) throws IOException;
}
