package org.eclipse.aether.connector.basic;

import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.ArtifactTransfer;
import org.eclipse.aether.transfer.ArtifactNotFoundException;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.TransferEvent;

final class ArtifactTransportListener extends TransferTransportListener<ArtifactTransfer> {
  private final RemoteRepository repository;
  
  ArtifactTransportListener(ArtifactTransfer transfer, RemoteRepository repository, TransferEvent.Builder eventBuilder) {
    super(transfer, eventBuilder);
    this.repository = repository;
  }
  
  public void transferFailed(Exception exception, int classification) {
    ArtifactTransferException e;
    if (classification == 1) {
      ArtifactNotFoundException artifactNotFoundException = new ArtifactNotFoundException(getTransfer().getArtifact(), this.repository);
    } else {
      e = new ArtifactTransferException(getTransfer().getArtifact(), this.repository, exception);
    } 
    getTransfer().setException(e);
    super.transferFailed((Exception)e, classification);
  }
}
