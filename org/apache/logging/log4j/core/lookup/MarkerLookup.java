package org.apache.logging.log4j.core.lookup;

import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "marker", category = "Lookup")
public class MarkerLookup extends AbstractLookup {
  static final String MARKER = "marker";
  
  public String lookup(LogEvent event, String key) {
    Marker marker = (event == null) ? null : event.getMarker();
    return (marker == null) ? null : marker.getName();
  }
  
  public String lookup(String key) {
    return MarkerManager.exists(key) ? key : null;
  }
}
