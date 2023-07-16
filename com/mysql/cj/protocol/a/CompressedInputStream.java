package com.mysql.cj.protocol.a;

import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.log.Log;
import com.mysql.cj.util.StringUtils;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

public class CompressedInputStream extends InputStream {
  private byte[] buffer;
  
  private InputStream in;
  
  private Inflater inflater;
  
  private RuntimeProperty<Boolean> traceProtocol;
  
  private Log log;
  
  private byte[] packetHeaderBuffer = new byte[7];
  
  private int pos = 0;
  
  public CompressedInputStream(InputStream streamFromServer, RuntimeProperty<Boolean> traceProtocol, Log log) {
    this.traceProtocol = traceProtocol;
    this.log = log;
    this.in = streamFromServer;
    this.inflater = new Inflater();
  }
  
  public int available() throws IOException {
    if (this.buffer == null)
      return this.in.available(); 
    return this.buffer.length - this.pos + this.in.available();
  }
  
  public void close() throws IOException {
    this.in.close();
    this.buffer = null;
    this.inflater.end();
    this.inflater = null;
    this.traceProtocol = null;
    this.log = null;
  }
  
  private void getNextPacketFromServer() throws IOException {
    byte[] uncompressedData = null;
    int lengthRead = readFully(this.packetHeaderBuffer, 0, 7);
    if (lengthRead < 7)
      throw new IOException("Unexpected end of input stream"); 
    int compressedPacketLength = (this.packetHeaderBuffer[0] & 0xFF) + ((this.packetHeaderBuffer[1] & 0xFF) << 8) + ((this.packetHeaderBuffer[2] & 0xFF) << 16);
    int uncompressedLength = (this.packetHeaderBuffer[4] & 0xFF) + ((this.packetHeaderBuffer[5] & 0xFF) << 8) + ((this.packetHeaderBuffer[6] & 0xFF) << 16);
    boolean doTrace = ((Boolean)this.traceProtocol.getValue()).booleanValue();
    if (doTrace)
      this.log.logTrace("Reading compressed packet of length " + compressedPacketLength + " uncompressed to " + uncompressedLength); 
    if (uncompressedLength > 0) {
      uncompressedData = new byte[uncompressedLength];
      byte[] compressedBuffer = new byte[compressedPacketLength];
      readFully(compressedBuffer, 0, compressedPacketLength);
      this.inflater.reset();
      this.inflater.setInput(compressedBuffer);
      try {
        this.inflater.inflate(uncompressedData);
      } catch (DataFormatException dfe) {
        throw new IOException("Error while uncompressing packet from server.");
      } 
    } else {
      if (doTrace)
        this.log.logTrace("Packet didn't meet compression threshold, not uncompressing..."); 
      uncompressedLength = compressedPacketLength;
      uncompressedData = new byte[uncompressedLength];
      readFully(uncompressedData, 0, uncompressedLength);
    } 
    if (doTrace)
      if (uncompressedLength > 1024) {
        this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, 256));
        byte[] tempData = new byte[256];
        System.arraycopy(uncompressedData, uncompressedLength - 256, tempData, 0, 256);
        this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(tempData, 256));
        this.log.logTrace("Large packet dump truncated. Showing first and last 256 bytes.");
      } else {
        this.log.logTrace("Uncompressed packet: \n" + StringUtils.dumpAsHex(uncompressedData, uncompressedLength));
      }  
    if (this.buffer != null && this.pos < this.buffer.length) {
      if (doTrace)
        this.log.logTrace("Combining remaining packet with new: "); 
      int remaining = this.buffer.length - this.pos;
      byte[] newBuffer = new byte[remaining + uncompressedData.length];
      System.arraycopy(this.buffer, this.pos, newBuffer, 0, remaining);
      System.arraycopy(uncompressedData, 0, newBuffer, remaining, uncompressedData.length);
      uncompressedData = newBuffer;
    } 
    this.pos = 0;
    this.buffer = uncompressedData;
  }
  
  private void getNextPacketIfRequired(int numBytes) throws IOException {
    if (this.buffer == null || this.pos + numBytes > this.buffer.length)
      getNextPacketFromServer(); 
  }
  
  public int read() throws IOException {
    try {
      getNextPacketIfRequired(1);
    } catch (IOException ioEx) {
      return -1;
    } 
    return this.buffer[this.pos++] & 0xFF;
  }
  
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    if (b == null)
      throw new NullPointerException(); 
    if (off < 0 || off > b.length || len < 0 || off + len > b.length || off + len < 0)
      throw new IndexOutOfBoundsException(); 
    if (len <= 0)
      return 0; 
    try {
      getNextPacketIfRequired(len);
    } catch (IOException ioEx) {
      return -1;
    } 
    int remainingBufferLength = this.buffer.length - this.pos;
    int consummedBytesLength = Math.min(remainingBufferLength, len);
    System.arraycopy(this.buffer, this.pos, b, off, consummedBytesLength);
    this.pos += consummedBytesLength;
    return consummedBytesLength;
  }
  
  private final int readFully(byte[] b, int off, int len) throws IOException {
    if (len < 0)
      throw new IndexOutOfBoundsException(); 
    int n = 0;
    while (n < len) {
      int count = this.in.read(b, off + n, len - n);
      if (count < 0)
        throw new EOFException(); 
      n += count;
    } 
    return n;
  }
  
  public long skip(long n) throws IOException {
    long count = 0L;
    long i;
    for (i = 0L; i < n; i++) {
      int bytesRead = read();
      if (bytesRead == -1)
        break; 
      count++;
    } 
    return count;
  }
}
