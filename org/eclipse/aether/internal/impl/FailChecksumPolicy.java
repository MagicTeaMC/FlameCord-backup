package org.eclipse.aether.internal.impl;

import org.eclipse.aether.transfer.ChecksumFailureException;
import org.eclipse.aether.transfer.TransferResource;

final class FailChecksumPolicy extends AbstractChecksumPolicy {
  FailChecksumPolicy(TransferResource resource) {
    super(resource);
  }
  
  public boolean onTransferChecksumFailure(ChecksumFailureException error) {
    return false;
  }
}
