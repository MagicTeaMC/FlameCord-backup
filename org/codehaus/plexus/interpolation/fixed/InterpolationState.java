package org.codehaus.plexus.interpolation.fixed;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.codehaus.plexus.interpolation.RecursionInterceptor;
import org.codehaus.plexus.interpolation.SimpleRecursionInterceptor;

public class InterpolationState {
  private final List<String> messages = new ArrayList<String>();
  
  private final List<Throwable> causes = new ArrayList<Throwable>();
  
  public void addFeedback(String message, Throwable cause) {
    this.messages.add(message);
    this.causes.add(cause);
  }
  
  public List asList() {
    ArrayList<Object> items = new ArrayList();
    for (int i = 0; i < this.messages.size(); i++) {
      String msg = this.messages.get(i);
      if (msg != null)
        items.add(msg); 
      Throwable cause = this.causes.get(i);
      if (cause != null)
        items.add(cause); 
    } 
    return (items.size() > 0) ? items : null;
  }
  
  public void clear() {
    this.messages.clear();
    this.causes.clear();
    this.unresolvable.clear();
    this.recursionInterceptor.clear();
    this.root = null;
  }
  
  final Set<String> unresolvable = new HashSet<String>();
  
  RecursionInterceptor recursionInterceptor = (RecursionInterceptor)new SimpleRecursionInterceptor();
  
  public void setRecursionInterceptor(RecursionInterceptor recursionInterceptor) {
    this.recursionInterceptor = recursionInterceptor;
  }
  
  FixedStringSearchInterpolator root = null;
}
