package org.codehaus.plexus.interpolation;

import java.util.List;

public interface Interpolator extends BasicInterpolator {
  void addValueSource(ValueSource paramValueSource);
  
  void removeValuesSource(ValueSource paramValueSource);
  
  void addPostProcessor(InterpolationPostProcessor paramInterpolationPostProcessor);
  
  void removePostProcessor(InterpolationPostProcessor paramInterpolationPostProcessor);
  
  String interpolate(String paramString1, String paramString2) throws InterpolationException;
  
  String interpolate(String paramString1, String paramString2, RecursionInterceptor paramRecursionInterceptor) throws InterpolationException;
  
  List getFeedback();
  
  void clearFeedback();
  
  boolean isCacheAnswers();
  
  void setCacheAnswers(boolean paramBoolean);
  
  void clearAnswers();
}
