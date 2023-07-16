package org.eclipse.sisu.wire;

import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.UntargettedBinding;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.inject.TypeArguments;

final class DependencyVerifier extends DefaultBindingTargetVisitor<Object, Boolean> {
  public Boolean visit(UntargettedBinding<?> binding) {
    return verifyImplementation(binding.getKey().getTypeLiteral());
  }
  
  public Boolean visit(LinkedKeyBinding<?> binding) {
    Key<?> linkedKey = binding.getLinkedKey();
    if (linkedKey.getAnnotationType() == null)
      return verifyImplementation(linkedKey.getTypeLiteral()); 
    return Boolean.TRUE;
  }
  
  public Boolean visitOther(Binding<?> binding) {
    return Boolean.TRUE;
  }
  
  private static Boolean verifyImplementation(TypeLiteral<?> type) {
    if (TypeArguments.isConcrete(type) && !type.toString().startsWith("java"))
      try {
        InjectionPoint.forInstanceMethodsAndFields(type);
        InjectionPoint.forConstructorOf(type);
      } catch (RuntimeException e) {
        Logs.trace("Potential problem: {}", type, e);
        return Boolean.FALSE;
      } catch (LinkageError e) {
        Logs.trace("Potential problem: {}", type, e);
        return Boolean.FALSE;
      }  
    return Boolean.TRUE;
  }
}
