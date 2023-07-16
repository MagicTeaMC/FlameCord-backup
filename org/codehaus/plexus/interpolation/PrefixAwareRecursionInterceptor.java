package org.codehaus.plexus.interpolation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Stack;
import org.codehaus.plexus.interpolation.util.ValueSourceUtils;

public class PrefixAwareRecursionInterceptor implements RecursionInterceptor {
  public static final String DEFAULT_START_TOKEN = "\\$\\{";
  
  public static final String DEFAULT_END_TOKEN = "\\}";
  
  private Stack<String> nakedExpressions = new Stack<String>();
  
  private final String[] possiblePrefixes;
  
  private boolean watchUnprefixedExpressions = true;
  
  public PrefixAwareRecursionInterceptor(Collection<String> possiblePrefixes, boolean watchUnprefixedExpressions) {
    this.possiblePrefixes = possiblePrefixes.<String>toArray(new String[possiblePrefixes.size()]);
    this.watchUnprefixedExpressions = watchUnprefixedExpressions;
  }
  
  public PrefixAwareRecursionInterceptor(Collection<String> possiblePrefixes) {
    this.possiblePrefixes = possiblePrefixes.<String>toArray(new String[possiblePrefixes.size()]);
  }
  
  public boolean hasRecursiveExpression(String expression) {
    String realExpr = ValueSourceUtils.trimPrefix(expression, this.possiblePrefixes, this.watchUnprefixedExpressions);
    return (realExpr != null && this.nakedExpressions.contains(realExpr));
  }
  
  public void expressionResolutionFinished(String expression) {
    this.nakedExpressions.pop();
  }
  
  public void expressionResolutionStarted(String expression) {
    String realExpr = ValueSourceUtils.trimPrefix(expression, this.possiblePrefixes, this.watchUnprefixedExpressions);
    this.nakedExpressions.push(realExpr);
  }
  
  public List getExpressionCycle(String expression) {
    String expr = ValueSourceUtils.trimPrefix(expression, this.possiblePrefixes, this.watchUnprefixedExpressions);
    if (expr == null)
      return Collections.EMPTY_LIST; 
    int idx = this.nakedExpressions.indexOf(expr);
    if (idx < 0)
      return Collections.EMPTY_LIST; 
    return this.nakedExpressions.subList(idx, this.nakedExpressions.size());
  }
  
  public void clear() {
    this.nakedExpressions.clear();
  }
}
