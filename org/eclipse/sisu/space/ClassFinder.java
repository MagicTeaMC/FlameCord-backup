package org.eclipse.sisu.space;

import java.net.URL;
import java.util.Enumeration;

public interface ClassFinder {
  Enumeration<URL> findClasses(ClassSpace paramClassSpace);
}
