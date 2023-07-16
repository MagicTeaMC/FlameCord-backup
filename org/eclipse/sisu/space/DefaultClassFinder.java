package org.eclipse.sisu.space;

import java.net.URL;
import java.util.Enumeration;

public final class DefaultClassFinder implements ClassFinder {
  private final String path;
  
  private final boolean recurse;
  
  public DefaultClassFinder(String pkg) {
    String tempPath = pkg.replace('.', '/');
    if (tempPath.endsWith("*")) {
      this.path = tempPath.substring(0, tempPath.length() - 1);
      this.recurse = true;
    } else {
      this.path = tempPath;
      this.recurse = false;
    } 
  }
  
  public DefaultClassFinder() {
    this.path = null;
    this.recurse = true;
  }
  
  public Enumeration<URL> findClasses(ClassSpace space) {
    return space.findEntries(this.path, "*.class", this.recurse);
  }
}
