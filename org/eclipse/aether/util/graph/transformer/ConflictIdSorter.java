package org.eclipse.aether.util.graph.transformer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.DependencyNode;

public final class ConflictIdSorter implements DependencyGraphTransformer {
  public DependencyNode transformGraph(DependencyNode node, DependencyGraphTransformationContext context) throws RepositoryException {
    Map<?, ?> conflictIds = (Map<?, ?>)context.get(TransformationContextKeys.CONFLICT_IDS);
    if (conflictIds == null) {
      ConflictMarker marker = new ConflictMarker();
      marker.transformGraph(node, context);
      conflictIds = (Map<?, ?>)context.get(TransformationContextKeys.CONFLICT_IDS);
    } 
    Map<String, Object> stats = (Map<String, Object>)context.get(TransformationContextKeys.STATS);
    long time1 = System.nanoTime();
    Map<Object, ConflictId> ids = new LinkedHashMap<>(256);
    ConflictId id = null;
    Object key = conflictIds.get(node);
    if (key != null) {
      id = new ConflictId(key, 0);
      ids.put(key, id);
    } 
    Map<DependencyNode, Object> visited = new IdentityHashMap<>(conflictIds.size());
    buildConflitIdDAG(ids, node, id, 0, visited, conflictIds);
    long time2 = System.nanoTime();
    int cycles = topsortConflictIds(ids.values(), context);
    if (stats != null) {
      long time3 = System.nanoTime();
      stats.put("ConflictIdSorter.graphTime", Long.valueOf(time2 - time1));
      stats.put("ConflictIdSorter.topsortTime", Long.valueOf(time3 - time2));
      stats.put("ConflictIdSorter.conflictIdCount", Integer.valueOf(ids.size()));
      stats.put("ConflictIdSorter.conflictIdCycleCount", Integer.valueOf(cycles));
    } 
    return node;
  }
  
  private void buildConflitIdDAG(Map<Object, ConflictId> ids, DependencyNode node, ConflictId id, int depth, Map<DependencyNode, Object> visited, Map<?, ?> conflictIds) {
    if (visited.put(node, Boolean.TRUE) != null)
      return; 
    depth++;
    for (DependencyNode child : node.getChildren()) {
      Object key = conflictIds.get(child);
      ConflictId childId = ids.get(key);
      if (childId == null) {
        childId = new ConflictId(key, depth);
        ids.put(key, childId);
      } else {
        childId.pullup(depth);
      } 
      if (id != null)
        id.add(childId); 
      buildConflitIdDAG(ids, child, childId, depth, visited, conflictIds);
    } 
  }
  
  private int topsortConflictIds(Collection<ConflictId> conflictIds, DependencyGraphTransformationContext context) {
    List<Object> sorted = new ArrayList(conflictIds.size());
    RootQueue roots = new RootQueue(conflictIds.size() / 2);
    for (ConflictId id : conflictIds) {
      if (id.inDegree <= 0)
        roots.add(id); 
    } 
    processRoots(sorted, roots);
    boolean cycle = (sorted.size() < conflictIds.size());
    while (sorted.size() < conflictIds.size()) {
      ConflictId nearest = null;
      for (ConflictId id : conflictIds) {
        if (id.inDegree <= 0)
          continue; 
        if (nearest == null || id.minDepth < nearest.minDepth || (id.minDepth == nearest.minDepth && id.inDegree < nearest.inDegree))
          nearest = id; 
      } 
      nearest.inDegree = 0;
      roots.add(nearest);
      processRoots(sorted, roots);
    } 
    Collection<Collection<Object>> cycles = Collections.emptySet();
    if (cycle)
      cycles = findCycles(conflictIds); 
    context.put(TransformationContextKeys.SORTED_CONFLICT_IDS, sorted);
    context.put(TransformationContextKeys.CYCLIC_CONFLICT_IDS, cycles);
    return cycles.size();
  }
  
  private void processRoots(List<Object> sorted, RootQueue roots) {
    while (!roots.isEmpty()) {
      ConflictId root = roots.remove();
      sorted.add(root.key);
      for (ConflictId child : root.children) {
        child.inDegree--;
        if (child.inDegree == 0)
          roots.add(child); 
      } 
    } 
  }
  
  private Collection<Collection<Object>> findCycles(Collection<ConflictId> conflictIds) {
    Collection<Collection<Object>> cycles = new HashSet<>();
    Map<Object, Integer> stack = new HashMap<>(128);
    Map<ConflictId, Object> visited = new IdentityHashMap<>(conflictIds.size());
    for (ConflictId id : conflictIds)
      findCycles(id, visited, stack, cycles); 
    return cycles;
  }
  
  private void findCycles(ConflictId id, Map<ConflictId, Object> visited, Map<Object, Integer> stack, Collection<Collection<Object>> cycles) {
    Integer depth = stack.put(id.key, Integer.valueOf(stack.size()));
    if (depth != null) {
      stack.put(id.key, depth);
      Collection<Object> cycle = new HashSet();
      for (Map.Entry<Object, Integer> entry : stack.entrySet()) {
        if (((Integer)entry.getValue()).intValue() >= depth.intValue())
          cycle.add(entry.getKey()); 
      } 
      cycles.add(cycle);
    } else {
      if (visited.put(id, Boolean.TRUE) == null)
        for (ConflictId childId : id.children)
          findCycles(childId, visited, stack, cycles);  
      stack.remove(id.key);
    } 
  }
  
  static final class ConflictId {
    final Object key;
    
    Collection<ConflictId> children = Collections.emptySet();
    
    int inDegree;
    
    int minDepth;
    
    ConflictId(Object key, int depth) {
      this.key = key;
      this.minDepth = depth;
    }
    
    public void add(ConflictId child) {
      if (this.children.isEmpty())
        this.children = new HashSet<>(); 
      if (this.children.add(child))
        child.inDegree++; 
    }
    
    public void pullup(int depth) {
      if (depth < this.minDepth) {
        this.minDepth = depth;
        depth++;
        for (ConflictId child : this.children)
          child.pullup(depth); 
      } 
    }
    
    public boolean equals(Object obj) {
      if (this == obj)
        return true; 
      if (!(obj instanceof ConflictId))
        return false; 
      ConflictId that = (ConflictId)obj;
      return this.key.equals(that.key);
    }
    
    public int hashCode() {
      return this.key.hashCode();
    }
    
    public String toString() {
      return this.key + " @ " + this.minDepth + " <" + this.inDegree;
    }
  }
  
  static final class RootQueue {
    private int nextOut;
    
    private int nextIn;
    
    private ConflictIdSorter.ConflictId[] ids;
    
    RootQueue(int capacity) {
      this.ids = new ConflictIdSorter.ConflictId[capacity + 16];
    }
    
    boolean isEmpty() {
      return (this.nextOut >= this.nextIn);
    }
    
    void add(ConflictIdSorter.ConflictId id) {
      if (this.nextOut >= this.nextIn && this.nextOut > 0) {
        this.nextIn -= this.nextOut;
        this.nextOut = 0;
      } 
      if (this.nextIn >= this.ids.length) {
        ConflictIdSorter.ConflictId[] tmp = new ConflictIdSorter.ConflictId[this.ids.length + this.ids.length / 2 + 16];
        System.arraycopy(this.ids, this.nextOut, tmp, 0, this.nextIn - this.nextOut);
        this.ids = tmp;
        this.nextIn -= this.nextOut;
        this.nextOut = 0;
      } 
      int i;
      for (i = this.nextIn - 1; i >= this.nextOut && id.minDepth < (this.ids[i]).minDepth; i--)
        this.ids[i + 1] = this.ids[i]; 
      this.ids[i + 1] = id;
      this.nextIn++;
    }
    
    ConflictIdSorter.ConflictId remove() {
      return this.ids[this.nextOut++];
    }
  }
}
