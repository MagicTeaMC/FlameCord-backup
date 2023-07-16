package org.apache.maven.repository.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Snapshot;
import org.apache.maven.artifact.repository.metadata.SnapshotVersion;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

final class LocalSnapshotMetadata extends MavenMetadata {
  private final Collection<Artifact> artifacts = new ArrayList<>();
  
  private final boolean legacyFormat;
  
  LocalSnapshotMetadata(Artifact artifact, boolean legacyFormat, Date timestamp) {
    super(createMetadata(artifact, legacyFormat), null, timestamp);
    this.legacyFormat = legacyFormat;
  }
  
  LocalSnapshotMetadata(Metadata metadata, File file, boolean legacyFormat, Date timestamp) {
    super(metadata, file, timestamp);
    this.legacyFormat = legacyFormat;
  }
  
  private static Metadata createMetadata(Artifact artifact, boolean legacyFormat) {
    Snapshot snapshot = new Snapshot();
    snapshot.setLocalCopy(true);
    Versioning versioning = new Versioning();
    versioning.setSnapshot(snapshot);
    Metadata metadata = new Metadata();
    metadata.setVersioning(versioning);
    metadata.setGroupId(artifact.getGroupId());
    metadata.setArtifactId(artifact.getArtifactId());
    metadata.setVersion(artifact.getBaseVersion());
    if (!legacyFormat)
      metadata.setModelVersion("1.1.0"); 
    return metadata;
  }
  
  public void bind(Artifact artifact) {
    this.artifacts.add(artifact);
  }
  
  public MavenMetadata setFile(File file) {
    return new LocalSnapshotMetadata(this.metadata, file, this.legacyFormat, this.timestamp);
  }
  
  public Object getKey() {
    return getGroupId() + ':' + getArtifactId() + ':' + getVersion();
  }
  
  public static Object getKey(Artifact artifact) {
    return artifact.getGroupId() + ':' + artifact.getArtifactId() + ':' + artifact.getBaseVersion();
  }
  
  protected void merge(Metadata recessive) {
    this.metadata.getVersioning().setLastUpdatedTimestamp(this.timestamp);
    if (!this.legacyFormat) {
      String lastUpdated = this.metadata.getVersioning().getLastUpdated();
      Map<String, SnapshotVersion> versions = new LinkedHashMap<>();
      for (Artifact artifact : this.artifacts) {
        SnapshotVersion sv = new SnapshotVersion();
        sv.setClassifier(artifact.getClassifier());
        sv.setExtension(artifact.getExtension());
        sv.setVersion(getVersion());
        sv.setUpdated(lastUpdated);
        versions.put(getKey(sv.getClassifier(), sv.getExtension()), sv);
      } 
      Versioning versioning = recessive.getVersioning();
      if (versioning != null)
        for (SnapshotVersion sv : versioning.getSnapshotVersions()) {
          String key = getKey(sv.getClassifier(), sv.getExtension());
          if (!versions.containsKey(key))
            versions.put(key, sv); 
        }  
      this.metadata.getVersioning().setSnapshotVersions(new ArrayList(versions.values()));
    } 
    this.artifacts.clear();
  }
  
  private String getKey(String classifier, String extension) {
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
