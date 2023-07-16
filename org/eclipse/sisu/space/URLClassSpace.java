package org.eclipse.sisu.space;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.eclipse.sisu.inject.DeferredClass;

public class URLClassSpace implements ClassSpace {
  private static final String MANIFEST_ENTRY = "META-INF/MANIFEST.MF";
  
  private static final URL[] NO_URLS;
  
  private static final Enumeration<URL> NO_ENTRIES;
  
  private static final String[] EMPTY_CLASSPATH;
  
  private static final ClassLoader SYSTEM_LOADER;
  
  private static final String SYSTEM_CLASSPATH;
  
  private final ClassLoader loader;
  
  private final String pathDetails;
  
  private URL[] classPath;
  
  static {
    ClassLoader systemLoader;
    String classPath;
    try {
      systemLoader = ClassLoader.getSystemClassLoader();
      classPath = System.getProperty("java.class.path", ".");
    } catch (RuntimeException runtimeException) {
      systemLoader = null;
      classPath = null;
    } catch (LinkageError linkageError) {
      systemLoader = null;
      classPath = null;
    } 
    SYSTEM_LOADER = systemLoader;
    SYSTEM_CLASSPATH = classPath;
    NO_URLS = new URL[0];
    NO_ENTRIES = Collections.enumeration(Collections.emptySet());
    EMPTY_CLASSPATH = new String[0];
  }
  
  public URLClassSpace(ClassLoader loader) {
    this.loader = loader;
    this.pathDetails = null;
  }
  
  public URLClassSpace(ClassLoader loader, URL[] path) {
    this.loader = loader;
    this.pathDetails = Arrays.toString((Object[])path);
    if (path != null && path.length > 0) {
      this.classPath = expandClassPath(path);
    } else {
      this.classPath = NO_URLS;
    } 
  }
  
  public final Class<?> loadClass(String name) {
    try {
      return this.loader.loadClass(name);
    } catch (Exception e) {
      throw new TypeNotPresentException(name, e);
    } catch (LinkageError e) {
      throw new TypeNotPresentException(name, e);
    } 
  }
  
  public final DeferredClass<?> deferLoadClass(String name) {
    return new NamedClass(this, name);
  }
  
  public final URL getResource(String name) {
    return this.loader.getResource(name);
  }
  
  public final Enumeration<URL> getResources(String name) {
    try {
      Enumeration<URL> resources = this.loader.getResources(name);
      return (resources != null) ? resources : NO_ENTRIES;
    } catch (IOException iOException) {
      return NO_ENTRIES;
    } 
  }
  
  public final Enumeration<URL> findEntries(String path, String glob, boolean recurse) {
    if (SYSTEM_LOADER != null && this.loader == SYSTEM_LOADER && this.pathDetails == null && 
      !recurse && glob != null && glob.indexOf('*') < 0)
      return getResources(String.valueOf(ResourceEnumeration.normalizeSearchPath(path)) + glob); 
    return new ResourceEnumeration(path, glob, recurse, getClassPath());
  }
  
  public final URL[] getURLs() {
    return (URL[])getClassPath().clone();
  }
  
  public final int hashCode() {
    return this.loader.hashCode();
  }
  
  public final boolean equals(Object rhs) {
    if (this == rhs)
      return true; 
    if (rhs instanceof URLClassSpace)
      return this.loader.equals(((URLClassSpace)rhs).loader); 
    return false;
  }
  
  public final String toString() {
    return (this.pathDetails == null) ? this.loader.toString() : (this.loader + "(" + this.pathDetails + ")");
  }
  
  private synchronized URL[] getClassPath() {
    if (this.classPath == null) {
      for (ClassLoader l = this.loader; l != null; l = l.getParent()) {
        if (l instanceof URLClassLoader) {
          URL[] path = ((URLClassLoader)l).getURLs();
          if (path != null && path.length > 0) {
            this.classPath = expandClassPath(path);
            break;
          } 
        } else if (SYSTEM_LOADER != null && l == SYSTEM_LOADER) {
          this.classPath = expandClassPath(getSystemClassPath());
          break;
        } 
      } 
      if (this.classPath == null)
        this.classPath = NO_URLS; 
    } 
    return this.classPath;
  }
  
  private static URL[] getSystemClassPath() {
    String[] paths = SYSTEM_CLASSPATH.split(File.pathSeparator);
    URL[] urls = new URL[paths.length];
    for (int i = 0; i < paths.length; i++) {
      try {
        urls[i] = (new File(paths[i])).toURI().toURL();
      } catch (MalformedURLException malformedURLException) {
        urls[i] = null;
      } 
    } 
    return urls;
  }
  
  private static URL[] expandClassPath(URL[] classPath) {
    List<URL> searchPath = new ArrayList<URL>();
    Collections.addAll(searchPath, classPath);
    List<URL> expandedPath = new ArrayList<URL>();
    Set<String> visited = new HashSet<String>();
    for (int i = 0; i < searchPath.size(); i++) {
      URL url = normalizeEntry(searchPath.get(i));
      if (url != null && visited.add(url.toString())) {
        String[] classPathEntries;
        expandedPath.add(url);
        try {
          classPathEntries = getClassPathEntries(url);
        } catch (IOException iOException) {}
        byte b;
        int j;
        String[] arrayOfString1;
        for (j = (arrayOfString1 = classPathEntries).length, b = 0; b < j; ) {
          String entry = arrayOfString1[b];
          try {
            searchPath.add(new URL(url, entry));
          } catch (MalformedURLException malformedURLException) {}
          b++;
        } 
      } 
    } 
    return expandedPath.<URL>toArray(new URL[expandedPath.size()]);
  }
  
  private static URL normalizeEntry(URL url) {
    if (url != null && "jar".equals(url.getProtocol())) {
      String path = url.getPath();
      if (path.endsWith("!/"))
        try {
          return new URL(path.substring(0, path.length() - 2));
        } catch (MalformedURLException e) {
          throw new IllegalStateException(e.toString());
        }  
    } 
    return url;
  }
  
  private static String[] getClassPathEntries(URL url) throws IOException {
    Manifest manifest;
    if (url.getPath().endsWith("/")) {
      InputStream in = Streams.open(new URL(url, "META-INF/MANIFEST.MF"));
      try {
        manifest = new Manifest(in);
      } finally {
        in.close();
      } 
    } else if ("file".equals(url.getProtocol())) {
      JarFile jf = new JarFile(FileEntryIterator.toFile(url));
      try {
        manifest = jf.getManifest();
      } finally {
        jf.close();
      } 
    } else {
      JarInputStream jin = new JarInputStream(Streams.open(url));
      try {
        manifest = jin.getManifest();
      } finally {
        jin.close();
      } 
    } 
    if (manifest != null) {
      String classPath = manifest.getMainAttributes().getValue("Class-Path");
      if (classPath != null)
        return classPath.split(" "); 
    } 
    return EMPTY_CLASSPATH;
  }
}
