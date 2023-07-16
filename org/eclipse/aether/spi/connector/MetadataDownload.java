package org.eclipse.aether.spi.connector;

import java.io.File;
import java.util.Collections;
import java.util.List;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.MetadataTransferException;
import org.eclipse.aether.transfer.TransferListener;

public final class MetadataDownload extends MetadataTransfer {
  private String checksumPolicy = "";
  
  private String context = "";
  
  private List<RemoteRepository> repositories = Collections.emptyList();
  
  public MetadataDownload(Metadata metadata, String context, File file, String checksumPolicy) {
    setMetadata(metadata);
    setFile(file);
    setChecksumPolicy(checksumPolicy);
    setRequestContext(context);
  }
  
  public MetadataDownload setMetadata(Metadata metadata) {
    super.setMetadata(metadata);
    return this;
  }
  
  public MetadataDownload setFile(File file) {
    super.setFile(file);
    return this;
  }
  
  public String getChecksumPolicy() {
    return this.checksumPolicy;
  }
  
  public MetadataDownload setChecksumPolicy(String checksumPolicy) {
    this.checksumPolicy = (checksumPolicy != null) ? checksumPolicy : "";
    return this;
  }
  
  public String getRequestContext() {
    return this.context;
  }
  
  public MetadataDownload setRequestContext(String context) {
    this.context = (context != null) ? context : "";
    return this;
  }
  
  public List<RemoteRepository> getRepositories() {
    return this.repositories;
  }
  
  public MetadataDownload setRepositories(List<RemoteRepository> repositories) {
    if (repositories == null) {
      this.repositories = Collections.emptyList();
    } else {
      this.repositories = repositories;
    } 
    return this;
  }
  
  public MetadataDownload setException(MetadataTransferException exception) {
    super.setException(exception);
    return this;
  }
  
  public MetadataDownload setListener(TransferListener listener) {
    super.setListener(listener);
    return this;
  }
  
  public MetadataDownload setTrace(RequestTrace trace) {
    super.setTrace(trace);
    return this;
  }
  
  public String toString() {
    return getMetadata() + " - " + getFile();
  }
  
  public MetadataDownload() {}
}
