package org.eclipse.aether.internal.impl.collect;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import org.eclipse.aether.RepositoryCache;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.collection.VersionFilter;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.ArtifactRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;
import org.eclipse.aether.version.VersionConstraint;

final class DataPool {
  private static final String ARTIFACT_POOL = DataPool.class.getName() + "$Artifact";
  
  private static final String DEPENDENCY_POOL = DataPool.class.getName() + "$Dependency";
  
  private static final String DESCRIPTORS = DataPool.class.getName() + "$Descriptors";
  
  static final ArtifactDescriptorResult NO_DESCRIPTOR = new ArtifactDescriptorResult(new ArtifactDescriptorRequest());
  
  private ObjectPool<Artifact> artifacts;
  
  private ObjectPool<Dependency> dependencies;
  
  private Map<Object, Descriptor> descriptors;
  
  private Map<Object, Constraint> constraints = new HashMap<>();
  
  private Map<Object, List<DependencyNode>> nodes = new HashMap<>(256);
  
  DataPool(RepositorySystemSession session) {
    RepositoryCache cache = session.getCache();
    if (cache != null) {
      this.artifacts = (ObjectPool<Artifact>)cache.get(session, ARTIFACT_POOL);
      this.dependencies = (ObjectPool<Dependency>)cache.get(session, DEPENDENCY_POOL);
      this.descriptors = (Map<Object, Descriptor>)cache.get(session, DESCRIPTORS);
    } 
    if (this.artifacts == null) {
      this.artifacts = new ObjectPool<>();
      if (cache != null)
        cache.put(session, ARTIFACT_POOL, this.artifacts); 
    } 
    if (this.dependencies == null) {
      this.dependencies = new ObjectPool<>();
      if (cache != null)
        cache.put(session, DEPENDENCY_POOL, this.dependencies); 
    } 
    if (this.descriptors == null) {
      this.descriptors = Collections.synchronizedMap(new WeakHashMap<>(256));
      if (cache != null)
        cache.put(session, DESCRIPTORS, this.descriptors); 
    } 
  }
  
  public Artifact intern(Artifact artifact) {
    return this.artifacts.intern(artifact);
  }
  
  public Dependency intern(Dependency dependency) {
    return this.dependencies.intern(dependency);
  }
  
  Object toKey(ArtifactDescriptorRequest request) {
    return request.getArtifact();
  }
  
  ArtifactDescriptorResult getDescriptor(Object key, ArtifactDescriptorRequest request) {
    Descriptor descriptor = this.descriptors.get(key);
    if (descriptor != null)
      return descriptor.toResult(request); 
    return null;
  }
  
  void putDescriptor(Object key, ArtifactDescriptorResult result) {
    this.descriptors.put(key, new GoodDescriptor(result));
  }
  
  void putDescriptor(Object key, ArtifactDescriptorException e) {
    this.descriptors.put(key, BadDescriptor.INSTANCE);
  }
  
  Object toKey(VersionRangeRequest request) {
    return new ConstraintKey(request);
  }
  
  VersionRangeResult getConstraint(Object key, VersionRangeRequest request) {
    Constraint constraint = this.constraints.get(key);
    if (constraint != null)
      return constraint.toResult(request); 
    return null;
  }
  
  void putConstraint(Object key, VersionRangeResult result) {
    this.constraints.put(key, new Constraint(result));
  }
  
  public Object toKey(Artifact artifact, List<RemoteRepository> repositories, DependencySelector selector, DependencyManager manager, DependencyTraverser traverser, VersionFilter filter) {
    return new GraphKey(artifact, repositories, selector, manager, traverser, filter);
  }
  
  public List<DependencyNode> getChildren(Object key) {
    return this.nodes.get(key);
  }
  
  public void putChildren(Object key, List<DependencyNode> children) {
    this.nodes.put(key, children);
  }
  
  static abstract class Descriptor {
    public abstract ArtifactDescriptorResult toResult(ArtifactDescriptorRequest param1ArtifactDescriptorRequest);
  }
  
  static final class GoodDescriptor extends Descriptor {
    final Artifact artifact;
    
    final List<Artifact> relocations;
    
    final Collection<Artifact> aliases;
    
    final List<RemoteRepository> repositories;
    
    final List<Dependency> dependencies;
    
    final List<Dependency> managedDependencies;
    
    GoodDescriptor(ArtifactDescriptorResult result) {
      this.artifact = result.getArtifact();
      this.relocations = result.getRelocations();
      this.aliases = result.getAliases();
      this.dependencies = result.getDependencies();
      this.managedDependencies = result.getManagedDependencies();
      this.repositories = result.getRepositories();
    }
    
    public ArtifactDescriptorResult toResult(ArtifactDescriptorRequest request) {
      ArtifactDescriptorResult result = new ArtifactDescriptorResult(request);
      result.setArtifact(this.artifact);
      result.setRelocations(this.relocations);
      result.setAliases(this.aliases);
      result.setDependencies(this.dependencies);
      result.setManagedDependencies(this.managedDependencies);
      result.setRepositories(this.repositories);
      return result;
    }
  }
  
  static final class BadDescriptor extends Descriptor {
    static final BadDescriptor INSTANCE = new BadDescriptor();
    
    public ArtifactDescriptorResult toResult(ArtifactDescriptorRequest request) {
      return DataPool.NO_DESCRIPTOR;
    }
  }
  
  private static final class Constraint {
    final VersionRepo[] repositories;
    
    final VersionConstraint versionConstraint;
    
    Constraint(VersionRangeResult result) {
      this.versionConstraint = result.getVersionConstraint();
      List<Version> versions = result.getVersions();
      this.repositories = new VersionRepo[versions.size()];
      int i = 0;
      for (Version version : versions)
        this.repositories[i++] = new VersionRepo(version, result.getRepository(version)); 
    }
    
    VersionRangeResult toResult(VersionRangeRequest request) {
      VersionRangeResult result = new VersionRangeResult(request);
      for (VersionRepo vr : this.repositories) {
        result.addVersion(vr.version);
        result.setRepository(vr.version, vr.repo);
      } 
      result.setVersionConstraint(this.versionConstraint);
      return result;
    }
    
    static final class VersionRepo {
      final Version version;
      
      final ArtifactRepository repo;
      
      VersionRepo(Version version, ArtifactRepository repo) {
        this.version = version;
        this.repo = repo;
      }
    }
  }
  
  static final class ConstraintKey {
    private final Artifact artifact;
    
    private final List<RemoteRepository> repositories;
    
    private final int hashCode;
    
    ConstraintKey(VersionRangeRequest request) {
      this.artifact = request.getArtifact();
      this.repositories = request.getRepositories();
      this.hashCode = this.artifact.hashCode();
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof ConstraintKey))
        return false; 
      ConstraintKey that = (ConstraintKey)obj;
      return (this.artifact.equals(that.artifact) && equals(this.repositories, that.repositories));
    }
    
    private static boolean equals(List<RemoteRepository> repos1, List<RemoteRepository> repos2) {
      if (repos1.size() != repos2.size())
        return false; 
      for (int i = 0, n = repos1.size(); i < n; i++) {
        RemoteRepository repo1 = repos1.get(i);
        RemoteRepository repo2 = repos2.get(i);
        if (repo1.isRepositoryManager() != repo2.isRepositoryManager())
          return false; 
        if (repo1.isRepositoryManager()) {
          if (!equals(repo1.getMirroredRepositories(), repo2.getMirroredRepositories()))
            return false; 
        } else {
          if (!repo1.getUrl().equals(repo2.getUrl()))
            return false; 
          if (repo1.getPolicy(true).isEnabled() != repo2.getPolicy(true).isEnabled())
            return false; 
          if (repo1.getPolicy(false).isEnabled() != repo2.getPolicy(false).isEnabled())
            return false; 
        } 
      } 
      return true;
    }
    
    public int hashCode() {
      return this.hashCode;
    }
  }
  
  static final class GraphKey {
    private final Artifact artifact;
    
    private final List<RemoteRepository> repositories;
    
    private final DependencySelector selector;
    
    private final DependencyManager manager;
    
    private final DependencyTraverser traverser;
    
    private final VersionFilter filter;
    
    private final int hashCode;
    
    GraphKey(Artifact artifact, List<RemoteRepository> repositories, DependencySelector selector, DependencyManager manager, DependencyTraverser traverser, VersionFilter filter) {
      this.artifact = artifact;
      this.repositories = repositories;
      this.selector = selector;
      this.manager = manager;
      this.traverser = traverser;
      this.filter = filter;
      this.hashCode = Objects.hash(new Object[] { artifact, repositories, selector, manager, traverser, filter });
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof GraphKey))
        return false; 
      GraphKey that = (GraphKey)obj;
      return (Objects.equals(this.artifact, that.artifact) && Objects.equals(this.repositories, that.repositories) && 
        Objects.equals(this.selector, that.selector) && Objects.equals(this.manager, that.manager) && 
        Objects.equals(this.traverser, that.traverser) && Objects.equals(this.filter, that.filter));
    }
    
    public int hashCode() {
      return this.hashCode;
    }
  }
}
