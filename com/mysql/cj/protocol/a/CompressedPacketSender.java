package com.mysql.cj.protocol.a;

import com.mysql.cj.protocol.MessageSender;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

public class CompressedPacketSender implements MessageSender<NativePacketPayload> {
  private BufferedOutputStream outputStream;
  
  private Deflater deflater = new Deflater();
  
  private byte[] compressedPacket;
  
  private byte compressedSequenceId = 0;
  
  private int compressedPayloadLen = 0;
  
  public static final int COMP_HEADER_LENGTH = 7;
  
  public static final int MIN_COMPRESS_LEN = 50;
  
  public CompressedPacketSender(BufferedOutputStream outputStream) {
    this.outputStream = outputStream;
  }
  
  public void stop() {
    this.deflater.end();
    this.deflater = null;
  }
  
  private void resetPacket() {
    this.compressedPayloadLen = 0;
    this.deflater.reset();
  }
  
  private void addUncompressedHeader(byte packetSequence, int uncompressedPacketLen) {
    byte[] uncompressedHeader = new byte[4];
    NativeUtils.encodeMysqlThreeByteInteger(uncompressedPacketLen, uncompressedHeader, 0);
    uncompressedHeader[3] = packetSequence;
    this.deflater.setInput(uncompressedHeader);
    this.compressedPayloadLen += this.deflater.deflate(this.compressedPacket, this.compressedPayloadLen, this.compressedPacket.length - this.compressedPayloadLen);
  }
  
  private void addPayload(byte[] payload, int payloadOffset, int payloadLen) {
    this.deflater.setInput(payload, payloadOffset, payloadLen);
    this.compressedPayloadLen += this.deflater.deflate(this.compressedPacket, this.compressedPayloadLen, this.compressedPacket.length - this.compressedPayloadLen);
  }
  
  private void completeCompression() {
    this.deflater.finish();
    this.compressedPayloadLen += this.deflater.deflate(this.compressedPacket, this.compressedPayloadLen, this.compressedPacket.length - this.compressedPayloadLen);
  }
  
  private void writeCompressedHeader(int compLen, byte seq, int uncompLen) throws IOException {
    this.outputStream.write(NativeUtils.encodeMysqlThreeByteInteger(compLen));
    this.outputStream.write(seq);
    this.outputStream.write(NativeUtils.encodeMysqlThreeByteInteger(uncompLen));
  }
  
  private void writeUncompressedHeader(int packetLen, byte packetSequence) throws IOException {
    this.outputStream.write(NativeUtils.encodeMysqlThreeByteInteger(packetLen));
    this.outputStream.write(packetSequence);
  }
  
  private void sendCompressedPacket(int uncompressedPayloadLen) throws IOException {
    this.compressedSequenceId = (byte)(this.compressedSequenceId + 1);
    writeCompressedHeader(this.compressedPayloadLen, this.compressedSequenceId, uncompressedPayloadLen);
    this.outputStream.write(this.compressedPacket, 0, this.compressedPayloadLen);
  }
  
  public void send(byte[] packet, int packetLen, byte packetSequence) throws IOException {
    this.compressedSequenceId = packetSequence;
    if (packetLen < 50) {
      writeCompressedHeader(packetLen + 4, this.compressedSequenceId, 0);
      writeUncompressedHeader(packetLen, packetSequence);
      this.outputStream.write(packet, 0, packetLen);
      this.outputStream.flush();
      return;
    } 
    if (packetLen + 4 > 16777215) {
      this.compressedPacket = new byte[16777215];
    } else {
      this.compressedPacket = new byte[4 + packetLen];
    } 
    PacketSplitter packetSplitter = new PacketSplitter(packetLen);
    int unsentPayloadLen = 0;
    int unsentOffset = 0;
    while (true) {
      this.compressedPayloadLen = 0;
      if (packetSplitter.nextPacket()) {
        if (unsentPayloadLen > 0)
          addPayload(packet, unsentOffset, unsentPayloadLen); 
        int remaining = 16777215 - unsentPayloadLen;
        int len = Math.min(remaining, 4 + packetSplitter.getPacketLen());
        int lenNoHdr = len - 4;
        addUncompressedHeader(packetSequence, packetSplitter.getPacketLen());
        addPayload(packet, packetSplitter.getOffset(), lenNoHdr);
        completeCompression();
        if (this.compressedPayloadLen >= len) {
          this.compressedSequenceId = (byte)(this.compressedSequenceId + 1);
          writeCompressedHeader(unsentPayloadLen + len, this.compressedSequenceId, 0);
          this.outputStream.write(packet, unsentOffset, unsentPayloadLen);
          writeUncompressedHeader(lenNoHdr, packetSequence);
          this.outputStream.write(packet, packetSplitter.getOffset(), lenNoHdr);
        } else {
          sendCompressedPacket(len + unsentPayloadLen);
        } 
        packetSequence = (byte)(packetSequence + 1);
        unsentPayloadLen = packetSplitter.getPacketLen() - lenNoHdr;
        unsentOffset = packetSplitter.getOffset() + lenNoHdr;
        resetPacket();
        continue;
      } 
      break;
    } 
    if (unsentPayloadLen > 0) {
      addPayload(packet, unsentOffset, unsentPayloadLen);
      completeCompression();
      if (this.compressedPayloadLen >= unsentPayloadLen) {
        writeCompressedHeader(unsentPayloadLen, this.compressedSequenceId, 0);
        this.outputStream.write(packet, unsentOffset, unsentPayloadLen);
      } else {
        sendCompressedPacket(unsentPayloadLen);
      } 
      resetPacket();
    } 
    this.outputStream.flush();
    this.compressedPacket = null;
  }
  
  public MessageSender<NativePacketPayload> undecorateAll() {
    return this;
  }
  
  public MessageSender<NativePacketPayload> undecorate() {
    return this;
  }
}
