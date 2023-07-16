package org.apache.logging.log4j.core.appender.rolling.action;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

public interface PathCondition {
  public static final PathCondition[] EMPTY_ARRAY = new PathCondition[0];
  
  static PathCondition[] copy(PathCondition... source) {
    return (source == null || source.length == 0) ? EMPTY_ARRAY : Arrays.<PathCondition>copyOf(source, source.length);
  }
  
  void beforeFileTreeWalk();
  
  boolean accept(Path paramPath1, Path paramPath2, BasicFileAttributes paramBasicFileAttributes);
}
