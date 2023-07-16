package io.netty.util;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class ConstantPool<T extends Constant<T>> {
  private final ConcurrentMap<String, T> constants = PlatformDependent.newConcurrentHashMap();
  
  private final AtomicInteger nextId = new AtomicInteger(1);
  
  public T valueOf(Class<?> firstNameComponent, String secondNameComponent) {
    return valueOf((
        (Class)ObjectUtil.checkNotNull(firstNameComponent, "firstNameComponent")).getName() + '#' + 
        
        (String)ObjectUtil.checkNotNull(secondNameComponent, "secondNameComponent"));
  }
  
  public T valueOf(String name) {
    return getOrCreate(ObjectUtil.checkNonEmpty(name, "name"));
  }
  
  private T getOrCreate(String name) {
    Constant constant = (Constant)this.constants.get(name);
    if (constant == null) {
      T tempConstant = newConstant(nextId(), name);
      constant = (Constant)this.constants.putIfAbsent(name, tempConstant);
      if (constant == null)
        return tempConstant; 
    } 
    return (T)constant;
  }
  
  public boolean exists(String name) {
    return this.constants.containsKey(ObjectUtil.checkNonEmpty(name, "name"));
  }
  
  public T newInstance(String name) {
    return createOrThrow(ObjectUtil.checkNonEmpty(name, "name"));
  }
  
  private T createOrThrow(String name) {
    Constant constant = (Constant)this.constants.get(name);
    if (constant == null) {
      T tempConstant = newConstant(nextId(), name);
      constant = (Constant)this.constants.putIfAbsent(name, tempConstant);
      if (constant == null)
        return tempConstant; 
    } 
    throw new IllegalArgumentException(String.format("'%s' is already in use", new Object[] { name }));
  }
  
  protected abstract T newConstant(int paramInt, String paramString);
  
  @Deprecated
  public final int nextId() {
    return this.nextId.getAndIncrement();
  }
}
