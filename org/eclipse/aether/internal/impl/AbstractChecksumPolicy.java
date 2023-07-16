package org.eclipse.aether.internal.impl;

import org.eclipse.aether.spi.connector.checksum.ChecksumPolicy;
import org.eclipse.aether.transfer.ChecksumFailureException;
import org.eclipse.aether.transfer.TransferResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class AbstractChecksumPolicy implements ChecksumPolicy {
  protected final Logger logger = LoggerFactory.getLogger(getClass());
  
  protected final TransferResource resource;
  
  protected AbstractChecksumPolicy(TransferResource resource) {
    this.resource = resource;
  }
  
  public boolean onChecksumMatch(String algorithm, int kind) {
    return true;
  }
  
  public void onChecksumMismatch(String algorithm, int kind, ChecksumFailureException exception) throws ChecksumFailureException {
    if ((kind & 0x1) == 0)
      throw exception; 
  }
  
  public void onChecksumError(String algorithm, int kind, ChecksumFailureException exception) throws ChecksumFailureException {
    this.logger.debug("Could not validate {} checksum for {}", new Object[] { algorithm, this.resource.getResourceName(), exception });
  }
  
  public void onNoMoreChecksums() throws ChecksumFailureException {
    throw new ChecksumFailureException("Checksum validation failed, no checksums available");
  }
  
  public void onTransferRetry() {}
}
