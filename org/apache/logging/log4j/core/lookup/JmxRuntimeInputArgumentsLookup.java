package org.apache.logging.log4j.core.lookup;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;

@Plugin(name = "jvmrunargs", category = "Lookup")
public class JmxRuntimeInputArgumentsLookup extends MapLookup {
  public static final JmxRuntimeInputArgumentsLookup JMX_SINGLETON;
  
  static {
    List<String> argsList = ManagementFactory.getRuntimeMXBean().getInputArguments();
    JMX_SINGLETON = new JmxRuntimeInputArgumentsLookup(MapLookup.toMap(argsList));
  }
  
  public JmxRuntimeInputArgumentsLookup() {}
  
  public JmxRuntimeInputArgumentsLookup(Map<String, String> map) {
    super(map);
  }
  
  public String lookup(LogEvent event, String key) {
    return lookup(key);
  }
  
  public String lookup(String key) {
    if (key == null)
      return null; 
    Map<String, String> map = getMap();
    return (map == null) ? null : map.get(key);
  }
}
