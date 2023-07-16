package org.eclipse.sisu.space;

import java.security.AccessController;
import java.security.PrivilegedAction;
import org.eclipse.sisu.inject.DeferredClass;
import org.eclipse.sisu.space.asm.ClassWriter;
import org.eclipse.sisu.space.asm.MethodVisitor;

public final class CloningClassSpace extends URLClassSpace {
  private static final String CLONE_MARKER = "$__sisu";
  
  private int cloneCount;
  
  public CloningClassSpace(ClassSpace parent) {
    super(AccessController.<ClassLoader>doPrivileged(new PrivilegedAction<ClassLoader>(parent) {
            public ClassLoader run() {
              return new CloningClassSpace.CloningClassLoader(parent);
            }
          },  ), null);
  }
  
  public DeferredClass<?> cloneClass(String name) {
    StringBuilder buf = new StringBuilder();
    if (name.startsWith("java"))
      buf.append('$'); 
    return deferLoadClass(buf.append(name).append("$__sisu").append(++this.cloneCount).toString());
  }
  
  public static String originalName(String proxyName) {
    int cloneMarker = proxyName.lastIndexOf("$__sisu");
    if (cloneMarker < 0)
      return proxyName; 
    for (int i = cloneMarker + "$__sisu".length(), end = proxyName.length(); i < end; i++) {
      char c = proxyName.charAt(i);
      if (c < '0' || c > '9')
        return proxyName; 
    } 
    return proxyName.substring(('$' == proxyName.charAt(0)) ? 1 : 0, cloneMarker);
  }
  
  private static final class CloningClassLoader extends ClassLoader {
    private final ClassSpace parent;
    
    CloningClassLoader(ClassSpace parent) {
      this.parent = parent;
    }
    
    public String toString() {
      return this.parent.toString();
    }
    
    protected synchronized Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
      if (!name.contains("$__sisu"))
        try {
          return this.parent.loadClass(name);
        } catch (TypeNotPresentException typeNotPresentException) {
          throw new ClassNotFoundException(name);
        }  
      return super.loadClass(name, resolve);
    }
    
    protected Class<?> findClass(String name) throws ClassNotFoundException {
      String proxyName = name.replace('.', '/');
      String superName = CloningClassSpace.originalName(proxyName);
      if (superName.equals(proxyName))
        throw new ClassNotFoundException(name); 
      ClassWriter cw = new ClassWriter(0);
      cw.visit(50, 1, proxyName, null, superName, null);
      MethodVisitor mv = cw.visitMethod(1, "<init>", "()V", null, null);
      mv.visitCode();
      mv.visitVarInsn(25, 0);
      mv.visitMethodInsn(183, superName, "<init>", "()V", false);
      mv.visitInsn(177);
      mv.visitMaxs(1, 1);
      mv.visitEnd();
      cw.visitEnd();
      byte[] buf = cw.toByteArray();
      return defineClass(name, buf, 0, buf.length);
    }
  }
}
