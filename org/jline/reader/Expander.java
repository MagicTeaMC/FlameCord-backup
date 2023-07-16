package org.jline.reader;

public interface Expander {
  String expandHistory(History paramHistory, String paramString);
  
  String expandVar(String paramString);
}
