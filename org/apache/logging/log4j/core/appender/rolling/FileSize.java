package org.apache.logging.log4j.core.appender.rolling;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

public final class FileSize {
  private static final Logger LOGGER = (Logger)StatusLogger.getLogger();
  
  private static final long KB = 1024L;
  
  private static final long MB = 1048576L;
  
  private static final long GB = 1073741824L;
  
  private static final long TB = 1099511627776L;
  
  private static final Pattern VALUE_PATTERN = Pattern.compile("([0-9]+([.,][0-9]+)?)\\s*(|K|M|G|T)B?", 2);
  
  public static long parse(String string, long defaultValue) {
    Matcher matcher = VALUE_PATTERN.matcher(string);
    if (matcher.matches())
      try {
        String quantityString = matcher.group(1);
        double quantity = NumberFormat.getNumberInstance(Locale.ROOT).parse(quantityString).doubleValue();
        String unit = matcher.group(3);
        if (unit == null || unit.isEmpty())
          return (long)quantity; 
        if (unit.equalsIgnoreCase("K"))
          return (long)(quantity * 1024.0D); 
        if (unit.equalsIgnoreCase("M"))
          return (long)(quantity * 1048576.0D); 
        if (unit.equalsIgnoreCase("G"))
          return (long)(quantity * 1.073741824E9D); 
        if (unit.equalsIgnoreCase("T"))
          return (long)(quantity * 1.099511627776E12D); 
        LOGGER.error("FileSize units not recognized: " + string);
        return defaultValue;
      } catch (ParseException error) {
        LOGGER.error("FileSize unable to parse numeric part: " + string, error);
        return defaultValue;
      }  
    LOGGER.error("FileSize unable to parse bytes: " + string);
    return defaultValue;
  }
}
