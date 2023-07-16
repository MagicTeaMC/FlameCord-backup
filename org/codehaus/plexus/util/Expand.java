package org.codehaus.plexus.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Expand {
  private File dest;
  
  private File source;
  
  private boolean overwrite = true;
  
  public void execute() throws Exception {
    expandFile(this.source, this.dest);
  }
  
  protected void expandFile(File srcF, File dir) throws Exception {
    ZipInputStream zis = null;
    try {
      zis = new ZipInputStream(new FileInputStream(srcF));
      for (ZipEntry ze = zis.getNextEntry(); ze != null; ze = zis.getNextEntry())
        extractFile(srcF, dir, zis, ze.getName(), new Date(ze.getTime()), ze.isDirectory()); 
      zis.close();
      zis = null;
    } catch (IOException ioe) {
      throw new Exception("Error while expanding " + srcF.getPath(), ioe);
    } finally {
      IOUtil.close(zis);
    } 
  }
  
  protected void extractFile(File srcF, File dir, InputStream compressedInputStream, String entryName, Date entryDate, boolean isDirectory) throws Exception {
    File f = FileUtils.resolveFile(dir, entryName);
    if (!f.getAbsolutePath().startsWith(dir.getAbsolutePath()))
      throw new IOException("Entry '" + entryName + "' outside the target directory."); 
    try {
      if (!this.overwrite && f.exists() && f.lastModified() >= entryDate.getTime())
        return; 
      File dirF = f.getParentFile();
      dirF.mkdirs();
      if (isDirectory) {
        f.mkdirs();
      } else {
        byte[] buffer = new byte[65536];
        FileOutputStream fos = null;
        try {
          fos = new FileOutputStream(f);
          int length = compressedInputStream.read(buffer);
          while (length >= 0) {
            fos.write(buffer, 0, length);
            length = compressedInputStream.read(buffer);
          } 
          fos.close();
          fos = null;
        } finally {
          IOUtil.close(fos);
        } 
      } 
      f.setLastModified(entryDate.getTime());
    } catch (FileNotFoundException ex) {
      throw new Exception("Can't extract file " + srcF.getPath(), ex);
    } 
  }
  
  public void setDest(File d) {
    this.dest = d;
  }
  
  public void setSrc(File s) {
    this.source = s;
  }
  
  public void setOverwrite(boolean b) {
    this.overwrite = b;
  }
}
