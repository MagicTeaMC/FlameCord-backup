package org.eclipse.aether.util.graph.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyManagement;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;

public final class TransitiveDependencyManager implements DependencyManager {
  private final Map<Object, String> managedVersions;
  
  private final Map<Object, String> managedScopes;
  
  private final Map<Object, Boolean> managedOptionals;
  
  private final Map<Object, String> managedLocalPaths;
  
  private final Map<Object, Collection<Exclusion>> managedExclusions;
  
  private final int depth;
  
  private int hashCode;
  
  public TransitiveDependencyManager() {
    this(0, Collections.emptyMap(), Collections.emptyMap(), 
        Collections.emptyMap(), Collections.emptyMap(), 
        Collections.emptyMap());
  }
  
  private TransitiveDependencyManager(int depth, Map<Object, String> managedVersions, Map<Object, String> managedScopes, Map<Object, Boolean> managedOptionals, Map<Object, String> managedLocalPaths, Map<Object, Collection<Exclusion>> managedExclusions) {
    this.depth = depth;
    this.managedVersions = managedVersions;
    this.managedScopes = managedScopes;
    this.managedOptionals = managedOptionals;
    this.managedLocalPaths = managedLocalPaths;
    this.managedExclusions = managedExclusions;
  }
  
  public DependencyManager deriveChildManager(DependencyCollectionContext context) {
    Map<Object, String> versions = this.managedVersions;
    Map<Object, String> scopes = this.managedScopes;
    Map<Object, Boolean> optionals = this.managedOptionals;
    Map<Object, String> localPaths = this.managedLocalPaths;
    Map<Object, Collection<Exclusion>> exclusions = this.managedExclusions;
    for (Dependency managedDependency : context.getManagedDependencies()) {
      Artifact artifact = managedDependency.getArtifact();
      Object key = getKey(artifact);
      String version = artifact.getVersion();
      if (version.length() > 0 && !versions.containsKey(key)) {
        if (versions == this.managedVersions)
          versions = new HashMap<>(this.managedVersions); 
        versions.put(key, version);
      } 
      String scope = managedDependency.getScope();
      if (scope.length() > 0 && !scopes.containsKey(key)) {
        if (scopes == this.managedScopes)
          scopes = new HashMap<>(this.managedScopes); 
        scopes.put(key, scope);
      } 
      Boolean optional = managedDependency.getOptional();
      if (optional != null && !optionals.containsKey(key)) {
        if (optionals == this.managedOptionals)
          optionals = new HashMap<>(this.managedOptionals); 
        optionals.put(key, optional);
      } 
      String localPath = managedDependency.getArtifact().getProperty("localPath", null);
      if (localPath != null && !localPaths.containsKey(key)) {
        if (localPaths == this.managedLocalPaths)
          localPaths = new HashMap<>(this.managedLocalPaths); 
        localPaths.put(key, localPath);
      } 
      if (!managedDependency.getExclusions().isEmpty()) {
        if (exclusions == this.managedExclusions)
          exclusions = new HashMap<>(this.managedExclusions); 
        Collection<Exclusion> managed = exclusions.get(key);
        if (managed == null) {
          managed = new LinkedHashSet<>();
          exclusions.put(key, managed);
        } 
        managed.addAll(managedDependency.getExclusions());
      } 
    } 
    return new TransitiveDependencyManager(this.depth + 1, versions, scopes, optionals, localPaths, exclusions);
  }
  
  public DependencyManagement manageDependency(Dependency dependency) {
    DependencyManagement management = null;
    Object key = getKey(dependency.getArtifact());
    if (this.depth >= 2) {
      String version = this.managedVersions.get(key);
      if (version != null) {
        management = new DependencyManagement();
        management.setVersion(version);
      } 
      String scope = this.managedScopes.get(key);
      if (scope != null) {
        if (management == null)
          management = new DependencyManagement(); 
        management.setScope(scope);
        if (!"system".equals(scope) && dependency.getArtifact().getProperty("localPath", null) != null) {
          Map<String, String> properties = new HashMap<>(dependency.getArtifact().getProperties());
          properties.remove("localPath");
          management.setProperties(properties);
        } 
      } 
      if ("system".equals(scope) || (scope == null && "system"
        .equals(dependency.getScope()))) {
        String localPath = this.managedLocalPaths.get(key);
        if (localPath != null) {
          if (management == null)
            management = new DependencyManagement(); 
          Map<String, String> properties = new HashMap<>(dependency.getArtifact().getProperties());
          properties.put("localPath", localPath);
          management.setProperties(properties);
        } 
      } 
      Boolean optional = this.managedOptionals.get(key);
      if (optional != null) {
        if (management == null)
          management = new DependencyManagement(); 
        management.setOptional(optional);
      } 
    } 
    Collection<Exclusion> exclusions = this.managedExclusions.get(key);
    if (exclusions != null) {
      if (management == null)
        management = new DependencyManagement(); 
      Collection<Exclusion> result = new LinkedHashSet<>(dependency.getExclusions());
      result.addAll(exclusions);
      management.setExclusions(result);
    } 
    return management;
  }
  
  private Object getKey(Artifact a) {
    return new Key(a);
  }
  
  public boolean equals(Object obj) {
    boolean equal = obj instanceof TransitiveDependencyManager;
    if (equal) {
      TransitiveDependencyManager that = (TransitiveDependencyManager)obj;
      return (this.depth == that.depth && 
        Objects.equals(this.managedVersions, that.managedVersions) && 
        Objects.equals(this.managedScopes, that.managedScopes) && 
        Objects.equals(this.managedOptionals, that.managedOptionals) && 
        Objects.equals(this.managedExclusions, that.managedExclusions));
    } 
    return false;
  }
  
  public int hashCode() {
    if (this.hashCode == 0)
      this.hashCode = Objects.hash(new Object[] { Integer.valueOf(this.depth), this.managedVersions, this.managedScopes, this.managedOptionals, this.managedExclusions }); 
    return this.hashCode;
  }
  
  static class Key {
    private final Artifact artifact;
    
    private final int hashCode;
    
    Key(Artifact artifact) {
      this.artifact = artifact;
      this.hashCode = Objects.hash(new Object[] { artifact.getGroupId(), artifact.getArtifactId() });
    }
    
    public boolean equals(Object obj) {
      boolean equal = obj instanceof Key;
      if (equal) {
        Key that = (Key)obj;
        return (Objects.equals(this.artifact.getArtifactId(), that.artifact.getArtifactId()) && 
          Objects.equals(this.artifact.getGroupId(), that.artifact.getGroupId()) && 
          Objects.equals(this.artifact.getExtension(), that.artifact.getExtension()) && 
          Objects.equals(this.artifact.getClassifier(), that.artifact.getClassifier()));
      } 
      return equal;
    }
    
    public int hashCode() {
      return this.hashCode;
    }
  }
}
