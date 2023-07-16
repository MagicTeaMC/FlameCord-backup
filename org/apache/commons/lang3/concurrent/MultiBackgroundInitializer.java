package org.apache.commons.lang3.concurrent;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import org.apache.commons.lang3.Validate;

public class MultiBackgroundInitializer extends BackgroundInitializer<MultiBackgroundInitializer.MultiBackgroundInitializerResults> {
  private final Map<String, BackgroundInitializer<?>> childInitializers = new HashMap<>();
  
  public MultiBackgroundInitializer(ExecutorService exec) {
    super(exec);
  }
  
  public void addInitializer(String name, BackgroundInitializer<?> init) {
    Validate.isTrue((name != null), "Name of child initializer must not be null!", new Object[0]);
    Validate.isTrue((init != null), "Child initializer must not be null!", new Object[0]);
    synchronized (this) {
      if (isStarted())
        throw new IllegalStateException("addInitializer() must not be called after start()!"); 
      this.childInitializers.put(name, init);
    } 
  }
  
  protected int getTaskCount() {
    int result = 1;
    for (BackgroundInitializer<?> bi : this.childInitializers.values())
      result += bi.getTaskCount(); 
    return result;
  }
  
  protected MultiBackgroundInitializerResults initialize() throws Exception {
    Map<String, BackgroundInitializer<?>> inits;
    synchronized (this) {
      inits = new HashMap<>(this.childInitializers);
    } 
    ExecutorService exec = getActiveExecutor();
    for (BackgroundInitializer<?> bi : inits.values()) {
      if (bi.getExternalExecutor() == null)
        bi.setExternalExecutor(exec); 
      bi.start();
    } 
    Map<String, Object> results = new HashMap<>();
    Map<String, ConcurrentException> excepts = new HashMap<>();
    for (Map.Entry<String, BackgroundInitializer<?>> e : inits.entrySet()) {
      try {
        results.put(e.getKey(), ((BackgroundInitializer)e.getValue()).get());
      } catch (ConcurrentException cex) {
        excepts.put(e.getKey(), cex);
      } 
    } 
    return new MultiBackgroundInitializerResults(inits, results, excepts);
  }
  
  public MultiBackgroundInitializer() {}
  
  public static class MultiBackgroundInitializerResults {
    private final Map<String, BackgroundInitializer<?>> initializers;
    
    private final Map<String, Object> resultObjects;
    
    private final Map<String, ConcurrentException> exceptions;
    
    private MultiBackgroundInitializerResults(Map<String, BackgroundInitializer<?>> inits, Map<String, Object> results, Map<String, ConcurrentException> excepts) {
      this.initializers = inits;
      this.resultObjects = results;
      this.exceptions = excepts;
    }
    
    public BackgroundInitializer<?> getInitializer(String name) {
      return checkName(name);
    }
    
    public Object getResultObject(String name) {
      checkName(name);
      return this.resultObjects.get(name);
    }
    
    public boolean isException(String name) {
      checkName(name);
      return this.exceptions.containsKey(name);
    }
    
    public ConcurrentException getException(String name) {
      checkName(name);
      return this.exceptions.get(name);
    }
    
    public Set<String> initializerNames() {
      return Collections.unmodifiableSet(this.initializers.keySet());
    }
    
    public boolean isSuccessful() {
      return this.exceptions.isEmpty();
    }
    
    private BackgroundInitializer<?> checkName(String name) {
      BackgroundInitializer<?> init = this.initializers.get(name);
      if (init == null)
        throw new NoSuchElementException("No child initializer with name " + name); 
      return init;
    }
  }
}
