package org.eclipse.aether.internal.impl;

import org.eclipse.aether.transfer.ChecksumFailureException;
import org.eclipse.aether.transfer.TransferResource;

final class WarnChecksumPolicy extends AbstractChecksumPolicy {
  WarnChecksumPolicy(TransferResource resource) {
    super(resource);
  }
  
  public boolean onTransferChecksumFailure(ChecksumFailureException exception) {
    this.logger.warn("Could not validate integrity of download from {}{}", new Object[] { this.resource.getRepositoryUrl(), this.resource
          .getResourceName(), exception });
    return true;
  }
}
