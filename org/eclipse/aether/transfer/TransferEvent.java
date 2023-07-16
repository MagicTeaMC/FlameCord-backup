package org.eclipse.aether.transfer;

import java.nio.ByteBuffer;
import java.util.Objects;
import org.eclipse.aether.RepositorySystemSession;

public final class TransferEvent {
  private final EventType type;
  
  private final RequestType requestType;
  
  private final RepositorySystemSession session;
  
  private final TransferResource resource;
  
  private final ByteBuffer dataBuffer;
  
  private final long transferredBytes;
  
  private final Exception exception;
  
  public enum EventType {
    INITIATED, STARTED, PROGRESSED, CORRUPTED, SUCCEEDED, FAILED;
  }
  
  public enum RequestType {
    GET, GET_EXISTENCE, PUT;
  }
  
  TransferEvent(Builder builder) {
    this.type = builder.type;
    this.requestType = builder.requestType;
    this.session = builder.session;
    this.resource = builder.resource;
    this.dataBuffer = builder.dataBuffer;
    this.transferredBytes = builder.transferredBytes;
    this.exception = builder.exception;
  }
  
  public EventType getType() {
    return this.type;
  }
  
  public RequestType getRequestType() {
    return this.requestType;
  }
  
  public RepositorySystemSession getSession() {
    return this.session;
  }
  
  public TransferResource getResource() {
    return this.resource;
  }
  
  public long getTransferredBytes() {
    return this.transferredBytes;
  }
  
  public ByteBuffer getDataBuffer() {
    return (this.dataBuffer != null) ? this.dataBuffer.asReadOnlyBuffer() : null;
  }
  
  public int getDataLength() {
    return (this.dataBuffer != null) ? this.dataBuffer.remaining() : 0;
  }
  
  public Exception getException() {
    return this.exception;
  }
  
  public String toString() {
    return getRequestType() + " " + getType() + " " + getResource();
  }
  
  public static final class Builder {
    TransferEvent.EventType type;
    
    TransferEvent.RequestType requestType;
    
    RepositorySystemSession session;
    
    TransferResource resource;
    
    ByteBuffer dataBuffer;
    
    long transferredBytes;
    
    Exception exception;
    
    public Builder(RepositorySystemSession session, TransferResource resource) {
      this.session = Objects.<RepositorySystemSession>requireNonNull(session, "repository system session cannot be null");
      this.resource = Objects.<TransferResource>requireNonNull(resource, "transfer resource cannot be null");
      this.type = TransferEvent.EventType.INITIATED;
      this.requestType = TransferEvent.RequestType.GET;
    }
    
    private Builder(Builder prototype) {
      this.session = prototype.session;
      this.resource = prototype.resource;
      this.type = prototype.type;
      this.requestType = prototype.requestType;
      this.dataBuffer = prototype.dataBuffer;
      this.transferredBytes = prototype.transferredBytes;
      this.exception = prototype.exception;
    }
    
    public Builder copy() {
      return new Builder(this);
    }
    
    public Builder resetType(TransferEvent.EventType type) {
      this.type = Objects.<TransferEvent.EventType>requireNonNull(type, "event type cannot be null");
      this.dataBuffer = null;
      this.exception = null;
      switch (type) {
        case INITIATED:
        case STARTED:
          this.transferredBytes = 0L;
          break;
      } 
      return this;
    }
    
    public Builder setType(TransferEvent.EventType type) {
      this.type = Objects.<TransferEvent.EventType>requireNonNull(type, "event type cannot be null");
      return this;
    }
    
    public Builder setRequestType(TransferEvent.RequestType requestType) {
      this.requestType = Objects.<TransferEvent.RequestType>requireNonNull(requestType, "request type cannot be null");
      return this;
    }
    
    public Builder setTransferredBytes(long transferredBytes) {
      if (transferredBytes < 0L)
        throw new IllegalArgumentException("number of transferred bytes cannot be negative"); 
      this.transferredBytes = transferredBytes;
      return this;
    }
    
    public Builder addTransferredBytes(long transferredBytes) {
      if (transferredBytes < 0L)
        throw new IllegalArgumentException("number of transferred bytes cannot be negative"); 
      this.transferredBytes += transferredBytes;
      return this;
    }
    
    public Builder setDataBuffer(byte[] buffer, int offset, int length) {
      return setDataBuffer((buffer != null) ? ByteBuffer.wrap(buffer, offset, length) : null);
    }
    
    public Builder setDataBuffer(ByteBuffer dataBuffer) {
      this.dataBuffer = dataBuffer;
      return this;
    }
    
    public Builder setException(Exception exception) {
      this.exception = exception;
      return this;
    }
    
    public TransferEvent build() {
      return new TransferEvent(this);
    }
  }
}
