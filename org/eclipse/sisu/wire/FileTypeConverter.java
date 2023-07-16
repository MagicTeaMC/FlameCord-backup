package org.eclipse.sisu.wire;

import com.google.inject.TypeLiteral;
import java.io.File;

final class FileTypeConverter extends AbstractTypeConverter<File> {
  public Object convert(String value, TypeLiteral<?> toType) {
    return new File(value);
  }
}
