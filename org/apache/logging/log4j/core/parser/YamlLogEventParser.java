package org.apache.logging.log4j.core.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.jackson.Log4jYamlObjectMapper;

public class YamlLogEventParser extends AbstractJacksonLogEventParser {
  public YamlLogEventParser() {
    super((ObjectMapper)new Log4jYamlObjectMapper());
  }
}
