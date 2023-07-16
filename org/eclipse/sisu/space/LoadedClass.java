package org.eclipse.sisu.space;

public final class LoadedClass<T> extends AbstractDeferredClass<T> {
  private final Class<T> clazz;
  
  public LoadedClass(Class<? extends T> clazz) {
    this.clazz = (Class)clazz;
  }
  
  public Class<T> load() {
    return this.clazz;
  }
  
  public String getName() {
    return this.clazz.getName();
  }
  
  public int hashCode() {
    return this.clazz.hashCode();
  }
  
  public boolean equals(Object rhs) {
    if (this == rhs)
      return true; 
    if (rhs instanceof LoadedClass)
      return (this.clazz == ((LoadedClass)rhs).clazz); 
    return false;
  }
  
  public String toString() {
    String id = "Loaded " + this.clazz;
    ClassLoader space = this.clazz.getClassLoader();
    return (space != null) ? (String.valueOf(id) + " from " + space) : id;
  }
}
