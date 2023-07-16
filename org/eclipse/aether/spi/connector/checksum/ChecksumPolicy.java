package org.eclipse.aether.spi.connector.checksum;

import org.eclipse.aether.transfer.ChecksumFailureException;

public interface ChecksumPolicy {
  public static final int KIND_UNOFFICIAL = 1;
  
  boolean onChecksumMatch(String paramString, int paramInt);
  
  void onChecksumMismatch(String paramString, int paramInt, ChecksumFailureException paramChecksumFailureException) throws ChecksumFailureException;
  
  void onChecksumError(String paramString, int paramInt, ChecksumFailureException paramChecksumFailureException) throws ChecksumFailureException;
  
  void onNoMoreChecksums() throws ChecksumFailureException;
  
  void onTransferRetry();
  
  boolean onTransferChecksumFailure(ChecksumFailureException paramChecksumFailureException);
}
