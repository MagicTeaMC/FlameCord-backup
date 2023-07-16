package org.eclipse.aether.spi.connector;

import java.io.File;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.transfer.MetadataTransferException;
import org.eclipse.aether.transfer.TransferListener;

public final class MetadataUpload extends MetadataTransfer {
  public MetadataUpload() {}
  
  public MetadataUpload(Metadata metadata, File file) {
    setMetadata(metadata);
    setFile(file);
  }
  
  public MetadataUpload setMetadata(Metadata metadata) {
    super.setMetadata(metadata);
    return this;
  }
  
  public MetadataUpload setFile(File file) {
    super.setFile(file);
    return this;
  }
  
  public MetadataUpload setException(MetadataTransferException exception) {
    super.setException(exception);
    return this;
  }
  
  public MetadataUpload setListener(TransferListener listener) {
    super.setListener(listener);
    return this;
  }
  
  public MetadataUpload setTrace(RequestTrace trace) {
    super.setTrace(trace);
    return this;
  }
  
  public String toString() {
    return getMetadata() + " - " + getFile();
  }
}
