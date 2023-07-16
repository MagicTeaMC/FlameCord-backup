package org.eclipse.aether.transfer;

public interface TransferListener {
  void transferInitiated(TransferEvent paramTransferEvent) throws TransferCancelledException;
  
  void transferStarted(TransferEvent paramTransferEvent) throws TransferCancelledException;
  
  void transferProgressed(TransferEvent paramTransferEvent) throws TransferCancelledException;
  
  void transferCorrupted(TransferEvent paramTransferEvent) throws TransferCancelledException;
  
  void transferSucceeded(TransferEvent paramTransferEvent);
  
  void transferFailed(TransferEvent paramTransferEvent);
}
