package org.eclipse.sisu.wire;

import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;
import com.google.inject.spi.TypeConverterBinding;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;

@Singleton
final class TypeConverterCache {
  private final Map<TypeLiteral<?>, TypeConverter> converterMap = new ConcurrentHashMap<TypeLiteral<?>, TypeConverter>(16, 0.75F, 1);
  
  private final Injector injector;
  
  @Inject
  TypeConverterCache(Injector injector) {
    this.injector = injector;
  }
  
  public TypeConverter getTypeConverter(TypeLiteral<?> type) {
    TypeConverter converter = this.converterMap.get(type);
    if (converter == null)
      for (TypeConverterBinding b : this.injector.getTypeConverterBindings()) {
        if (b.getTypeMatcher().matches(type)) {
          converter = b.getTypeConverter();
          this.converterMap.put(type, converter);
          break;
        } 
      }  
    return converter;
  }
}
