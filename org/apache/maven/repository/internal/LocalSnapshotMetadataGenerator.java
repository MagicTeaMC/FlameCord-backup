package org.apache.maven.repository.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.util.ConfigUtils;

class LocalSnapshotMetadataGenerator implements MetadataGenerator {
  private Map<Object, LocalSnapshotMetadata> snapshots;
  
  private final boolean legacyFormat;
  
  private final Date timestamp;
  
  LocalSnapshotMetadataGenerator(RepositorySystemSession session, InstallRequest request) {
    this.legacyFormat = ConfigUtils.getBoolean(session.getConfigProperties(), false, new String[] { "maven.metadata.legacy" });
    this.timestamp = (Date)ConfigUtils.getObject(session, new Date(), new String[] { "maven.startTime" });
    this.snapshots = new LinkedHashMap<>();
  }
  
  public Collection<? extends Metadata> prepare(Collection<? extends Artifact> artifacts) {
    for (Artifact artifact : artifacts) {
      if (artifact.isSnapshot()) {
        Object key = LocalSnapshotMetadata.getKey(artifact);
        LocalSnapshotMetadata snapshotMetadata = this.snapshots.get(key);
        if (snapshotMetadata == null) {
          snapshotMetadata = new LocalSnapshotMetadata(artifact, this.legacyFormat, this.timestamp);
          this.snapshots.put(key, snapshotMetadata);
        } 
        snapshotMetadata.bind(artifact);
      } 
    } 
    return Collections.emptyList();
  }
  
  public Artifact transformArtifact(Artifact artifact) {
    return artifact;
  }
  
  public Collection<? extends Metadata> finish(Collection<? extends Artifact> artifacts) {
    return (Collection)this.snapshots.values();
  }
}
