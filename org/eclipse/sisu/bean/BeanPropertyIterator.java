package org.eclipse.sisu.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.NoSuchElementException;

final class BeanPropertyIterator<T> implements Iterator<BeanProperty<T>> {
  private final Iterator<Member> memberIterator;
  
  private BeanProperty<T> nextProperty;
  
  BeanPropertyIterator(Iterable<Member> members) {
    this.memberIterator = members.iterator();
  }
  
  public boolean hasNext() {
    while (this.nextProperty == null) {
      if (!this.memberIterator.hasNext())
        return false; 
      Member member = this.memberIterator.next();
      int modifiers = member.getModifiers();
      if (Modifier.isStatic(modifiers) || Modifier.isAbstract(modifiers) || member.isSynthetic())
        continue; 
      if (member instanceof Method) {
        if (isSetter(member))
          this.nextProperty = new BeanPropertySetter<T>((Method)member); 
        continue;
      } 
      if (member instanceof Field)
        this.nextProperty = new BeanPropertyField<T>((Field)member); 
    } 
    return true;
  }
  
  public BeanProperty<T> next() {
    if (hasNext()) {
      BeanProperty<T> property = this.nextProperty;
      this.nextProperty = null;
      return property;
    } 
    throw new NoSuchElementException();
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  private static boolean isSetter(Member member) {
    String name = member.getName();
    return (name.startsWith("set") && name.length() > 3 && Character.isUpperCase(name.charAt(3)) && ((
      (Method)member).getParameterTypes()).length == 1);
  }
}
