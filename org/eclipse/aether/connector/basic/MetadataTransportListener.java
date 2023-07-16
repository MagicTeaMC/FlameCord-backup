package org.eclipse.aether.connector.basic;

import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.MetadataTransfer;
import org.eclipse.aether.transfer.MetadataNotFoundException;
import org.eclipse.aether.transfer.MetadataTransferException;
import org.eclipse.aether.transfer.TransferEvent;

final class MetadataTransportListener extends TransferTransportListener<MetadataTransfer> {
  private final RemoteRepository repository;
  
  MetadataTransportListener(MetadataTransfer transfer, RemoteRepository repository, TransferEvent.Builder eventBuilder) {
    super(transfer, eventBuilder);
    this.repository = repository;
  }
  
  public void transferFailed(Exception exception, int classification) {
    MetadataTransferException e;
    if (classification == 1) {
      MetadataNotFoundException metadataNotFoundException = new MetadataNotFoundException(getTransfer().getMetadata(), this.repository);
    } else {
      e = new MetadataTransferException(getTransfer().getMetadata(), this.repository, exception);
    } 
    getTransfer().setException(e);
    super.transferFailed((Exception)e, classification);
  }
}
