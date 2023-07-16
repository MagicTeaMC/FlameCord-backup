package org.apache.maven.model.building;

public interface ModelCache {
  void put(String paramString1, String paramString2, String paramString3, String paramString4, Object paramObject);
  
  Object get(String paramString1, String paramString2, String paramString3, String paramString4);
}
