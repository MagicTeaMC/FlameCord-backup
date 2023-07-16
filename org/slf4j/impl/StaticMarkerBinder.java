package org.slf4j.impl;

import org.apache.logging.slf4j.Log4jMarkerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MarkerFactoryBinder;

public class StaticMarkerBinder implements MarkerFactoryBinder {
  public static final StaticMarkerBinder SINGLETON = new StaticMarkerBinder();
  
  private final IMarkerFactory markerFactory = (IMarkerFactory)new Log4jMarkerFactory();
  
  public static StaticMarkerBinder getSingleton() {
    return SINGLETON;
  }
  
  public IMarkerFactory getMarkerFactory() {
    return this.markerFactory;
  }
  
  public String getMarkerFactoryClassStr() {
    return Log4jMarkerFactory.class.getName();
  }
}
