package org.eclipse.aether.transfer;

public abstract class AbstractTransferListener implements TransferListener {
  public void transferInitiated(TransferEvent event) throws TransferCancelledException {}
  
  public void transferStarted(TransferEvent event) throws TransferCancelledException {}
  
  public void transferProgressed(TransferEvent event) throws TransferCancelledException {}
  
  public void transferCorrupted(TransferEvent event) throws TransferCancelledException {}
  
  public void transferSucceeded(TransferEvent event) {}
  
  public void transferFailed(TransferEvent event) {}
}
