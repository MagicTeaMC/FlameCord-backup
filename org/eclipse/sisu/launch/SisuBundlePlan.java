package org.eclipse.sisu.launch;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import org.eclipse.sisu.inject.BindingPublisher;
import org.eclipse.sisu.inject.InjectorBindings;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.osgi.framework.Bundle;

public class SisuBundlePlan implements BundlePlan {
  protected final MutableBeanLocator locator;
  
  public SisuBundlePlan(MutableBeanLocator locator) {
    this.locator = locator;
  }
  
  public BindingPublisher prepare(Bundle bundle) {
    return appliesTo(bundle) ? (BindingPublisher)new InjectorBindings(inject(compose(bundle))) : null;
  }
  
  protected boolean appliesTo(Bundle bundle) {
    if (bundle.getHeaders().get("Bundle-Blueprint") != null)
      return false; 
    String imports = (String)bundle.getHeaders().get("Import-Package");
    return (imports != null && (imports.contains("javax.inject") || imports.contains("com.google.inject")));
  }
  
  protected Injector inject(Module module) {
    return Guice.createInjector(new Module[] { module });
  }
  
  protected Module compose(Bundle bundle) {
    return new BundleModule(bundle, this.locator);
  }
}
