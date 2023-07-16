package org.eclipse.sisu.wire;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import java.net.MalformedURLException;
import java.net.URL;

final class URLTypeConverter extends AbstractTypeConverter<URL> {
  public Object convert(String value, TypeLiteral<?> toType) {
    try {
      return new URL(value);
    } catch (MalformedURLException e) {
      throw new ProvisionException(e.toString());
    } 
  }
}
