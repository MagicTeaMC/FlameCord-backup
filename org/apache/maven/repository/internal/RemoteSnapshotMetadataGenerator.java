package org.apache.maven.repository.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.util.ConfigUtils;

class RemoteSnapshotMetadataGenerator implements MetadataGenerator {
  private final Map<Object, RemoteSnapshotMetadata> snapshots;
  
  private final boolean legacyFormat;
  
  private final Date timestamp;
  
  RemoteSnapshotMetadataGenerator(RepositorySystemSession session, DeployRequest request) {
    this.legacyFormat = ConfigUtils.getBoolean(session, false, new String[] { "maven.metadata.legacy" });
    this.timestamp = (Date)ConfigUtils.getObject(session, new Date(), new String[] { "maven.startTime" });
    this.snapshots = new LinkedHashMap<>();
    for (Metadata metadata : request.getMetadata()) {
      if (metadata instanceof RemoteSnapshotMetadata) {
        RemoteSnapshotMetadata snapshotMetadata = (RemoteSnapshotMetadata)metadata;
        this.snapshots.put(snapshotMetadata.getKey(), snapshotMetadata);
      } 
    } 
  }
  
  public Collection<? extends Metadata> prepare(Collection<? extends Artifact> artifacts) {
    for (Artifact artifact : artifacts) {
      if (artifact.isSnapshot()) {
        Object key = RemoteSnapshotMetadata.getKey(artifact);
        RemoteSnapshotMetadata snapshotMetadata = this.snapshots.get(key);
        if (snapshotMetadata == null) {
          snapshotMetadata = new RemoteSnapshotMetadata(artifact, this.legacyFormat, this.timestamp);
          this.snapshots.put(key, snapshotMetadata);
        } 
        snapshotMetadata.bind(artifact);
      } 
    } 
    return (Collection)this.snapshots.values();
  }
  
  public Artifact transformArtifact(Artifact artifact) {
    if (artifact.isSnapshot() && artifact.getVersion().equals(artifact.getBaseVersion())) {
      Object key = RemoteSnapshotMetadata.getKey(artifact);
      RemoteSnapshotMetadata snapshotMetadata = this.snapshots.get(key);
      if (snapshotMetadata != null)
        artifact = artifact.setVersion(snapshotMetadata.getExpandedVersion(artifact)); 
    } 
    return artifact;
  }
  
  public Collection<? extends Metadata> finish(Collection<? extends Artifact> artifacts) {
    return Collections.emptyList();
  }
}
