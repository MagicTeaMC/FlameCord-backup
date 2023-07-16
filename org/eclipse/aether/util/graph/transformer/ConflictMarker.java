package org.eclipse.aether.util.graph.transformer;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;

public final class ConflictMarker implements DependencyGraphTransformer {
  public DependencyNode transformGraph(DependencyNode node, DependencyGraphTransformationContext context) throws RepositoryException {
    Map<String, Object> stats = (Map<String, Object>)context.get(TransformationContextKeys.STATS);
    long time1 = System.nanoTime();
    Map<DependencyNode, Object> nodes = new IdentityHashMap<>(1024);
    Map<Object, ConflictGroup> groups = new HashMap<>(1024);
    analyze(node, nodes, groups, new int[] { 0 });
    long time2 = System.nanoTime();
    Map<DependencyNode, Object> conflictIds = mark(nodes.keySet(), groups);
    context.put(TransformationContextKeys.CONFLICT_IDS, conflictIds);
    if (stats != null) {
      long time3 = System.nanoTime();
      stats.put("ConflictMarker.analyzeTime", Long.valueOf(time2 - time1));
      stats.put("ConflictMarker.markTime", Long.valueOf(time3 - time2));
      stats.put("ConflictMarker.nodeCount", Integer.valueOf(nodes.size()));
    } 
    return node;
  }
  
  private void analyze(DependencyNode node, Map<DependencyNode, Object> nodes, Map<Object, ConflictGroup> groups, int[] counter) {
    if (nodes.put(node, Boolean.TRUE) != null)
      return; 
    Set<Object> keys = getKeys(node);
    if (!keys.isEmpty()) {
      ConflictGroup group = null;
      boolean fixMappings = false;
      for (Object key : keys) {
        ConflictGroup g = groups.get(key);
        if (group != g) {
          if (group == null) {
            Set<Object> set = merge(g.keys, keys);
            if (set == g.keys) {
              group = g;
              break;
            } 
            counter[0] = counter[0] + 1;
            group = new ConflictGroup(set, counter[0]);
            fixMappings = true;
            continue;
          } 
          if (g == null) {
            fixMappings = true;
            continue;
          } 
          Set<Object> newKeys = merge(g.keys, group.keys);
          if (newKeys == g.keys) {
            group = g;
            fixMappings = false;
            break;
          } 
          if (newKeys != group.keys) {
            counter[0] = counter[0] + 1;
            group = new ConflictGroup(newKeys, counter[0]);
            fixMappings = true;
          } 
        } 
      } 
      if (group == null) {
        counter[0] = counter[0] + 1;
        group = new ConflictGroup(keys, counter[0]);
        fixMappings = true;
      } 
      if (fixMappings)
        for (Object key : group.keys)
          groups.put(key, group);  
    } 
    for (DependencyNode child : node.getChildren())
      analyze(child, nodes, groups, counter); 
  }
  
  private Set<Object> merge(Set<Object> keys1, Set<Object> keys2) {
    int size1 = keys1.size();
    int size2 = keys2.size();
    if (size1 < size2) {
      if (keys2.containsAll(keys1))
        return keys2; 
    } else if (keys1.containsAll(keys2)) {
      return keys1;
    } 
    Set<Object> keys = new HashSet();
    keys.addAll(keys1);
    keys.addAll(keys2);
    return keys;
  }
  
  private Set<Object> getKeys(DependencyNode node) {
    Set<Object> keys;
    Dependency dependency = node.getDependency();
    if (dependency == null) {
      keys = Collections.emptySet();
    } else {
      Object key = toKey(dependency.getArtifact());
      if (node.getRelocations().isEmpty() && node.getAliases().isEmpty()) {
        keys = Collections.singleton(key);
      } else {
        keys = new HashSet();
        keys.add(key);
        for (Artifact relocation : node.getRelocations()) {
          key = toKey(relocation);
          keys.add(key);
        } 
        for (Artifact alias : node.getAliases()) {
          key = toKey(alias);
          keys.add(key);
        } 
      } 
    } 
    return keys;
  }
  
  private Map<DependencyNode, Object> mark(Collection<DependencyNode> nodes, Map<Object, ConflictGroup> groups) {
    Map<DependencyNode, Object> conflictIds = new IdentityHashMap<>(nodes.size() + 1);
    for (DependencyNode node : nodes) {
      Dependency dependency = node.getDependency();
      if (dependency != null) {
        Object key = toKey(dependency.getArtifact());
        conflictIds.put(node, Integer.valueOf(((ConflictGroup)groups.get(key)).index));
      } 
    } 
    return conflictIds;
  }
  
  private static Object toKey(Artifact artifact) {
    return new Key(artifact);
  }
  
  static class ConflictGroup {
    final Set<Object> keys;
    
    final int index;
    
    ConflictGroup(Set<Object> keys, int index) {
      this.keys = keys;
      this.index = index;
    }
    
    public String toString() {
      return String.valueOf(this.keys);
    }
  }
  
  static class Key {
    private final Artifact artifact;
    
    Key(Artifact artifact) {
      this.artifact = artifact;
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
      int hash = 17;
      hash = hash * 31 + this.artifact.getArtifactId().hashCode();
      hash = hash * 31 + this.artifact.getGroupId().hashCode();
      hash = hash * 31 + this.artifact.getClassifier().hashCode();
      hash = hash * 31 + this.artifact.getExtension().hashCode();
      return hash;
    }
    
    public String toString() {
      return this.artifact.getGroupId() + ':' + this.artifact.getArtifactId() + ':' + this.artifact.getClassifier() + ':' + this.artifact
        .getExtension();
    }
  }
}
