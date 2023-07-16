package org.eclipse.sisu.space;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

final class ZipEntryIterator implements Iterator<String> {
  private String[] entryNames;
  
  private int index;
  
  ZipEntryIterator(URL url) {
    try {
      if ("file".equals(url.getProtocol())) {
        this.entryNames = getEntryNames(new ZipFile(FileEntryIterator.toFile(url)));
      } else {
        this.entryNames = getEntryNames(new ZipInputStream(Streams.open(url)));
      } 
    } catch (IOException iOException) {
      this.entryNames = new String[0];
    } 
  }
  
  public boolean hasNext() {
    return (this.index < this.entryNames.length);
  }
  
  public String next() {
    return this.entryNames[this.index++];
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  private static String[] getEntryNames(ZipFile zipFile) throws IOException {
    try {
      String[] names = new String[zipFile.size()];
      Enumeration<? extends ZipEntry> e = zipFile.entries();
      for (int i = 0; i < names.length; i++)
        names[i] = ((ZipEntry)e.nextElement()).getName(); 
      return names;
    } finally {
      zipFile.close();
    } 
  }
  
  private static String[] getEntryNames(ZipInputStream zipStream) throws IOException {
    try {
      List<String> names = new ArrayList<String>(64);
      for (ZipEntry e = zipStream.getNextEntry(); e != null; e = zipStream.getNextEntry())
        names.add(e.getName()); 
      return names.<String>toArray(new String[names.size()]);
    } finally {
      zipStream.close();
    } 
  }
}
