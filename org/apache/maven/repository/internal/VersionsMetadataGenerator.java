package org.apache.maven.repository.internal;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.installation.InstallRequest;
import org.eclipse.aether.metadata.Metadata;
import org.eclipse.aether.util.ConfigUtils;

class VersionsMetadataGenerator implements MetadataGenerator {
  private Map<Object, VersionsMetadata> versions;
  
  private Map<Object, VersionsMetadata> processedVersions;
  
  private final Date timestamp;
  
  VersionsMetadataGenerator(RepositorySystemSession session, InstallRequest request) {
    this(session, request.getMetadata());
  }
  
  VersionsMetadataGenerator(RepositorySystemSession session, DeployRequest request) {
    this(session, request.getMetadata());
  }
  
  private VersionsMetadataGenerator(RepositorySystemSession session, Collection<? extends Metadata> metadatas) {
    this.versions = new LinkedHashMap<>();
    this.processedVersions = new LinkedHashMap<>();
    this.timestamp = (Date)ConfigUtils.getObject(session, new Date(), new String[] { "maven.startTime" });
    for (Iterator<? extends Metadata> it = metadatas.iterator(); it.hasNext(); ) {
      Metadata metadata = it.next();
      if (metadata instanceof VersionsMetadata) {
        it.remove();
        VersionsMetadata versionsMetadata = (VersionsMetadata)metadata;
        this.processedVersions.put(versionsMetadata.getKey(), versionsMetadata);
      } 
    } 
  }
  
  public Collection<? extends Metadata> prepare(Collection<? extends Artifact> artifacts) {
    return Collections.emptyList();
  }
  
  public Artifact transformArtifact(Artifact artifact) {
    return artifact;
  }
  
  public Collection<? extends Metadata> finish(Collection<? extends Artifact> artifacts) {
    for (Artifact artifact : artifacts) {
      Object key = VersionsMetadata.getKey(artifact);
      if (this.processedVersions.get(key) == null) {
        VersionsMetadata versionsMetadata = this.versions.get(key);
        if (versionsMetadata == null) {
          versionsMetadata = new VersionsMetadata(artifact, this.timestamp);
          this.versions.put(key, versionsMetadata);
        } 
      } 
    } 
    return (Collection)this.versions.values();
  }
}
