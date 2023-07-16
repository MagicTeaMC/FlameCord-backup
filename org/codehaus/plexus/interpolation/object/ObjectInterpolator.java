package org.codehaus.plexus.interpolation.object;

import java.util.List;
import org.codehaus.plexus.interpolation.BasicInterpolator;
import org.codehaus.plexus.interpolation.InterpolationException;
import org.codehaus.plexus.interpolation.RecursionInterceptor;

public interface ObjectInterpolator {
  void interpolate(Object paramObject, BasicInterpolator paramBasicInterpolator) throws InterpolationException;
  
  void interpolate(Object paramObject, BasicInterpolator paramBasicInterpolator, RecursionInterceptor paramRecursionInterceptor) throws InterpolationException;
  
  boolean hasWarnings();
  
  List getWarnings();
}
