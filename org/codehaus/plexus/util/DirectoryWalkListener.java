package org.codehaus.plexus.util;

import java.io.File;

public interface DirectoryWalkListener {
  void directoryWalkStarting(File paramFile);
  
  void directoryWalkStep(int paramInt, File paramFile);
  
  void directoryWalkFinished();
  
  void debug(String paramString);
}
