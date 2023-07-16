package org.apache.logging.log4j.core.net;

public enum Protocol {
  TCP, SSL, UDP;
  
  public boolean isEqual(String name) {
    return name().equalsIgnoreCase(name);
  }
}
