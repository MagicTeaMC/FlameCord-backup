package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.ImplementedBy;
import com.google.inject.Key;
import com.google.inject.ProvidedBy;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.spi.InjectionPoint;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Provider;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.Hidden;
import org.eclipse.sisu.inject.Legacy;
import org.eclipse.sisu.inject.Sources;
import org.eclipse.sisu.inject.TypeArguments;
import org.sonatype.inject.BeanEntry;

public final class LocatorWiring implements Wiring {
  private static final Hidden HIDDEN_WIRING = Sources.hide(LocatorWiring.class.getName());
  
  private final BeanProviders beanProviders;
  
  private final Binder binder;
  
  public LocatorWiring(Binder binder) {
    this.beanProviders = new BeanProviders(binder);
    this.binder = binder.withSource(HIDDEN_WIRING);
  }
  
  public boolean wire(Key<?> key) {
    Class<?> clazz = key.getTypeLiteral().getRawType();
    if (Map.class == clazz) {
      bindMapImport(key);
    } else if (List.class == clazz || Collection.class == clazz || Iterable.class == clazz) {
      bindListImport(key);
    } else if (Set.class == clazz) {
      bindSetImport(key);
    } else {
      bindBeanImport(key);
    } 
    return true;
  }
  
  private void bindMapImport(Key key) {
    TypeLiteral[] args = TypeArguments.get(key.getTypeLiteral());
    if (2 == args.length && key.getAnnotation() == null) {
      Class<String> qualifierType = args[0].getRawType();
      if (String.class == qualifierType) {
        this.binder.bind(key).toProvider(this.beanProviders.stringMapOf(args[1]));
      } else if (qualifierType.isAnnotation()) {
        this.binder.bind(key).toProvider(this.beanProviders.mapOf(Key.get(args[1], qualifierType)));
      } else if (Annotation.class == qualifierType) {
        this.binder.bind(key).toProvider(this.beanProviders.mapOf(Key.get(args[1])));
      } 
    } 
  }
  
  private void bindListImport(Key key) {
    TypeLiteral[] args = TypeArguments.get(key.getTypeLiteral());
    if (1 == args.length && key.getAnnotation() == null) {
      TypeLiteral<?> elementType = args[0];
      if (BeanEntry.class == elementType.getRawType() || 
        BeanEntry.class == elementType.getRawType()) {
        Provider beanEntriesProvider = getBeanEntriesProvider(elementType);
        if (beanEntriesProvider != null)
          this.binder.bind(key).toProvider(beanEntriesProvider); 
      } else {
        this.binder.bind(key).toProvider(this.beanProviders.listOf(Key.get(elementType)));
      } 
    } 
  }
  
  private Provider getBeanEntriesProvider(TypeLiteral entryType) {
    TypeLiteral[] args = TypeArguments.get(entryType);
    if (2 == args.length) {
      Class qualifierType = args[0].getRawType();
      Key<?> key = qualifierType.isAnnotation() ? Key.get(args[1], qualifierType) : Key.get(args[1]);
      Provider<Iterable<? extends BeanEntry<Annotation, ?>>> beanEntries = this.beanProviders.beanEntriesOf(key);
      return (BeanEntry.class == entryType.getRawType()) ? beanEntries : 
        Legacy.adapt(beanEntries);
    } 
    return null;
  }
  
  private void bindSetImport(Key key) {
    TypeLiteral[] args = TypeArguments.get(key.getTypeLiteral());
    if (1 == args.length && key.getAnnotation() == null)
      this.binder.bind(key).toProvider(this.beanProviders.setOf(Key.get(args[0]))); 
  }
  
  private <T> void bindBeanImport(Key<T> key) {
    Annotation qualifier = key.getAnnotation();
    if (qualifier instanceof Named) {
      if (((Named)qualifier).value().length() == 0) {
        this.binder.bind(key).toProvider(this.beanProviders.firstOf(Key.get(key.getTypeLiteral(), Named.class)));
      } else {
        this.binder.bind(key).toProvider(this.beanProviders.placeholderOf(key));
      } 
    } else if (qualifier instanceof org.eclipse.sisu.Dynamic) {
      Provider<T> delegate = this.beanProviders.firstOf(Key.get(key.getTypeLiteral()));
      this.binder.bind(key).toInstance(GlueLoader.dynamicGlue(key.getTypeLiteral(), (Provider<T>)delegate));
    } else {
      this.binder.bind(key).toProvider(this.beanProviders.firstOf(key));
      if (key.getAnnotationType() == null)
        bindImplicitType(key.getTypeLiteral()); 
    } 
  }
  
  private void bindImplicitType(TypeLiteral type) {
    try {
      Class<?> clazz = type.getRawType();
      if (TypeArguments.isConcrete(clazz)) {
        Member ctor = InjectionPoint.forConstructorOf(type).getMember();
        this.binder.bind(TypeArguments.implicitKey(clazz)).toConstructor((Constructor)ctor);
      } else {
        ImplementedBy implementedBy = clazz.<ImplementedBy>getAnnotation(ImplementedBy.class);
        if (implementedBy != null) {
          this.binder.bind(TypeArguments.implicitKey(clazz)).to(implementedBy.value());
        } else {
          ProvidedBy providedBy = clazz.<ProvidedBy>getAnnotation(ProvidedBy.class);
          if (providedBy != null)
            this.binder.bind(TypeArguments.implicitKey(clazz)).toProvider(providedBy.value()); 
        } 
      } 
    } catch (RuntimeException runtimeException) {
    
    } catch (LinkageError linkageError) {}
  }
}
