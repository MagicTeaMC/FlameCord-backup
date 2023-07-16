package org.eclipse.sisu.space;

final class NamedClass<T> extends AbstractDeferredClass<T> {
  private final ClassSpace space;
  
  private final String name;
  
  NamedClass(ClassSpace space, String name) {
    this.space = space;
    this.name = name;
  }
  
  public Class<T> load() {
    return (Class)this.space.loadClass(this.name);
  }
  
  public String getName() {
    return this.name;
  }
  
  public int hashCode() {
    return (527 + this.name.hashCode()) * 31 + this.space.hashCode();
  }
  
  public boolean equals(Object rhs) {
    if (this == rhs)
      return true; 
    if (rhs instanceof NamedClass) {
      NamedClass<?> clazz = (NamedClass)rhs;
      return (this.name.equals(clazz.name) && this.space.equals(clazz.space));
    } 
    return false;
  }
  
  public String toString() {
    return "Deferred " + this.name + " from " + this.space;
  }
}
