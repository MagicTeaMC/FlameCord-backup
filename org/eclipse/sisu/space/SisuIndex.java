package org.eclipse.sisu.space;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.sisu.inject.Logs;

public class SisuIndex extends AbstractSisuIndex implements SpaceVisitor, ClassVisitor {
  private final QualifierCache qualifierCache = new QualifierCache();
  
  private final File targetDirectory;
  
  private ClassSpace space;
  
  private String clazzName;
  
  public SisuIndex(File targetDirectory) {
    this.targetDirectory = targetDirectory;
  }
  
  public static void main(String[] args) {
    List<URL> indexPath = new ArrayList<URL>(args.length);
    byte b;
    int i;
    String[] arrayOfString;
    for (i = (arrayOfString = args).length, b = 0; b < i; ) {
      String path = arrayOfString[b];
      try {
        indexPath.add((new File(path)).toURI().toURL());
      } catch (MalformedURLException e) {
        Logs.warn("Bad classpath element: {}", path, e);
      } 
      b++;
    } 
    ClassLoader parent = SisuIndex.class.getClassLoader();
    URL[] urls = indexPath.<URL>toArray(new URL[indexPath.size()]);
    ClassLoader loader = (urls.length > 0) ? URLClassLoader.newInstance(urls, parent) : parent;
    (new SisuIndex(new File("."))).index(new URLClassSpace(loader));
  }
  
  public final void index(ClassSpace _space) {
    try {
      (new SpaceScanner(_space)).accept(this);
    } finally {
      flushIndex();
    } 
  }
  
  public final void enterSpace(ClassSpace _space) {
    this.space = _space;
  }
  
  public final ClassVisitor visitClass(URL url) {
    return this;
  }
  
  public final void enterClass(int modifiers, String name, String _extends, String[] _implements) {
    if ((modifiers & 0x7600) == 0)
      this.clazzName = name; 
  }
  
  public final AnnotationVisitor visitAnnotation(String desc) {
    if (this.clazzName != null && this.qualifierCache.qualify(this.space, desc))
      addClassToIndex("javax.inject.Named", this.clazzName.replace('/', '.')); 
    return null;
  }
  
  public final void leaveClass() {
    this.clazzName = null;
  }
  
  public final void leaveSpace() {
    this.space = null;
  }
  
  protected void info(String message) {
    System.out.println("[INFO] " + message);
  }
  
  protected void warn(String message) {
    System.out.println("[WARN] " + message);
  }
  
  protected Reader getReader(String path) throws IOException {
    return new InputStreamReader(new FileInputStream(new File(this.targetDirectory, path)), "UTF-8");
  }
  
  protected Writer getWriter(String path) throws IOException {
    File index = new File(this.targetDirectory, path);
    File parent = index.getParentFile();
    if (parent.isDirectory() || parent.mkdirs())
      return new OutputStreamWriter(new FileOutputStream(index), "UTF-8"); 
    throw new IOException("Error creating: " + parent);
  }
}
