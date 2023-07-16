package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.util.ConfigUtils;

final class PrioritizedComponents<T> {
  private static final String FACTORY_SUFFIX = "Factory";
  
  private final Map<?, ?> configProps;
  
  private final boolean useInsertionOrder;
  
  private final List<PrioritizedComponent<T>> components;
  
  private int firstDisabled;
  
  PrioritizedComponents(RepositorySystemSession session) {
    this(session.getConfigProperties());
  }
  
  PrioritizedComponents(Map<?, ?> configurationProperties) {
    this.configProps = configurationProperties;
    this
      .useInsertionOrder = ConfigUtils.getBoolean(this.configProps, false, new String[] { "aether.priority.implicit" });
    this.components = new ArrayList<>();
    this.firstDisabled = 0;
  }
  
  public void add(T component, float priority) {
    Class<?> type = getImplClass(component);
    int index = this.components.size();
    priority = this.useInsertionOrder ? -index : ConfigUtils.getFloat(this.configProps, priority, getConfigKeys(type));
    PrioritizedComponent<T> pc = new PrioritizedComponent<>(component, type, priority, index);
    if (!this.useInsertionOrder) {
      index = Collections.binarySearch((List)this.components, (T)pc);
      if (index < 0) {
        index = -index - 1;
      } else {
        index++;
      } 
    } 
    this.components.add(index, pc);
    if (index <= this.firstDisabled && !pc.isDisabled())
      this.firstDisabled++; 
  }
  
  private static Class<?> getImplClass(Object component) {
    Class<?> type = component.getClass();
    int idx = type.getName().indexOf("$$");
    if (idx >= 0) {
      Class<?> base = type.getSuperclass();
      if (base != null && idx == base.getName().length() && type.getName().startsWith(base.getName()))
        type = base; 
    } 
    return type;
  }
  
  static String[] getConfigKeys(Class<?> type) {
    List<String> keys = new ArrayList<>();
    keys.add("aether.priority." + type.getName());
    String sn = type.getSimpleName();
    keys.add("aether.priority." + sn);
    if (sn.endsWith("Factory"))
      keys.add("aether.priority." + sn
          .substring(0, sn.length() - "Factory".length())); 
    return keys.<String>toArray(new String[keys.size()]);
  }
  
  public boolean isEmpty() {
    return this.components.isEmpty();
  }
  
  public List<PrioritizedComponent<T>> getAll() {
    return this.components;
  }
  
  public List<PrioritizedComponent<T>> getEnabled() {
    return this.components.subList(0, this.firstDisabled);
  }
  
  public void list(StringBuilder buffer) {
    for (int i = 0; i < this.components.size(); i++) {
      if (i > 0)
        buffer.append(", "); 
      PrioritizedComponent<?> component = this.components.get(i);
      buffer.append(component.getType().getSimpleName());
      if (component.isDisabled())
        buffer.append(" (disabled)"); 
    } 
  }
  
  public String toString() {
    return this.components.toString();
  }
}
