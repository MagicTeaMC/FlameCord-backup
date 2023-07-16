package com.mysql.cj.protocol.x;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.mysql.cj.exceptions.CJCommunicationsException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.FullReadInputStream;
import com.mysql.cj.protocol.Message;
import com.mysql.cj.protocol.MessageHeader;
import com.mysql.cj.protocol.MessageListener;
import com.mysql.cj.protocol.MessageReader;
import com.mysql.cj.protocol.Protocol;
import com.mysql.cj.x.protobuf.Mysqlx;
import com.mysql.cj.x.protobuf.MysqlxNotice;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class SyncMessageReader implements MessageReader<XMessageHeader, XMessage> {
  private FullReadInputStream inputStream;
  
  LinkedList<XMessageHeader> headersQueue = new LinkedList<>();
  
  LinkedList<GeneratedMessageV3> messagesQueue = new LinkedList<>();
  
  BlockingQueue<MessageListener<XMessage>> messageListenerQueue = new LinkedBlockingQueue<>();
  
  Object dispatchingThreadMonitor = new Object();
  
  Object waitingSyncOperationMonitor = new Object();
  
  Thread dispatchingThread = null;
  
  private Protocol.ProtocolEventHandler protocolEventHandler = null;
  
  public SyncMessageReader(FullReadInputStream inputStream, Protocol.ProtocolEventHandler protocolEventHandler) {
    this.inputStream = inputStream;
    this.protocolEventHandler = protocolEventHandler;
  }
  
  public XMessageHeader readHeader() throws IOException {
    synchronized (this.waitingSyncOperationMonitor) {
      XMessageHeader header;
      if ((header = this.headersQueue.peek()) == null)
        header = readHeaderLocal(); 
      if (header.getMessageType() == 1)
        throw new XProtocolError((Mysqlx.Error)readMessageLocal(Mysqlx.Error.class, true)); 
      return header;
    } 
  }
  
  public int getNextNonNoticeMessageType() throws IOException {
    synchronized (this.waitingSyncOperationMonitor) {
      XMessageHeader header;
      if (!this.headersQueue.isEmpty())
        for (XMessageHeader hdr : this.headersQueue) {
          if (hdr.getMessageType() != 11)
            return hdr.getMessageType(); 
        }  
      do {
        header = readHeaderLocal();
        if (header.getMessageType() == 1) {
          Mysqlx.Error msg;
          this.messagesQueue.addLast(msg = readMessageLocal(Mysqlx.Error.class, false));
          throw new XProtocolError(msg);
        } 
        if (header.getMessageType() != 11)
          continue; 
        this.messagesQueue.addLast((GeneratedMessageV3)readMessageLocal(MysqlxNotice.Frame.class, false));
      } while (header.getMessageType() == 11);
      return header.getMessageType();
    } 
  }
  
  private XMessageHeader readHeaderLocal() throws IOException {
    XMessageHeader header;
    try {
      byte[] buf = new byte[5];
      this.inputStream.readFully(buf);
      header = new XMessageHeader(buf);
      this.headersQueue.add(header);
    } catch (IOException ex) {
      throw new CJCommunicationsException("Cannot read packet header", ex);
    } 
    return header;
  }
  
  private <T extends GeneratedMessageV3> T readMessageLocal(Class<T> messageClass, boolean fromQueue) {
    XMessageHeader header;
    if (fromQueue) {
      header = this.headersQueue.poll();
      GeneratedMessageV3 generatedMessageV3 = this.messagesQueue.poll();
      if (generatedMessageV3 != null)
        return (T)generatedMessageV3; 
    } else {
      header = this.headersQueue.getLast();
    } 
    Parser<T> parser = (Parser<T>)MessageConstants.MESSAGE_CLASS_TO_PARSER.get(messageClass);
    byte[] packet = new byte[header.getMessageSize()];
    try {
      this.inputStream.readFully(packet);
    } catch (IOException ex) {
      throw new CJCommunicationsException("Cannot read packet payload", ex);
    } 
    try {
      GeneratedMessageV3 generatedMessageV3 = (GeneratedMessageV3)parser.parseFrom(packet);
      if (generatedMessageV3 instanceof MysqlxNotice.Frame && ((MysqlxNotice.Frame)generatedMessageV3).getType() == 1 && ((MysqlxNotice.Frame)generatedMessageV3).getScope() == MysqlxNotice.Frame.Scope.GLOBAL) {
        Notice.XWarning w = new Notice.XWarning((MysqlxNotice.Frame)generatedMessageV3);
        int code = (int)w.getCode();
        if (code == 1053 || code == 1810 || code == 3169) {
          CJCommunicationsException ex = new CJCommunicationsException(w.getMessage());
          ex.setVendorCode(code);
          if (this.protocolEventHandler != null)
            this.protocolEventHandler.invokeListeners((code == 1053) ? Protocol.ProtocolEventListener.EventType.SERVER_SHUTDOWN : Protocol.ProtocolEventListener.EventType.SERVER_CLOSED_SESSION, (Throwable)ex); 
          throw ex;
        } 
      } 
      return (T)generatedMessageV3;
    } catch (InvalidProtocolBufferException ex) {
      throw new WrongArgumentException(ex);
    } 
  }
  
  public XMessage readMessage(Optional<XMessage> reuse, XMessageHeader hdr) throws IOException {
    return readMessage(reuse, hdr.getMessageType());
  }
  
  public XMessage readMessage(Optional<XMessage> reuse, int expectedType) throws IOException {
    synchronized (this.waitingSyncOperationMonitor) {
      Class<? extends GeneratedMessageV3> expectedClass = MessageConstants.getMessageClassForType(expectedType);
      List<Notice> notices = null;
      XMessageHeader hdr;
      while ((hdr = readHeader()).getMessageType() == 11 && expectedType != 11) {
        if (notices == null)
          notices = new ArrayList<>(); 
        notices.add(
            Notice.getInstance(new XMessage((Message)readMessageLocal(MessageConstants.getMessageClassForType(11), true))));
      } 
      Class<? extends GeneratedMessageV3> messageClass = MessageConstants.getMessageClassForType(hdr.getMessageType());
      if (expectedClass != messageClass)
        throw new WrongArgumentException("Unexpected message class. Expected '" + expectedClass.getSimpleName() + "' but actually received '" + messageClass
            .getSimpleName() + "'"); 
      return (new XMessage((Message)readMessageLocal(messageClass, true))).addNotices(notices);
    } 
  }
  
  public void pushMessageListener(MessageListener<XMessage> listener) {
    try {
      this.messageListenerQueue.put(listener);
    } catch (InterruptedException e) {
      throw new CJCommunicationsException("Cannot queue message listener.", e);
    } 
    synchronized (this.dispatchingThreadMonitor) {
      if (this.dispatchingThread == null) {
        ListenersDispatcher ld = new ListenersDispatcher();
        this.dispatchingThread = new Thread(ld, "Message listeners dispatching thread");
        this.dispatchingThread.start();
        int millis = 5000;
        while (!ld.started) {
          try {
            Thread.sleep(10L);
            millis -= 10;
          } catch (InterruptedException e) {
            throw new XProtocolError(e.getMessage(), e);
          } 
          if (millis <= 0)
            throw new XProtocolError("Timeout for starting ListenersDispatcher exceeded."); 
        } 
      } 
    } 
  }
  
  private class ListenersDispatcher implements Runnable {
    private static final long POLL_TIMEOUT = 100L;
    
    boolean started = false;
    
    public void run() {
      synchronized (SyncMessageReader.this.waitingSyncOperationMonitor) {
        this.started = true;
        try {
          label32: while (true) {
            MessageListener<XMessage> l;
            while (true) {
              if ((l = SyncMessageReader.this.messageListenerQueue.poll(100L, TimeUnit.MILLISECONDS)) == null) {
                synchronized (SyncMessageReader.this.dispatchingThreadMonitor) {
                  if (SyncMessageReader.this.messageListenerQueue.peek() == null) {
                    SyncMessageReader.this.dispatchingThread = null;
                  } else {
                    continue;
                  } 
                } 
              } else {
                break;
              } 
            } 
            try {
              XMessage msg = null;
              while (true) {
                XMessageHeader hdr = SyncMessageReader.this.readHeader();
                msg = SyncMessageReader.this.readMessage((Optional<XMessage>)null, hdr);
                if (l.processMessage(msg))
                  continue label32; 
              } 
            } catch (Throwable t) {
              l.error(t);
            } 
          } 
        } catch (InterruptedException e) {
          throw new CJCommunicationsException("Read operation interrupted.", e);
        } 
      } 
    }
  }
}
