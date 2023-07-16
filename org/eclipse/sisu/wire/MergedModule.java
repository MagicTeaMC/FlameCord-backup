package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.Elements;
import java.util.Arrays;

public final class MergedModule implements Module {
  private final Iterable<Module> modules;
  
  public MergedModule(Module... modules) {
    this.modules = Arrays.asList(modules);
  }
  
  public MergedModule(Iterable<Module> modules) {
    this.modules = modules;
  }
  
  public void configure(Binder binder) {
    ElementMerger merger = new ElementMerger(binder);
    for (Element e : Elements.getElements(this.modules))
      e.acceptVisitor((ElementVisitor)merger); 
  }
}
