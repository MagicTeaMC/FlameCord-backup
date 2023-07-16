package org.eclipse.sisu.wire;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Provider;
import org.eclipse.sisu.space.asm.ClassWriter;
import org.eclipse.sisu.space.asm.Label;
import org.eclipse.sisu.space.asm.MethodVisitor;
import org.eclipse.sisu.space.asm.Type;

final class DynamicGlue {
  private static final String PROVIDER_NAME = Type.getInternalName(Provider.class);
  
  private static final String PROVIDER_DESC = Type.getDescriptor(Provider.class);
  
  private static final String PROVIDER_HANDLE = "__sisu__";
  
  private static final String OBJECT_NAME = Type.getInternalName(Object.class);
  
  private static final String OBJECT_DESC = Type.getDescriptor(Object.class);
  
  private static final String ILLEGAL_STATE_NAME = Type.getInternalName(IllegalStateException.class);
  
  private static final Map<String, Method> OBJECT_METHOD_MAP = new HashMap<String, Method>();
  
  static {
    byte b;
    int i;
    Method[] arrayOfMethod;
    for (i = (arrayOfMethod = Object.class.getMethods()).length, b = 0; b < i; ) {
      Method m = arrayOfMethod[b];
      if (isWrappable(m))
        OBJECT_METHOD_MAP.put(signatureKey(m), m); 
      b++;
    } 
  }
  
  public static byte[] generateProxyClass(String proxyName, Class<?> facade) {
    String superName, apiNames[], facadeName = Type.getInternalName(facade);
    if (facade.isInterface()) {
      superName = OBJECT_NAME;
      apiNames = new String[] { facadeName };
    } else {
      superName = facadeName;
      apiNames = getInternalNames(facade.getInterfaces());
    } 
    ClassWriter cw = new ClassWriter(1);
    cw.visit(50, 17, proxyName, null, superName, apiNames);
    init(cw, superName, proxyName);
    for (Method m : getWrappableMethods(facade))
      wrap(cw, proxyName, m); 
    cw.visitEnd();
    return cw.toByteArray();
  }
  
  private static void init(ClassWriter cw, String superName, String proxyName) {
    cw.visitField(18, "__sisu__", PROVIDER_DESC, null, null).visitEnd();
    MethodVisitor v = cw.visitMethod(1, "<init>", String.valueOf('(') + PROVIDER_DESC + ")V", null, null);
    v.visitCode();
    v.visitVarInsn(25, 0);
    v.visitInsn(89);
    v.visitVarInsn(25, 1);
    v.visitFieldInsn(181, proxyName, "__sisu__", PROVIDER_DESC);
    v.visitMethodInsn(183, superName, "<init>", "()V", false);
    v.visitInsn(177);
    v.visitMaxs(0, 0);
    v.visitEnd();
  }
  
  private static void wrap(ClassWriter cw, String proxyName, Method method) {
    String methodName = method.getName();
    String descriptor = Type.getMethodDescriptor(method);
    String[] exceptions = getInternalNames(method.getExceptionTypes());
    Label handleNullTarget = new Label();
    int modifiers = method.getModifiers() & 0xFFFFFADF;
    MethodVisitor v = cw.visitMethod(modifiers, methodName, descriptor, null, exceptions);
    v.visitCode();
    Class<?> declaringClazz = method.getDeclaringClass();
    String declaringName = Type.getInternalName(declaringClazz);
    boolean isObjectMethod = OBJECT_METHOD_MAP.containsKey(signatureKey(method));
    if (!isObjectMethod || "toString".equals(methodName)) {
      v.visitVarInsn(25, 0);
      v.visitFieldInsn(180, proxyName, "__sisu__", PROVIDER_DESC);
      v.visitMethodInsn(185, PROVIDER_NAME, "get", "()" + OBJECT_DESC, true);
      v.visitInsn(89);
      v.visitJumpInsn(198, handleNullTarget);
      boolean isInterface = declaringClazz.isInterface();
      if (!isInterface && Object.class != declaringClazz)
        v.visitTypeInsn(192, declaringName); 
      int slot = 1;
      byte b;
      int i;
      Type[] arrayOfType;
      for (i = (arrayOfType = Type.getArgumentTypes(method)).length, b = 0; b < i; ) {
        Type t = arrayOfType[b];
        v.visitVarInsn(t.getOpcode(21), slot);
        slot += t.getSize();
        b++;
      } 
      int invoke = isInterface ? 185 : 182;
      v.visitMethodInsn(invoke, declaringName, methodName, descriptor, isInterface);
      v.visitInsn(Type.getReturnType(method).getOpcode(172));
      v.visitLabel(handleNullTarget);
      v.visitInsn(87);
      if (!isObjectMethod) {
        v.visitTypeInsn(187, ILLEGAL_STATE_NAME);
        v.visitInsn(89);
        v.visitMethodInsn(183, ILLEGAL_STATE_NAME, "<init>", "()V", false);
        v.visitInsn(191);
      } 
    } 
    if (isObjectMethod) {
      v.visitVarInsn(25, 0);
      int slot = 1;
      byte b;
      int i;
      Type[] arrayOfType;
      for (i = (arrayOfType = Type.getArgumentTypes(method)).length, b = 0; b < i; ) {
        Type t = arrayOfType[b];
        v.visitVarInsn(t.getOpcode(21), slot);
        slot += t.getSize();
        b++;
      } 
      v.visitMethodInsn(183, declaringName, methodName, descriptor, false);
      v.visitInsn(Type.getReturnType(method).getOpcode(172));
    } 
    v.visitMaxs(0, 0);
    v.visitEnd();
  }
  
  private static String[] getInternalNames(Class... clazzes) {
    String[] names = new String[clazzes.length];
    for (int i = 0; i < names.length; i++)
      names[i] = Type.getInternalName(clazzes[i]); 
    return names;
  }
  
  private static Collection<Method> getWrappableMethods(Class<?> clazz) {
    Map<String, Method> methodMap = new HashMap<String, Method>(OBJECT_METHOD_MAP);
    byte b;
    int i;
    Method[] arrayOfMethod;
    for (i = (arrayOfMethod = clazz.getMethods()).length, b = 0; b < i; ) {
      Method m = arrayOfMethod[b];
      if (isWrappable(m))
        methodMap.put(signatureKey(m), m); 
      b++;
    } 
    return methodMap.values();
  }
  
  private static boolean isWrappable(Method method) {
    return ((method.getModifiers() & 0x18) == 0);
  }
  
  private static String signatureKey(Method method) {
    StringBuilder buf = new StringBuilder(method.getName());
    byte b;
    int i;
    Class[] arrayOfClass;
    for (i = (arrayOfClass = method.getParameterTypes()).length, b = 0; b < i; ) {
      Class<?> t = arrayOfClass[b];
      buf.append(':').append(t);
      b++;
    } 
    return buf.toString();
  }
}
