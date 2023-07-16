package org.apache.logging.log4j.util;

import java.lang.invoke.CallSite;
import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class ServiceLoaderUtil {
  private static final int MAX_BROKEN_SERVICES = 8;
  
  public static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup) {
    return loadServices(serviceType, lookup, false);
  }
  
  public static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup, boolean useTccl) {
    return loadServices(serviceType, lookup, useTccl, true);
  }
  
  static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup, boolean useTccl, boolean verbose) {
    ClassLoader classLoader = lookup.lookupClass().getClassLoader();
    Stream<T> services = loadClassloaderServices(serviceType, lookup, classLoader, verbose);
    if (useTccl) {
      ClassLoader contextClassLoader = LoaderUtil.getThreadContextClassLoader();
      if (contextClassLoader != classLoader)
        services = Stream.concat(services, 
            loadClassloaderServices(serviceType, lookup, contextClassLoader, verbose)); 
    } 
    if (OsgiServiceLocator.isAvailable())
      services = Stream.concat(services, OsgiServiceLocator.loadServices(serviceType, lookup, verbose)); 
    Set<Class<?>> classes = new HashSet<>();
    return services.filter(service -> classes.add(service.getClass()));
  }
  
  static <T> Stream<T> loadClassloaderServices(Class<T> serviceType, MethodHandles.Lookup lookup, ClassLoader classLoader, boolean verbose) {
    return StreamSupport.stream(new ServiceLoaderSpliterator<>(serviceType, lookup, classLoader, verbose), false);
  }
  
  static <T> Iterable<T> callServiceLoader(MethodHandles.Lookup lookup, Class<T> serviceType, ClassLoader classLoader, boolean verbose) {
    try {
      ServiceLoader<T> serviceLoader;
      MethodHandle loadHandle = lookup.findStatic(ServiceLoader.class, "load", 
          MethodType.methodType(ServiceLoader.class, Class.class, new Class[] { ClassLoader.class }));
      CallSite callSite = LambdaMetafactory.metafactory(lookup, "run", 
          
          MethodType.methodType(PrivilegedAction.class, Class.class, new Class[] { ClassLoader.class }), MethodType.methodType(Object.class), loadHandle, 
          
          MethodType.methodType(ServiceLoader.class));
      PrivilegedAction<ServiceLoader<T>> action = callSite.getTarget().bindTo(serviceType).bindTo(classLoader).invoke();
      if (System.getSecurityManager() == null) {
        serviceLoader = action.run();
      } else {
        MethodHandle privilegedHandle = lookup.findStatic(AccessController.class, "doPrivileged", 
            MethodType.methodType(Object.class, PrivilegedAction.class));
        serviceLoader = privilegedHandle.invoke(action);
      } 
      return serviceLoader;
    } catch (Throwable e) {
      if (verbose)
        StatusLogger.getLogger().error("Unable to load services for service {}", serviceType, e); 
      return Collections.emptyList();
    } 
  }
  
  private static class ServiceLoaderSpliterator<S> implements Spliterator<S> {
    private final Iterator<S> serviceIterator;
    
    private final Logger logger;
    
    private final String serviceName;
    
    public ServiceLoaderSpliterator(Class<S> serviceType, MethodHandles.Lookup lookup, ClassLoader classLoader, boolean verbose) {
      this.serviceIterator = ServiceLoaderUtil.<S>callServiceLoader(lookup, serviceType, classLoader, verbose).iterator();
      this.logger = verbose ? (Logger)StatusLogger.getLogger() : null;
      this.serviceName = serviceType.toString();
    }
    
    public boolean tryAdvance(Consumer<? super S> action) {
      int i = 8;
      while (i-- > 0) {
        try {
          if (this.serviceIterator.hasNext()) {
            action.accept(this.serviceIterator.next());
            return true;
          } 
        } catch (ServiceConfigurationError|LinkageError e) {
          if (this.logger != null)
            this.logger.warn("Unable to load service class for service {}", this.serviceName, e); 
        } catch (Throwable e) {
          if (this.logger != null)
            this.logger.warn("Unable to load service class for service {}", this.serviceName, e); 
          throw e;
        } 
      } 
      return false;
    }
    
    public Spliterator<S> trySplit() {
      return null;
    }
    
    public long estimateSize() {
      return Long.MAX_VALUE;
    }
    
    public int characteristics() {
      return 1280;
    }
  }
}
