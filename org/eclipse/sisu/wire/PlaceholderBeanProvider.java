package org.eclipse.sisu.wire;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import com.google.inject.spi.TypeConverter;
import java.lang.annotation.Annotation;
import java.util.Map;
import javax.inject.Inject;
import org.eclipse.sisu.Parameters;
import org.eclipse.sisu.inject.BeanLocator;

final class PlaceholderBeanProvider<V> implements Provider<V> {
  private static final int EXPRESSION_RECURSION_LIMIT = 8;
  
  @Inject
  @Parameters
  private Map properties;
  
  @Inject
  private TypeConverterCache converterCache;
  
  private final Provider<BeanLocator> locator;
  
  private final Key<V> placeholderKey;
  
  PlaceholderBeanProvider(Provider<BeanLocator> locator, Key<V> key) {
    this.locator = locator;
    this.placeholderKey = key;
  }
  
  public V get() {
    String template = ((Named)this.placeholderKey.getAnnotation()).value();
    TypeLiteral<V> expectedType = this.placeholderKey.getTypeLiteral();
    Class<?> clazz = expectedType.getRawType();
    Object value = interpolate(template, clazz);
    if (!(value instanceof String))
      return (V)value; 
    Key<V> lookupKey = Key.get(expectedType, (Annotation)Names.named((String)value));
    if (String.class != clazz) {
      V bean = lookup(lookupKey);
      if (bean != null)
        return bean; 
    } 
    if (template == value)
      value = nullify(lookup(lookupKey.ofType(String.class))); 
    if (value == null || String.class == clazz)
      return (V)value; 
    TypeConverter converter = this.converterCache.getTypeConverter(expectedType);
    if (converter != null)
      return (V)converter.convert((String)value, expectedType); 
    return null;
  }
  
  private <T> T lookup(Key<T> key) {
    return BeanProviders.firstOf(((BeanLocator)this.locator.get()).locate(key));
  }
  
  private static String nullify(String value) {
    return "null".equals(value) ? null : value;
  }
  
  private Object interpolate(String template, Class<?> clazz) {
    StringBuilder buf;
    if (template.contains("${")) {
      buf = new StringBuilder(template);
    } else if (this.properties.containsKey(template)) {
      buf = (new StringBuilder("${")).append(template).append('}');
    } else {
      return template;
    } 
    int x = 0, expressionEnd = 0, expressionNum = 0;
    int y;
    while ((x = buf.indexOf("${", x)) >= 0 && (y = buf.indexOf("}", x) + 1) > 0) {
      if (y > expressionEnd) {
        expressionNum = 0;
        expressionEnd = y;
      } 
      String key = buf.substring(x + 2, y - 1);
      int anchor = key.indexOf(":-");
      Object value = this.properties.get((anchor < 0) ? key : key.substring(0, anchor));
      if (value == null && anchor >= 0)
        value = key.substring(anchor + 2); 
      if (expressionNum++ >= 8)
        throw new ProvisionException("Recursive configuration: " + template + " stopped at: " + buf); 
      int len = buf.length();
      if (x == 0 && len == y && String.class != clazz && clazz.isInstance(value))
        return value; 
      buf.replace(x, y, String.valueOf(value));
      expressionEnd += buf.length() - len;
    } 
    return nullify(buf.toString());
  }
}
