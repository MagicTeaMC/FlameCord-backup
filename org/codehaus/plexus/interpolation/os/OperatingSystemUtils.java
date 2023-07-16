package org.codehaus.plexus.interpolation.os;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

public final class OperatingSystemUtils {
  private static EnvVarSource envVarSource = new DefaultEnvVarSource();
  
  public static Properties getSystemEnvVars() throws IOException {
    return getSystemEnvVars(true);
  }
  
  public static Properties getSystemEnvVars(boolean caseSensitive) throws IOException {
    Properties envVars = new Properties();
    Map<String, String> envs = envVarSource.getEnvMap();
    for (String key : envs.keySet()) {
      String value = envs.get(key);
      if (!caseSensitive)
        key = key.toUpperCase(Locale.ENGLISH); 
      envVars.put(key, value);
    } 
    return envVars;
  }
  
  public static void setEnvVarSource(EnvVarSource source) {
    envVarSource = source;
  }
  
  public static class DefaultEnvVarSource implements EnvVarSource {
    public Map<String, String> getEnvMap() {
      return System.getenv();
    }
  }
  
  public static interface EnvVarSource {
    Map<String, String> getEnvMap();
  }
}
