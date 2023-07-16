package org.apache.maven.repository.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

abstract class MavenSnapshotMetadata extends MavenMetadata {
  static final String SNAPSHOT = "SNAPSHOT";
  
  protected final Collection<Artifact> artifacts = new ArrayList<>();
  
  protected final boolean legacyFormat;
  
  protected MavenSnapshotMetadata(Metadata metadata, File file, boolean legacyFormat, Date timestamp) {
    super(metadata, file, timestamp);
    this.legacyFormat = legacyFormat;
  }
  
  protected static Metadata createRepositoryMetadata(Artifact artifact, boolean legacyFormat) {
    Metadata metadata = new Metadata();
    if (!legacyFormat)
      metadata.setModelVersion("1.1.0"); 
    metadata.setGroupId(artifact.getGroupId());
    metadata.setArtifactId(artifact.getArtifactId());
    metadata.setVersion(artifact.getBaseVersion());
    return metadata;
  }
  
  public void bind(Artifact artifact) {
    this.artifacts.add(artifact);
  }
  
  public Object getKey() {
    return getGroupId() + ':' + getArtifactId() + ':' + getVersion();
  }
  
  public static Object getKey(Artifact artifact) {
    return artifact.getGroupId() + ':' + artifact.getArtifactId() + ':' + artifact.getBaseVersion();
  }
  
  protected String getKey(String classifier, String extension) {
    return classifier + ':' + extension;
  }
  
  public String getGroupId() {
    return this.metadata.getGroupId();
  }
  
  public String getArtifactId() {
    return this.metadata.getArtifactId();
  }
  
  public String getVersion() {
    return this.metadata.getVersion();
  }
  
  public Metadata.Nature getNature() {
    return Metadata.Nature.SNAPSHOT;
  }
}
