package org.eclipse.sisu.space;

import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.Enumeration;
import org.eclipse.sisu.inject.Logs;
import org.eclipse.sisu.space.asm.AnnotationVisitor;
import org.eclipse.sisu.space.asm.ClassReader;
import org.eclipse.sisu.space.asm.ClassVisitor;
import org.eclipse.sisu.space.asm.Type;

public final class SpaceScanner {
  private static final int ASM_FLAGS = 7;
  
  static final ClassFinder DEFAULT_FINDER = new DefaultClassFinder();
  
  private final ClassSpace space;
  
  private final ClassFinder finder;
  
  public SpaceScanner(ClassSpace space, ClassFinder finder) {
    this.space = space;
    this.finder = finder;
  }
  
  public SpaceScanner(ClassSpace space) {
    this(space, DEFAULT_FINDER);
  }
  
  public void accept(SpaceVisitor visitor) {
    visitor.enterSpace(this.space);
    for (Enumeration<URL> result = this.finder.findClasses(this.space); result.hasMoreElements(); ) {
      URL url = result.nextElement();
      ClassVisitor cv = visitor.visitClass(url);
      if (cv != null)
        accept(cv, url); 
    } 
    visitor.leaveSpace();
  }
  
  public static void accept(ClassVisitor visitor, URL url) {
    if (url == null)
      return; 
    try {
      InputStream in = Streams.open(url);
      try {
        (new ClassReader(in)).accept(adapt(visitor), 7);
      } finally {
        in.close();
      } 
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
    
    } catch (Exception e) {
      Logs.trace("Problem scanning: {}", url, e);
    } 
  }
  
  public static String jvmDescriptor(Class<? extends Annotation> clazz) {
    return String.valueOf('L') + clazz.getName().replace('.', '/') + ';';
  }
  
  private static ClassVisitor adapt(final ClassVisitor _cv) {
    return (_cv == null) ? null : new ClassVisitor(327680) {
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
          _cv.enterClass(access, name, superName, interfaces);
        }
        
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
          final AnnotationVisitor _av = _cv.visitAnnotation(desc);
          return (_av == null) ? null : new AnnotationVisitor(327680) {
              public void visit(String name, Object value) {
                _av.visitElement(name, (value instanceof Type) ? ((Type)value).getClassName() : value);
              }
              
              public void visitEnd() {
                _av.leaveAnnotation();
              }
            };
        }
        
        public void visitEnd() {
          _cv.leaveClass();
        }
      };
  }
}
