package org.eclipse.sisu.launch;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.space.ClassSpace;
import org.eclipse.sisu.space.ClassVisitor;
import org.eclipse.sisu.space.IndexedClassFinder;
import org.eclipse.sisu.space.SpaceModule;
import org.eclipse.sisu.space.SpaceVisitor;
import org.eclipse.sisu.wire.WireModule;
import org.eclipse.sisu.wire.Wiring;

public final class SisuExtensions implements SpaceModule.Strategy, WireModule.Strategy {
  private final ClassSpace space;
  
  private final boolean global;
  
  private SisuExtensions(ClassSpace space, boolean global) {
    this.space = space;
    this.global = global;
  }
  
  public static SisuExtensions local(ClassSpace space) {
    return new SisuExtensions(space, false);
  }
  
  public static SisuExtensions global(ClassSpace space) {
    return new SisuExtensions(space, true);
  }
  
  public void install(Binder binder) {
    install(binder, null, null);
  }
  
  public <C> void install(Binder binder, Class<C> contextType, C context) {
    for (Module m : create(Module.class, contextType, context))
      binder.install(m); 
  }
  
  public Wiring wiring(Binder binder) {
    final Wiring defaultWiring = WireModule.Strategy.DEFAULT.wiring(binder);
    final List<Wiring> customWiring = create(Wiring.class, Binder.class, binder);
    return customWiring.isEmpty() ? defaultWiring : new Wiring() {
        public boolean wire(Key<?> key) {
          for (Wiring w : customWiring) {
            if (w.wire(key))
              return true; 
          } 
          return defaultWiring.wire(key);
        }
      };
  }
  
  public SpaceVisitor visitor(Binder binder) {
    final SpaceVisitor defaultVisitor = SpaceModule.Strategy.DEFAULT.visitor(binder);
    final List<SpaceVisitor> customVisitors = create(SpaceVisitor.class, Binder.class, binder);
    return customVisitors.isEmpty() ? defaultVisitor : new SpaceVisitor() {
        public void enterSpace(ClassSpace _space) {
          for (SpaceVisitor v : customVisitors)
            v.enterSpace(_space); 
          defaultVisitor.enterSpace(_space);
        }
        
        public ClassVisitor visitClass(URL url) {
          for (SpaceVisitor v : customVisitors) {
            ClassVisitor cv = v.visitClass(url);
            if (cv != null)
              return cv; 
          } 
          return defaultVisitor.visitClass(url);
        }
        
        public void leaveSpace() {
          for (SpaceVisitor v : customVisitors)
            v.leaveSpace(); 
          defaultVisitor.leaveSpace();
        }
      };
  }
  
  public <T> List<T> create(Class<T> spi) {
    return create(spi, null, null);
  }
  
  public <T, C> List<T> create(Class<T> spi, Class<C> contextType, C context) {
    List<T> extensions = new ArrayList<T>();
    for (Class<? extends T> impl : load(spi)) {
      try {
        T instance = null;
        if (contextType != null)
          try {
            instance = impl.getConstructor(new Class[] { contextType }).newInstance(new Object[] { context });
          } catch (NoSuchMethodException noSuchMethodException) {} 
        extensions.add((instance != null) ? instance : impl.newInstance());
      } catch (Exception e) {
        Throwable cause = (e instanceof java.lang.reflect.InvocationTargetException) ? e.getCause() : e;
        Logs.trace("Problem creating: {}", impl, cause);
      } catch (LinkageError e) {
        Logs.trace("Problem creating: {}", impl, e);
      } 
    } 
    return extensions;
  }
  
  public <T> List<Class<? extends T>> load(Class<T> spi) {
    String index = "META-INF/services/" + spi.getName();
    List<Class<? extends T>> extensionTypes = new ArrayList<Class<? extends T>>();
    for (String name : (new IndexedClassFinder(index, this.global)).indexedNames(this.space)) {
      try {
        extensionTypes.add(this.space.loadClass(name).asSubclass(spi));
      } catch (Exception e) {
        Logs.trace("Problem loading: {}", name, e);
      } catch (LinkageError e) {
        Logs.trace("Problem loading: {}", name, e);
      } 
    } 
    return extensionTypes;
  }
}
