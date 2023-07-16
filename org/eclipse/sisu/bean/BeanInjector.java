package org.eclipse.sisu.bean;

import com.google.inject.MembersInjector;
import java.util.List;

final class BeanInjector<B> implements MembersInjector<B> {
  private final PropertyBinding[] bindings;
  
  BeanInjector(List<PropertyBinding> bindings) {
    int size = bindings.size();
    this.bindings = new PropertyBinding[size];
    for (int i = 0, n = size; i < size;)
      this.bindings[i++] = bindings.get(--n); 
  }
  
  public void injectMembers(B bean) {
    byte b;
    int i;
    PropertyBinding[] arrayOfPropertyBinding;
    for (i = (arrayOfPropertyBinding = this.bindings).length, b = 0; b < i; ) {
      PropertyBinding propertyBinding = arrayOfPropertyBinding[b];
      propertyBinding.injectProperty(bean);
      b++;
    } 
  }
}
