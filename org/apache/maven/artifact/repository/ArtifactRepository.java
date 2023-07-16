package org.apache.maven.artifact.repository;

import java.util.List;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.layout.ArtifactRepositoryLayout;
import org.apache.maven.repository.Proxy;

public interface ArtifactRepository {
  String pathOf(Artifact paramArtifact);
  
  String pathOfRemoteRepositoryMetadata(ArtifactMetadata paramArtifactMetadata);
  
  String pathOfLocalRepositoryMetadata(ArtifactMetadata paramArtifactMetadata, ArtifactRepository paramArtifactRepository);
  
  String getUrl();
  
  void setUrl(String paramString);
  
  String getBasedir();
  
  String getProtocol();
  
  String getId();
  
  void setId(String paramString);
  
  ArtifactRepositoryPolicy getSnapshots();
  
  void setSnapshotUpdatePolicy(ArtifactRepositoryPolicy paramArtifactRepositoryPolicy);
  
  ArtifactRepositoryPolicy getReleases();
  
  void setReleaseUpdatePolicy(ArtifactRepositoryPolicy paramArtifactRepositoryPolicy);
  
  ArtifactRepositoryLayout getLayout();
  
  void setLayout(ArtifactRepositoryLayout paramArtifactRepositoryLayout);
  
  String getKey();
  
  @Deprecated
  boolean isUniqueVersion();
  
  @Deprecated
  boolean isBlacklisted();
  
  @Deprecated
  void setBlacklisted(boolean paramBoolean);
  
  boolean isBlocked();
  
  void setBlocked(boolean paramBoolean);
  
  Artifact find(Artifact paramArtifact);
  
  List<String> findVersions(Artifact paramArtifact);
  
  boolean isProjectAware();
  
  void setAuthentication(Authentication paramAuthentication);
  
  Authentication getAuthentication();
  
  void setProxy(Proxy paramProxy);
  
  Proxy getProxy();
  
  List<ArtifactRepository> getMirroredRepositories();
  
  void setMirroredRepositories(List<ArtifactRepository> paramList);
}
