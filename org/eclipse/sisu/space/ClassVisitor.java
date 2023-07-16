package org.eclipse.sisu.space;

public interface ClassVisitor {
  public static final int NON_INSTANTIABLE = 30208;
  
  void enterClass(int paramInt, String paramString1, String paramString2, String[] paramArrayOfString);
  
  AnnotationVisitor visitAnnotation(String paramString);
  
  void leaveClass();
}
