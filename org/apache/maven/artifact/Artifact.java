package org.apache.maven.artifact;

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.maven.artifact.handler.ArtifactHandler;
import org.apache.maven.artifact.metadata.ArtifactMetadata;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.OverConstrainedVersionException;
import org.apache.maven.artifact.versioning.VersionRange;

public interface Artifact extends Comparable<Artifact> {
  public static final String RELEASE_VERSION = "RELEASE";
  
  public static final String LATEST_VERSION = "LATEST";
  
  public static final String SNAPSHOT_VERSION = "SNAPSHOT";
  
  public static final Pattern VERSION_FILE_PATTERN = Pattern.compile("^(.*)-([0-9]{8}\\.[0-9]{6})-([0-9]+)$");
  
  public static final String SCOPE_COMPILE = "compile";
  
  public static final String SCOPE_COMPILE_PLUS_RUNTIME = "compile+runtime";
  
  public static final String SCOPE_TEST = "test";
  
  public static final String SCOPE_RUNTIME = "runtime";
  
  public static final String SCOPE_RUNTIME_PLUS_SYSTEM = "runtime+system";
  
  public static final String SCOPE_PROVIDED = "provided";
  
  public static final String SCOPE_SYSTEM = "system";
  
  public static final String SCOPE_IMPORT = "import";
  
  String getGroupId();
  
  String getArtifactId();
  
  String getVersion();
  
  void setVersion(String paramString);
  
  String getScope();
  
  String getType();
  
  String getClassifier();
  
  boolean hasClassifier();
  
  File getFile();
  
  void setFile(File paramFile);
  
  String getBaseVersion();
  
  void setBaseVersion(String paramString);
  
  String getId();
  
  String getDependencyConflictId();
  
  void addMetadata(ArtifactMetadata paramArtifactMetadata);
  
  Collection<ArtifactMetadata> getMetadataList();
  
  void setRepository(ArtifactRepository paramArtifactRepository);
  
  ArtifactRepository getRepository();
  
  void updateVersion(String paramString, ArtifactRepository paramArtifactRepository);
  
  String getDownloadUrl();
  
  void setDownloadUrl(String paramString);
  
  ArtifactFilter getDependencyFilter();
  
  void setDependencyFilter(ArtifactFilter paramArtifactFilter);
  
  ArtifactHandler getArtifactHandler();
  
  List<String> getDependencyTrail();
  
  void setDependencyTrail(List<String> paramList);
  
  void setScope(String paramString);
  
  VersionRange getVersionRange();
  
  void setVersionRange(VersionRange paramVersionRange);
  
  void selectVersion(String paramString);
  
  void setGroupId(String paramString);
  
  void setArtifactId(String paramString);
  
  boolean isSnapshot();
  
  void setResolved(boolean paramBoolean);
  
  boolean isResolved();
  
  void setResolvedVersion(String paramString);
  
  void setArtifactHandler(ArtifactHandler paramArtifactHandler);
  
  boolean isRelease();
  
  void setRelease(boolean paramBoolean);
  
  List<ArtifactVersion> getAvailableVersions();
  
  void setAvailableVersions(List<ArtifactVersion> paramList);
  
  boolean isOptional();
  
  void setOptional(boolean paramBoolean);
  
  ArtifactVersion getSelectedVersion() throws OverConstrainedVersionException;
  
  boolean isSelectedVersionKnown() throws OverConstrainedVersionException;
}
