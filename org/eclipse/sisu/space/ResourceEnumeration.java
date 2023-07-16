package org.eclipse.sisu.space;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

final class ResourceEnumeration implements Enumeration<URL> {
  private static final Iterator<String> NO_ENTRIES = Collections.<String>emptySet().iterator();
  
  private final URL[] urls;
  
  private final String subPath;
  
  private final GlobberStrategy globber;
  
  private final Object globPattern;
  
  private final boolean recurse;
  
  private int index;
  
  private URL currentURL;
  
  private boolean isFolder;
  
  private Iterator<String> entryNames = NO_ENTRIES;
  
  private String nextEntryName;
  
  ResourceEnumeration(String subPath, String glob, boolean recurse, URL[] urls) {
    this.subPath = normalizeSearchPath(subPath);
    this.globber = GlobberStrategy.selectFor(glob);
    this.globPattern = this.globber.compile(glob);
    this.recurse = recurse;
    this.urls = urls;
  }
  
  public boolean hasMoreElements() {
    while (this.nextEntryName == null) {
      if (this.entryNames.hasNext()) {
        String name = this.entryNames.next();
        if (matchesRequest(name))
          this.nextEntryName = name; 
        continue;
      } 
      if (this.index < this.urls.length) {
        this.currentURL = this.urls[this.index++];
        this.entryNames = scan(this.currentURL);
        continue;
      } 
      return false;
    } 
    return true;
  }
  
  public URL nextElement() {
    if (hasMoreElements()) {
      String name = this.nextEntryName;
      this.nextEntryName = null;
      try {
        return findResource(name);
      } catch (MalformedURLException e) {
        throw new IllegalStateException(e.toString());
      } 
    } 
    throw new NoSuchElementException();
  }
  
  static String normalizeSearchPath(String path) {
    if (path == null || "/".equals(path))
      return ""; 
    boolean echoSlash = false;
    StringBuilder buf = new StringBuilder();
    for (int i = 0, length = path.length(); i < length; i++) {
      char c = path.charAt(i);
      boolean isNotSlash = ('/' != c);
      if (echoSlash || isNotSlash) {
        echoSlash = isNotSlash;
        buf.append(c);
      } 
    } 
    if (echoSlash)
      buf.append('/'); 
    return buf.toString();
  }
  
  private Iterator<String> scan(URL url) {
    this.isFolder = url.getPath().endsWith("/");
    if (this.globber == GlobberStrategy.EXACT && !this.recurse) {
      try {
        this.nextEntryName = String.valueOf(this.subPath) + this.globPattern;
        Streams.open(findResource(this.nextEntryName)).close();
      } catch (Exception exception) {
        this.nextEntryName = null;
      } 
      return NO_ENTRIES;
    } 
    return this.isFolder ? new FileEntryIterator(url, this.subPath, this.recurse) : new ZipEntryIterator(url);
  }
  
  private URL findResource(String name) throws MalformedURLException {
    if (this.isFolder)
      return new URL(this.currentURL, name); 
    if ("jar".equals(this.currentURL.getProtocol()))
      return new URL(this.currentURL, "#" + name, new NestedJarHandler()); 
    return new URL("jar:" + this.currentURL + "!/" + name);
  }
  
  private boolean matchesRequest(String entryName) {
    if (entryName.endsWith("/") || !entryName.startsWith(this.subPath))
      return false; 
    if (!this.recurse && entryName.indexOf('/', this.subPath.length()) > 0)
      return false; 
    return this.globber.matches(this.globPattern, entryName);
  }
  
  static final class NestedJarHandler extends URLStreamHandler {
    protected URLConnection openConnection(URL url) {
      return new ResourceEnumeration.NestedJarConnection(url);
    }
  }
  
  static final class NestedJarConnection extends URLConnection {
    NestedJarConnection(URL url) {
      super(url);
    }
    
    public void connect() {}
    
    public InputStream getInputStream() throws IOException {
      URL containingURL = new URL("jar", null, -1, this.url.getFile());
      ZipInputStream is = new ZipInputStream(Streams.open(containingURL));
      String entryName = this.url.getRef();
      for (ZipEntry entry = is.getNextEntry(); entry != null; entry = is.getNextEntry()) {
        if (entryName.equals(entry.getName()))
          return is; 
      } 
      throw new ZipException("No such entry: " + entryName + " in: " + containingURL);
    }
  }
}
