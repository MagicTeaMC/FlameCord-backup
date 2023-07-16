package joptsimple.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Locale;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;
import joptsimple.internal.Messages;

public class InetAddressConverter implements ValueConverter<InetAddress> {
  public InetAddress convert(String value) {
    try {
      return InetAddress.getByName(value);
    } catch (UnknownHostException e) {
      throw new ValueConversionException(message(value));
    } 
  }
  
  public Class<InetAddress> valueType() {
    return InetAddress.class;
  }
  
  public String valuePattern() {
    return null;
  }
  
  private String message(String value) {
    return Messages.message(
        Locale.getDefault(), "joptsimple.ExceptionMessages", InetAddressConverter.class, "message", new Object[] { value });
  }
}
