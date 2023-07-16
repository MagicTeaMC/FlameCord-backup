package org.eclipse.sisu.inject;

import com.google.inject.ImplementedBy;
import com.google.inject.Key;
import com.google.inject.ProvidedBy;
import com.google.inject.TypeLiteral;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import javax.inject.Qualifier;

public final class TypeArguments {
  private static final TypeLiteral<Object> OBJECT_TYPE_LITERAL = TypeLiteral.get(Object.class);
  
  private static final TypeLiteral<?>[] NO_TYPE_LITERALS = (TypeLiteral<?>[])new TypeLiteral[0];
  
  public static TypeLiteral<?>[] get(TypeLiteral<?> typeLiteral) {
    Type type = typeLiteral.getType();
    if (type instanceof ParameterizedType) {
      Type[] argumentTypes = ((ParameterizedType)type).getActualTypeArguments();
      TypeLiteral[] argumentLiterals = new TypeLiteral[argumentTypes.length];
      for (int i = 0; i < argumentTypes.length; i++)
        argumentLiterals[i] = expand(argumentTypes[i]); 
      return (TypeLiteral<?>[])argumentLiterals;
    } 
    if (type instanceof GenericArrayType)
      return (TypeLiteral<?>[])new TypeLiteral[] { expand(((GenericArrayType)type).getGenericComponentType()) }; 
    return NO_TYPE_LITERALS;
  }
  
  public static TypeLiteral<?> get(TypeLiteral<?> typeLiteral, int index) {
    Type type = typeLiteral.getType();
    if (type instanceof ParameterizedType)
      return expand(((ParameterizedType)type).getActualTypeArguments()[index]); 
    if (type instanceof GenericArrayType) {
      if (index == 0)
        return expand(((GenericArrayType)type).getGenericComponentType()); 
      throw new ArrayIndexOutOfBoundsException(index);
    } 
    return OBJECT_TYPE_LITERAL;
  }
  
  public static boolean isAssignableFrom(TypeLiteral<?> superLiteral, TypeLiteral<?> subLiteral) {
    Class<?> superClazz = superLiteral.getRawType();
    if (!superClazz.isAssignableFrom(subLiteral.getRawType()))
      return false; 
    Type superType = superLiteral.getType();
    if (superClazz == superType)
      return true; 
    if (superType instanceof ParameterizedType) {
      Type resolvedType = subLiteral.getSupertype(superClazz).getType();
      if (resolvedType instanceof ParameterizedType) {
        Type[] superArgs = ((ParameterizedType)superType).getActualTypeArguments();
        Type[] subArgs = ((ParameterizedType)resolvedType).getActualTypeArguments();
        return isAssignableFrom(superArgs, subArgs);
      } 
    } else if (superType instanceof GenericArrayType) {
      Type resolvedType = subLiteral.getSupertype(superClazz).getType();
      if (resolvedType instanceof GenericArrayType) {
        Type superComponent = ((GenericArrayType)superType).getGenericComponentType();
        Type subComponent = ((GenericArrayType)resolvedType).getGenericComponentType();
        return isAssignableFrom(new Type[] { superComponent }, new Type[] { subComponent });
      } 
    } 
    return false;
  }
  
  public static boolean isConcrete(TypeLiteral<?> literal) {
    return isConcrete(literal.getRawType());
  }
  
  public static boolean isConcrete(Class<?> clazz) {
    return (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers()));
  }
  
  public static boolean isImplicit(TypeLiteral<?> literal) {
    return isImplicit(literal.getRawType());
  }
  
  public static boolean isImplicit(Class<?> clazz) {
    return !(!isConcrete(clazz) && !clazz.isAnnotationPresent((Class)ImplementedBy.class) && 
      !clazz.isAnnotationPresent((Class)ProvidedBy.class));
  }
  
  public static <T> Key<T> implicitKey(Class<T> clazz) {
    return Key.get(clazz, Implicit.class);
  }
  
  private static TypeLiteral<?> expand(Type type) {
    if (type instanceof WildcardType)
      return TypeLiteral.get(((WildcardType)type).getUpperBounds()[0]); 
    if (type instanceof TypeVariable)
      return TypeLiteral.get(((TypeVariable)type).getBounds()[0]); 
    return TypeLiteral.get(type);
  }
  
  private static boolean isAssignableFrom(Type[] superArgs, Type[] subArgs) {
    for (int i = 0, len = Math.min(superArgs.length, subArgs.length); i < len; i++) {
      Type superType = superArgs[i];
      Type subType = subArgs[i];
      if (!(subType instanceof TypeVariable) || !isAssignableFrom(expand(subType), expand(superType)))
        if (superType instanceof WildcardType || superType instanceof TypeVariable) {
          if (!isAssignableFrom(expand(superType), expand(subType)))
            return false; 
        } else if (!superType.equals(subType)) {
          return false;
        }  
    } 
    return true;
  }
  
  @Qualifier
  @Retention(RetentionPolicy.RUNTIME)
  private static @interface Implicit {}
}
