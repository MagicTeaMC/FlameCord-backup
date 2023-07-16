package org.eclipse.sisu.launch;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provides;
import java.util.Collections;
import java.util.Map;
import javax.inject.Inject;
import org.eclipse.sisu.Parameters;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.space.BeanScanning;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.URLClassSpace;
import org.eclipse.sisu.wire.ParameterKeys;
import org.eclipse.sisu.wire.WireModule;

public final class Main implements Module {
  private final Map<?, ?> properties;
  
  private final String[] args;
  
  private Main(Map<?, ?> properties, String... args) {
    this.properties = Collections.unmodifiableMap(properties);
    this.args = args;
  }
  
  public static void main(String... args) {
    boot(System.getProperties(), args);
  }
  
  public static <T> T boot(Class<T> type, String... args) {
    return (T)boot(System.getProperties(), args).getInstance(type);
  }
  
  public static Injector boot(Map<?, ?> properties, String... args) {
    BeanScanning scanning = BeanScanning.select(properties);
    Module app = wire(scanning, new Module[] { new Main(properties, args) });
    Injector injector = Guice.createInjector(new Module[] { app });
    return injector;
  }
  
  public static Module wire(BeanScanning scanning, Module... bindings) {
    Module[] modules = new Module[bindings.length + 1];
    System.arraycopy(bindings, 0, modules, 0, bindings.length);
    ClassLoader tccl = Thread.currentThread().getContextClassLoader();
    modules[bindings.length] = (Module)new SpaceModule((ClassSpace)new URLClassSpace(tccl), scanning);
    return (Module)new WireModule(modules);
  }
  
  public void configure(Binder binder) {
    binder.bind(ParameterKeys.PROPERTIES).toInstance(this.properties);
    binder.bind(ShutdownThread.class).asEagerSingleton();
  }
  
  @Provides
  @Parameters
  String[] parameters() {
    return (String[])this.args.clone();
  }
  
  static final class ShutdownThread extends Thread {
    private final MutableBeanLocator locator;
    
    @Inject
    ShutdownThread(MutableBeanLocator locator) {
      this.locator = locator;
      Runtime.getRuntime().addShutdownHook(this);
    }
    
    public void run() {
      this.locator.clear();
    }
  }
}
