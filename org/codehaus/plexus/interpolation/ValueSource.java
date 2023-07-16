package org.codehaus.plexus.interpolation;

import java.util.List;

public interface ValueSource {
  Object getValue(String paramString);
  
  List getFeedback();
  
  void clearFeedback();
}
