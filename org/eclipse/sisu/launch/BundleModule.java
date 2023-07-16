package org.eclipse.sisu.launch;

import com.google.inject.Binder;
import com.google.inject.Module;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.BundleClassSpace;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.wire.ParameterKeys;
import org.eclipse.sisu.wire.WireModule;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class BundleModule implements Module {
  protected final BundleClassSpace space;
  
  protected final SisuExtensions extensions;
  
  protected final MutableBeanLocator locator;
  
  public BundleModule(Bundle bundle, MutableBeanLocator locator) {
    this.space = new BundleClassSpace(bundle);
    this.extensions = SisuExtensions.local((ClassSpace)this.space);
    this.locator = locator;
  }
  
  public void configure(Binder binder) {
    (new WireModule(modules())).with(this.extensions).configure(binder);
  }
  
  protected Map<?, ?> getProperties() {
    return System.getProperties();
  }
  
  protected List<Module> modules() {
    return Arrays.asList(new Module[] { extensionsModule(), contextModule(), spaceModule() });
  }
  
  protected Module extensionsModule() {
    return new Module() {
        public void configure(Binder binder) {
          BundleModule.this.extensions.install(binder, Bundle.class, BundleModule.this.space.getBundle());
        }
      };
  }
  
  protected Module contextModule() {
    return new Module() {
        public void configure(Binder binder) {
          binder.bind(MutableBeanLocator.class).toInstance(BundleModule.this.locator);
          Bundle bundle = BundleModule.this.space.getBundle();
          binder.bind(ParameterKeys.PROPERTIES).toInstance(BundleModule.this.getProperties());
          binder.bind(BundleContext.class).toInstance(bundle.getBundleContext());
        }
      };
  }
  
  protected Module spaceModule() {
    return (new SpaceModule((ClassSpace)this.space, BeanScanning.select(getProperties()))).with(this.extensions);
  }
}
