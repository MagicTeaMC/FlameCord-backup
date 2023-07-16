package org.eclipse.sisu.space;

import java.net.URL;

public interface SpaceVisitor {
  void enterSpace(ClassSpace paramClassSpace);
  
  ClassVisitor visitClass(URL paramURL);
  
  void leaveSpace();
}
