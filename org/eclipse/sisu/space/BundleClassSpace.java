package org.eclipse.sisu.space;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.eclipse.sisu.inject.DeferredClass;
import org.osgi.framework.Bundle;

public final class BundleClassSpace implements ClassSpace {
  private static final URL[] NO_URLS = new URL[0];
  
  private static final Enumeration<URL> NO_ENTRIES = Collections.enumeration(Collections.emptySet());
  
  private final Bundle bundle;
  
  private URL[] bundleClassPath;
  
  public BundleClassSpace(Bundle bundle) {
    this.bundle = bundle;
  }
  
  public Class<?> loadClass(String name) {
    try {
      return this.bundle.loadClass(name);
    } catch (Exception e) {
      throw new TypeNotPresentException(name, e);
    } catch (LinkageError e) {
      throw new TypeNotPresentException(name, e);
    } 
  }
  
  public DeferredClass<?> deferLoadClass(String name) {
    return new NamedClass(this, name);
  }
  
  public URL getResource(String name) {
    return this.bundle.getResource(name);
  }
  
  public Enumeration<URL> getResources(String name) {
    try {
      Enumeration<URL> resources = this.bundle.getResources(name);
      return (resources != null) ? resources : NO_ENTRIES;
    } catch (IOException iOException) {
      return NO_ENTRIES;
    } 
  }
  
  public Enumeration<URL> findEntries(String path, String glob, boolean recurse) {
    URL[] classPath = getBundleClassPath();
    Enumeration<URL> entries = this.bundle.findEntries((path != null) ? path : "/", glob, recurse);
    if (classPath.length > 0)
      return new ChainedEnumeration<URL>((Enumeration<URL>[])new Enumeration[] { entries, new ResourceEnumeration(path, glob, recurse, classPath) }); 
    return (entries != null) ? entries : NO_ENTRIES;
  }
  
  public Bundle getBundle() {
    return this.bundle;
  }
  
  public int hashCode() {
    return this.bundle.hashCode();
  }
  
  public boolean equals(Object rhs) {
    if (this == rhs)
      return true; 
    if (rhs instanceof BundleClassSpace)
      return this.bundle.equals(((BundleClassSpace)rhs).bundle); 
    return false;
  }
  
  public String toString() {
    return this.bundle.toString();
  }
  
  private synchronized URL[] getBundleClassPath() {
    if (this.bundleClassPath == null) {
      String path = (String)this.bundle.getHeaders().get("Bundle-ClassPath");
      if (path == null) {
        this.bundleClassPath = NO_URLS;
      } else {
        List<URL> classPath = new ArrayList<URL>();
        Set<String> visited = new HashSet<String>();
        visited.add(".");
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = path.trim().split("\\s*,\\s*")).length, b = 0; b < i; ) {
          String entry = arrayOfString[b];
          if (visited.add(entry)) {
            URL url = this.bundle.getEntry(entry);
            if (url != null)
              classPath.add(url); 
          } 
          b++;
        } 
        this.bundleClassPath = classPath.isEmpty() ? NO_URLS : classPath.<URL>toArray(new URL[classPath.size()]);
      } 
    } 
    return this.bundleClassPath;
  }
  
  private static final class ChainedEnumeration<T> implements Enumeration<T> {
    private final Enumeration<T>[] enumerations;
    
    private int index;
    
    ChainedEnumeration(Enumeration... enumerations) {
      this.enumerations = (Enumeration<T>[])enumerations;
    }
    
    public boolean hasMoreElements() {
      for (; this.index < this.enumerations.length; this.index++) {
        if (this.enumerations[this.index] != null && this.enumerations[this.index].hasMoreElements())
          return true; 
      } 
      return false;
    }
    
    public T nextElement() {
      if (hasMoreElements())
        return this.enumerations[this.index].nextElement(); 
      throw new NoSuchElementException();
    }
  }
}
