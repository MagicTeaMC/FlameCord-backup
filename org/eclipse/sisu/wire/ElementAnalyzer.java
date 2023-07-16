package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Binding;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.BindingTargetVisitor;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.ElementVisitor;
import com.google.inject.spi.Elements;
import com.google.inject.spi.InjectionRequest;
import com.google.inject.spi.InstanceBinding;
import com.google.inject.spi.PrivateElements;
import com.google.inject.spi.ProviderInstanceBinding;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.spi.RequireExplicitBindingsOption;
import com.google.inject.spi.StaticInjectionRequest;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.sisu.Parameters;
import org.eclipse.sisu.inject.BeanLocator;
import org.eclipse.sisu.inject.DefaultBeanLocator;
import org.eclipse.sisu.inject.DefaultRankingFunction;
import org.eclipse.sisu.inject.Guice4;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.inject.MutableBeanLocator;
import org.eclipse.sisu.inject.RankingFunction;
import org.eclipse.sisu.inject.TypeArguments;
import org.sonatype.inject.Parameters;

final class ElementAnalyzer extends DefaultElementVisitor<Void> {
  private static final Map<Key<?>, Key<?>> LEGACY_KEY_ALIASES;
  
  static {
    Map<Key<?>, Key<?>> aliases = new HashMap<Key<?>, Key<?>>();
    try {
      addLegacyKeyAlias(aliases, BeanLocator.class);
      addLegacyKeyAlias(aliases, MutableBeanLocator.class);
      addLegacyKeyAlias(aliases, RankingFunction.class);
    } catch (Exception exception) {
    
    } catch (LinkageError linkageError) {}
    LEGACY_KEY_ALIASES = aliases.isEmpty() ? null : aliases;
  }
  
  private static final List<Element> JIT_BINDINGS = Elements.getElements(new Module[] { new Module() {
          public void configure(Binder binder) {
            binder.bind(BeanLocator.class).to(MutableBeanLocator.class);
            binder.bind(MutableBeanLocator.class).to(DefaultBeanLocator.class);
            binder.bind(RankingFunction.class).to(DefaultRankingFunction.class);
            binder.bind(TypeConverterCache.class);
          }
        } });
  
  private final Set<Key<?>> localKeys = new HashSet<Key<?>>();
  
  private final DependencyAnalyzer analyzer = new DependencyAnalyzer();
  
  private final List<ElementAnalyzer> privateAnalyzers = new ArrayList<ElementAnalyzer>();
  
  private final List<Map<?, ?>> properties = new ArrayList<Map<?, ?>>();
  
  private final List<String> arguments = new ArrayList<String>();
  
  private final Binder binder;
  
  private boolean requireExplicitBindings;
  
  ElementAnalyzer(Binder binder) {
    this.binder = binder;
  }
  
  public void ignoreKeys(Set<Key<?>> keys) {
    this.localKeys.addAll(keys);
  }
  
  public void apply(WireModule.Strategy strategy) {
    if (this.requireExplicitBindings)
      makeJitBindingsExplicit(); 
    Set<Key<?>> missingKeys = this.analyzer.findMissingKeys(this.localKeys);
    Map<?, ?> mergedProperties = new MergedProperties(this.properties);
    Wiring wiring = strategy.wiring(this.binder);
    for (Key<?> key : missingKeys) {
      if (isParameters(key)) {
        wireParameters(key, mergedProperties);
        continue;
      } 
      if (!isRestricted(key))
        wiring.wire(key); 
    } 
    for (ElementAnalyzer privateAnalyzer : this.privateAnalyzers) {
      privateAnalyzer.ignoreKeys(this.localKeys);
      privateAnalyzer.ignoreKeys(missingKeys);
      privateAnalyzer.apply(strategy);
    } 
  }
  
  public <T> Void visit(Binding<T> binding) {
    Key<T> key = binding.getKey();
    if (!this.localKeys.contains(key))
      if (isParameters(key)) {
        mergeParameters(binding);
      } else if (Boolean.TRUE.equals(binding.acceptTargetVisitor((BindingTargetVisitor)this.analyzer))) {
        this.localKeys.add(key);
        binding.applyTo(this.binder);
        if (LEGACY_KEY_ALIASES != null) {
          Key<T> alias = (Key<T>)LEGACY_KEY_ALIASES.get(key);
          if (alias != null && this.localKeys.add(alias))
            this.binder.bind(alias).to(key); 
        } 
      } else {
        Logs.trace("Discard binding: {}", binding, null);
      }  
    return null;
  }
  
  public Void visit(PrivateElements elements) {
    PrivateBinder privateBinder = this.binder.withSource(elements.getSource()).newPrivateBinder();
    ElementAnalyzer privateAnalyzer = new ElementAnalyzer((Binder)privateBinder);
    this.privateAnalyzers.add(privateAnalyzer);
    privateAnalyzer.ignoreKeys(this.localKeys);
    for (Element e : elements.getElements())
      e.acceptVisitor((ElementVisitor)privateAnalyzer); 
    for (Key<?> k : (Iterable<Key<?>>)elements.getExposedKeys()) {
      if (privateAnalyzer.localKeys.contains(k) && this.localKeys.add(k))
        privateBinder.withSource(elements.getExposedSource(k)).expose(k); 
    } 
    return null;
  }
  
  public <T> Void visit(ProviderLookup<T> lookup) {
    this.analyzer.visit(lookup);
    lookup.applyTo(this.binder);
    return null;
  }
  
  public Void visit(StaticInjectionRequest request) {
    this.analyzer.visit(request);
    request.applyTo(this.binder);
    return null;
  }
  
  public Void visit(InjectionRequest<?> request) {
    this.analyzer.visit(request);
    request.applyTo(this.binder);
    return null;
  }
  
  public Void visit(RequireExplicitBindingsOption option) {
    this.requireExplicitBindings = true;
    option.applyTo(this.binder);
    return null;
  }
  
  public Void visitOther(Element element) {
    element.applyTo(this.binder);
    return null;
  }
  
  private void makeJitBindingsExplicit() {
    for (Element element : JIT_BINDINGS) {
      if (element instanceof Binding && this.localKeys.add(((Binding)element).getKey()))
        element.applyTo(this.binder); 
    } 
  }
  
  private void mergeParameters(Binding<?> binding) {
    Object parameters = null;
    if (binding instanceof InstanceBinding) {
      parameters = ((InstanceBinding)binding).getInstance();
    } else if (binding instanceof ProviderInstanceBinding) {
      parameters = Guice4.getProviderInstance((ProviderInstanceBinding)binding).get();
    } 
    if (parameters instanceof Map) {
      this.properties.add((Map<?, ?>)parameters);
    } else if (parameters instanceof String[]) {
      Collections.addAll(this.arguments, (String[])parameters);
    } else {
      Logs.warn("Ignoring incompatible @Parameters binding: {}", binding, null);
    } 
  }
  
  private void wireParameters(Key key, Map mergedProperties) {
    if (ParameterKeys.PROPERTIES.equals(key)) {
      this.binder.bind(key).toInstance(mergedProperties);
    } else {
      TypeLiteral<?> type = key.getTypeLiteral();
      Class<?> clazz = type.getRawType();
      if (Map.class == clazz) {
        TypeLiteral[] constraints = TypeArguments.get(type);
        if (constraints.length == 2 && String.class == constraints[1].getRawType()) {
          this.binder.bind(key).to(StringProperties.class);
        } else {
          this.binder.bind(key).to(ParameterKeys.PROPERTIES);
        } 
      } else if (String[].class == clazz) {
        this.binder.bind(key).toInstance(this.arguments.toArray(new String[this.arguments.size()]));
      } 
    } 
  }
  
  private static boolean isParameters(Key<?> key) {
    Class<? extends Annotation> qualifierType = key.getAnnotationType();
    return !(Parameters.class != qualifierType && Parameters.class != qualifierType);
  }
  
  private static boolean isRestricted(Key<?> key) {
    String name = key.getTypeLiteral().getRawType().getName();
    if (name.startsWith("org.eclipse.sisu.inject") || name.startsWith("org.sonatype.guice.bean.locators"))
      return !(!name.endsWith("BeanLocator") && !name.endsWith("RankingFunction")); 
    return "org.slf4j.Logger".equals(name);
  }
  
  private static void addLegacyKeyAlias(Map<Key<?>, Key<?>> aliases, Class<?> clazz) throws ClassNotFoundException {
    String legacyName = "org.sonatype.guice.bean.locators." + clazz.getSimpleName();
    Class<?> legacyType = ElementAnalyzer.class.getClassLoader().loadClass(legacyName);
    if (clazz.isAssignableFrom(legacyType))
      aliases.put(Key.get(legacyType), Key.get(clazz)); 
  }
}
