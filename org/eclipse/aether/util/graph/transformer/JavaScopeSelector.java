package org.eclipse.aether.util.graph.transformer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.aether.RepositoryException;

public final class JavaScopeSelector extends ConflictResolver.ScopeSelector {
  public void selectScope(ConflictResolver.ConflictContext context) throws RepositoryException {
    String scope = context.getWinner().getDependency().getScope();
    if (!"system".equals(scope))
      scope = chooseEffectiveScope(context.getItems()); 
    context.setScope(scope);
  }
  
  private String chooseEffectiveScope(Collection<ConflictResolver.ConflictItem> items) {
    Set<String> scopes = new HashSet<>();
    for (ConflictResolver.ConflictItem item : items) {
      if (item.getDepth() <= 1)
        return item.getDependency().getScope(); 
      scopes.addAll(item.getScopes());
    } 
    return chooseEffectiveScope(scopes);
  }
  
  private String chooseEffectiveScope(Set<String> scopes) {
    if (scopes.size() > 1)
      scopes.remove("system"); 
    String effectiveScope = "";
    if (scopes.size() == 1) {
      effectiveScope = scopes.iterator().next();
    } else if (scopes.contains("compile")) {
      effectiveScope = "compile";
    } else if (scopes.contains("runtime")) {
      effectiveScope = "runtime";
    } else if (scopes.contains("provided")) {
      effectiveScope = "provided";
    } else if (scopes.contains("test")) {
      effectiveScope = "test";
    } 
    return effectiveScope;
  }
}
