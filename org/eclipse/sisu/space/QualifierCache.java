package org.eclipse.sisu.space;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

final class QualifierCache implements ClassVisitor {
  private static final String QUALIFIER_DESC = "Ljavax/inject/Qualifier;";
  
  private static final String NAMED_DESC = "Ljavax/inject/Named;";
  
  private static final Map<String, Boolean> cachedResults = new ConcurrentHashMap<String, Boolean>(32, 0.75F, 1);
  
  private boolean isQualified;
  
  public void enterClass(int modifiers, String name, String _extends, String[] _implements) {}
  
  public AnnotationVisitor visitAnnotation(String desc) {
    this.isQualified |= "Ljavax/inject/Qualifier;".equals(desc);
    return null;
  }
  
  public void leaveClass() {}
  
  boolean qualify(ClassSpace space, String desc) {
    if ("Ljavax/inject/Named;".equals(desc))
      return true; 
    Boolean result = cachedResults.get(desc);
    if (result == null) {
      this.isQualified = false;
      String name = desc.substring(1, desc.length() - 1);
      SpaceScanner.accept(this, space.getResource(String.valueOf(name) + ".class"));
      cachedResults.put(desc, Boolean.valueOf(this.isQualified));
      return this.isQualified;
    } 
    return result.booleanValue();
  }
}
