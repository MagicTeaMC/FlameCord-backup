package org.eclipse.sisu.wire;

import com.google.inject.AbstractModule;
import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.ImplementedBy;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.MembersInjector;
import com.google.inject.Module;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.Scope;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.Dependency;
import com.google.inject.spi.HasDependencies;
import com.google.inject.spi.InjectionPoint;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.LinkedKeyBinding;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderKeyBinding;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.spi.StaticInjectionRequest;
import com.google.inject.spi.UntargettedBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import javax.inject.Provider;
import org.eclipse.sisu.inject.DeferredProvider;
import org.eclipse.sisu.inject.Guice4;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.inject.TypeArguments;

final class DependencyAnalyzer extends DefaultBindingTargetVisitor<Object, Boolean> {
  private static final Set<Class<?>> RESTRICTED_CLASSES = new HashSet<Class<?>>(Arrays.asList(new Class[] { 
          AbstractModule.class, Binder.class, Binding.class, 
          Injector.class, Key.class, Logger.class, 
          MembersInjector.class, Module.class, Provider.class, 
          Scope.class, 
          TypeLiteral.class }));
  
  private final Map<TypeLiteral<?>, Boolean> analyzedTypes = new HashMap<TypeLiteral<?>, Boolean>();
  
  private final Set<Key<?>> requiredKeys = new HashSet<Key<?>>();
  
  DependencyAnalyzer() {
    this.requiredKeys.add(ParameterKeys.PROPERTIES);
  }
  
  public Set<Key<?>> findMissingKeys(Set<Key<?>> localKeys) {
    Set<Key<?>> missingKeys = new HashSet<Key<?>>();
    while (this.requiredKeys.size() > 0) {
      List<Key<?>> candidateKeys = new ArrayList<Key<?>>(this.requiredKeys);
      this.requiredKeys.clear();
      for (Key<?> key : candidateKeys) {
        if (!localKeys.contains(key) && missingKeys.add(key))
          analyzeImplicitBindings(key.getTypeLiteral()); 
      } 
    } 
    return missingKeys;
  }
  
  public Boolean visit(UntargettedBinding<?> binding) {
    return analyzeImplementation(binding.getKey().getTypeLiteral(), true);
  }
  
  public Boolean visit(LinkedKeyBinding<?> binding) {
    Key<?> linkedKey = binding.getLinkedKey();
    if (linkedKey.getAnnotationType() == null)
      return analyzeImplementation(linkedKey.getTypeLiteral(), true); 
    return Boolean.TRUE;
  }
  
  public Boolean visit(ProviderKeyBinding<?> binding) {
    Key<?> providerKey = binding.getProviderKey();
    if (providerKey.getAnnotationType() == null)
      return analyzeImplementation(providerKey.getTypeLiteral(), true); 
    return Boolean.TRUE;
  }
  
  public Boolean visit(ProviderInstanceBinding<?> binding) {
    Provider<?> provider = Guice4.getProviderInstance(binding);
    if (provider instanceof DeferredProvider) {
      try {
        Class<?> clazz = ((DeferredProvider)provider).getImplementationClass().load();
        analyzeImplementation(TypeLiteral.get(clazz), false);
      } catch (TypeNotPresentException typeNotPresentException) {}
      return Boolean.TRUE;
    } 
    return Boolean.valueOf(analyzeDependencies(binding.getDependencies()));
  }
  
  public Boolean visitOther(Binding<?> binding) {
    if (binding instanceof HasDependencies)
      return Boolean.valueOf(analyzeDependencies(((HasDependencies)binding).getDependencies())); 
    return Boolean.TRUE;
  }
  
  public <T> Boolean visit(ProviderLookup<T> lookup) {
    requireKey(lookup.getKey());
    return Boolean.TRUE;
  }
  
  public Boolean visit(StaticInjectionRequest request) {
    return Boolean.valueOf(analyzeInjectionPoints(request.getInjectionPoints()));
  }
  
  public Boolean visit(InjectionRequest<?> request) {
    return Boolean.valueOf(analyzeInjectionPoints(request.getInjectionPoints()));
  }
  
  private void requireKey(Key<?> key) {
    if (!this.requiredKeys.contains(key)) {
      Class<?> clazz = key.getTypeLiteral().getRawType();
      if (Provider.class == clazz || Provider.class == clazz) {
        requireKey(key.ofType(TypeArguments.get(key.getTypeLiteral(), 0)));
      } else if (!RESTRICTED_CLASSES.contains(clazz)) {
        this.requiredKeys.add(key);
      } 
    } 
  }
  
  private Boolean analyzeImplementation(TypeLiteral<?> type, boolean reportErrors) {
    Boolean applyBinding = this.analyzedTypes.get(type);
    if (applyBinding == null) {
      applyBinding = Boolean.TRUE;
      if (TypeArguments.isConcrete(type) && !type.toString().startsWith("java"))
        try {
          boolean rhs = analyzeInjectionPoints(InjectionPoint.forInstanceMethodsAndFields(type));
          if (!analyzeDependencies(InjectionPoint.forConstructorOf(type).getDependencies()) || !rhs)
            applyBinding = Boolean.FALSE; 
        } catch (RuntimeException e) {
          if (reportErrors)
            Logs.trace("Potential problem: {}", type, e); 
          applyBinding = Boolean.FALSE;
        } catch (LinkageError e) {
          if (reportErrors)
            Logs.trace("Potential problem: {}", type, e); 
          applyBinding = Boolean.FALSE;
        }  
      this.analyzedTypes.put(type, applyBinding);
    } 
    return applyBinding;
  }
  
  private boolean analyzeInjectionPoints(Set<InjectionPoint> points) {
    boolean applyBinding = true;
    for (InjectionPoint p : points)
      applyBinding &= analyzeDependencies(p.getDependencies()); 
    return applyBinding;
  }
  
  private boolean analyzeDependencies(Collection<Dependency<?>> dependencies) {
    boolean applyBinding = true;
    for (Dependency<?> d : dependencies) {
      Key<?> key = d.getKey();
      if (key.hasAttributes() && "Assisted".equals(key.getAnnotationType().getSimpleName())) {
        applyBinding = false;
        continue;
      } 
      requireKey(key);
    } 
    return applyBinding;
  }
  
  private void analyzeImplicitBindings(TypeLiteral<?> type) {
    if (!this.analyzedTypes.containsKey(type)) {
      Class<?> clazz = type.getRawType();
      if (TypeArguments.isConcrete(clazz)) {
        analyzeImplementation(type, false);
      } else {
        this.analyzedTypes.put(type, Boolean.TRUE);
        ImplementedBy implementedBy = clazz.<ImplementedBy>getAnnotation(ImplementedBy.class);
        if (implementedBy != null) {
          requireKey(Key.get(implementedBy.value()));
        } else {
          ProvidedBy providedBy = clazz.<ProvidedBy>getAnnotation(ProvidedBy.class);
          if (providedBy != null)
            requireKey(Key.get(providedBy.value())); 
        } 
      } 
    } 
  }
}
