package org.eclipse.sisu.launch;

import java.util.Collections;
import java.util.Map;
import org.eclipse.sisu.inject.BindingPublisher;
import org.eclipse.sisu.inject.DefaultBeanLocator;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.inject.Weak;
import org.eclipse.sisu.osgi.ServiceBindings;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class SisuExtender implements BundleActivator {
  private static final Map<Long, MutableBeanLocator> locators = Collections.synchronizedMap(Weak.values());
  
  protected SisuTracker tracker;
  
  public void start(BundleContext context) {
    this.tracker = createTracker(context);
    this.tracker.open();
  }
  
  public void stop(BundleContext context) {
    this.tracker.close();
    this.tracker = null;
  }
  
  protected int bundleStateMask() {
    return 40;
  }
  
  protected SisuTracker createTracker(BundleContext context) {
    return new SisuTracker(context, bundleStateMask(), findLocator(context));
  }
  
  protected MutableBeanLocator createLocator(BundleContext context) {
    DefaultBeanLocator defaultBeanLocator = new DefaultBeanLocator();
    defaultBeanLocator.add((BindingPublisher)new ServiceBindings(context));
    return (MutableBeanLocator)defaultBeanLocator;
  }
  
  protected final MutableBeanLocator findLocator(BundleContext context) {
    Long extenderId = Long.valueOf(context.getBundle().getBundleId());
    MutableBeanLocator locator = locators.get(extenderId);
    if (locator == null)
      locators.put(extenderId, locator = createLocator(context)); 
    return locator;
  }
}
