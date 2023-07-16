package org.eclipse.aether.spi.connector;

import java.io.File;
import org.eclipse.aether.RequestTrace;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.transfer.ArtifactTransferException;
import org.eclipse.aether.transfer.TransferListener;
import org.eclipse.aether.transform.FileTransformer;

public final class ArtifactUpload extends ArtifactTransfer {
  private FileTransformer fileTransformer;
  
  public ArtifactUpload() {}
  
  public ArtifactUpload(Artifact artifact, File file) {
    setArtifact(artifact);
    setFile(file);
  }
  
  public ArtifactUpload(Artifact artifact, File file, FileTransformer fileTransformer) {
    setArtifact(artifact);
    setFile(file);
    setFileTransformer(fileTransformer);
  }
  
  public ArtifactUpload setArtifact(Artifact artifact) {
    super.setArtifact(artifact);
    return this;
  }
  
  public ArtifactUpload setFile(File file) {
    super.setFile(file);
    return this;
  }
  
  public ArtifactUpload setException(ArtifactTransferException exception) {
    super.setException(exception);
    return this;
  }
  
  public ArtifactUpload setListener(TransferListener listener) {
    super.setListener(listener);
    return this;
  }
  
  public ArtifactUpload setTrace(RequestTrace trace) {
    super.setTrace(trace);
    return this;
  }
  
  public ArtifactUpload setFileTransformer(FileTransformer fileTransformer) {
    this.fileTransformer = fileTransformer;
    return this;
  }
  
  public FileTransformer getFileTransformer() {
    return this.fileTransformer;
  }
  
  public String toString() {
    if (getFileTransformer() != null)
      return getArtifact() + " >>> " + getFileTransformer().transformArtifact(getArtifact()) + " - " + 
        getFile(); 
    return getArtifact() + " - " + getFile();
  }
}
