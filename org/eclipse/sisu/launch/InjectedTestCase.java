package org.eclipse.sisu.launch;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.name.Names;
import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import javax.inject.Inject;
import junit.framework.TestCase;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.ParameterKeys;
import org.eclipse.sisu.wire.WireModule;

public abstract class InjectedTestCase extends TestCase implements Module {
  private String basedir;
  
  @Inject
  private MutableBeanLocator locator;
  
  protected void setUp() throws Exception {
    Guice.createInjector(
        new Module[] { (Module)new WireModule(new Module[] { new SetUpModule(), (Module)spaceModule() }) });
  }
  
  protected void tearDown() throws Exception {
    this.locator.clear();
  }
  
  final class SetUpModule implements Module {
    public void configure(Binder binder) {
      binder.install(InjectedTestCase.this);
      Properties properties = new Properties();
      properties.put("basedir", InjectedTestCase.this.getBasedir());
      InjectedTestCase.this.configure(properties);
      binder.bind(ParameterKeys.PROPERTIES).toInstance(properties);
      binder.requestInjection(InjectedTestCase.this);
    }
  }
  
  public SpaceModule spaceModule() {
    return new SpaceModule(space(), scanning());
  }
  
  public ClassSpace space() {
    return (ClassSpace)new URLClassSpace(getClass().getClassLoader());
  }
  
  public BeanScanning scanning() {
    return BeanScanning.CACHE;
  }
  
  public void configure(Binder binder) {}
  
  public void configure(Properties properties) {}
  
  public final <T> T lookup(Class<T> type) {
    return lookup(Key.get(type));
  }
  
  public final <T> T lookup(Class<T> type, String name) {
    return lookup(type, (Annotation)Names.named(name));
  }
  
  public final <T> T lookup(Class<T> type, Class<? extends Annotation> qualifier) {
    return lookup(Key.get(type, qualifier));
  }
  
  public final <T> T lookup(Class<T> type, Annotation qualifier) {
    return lookup(Key.get(type, qualifier));
  }
  
  public final String getBasedir() {
    if (this.basedir == null)
      this.basedir = System.getProperty("basedir", (new File("")).getAbsolutePath()); 
    return this.basedir;
  }
  
  private <T> T lookup(Key<T> key) {
    Iterator<? extends Map.Entry<Annotation, T>> i = this.locator.locate(key).iterator();
    return i.hasNext() ? (T)((Map.Entry)i.next()).getValue() : null;
  }
}
