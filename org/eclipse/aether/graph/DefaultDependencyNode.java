package org.eclipse.aether.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

public final class DefaultDependencyNode implements DependencyNode {
  private List<DependencyNode> children;
  
  private Dependency dependency;
  
  private Artifact artifact;
  
  private List<? extends Artifact> relocations;
  
  private Collection<? extends Artifact> aliases;
  
  private VersionConstraint versionConstraint;
  
  private Version version;
  
  private byte managedBits;
  
  private List<RemoteRepository> repositories;
  
  private String context;
  
  private Map<Object, Object> data;
  
  public DefaultDependencyNode(Dependency dependency) {
    this.dependency = dependency;
    this.artifact = (dependency != null) ? dependency.getArtifact() : null;
    this.children = new ArrayList<>(0);
    this.aliases = Collections.emptyList();
    this.relocations = Collections.emptyList();
    this.repositories = Collections.emptyList();
    this.context = "";
    this.data = Collections.emptyMap();
  }
  
  public DefaultDependencyNode(Artifact artifact) {
    this.artifact = artifact;
    this.children = new ArrayList<>(0);
    this.aliases = Collections.emptyList();
    this.relocations = Collections.emptyList();
    this.repositories = Collections.emptyList();
    this.context = "";
    this.data = Collections.emptyMap();
  }
  
  public DefaultDependencyNode(DependencyNode node) {
    this.dependency = node.getDependency();
    this.artifact = node.getArtifact();
    this.children = new ArrayList<>(0);
    setAliases(node.getAliases());
    setRequestContext(node.getRequestContext());
    setManagedBits(node.getManagedBits());
    setRelocations(node.getRelocations());
    setRepositories(node.getRepositories());
    setVersion(node.getVersion());
    setVersionConstraint(node.getVersionConstraint());
    Map<?, ?> data = node.getData();
    setData(data.isEmpty() ? null : new HashMap<>(data));
  }
  
  public List<DependencyNode> getChildren() {
    return this.children;
  }
  
  public void setChildren(List<DependencyNode> children) {
    if (children == null) {
      this.children = new ArrayList<>(0);
    } else {
      this.children = children;
    } 
  }
  
  public Dependency getDependency() {
    return this.dependency;
  }
  
  public Artifact getArtifact() {
    return this.artifact;
  }
  
  public void setArtifact(Artifact artifact) {
    if (this.dependency == null)
      throw new IllegalStateException("node does not have a dependency"); 
    this.dependency = this.dependency.setArtifact(artifact);
    this.artifact = this.dependency.getArtifact();
  }
  
  public List<? extends Artifact> getRelocations() {
    return this.relocations;
  }
  
  public void setRelocations(List<? extends Artifact> relocations) {
    if (relocations == null || relocations.isEmpty()) {
      this.relocations = Collections.emptyList();
    } else {
      this.relocations = relocations;
    } 
  }
  
  public Collection<? extends Artifact> getAliases() {
    return this.aliases;
  }
  
  public void setAliases(Collection<? extends Artifact> aliases) {
    if (aliases == null || aliases.isEmpty()) {
      this.aliases = Collections.emptyList();
    } else {
      this.aliases = aliases;
    } 
  }
  
  public VersionConstraint getVersionConstraint() {
    return this.versionConstraint;
  }
  
  public void setVersionConstraint(VersionConstraint versionConstraint) {
    this.versionConstraint = versionConstraint;
  }
  
  public Version getVersion() {
    return this.version;
  }
  
  public void setVersion(Version version) {
    this.version = version;
  }
  
  public void setScope(String scope) {
    if (this.dependency == null)
      throw new IllegalStateException("node does not have a dependency"); 
    this.dependency = this.dependency.setScope(scope);
  }
  
  public void setOptional(Boolean optional) {
    if (this.dependency == null)
      throw new IllegalStateException("node does not have a dependency"); 
    this.dependency = this.dependency.setOptional(optional);
  }
  
  public int getManagedBits() {
    return this.managedBits;
  }
  
  public void setManagedBits(int managedBits) {
    this.managedBits = (byte)(managedBits & 0x1F);
  }
  
  public List<RemoteRepository> getRepositories() {
    return this.repositories;
  }
  
  public void setRepositories(List<RemoteRepository> repositories) {
    if (repositories == null || repositories.isEmpty()) {
      this.repositories = Collections.emptyList();
    } else {
      this.repositories = repositories;
    } 
  }
  
  public String getRequestContext() {
    return this.context;
  }
  
  public void setRequestContext(String context) {
    this.context = (context != null) ? context : "";
  }
  
  public Map<Object, Object> getData() {
    return this.data;
  }
  
  public void setData(Map<Object, Object> data) {
    if (data == null) {
      this.data = Collections.emptyMap();
    } else {
      this.data = data;
    } 
  }
  
  public void setData(Object key, Object value) {
    Objects.requireNonNull(key, "key cannot be null");
    if (value == null) {
      if (!this.data.isEmpty()) {
        this.data.remove(key);
        if (this.data.isEmpty())
          this.data = Collections.emptyMap(); 
      } 
    } else {
      if (this.data.isEmpty())
        this.data = new HashMap<>(1, 2.0F); 
      this.data.put(key, value);
    } 
  }
  
  public boolean accept(DependencyVisitor visitor) {
    if (visitor.visitEnter(this))
      for (DependencyNode child : this.children) {
        if (!child.accept(visitor))
          break; 
      }  
    return visitor.visitLeave(this);
  }
  
  public String toString() {
    Dependency dep = getDependency();
    if (dep == null)
      return String.valueOf(getArtifact()); 
    return dep.toString();
  }
}
