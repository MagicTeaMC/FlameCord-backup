package org.eclipse.sisu.space;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.spi.InjectionListener;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Provider;
import org.eclipse.sisu.Mediator;
import org.eclipse.sisu.inject.BeanLocator;

final class MediationListener extends AbstractMatcher<TypeLiteral<?>> implements TypeListener, InjectionListener<Object> {
  private final List<Mediation<?, ?, ?>> mediation;
  
  private final Provider<BeanLocator> locator;
  
  public MediationListener(Binder binder) {
    this.mediation = new ArrayList<Mediation<?, ?, ?>>();
    this.locator = (Provider<BeanLocator>)binder.getProvider(BeanLocator.class);
  }
  
  public void mediate(Key<?> key, Mediator<?, ?, ?> mediator, Class<?> watcherType) {
    this.mediation.add(new Mediation<Object, Object, Object>(key, mediator, watcherType));
  }
  
  public boolean matches(TypeLiteral<?> type) {
    for (Mediation<?, ?, ?> m : this.mediation) {
      if (m.watcherType.isAssignableFrom(type.getRawType()))
        return true; 
    } 
    return false;
  }
  
  public <T> void hear(TypeLiteral<T> type, TypeEncounter<T> encounter) {
    encounter.register(this);
  }
  
  public void afterInjection(Object watcher) {
    for (Mediation<?, ?, ?> m : this.mediation) {
      if (m.watcherType.isInstance(watcher))
        ((BeanLocator)this.locator.get()).watch(m.watchedKey, m.mediator, watcher); 
    } 
  }
  
  private static final class Mediation<Q extends Annotation, T, W> {
    final Key<T> watchedKey;
    
    final Mediator<Q, T, W> mediator;
    
    final Class<W> watcherType;
    
    Mediation(Key<T> watchedKey, Mediator<Q, T, W> mediator, Class<W> watcherType) {
      this.watchedKey = watchedKey;
      this.mediator = mediator;
      this.watcherType = watcherType;
    }
  }
}
