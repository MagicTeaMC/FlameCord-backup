package org.eclipse.aether.util.graph.transformer;

import org.eclipse.aether.RepositoryException;

public final class JavaScopeDeriver extends ConflictResolver.ScopeDeriver {
  public void deriveScope(ConflictResolver.ScopeContext context) throws RepositoryException {
    context.setDerivedScope(getDerivedScope(context.getParentScope(), context.getChildScope()));
  }
  
  private String getDerivedScope(String parentScope, String childScope) {
    String derivedScope;
    if ("system".equals(childScope) || "test".equals(childScope)) {
      derivedScope = childScope;
    } else if (parentScope == null || parentScope.length() <= 0 || "compile".equals(parentScope)) {
      derivedScope = childScope;
    } else if ("test".equals(parentScope) || "runtime".equals(parentScope)) {
      derivedScope = parentScope;
    } else if ("system".equals(parentScope) || "provided".equals(parentScope)) {
      derivedScope = "provided";
    } else {
      derivedScope = "runtime";
    } 
    return derivedScope;
  }
}
