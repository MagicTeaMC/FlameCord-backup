package org.codehaus.plexus.interpolation.multi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.plexus.interpolation.InterpolationCycleException;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.InterpolationPostProcessor;
import org.codehaus.plexus.interpolation.Interpolator;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;
import org.codehaus.plexus.interpolation.ValueSource;

public class MultiDelimiterStringSearchInterpolator implements Interpolator {
  private static final int MAX_TRIES = 10;
  
  private Map existingAnswers = new HashMap<Object, Object>();
  
  private List<ValueSource> valueSources = new ArrayList<ValueSource>();
  
  private List postProcessors = new ArrayList();
  
  private boolean cacheAnswers = false;
  
  private LinkedHashSet<DelimiterSpecification> delimiters = new LinkedHashSet<DelimiterSpecification>();
  
  private String escapeString;
  
  public MultiDelimiterStringSearchInterpolator() {
    this.delimiters.add(DelimiterSpecification.DEFAULT_SPEC);
  }
  
  public MultiDelimiterStringSearchInterpolator addDelimiterSpec(String delimiterSpec) {
    if (delimiterSpec == null)
      return this; 
    this.delimiters.add(DelimiterSpecification.parse(delimiterSpec));
    return this;
  }
  
  public boolean removeDelimiterSpec(String delimiterSpec) {
    if (delimiterSpec == null)
      return false; 
    return this.delimiters.remove(DelimiterSpecification.parse(delimiterSpec));
  }
  
  public MultiDelimiterStringSearchInterpolator withValueSource(ValueSource vs) {
    addValueSource(vs);
    return this;
  }
  
  public MultiDelimiterStringSearchInterpolator withPostProcessor(InterpolationPostProcessor postProcessor) {
    addPostProcessor(postProcessor);
    return this;
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
    return interpolate(input, (RecursionInterceptor)new SimpleRecursionInterceptor());
  }
  
  public String interpolate(String input, String thisPrefixPattern, RecursionInterceptor recursionInterceptor) throws InterpolationException {
    return interpolate(input, recursionInterceptor);
  }
  
  public String interpolate(String input) throws InterpolationException {
    return interpolate(input, (RecursionInterceptor)new SimpleRecursionInterceptor());
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
    StringBuilder result = new StringBuilder(input.length() * 2);
    String lastResult = input;
    int tries = 0;
    do {
      tries++;
      if (result.length() > 0) {
        lastResult = result.toString();
        result.setLength(0);
      } 
      int startIdx = -1;
      int endIdx = -1;
      DelimiterSpecification selectedSpec = null;
      while ((selectedSpec = select(input, endIdx)) != null) {
        String startExpr = selectedSpec.getBegin();
        String endExpr = selectedSpec.getEnd();
        startIdx = selectedSpec.getNextStartIndex();
        result.append(input, endIdx + 1, startIdx);
        endIdx = input.indexOf(endExpr, startIdx + 1);
        if (endIdx < 0)
          break; 
        String wholeExpr = input.substring(startIdx, endIdx + endExpr.length());
        String realExpr = wholeExpr.substring(startExpr.length(), wholeExpr.length() - endExpr.length());
        if (startIdx >= 0 && this.escapeString != null && this.escapeString.length() > 0) {
          int startEscapeIdx = (startIdx == 0) ? 0 : (startIdx - this.escapeString.length());
          if (startEscapeIdx >= 0) {
            String escape = input.substring(startEscapeIdx, startIdx);
            if (escape != null && this.escapeString.equals(escape)) {
              result.append(wholeExpr);
              if (startEscapeIdx > 0)
                startEscapeIdx--; 
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
          Object value = this.existingAnswers.get(realExpr);
          Object bestAnswer = null;
          for (ValueSource vs : this.valueSources) {
            if (value != null)
              break; 
            value = vs.getValue(realExpr);
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
              for (Object postProcessor1 : this.postProcessors) {
                InterpolationPostProcessor postProcessor = (InterpolationPostProcessor)postProcessor1;
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
        } 
        if (!resolved)
          result.append(wholeExpr); 
        if (endIdx > -1)
          endIdx += endExpr.length() - 1; 
      } 
      if (endIdx == -1 && startIdx > -1) {
        result.append(input, startIdx, input.length());
      } else if (endIdx < input.length()) {
        result.append(input, endIdx + 1, input.length());
      } 
    } while (!lastResult.equals(result.toString()) && tries < 10);
    return result.toString();
  }
  
  private DelimiterSpecification select(String input, int lastEndIdx) {
    DelimiterSpecification selected = null;
    for (DelimiterSpecification spec : this.delimiters) {
      spec.clearNextStart();
      if (selected == null) {
        int idx = input.indexOf(spec.getBegin(), lastEndIdx + 1);
        if (idx > -1) {
          spec.setNextStartIndex(idx);
          selected = spec;
        } 
      } 
    } 
    return selected;
  }
  
  public List getFeedback() {
    List messages = new ArrayList();
    for (ValueSource vs : this.valueSources) {
      List feedback = vs.getFeedback();
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
  
  public MultiDelimiterStringSearchInterpolator escapeString(String escapeString) {
    this.escapeString = escapeString;
    return this;
  }
  
  public MultiDelimiterStringSearchInterpolator setDelimiterSpecs(LinkedHashSet<String> specs) {
    this.delimiters.clear();
    for (String spec : specs) {
      if (spec == null)
        continue; 
      this.delimiters.add(DelimiterSpecification.parse(spec));
    } 
    return this;
  }
}
