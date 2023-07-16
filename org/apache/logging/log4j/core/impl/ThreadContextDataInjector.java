package org.apache.logging.log4j.core.impl;

import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedDeque;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.apache.logging.log4j.core.ContextDataInjector;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.util.ContextDataProvider;
import org.apache.logging.log4j.spi.ReadOnlyThreadContextMap;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.ReadOnlyStringMap;
import org.apache.logging.log4j.util.ServiceLoaderUtil;
import org.apache.logging.log4j.util.StringMap;

public class ThreadContextDataInjector {
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  public static Collection<ContextDataProvider> contextDataProviders = new ConcurrentLinkedDeque<>();
  
  private static final List<ContextDataProvider> SERVICE_PROVIDERS = getServiceProviders();
  
  @Deprecated
  public static void initServiceProviders() {}
  
  private static List<ContextDataProvider> getServiceProviders() {
    List<ContextDataProvider> providers = new ArrayList<>();
    ServiceLoaderUtil.loadServices(ContextDataProvider.class, MethodHandles.lookup(), false)
      .forEach(providers::add);
    return Collections.unmodifiableList(providers);
  }
  
  public static class ForDefaultThreadContextMap implements ContextDataInjector {
    private final List<ContextDataProvider> providers = ThreadContextDataInjector.getProviders();
    
    public StringMap injectContextData(List<Property> props, StringMap contextData) {
      Map<String, String> copy;
      if (this.providers.size() == 1) {
        copy = ((ContextDataProvider)this.providers.get(0)).supplyContextData();
      } else {
        copy = new HashMap<>();
        for (ContextDataProvider provider : this.providers)
          copy.putAll(provider.supplyContextData()); 
      } 
      if (props == null || props.isEmpty())
        return copy.isEmpty() ? ContextDataFactory.emptyFrozenContextData() : frozenStringMap(copy); 
      StringMap result = new JdkMapAdapterStringMap(new HashMap<>(copy));
      for (int i = 0; i < props.size(); i++) {
        Property prop = props.get(i);
        if (!copy.containsKey(prop.getName()))
          result.putValue(prop.getName(), prop.getValue()); 
      } 
      result.freeze();
      return result;
    }
    
    private static JdkMapAdapterStringMap frozenStringMap(Map<String, String> copy) {
      JdkMapAdapterStringMap result = new JdkMapAdapterStringMap(copy);
      result.freeze();
      return result;
    }
    
    public ReadOnlyStringMap rawContextData() {
      ReadOnlyThreadContextMap map = ThreadContext.getThreadContextMap();
      if (map instanceof ReadOnlyStringMap)
        return (ReadOnlyStringMap)map; 
      Map<String, String> copy = ThreadContext.getImmutableContext();
      return copy.isEmpty() ? (ReadOnlyStringMap)ContextDataFactory.emptyFrozenContextData() : (ReadOnlyStringMap)new JdkMapAdapterStringMap(copy);
    }
  }
  
  public static class ForGarbageFreeThreadContextMap implements ContextDataInjector {
    private final List<ContextDataProvider> providers = ThreadContextDataInjector.getProviders();
    
    public StringMap injectContextData(List<Property> props, StringMap reusable) {
      ThreadContextDataInjector.copyProperties(props, reusable);
      for (int i = 0; i < this.providers.size(); i++)
        reusable.putAll((ReadOnlyStringMap)((ContextDataProvider)this.providers.get(i)).supplyStringMap()); 
      return reusable;
    }
    
    public ReadOnlyStringMap rawContextData() {
      return (ReadOnlyStringMap)ThreadContext.getThreadContextMap().getReadOnlyContextData();
    }
  }
  
  public static class ForCopyOnWriteThreadContextMap implements ContextDataInjector {
    private final List<ContextDataProvider> providers = ThreadContextDataInjector.getProviders();
    
    public StringMap injectContextData(List<Property> props, StringMap ignore) {
      if (this.providers.size() == 1 && (props == null || props.isEmpty()))
        return ((ContextDataProvider)this.providers.get(0)).supplyStringMap(); 
      int count = (props == null) ? 0 : props.size();
      StringMap[] maps = new StringMap[this.providers.size()];
      for (int i = 0; i < this.providers.size(); i++) {
        maps[i] = ((ContextDataProvider)this.providers.get(i)).supplyStringMap();
        count += maps[i].size();
      } 
      StringMap result = ContextDataFactory.createContextData(count);
      ThreadContextDataInjector.copyProperties(props, result);
      for (StringMap map : maps)
        result.putAll((ReadOnlyStringMap)map); 
      return result;
    }
    
    public ReadOnlyStringMap rawContextData() {
      return (ReadOnlyStringMap)ThreadContext.getThreadContextMap().getReadOnlyContextData();
    }
  }
  
  public static void copyProperties(List<Property> properties, StringMap result) {
    if (properties != null)
      for (int i = 0; i < properties.size(); i++) {
        Property prop = properties.get(i);
        result.putValue(prop.getName(), prop.getValue());
      }  
  }
  
  private static List<ContextDataProvider> getProviders() {
    List<ContextDataProvider> providers = new ArrayList<>(contextDataProviders.size() + SERVICE_PROVIDERS.size());
    providers.addAll(contextDataProviders);
    providers.addAll(SERVICE_PROVIDERS);
    return providers;
  }
}
