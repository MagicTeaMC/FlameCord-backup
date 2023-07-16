package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Provider;
import org.eclipse.sisu.BeanEntry;
import org.eclipse.sisu.inject.BeanLocator;
import org.eclipse.sisu.inject.TypeArguments;

final class BeanProviders {
  final Provider<BeanLocator> locator;
  
  BeanProviders(Binder binder) {
    this.locator = binder.getProvider(BeanLocator.class);
  }
  
  public <K extends Annotation, V> Provider<Iterable<? extends BeanEntry<K, V>>> beanEntriesOf(final Key<V> key) {
    return new Provider<Iterable<? extends BeanEntry<K, V>>>() {
        public Iterable<? extends BeanEntry<K, V>> get() {
          return ((BeanLocator)BeanProviders.this.locator.get()).locate(key);
        }
      };
  }
  
  public <K extends Annotation, V> Provider<Iterable<Map.Entry<K, V>>> entriesOf(Key<V> key) {
    TypeLiteral<V> type = key.getTypeLiteral();
    Class<?> clazz = type.getRawType();
    if (Provider.class != clazz && Provider.class != clazz)
      return (Provider)beanEntriesOf(key); 
    final Provider<Iterable<BeanEntry>> beanEntries = (Provider)beanEntriesOf(key.ofType(TypeArguments.get(type, 0)));
    return new Provider<Iterable<Map.Entry<K, V>>>() {
        public Iterable<Map.Entry<K, V>> get() {
          return (Iterable)new ProviderIterableAdapter<Annotation, Object>((Iterable<BeanEntry<Annotation, ?>>)beanEntries.get());
        }
      };
  }
  
  public <K extends Annotation, V> Provider<List<V>> listOf(Key<V> key) {
    final Provider<Iterable<Map.Entry<K, V>>> entries = entriesOf(key);
    return new Provider<List<V>>() {
        public List<V> get() {
          return new EntryListAdapter<V>((Iterable<? extends Map.Entry<?, V>>)entries.get());
        }
      };
  }
  
  public <K extends Annotation, V> Provider<Set<V>> setOf(Key<V> key) {
    final Provider<Iterable<Map.Entry<K, V>>> entries = entriesOf(key);
    return new Provider<Set<V>>() {
        public Set<V> get() {
          return new EntrySetAdapter<V>((Iterable<? extends Map.Entry<?, V>>)entries.get());
        }
      };
  }
  
  public <K extends Annotation, V> Provider<Map<K, V>> mapOf(Key<V> key) {
    final Provider<Iterable<Map.Entry<K, V>>> entries = entriesOf(key);
    return new Provider<Map<K, V>>() {
        public Map<K, V> get() {
          return new EntryMapAdapter<K, V>((Iterable<? extends Map.Entry<K, V>>)entries.get());
        }
      };
  }
  
  public <V> Provider<Map<String, V>> stringMapOf(TypeLiteral<V> type) {
    final Provider<Iterable<Map.Entry<Named, V>>> entries = entriesOf(Key.get(type, Named.class));
    return new Provider<Map<String, V>>() {
        public Map<String, V> get() {
          return new EntryMapAdapter<String, V>(new NamedIterableAdapter((Iterable<Map.Entry<Named, ?>>)entries.get()));
        }
      };
  }
  
  <V> Provider<V> firstOf(Key<V> key) {
    final Provider<Iterable<? extends BeanEntry<Annotation, V>>> beanEntries = beanEntriesOf(key);
    return new Provider<V>() {
        public V get() {
          return BeanProviders.firstOf((Iterable<? extends Map.Entry<?, V>>)beanEntries.get());
        }
      };
  }
  
  public <V> Provider<V> placeholderOf(Key<V> key) {
    return new PlaceholderBeanProvider<V>(this.locator, key);
  }
  
  public static <V> V firstOf(Iterable<? extends Map.Entry<?, V>> entries) {
    Iterator<? extends Map.Entry<?, V>> itr = entries.iterator();
    return itr.hasNext() ? (V)((Map.Entry)itr.next()).getValue() : null;
  }
}
