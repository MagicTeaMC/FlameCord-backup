package org.apache.maven.building;

import java.io.IOException;
import java.io.InputStream;

public interface Source {
  InputStream getInputStream() throws IOException;
  
  String getLocation();
}
