package com.maxmind.db;

import java.lang.reflect.Type;

public final class CacheKey<T> {
  private final int offset;
  
  private final Class<T> cls;
  
  private final Type type;
  
  CacheKey(int offset, Class<T> cls, Type type) {
    this.offset = offset;
    this.cls = cls;
    this.type = type;
  }
  
  int getOffset() {
    return this.offset;
  }
  
  Class<T> getCls() {
    return this.cls;
  }
  
  Type getType() {
    return this.type;
  }
  
  public boolean equals(Object o) {
    if (o == null)
      return false; 
    CacheKey other = (CacheKey)o;
    if (this.offset != other.offset)
      return false; 
    if (this.cls == null) {
      if (other.cls != null)
        return false; 
    } else if (!this.cls.equals(other.cls)) {
      return false;
    } 
    if (this.type == null)
      return (other.type == null); 
    return this.type.equals(other.type);
  }
  
  public int hashCode() {
    int result = this.offset;
    result = 31 * result + ((this.cls == null) ? 0 : this.cls.hashCode());
    result = 31 * result + ((this.type == null) ? 0 : this.type.hashCode());
    return result;
  }
}
