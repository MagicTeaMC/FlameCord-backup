package org.eclipse.aether.resolution;

import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;

public final class MetadataRequest {
  private Metadata metadata;
  
  private RemoteRepository repository;
  
  private String context = "";
  
  private boolean deleteLocalCopyIfMissing;
  
  private boolean favorLocalRepository;
  
  private RequestTrace trace;
  
  public MetadataRequest() {}
  
  public MetadataRequest(Metadata metadata) {
    setMetadata(metadata);
  }
  
  public MetadataRequest(Metadata metadata, RemoteRepository repository, String context) {
    setMetadata(metadata);
    setRepository(repository);
    setRequestContext(context);
  }
  
  public Metadata getMetadata() {
    return this.metadata;
  }
  
  public MetadataRequest setMetadata(Metadata metadata) {
    this.metadata = metadata;
    return this;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public MetadataRequest setRepository(RemoteRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public String getRequestContext() {
    return this.context;
  }
  
  public MetadataRequest setRequestContext(String context) {
    this.context = (context != null) ? context : "";
    return this;
  }
  
  public boolean isDeleteLocalCopyIfMissing() {
    return this.deleteLocalCopyIfMissing;
  }
  
  public MetadataRequest setDeleteLocalCopyIfMissing(boolean deleteLocalCopyIfMissing) {
    this.deleteLocalCopyIfMissing = deleteLocalCopyIfMissing;
    return this;
  }
  
  public boolean isFavorLocalRepository() {
    return this.favorLocalRepository;
  }
  
  public MetadataRequest setFavorLocalRepository(boolean favorLocalRepository) {
    this.favorLocalRepository = favorLocalRepository;
    return this;
  }
  
  public RequestTrace getTrace() {
    return this.trace;
  }
  
  public MetadataRequest setTrace(RequestTrace trace) {
    this.trace = trace;
    return this;
  }
  
  public String toString() {
    return getMetadata() + " < " + getRepository();
  }
}
