package joptsimple.util;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.ResourceBundle;
import joptsimple.ValueConversionException;
import joptsimple.ValueConverter;

public abstract class EnumConverter<E extends Enum<E>> implements ValueConverter<E> {
  private final Class<E> clazz;
  
  private String delimiters = "[,]";
  
  protected EnumConverter(Class<E> clazz) {
    this.clazz = clazz;
  }
  
  public E convert(String value) {
    for (Enum enum_ : (Enum[])valueType().getEnumConstants()) {
      if (enum_.name().equalsIgnoreCase(value))
        return (E)enum_; 
    } 
    throw new ValueConversionException(message(value));
  }
  
  public Class<E> valueType() {
    return this.clazz;
  }
  
  public void setDelimiters(String delimiters) {
    this.delimiters = delimiters;
  }
  
  public String valuePattern() {
    EnumSet<E> values = EnumSet.allOf(valueType());
    StringBuilder builder = new StringBuilder();
    builder.append(this.delimiters.charAt(0));
    for (Iterator<E> i = values.iterator(); i.hasNext(); ) {
      builder.append(((Enum)i.next()).toString());
      if (i.hasNext())
        builder.append(this.delimiters.charAt(1)); 
    } 
    builder.append(this.delimiters.charAt(2));
    return builder.toString();
  }
  
  private String message(String value) {
    ResourceBundle bundle = ResourceBundle.getBundle("joptsimple.ExceptionMessages");
    Object[] arguments = { value, valuePattern() };
    String template = bundle.getString(EnumConverter.class.getName() + ".message");
    return (new MessageFormat(template)).format(arguments);
  }
}
