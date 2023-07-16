package org.apache.logging.log4j.jul;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import org.apache.logging.log4j.Level;

public class DefaultLevelConverter implements LevelConverter {
  static final class JulLevelComparator implements Comparator<Level>, Serializable {
    private static final long serialVersionUID = 1L;
    
    public int compare(Level level1, Level level2) {
      return Integer.compare(level1.intValue(), level2.intValue());
    }
  }
  
  private final ConcurrentMap<Level, Level> julToLog4j = new ConcurrentHashMap<>(9);
  
  private final Map<Level, Level> log4jToJul = new IdentityHashMap<>(10);
  
  private final List<Level> sortedJulLevels = new ArrayList<>(9);
  
  public DefaultLevelConverter() {
    mapJulToLog4j(Level.ALL, Level.ALL);
    mapJulToLog4j(Level.FINEST, LevelTranslator.FINEST);
    mapJulToLog4j(Level.FINER, Level.TRACE);
    mapJulToLog4j(Level.FINE, Level.DEBUG);
    mapJulToLog4j(Level.CONFIG, LevelTranslator.CONFIG);
    mapJulToLog4j(Level.INFO, Level.INFO);
    mapJulToLog4j(Level.WARNING, Level.WARN);
    mapJulToLog4j(Level.SEVERE, Level.ERROR);
    mapJulToLog4j(Level.OFF, Level.OFF);
    mapLog4jToJul(Level.ALL, Level.ALL);
    mapLog4jToJul(LevelTranslator.FINEST, Level.FINEST);
    mapLog4jToJul(Level.TRACE, Level.FINER);
    mapLog4jToJul(Level.DEBUG, Level.FINE);
    mapLog4jToJul(LevelTranslator.CONFIG, Level.CONFIG);
    mapLog4jToJul(Level.INFO, Level.INFO);
    mapLog4jToJul(Level.WARN, Level.WARNING);
    mapLog4jToJul(Level.ERROR, Level.SEVERE);
    mapLog4jToJul(Level.FATAL, Level.SEVERE);
    mapLog4jToJul(Level.OFF, Level.OFF);
    this.sortedJulLevels.addAll(this.julToLog4j.keySet());
    Collections.sort(this.sortedJulLevels, new JulLevelComparator());
  }
  
  private long distance(Level javaLevel, Level customJavaLevel) {
    return Math.abs(customJavaLevel.intValue() - javaLevel.intValue());
  }
  
  private void mapJulToLog4j(Level julLevel, Level level) {
    this.julToLog4j.put(julLevel, level);
  }
  
  private void mapLog4jToJul(Level level, Level julLevel) {
    this.log4jToJul.put(level, julLevel);
  }
  
  private Level nearestLevel(Level customJavaLevel) {
    long prevDist = Long.MAX_VALUE;
    Level prevLevel = null;
    for (Level mappedJavaLevel : this.sortedJulLevels) {
      long distance = distance(customJavaLevel, mappedJavaLevel);
      if (distance > prevDist)
        return this.julToLog4j.get(prevLevel); 
      prevDist = distance;
      prevLevel = mappedJavaLevel;
    } 
    return this.julToLog4j.get(prevLevel);
  }
  
  public Level toJavaLevel(Level level) {
    return this.log4jToJul.get(level);
  }
  
  public Level toLevel(Level javaLevel) {
    if (javaLevel == null)
      return null; 
    Level level = this.julToLog4j.get(javaLevel);
    if (level != null)
      return level; 
    Level nearestLevel = nearestLevel(javaLevel);
    this.julToLog4j.put(javaLevel, nearestLevel);
    return nearestLevel;
  }
}
