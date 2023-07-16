package org.eclipse.aether.util.graph.manager;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyCollectionContext;
import org.eclipse.aether.collection.DependencyManagement;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.Exclusion;

public final class ClassicDependencyManager implements DependencyManager {
  private final int depth;
  
  private final Map<Object, String> managedVersions;
  
  private final Map<Object, String> managedScopes;
  
  private final Map<Object, Boolean> managedOptionals;
  
  private final Map<Object, String> managedLocalPaths;
  
  private final Map<Object, Collection<Exclusion>> managedExclusions;
  
  private int hashCode;
  
  public ClassicDependencyManager() {
    this(0, Collections.emptyMap(), Collections.emptyMap(), 
        Collections.emptyMap(), Collections.emptyMap(), 
        Collections.emptyMap());
  }
  
  private ClassicDependencyManager(int depth, Map<Object, String> managedVersions, Map<Object, String> managedScopes, Map<Object, Boolean> managedOptionals, Map<Object, String> managedLocalPaths, Map<Object, Collection<Exclusion>> managedExclusions) {
    this.depth = depth;
    this.managedVersions = managedVersions;
    this.managedScopes = managedScopes;
    this.managedOptionals = managedOptionals;
    this.managedLocalPaths = managedLocalPaths;
    this.managedExclusions = managedExclusions;
  }
  
  public DependencyManager deriveChildManager(DependencyCollectionContext context) {
    if (this.depth >= 2)
      return this; 
    if (this.depth == 1)
      return new ClassicDependencyManager(this.depth + 1, this.managedVersions, this.managedScopes, this.managedOptionals, this.managedLocalPaths, this.managedExclusions); 
    Map<Object, String> managedVersions = this.managedVersions;
    Map<Object, String> managedScopes = this.managedScopes;
    Map<Object, Boolean> managedOptionals = this.managedOptionals;
    Map<Object, String> managedLocalPaths = this.managedLocalPaths;
    Map<Object, Collection<Exclusion>> managedExclusions = this.managedExclusions;
    for (Dependency managedDependency : context.getManagedDependencies()) {
      Artifact artifact = managedDependency.getArtifact();
      Object key = getKey(artifact);
      String version = artifact.getVersion();
      if (version.length() > 0 && !managedVersions.containsKey(key)) {
        if (managedVersions == this.managedVersions)
          managedVersions = new HashMap<>(this.managedVersions); 
        managedVersions.put(key, version);
      } 
      String scope = managedDependency.getScope();
      if (scope.length() > 0 && !managedScopes.containsKey(key)) {
        if (managedScopes == this.managedScopes)
          managedScopes = new HashMap<>(this.managedScopes); 
        managedScopes.put(key, scope);
      } 
      Boolean optional = managedDependency.getOptional();
      if (optional != null && !managedOptionals.containsKey(key)) {
        if (managedOptionals == this.managedOptionals)
          managedOptionals = new HashMap<>(this.managedOptionals); 
        managedOptionals.put(key, optional);
      } 
      String localPath = managedDependency.getArtifact().getProperty("localPath", null);
      if (localPath != null && !managedLocalPaths.containsKey(key)) {
        if (managedLocalPaths == this.managedLocalPaths)
          managedLocalPaths = new HashMap<>(this.managedLocalPaths); 
        managedLocalPaths.put(key, localPath);
      } 
      Collection<Exclusion> exclusions = managedDependency.getExclusions();
      if (!exclusions.isEmpty()) {
        if (managedExclusions == this.managedExclusions)
          managedExclusions = new HashMap<>(this.managedExclusions); 
        Collection<Exclusion> managed = managedExclusions.get(key);
        if (managed == null) {
          managed = new LinkedHashSet<>();
          managedExclusions.put(key, managed);
        } 
        managed.addAll(exclusions);
      } 
    } 
    return new ClassicDependencyManager(this.depth + 1, managedVersions, managedScopes, managedOptionals, managedLocalPaths, managedExclusions);
  }
  
  public DependencyManagement manageDependency(Dependency dependency) {
    DependencyManagement management = null;
    Object key = getKey(dependency.getArtifact());
    if (this.depth >= 2) {
      String version = this.managedVersions.get(key);
      if (version != null) {
        if (management == null)
          management = new DependencyManagement(); 
        management.setVersion(version);
      } 
      String scope = this.managedScopes.get(key);
      if (scope != null) {
        if (management == null)
          management = new DependencyManagement(); 
        management.setScope(scope);
        if (!"system".equals(scope) && dependency
          .getArtifact().getProperty("localPath", null) != null) {
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
    if (this == obj)
      return true; 
    if (null == obj || !getClass().equals(obj.getClass()))
      return false; 
    ClassicDependencyManager that = (ClassicDependencyManager)obj;
    return (this.depth == that.depth && this.managedVersions.equals(that.managedVersions) && this.managedScopes
      .equals(that.managedScopes) && this.managedOptionals.equals(that.managedOptionals) && this.managedExclusions
      .equals(that.managedExclusions));
  }
  
  public int hashCode() {
    if (this.hashCode == 0) {
      int hash = 17;
      hash = hash * 31 + this.depth;
      hash = hash * 31 + this.managedVersions.hashCode();
      hash = hash * 31 + this.managedScopes.hashCode();
      hash = hash * 31 + this.managedOptionals.hashCode();
      hash = hash * 31 + this.managedExclusions.hashCode();
      this.hashCode = hash;
    } 
    return this.hashCode;
  }
  
  static class Key {
    private final Artifact artifact;
    
    private final int hashCode;
    
    Key(Artifact artifact) {
      this.artifact = artifact;
      int hash = 17;
      hash = hash * 31 + artifact.getGroupId().hashCode();
      hash = hash * 31 + artifact.getArtifactId().hashCode();
      this.hashCode = hash;
    }
    
    public boolean equals(Object obj) {
      if (obj == this)
        return true; 
      if (!(obj instanceof Key))
        return false; 
      Key that = (Key)obj;
      return (this.artifact.getArtifactId().equals(that.artifact.getArtifactId()) && this.artifact
        .getGroupId().equals(that.artifact.getGroupId()) && this.artifact
        .getExtension().equals(that.artifact.getExtension()) && this.artifact
        .getClassifier().equals(that.artifact.getClassifier()));
    }
    
    public int hashCode() {
      return this.hashCode;
    }
  }
}
