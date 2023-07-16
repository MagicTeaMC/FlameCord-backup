package org.apache.logging.log4j.core.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.impl.ContextDataFactory;
import org.apache.logging.log4j.util.StringMap;

public class ContextDataAsEntryListDeserializer extends StdDeserializer<StringMap> {
  private static final long serialVersionUID = 1L;
  
  ContextDataAsEntryListDeserializer() {
    super(Map.class);
  }
  
  public StringMap deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
    List<MapEntry> list = (List<MapEntry>)jp.readValueAs(new TypeReference<List<MapEntry>>() {
        
        });
    new ContextDataFactory();
    StringMap contextData = ContextDataFactory.createContextData();
    for (MapEntry mapEntry : list)
      contextData.putValue(mapEntry.getKey(), mapEntry.getValue()); 
    return contextData;
  }
}
