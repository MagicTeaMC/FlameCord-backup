package org.eclipse.sisu.space;

public interface AnnotationVisitor {
  void enterAnnotation();
  
  void visitElement(String paramString, Object paramObject);
  
  void leaveAnnotation();
}
