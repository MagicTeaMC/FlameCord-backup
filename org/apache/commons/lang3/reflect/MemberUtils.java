package org.apache.commons.lang3.reflect;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.apache.commons.lang3.ClassUtils;

abstract class MemberUtils {
  private static final int ACCESS_TEST = 7;
  
  private static final Class<?>[] ORDERED_PRIMITIVE_TYPES = new Class[] { byte.class, short.class, char.class, int.class, long.class, float.class, double.class };
  
  static boolean setAccessibleWorkaround(AccessibleObject o) {
    if (o == null || o.isAccessible())
      return false; 
    Member m = (Member)o;
    if (!o.isAccessible() && Modifier.isPublic(m.getModifiers()) && isPackageAccess(m.getDeclaringClass().getModifiers()))
      try {
        o.setAccessible(true);
        return true;
      } catch (SecurityException securityException) {} 
    return false;
  }
  
  static boolean isPackageAccess(int modifiers) {
    return ((modifiers & 0x7) == 0);
  }
  
  static boolean isAccessible(Member m) {
    return (m != null && Modifier.isPublic(m.getModifiers()) && !m.isSynthetic());
  }
  
  static int compareConstructorFit(Constructor<?> left, Constructor<?> right, Class<?>[] actual) {
    return compareParameterTypes(Executable.of(left), Executable.of(right), actual);
  }
  
  static int compareMethodFit(Method left, Method right, Class<?>[] actual) {
    return compareParameterTypes(Executable.of(left), Executable.of(right), actual);
  }
  
  private static int compareParameterTypes(Executable left, Executable right, Class<?>[] actual) {
    float leftCost = getTotalTransformationCost(actual, left);
    float rightCost = getTotalTransformationCost(actual, right);
    return (leftCost < rightCost) ? -1 : ((rightCost < leftCost) ? 1 : 0);
  }
  
  private static float getTotalTransformationCost(Class<?>[] srcArgs, Executable executable) {
    Class<?>[] destArgs = executable.getParameterTypes();
    boolean isVarArgs = executable.isVarArgs();
    float totalCost = 0.0F;
    long normalArgsLen = isVarArgs ? (destArgs.length - 1) : destArgs.length;
    if (srcArgs.length < normalArgsLen)
      return Float.MAX_VALUE; 
    for (int i = 0; i < normalArgsLen; i++)
      totalCost += getObjectTransformationCost(srcArgs[i], destArgs[i]); 
    if (isVarArgs) {
      boolean noVarArgsPassed = (srcArgs.length < destArgs.length);
      boolean explicitArrayForVarags = (srcArgs.length == destArgs.length && srcArgs[srcArgs.length - 1].isArray());
      float varArgsCost = 0.001F;
      Class<?> destClass = destArgs[destArgs.length - 1].getComponentType();
      if (noVarArgsPassed) {
        totalCost += getObjectTransformationCost(destClass, Object.class) + 0.001F;
      } else if (explicitArrayForVarags) {
        Class<?> sourceClass = srcArgs[srcArgs.length - 1].getComponentType();
        totalCost += getObjectTransformationCost(sourceClass, destClass) + 0.001F;
      } else {
        for (int j = destArgs.length - 1; j < srcArgs.length; j++) {
          Class<?> srcClass = srcArgs[j];
          totalCost += getObjectTransformationCost(srcClass, destClass) + 0.001F;
        } 
      } 
    } 
    return totalCost;
  }
  
  private static float getObjectTransformationCost(Class<?> srcClass, Class<?> destClass) {
    if (destClass.isPrimitive())
      return getPrimitivePromotionCost(srcClass, destClass); 
    float cost = 0.0F;
    while (srcClass != null && !destClass.equals(srcClass)) {
      if (destClass.isInterface() && ClassUtils.isAssignable(srcClass, destClass)) {
        cost += 0.25F;
        break;
      } 
      cost++;
      srcClass = srcClass.getSuperclass();
    } 
    if (srcClass == null)
      cost += 1.5F; 
    return cost;
  }
  
  private static float getPrimitivePromotionCost(Class<?> srcClass, Class<?> destClass) {
    float cost = 0.0F;
    Class<?> cls = srcClass;
    if (!cls.isPrimitive()) {
      cost += 0.1F;
      cls = ClassUtils.wrapperToPrimitive(cls);
    } 
    for (int i = 0; cls != destClass && i < ORDERED_PRIMITIVE_TYPES.length; i++) {
      if (cls == ORDERED_PRIMITIVE_TYPES[i]) {
        cost += 0.1F;
        if (i < ORDERED_PRIMITIVE_TYPES.length - 1)
          cls = ORDERED_PRIMITIVE_TYPES[i + 1]; 
      } 
    } 
    return cost;
  }
  
  static boolean isMatchingMethod(Method method, Class<?>[] parameterTypes) {
    return isMatchingExecutable(Executable.of(method), parameterTypes);
  }
  
  static boolean isMatchingConstructor(Constructor<?> method, Class<?>[] parameterTypes) {
    return isMatchingExecutable(Executable.of(method), parameterTypes);
  }
  
  private static boolean isMatchingExecutable(Executable method, Class<?>[] parameterTypes) {
    Class<?>[] methodParameterTypes = method.getParameterTypes();
    if (ClassUtils.isAssignable(parameterTypes, methodParameterTypes, true))
      return true; 
    if (method.isVarArgs()) {
      int i;
      for (i = 0; i < methodParameterTypes.length - 1 && i < parameterTypes.length; i++) {
        if (!ClassUtils.isAssignable(parameterTypes[i], methodParameterTypes[i], true))
          return false; 
      } 
      Class<?> varArgParameterType = methodParameterTypes[methodParameterTypes.length - 1].getComponentType();
      for (; i < parameterTypes.length; i++) {
        if (!ClassUtils.isAssignable(parameterTypes[i], varArgParameterType, true))
          return false; 
      } 
      return true;
    } 
    return false;
  }
  
  private static final class Executable {
    private final Class<?>[] parameterTypes;
    
    private final boolean isVarArgs;
    
    private static Executable of(Method method) {
      return new Executable(method);
    }
    
    private static Executable of(Constructor<?> constructor) {
      return new Executable(constructor);
    }
    
    private Executable(Method method) {
      this.parameterTypes = method.getParameterTypes();
      this.isVarArgs = method.isVarArgs();
    }
    
    private Executable(Constructor<?> constructor) {
      this.parameterTypes = constructor.getParameterTypes();
      this.isVarArgs = constructor.isVarArgs();
    }
    
    public Class<?>[] getParameterTypes() {
      return this.parameterTypes;
    }
    
    public boolean isVarArgs() {
      return this.isVarArgs;
    }
  }
}
