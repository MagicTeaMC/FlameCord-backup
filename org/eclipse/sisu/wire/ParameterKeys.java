package org.eclipse.sisu.wire;

import com.google.inject.Key;
import java.util.Map;
import org.eclipse.sisu.Parameters;

public interface ParameterKeys {
  public static final Key<Map> PROPERTIES = Key.get(Map.class, Parameters.class);
  
  public static final Key<String[]> ARGUMENTS = Key.get(String[].class, Parameters.class);
}
