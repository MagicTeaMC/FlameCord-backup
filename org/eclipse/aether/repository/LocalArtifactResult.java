package org.eclipse.aether.repository;

import java.io.File;
import java.util.Objects;

public final class LocalArtifactResult {
  private final LocalArtifactRequest request;
  
  private File file;
  
  private boolean available;
  
  private RemoteRepository repository;
  
  public LocalArtifactResult(LocalArtifactRequest request) {
    this.request = Objects.<LocalArtifactRequest>requireNonNull(request, "local artifact request cannot be null");
  }
  
  public LocalArtifactRequest getRequest() {
    return this.request;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public LocalArtifactResult setFile(File file) {
    this.file = file;
    return this;
  }
  
  public boolean isAvailable() {
    return this.available;
  }
  
  public LocalArtifactResult setAvailable(boolean available) {
    this.available = available;
    return this;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public LocalArtifactResult setRepository(RemoteRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public String toString() {
    return getFile() + " (" + (isAvailable() ? "available" : "unavailable") + ")";
  }
}
