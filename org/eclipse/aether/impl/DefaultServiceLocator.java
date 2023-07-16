package org.eclipse.aether.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.internal.impl.DefaultArtifactResolver;
import org.eclipse.aether.internal.impl.DefaultChecksumPolicyProvider;
import org.eclipse.aether.internal.impl.DefaultDeployer;
import org.eclipse.aether.internal.impl.DefaultFileProcessor;
import org.eclipse.aether.internal.impl.DefaultInstaller;
import org.eclipse.aether.internal.impl.DefaultLocalRepositoryProvider;
import org.eclipse.aether.internal.impl.DefaultMetadataResolver;
import org.eclipse.aether.internal.impl.DefaultOfflineController;
import org.eclipse.aether.internal.impl.DefaultRemoteRepositoryManager;
import org.eclipse.aether.internal.impl.DefaultRepositoryConnectorProvider;
import org.eclipse.aether.internal.impl.DefaultRepositoryEventDispatcher;
import org.eclipse.aether.internal.impl.DefaultRepositoryLayoutProvider;
import org.eclipse.aether.internal.impl.DefaultRepositorySystem;
import org.eclipse.aether.internal.impl.DefaultSyncContextFactory;
import org.eclipse.aether.internal.impl.DefaultTransporterProvider;
import org.eclipse.aether.internal.impl.DefaultUpdateCheckManager;
import org.eclipse.aether.internal.impl.DefaultUpdatePolicyAnalyzer;
import org.eclipse.aether.internal.impl.EnhancedLocalRepositoryManagerFactory;
import org.eclipse.aether.internal.impl.Maven2RepositoryLayoutFactory;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.internal.impl.collect.DefaultDependencyCollector;
import org.eclipse.aether.internal.impl.slf4j.Slf4jLoggerFactory;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicyProvider;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutFactory;
import org.eclipse.aether.spi.connector.layout.RepositoryLayoutProvider;
import org.eclipse.aether.spi.connector.transport.TransporterProvider;
import org.eclipse.aether.spi.io.FileProcessor;
import org.eclipse.aether.spi.localrepo.LocalRepositoryManagerFactory;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.spi.log.LoggerFactory;

public final class DefaultServiceLocator implements ServiceLocator {
  private final Map<Class<?>, Entry<?>> entries;
  
  private ErrorHandler errorHandler;
  
  private class Entry<T> {
    private final Class<T> type;
    
    private final Collection<Object> providers;
    
    private List<T> instances;
    
    Entry(Class<T> type) {
      this.type = Objects.<Class<T>>requireNonNull(type, "service type cannot be null");
      this.providers = new LinkedHashSet(8);
    }
    
    public synchronized void setServices(T... services) {
      this.providers.clear();
      if (services != null)
        for (T service : services)
          this.providers.add(Objects.requireNonNull(service, "service instance cannot be null"));  
      this.instances = null;
    }
    
    public synchronized void setService(Class<? extends T> impl) {
      this.providers.clear();
      addService(impl);
    }
    
    public synchronized void addService(Class<? extends T> impl) {
      this.providers.add(Objects.requireNonNull(impl, "implementation class cannot be null"));
      this.instances = null;
    }
    
    public T getInstance() {
      List<T> instances = getInstances();
      return instances.isEmpty() ? null : instances.get(0);
    }
    
    public synchronized List<T> getInstances() {
      if (this.instances == null) {
        this.instances = new ArrayList<>(this.providers.size());
        for (Object provider : this.providers) {
          T instance;
          if (provider instanceof Class) {
            instance = newInstance((Class)provider);
          } else {
            instance = this.type.cast(provider);
          } 
          if (instance != null)
            this.instances.add(instance); 
        } 
        this.instances = Collections.unmodifiableList(this.instances);
      } 
      return this.instances;
    }
    
    private T newInstance(Class<?> impl) {
      try {
        Constructor<?> constr = impl.getDeclaredConstructor(new Class[0]);
        if (!Modifier.isPublic(constr.getModifiers()))
          constr.setAccessible(true); 
        Object obj = constr.newInstance(new Object[0]);
        T instance = this.type.cast(obj);
        if (instance instanceof Service)
          ((Service)instance).initService(DefaultServiceLocator.this); 
        return instance;
      } catch (Exception|LinkageError e) {
        DefaultServiceLocator.this.serviceCreationFailed(this.type, impl, e);
        return null;
      } 
    }
  }
  
  public DefaultServiceLocator() {
    this.entries = new HashMap<>();
    addService(RepositorySystem.class, DefaultRepositorySystem.class);
    addService(ArtifactResolver.class, DefaultArtifactResolver.class);
    addService(DependencyCollector.class, DefaultDependencyCollector.class);
    addService(Deployer.class, DefaultDeployer.class);
    addService(Installer.class, DefaultInstaller.class);
    addService(MetadataResolver.class, DefaultMetadataResolver.class);
    addService(RepositoryLayoutProvider.class, DefaultRepositoryLayoutProvider.class);
    addService(RepositoryLayoutFactory.class, Maven2RepositoryLayoutFactory.class);
    addService(TransporterProvider.class, DefaultTransporterProvider.class);
    addService(ChecksumPolicyProvider.class, DefaultChecksumPolicyProvider.class);
    addService(RepositoryConnectorProvider.class, DefaultRepositoryConnectorProvider.class);
    addService(RemoteRepositoryManager.class, DefaultRemoteRepositoryManager.class);
    addService(UpdateCheckManager.class, DefaultUpdateCheckManager.class);
    addService(UpdatePolicyAnalyzer.class, DefaultUpdatePolicyAnalyzer.class);
    addService(FileProcessor.class, DefaultFileProcessor.class);
    addService(SyncContextFactory.class, DefaultSyncContextFactory.class);
    addService(RepositoryEventDispatcher.class, DefaultRepositoryEventDispatcher.class);
    addService(OfflineController.class, DefaultOfflineController.class);
    addService(LocalRepositoryProvider.class, DefaultLocalRepositoryProvider.class);
    addService(LocalRepositoryManagerFactory.class, SimpleLocalRepositoryManagerFactory.class);
    addService(LocalRepositoryManagerFactory.class, EnhancedLocalRepositoryManagerFactory.class);
    addService(LoggerFactory.class, Slf4jLoggerFactory.class);
  }
  
  private <T> Entry<T> getEntry(Class<T> type, boolean create) {
    Entry<T> entry = (Entry<T>)this.entries.get(Objects.requireNonNull(type, "service type cannot be null"));
    if (entry == null && create) {
      entry = new Entry<>(type);
      this.entries.put(type, entry);
    } 
    return entry;
  }
  
  public <T> DefaultServiceLocator setService(Class<T> type, Class<? extends T> impl) {
    getEntry(type, true).setService(impl);
    return this;
  }
  
  public <T> DefaultServiceLocator addService(Class<T> type, Class<? extends T> impl) {
    getEntry(type, true).addService(impl);
    return this;
  }
  
  public <T> DefaultServiceLocator setServices(Class<T> type, T... services) {
    getEntry(type, true).setServices(services);
    return this;
  }
  
  public <T> T getService(Class<T> type) {
    Entry<T> entry = getEntry(type, false);
    return (entry != null) ? entry.getInstance() : null;
  }
  
  public <T> List<T> getServices(Class<T> type) {
    Entry<T> entry = getEntry(type, false);
    return (entry != null) ? entry.getInstances() : null;
  }
  
  private void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
    if (this.errorHandler != null)
      this.errorHandler.serviceCreationFailed(type, impl, exception); 
  }
  
  public void setErrorHandler(ErrorHandler errorHandler) {
    this.errorHandler = errorHandler;
  }
  
  public static abstract class ErrorHandler {
    public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {}
  }
}
