package org.codehaus.plexus.interpolation.fixed;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.interpolation.BasicInterpolator;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.RecursionInterceptor;

public class FixedStringSearchInterpolator implements FixedValueSource {
  private final FixedValueSource[] valueSources;
  
  private final InterpolationPostProcessor postProcessor;
  
  public static final String DEFAULT_START_EXPR = "${";
  
  public static final String DEFAULT_END_EXPR = "}";
  
  private final String startExpr;
  
  private final String endExpr;
  
  private final String escapeString;
  
  private FixedStringSearchInterpolator(String startExpr, String endExpr, String escapeString, InterpolationPostProcessor postProcessor, FixedValueSource... valueSources) {
    this.startExpr = startExpr;
    this.endExpr = endExpr;
    this.escapeString = escapeString;
    if (valueSources == null)
      throw new IllegalArgumentException("valueSources cannot be null"); 
    for (int i = 0; i < valueSources.length; i++) {
      if (valueSources[i] == null)
        throw new IllegalArgumentException("valueSources[" + i + "] is null"); 
    } 
    this.valueSources = valueSources;
    this.postProcessor = postProcessor;
  }
  
  public static FixedStringSearchInterpolator create(String startExpr, String endExpr, FixedValueSource... valueSources) {
    return new FixedStringSearchInterpolator(startExpr, endExpr, null, null, valueSources);
  }
  
  public static FixedStringSearchInterpolator create(FixedValueSource... valueSources) {
    return new FixedStringSearchInterpolator("${", "}", null, null, valueSources);
  }
  
  public static FixedStringSearchInterpolator createWithPermittedNulls(FixedValueSource... valueSources) {
    List<FixedValueSource> nonnulls = new ArrayList<FixedValueSource>();
    for (FixedValueSource item : valueSources) {
      if (item != null)
        nonnulls.add(item); 
    } 
    return new FixedStringSearchInterpolator("${", "}", null, null, nonnulls.<FixedValueSource>toArray(new FixedValueSource[nonnulls.size()]));
  }
  
  public FixedStringSearchInterpolator withExpressionMarkers(String startExpr, String endExpr) {
    return new FixedStringSearchInterpolator(startExpr, endExpr, this.escapeString, this.postProcessor, this.valueSources);
  }
  
  public FixedStringSearchInterpolator withPostProcessor(InterpolationPostProcessor postProcessor) {
    return new FixedStringSearchInterpolator(this.startExpr, this.endExpr, this.escapeString, postProcessor, this.valueSources);
  }
  
  public FixedStringSearchInterpolator withEscapeString(String escapeString) {
    return new FixedStringSearchInterpolator(this.startExpr, this.endExpr, escapeString, this.postProcessor, this.valueSources);
  }
  
  public String interpolate(String input) throws InterpolationCycleException {
    return interpolate(input, new InterpolationState());
  }
  
  public static FixedStringSearchInterpolator empty() {
    return create(new FixedValueSource[0]);
  }
  
  public Object getValue(String realExpr, InterpolationState interpolationState) {
    interpolationState.recursionInterceptor.expressionResolutionStarted(realExpr);
    try {
      Object value = null;
      for (FixedValueSource valueSource : this.valueSources) {
        value = valueSource.getValue(realExpr, interpolationState);
        if (value != null)
          break; 
      } 
      if (value != null) {
        if (interpolationState.root != null)
          value = interpolationState.root.interpolate(String.valueOf(value), interpolationState); 
        return String.valueOf(value);
      } 
      return null;
    } finally {
      interpolationState.recursionInterceptor.expressionResolutionFinished(realExpr);
    } 
  }
  
  public BasicInterpolator asBasicInterpolator() {
    final InterpolationState is = new InterpolationState();
    return new BasicInterpolator() {
        public String interpolate(String input) throws InterpolationException {
          return FixedStringSearchInterpolator.this.interpolate(input, is);
        }
        
        public String interpolate(String input, RecursionInterceptor recursionInterceptor) throws InterpolationException {
          is.setRecursionInterceptor(recursionInterceptor);
          return FixedStringSearchInterpolator.this.interpolate(input, is);
        }
      };
  }
  
  public String interpolate(String input, InterpolationState interpolationState) throws InterpolationCycleException {
    if (interpolationState.root == null)
      interpolationState.root = this; 
    if (input == null)
      return ""; 
    StringBuilder result = new StringBuilder(input.length() * 2);
    int endIdx = -1;
    int startIdx;
    while ((startIdx = input.indexOf(this.startExpr, endIdx + 1)) > -1) {
      result.append(input, endIdx + 1, startIdx);
      endIdx = input.indexOf(this.endExpr, startIdx + 1);
      if (endIdx < 0)
        break; 
      String wholeExpr = input.substring(startIdx, endIdx + this.endExpr.length());
      String realExpr = wholeExpr.substring(this.startExpr.length(), wholeExpr.length() - this.endExpr.length());
      if (startIdx >= 0 && this.escapeString != null && this.escapeString.length() > 0) {
        int startEscapeIdx = (startIdx == 0) ? 0 : (startIdx - this.escapeString.length());
        if (startEscapeIdx >= 0) {
          String escape = input.substring(startEscapeIdx, startIdx);
          if (this.escapeString.equals(escape)) {
            result.append(wholeExpr);
            result.replace(startEscapeIdx, startEscapeIdx + this.escapeString.length(), "");
            continue;
          } 
        } 
      } 
      boolean resolved = false;
      if (!interpolationState.unresolvable.contains(wholeExpr)) {
        if (realExpr.startsWith("."))
          realExpr = realExpr.substring(1); 
        if (interpolationState.recursionInterceptor.hasRecursiveExpression(realExpr))
          throw new InterpolationCycleException(interpolationState.recursionInterceptor, realExpr, wholeExpr); 
        Object value = getValue(realExpr, interpolationState);
        if (value != null) {
          value = interpolate(String.valueOf(value), interpolationState);
          if (this.postProcessor != null) {
            Object newVal = this.postProcessor.execute(realExpr, value);
            if (newVal != null)
              value = newVal; 
          } 
          result.append(String.valueOf(value));
          resolved = true;
        } else {
          interpolationState.unresolvable.add(wholeExpr);
        } 
      } 
      if (!resolved)
        result.append(wholeExpr); 
      if (endIdx > -1)
        endIdx += this.endExpr.length() - 1; 
    } 
    if (endIdx == -1 && startIdx > -1) {
      result.append(input, startIdx, input.length());
    } else if (endIdx < input.length()) {
      result.append(input, endIdx + 1, input.length());
    } 
    return result.toString();
  }
}
