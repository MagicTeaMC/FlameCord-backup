package org.apache.maven.repository.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import org.apache.maven.artifact.repository.metadata.Metadata;
import org.apache.maven.artifact.repository.metadata.Versioning;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

final class VersionsMetadata extends MavenMetadata {
  private final Artifact artifact;
  
  VersionsMetadata(Artifact artifact, Date timestamp) {
    super(createRepositoryMetadata(artifact), null, timestamp);
    this.artifact = artifact;
  }
  
  VersionsMetadata(Artifact artifact, File file, Date timestamp) {
    super(createRepositoryMetadata(artifact), file, timestamp);
    this.artifact = artifact;
  }
  
  private static Metadata createRepositoryMetadata(Artifact artifact) {
    Metadata metadata = new Metadata();
    metadata.setGroupId(artifact.getGroupId());
    metadata.setArtifactId(artifact.getArtifactId());
    Versioning versioning = new Versioning();
    versioning.addVersion(artifact.getBaseVersion());
    if (!artifact.isSnapshot())
      versioning.setRelease(artifact.getBaseVersion()); 
    if ("maven-plugin".equals(artifact.getProperty("type", "")))
      versioning.setLatest(artifact.getBaseVersion()); 
    metadata.setVersioning(versioning);
    return metadata;
  }
  
  protected void merge(Metadata recessive) {
    Versioning versioning = this.metadata.getVersioning();
    versioning.setLastUpdatedTimestamp(this.timestamp);
    if (recessive.getVersioning() != null) {
      if (versioning.getLatest() == null)
        versioning.setLatest(recessive.getVersioning().getLatest()); 
      if (versioning.getRelease() == null)
        versioning.setRelease(recessive.getVersioning().getRelease()); 
      Collection<String> versions = new LinkedHashSet<>(recessive.getVersioning().getVersions());
      versions.addAll(versioning.getVersions());
      versioning.setVersions(new ArrayList<>(versions));
    } 
  }
  
  public Object getKey() {
    return getGroupId() + ':' + getArtifactId();
  }
  
  public static Object getKey(Artifact artifact) {
    return artifact.getGroupId() + ':' + artifact.getArtifactId();
  }
  
  public MavenMetadata setFile(File file) {
    return new VersionsMetadata(this.artifact, file, this.timestamp);
  }
  
  public String getGroupId() {
    return this.artifact.getGroupId();
  }
  
  public String getArtifactId() {
    return this.artifact.getArtifactId();
  }
  
  public String getVersion() {
    return "";
  }
  
  public Metadata.Nature getNature() {
    return this.artifact.isSnapshot() ? Metadata.Nature.RELEASE_OR_SNAPSHOT : Metadata.Nature.RELEASE;
  }
}
