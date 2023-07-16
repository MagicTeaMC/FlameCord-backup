package org.eclipse.aether.connector.basic;

import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Map;
import org.eclipse.aether.spi.connector.Transfer;
import org.eclipse.aether.spi.connector.transport.TransportListener;
import org.eclipse.aether.transfer.TransferCancelledException;
import org.eclipse.aether.transfer.TransferEvent;
import org.eclipse.aether.transfer.TransferListener;

class TransferTransportListener<T extends Transfer> extends TransportListener {
  private final T transfer;
  
  private final TransferListener listener;
  
  private final TransferEvent.Builder eventBuilder;
  
  private ChecksumCalculator checksumCalculator;
  
  protected TransferTransportListener(T transfer, TransferEvent.Builder eventBuilder) {
    this.transfer = transfer;
    this.listener = transfer.getListener();
    this.eventBuilder = eventBuilder;
  }
  
  protected T getTransfer() {
    return this.transfer;
  }
  
  public void transferInitiated() throws TransferCancelledException {
    if (this.listener != null) {
      this.eventBuilder.resetType(TransferEvent.EventType.INITIATED);
      this.listener.transferInitiated(this.eventBuilder.build());
    } 
  }
  
  public void transportStarted(long dataOffset, long dataLength) throws TransferCancelledException {
    if (this.checksumCalculator != null)
      this.checksumCalculator.init(dataOffset); 
    if (this.listener != null) {
      this.eventBuilder.resetType(TransferEvent.EventType.STARTED).setTransferredBytes(dataOffset);
      TransferEvent event = this.eventBuilder.build();
      event.getResource().setContentLength(dataLength).setResumeOffset(dataOffset);
      this.listener.transferStarted(event);
    } 
  }
  
  public void transportProgressed(ByteBuffer data) throws TransferCancelledException {
    if (this.checksumCalculator != null)
      this.checksumCalculator.update(data); 
    if (this.listener != null) {
      this.eventBuilder.resetType(TransferEvent.EventType.PROGRESSED).addTransferredBytes(data.remaining())
        .setDataBuffer(data);
      this.listener.transferProgressed(this.eventBuilder.build());
    } 
  }
  
  public void transferCorrupted(Exception exception) throws TransferCancelledException {
    if (this.listener != null) {
      this.eventBuilder.resetType(TransferEvent.EventType.CORRUPTED).setException(exception);
      this.listener.transferCorrupted(this.eventBuilder.build());
    } 
  }
  
  public void transferFailed(Exception exception, int classification) {
    if (this.listener != null) {
      this.eventBuilder.resetType(TransferEvent.EventType.FAILED).setException(exception);
      this.listener.transferFailed(this.eventBuilder.build());
    } 
  }
  
  public void transferSucceeded() {
    if (this.listener != null) {
      this.eventBuilder.resetType(TransferEvent.EventType.SUCCEEDED);
      this.listener.transferSucceeded(this.eventBuilder.build());
    } 
  }
  
  public Map<String, Object> getChecksums() {
    if (this.checksumCalculator == null)
      return Collections.emptyMap(); 
    return this.checksumCalculator.get();
  }
  
  public void setChecksumCalculator(ChecksumCalculator checksumCalculator) {
    this.checksumCalculator = checksumCalculator;
  }
}
