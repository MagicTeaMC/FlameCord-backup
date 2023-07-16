package org.codehaus.plexus.interpolation.fixed;

import org.codehaus.plexus.interpolation.RecursionInterceptor;

public class InterpolationCycleException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  
  public InterpolationCycleException(RecursionInterceptor recursionInterceptor, String realExpr, String wholeExpr) {
    super("Detected the following recursive expression cycle in '" + realExpr + "': " + recursionInterceptor
        .getExpressionCycle(realExpr) + wholeExpr);
  }
}
