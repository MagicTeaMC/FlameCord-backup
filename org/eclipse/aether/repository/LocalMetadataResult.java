package org.eclipse.aether.repository;

import java.io.File;
import java.util.Objects;

public final class LocalMetadataResult {
  private final LocalMetadataRequest request;
  
  private File file;
  
  private boolean stale;
  
  public LocalMetadataResult(LocalMetadataRequest request) {
    this.request = Objects.<LocalMetadataRequest>requireNonNull(request, "local metadata request cannot be null");
  }
  
  public LocalMetadataRequest getRequest() {
    return this.request;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public LocalMetadataResult setFile(File file) {
    this.file = file;
    return this;
  }
  
  public boolean isStale() {
    return this.stale;
  }
  
  public LocalMetadataResult setStale(boolean stale) {
    this.stale = stale;
    return this;
  }
  
  public String toString() {
    return this.request.toString() + "(" + getFile() + ")";
  }
}
