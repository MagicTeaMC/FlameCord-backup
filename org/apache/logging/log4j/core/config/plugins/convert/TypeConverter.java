package org.apache.logging.log4j.core.config.plugins.convert;

public interface TypeConverter<T> {
  T convert(String paramString) throws Exception;
}
