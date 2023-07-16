package org.eclipse.aether.resolution;

import java.util.Objects;
import org.eclipse.aether.metadata.Metadata;

public final class MetadataResult {
  private final MetadataRequest request;
  
  private Exception exception;
  
  private boolean updated;
  
  private Metadata metadata;
  
  public MetadataResult(MetadataRequest request) {
    this.request = Objects.<MetadataRequest>requireNonNull(request, "metadata request cannot be null");
  }
  
  public MetadataRequest getRequest() {
    return this.request;
  }
  
  public Metadata getMetadata() {
    return this.metadata;
  }
  
  public MetadataResult setMetadata(Metadata metadata) {
    this.metadata = metadata;
    return this;
  }
  
  public MetadataResult setException(Exception exception) {
    this.exception = exception;
    return this;
  }
  
  public Exception getException() {
    return this.exception;
  }
  
  public MetadataResult setUpdated(boolean updated) {
    this.updated = updated;
    return this;
  }
  
  public boolean isUpdated() {
    return this.updated;
  }
  
  public boolean isResolved() {
    return (getMetadata() != null && getMetadata().getFile() != null);
  }
  
  public boolean isMissing() {
    return getException() instanceof org.eclipse.aether.transfer.MetadataNotFoundException;
  }
  
  public String toString() {
    return getMetadata() + (isUpdated() ? " (updated)" : " (cached)");
  }
}
