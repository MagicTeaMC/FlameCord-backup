package org.eclipse.sisu.bean;

import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

public final class BeanListener implements TypeListener {
  private final BeanBinder beanBinder;
  
  public BeanListener(BeanBinder beanBinder) {
    this.beanBinder = beanBinder;
  }
  
  public <B> void hear(TypeLiteral<B> type, TypeEncounter<B> encounter) {
    PropertyBinder propertyBinder = this.beanBinder.bindBean(type, encounter);
    if (propertyBinder == null)
      return; 
    List<PropertyBinding> bindings = new ArrayList<PropertyBinding>();
    Set<String> visited = new HashSet<String>();
    for (BeanProperty<?> property : (Iterable<BeanProperty<?>>)new BeanProperties(type.getRawType())) {
      if (property.getAnnotation(Inject.class) != null || 
        property.getAnnotation(Inject.class) != null)
        continue; 
      String name = property.getName();
      if (visited.add(name))
        try {
          PropertyBinding binding = propertyBinder.bindProperty(property);
          if (binding == PropertyBinder.LAST_BINDING)
            break; 
          if (binding != null) {
            bindings.add(binding);
            continue;
          } 
          visited.remove(name);
        } catch (RuntimeException e) {
          encounter.addError((Throwable)new ProvisionException("Error binding: " + property, e));
        }  
    } 
    if (bindings.size() > 0)
      encounter.register(new BeanInjector(bindings)); 
  }
}
