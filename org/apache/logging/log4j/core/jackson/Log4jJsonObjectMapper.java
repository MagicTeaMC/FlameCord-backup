package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Log4jJsonObjectMapper extends ObjectMapper {
  private static final long serialVersionUID = 1L;
  
  public Log4jJsonObjectMapper() {
    this(false, true, false, false);
  }
  
  public Log4jJsonObjectMapper(boolean encodeThreadContextAsList, boolean includeStacktrace, boolean stacktraceAsString, boolean objectMessageAsJsonObject) {
    registerModule((Module)new Log4jJsonModule(encodeThreadContextAsList, includeStacktrace, stacktraceAsString, objectMessageAsJsonObject));
    setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
  }
}
