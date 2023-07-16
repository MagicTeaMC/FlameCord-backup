package org.codehaus.plexus.util.cli;

import java.io.File;

public interface Arg {
  void setValue(String paramString);
  
  void setLine(String paramString);
  
  void setFile(File paramFile);
  
  String[] getParts();
}
