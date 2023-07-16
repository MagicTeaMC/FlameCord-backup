package org.codehaus.plexus.util.dag;

import java.util.Iterator;
import java.util.List;

public class CycleDetectedException extends Exception {
  private List<String> cycle;
  
  public CycleDetectedException(String message, List<String> cycle) {
    super(message);
    this.cycle = cycle;
  }
  
  public List<String> getCycle() {
    return this.cycle;
  }
  
  public String cycleToString() {
    StringBuilder buffer = new StringBuilder();
    for (Iterator<String> iterator = this.cycle.iterator(); iterator.hasNext(); ) {
      buffer.append(iterator.next());
      if (iterator.hasNext())
        buffer.append(" --> "); 
    } 
    return buffer.toString();
  }
  
  public String getMessage() {
    return super.getMessage() + " " + cycleToString();
  }
}
