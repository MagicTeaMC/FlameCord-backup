package org.apache.logging.slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.apache.logging.log4j.status.StatusLogger;
import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;

public class Log4jMarkerFactory implements IMarkerFactory {
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private final ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap<>();
  
  public Marker getMarker(String name) {
    if (name == null)
      throw new IllegalArgumentException("Marker name must not be null"); 
    Marker marker = this.markerMap.get(name);
    if (marker != null)
      return marker; 
    Marker log4jMarker = MarkerManager.getMarker(name);
    return addMarkerIfAbsent(name, log4jMarker);
  }
  
  private Marker addMarkerIfAbsent(String name, Marker log4jMarker) {
    Marker marker = new Log4jMarker(this, log4jMarker);
    Marker existing = this.markerMap.putIfAbsent(name, marker);
    return (existing == null) ? marker : existing;
  }
  
  public Marker getMarker(Marker marker) {
    if (marker == null)
      throw new IllegalArgumentException("Marker must not be null"); 
    Marker m = this.markerMap.get(marker.getName());
    if (m != null)
      return m; 
    return addMarkerIfAbsent(marker.getName(), convertMarker(marker));
  }
  
  Marker getLog4jMarker(Marker marker) {
    if (marker == null)
      return null; 
    if (marker instanceof Log4jMarker)
      return ((Log4jMarker)marker).getLog4jMarker(); 
    return ((Log4jMarker)getMarker(marker)).getLog4jMarker();
  }
  
  static Marker convertMarker(Marker original) {
    if (original == null)
      throw new IllegalArgumentException("Marker must not be null"); 
    return convertMarker(original, new ArrayList<>());
  }
  
  private static Marker convertMarker(Marker original, Collection<Marker> visited) {
    Marker marker = MarkerManager.getMarker(original.getName());
    if (original.hasReferences()) {
      Iterator<Marker> it = original.iterator();
      while (it.hasNext()) {
        Marker next = it.next();
        if (visited.contains(next)) {
          LOGGER.warn("Found a cycle in Marker [{}]. Cycle will be broken.", next.getName());
          continue;
        } 
        visited.add(next);
        marker.addParents(new Marker[] { convertMarker(next, visited) });
      } 
    } 
    return marker;
  }
  
  public boolean exists(String name) {
    return this.markerMap.containsKey(name);
  }
  
  public boolean detachMarker(String name) {
    return false;
  }
  
  public Marker getDetachedMarker(String name) {
    LOGGER.warn("Log4j does not support detached Markers. Returned Marker [{}] will be unchanged.", name);
    return getMarker(name);
  }
}
