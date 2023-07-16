package org.eclipse.sisu.space;

import java.net.URL;
import java.util.Enumeration;
import org.eclipse.sisu.inject.DeferredClass;

public interface ClassSpace {
  Class<?> loadClass(String paramString) throws TypeNotPresentException;
  
  DeferredClass<?> deferLoadClass(String paramString);
  
  URL getResource(String paramString);
  
  Enumeration<URL> getResources(String paramString);
  
  Enumeration<URL> findEntries(String paramString1, String paramString2, boolean paramBoolean);
}
