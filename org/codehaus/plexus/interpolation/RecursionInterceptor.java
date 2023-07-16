package org.codehaus.plexus.interpolation;

import java.util.List;

public interface RecursionInterceptor {
  void expressionResolutionStarted(String paramString);
  
  void expressionResolutionFinished(String paramString);
  
  boolean hasRecursiveExpression(String paramString);
  
  List getExpressionCycle(String paramString);
  
  void clear();
}
