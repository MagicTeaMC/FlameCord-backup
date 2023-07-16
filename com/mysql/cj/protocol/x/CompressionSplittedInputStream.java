package com.mysql.cj.protocol.x;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Parser;
import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.x.protobuf.Mysqlx;
import com.mysql.cj.x.protobuf.MysqlxConnection;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CompressionSplittedInputStream extends FilterInputStream {
  private CompressorStreamsFactory compressorIoStreamsFactory;
  
  private byte[] frameHeader = new byte[5];
  
  private int frameHeaderConsumed = 0;
  
  private int framePayloadLength = 0;
  
  private int framePayloadConsumed = 0;
  
  private XMessageHeader xMessageHeader;
  
  private InputStream compressorIn = null;
  
  private byte[] singleByte = new byte[1];
  
  private boolean closed = false;
  
  public CompressionSplittedInputStream(InputStream in, CompressorStreamsFactory streamsFactory) {
    super(in);
    this.compressorIoStreamsFactory = streamsFactory;
  }
  
  public int available() throws IOException {
    ensureOpen();
    if (this.compressorIn != null)
      return this.compressorIn.available(); 
    return ((this.frameHeaderConsumed > 0) ? (5 - this.frameHeaderConsumed) : 0) + this.in.available();
  }
  
  public void close() throws IOException {
    if (!this.closed) {
      super.close();
      this.in = null;
      if (this.compressorIn != null)
        this.compressorIn.close(); 
      this.compressorIn = null;
      this.closed = true;
    } 
  }
  
  public int read() throws IOException {
    ensureOpen();
    int read = read(this.singleByte, 0, 1);
    if (read >= 0)
      return this.singleByte[0] & 0xFF; 
    return read;
  }
  
  public int read(byte[] b) throws IOException {
    ensureOpen();
    return read(b, 0, b.length);
  }
  
  public int read(byte[] b, int off, int len) throws IOException {
    ensureOpen();
    if (len <= 0)
      return 0; 
    peekNextFrame();
    try {
      if (isCompressedDataAvailable()) {
        int bytesRead = readFully(this.compressorIn, b, off, len);
        if (isCompressedDataReadComplete()) {
          this.compressorIn.close();
          this.compressorIn = null;
        } 
        return bytesRead;
      } 
    } catch (IOException e) {
      throw e;
    } 
    int headerBytesRead = 0;
    if (!isFrameHeaderFullyConsumed()) {
      int lenToConsume = Math.min(len, 5 - this.frameHeaderConsumed);
      System.arraycopy(this.frameHeader, this.frameHeaderConsumed, b, off, lenToConsume);
      off += lenToConsume;
      len -= lenToConsume;
      this.frameHeaderConsumed += lenToConsume;
      headerBytesRead = lenToConsume;
    } 
    int payloadBytesRead = readFully(b, off, len);
    this.framePayloadConsumed += payloadBytesRead;
    return headerBytesRead + payloadBytesRead;
  }
  
  private void peekNextFrame() throws IOException {
    if (isDataAvailable())
      return; 
    readFully(this.frameHeader, 0, 5);
    this.xMessageHeader = new XMessageHeader(this.frameHeader);
    this.framePayloadLength = this.xMessageHeader.getMessageSize();
    this.frameHeaderConsumed = 0;
    this.framePayloadConsumed = 0;
    if (isCompressedFrame()) {
      MysqlxConnection.Compression compressedMessage = parseCompressedMessage();
      this
        
        .compressorIn = new ConfinedInputStream(this.compressorIoStreamsFactory.getInputStreamInstance(new ByteArrayInputStream(compressedMessage.getPayload().toByteArray())), (int)compressedMessage.getUncompressedSize());
      this.frameHeaderConsumed = 5;
      this.framePayloadConsumed = this.framePayloadLength;
    } 
  }
  
  private boolean isCompressedFrame() {
    return (Mysqlx.ServerMessages.Type.forNumber(this.xMessageHeader.getMessageType()) == Mysqlx.ServerMessages.Type.COMPRESSION);
  }
  
  private MysqlxConnection.Compression parseCompressedMessage() {
    Parser<MysqlxConnection.Compression> parser = (Parser<MysqlxConnection.Compression>)MessageConstants.MESSAGE_CLASS_TO_PARSER.get(MessageConstants.MESSAGE_TYPE_TO_CLASS.get(Integer.valueOf(19)));
    byte[] packet = new byte[this.xMessageHeader.getMessageSize()];
    try {
      readFully(packet);
    } catch (IOException e) {
      throw (CJCommunicationsException)ExceptionFactory.createException(CJCommunicationsException.class, Messages.getString("Protocol.Compression.Streams.0"), e);
    } 
    try {
      return (MysqlxConnection.Compression)parser.parseFrom(packet);
    } catch (InvalidProtocolBufferException e) {
      throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, Messages.getString("Protocol.Compression.Streams.1"), e);
    } 
  }
  
  private boolean isDataAvailable() throws IOException {
    return (isCompressedDataAvailable() || (this.frameHeaderConsumed > 0 && this.frameHeaderConsumed < 5) || (
      isFrameHeaderFullyConsumed() && this.framePayloadConsumed < this.framePayloadLength));
  }
  
  private boolean isCompressedDataAvailable() throws IOException {
    return (this.compressorIn != null && this.compressorIn.available() > 0);
  }
  
  private boolean isCompressedDataReadComplete() throws IOException {
    return (this.compressorIn != null && this.compressorIn.available() == 0);
  }
  
  boolean isFrameHeaderFullyConsumed() {
    return (this.frameHeaderConsumed == 5);
  }
  
  public int readFully(byte[] b) throws IOException {
    return readFully(b, 0, b.length);
  }
  
  private final int readFully(byte[] b, int off, int len) throws IOException {
    return readFully(this.in, b, off, len);
  }
  
  private final int readFully(InputStream inStream, byte[] b, int off, int len) throws IOException {
    if (len < 0)
      throw new IndexOutOfBoundsException(); 
    int total = 0;
    while (total < len) {
      int count = inStream.read(b, off + total, len - total);
      if (count < 0)
        throw new EOFException(); 
      total += count;
    } 
    return total;
  }
  
  private void ensureOpen() throws IOException {
    if (this.closed)
      throw new IOException("Stream closed"); 
  }
}
