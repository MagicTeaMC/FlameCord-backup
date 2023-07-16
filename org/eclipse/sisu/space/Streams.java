package org.eclipse.sisu.space;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Locale;

public final class Streams {
  private static final boolean USE_CACHES;
  
  static {
    boolean useCaches;
    try {
      String urlCaches = System.getProperty("sisu.url.caches");
      if (urlCaches != null && !urlCaches.isEmpty()) {
        useCaches = Boolean.parseBoolean(urlCaches);
      } else {
        String osName = System.getProperty("os.name").toLowerCase(Locale.US);
        useCaches = !osName.contains("windows");
      } 
    } catch (RuntimeException runtimeException) {
      useCaches = true;
    } 
    USE_CACHES = useCaches;
  }
  
  public static InputStream open(URL url) throws IOException {
    if (USE_CACHES)
      return url.openStream(); 
    URLConnection conn = url.openConnection();
    conn.setUseCaches(false);
    return conn.getInputStream();
  }
}
