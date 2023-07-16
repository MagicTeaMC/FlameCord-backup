package org.codehaus.plexus.interpolation.util;

import java.util.Collection;

public final class ValueSourceUtils {
  public static String trimPrefix(String expression, Collection<String> possiblePrefixes, boolean allowUnprefixedExpressions) {
    if (expression == null)
      return null; 
    String realExpr = null;
    for (String prefix : possiblePrefixes) {
      if (expression.startsWith(prefix)) {
        realExpr = expression.substring(prefix.length());
        if (realExpr.startsWith("."))
          realExpr = realExpr.substring(1); 
        break;
      } 
    } 
    if (realExpr == null && allowUnprefixedExpressions)
      realExpr = expression; 
    return realExpr;
  }
  
  public static String trimPrefix(String expression, String[] possiblePrefixes, boolean allowUnprefixedExpressions) {
    if (expression == null)
      return null; 
    String realExpr = null;
    for (String prefix : possiblePrefixes) {
      if (expression.startsWith(prefix)) {
        realExpr = expression.substring(prefix.length());
        if (realExpr.startsWith("."))
          realExpr = realExpr.substring(1); 
        break;
      } 
    } 
    if (realExpr == null && allowUnprefixedExpressions)
      realExpr = expression; 
    return realExpr;
  }
}
