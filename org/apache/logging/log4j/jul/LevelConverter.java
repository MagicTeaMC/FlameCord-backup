package org.apache.logging.log4j.jul;

import java.util.logging.Level;
import org.apache.logging.log4j.Level;

public interface LevelConverter {
  Level toLevel(Level paramLevel);
  
  Level toJavaLevel(Level paramLevel);
}
