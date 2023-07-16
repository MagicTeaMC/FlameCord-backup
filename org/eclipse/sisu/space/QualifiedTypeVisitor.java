package org.eclipse.sisu.space;

import com.google.inject.Module;
import java.net.URL;
import javax.inject.Qualifier;
import org.eclipse.sisu.inject.Logs;

public final class QualifiedTypeVisitor implements SpaceVisitor, ClassVisitor {
  private final QualifierCache qualifierCache = new QualifierCache();
  
  private final QualifiedTypeListener listener;
  
  private ClassSpace space;
  
  private URL location;
  
  private String source;
  
  private String clazzName;
  
  private boolean qualified;
  
  public QualifiedTypeVisitor(QualifiedTypeListener listener) {
    this.listener = listener;
  }
  
  public static boolean verify(ClassSpace space, Class... specification) {
    byte b;
    int i;
    Class[] arrayOfClass;
    for (i = (arrayOfClass = specification).length, b = 0; b < i; ) {
      Class<?> expectedClazz = arrayOfClass[b];
      try {
        Class<?> spaceClazz = space.loadClass(expectedClazz.getName());
        if (spaceClazz != expectedClazz) {
          Logs.warn("Inconsistent ClassLoader for: {} in: {}", expectedClazz, space);
          Logs.warn("Expected: {} saw: {}", expectedClazz.getClassLoader(), spaceClazz.getClassLoader());
        } 
      } catch (TypeNotPresentException typeNotPresentException) {
        if (expectedClazz.isAnnotation())
          Logs.trace("Potential problem: {} is not visible from: {}", expectedClazz, space); 
      } 
      b++;
    } 
    return true;
  }
  
  public void enterSpace(ClassSpace _space) {
    this.space = _space;
    this.source = null;
    if (Logs.TRACE_ENABLED)
      verify(_space, new Class[] { Qualifier.class, Module.class }); 
  }
  
  public ClassVisitor visitClass(URL url) {
    this.location = url;
    this.clazzName = null;
    this.qualified = false;
    return this;
  }
  
  public void enterClass(int modifiers, String name, String _extends, String[] _implements) {
    if ((modifiers & 0x7600) == 0)
      this.clazzName = name; 
  }
  
  public AnnotationVisitor visitAnnotation(String desc) {
    if (this.clazzName != null)
      this.qualified = !(!this.qualified && !this.qualifierCache.qualify(this.space, desc)); 
    return null;
  }
  
  public void disqualify() {
    this.qualified = false;
  }
  
  public void leaveClass() {
    if (this.qualified)
      this.listener.hear(this.space.loadClass(this.clazzName.replace('/', '.')), findSource()); 
  }
  
  public void leaveSpace() {}
  
  private String findSource() {
    if (this.location != null) {
      String path = this.location.getPath();
      if (this.source == null || !path.startsWith(this.source)) {
        int i = path.indexOf(this.clazzName);
        this.source = (i <= 0) ? path : path.substring(0, i);
      } 
    } else if (this.source == null) {
      this.source = this.space.toString();
    } 
    return this.source;
  }
}
