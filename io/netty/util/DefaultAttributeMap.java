package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultAttributeMap implements AttributeMap {
  private static final AtomicReferenceFieldUpdater<DefaultAttributeMap, DefaultAttribute[]> ATTRIBUTES_UPDATER = (AtomicReferenceFieldUpdater)AtomicReferenceFieldUpdater.newUpdater(DefaultAttributeMap.class, (Class)DefaultAttribute[].class, "attributes");
  
  private static final DefaultAttribute[] EMPTY_ATTRIBUTES = new DefaultAttribute[0];
  
  private static int searchAttributeByKey(DefaultAttribute[] sortedAttributes, AttributeKey<?> key) {
    int low = 0;
    int high = sortedAttributes.length - 1;
    while (low <= high) {
      int mid = low + high >>> 1;
      DefaultAttribute midVal = sortedAttributes[mid];
      AttributeKey<?> midValKey = midVal.key;
      if (midValKey == key)
        return mid; 
      int midValKeyId = midValKey.id();
      int keyId = key.id();
      assert midValKeyId != keyId;
      boolean searchRight = (midValKeyId < keyId);
      if (searchRight) {
        low = mid + 1;
        continue;
      } 
      high = mid - 1;
    } 
    return -(low + 1);
  }
  
  private static void orderedCopyOnInsert(DefaultAttribute[] sortedSrc, int srcLength, DefaultAttribute[] copy, DefaultAttribute toInsert) {
    int id = toInsert.key.id();
    int i;
    for (i = srcLength - 1; i >= 0; i--) {
      DefaultAttribute attribute = sortedSrc[i];
      assert attribute.key.id() != id;
      if (attribute.key.id() < id)
        break; 
      copy[i + 1] = sortedSrc[i];
    } 
    copy[i + 1] = toInsert;
    int toCopy = i + 1;
    if (toCopy > 0)
      System.arraycopy(sortedSrc, 0, copy, 0, toCopy); 
  }
  
  private volatile DefaultAttribute[] attributes = EMPTY_ATTRIBUTES;
  
  public <T> Attribute<T> attr(AttributeKey<T> key) {
    ObjectUtil.checkNotNull(key, "key");
    DefaultAttribute<T> newAttribute = null;
    while (true) {
      DefaultAttribute[] newAttributes, attributes = this.attributes;
      int index = searchAttributeByKey(attributes, key);
      if (index >= 0) {
        DefaultAttribute<T> attribute = attributes[index];
        assert attribute.key() == key;
        if (!attribute.isRemoved())
          return attribute; 
        if (newAttribute == null)
          newAttribute = new DefaultAttribute<T>(this, key); 
        int count = attributes.length;
        newAttributes = Arrays.<DefaultAttribute>copyOf(attributes, count);
        newAttributes[index] = newAttribute;
      } else {
        if (newAttribute == null)
          newAttribute = new DefaultAttribute<T>(this, key); 
        int count = attributes.length;
        newAttributes = new DefaultAttribute[count + 1];
        orderedCopyOnInsert(attributes, count, newAttributes, newAttribute);
      } 
      if (ATTRIBUTES_UPDATER.compareAndSet(this, attributes, newAttributes))
        return newAttribute; 
    } 
  }
  
  public <T> boolean hasAttr(AttributeKey<T> key) {
    ObjectUtil.checkNotNull(key, "key");
    return (searchAttributeByKey(this.attributes, key) >= 0);
  }
  
  private <T> void removeAttributeIfMatch(AttributeKey<T> key, DefaultAttribute<T> value) {
    DefaultAttribute[] attributes;
    DefaultAttribute[] newAttributes;
    do {
      attributes = this.attributes;
      int index = searchAttributeByKey(attributes, key);
      if (index < 0)
        return; 
      DefaultAttribute<T> attribute = attributes[index];
      assert attribute.key() == key;
      if (attribute != value)
        return; 
      int count = attributes.length;
      int newCount = count - 1;
      newAttributes = (newCount == 0) ? EMPTY_ATTRIBUTES : new DefaultAttribute[newCount];
      System.arraycopy(attributes, 0, newAttributes, 0, index);
      int remaining = count - index - 1;
      if (remaining <= 0)
        continue; 
      System.arraycopy(attributes, index + 1, newAttributes, index, remaining);
    } while (!ATTRIBUTES_UPDATER.compareAndSet(this, attributes, newAttributes));
  }
  
  private static final class DefaultAttribute<T> extends AtomicReference<T> implements Attribute<T> {
    private static final AtomicReferenceFieldUpdater<DefaultAttribute, DefaultAttributeMap> MAP_UPDATER = AtomicReferenceFieldUpdater.newUpdater(DefaultAttribute.class, DefaultAttributeMap.class, "attributeMap");
    
    private static final long serialVersionUID = -2661411462200283011L;
    
    private volatile DefaultAttributeMap attributeMap;
    
    private final AttributeKey<T> key;
    
    DefaultAttribute(DefaultAttributeMap attributeMap, AttributeKey<T> key) {
      this.attributeMap = attributeMap;
      this.key = key;
    }
    
    public AttributeKey<T> key() {
      return this.key;
    }
    
    private boolean isRemoved() {
      return (this.attributeMap == null);
    }
    
    public T setIfAbsent(T value) {
      while (!compareAndSet(null, value)) {
        T old = get();
        if (old != null)
          return old; 
      } 
      return null;
    }
    
    public T getAndRemove() {
      DefaultAttributeMap attributeMap = this.attributeMap;
      boolean removed = (attributeMap != null && MAP_UPDATER.compareAndSet(this, attributeMap, null));
      T oldValue = getAndSet(null);
      if (removed)
        attributeMap.removeAttributeIfMatch(this.key, this); 
      return oldValue;
    }
    
    public void remove() {
      DefaultAttributeMap attributeMap = this.attributeMap;
      boolean removed = (attributeMap != null && MAP_UPDATER.compareAndSet(this, attributeMap, null));
      set(null);
      if (removed)
        attributeMap.removeAttributeIfMatch(this.key, this); 
    }
  }
}
