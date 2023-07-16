package org.eclipse.aether.graph;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

public interface DependencyNode {
  public static final int MANAGED_VERSION = 1;
  
  public static final int MANAGED_SCOPE = 2;
  
  public static final int MANAGED_OPTIONAL = 4;
  
  public static final int MANAGED_PROPERTIES = 8;
  
  public static final int MANAGED_EXCLUSIONS = 16;
  
  List<DependencyNode> getChildren();
  
  void setChildren(List<DependencyNode> paramList);
  
  Dependency getDependency();
  
  Artifact getArtifact();
  
  void setArtifact(Artifact paramArtifact);
  
  List<? extends Artifact> getRelocations();
  
  Collection<? extends Artifact> getAliases();
  
  VersionConstraint getVersionConstraint();
  
  Version getVersion();
  
  void setScope(String paramString);
  
  void setOptional(Boolean paramBoolean);
  
  int getManagedBits();
  
  List<RemoteRepository> getRepositories();
  
  String getRequestContext();
  
  void setRequestContext(String paramString);
  
  Map<?, ?> getData();
  
  void setData(Map<Object, Object> paramMap);
  
  void setData(Object paramObject1, Object paramObject2);
  
  boolean accept(DependencyVisitor paramDependencyVisitor);
}
