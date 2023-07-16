package org.eclipse.aether.spi.connector;

import java.io.File;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.transfer.MetadataTransferException;

public abstract class MetadataTransfer extends Transfer {
  private Metadata metadata;
  
  private File file;
  
  private MetadataTransferException exception;
  
  public Metadata getMetadata() {
    return this.metadata;
  }
  
  public MetadataTransfer setMetadata(Metadata metadata) {
    this.metadata = metadata;
    return this;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public MetadataTransfer setFile(File file) {
    this.file = file;
    return this;
  }
  
  public MetadataTransferException getException() {
    return this.exception;
  }
  
  public MetadataTransfer setException(MetadataTransferException exception) {
    this.exception = exception;
    return this;
  }
}
