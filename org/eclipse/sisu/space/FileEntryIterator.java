package org.eclipse.sisu.space;

import java.io.File;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

final class FileEntryIterator implements Iterator<String> {
  private final String rootPath;
  
  private final Deque<String> entryNames = new ArrayDeque<String>();
  
  private final boolean recurse;
  
  FileEntryIterator(URL url, String subPath, boolean recurse) {
    this.rootPath = normalizePath(toFile(url).getAbsoluteFile());
    this.recurse = recurse;
    appendEntries(subPath);
  }
  
  public boolean hasNext() {
    return (this.entryNames.size() > 0);
  }
  
  public String next() {
    String name = this.entryNames.removeFirst();
    if (this.recurse && name.endsWith("/"))
      appendEntries(name); 
    return name;
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  static File toFile(URL url) {
    StringBuilder buf = new StringBuilder();
    String authority = url.getAuthority();
    if (authority != null && authority.length() > 0)
      buf.append(File.separatorChar).append(File.separatorChar).append(authority); 
    String path = url.getPath();
    int codePoint = 0, expectBytes = 0;
    for (int i = 0, length = path.length(); i < length; i++) {
      char c = path.charAt(i);
      if ('/' == c) {
        buf.append(File.separatorChar);
      } else if ('%' == c && i < length - 2) {
        int hi = Character.digit(path.charAt(i + 1), 16);
        int lo = Character.digit(path.charAt(i + 2), 16);
        if (hi >= 0 && lo >= 0) {
          if (hi < 8) {
            buf.append((char)(hi << 4 | lo));
          } else if (hi >= 12) {
            expectBytes = (12 == hi) ? 1 : (hi - 12);
            codePoint = ((13 == hi) ? (16 + lo) : lo) << 6 * expectBytes;
          } else if (expectBytes > 0) {
            codePoint |= ((0x3 & hi) << 4 | lo) << 6 * --expectBytes;
            if (expectBytes == 0)
              buf.appendCodePoint(codePoint); 
          } 
          i += 2;
        } else {
          buf.append('%');
        } 
      } else {
        buf.append(c);
      } 
    } 
    return new File(buf.toString());
  }
  
  private void appendEntries(String subPath) {
    File[] listing = (new File(String.valueOf(this.rootPath) + subPath)).listFiles();
    if (listing != null) {
      byte b;
      int i;
      File[] arrayOfFile;
      for (i = (arrayOfFile = listing).length, b = 0; b < i; ) {
        File f = arrayOfFile[b];
        this.entryNames.add(normalizePath(f).substring(this.rootPath.length()));
        b++;
      } 
    } 
  }
  
  private static String normalizePath(File file) {
    return file.toURI().getPath();
  }
}
