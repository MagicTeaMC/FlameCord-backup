package org.apache.logging.log4j.util;

import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.apache.logging.log4j.status.StatusLogger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

public class OsgiServiceLocator {
  private static final boolean OSGI_AVAILABLE = checkOsgiAvailable();
  
  private static boolean checkOsgiAvailable() {
    try {
      Class<?> clazz = Class.forName("org.osgi.framework.FrameworkUtil");
      return 
        
        (clazz.getMethod("getBundle", new Class[] { Class.class }).invoke(null, new Object[] { OsgiServiceLocator.class }) != null);
    } catch (ClassNotFoundException|NoSuchMethodException|LinkageError e) {
      return false;
    } catch (Throwable e) {
      LowLevelLogUtil.logException("Unknown error checking OSGI environment.", e);
      return false;
    } 
  }
  
  public static boolean isAvailable() {
    return OSGI_AVAILABLE;
  }
  
  public static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup) {
    return loadServices(serviceType, lookup, true);
  }
  
  public static <T> Stream<T> loadServices(Class<T> serviceType, MethodHandles.Lookup lookup, boolean verbose) {
    Bundle bundle = FrameworkUtil.getBundle(lookup.lookupClass());
    if (bundle != null) {
      BundleContext ctx = bundle.getBundleContext();
      try {
        return ctx.getServiceReferences(serviceType, null)
          .stream()
          .map(ctx::getService);
      } catch (Throwable e) {
        if (verbose)
          StatusLogger.getLogger().error("Unable to load OSGI services for service {}", serviceType, e); 
      } 
    } 
    return Stream.empty();
  }
}
