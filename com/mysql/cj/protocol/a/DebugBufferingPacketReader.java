package com.mysql.cj.protocol.a;

import com.mysql.cj.Messages;
import com.mysql.cj.conf.RuntimeProperty;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.MessageHeader;
import com.mysql.cj.protocol.MessageReader;
import com.mysql.cj.util.StringUtils;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Optional;

public class DebugBufferingPacketReader implements MessageReader<NativePacketHeader, NativePacketPayload> {
  private static final int MAX_PACKET_DUMP_LENGTH = 1024;
  
  private static final int DEBUG_MSG_LEN = 96;
  
  private MessageReader<NativePacketHeader, NativePacketPayload> packetReader;
  
  private LinkedList<StringBuilder> packetDebugBuffer;
  
  private RuntimeProperty<Integer> packetDebugBufferSize;
  
  private String lastHeaderPayload = "";
  
  private boolean packetSequenceReset = false;
  
  public DebugBufferingPacketReader(MessageReader<NativePacketHeader, NativePacketPayload> packetReader, LinkedList<StringBuilder> packetDebugBuffer, RuntimeProperty<Integer> packetDebugBufferSize) {
    this.packetReader = packetReader;
    this.packetDebugBuffer = packetDebugBuffer;
    this.packetDebugBufferSize = packetDebugBufferSize;
  }
  
  public NativePacketHeader readHeader() throws IOException {
    byte prevPacketSeq = this.packetReader.getMessageSequence();
    return readHeaderLocal(prevPacketSeq, (NativePacketHeader)this.packetReader.readHeader());
  }
  
  public NativePacketHeader probeHeader() throws IOException {
    byte prevPacketSeq = this.packetReader.getMessageSequence();
    return readHeaderLocal(prevPacketSeq, (NativePacketHeader)this.packetReader.probeHeader());
  }
  
  private NativePacketHeader readHeaderLocal(byte prevPacketSeq, NativePacketHeader hdr) throws IOException {
    byte currPacketSeq = hdr.getMessageSequence();
    if (!this.packetSequenceReset) {
      if (currPacketSeq == Byte.MIN_VALUE && prevPacketSeq != Byte.MAX_VALUE)
        throw new IOException(Messages.getString("PacketReader.9", new Object[] { "-128", Byte.valueOf(currPacketSeq) })); 
      if (prevPacketSeq == -1 && currPacketSeq != 0)
        throw new IOException(Messages.getString("PacketReader.9", new Object[] { "-1", Byte.valueOf(currPacketSeq) })); 
      if (currPacketSeq != Byte.MIN_VALUE && prevPacketSeq != -1 && currPacketSeq != prevPacketSeq + 1)
        throw new IOException(Messages.getString("PacketReader.9", new Object[] { Integer.valueOf(prevPacketSeq + 1), Byte.valueOf(currPacketSeq) })); 
    } else {
      this.packetSequenceReset = false;
    } 
    this.lastHeaderPayload = StringUtils.dumpAsHex(hdr.getBuffer().array(), 4);
    return hdr;
  }
  
  public NativePacketPayload readMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
    int packetLength = header.getMessageSize();
    NativePacketPayload buf = (NativePacketPayload)this.packetReader.readMessage(reuse, header);
    int bytesToDump = Math.min(1024, packetLength);
    String PacketPayloadImpl = StringUtils.dumpAsHex(buf.getByteBuffer(), bytesToDump);
    StringBuilder packetDump = new StringBuilder(100 + PacketPayloadImpl.length());
    packetDump.append("Server ");
    packetDump.append(reuse.isPresent() ? "(re-used) " : "(new) ");
    packetDump.append(buf.toString());
    packetDump.append(" --------------------> Client\n");
    packetDump.append("\nPacket payload:\n\n");
    packetDump.append(this.lastHeaderPayload);
    packetDump.append(PacketPayloadImpl);
    if (bytesToDump == 1024)
      packetDump.append("\nNote: Packet of " + packetLength + " bytes truncated to " + 'Ѐ' + " bytes.\n"); 
    if (this.packetDebugBuffer.size() + 1 > ((Integer)this.packetDebugBufferSize.getValue()).intValue())
      this.packetDebugBuffer.removeFirst(); 
    this.packetDebugBuffer.addLast(packetDump);
    return buf;
  }
  
  public NativePacketPayload probeMessage(Optional<NativePacketPayload> reuse, NativePacketHeader header) throws IOException {
    int packetLength = header.getMessageSize();
    NativePacketPayload buf = (NativePacketPayload)this.packetReader.probeMessage(reuse, header);
    int bytesToDump = Math.min(1024, packetLength);
    String PacketPayloadImpl = StringUtils.dumpAsHex(buf.getByteBuffer(), bytesToDump);
    StringBuilder packetDump = new StringBuilder(100 + PacketPayloadImpl.length());
    packetDump.append("Server ");
    packetDump.append(reuse.isPresent() ? "(re-used) " : "(new) ");
    packetDump.append(buf.toString());
    packetDump.append(" --------------------> Client\n");
    packetDump.append("\nPacket payload:\n\n");
    packetDump.append(this.lastHeaderPayload);
    packetDump.append(PacketPayloadImpl);
    if (bytesToDump == 1024)
      packetDump.append("\nNote: Packet of " + packetLength + " bytes truncated to " + 'Ѐ' + " bytes.\n"); 
    if (this.packetDebugBuffer.size() + 1 > ((Integer)this.packetDebugBufferSize.getValue()).intValue())
      this.packetDebugBuffer.removeFirst(); 
    this.packetDebugBuffer.addLast(packetDump);
    return buf;
  }
  
  public byte getMessageSequence() {
    return this.packetReader.getMessageSequence();
  }
  
  public void resetMessageSequence() {
    this.packetReader.resetMessageSequence();
    this.packetSequenceReset = true;
  }
  
  public MessageReader<NativePacketHeader, NativePacketPayload> undecorateAll() {
    return this.packetReader.undecorateAll();
  }
  
  public MessageReader<NativePacketHeader, NativePacketPayload> undecorate() {
    return this.packetReader;
  }
}
