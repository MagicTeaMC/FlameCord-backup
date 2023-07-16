package org.eclipse.sisu.space;

import java.util.Locale;
import java.util.Map;

public enum BeanScanning {
  ON, OFF, CACHE, INDEX, GLOBAL_INDEX;
  
  public static BeanScanning select(Map<?, ?> properties) throws IllegalArgumentException {
    String option = (String)properties.get(BeanScanning.class.getName());
    if (option == null || option.trim().length() == 0)
      return ON; 
    return Enum.<BeanScanning>valueOf(BeanScanning.class, option.toUpperCase(Locale.ENGLISH));
  }
}
