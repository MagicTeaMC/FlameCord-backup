package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.Elements;
import java.util.Arrays;

public final class WireModule implements Module {
  static final Module[] CONVERTERS = new Module[] { new FileTypeConverter(), new URLTypeConverter() };
  
  private final Iterable<Module> modules;
  
  private Strategy strategy = Strategy.DEFAULT;
  
  public WireModule(Module... modules) {
    this(Arrays.asList(modules));
  }
  
  public WireModule(Iterable<Module> modules) {
    this.modules = modules;
  }
  
  public Module with(Strategy _strategy) {
    this.strategy = _strategy;
    return this;
  }
  
  public void configure(Binder binder) {
    ElementAnalyzer analyzer = new ElementAnalyzer(binder);
    for (Element e : Elements.getElements(this.modules))
      e.acceptVisitor((ElementVisitor)analyzer); 
    analyzer.apply(this.strategy);
  }
  
  public static interface Strategy {
    public static final Strategy DEFAULT = new Strategy() {
        public Wiring wiring(Binder binder) {
          byte b;
          int i;
          Module[] arrayOfModule;
          for (i = (arrayOfModule = WireModule.CONVERTERS).length, b = 0; b < i; ) {
            Module m = arrayOfModule[b];
            m.configure(binder);
            b++;
          } 
          return new LocatorWiring(binder);
        }
      };
    
    Wiring wiring(Binder param1Binder);
  }
}
