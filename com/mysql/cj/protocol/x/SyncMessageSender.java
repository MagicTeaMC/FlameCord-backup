package com.mysql.cj.protocol.x;

import com.google.protobuf.Message;
import com.mysql.cj.Messages;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.CJPacketTooBigException;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.MessageSender;
import com.mysql.cj.protocol.PacketSentTimeHolder;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CompletableFuture;

public class SyncMessageSender implements MessageSender<XMessage>, PacketSentTimeHolder {
  static final int HEADER_LEN = 5;
  
  private OutputStream outputStream;
  
  private long lastPacketSentTime = 0L;
  
  private long previousPacketSentTime = 0L;
  
  private int maxAllowedPacket = -1;
  
  Object waitingAsyncOperationMonitor = new Object();
  
  public SyncMessageSender(OutputStream os) {
    this.outputStream = os;
  }
  
  public void send(XMessage message) {
    synchronized (this.waitingAsyncOperationMonitor) {
      Message message1 = message.getMessage();
      try {
        int type = MessageConstants.getTypeForMessageClass((Class)message1.getClass());
        int size = 1 + message1.getSerializedSize();
        if (this.maxAllowedPacket > 0 && size > this.maxAllowedPacket)
          throw new CJPacketTooBigException(Messages.getString("PacketTooBigException.1", new Object[] { Integer.valueOf(size), Integer.valueOf(this.maxAllowedPacket) })); 
        byte[] sizeHeader = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(size).array();
        this.outputStream.write(sizeHeader);
        this.outputStream.write(type);
        message1.writeTo(this.outputStream);
        this.outputStream.flush();
        this.previousPacketSentTime = this.lastPacketSentTime;
        this.lastPacketSentTime = System.currentTimeMillis();
      } catch (IOException ex) {
        throw new CJCommunicationsException("Unable to write message", ex);
      } 
    } 
  }
  
  public CompletableFuture<?> send(XMessage message, CompletableFuture<?> future, Runnable callback) {
    synchronized (this.waitingAsyncOperationMonitor) {
      CompletionHandler<Long, Void> resultHandler = new ErrorToFutureCompletionHandler<>(future, callback);
      Message message1 = message.getMessage();
      try {
        send(message);
        long result = (5 + message1.getSerializedSize());
        resultHandler.completed(Long.valueOf(result), null);
      } catch (Throwable t) {
        resultHandler.failed(t, null);
      } 
      return future;
    } 
  }
  
  public long getLastPacketSentTime() {
    return this.lastPacketSentTime;
  }
  
  public long getPreviousPacketSentTime() {
    return this.previousPacketSentTime;
  }
  
  public void setMaxAllowedPacket(int maxAllowedPacket) {
    this.maxAllowedPacket = maxAllowedPacket;
  }
}
