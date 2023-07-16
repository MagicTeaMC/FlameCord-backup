package org.eclipse.aether.internal.impl;

final class PrioritizedComponent<T> implements Comparable<PrioritizedComponent<?>> {
  private final T component;
  
  private final Class<?> type;
  
  private final float priority;
  
  private final int index;
  
  PrioritizedComponent(T component, Class<?> type, float priority, int index) {
    this.component = component;
    this.type = type;
    this.priority = priority;
    this.index = index;
  }
  
  public T getComponent() {
    return this.component;
  }
  
  public Class<?> getType() {
    return this.type;
  }
  
  public float getPriority() {
    return this.priority;
  }
  
  public boolean isDisabled() {
    return Float.isNaN(this.priority);
  }
  
  public int compareTo(PrioritizedComponent<?> o) {
    int rel = (isDisabled() ? 1 : 0) - (o.isDisabled() ? 1 : 0);
    if (rel == 0) {
      rel = Float.compare(o.priority, this.priority);
      if (rel == 0)
        rel = this.index - o.index; 
    } 
    return rel;
  }
  
  public String toString() {
    return this.priority + " (#" + this.index + "): " + this.component;
  }
}
