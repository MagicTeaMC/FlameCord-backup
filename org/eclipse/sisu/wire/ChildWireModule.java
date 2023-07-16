package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.Elements;
import java.util.Arrays;
import org.eclipse.sisu.inject.DefaultBeanLocator;

public final class ChildWireModule implements Module {
  private final Injector parent;
  
  private final Iterable<Module> modules;
  
  private WireModule.Strategy strategy = WireModule.Strategy.DEFAULT;
  
  public ChildWireModule(Injector parent, Module... modules) {
    this(parent, Arrays.asList(modules));
  }
  
  public ChildWireModule(Injector parent, Iterable<Module> modules) {
    this.modules = modules;
    this.parent = parent;
  }
  
  public Module with(WireModule.Strategy _strategy) {
    this.strategy = _strategy;
    return this;
  }
  
  public void configure(Binder binder) {
    binder.requestStaticInjection(new Class[] { DefaultBeanLocator.class });
    ElementAnalyzer analyzer = new ElementAnalyzer(binder);
    for (Injector i = this.parent; i != null; i = i.getParent())
      analyzer.ignoreKeys(i.getAllBindings().keySet()); 
    for (Element e : Elements.getElements(this.modules))
      e.acceptVisitor((ElementVisitor)analyzer); 
    analyzer.apply(this.strategy);
  }
}
