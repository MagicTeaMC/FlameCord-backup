package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.sisu.inject.Logs;

final class ElementMerger extends DefaultElementVisitor<Void> {
  private final DependencyVerifier verifier = new DependencyVerifier();
  
  private final Set<Key<?>> localKeys = new HashSet<Key<?>>();
  
  private final Binder binder;
  
  ElementMerger(Binder binder) {
    this.binder = binder;
  }
  
  public <T> Void visit(Binding<T> binding) {
    Key<T> key = binding.getKey();
    if (!this.localKeys.contains(key))
      if (Boolean.TRUE.equals(binding.acceptTargetVisitor((BindingTargetVisitor)this.verifier))) {
        this.localKeys.add(key);
        binding.applyTo(this.binder);
      } else {
        Logs.trace("Discard binding: {}", binding, null);
      }  
    return null;
  }
  
  public Void visitOther(Element element) {
    element.applyTo(this.binder);
    return null;
  }
}
