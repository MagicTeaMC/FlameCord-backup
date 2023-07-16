package org.eclipse.sisu.space;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.sisu.inject.Logs;

public final class IndexedClassFinder implements ClassFinder {
  private static final Pattern LINE_PATTERN = Pattern.compile("\\s*([^#\\s]+).*");
  
  private final String localPath;
  
  private final String indexName;
  
  public IndexedClassFinder(String name, boolean global) {
    if (global) {
      this.localPath = null;
      this.indexName = name;
    } else {
      int i = name.lastIndexOf('/') + 1;
      this.localPath = name.substring(0, i);
      this.indexName = name.substring(i);
    } 
  }
  
  public Iterable<String> indexedNames(ClassSpace space) {
    Enumeration<URL> indices;
    if (this.localPath == null) {
      indices = space.getResources(this.indexName);
    } else {
      indices = space.findEntries(this.localPath, this.indexName, false);
    } 
    Set<String> names = new LinkedHashSet<String>();
    while (indices.hasMoreElements()) {
      URL url = indices.nextElement();
      try {
        BufferedReader reader = 
          new BufferedReader(new InputStreamReader(Streams.open(url), "UTF-8"));
        try {
          for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            Matcher m = LINE_PATTERN.matcher(line);
            if (m.matches())
              names.add(m.group(1)); 
          } 
        } finally {
          reader.close();
        } 
      } catch (IOException e) {
        Logs.warn("Problem reading: {}", url, e);
      } 
    } 
    return names;
  }
  
  public Enumeration<URL> findClasses(final ClassSpace space) {
    final Iterator<String> itr = indexedNames(space).iterator();
    return new Enumeration<URL>() {
        private URL nextURL;
        
        public boolean hasMoreElements() {
          while (this.nextURL == null && itr.hasNext())
            this.nextURL = space.getResource(String.valueOf(((String)itr.next()).replace('.', '/')) + ".class"); 
          return (this.nextURL != null);
        }
        
        public URL nextElement() {
          if (hasMoreElements()) {
            URL tempURL = this.nextURL;
            this.nextURL = null;
            return tempURL;
          } 
          throw new NoSuchElementException();
        }
      };
  }
}
