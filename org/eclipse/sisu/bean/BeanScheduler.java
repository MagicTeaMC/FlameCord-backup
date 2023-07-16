package org.eclipse.sisu.bean;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.BindingScopingVisitor;
import com.google.inject.spi.DefaultBindingScopingVisitor;
import com.google.inject.spi.ProvisionListener;
import java.util.ArrayList;

public abstract class BeanScheduler {
  static final Object CYCLE_ACTIVATOR;
  
  static final Object CANDIDATE_CYCLE;
  
  static final Object CYCLE_CONFIRMED;
  
  static {
    Object cycleActivator, candidateCycle = new Object();
    Object cycleConfirmed = new Object();
    try {
      Binder.class.getMethod("bindListener", new Class[] { Matcher.class, ProvisionListener[].class });
      String detectCycles = System.getProperty("sisu.detect.cycles");
      if ("false".equalsIgnoreCase(detectCycles)) {
        cycleActivator = null;
      } else {
        cycleActivator = new CycleActivator();
      } 
      if ("pessimistic".equalsIgnoreCase(detectCycles))
        candidateCycle = cycleConfirmed; 
    } catch (Exception exception) {
      cycleActivator = null;
    } catch (LinkageError linkageError) {
      cycleActivator = null;
    } 
    CYCLE_ACTIVATOR = cycleActivator;
    CANDIDATE_CYCLE = candidateCycle;
    CYCLE_CONFIRMED = cycleConfirmed;
  }
  
  public static final Module MODULE = new Module() {
      public void configure(Binder binder) {
        if (BeanScheduler.CYCLE_ACTIVATOR != null)
          binder.bindListener(Matchers.any(), new ProvisionListener[] { (ProvisionListener)BeanScheduler.CYCLE_ACTIVATOR }); 
      }
    };
  
  static final ThreadLocal<Object[]> pendingHolder = new ThreadLocal();
  
  public static void detectCycle(Object value) {
    if (CYCLE_ACTIVATOR != null && Scopes.isCircularProxy(value)) {
      Object[] holder = pendingHolder.get();
      if (holder != null) {
        Object pending = holder[0];
        if (CANDIDATE_CYCLE.equals(pending))
          holder[0] = CYCLE_CONFIRMED; 
      } 
    } 
  }
  
  public final void schedule(Object bean) {
    if (CYCLE_ACTIVATOR != null) {
      Object[] holder = pendingHolder.get();
      if (holder != null) {
        Object pending = holder[0];
        if (CYCLE_CONFIRMED.equals(pending)) {
          holder[0] = new Pending(bean);
          return;
        } 
        if (pending instanceof Pending) {
          ((Pending)pending).add(bean);
          return;
        } 
      } 
    } 
    activate(bean);
  }
  
  protected abstract void activate(Object paramObject);
  
  private final class Pending extends ArrayList<Object> {
    Pending(Object bean) {
      add(bean);
    }
    
    public void activate() {
      for (int i = 0, size = size(); i < size; i++)
        BeanScheduler.this.activate(get(i)); 
    }
  }
  
  static final class CycleActivator implements ProvisionListener {
    private static final BindingScopingVisitor<Boolean> IS_SCOPED = (BindingScopingVisitor<Boolean>)new DefaultBindingScopingVisitor<Boolean>() {
        public Boolean visitNoScoping() {
          return Boolean.FALSE;
        }
        
        protected Boolean visitOther() {
          return Boolean.TRUE;
        }
      };
    
    public <T> void onProvision(ProvisionListener.ProvisionInvocation<T> pi) {
      if (Boolean.TRUE.equals(pi.getBinding().acceptScopingVisitor(IS_SCOPED))) {
        Object[] holder = BeanScheduler.pendingHolder.get();
        if (holder == null)
          BeanScheduler.pendingHolder.set(holder = new Object[1]); 
        if (holder[0] == null) {
          Object pending;
          holder[0] = BeanScheduler.CANDIDATE_CYCLE;
          try {
            pi.provision();
          } finally {
            Object object = holder[0];
            holder[0] = null;
          } 
          if (pending instanceof BeanScheduler.Pending)
            ((BeanScheduler.Pending)pending).activate(); 
        } 
      } 
    }
  }
}
