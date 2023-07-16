package org.apache.logging.log4j.jul;

import java.util.logging.Level;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.LoaderUtil;
import org.apache.logging.log4j.util.PropertiesUtil;

public final class LevelTranslator {
  public static final Level FINEST = Level.forName("FINEST", Level.TRACE.intLevel() + 100);
  
  public static final Level CONFIG = Level.forName("CONFIG", Level.INFO.intLevel() + 50);
  
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private static final LevelConverter LEVEL_CONVERTER;
  
  static {
    String levelConverterClassName = PropertiesUtil.getProperties().getStringProperty("log4j.jul.levelConverter");
    if (levelConverterClassName != null) {
      LevelConverter levelConverter;
      try {
        levelConverter = (LevelConverter)LoaderUtil.newCheckedInstanceOf(levelConverterClassName, LevelConverter.class);
      } catch (Exception e) {
        LOGGER.error("Could not create custom LevelConverter [{}].", levelConverterClassName, e);
        levelConverter = new DefaultLevelConverter();
      } 
      LEVEL_CONVERTER = levelConverter;
    } else {
      LEVEL_CONVERTER = new DefaultLevelConverter();
    } 
  }
  
  public static Level toLevel(Level level) {
    return LEVEL_CONVERTER.toLevel(level);
  }
  
  public static Level toJavaLevel(Level level) {
    return LEVEL_CONVERTER.toJavaLevel(level);
  }
}
