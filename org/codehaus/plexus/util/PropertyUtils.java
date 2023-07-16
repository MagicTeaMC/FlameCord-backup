package org.codehaus.plexus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class PropertyUtils {
  public static Properties loadProperties(URL url) throws IOException {
    if (url == null)
      throw new NullPointerException("url"); 
    return loadProperties(url.openStream());
  }
  
  public static Properties loadProperties(File file) throws IOException {
    if (file == null)
      throw new NullPointerException("file"); 
    return loadProperties(new FileInputStream(file));
  }
  
  public static Properties loadProperties(InputStream is) throws IOException {
    InputStream in = is;
    try {
      Properties properties = new Properties();
      if (in != null) {
        properties.load(in);
        in.close();
        in = null;
      } 
      return properties;
    } finally {
      IOUtil.close(in);
    } 
  }
}
