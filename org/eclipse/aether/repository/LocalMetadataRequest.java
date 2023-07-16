package org.eclipse.aether.repository;

import org.eclipse.aether.metadata.Metadata;

public final class LocalMetadataRequest {
  private Metadata metadata;
  
  private String context = "";
  
  private RemoteRepository repository = null;
  
  public LocalMetadataRequest() {}
  
  public LocalMetadataRequest(Metadata metadata, RemoteRepository repository, String context) {
    setMetadata(metadata);
    setRepository(repository);
    setContext(context);
  }
  
  public Metadata getMetadata() {
    return this.metadata;
  }
  
  public LocalMetadataRequest setMetadata(Metadata metadata) {
    this.metadata = metadata;
    return this;
  }
  
  public String getContext() {
    return this.context;
  }
  
  public LocalMetadataRequest setContext(String context) {
    this.context = (context != null) ? context : "";
    return this;
  }
  
  public RemoteRepository getRepository() {
    return this.repository;
  }
  
  public LocalMetadataRequest setRepository(RemoteRepository repository) {
    this.repository = repository;
    return this;
  }
  
  public String toString() {
    return getMetadata() + " @ " + getRepository();
  }
}
