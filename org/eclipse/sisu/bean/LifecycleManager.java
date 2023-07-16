package org.eclipse.sisu.bean;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class LifecycleManager extends BeanScheduler implements BeanManager {
  private final LifecycleBuilder builder = new LifecycleBuilder();
  
  private final Map<Class<?>, BeanLifecycle> lifecycles = new ConcurrentHashMap<Class<?>, BeanLifecycle>();
  
  private final Deque<Object> stoppableBeans = new ArrayDeque();
  
  public boolean manage(Class<?> clazz) {
    return buildLifecycle(clazz);
  }
  
  public PropertyBinding manage(BeanProperty<?> property) {
    return null;
  }
  
  public boolean manage(Object bean) {
    BeanLifecycle lifecycle = lifecycleFor(bean);
    if (lifecycle.isStoppable())
      pushStoppable(bean); 
    if (lifecycle.isStartable())
      schedule(bean); 
    return true;
  }
  
  public boolean unmanage(Object bean) {
    if (removeStoppable(bean))
      lifecycleFor(bean).stop(bean); 
    return true;
  }
  
  public boolean unmanage() {
    Object bean;
    while ((bean = popStoppable()) != null)
      lifecycleFor(bean).stop(bean); 
    return true;
  }
  
  protected void activate(Object bean) {
    lifecycleFor(bean).start(bean);
  }
  
  private boolean buildLifecycle(Class<?> clazz) {
    BeanLifecycle lifecycle = this.lifecycles.get(clazz);
    if (lifecycle == null) {
      lifecycle = this.builder.build(clazz);
      this.lifecycles.put(clazz, lifecycle);
    } 
    return (lifecycle != BeanLifecycle.NO_OP);
  }
  
  private BeanLifecycle lifecycleFor(Object bean) {
    if (bean != null)
      for (Class<?> c = bean.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
        BeanLifecycle lifecycle = this.lifecycles.get(c);
        if (lifecycle != null)
          return lifecycle; 
      }  
    return BeanLifecycle.NO_OP;
  }
  
  private void pushStoppable(Object bean) {
    synchronized (this.stoppableBeans) {
      this.stoppableBeans.addLast(bean);
    } 
  }
  
  private boolean removeStoppable(Object bean) {
    synchronized (this.stoppableBeans) {
      return this.stoppableBeans.remove(bean);
    } 
  }
  
  private Object popStoppable() {
    synchronized (this.stoppableBeans) {
      return this.stoppableBeans.pollLast();
    } 
  }
}
