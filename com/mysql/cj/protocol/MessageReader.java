package com.mysql.cj.protocol;

import com.mysql.cj.exceptions.CJOperationNotSupportedException;
import com.mysql.cj.exceptions.ExceptionFactory;
import java.io.IOException;
import java.util.Optional;

public interface MessageReader<H extends MessageHeader, M extends Message> {
  H readHeader() throws IOException;
  
  default H probeHeader() throws IOException {
    return readHeader();
  }
  
  M readMessage(Optional<M> paramOptional, H paramH) throws IOException;
  
  default M probeMessage(Optional<M> reuse, H header) throws IOException {
    return readMessage(reuse, header);
  }
  
  default M readMessage(Optional<M> reuse, int expectedType) throws IOException {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  default void skipPacket() throws IOException {
    readMessage(Optional.empty(), readHeader());
  }
  
  default void pushMessageListener(MessageListener<M> l) {
    throw (CJOperationNotSupportedException)ExceptionFactory.createException(CJOperationNotSupportedException.class, "Not supported");
  }
  
  default byte getMessageSequence() {
    return 0;
  }
  
  default void resetMessageSequence() {}
  
  default MessageReader<H, M> undecorateAll() {
    return this;
  }
  
  default MessageReader<H, M> undecorate() {
    return this;
  }
  
  default void start() {}
  
  default void stopAfterNextMessage() {}
}
