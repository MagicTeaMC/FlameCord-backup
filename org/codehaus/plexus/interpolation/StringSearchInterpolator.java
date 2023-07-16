package org.codehaus.plexus.interpolation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class StringSearchInterpolator implements Interpolator {
  private Map<String, Object> existingAnswers = new HashMap<String, Object>();
  
  private List<ValueSource> valueSources = new ArrayList<ValueSource>();
  
  private List<InterpolationPostProcessor> postProcessors = new ArrayList<InterpolationPostProcessor>();
  
  private boolean cacheAnswers = false;
  
  public static final String DEFAULT_START_EXPR = "${";
  
  public static final String DEFAULT_END_EXPR = "}";
  
  private String startExpr;
  
  private String endExpr;
  
  private String escapeString;
  
  public StringSearchInterpolator() {
    this.startExpr = "${";
    this.endExpr = "}";
  }
  
  public StringSearchInterpolator(String startExpr, String endExpr) {
    this.startExpr = startExpr;
    this.endExpr = endExpr;
  }
  
  public void addValueSource(ValueSource valueSource) {
    this.valueSources.add(valueSource);
  }
  
  public void removeValuesSource(ValueSource valueSource) {
    this.valueSources.remove(valueSource);
  }
  
  public void addPostProcessor(InterpolationPostProcessor postProcessor) {
    this.postProcessors.add(postProcessor);
  }
  
  public void removePostProcessor(InterpolationPostProcessor postProcessor) {
    this.postProcessors.remove(postProcessor);
  }
  
  public String interpolate(String input, String thisPrefixPattern) throws InterpolationException {
    return interpolate(input, new SimpleRecursionInterceptor());
  }
  
  public String interpolate(String input, String thisPrefixPattern, RecursionInterceptor recursionInterceptor) throws InterpolationException {
    return interpolate(input, recursionInterceptor);
  }
  
  public String interpolate(String input) throws InterpolationException {
    return interpolate(input, new SimpleRecursionInterceptor());
  }
  
  public String interpolate(String input, RecursionInterceptor recursionInterceptor) throws InterpolationException {
    try {
      return interpolate(input, recursionInterceptor, new HashSet<String>());
    } finally {
      if (!this.cacheAnswers)
        this.existingAnswers.clear(); 
    } 
  }
  
  private String interpolate(String input, RecursionInterceptor recursionInterceptor, Set<String> unresolvable) throws InterpolationException {
    if (input == null)
      return ""; 
    int endIdx = -1;
    int startIdx;
    if ((startIdx = input.indexOf(this.startExpr, endIdx + 1)) > -1) {
      StringBuilder result = new StringBuilder(input.length() * 2);
      label76: do {
        result.append(input, endIdx + 1, startIdx);
        endIdx = input.indexOf(this.endExpr, startIdx + 1);
        if (endIdx < 0)
          break label76; 
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
        if (!unresolvable.contains(wholeExpr)) {
          if (realExpr.startsWith("."))
            realExpr = realExpr.substring(1); 
          if (recursionInterceptor.hasRecursiveExpression(realExpr))
            throw new InterpolationCycleException(recursionInterceptor, realExpr, wholeExpr); 
          recursionInterceptor.expressionResolutionStarted(realExpr);
          try {
            Object value = this.existingAnswers.get(realExpr);
            Object bestAnswer = null;
            for (ValueSource valueSource : this.valueSources) {
              if (value != null)
                break; 
              value = valueSource.getValue(realExpr);
              if (value != null && value.toString().contains(wholeExpr)) {
                bestAnswer = value;
                value = null;
              } 
            } 
            if (value == null && bestAnswer != null)
              throw new InterpolationCycleException(recursionInterceptor, realExpr, wholeExpr); 
            if (value != null) {
              value = interpolate(String.valueOf(value), recursionInterceptor, unresolvable);
              if (this.postProcessors != null && !this.postProcessors.isEmpty())
                for (InterpolationPostProcessor postProcessor : this.postProcessors) {
                  Object newVal = postProcessor.execute(realExpr, value);
                  if (newVal != null) {
                    value = newVal;
                    break;
                  } 
                }  
              result.append(String.valueOf(value));
              resolved = true;
            } else {
              unresolvable.add(wholeExpr);
            } 
            recursionInterceptor.expressionResolutionFinished(realExpr);
          } finally {
            recursionInterceptor.expressionResolutionFinished(realExpr);
          } 
        } 
        if (!resolved)
          result.append(wholeExpr); 
        if (endIdx > -1)
          endIdx += this.endExpr.length() - 1; 
      } while ((startIdx = input.indexOf(this.startExpr, endIdx + 1)) > -1);
      if (endIdx == -1 && startIdx > -1) {
        result.append(input, startIdx, input.length());
      } else if (endIdx < input.length()) {
        result.append(input, endIdx + 1, input.length());
      } 
      return result.toString();
    } 
    return input;
  }
  
  public List getFeedback() {
    List<?> messages = new ArrayList();
    for (ValueSource vs : this.valueSources) {
      List<?> feedback = vs.getFeedback();
      if (feedback != null && !feedback.isEmpty())
        messages.addAll(feedback); 
    } 
    return messages;
  }
  
  public void clearFeedback() {
    for (ValueSource vs : this.valueSources)
      vs.clearFeedback(); 
  }
  
  public boolean isCacheAnswers() {
    return this.cacheAnswers;
  }
  
  public void setCacheAnswers(boolean cacheAnswers) {
    this.cacheAnswers = cacheAnswers;
  }
  
  public void clearAnswers() {
    this.existingAnswers.clear();
  }
  
  public String getEscapeString() {
    return this.escapeString;
  }
  
  public void setEscapeString(String escapeString) {
    this.escapeString = escapeString;
  }
}
