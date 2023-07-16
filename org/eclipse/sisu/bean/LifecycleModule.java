package org.eclipse.sisu.bean;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

public final class LifecycleModule implements Module {
  private final Matcher<TypeLiteral<?>> matcher = (Matcher<TypeLiteral<?>>)new AbstractMatcher<TypeLiteral<?>>() {
      public boolean matches(TypeLiteral<?> type) {
        return LifecycleModule.this.manager.manage(type.getRawType());
      }
    };
  
  private final TypeListener typeListener = new TypeListener() {
      private final InjectionListener<Object> listener = new InjectionListener<Object>() {
          public void afterInjection(Object bean) {
            (LifecycleModule.null.access$0(LifecycleModule.null.this)).manager.manage(bean);
          }
        };
      
      public <B> void hear(TypeLiteral<B> type, TypeEncounter<B> encounter) {
        encounter.register(this.listener);
      }
    };
  
  final BeanManager manager;
  
  public LifecycleModule() {
    this(new LifecycleManager());
  }
  
  public LifecycleModule(BeanManager manager) {
    this.manager = manager;
  }
  
  public void configure(Binder binder) {
    binder.bind(BeanManager.class).toInstance(this.manager);
    binder.bindListener(this.matcher, this.typeListener);
  }
}
