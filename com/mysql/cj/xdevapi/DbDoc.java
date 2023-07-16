package com.mysql.cj.xdevapi;

import java.util.Map;

public interface DbDoc extends JsonValue, Map<String, JsonValue> {
  DbDoc add(String paramString, JsonValue paramJsonValue);
}
