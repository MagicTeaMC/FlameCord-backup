package org.eclipse.aether.util.graph.transformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.RepositoryException;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.DependencyGraphTransformationContext;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.graph.DefaultDependencyNode;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.util.ConfigUtils;

public final class ConflictResolver implements DependencyGraphTransformer {
  public static final String CONFIG_PROP_VERBOSE = "aether.conflictResolver.verbose";
  
  public static final String NODE_DATA_WINNER = "conflict.winner";
  
  public static final String NODE_DATA_ORIGINAL_SCOPE = "conflict.originalScope";
  
  public static final String NODE_DATA_ORIGINAL_OPTIONALITY = "conflict.originalOptionality";
  
  private final VersionSelector versionSelector;
  
  private final ScopeSelector scopeSelector;
  
  private final ScopeDeriver scopeDeriver;
  
  private final OptionalitySelector optionalitySelector;
  
  public ConflictResolver(VersionSelector versionSelector, ScopeSelector scopeSelector, OptionalitySelector optionalitySelector, ScopeDeriver scopeDeriver) {
    this.versionSelector = Objects.<VersionSelector>requireNonNull(versionSelector, "version selector cannot be null");
    this.scopeSelector = Objects.<ScopeSelector>requireNonNull(scopeSelector, "scope selector cannot be null");
    this.optionalitySelector = Objects.<OptionalitySelector>requireNonNull(optionalitySelector, "optionality selector cannot be null");
    this.scopeDeriver = Objects.<ScopeDeriver>requireNonNull(scopeDeriver, "scope deriver cannot be null");
  }
  
  public DependencyNode transformGraph(DependencyNode node, DependencyGraphTransformationContext context) throws RepositoryException {
    List<?> sortedConflictIds = (List)context.get(TransformationContextKeys.SORTED_CONFLICT_IDS);
    if (sortedConflictIds == null) {
      ConflictIdSorter sorter = new ConflictIdSorter();
      sorter.transformGraph(node, context);
      sortedConflictIds = (List)context.get(TransformationContextKeys.SORTED_CONFLICT_IDS);
    } 
    Map<String, Object> stats = (Map<String, Object>)context.get(TransformationContextKeys.STATS);
    long time1 = System.nanoTime();
    Collection<Collection<?>> conflictIdCycles = (Collection<Collection<?>>)context.get(TransformationContextKeys.CYCLIC_CONFLICT_IDS);
    if (conflictIdCycles == null)
      throw new RepositoryException("conflict id cycles have not been identified"); 
    Map<?, ?> conflictIds = (Map<?, ?>)context.get(TransformationContextKeys.CONFLICT_IDS);
    if (conflictIds == null)
      throw new RepositoryException("conflict groups have not been identified"); 
    Map<Object, Collection<Object>> cyclicPredecessors = new HashMap<>();
    for (Collection<?> cycle : conflictIdCycles) {
      for (Object conflictId : cycle) {
        Collection<Object> predecessors = cyclicPredecessors.get(conflictId);
        if (predecessors == null) {
          predecessors = new HashSet();
          cyclicPredecessors.put(conflictId, predecessors);
        } 
        predecessors.addAll(cycle);
      } 
    } 
    State state = new State(node, conflictIds, sortedConflictIds.size(), context);
    for (Iterator<?> it = sortedConflictIds.iterator(); it.hasNext(); ) {
      Object conflictId = it.next();
      state.prepare(conflictId, cyclicPredecessors.get(conflictId));
      gatherConflictItems(node, state);
      state.finish();
      if (!state.items.isEmpty()) {
        ConflictContext ctx = state.conflictCtx;
        state.versionSelector.selectVersion(ctx);
        if (ctx.winner == null)
          throw new RepositoryException("conflict resolver did not select winner among " + state.items); 
        DependencyNode winner = ctx.winner.node;
        state.scopeSelector.selectScope(ctx);
        if (state.verbose)
          winner.setData("conflict.originalScope", winner.getDependency().getScope()); 
        winner.setScope(ctx.scope);
        state.optionalitySelector.selectOptionality(ctx);
        if (state.verbose)
          winner.setData("conflict.originalOptionality", Boolean.valueOf(winner.getDependency().isOptional())); 
        winner.setOptional(ctx.optional);
        removeLosers(state);
      } 
      state.winner();
      if (!it.hasNext() && !conflictIdCycles.isEmpty() && state.conflictCtx.winner != null) {
        DependencyNode winner = state.conflictCtx.winner.node;
        state.prepare(state, null);
        gatherConflictItems(winner, state);
      } 
    } 
    if (stats != null) {
      long time2 = System.nanoTime();
      stats.put("ConflictResolver.totalTime", Long.valueOf(time2 - time1));
      stats.put("ConflictResolver.conflictItemCount", Integer.valueOf(state.totalConflictItems));
    } 
    return node;
  }
  
  private boolean gatherConflictItems(DependencyNode node, State state) throws RepositoryException {
    Object conflictId = state.conflictIds.get(node);
    if (state.currentId.equals(conflictId)) {
      state.add(node);
    } else {
      if (state.loser(node, conflictId))
        return false; 
      if (state.push(node, conflictId)) {
        for (Iterator<DependencyNode> it = node.getChildren().iterator(); it.hasNext(); ) {
          DependencyNode child = it.next();
          if (!gatherConflictItems(child, state))
            it.remove(); 
        } 
        state.pop();
      } 
    } 
    return true;
  }
  
  private void removeLosers(State state) {
    ConflictItem winner = state.conflictCtx.winner;
    List<DependencyNode> previousParent = null;
    ListIterator<DependencyNode> childIt = null;
    boolean conflictVisualized = false;
    label24: for (ConflictItem item : state.items) {
      if (item == winner)
        continue; 
      if (item.parent != previousParent) {
        childIt = item.parent.listIterator();
        previousParent = item.parent;
        conflictVisualized = false;
      } 
      while (childIt.hasNext()) {
        DependencyNode child = childIt.next();
        if (child == item.node) {
          if (state.verbose) {
            if (!conflictVisualized) {
              if (item.parent != winner.parent) {
                conflictVisualized = true;
                DefaultDependencyNode defaultDependencyNode = new DefaultDependencyNode(child);
                defaultDependencyNode.setData("conflict.winner", winner.node);
                defaultDependencyNode.setData("conflict.originalScope", defaultDependencyNode.getDependency().getScope());
                defaultDependencyNode.setData("conflict.originalOptionality", Boolean.valueOf(defaultDependencyNode.getDependency().isOptional()));
                defaultDependencyNode.setScope(item.getScopes().iterator().next());
                defaultDependencyNode.setChildren(Collections.emptyList());
                childIt.set(defaultDependencyNode);
                continue;
              } 
              continue label24;
            } 
            childIt.remove();
            continue;
          } 
          continue label24;
        } 
      } 
    } 
  }
  
  static final class NodeInfo {
    int minDepth;
    
    Object derivedScopes;
    
    int derivedOptionalities;
    
    List<ConflictResolver.ConflictItem> children;
    
    static final int CHANGE_SCOPE = 1;
    
    static final int CHANGE_OPTIONAL = 2;
    
    private static final int OPT_FALSE = 1;
    
    private static final int OPT_TRUE = 2;
    
    NodeInfo(int depth, String derivedScope, boolean optional) {
      this.minDepth = depth;
      this.derivedScopes = derivedScope;
      this.derivedOptionalities = optional ? 2 : 1;
    }
    
    int update(int depth, String derivedScope, boolean optional) {
      int changes;
      if (depth < this.minDepth)
        this.minDepth = depth; 
      if (this.derivedScopes.equals(derivedScope)) {
        changes = 0;
      } else if (this.derivedScopes instanceof Collection) {
        changes = ((Collection<String>)this.derivedScopes).add(derivedScope) ? 1 : 0;
      } else {
        Collection<String> scopes = new HashSet<>();
        scopes.add((String)this.derivedScopes);
        scopes.add(derivedScope);
        this.derivedScopes = scopes;
        changes = 1;
      } 
      int bit = optional ? 2 : 1;
      if ((this.derivedOptionalities & bit) == 0) {
        this.derivedOptionalities |= bit;
        changes |= 0x2;
      } 
      return changes;
    }
    
    void add(ConflictResolver.ConflictItem item) {
      if (this.children == null)
        this.children = new ArrayList<>(1); 
      this.children.add(item);
    }
  }
  
  final class State {
    Object currentId;
    
    int totalConflictItems;
    
    final boolean verbose;
    
    final Map<Object, DependencyNode> resolvedIds;
    
    final Collection<Object> potentialAncestorIds;
    
    final Map<?, ?> conflictIds;
    
    final List<ConflictResolver.ConflictItem> items;
    
    final Map<List<DependencyNode>, ConflictResolver.NodeInfo> infos;
    
    final Map<List<DependencyNode>, Object> stack;
    
    final List<DependencyNode> parentNodes;
    
    final List<String> parentScopes;
    
    final List<Boolean> parentOptionals;
    
    final List<ConflictResolver.NodeInfo> parentInfos;
    
    final ConflictResolver.ConflictContext conflictCtx;
    
    final ConflictResolver.ScopeContext scopeCtx;
    
    final ConflictResolver.VersionSelector versionSelector;
    
    final ConflictResolver.ScopeSelector scopeSelector;
    
    final ConflictResolver.ScopeDeriver scopeDeriver;
    
    final ConflictResolver.OptionalitySelector optionalitySelector;
    
    State(DependencyNode root, Map<?, ?> conflictIds, int conflictIdCount, DependencyGraphTransformationContext context) throws RepositoryException {
      this.conflictIds = conflictIds;
      this.verbose = ConfigUtils.getBoolean(context.getSession(), false, new String[] { "aether.conflictResolver.verbose" });
      this.potentialAncestorIds = new HashSet(conflictIdCount * 2);
      this.resolvedIds = new HashMap<>(conflictIdCount * 2);
      this.items = new ArrayList<>(256);
      this.infos = new IdentityHashMap<>(64);
      this.stack = new IdentityHashMap<>(64);
      this.parentNodes = new ArrayList<>(64);
      this.parentScopes = new ArrayList<>(64);
      this.parentOptionals = new ArrayList<>(64);
      this.parentInfos = new ArrayList<>(64);
      this.conflictCtx = new ConflictResolver.ConflictContext(root, conflictIds, this.items);
      this.scopeCtx = new ConflictResolver.ScopeContext(null, null);
      this.versionSelector = ConflictResolver.this.versionSelector.getInstance(root, context);
      this.scopeSelector = ConflictResolver.this.scopeSelector.getInstance(root, context);
      this.scopeDeriver = ConflictResolver.this.scopeDeriver.getInstance(root, context);
      this.optionalitySelector = ConflictResolver.this.optionalitySelector.getInstance(root, context);
    }
    
    void prepare(Object conflictId, Collection<Object> cyclicPredecessors) {
      this.currentId = conflictId;
      this.conflictCtx.conflictId = conflictId;
      this.conflictCtx.winner = null;
      this.conflictCtx.scope = null;
      this.conflictCtx.optional = null;
      this.items.clear();
      this.infos.clear();
      if (cyclicPredecessors != null)
        this.potentialAncestorIds.addAll(cyclicPredecessors); 
    }
    
    void finish() {
      List<DependencyNode> previousParent = null;
      int previousDepth = 0;
      this.totalConflictItems += this.items.size();
      for (int i = this.items.size() - 1; i >= 0; i--) {
        ConflictResolver.ConflictItem item = this.items.get(i);
        if (item.parent == previousParent) {
          item.depth = previousDepth;
        } else if (item.parent != null) {
          previousParent = item.parent;
          ConflictResolver.NodeInfo info = this.infos.get(previousParent);
          previousDepth = info.minDepth + 1;
          item.depth = previousDepth;
        } 
      } 
      this.potentialAncestorIds.add(this.currentId);
    }
    
    void winner() {
      this.resolvedIds.put(this.currentId, (this.conflictCtx.winner != null) ? this.conflictCtx.winner.node : null);
    }
    
    boolean loser(DependencyNode node, Object conflictId) {
      DependencyNode winner = this.resolvedIds.get(conflictId);
      return (winner != null && winner != node);
    }
    
    boolean push(DependencyNode node, Object conflictId) throws RepositoryException {
      if (conflictId == null) {
        if (node.getDependency() != null) {
          if (node.getData().get("conflict.winner") != null)
            return false; 
          throw new RepositoryException("missing conflict id for node " + node);
        } 
      } else if (!this.potentialAncestorIds.contains(conflictId)) {
        return false;
      } 
      List<DependencyNode> graphNode = node.getChildren();
      if (this.stack.put(graphNode, Boolean.TRUE) != null)
        return false; 
      int depth = depth();
      String scope = deriveScope(node, conflictId);
      boolean optional = deriveOptional(node, conflictId);
      ConflictResolver.NodeInfo info = this.infos.get(graphNode);
      if (info == null) {
        info = new ConflictResolver.NodeInfo(depth, scope, optional);
        this.infos.put(graphNode, info);
        this.parentInfos.add(info);
        this.parentNodes.add(node);
        this.parentScopes.add(scope);
        this.parentOptionals.add(Boolean.valueOf(optional));
      } else {
        int changes = info.update(depth, scope, optional);
        if (changes == 0) {
          this.stack.remove(graphNode);
          return false;
        } 
        this.parentInfos.add(null);
        this.parentNodes.add(node);
        this.parentScopes.add(scope);
        this.parentOptionals.add(Boolean.valueOf(optional));
        if (info.children != null) {
          if ((changes & 0x1) != 0)
            for (int i = info.children.size() - 1; i >= 0; i--) {
              ConflictResolver.ConflictItem item = info.children.get(i);
              String childScope = deriveScope(item.node, null);
              item.addScope(childScope);
            }  
          if ((changes & 0x2) != 0)
            for (int i = info.children.size() - 1; i >= 0; i--) {
              ConflictResolver.ConflictItem item = info.children.get(i);
              boolean childOptional = deriveOptional(item.node, null);
              item.addOptional(childOptional);
            }  
        } 
      } 
      return true;
    }
    
    void pop() {
      int last = this.parentInfos.size() - 1;
      this.parentInfos.remove(last);
      this.parentScopes.remove(last);
      this.parentOptionals.remove(last);
      DependencyNode node = this.parentNodes.remove(last);
      this.stack.remove(node.getChildren());
    }
    
    void add(DependencyNode node) throws RepositoryException {
      DependencyNode parent = parent();
      if (parent == null) {
        ConflictResolver.ConflictItem item = newConflictItem(parent, node);
        this.items.add(item);
      } else {
        ConflictResolver.NodeInfo info = this.parentInfos.get(this.parentInfos.size() - 1);
        if (info != null) {
          ConflictResolver.ConflictItem item = newConflictItem(parent, node);
          info.add(item);
          this.items.add(item);
        } 
      } 
    }
    
    private ConflictResolver.ConflictItem newConflictItem(DependencyNode parent, DependencyNode node) throws RepositoryException {
      return new ConflictResolver.ConflictItem(parent, node, deriveScope(node, null), deriveOptional(node, null));
    }
    
    private int depth() {
      return this.parentNodes.size();
    }
    
    private DependencyNode parent() {
      int size = this.parentNodes.size();
      return (size <= 0) ? null : this.parentNodes.get(size - 1);
    }
    
    private String deriveScope(DependencyNode node, Object conflictId) throws RepositoryException {
      if ((node.getManagedBits() & 0x2) != 0 || (conflictId != null && this.resolvedIds
        .containsKey(conflictId)))
        return scope(node.getDependency()); 
      int depth = this.parentNodes.size();
      scopes(depth, node.getDependency());
      if (depth > 0)
        this.scopeDeriver.deriveScope(this.scopeCtx); 
      return this.scopeCtx.derivedScope;
    }
    
    private void scopes(int parent, Dependency child) {
      this.scopeCtx.parentScope = (parent > 0) ? this.parentScopes.get(parent - 1) : null;
      this.scopeCtx.derivedScope = scope(child);
      this.scopeCtx.childScope = scope(child);
    }
    
    private String scope(Dependency dependency) {
      return (dependency != null) ? dependency.getScope() : null;
    }
    
    private boolean deriveOptional(DependencyNode node, Object conflictId) {
      Dependency dep = node.getDependency();
      boolean optional = (dep != null && dep.isOptional());
      if (optional || (node.getManagedBits() & 0x4) != 0 || (conflictId != null && this.resolvedIds
        .containsKey(conflictId)))
        return optional; 
      int depth = this.parentNodes.size();
      return (depth > 0) ? ((Boolean)this.parentOptionals.get(depth - 1)).booleanValue() : false;
    }
  }
  
  public static final class ScopeContext {
    String parentScope;
    
    String childScope;
    
    String derivedScope;
    
    public ScopeContext(String parentScope, String childScope) {
      this.parentScope = (parentScope != null) ? parentScope : "";
      this.derivedScope = (childScope != null) ? childScope : "";
      this.childScope = (childScope != null) ? childScope : "";
    }
    
    public String getParentScope() {
      return this.parentScope;
    }
    
    public String getChildScope() {
      return this.childScope;
    }
    
    public String getDerivedScope() {
      return this.derivedScope;
    }
    
    public void setDerivedScope(String derivedScope) {
      this.derivedScope = (derivedScope != null) ? derivedScope : "";
    }
  }
  
  public static final class ConflictItem {
    final List<DependencyNode> parent;
    
    final Artifact artifact;
    
    final DependencyNode node;
    
    int depth;
    
    Object scopes;
    
    int optionalities;
    
    public static final int OPTIONAL_FALSE = 1;
    
    public static final int OPTIONAL_TRUE = 2;
    
    ConflictItem(DependencyNode parent, DependencyNode node, String scope, boolean optional) {
      if (parent != null) {
        this.parent = parent.getChildren();
        this.artifact = parent.getArtifact();
      } else {
        this.parent = null;
        this.artifact = null;
      } 
      this.node = node;
      this.scopes = scope;
      this.optionalities = optional ? 2 : 1;
    }
    
    public ConflictItem(DependencyNode parent, DependencyNode node, int depth, int optionalities, String... scopes) {
      this.parent = (parent != null) ? parent.getChildren() : null;
      this.artifact = (parent != null) ? parent.getArtifact() : null;
      this.node = node;
      this.depth = depth;
      this.optionalities = optionalities;
      this.scopes = Arrays.asList(scopes);
    }
    
    public boolean isSibling(ConflictItem item) {
      return (this.parent == item.parent);
    }
    
    public DependencyNode getNode() {
      return this.node;
    }
    
    public Dependency getDependency() {
      return this.node.getDependency();
    }
    
    public int getDepth() {
      return this.depth;
    }
    
    public Collection<String> getScopes() {
      if (this.scopes instanceof String)
        return Collections.singleton((String)this.scopes); 
      return (Collection<String>)this.scopes;
    }
    
    void addScope(String scope) {
      if (this.scopes instanceof Collection) {
        ((Collection<String>)this.scopes).add(scope);
      } else if (!this.scopes.equals(scope)) {
        Collection<Object> set = new HashSet();
        set.add(this.scopes);
        set.add(scope);
        this.scopes = set;
      } 
    }
    
    public int getOptionalities() {
      return this.optionalities;
    }
    
    void addOptional(boolean optional) {
      this.optionalities |= optional ? 2 : 1;
    }
    
    public String toString() {
      return this.node + " @ " + this.depth + " < " + this.artifact;
    }
  }
  
  public static final class ConflictContext {
    final DependencyNode root;
    
    final Map<?, ?> conflictIds;
    
    final Collection<ConflictResolver.ConflictItem> items;
    
    Object conflictId;
    
    ConflictResolver.ConflictItem winner;
    
    String scope;
    
    Boolean optional;
    
    ConflictContext(DependencyNode root, Map<?, ?> conflictIds, Collection<ConflictResolver.ConflictItem> items) {
      this.root = root;
      this.conflictIds = conflictIds;
      this.items = Collections.unmodifiableCollection(items);
    }
    
    public ConflictContext(DependencyNode root, Object conflictId, Map<DependencyNode, Object> conflictIds, Collection<ConflictResolver.ConflictItem> items) {
      this(root, conflictIds, items);
      this.conflictId = conflictId;
    }
    
    public DependencyNode getRoot() {
      return this.root;
    }
    
    public boolean isIncluded(DependencyNode node) {
      return this.conflictId.equals(this.conflictIds.get(node));
    }
    
    public Collection<ConflictResolver.ConflictItem> getItems() {
      return this.items;
    }
    
    public ConflictResolver.ConflictItem getWinner() {
      return this.winner;
    }
    
    public void setWinner(ConflictResolver.ConflictItem winner) {
      this.winner = winner;
    }
    
    public String getScope() {
      return this.scope;
    }
    
    public void setScope(String scope) {
      this.scope = scope;
    }
    
    public Boolean getOptional() {
      return this.optional;
    }
    
    public void setOptional(Boolean optional) {
      this.optional = optional;
    }
    
    public String toString() {
      return this.winner + " @ " + this.scope + " < " + this.items;
    }
  }
  
  public static abstract class VersionSelector {
    public VersionSelector getInstance(DependencyNode root, DependencyGraphTransformationContext context) throws RepositoryException {
      return this;
    }
    
    public abstract void selectVersion(ConflictResolver.ConflictContext param1ConflictContext) throws RepositoryException;
  }
  
  public static abstract class ScopeSelector {
    public ScopeSelector getInstance(DependencyNode root, DependencyGraphTransformationContext context) throws RepositoryException {
      return this;
    }
    
    public abstract void selectScope(ConflictResolver.ConflictContext param1ConflictContext) throws RepositoryException;
  }
  
  public static abstract class ScopeDeriver {
    public ScopeDeriver getInstance(DependencyNode root, DependencyGraphTransformationContext context) throws RepositoryException {
      return this;
    }
    
    public abstract void deriveScope(ConflictResolver.ScopeContext param1ScopeContext) throws RepositoryException;
  }
  
  public static abstract class OptionalitySelector {
    public OptionalitySelector getInstance(DependencyNode root, DependencyGraphTransformationContext context) throws RepositoryException {
      return this;
    }
    
    public abstract void selectOptionality(ConflictResolver.ConflictContext param1ConflictContext) throws RepositoryException;
  }
}
