package org.eclipse.sisu.bean;

import java.lang.reflect.Member;
import java.util.Iterator;

public final class BeanProperties implements Iterable<BeanProperty<Object>> {
  private final Iterable<Member> members;
  
  public BeanProperties(Class<?> clazz) {
    if (clazz.isAnnotationPresent((Class)IgnoreSetters.class)) {
      this.members = new DeclaredMembers(clazz, new DeclaredMembers.View[] { DeclaredMembers.View.FIELDS });
    } else {
      this.members = new DeclaredMembers(clazz, new DeclaredMembers.View[] { DeclaredMembers.View.METHODS, DeclaredMembers.View.FIELDS });
    } 
  }
  
  BeanProperties(Iterable<Member> members) {
    this.members = members;
  }
  
  public Iterator<BeanProperty<Object>> iterator() {
    return new BeanPropertyIterator(this.members);
  }
}
