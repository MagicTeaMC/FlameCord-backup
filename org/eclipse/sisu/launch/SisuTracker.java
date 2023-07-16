package org.eclipse.sisu.launch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import org.eclipse.sisu.inject.BindingPublisher;
import org.eclipse.sisu.inject.InjectorBindings;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.inject.Weak;
import org.eclipse.sisu.space.BundleClassSpace;
import org.eclipse.sisu.space.ClassSpace;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.BundleTracker;

public class SisuTracker extends BundleTracker<Object> implements BundlePlan {
  private static final Object PLACEHOLDER;
  
  private static final Set<String> SUPPORT_BUNDLE_NAMES;
  
  private static final Map<Long, Object> bundlePublishers;
  
  protected final int stateMask;
  
  protected final MutableBeanLocator locator;
  
  protected final List<BundlePlan> plans;
  
  static {
    Set<String> supportBundleNames = new HashSet<String>();
    Class[] supportTypes = { Inject.class, Guice.class, SisuExtender.class };
    byte b;
    int i;
    Class[] arrayOfClass1;
    for (i = (arrayOfClass1 = supportTypes).length, b = 0; b < i; ) {
      Class<?> type = arrayOfClass1[b];
      Bundle bundle = FrameworkUtil.getBundle(type);
      if (bundle != null)
        supportBundleNames.add(bundle.getSymbolicName()); 
      b++;
    } 
    SUPPORT_BUNDLE_NAMES = supportBundleNames;
    PLACEHOLDER = new Object();
    bundlePublishers = 
      Collections.synchronizedMap(Weak.values());
  }
  
  public SisuTracker(BundleContext context, int stateMask, MutableBeanLocator locator) {
    super(context, stateMask, null);
    this.stateMask = stateMask;
    this.locator = locator;
    this.plans = discoverPlans();
  }
  
  public final void open() {
    super.open();
    purgeBundles();
  }
  
  public final Object addingBundle(Bundle bundle, BundleEvent event) {
    Long bundleId = Long.valueOf(bundle.getBundleId());
    if (!bundlePublishers.containsKey(bundleId)) {
      bundlePublishers.put(bundleId, PLACEHOLDER);
      BindingPublisher publisher = prepare(bundle);
      if (publisher != null) {
        addPublisher(bundleId, publisher);
      } else {
        bundlePublishers.remove(bundleId);
      } 
    } 
    return bundle;
  }
  
  public final void removedBundle(Bundle bundle, BundleEvent event, Object object) {
    if (evictBundle(bundle))
      removePublisher(Long.valueOf(bundle.getBundleId())); 
  }
  
  public final void purgeBundles() {
    for (Iterator<?> iterator = (new ArrayList(bundlePublishers.keySet())).iterator(); iterator.hasNext(); ) {
      long bundleId = ((Long)iterator.next()).longValue();
      Bundle bundle = this.context.getBundle(bundleId);
      if (bundle == null || evictBundle(bundle))
        removePublisher(Long.valueOf(bundleId)); 
    } 
  }
  
  public BindingPublisher prepare(Bundle bundle) {
    if (SUPPORT_BUNDLE_NAMES.contains(bundle.getSymbolicName()))
      return null; 
    if (bundle.getHeaders().get("Fragment-Host") != null)
      return null; 
    BindingPublisher publisher = null;
    for (int i = this.plans.size() - 1; i >= 0 && publisher == null; i--)
      publisher = ((BundlePlan)this.plans.get(i)).prepare(bundle); 
    return publisher;
  }
  
  protected List<BundlePlan> discoverPlans() {
    List<BundlePlan> localPlans = new ArrayList<BundlePlan>();
    localPlans.add(new SisuBundlePlan(this.locator));
    SisuExtensions extensions = SisuExtensions.local((ClassSpace)new BundleClassSpace(this.context.getBundle()));
    localPlans.addAll(extensions.create(BundlePlan.class, MutableBeanLocator.class, this.locator));
    return localPlans;
  }
  
  protected boolean evictBundle(Bundle bundle) {
    return ((bundle.getState() & this.stateMask) == 0);
  }
  
  private void addPublisher(Long bundleId, BindingPublisher publisher) {
    if (this.locator.add(publisher)) {
      bundlePublishers.put(bundleId, publisher);
    } else if (publisher instanceof InjectorBindings) {
      bundlePublishers.put(bundleId, ((InjectorBindings)publisher).getInjector());
    } 
  }
  
  private void removePublisher(Long bundleId) {
    Object publisher = bundlePublishers.remove(bundleId);
    if (publisher instanceof BindingPublisher) {
      this.locator.remove((BindingPublisher)publisher);
    } else if (publisher instanceof Injector) {
      this.locator.remove((BindingPublisher)new InjectorBindings((Injector)publisher, null));
    } 
  }
}
