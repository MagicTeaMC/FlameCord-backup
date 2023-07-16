package org.codehaus.plexus.interpolation;

public interface BasicInterpolator {
  String interpolate(String paramString) throws InterpolationException;
  
  String interpolate(String paramString, RecursionInterceptor paramRecursionInterceptor) throws InterpolationException;
}
