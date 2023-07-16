package org.eclipse.sisu.space;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.binder.AnnotatedBindingBuilder;
import com.google.inject.binder.ScopedBindingBuilder;
import com.google.inject.matcher.Matcher;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import java.lang.annotation.Annotation;
import java.lang.annotation.IncompleteAnnotationException;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.enterprise.inject.Typed;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import org.eclipse.sisu.EagerSingleton;
import org.eclipse.sisu.Mediator;
import org.eclipse.sisu.Typed;
import org.eclipse.sisu.inject.Legacy;
import org.eclipse.sisu.inject.TypeArguments;
import org.sonatype.inject.EagerSingleton;
import org.sonatype.inject.Mediator;

public final class QualifiedTypeBinder implements QualifiedTypeListener {
  private static final TypeLiteral<Object> OBJECT_TYPE_LITERAL;
  
  private static final boolean HAS_JSR299_TYPED;
  
  private final Binder rootBinder;
  
  private MediationListener mediationListener;
  
  private Object currentSource;
  
  private Binder binder;
  
  static {
    boolean hasJsr299Typed;
    try {
      hasJsr299Typed = Typed.class.isAnnotation();
    } catch (LinkageError linkageError) {
      hasJsr299Typed = false;
    } 
    HAS_JSR299_TYPED = hasJsr299Typed;
    OBJECT_TYPE_LITERAL = TypeLiteral.get(Object.class);
  }
  
  public QualifiedTypeBinder(Binder binder) {
    this.rootBinder = binder;
    this.binder = binder;
  }
  
  public void hear(Class<?> qualifiedType, Object source) {
    if (this.currentSource != source)
      if (source != null) {
        this.binder = this.rootBinder.withSource(source);
        this.currentSource = source;
      } else {
        this.binder = this.rootBinder;
        this.currentSource = null;
      }  
    if (!TypeArguments.isConcrete(qualifiedType))
      return; 
    if (Module.class.isAssignableFrom(qualifiedType)) {
      installModule((Class)qualifiedType);
    } else if (Mediator.class.isAssignableFrom(qualifiedType)) {
      registerMediator((Class)qualifiedType);
    } else if (Mediator.class.isAssignableFrom(qualifiedType)) {
      registerLegacyMediator((Class)qualifiedType);
    } else if (Provider.class.isAssignableFrom(qualifiedType)) {
      bindProviderType(qualifiedType);
    } else {
      bindQualifiedType(qualifiedType);
    } 
  }
  
  private void installModule(Class<Module> moduleType) {
    Module module = newInstance(moduleType);
    if (module != null)
      this.binder.install(module); 
  }
  
  private void registerMediator(Class<Mediator> mediatorType) {
    TypeLiteral[] args = (TypeLiteral[])resolveTypeArguments(mediatorType, Mediator.class);
    if (args.length != 3) {
      this.binder.addError(mediatorType + " has wrong number of type arguments", new Object[0]);
    } else {
      Mediator mediator = newInstance(mediatorType);
      if (mediator != null)
        mediate(watchedKey(args[1], args[0].getRawType()), mediator, args[2].getRawType()); 
    } 
  }
  
  private void registerLegacyMediator(Class<Mediator> mediatorType) {
    TypeLiteral[] args = (TypeLiteral[])resolveTypeArguments(mediatorType, Mediator.class);
    if (args.length != 3) {
      this.binder.addError(mediatorType + " has wrong number of type arguments", new Object[0]);
    } else {
      Mediator mediator = Legacy.adapt(newInstance(mediatorType));
      if (mediator != null)
        mediate(watchedKey(args[1], args[0].getRawType()), mediator, args[2].getRawType()); 
    } 
  }
  
  private void mediate(Key watchedKey, Mediator mediator, Class watcherType) {
    if (this.mediationListener == null) {
      this.mediationListener = new MediationListener(this.binder);
      this.binder.bindListener((Matcher)this.mediationListener, this.mediationListener);
    } 
    this.mediationListener.mediate(watchedKey, mediator, watcherType);
  }
  
  private void bindProviderType(Class<?> providerType) {
    TypeLiteral[] args = (TypeLiteral[])resolveTypeArguments(providerType, Provider.class);
    if (args.length != 1) {
      this.binder.addError(providerType + " has wrong number of type arguments", new Object[0]);
    } else {
      this.binder.bind(providerType).in(Scopes.SINGLETON);
      Named bindingName = getBindingName(providerType);
      Class[] types = getBindingTypes(providerType);
      Key<?> key = getBindingKey(args[0], (Annotation)bindingName);
      ScopedBindingBuilder sbb = this.binder.bind(key).toProvider(providerType);
      if (isEagerSingleton(providerType)) {
        sbb.asEagerSingleton();
      } else if (isSingleton(providerType)) {
        sbb.in(Scopes.SINGLETON);
      } 
      if (types != null) {
        byte b;
        int i;
        Class[] arrayOfClass;
        for (i = (arrayOfClass = types).length, b = 0; b < i; ) {
          Class bindingType = arrayOfClass[b];
          this.binder.bind(key.ofType(bindingType)).to(key);
          b++;
        } 
      } 
    } 
  }
  
  private void bindQualifiedType(Class<?> qualifiedType) {
    AnnotatedBindingBuilder annotatedBindingBuilder = this.binder.bind(qualifiedType);
    if (isEagerSingleton(qualifiedType))
      annotatedBindingBuilder.asEagerSingleton(); 
    Named bindingName = getBindingName(qualifiedType);
    Class[] types = getBindingTypes(qualifiedType);
    if (types != null) {
      Key key = getBindingKey(OBJECT_TYPE_LITERAL, (Annotation)bindingName);
      byte b;
      int i;
      Class[] arrayOfClass;
      for (i = (arrayOfClass = types).length, b = 0; b < i; ) {
        Class bindingType = arrayOfClass[b];
        this.binder.bind(key.ofType(bindingType)).to(qualifiedType);
        b++;
      } 
    } else {
      this.binder.bind(WildcardKey.get(qualifiedType, (Annotation)bindingName)).to(qualifiedType);
    } 
  }
  
  private <T> T newInstance(Class<T> type) {
    try {
      final Constructor<T> ctor = type.getDeclaredConstructor(new Class[0]);
      if (!ctor.isAccessible())
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
              public Void run() {
                ctor.setAccessible(true);
                return null;
              }
            }); 
      return ctor.newInstance(new Object[0]);
    } catch (Exception e) {
      Throwable cause = (e instanceof java.lang.reflect.InvocationTargetException) ? e.getCause() : e;
      this.binder.addError("Error creating instance of: " + type + " reason: " + cause, new Object[0]);
      return null;
    } catch (LinkageError e) {
      this.binder.addError("Error creating instance of: " + type + " reason: " + e, new Object[0]);
      return null;
    } 
  }
  
  private static TypeLiteral<?>[] resolveTypeArguments(Class<?> type, Class<?> superType) {
    return (TypeLiteral<?>[])TypeArguments.get(TypeLiteral.get(type).getSupertype(superType));
  }
  
  private static <T> Key<T> getBindingKey(TypeLiteral<T> bindingType, Annotation qualifier) {
    return (qualifier != null) ? Key.get(bindingType, qualifier) : Key.get(bindingType);
  }
  
  private static Named getBindingName(Class<?> qualifiedType) {
    Named jsr330 = qualifiedType.<Named>getAnnotation(Named.class);
    if (jsr330 != null) {
      try {
        String name = jsr330.value();
        if (name.length() > 0)
          return "default".equals(name) ? null : Names.named(name); 
      } catch (IncompleteAnnotationException incompleteAnnotationException) {}
    } else {
      Named guice = qualifiedType.<Named>getAnnotation(Named.class);
      if (guice != null) {
        String name = guice.value();
        if (name.length() > 0)
          return "default".equals(name) ? null : guice; 
      } 
    } 
    if (qualifiedType.getSimpleName().startsWith("Default"))
      return null; 
    return Names.named(qualifiedType.getName());
  }
  
  private static Class<?>[] getBindingTypes(Class<?> clazz) {
    for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
      if (HAS_JSR299_TYPED) {
        Typed typed1 = c.<Typed>getAnnotation(Typed.class);
        if (typed1 != null)
          return ((typed1.value()).length > 0) ? typed1.value() : c.getInterfaces(); 
      } 
      Typed typed = c.<Typed>getAnnotation(Typed.class);
      if (typed != null)
        return ((typed.value()).length > 0) ? typed.value() : c.getInterfaces(); 
    } 
    return null;
  }
  
  private static boolean isSingleton(Class<?> type) {
    return !(!type.isAnnotationPresent((Class)Singleton.class) && 
      !type.isAnnotationPresent((Class)Singleton.class));
  }
  
  private static boolean isEagerSingleton(Class<?> type) {
    return !(!type.isAnnotationPresent((Class)EagerSingleton.class) && 
      !type.isAnnotationPresent((Class)EagerSingleton.class));
  }
  
  private static <T> Key<T> watchedKey(TypeLiteral<T> type, Class qualifierType) {
    return qualifierType.isAnnotation() ? Key.get(type, qualifierType) : Key.get(type);
  }
}
