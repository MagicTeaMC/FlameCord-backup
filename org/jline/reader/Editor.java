package org.jline.reader;

import java.io.IOException;
import java.util.List;

public interface Editor {
  void open(List<String> paramList) throws IOException;
  
  void run() throws IOException;
  
  void setRestricted(boolean paramBoolean);
}
