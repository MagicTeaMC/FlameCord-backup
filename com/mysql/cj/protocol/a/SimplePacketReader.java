package com.mysql.cj.protocol.a;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.exceptions.CJPacketTooBigException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.MessageHeader;
import com.mysql.cj.protocol.MessageReader;
import com.mysql.cj.protocol.SocketConnection;
import java.io.IOException;
import java.util.Optional;

public class SimplePacketReader implements MessageReader<NativePacketHeader, NativePacketPayload> {
  protected SocketConnection socketConnection;
  
  protected RuntimeProperty<Integer> maxAllowedPacket;
  
  private byte readPacketSequence = -1;
  
  NativePacketHeader lastHeader = null;
  
  NativePacketPayload lastMessage = null;
  
  public SimplePacketReader(SocketConnection socketConnection, RuntimeProperty<Integer> maxAllowedPacket) {
    this.socketConnection = socketConnection;
    this.maxAllowedPacket = maxAllowedPacket;
  }
  
  public NativePacketHeader readHeader() throws IOException {
    if (this.lastHeader == null)
      return readHeaderLocal(); 
    NativePacketHeader hdr = this.lastHeader;
    this.lastHeader = null;
    this.readPacketSequence = hdr.getMessageSequence();
    return hdr;
  }
  
  public NativePacketHeader probeHeader() throws IOException {
    this.lastHeader = readHeaderLocal();
    return this.lastHeader;
  }
  
  private NativePacketHeader readHeaderLocal() throws IOException {
    NativePacketHeader hdr = new NativePacketHeader();
    try {
      this.socketConnection.getMysqlInput().readFully(hdr.getBuffer().array(), 0, 4);
      int packetLength = hdr.getMessageSize();
      if (packetLength > ((Integer)this.maxAllowedPacket.getValue()).intValue())
        throw new CJPacketTooBigException(packetLength, ((Integer)this.maxAllowedPacket.getValue()).intValue()); 
    } catch (IOException|CJPacketTooBigException e) {
      try {
        this.socketConnection.forceClose();
      } catch (Exception exception) {}
      throw e;
    } 
    this.readPacketSequence = hdr.getMessageSequence();
    return hdr;
  }
  
  public NativePacketPayload readMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
    if (this.lastMessage == null)
      return readMessageLocal(reuse, header); 
    NativePacketPayload buf = this.lastMessage;
    this.lastMessage = null;
    return buf;
  }
  
  public NativePacketPayload probeMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
    this.lastMessage = readMessageLocal(reuse, header);
    return this.lastMessage;
  }
  
  private NativePacketPayload readMessageLocal(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
    try {
      NativePacketPayload message;
      int packetLength = header.getMessageSize();
      if (reuse.isPresent()) {
        message = reuse.get();
        message.setPosition(0);
        if ((message.getByteBuffer()).length < packetLength)
          message.setByteBuffer(new byte[packetLength]); 
        message.setPayloadLength(packetLength);
      } else {
        message = new NativePacketPayload(new byte[packetLength]);
      } 
      int numBytesRead = this.socketConnection.getMysqlInput().readFully(message.getByteBuffer(), 0, packetLength);
      if (numBytesRead != packetLength)
        throw new IOException(Messages.getString("PacketReader.1", new Object[] { Integer.valueOf(packetLength), Integer.valueOf(numBytesRead) })); 
      return message;
    } catch (IOException e) {
      try {
        this.socketConnection.forceClose();
      } catch (Exception exception) {}
      throw e;
    } 
  }
  
  public byte getMessageSequence() {
    return this.readPacketSequence;
  }
  
  public void resetMessageSequence() {
    this.readPacketSequence = 0;
  }
}
