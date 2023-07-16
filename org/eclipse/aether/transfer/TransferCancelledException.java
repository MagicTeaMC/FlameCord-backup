package org.eclipse.aether.transfer;

import org.eclipse.aether.RepositoryException;

public class TransferCancelledException extends RepositoryException {
  public TransferCancelledException() {
    super("The operation was cancelled.");
  }
  
  public TransferCancelledException(String message) {
    super(message);
  }
  
  public TransferCancelledException(String message, Throwable cause) {
    super(message, cause);
  }
}
