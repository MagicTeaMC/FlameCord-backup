package org.eclipse.sisu.wire;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.spi.TypeConverter;
import org.eclipse.sisu.inject.TypeArguments;

public abstract class AbstractTypeConverter<T> implements TypeConverter, Module {
  public final void configure(Binder binder) {
    TypeLiteral<?> superType = TypeLiteral.get(getClass()).getSupertype(AbstractTypeConverter.class);
    binder.convertToTypes(Matchers.only(TypeArguments.get(superType, 0)), this);
  }
}
