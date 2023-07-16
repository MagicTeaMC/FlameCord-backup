package org.codehaus.plexus.util.xml;

public interface XMLWriter {
  void startElement(String paramString);
  
  void addAttribute(String paramString1, String paramString2);
  
  void writeText(String paramString);
  
  void writeMarkup(String paramString);
  
  void endElement();
}
