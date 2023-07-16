package org.eclipse.sisu.wire;

import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Map;
import javax.inject.Provider;
import org.eclipse.sisu.BeanEntry;

final class ProviderIterableAdapter<K extends Annotation, V> implements Iterable<Map.Entry<K, Provider<V>>> {
  private final Iterable<BeanEntry<K, V>> delegate;
  
  ProviderIterableAdapter(Iterable<BeanEntry<K, V>> delegate) {
    this.delegate = delegate;
  }
  
  public Iterator<Map.Entry<K, Provider<V>>> iterator() {
    return new ProviderIterator<K, V>(this.delegate);
  }
  
  private static final class ProviderIterator<K extends Annotation, V> implements Iterator<Map.Entry<K, Provider<V>>> {
    private final Iterator<BeanEntry<K, V>> iterator;
    
    ProviderIterator(Iterable<BeanEntry<K, V>> iterable) {
      this.iterator = iterable.iterator();
    }
    
    public boolean hasNext() {
      return this.iterator.hasNext();
    }
    
    public Map.Entry<K, Provider<V>> next() {
      return new ProviderIterableAdapter.ProviderEntry<K, V>(this.iterator.next());
    }
    
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
  
  private static final class ProviderEntry<K extends Annotation, V> implements Map.Entry<K, Provider<V>> {
    private final BeanEntry<K, V> entry;
    
    ProviderEntry(BeanEntry<K, V> entry) {
      this.entry = entry;
    }
    
    public K getKey() {
      return (K)this.entry.getKey();
    }
    
    public Provider<V> getValue() {
      return this.entry.getProvider();
    }
    
    public Provider<V> setValue(Provider<V> value) {
      throw new UnsupportedOperationException();
    }
    
    public String toString() {
      return (new StringBuilder()).append(getKey()).append("=").append(getValue()).toString();
    }
  }
}
