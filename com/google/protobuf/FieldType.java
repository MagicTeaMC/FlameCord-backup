package com.google.protobuf;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.List;

public enum FieldType {
  DOUBLE(0, Collection.SCALAR, JavaType.DOUBLE),
  FLOAT(1, Collection.SCALAR, JavaType.FLOAT),
  INT64(2, Collection.SCALAR, JavaType.LONG),
  UINT64(3, Collection.SCALAR, JavaType.LONG),
  INT32(4, Collection.SCALAR, JavaType.INT),
  FIXED64(5, Collection.SCALAR, JavaType.LONG),
  FIXED32(6, Collection.SCALAR, JavaType.INT),
  BOOL(7, Collection.SCALAR, JavaType.BOOLEAN),
  STRING(8, Collection.SCALAR, JavaType.STRING),
  MESSAGE(9, Collection.SCALAR, JavaType.MESSAGE),
  BYTES(10, Collection.SCALAR, JavaType.BYTE_STRING),
  UINT32(11, Collection.SCALAR, JavaType.INT),
  ENUM(12, Collection.SCALAR, JavaType.ENUM),
  SFIXED32(13, Collection.SCALAR, JavaType.INT),
  SFIXED64(14, Collection.SCALAR, JavaType.LONG),
  SINT32(15, Collection.SCALAR, JavaType.INT),
  SINT64(16, Collection.SCALAR, JavaType.LONG),
  GROUP(17, Collection.SCALAR, JavaType.MESSAGE),
  DOUBLE_LIST(18, Collection.VECTOR, JavaType.DOUBLE),
  FLOAT_LIST(19, Collection.VECTOR, JavaType.FLOAT),
  INT64_LIST(20, Collection.VECTOR, JavaType.LONG),
  UINT64_LIST(21, Collection.VECTOR, JavaType.LONG),
  INT32_LIST(22, Collection.VECTOR, JavaType.INT),
  FIXED64_LIST(23, Collection.VECTOR, JavaType.LONG),
  FIXED32_LIST(24, Collection.VECTOR, JavaType.INT),
  BOOL_LIST(25, Collection.VECTOR, JavaType.BOOLEAN),
  STRING_LIST(26, Collection.VECTOR, JavaType.STRING),
  MESSAGE_LIST(27, Collection.VECTOR, JavaType.MESSAGE),
  BYTES_LIST(28, Collection.VECTOR, JavaType.BYTE_STRING),
  UINT32_LIST(29, Collection.VECTOR, JavaType.INT),
  ENUM_LIST(30, Collection.VECTOR, JavaType.ENUM),
  SFIXED32_LIST(31, Collection.VECTOR, JavaType.INT),
  SFIXED64_LIST(32, Collection.VECTOR, JavaType.LONG),
  SINT32_LIST(33, Collection.VECTOR, JavaType.INT),
  SINT64_LIST(34, Collection.VECTOR, JavaType.LONG),
  DOUBLE_LIST_PACKED(35, Collection.PACKED_VECTOR, JavaType.DOUBLE),
  FLOAT_LIST_PACKED(36, Collection.PACKED_VECTOR, JavaType.FLOAT),
  INT64_LIST_PACKED(37, Collection.PACKED_VECTOR, JavaType.LONG),
  UINT64_LIST_PACKED(38, Collection.PACKED_VECTOR, JavaType.LONG),
  INT32_LIST_PACKED(39, Collection.PACKED_VECTOR, JavaType.INT),
  FIXED64_LIST_PACKED(40, Collection.PACKED_VECTOR, JavaType.LONG),
  FIXED32_LIST_PACKED(41, Collection.PACKED_VECTOR, JavaType.INT),
  BOOL_LIST_PACKED(42, Collection.PACKED_VECTOR, JavaType.BOOLEAN),
  UINT32_LIST_PACKED(43, Collection.PACKED_VECTOR, JavaType.INT),
  ENUM_LIST_PACKED(44, Collection.PACKED_VECTOR, JavaType.ENUM),
  SFIXED32_LIST_PACKED(45, Collection.PACKED_VECTOR, JavaType.INT),
  SFIXED64_LIST_PACKED(46, Collection.PACKED_VECTOR, JavaType.LONG),
  SINT32_LIST_PACKED(47, Collection.PACKED_VECTOR, JavaType.INT),
  SINT64_LIST_PACKED(48, Collection.PACKED_VECTOR, JavaType.LONG),
  GROUP_LIST(49, Collection.VECTOR, JavaType.MESSAGE),
  MAP(50, Collection.MAP, JavaType.VOID);
  
  private final JavaType javaType;
  
  private final int id;
  
  private final Collection collection;
  
  private final Class<?> elementType;
  
  private final boolean primitiveScalar;
  
  private static final FieldType[] VALUES;
  
  private static final Type[] EMPTY_TYPES;
  
  FieldType(int id, Collection collection, JavaType javaType) {
    this.id = id;
    this.collection = collection;
    this.javaType = javaType;
    switch (collection) {
      case BYTE_STRING:
        this.elementType = javaType.getBoxedType();
        break;
      case MESSAGE:
        this.elementType = javaType.getBoxedType();
        break;
      default:
        this.elementType = null;
        break;
    } 
    boolean primitiveScalar = false;
    if (collection == Collection.SCALAR)
      switch (javaType) {
        case BYTE_STRING:
        case MESSAGE:
        case STRING:
          break;
        default:
          primitiveScalar = true;
          break;
      }  
    this.primitiveScalar = primitiveScalar;
  }
  
  public int id() {
    return this.id;
  }
  
  public JavaType getJavaType() {
    return this.javaType;
  }
  
  public boolean isPacked() {
    return Collection.PACKED_VECTOR.equals(this.collection);
  }
  
  public boolean isPrimitiveScalar() {
    return this.primitiveScalar;
  }
  
  public boolean isScalar() {
    return (this.collection == Collection.SCALAR);
  }
  
  public boolean isList() {
    return this.collection.isList();
  }
  
  public boolean isMap() {
    return (this.collection == Collection.MAP);
  }
  
  public boolean isValidForField(Field field) {
    if (Collection.VECTOR.equals(this.collection))
      return isValidForList(field); 
    return this.javaType.getType().isAssignableFrom(field.getType());
  }
  
  private boolean isValidForList(Field field) {
    Class<?> clazz = field.getType();
    if (!this.javaType.getType().isAssignableFrom(clazz))
      return false; 
    Type[] types = EMPTY_TYPES;
    Type genericType = field.getGenericType();
    if (genericType instanceof ParameterizedType)
      types = ((ParameterizedType)field.getGenericType()).getActualTypeArguments(); 
    Type listParameter = getListParameter(clazz, types);
    if (!(listParameter instanceof Class))
      return true; 
    return this.elementType.isAssignableFrom((Class)listParameter);
  }
  
  public static FieldType forId(int id) {
    if (id < 0 || id >= VALUES.length)
      return null; 
    return VALUES[id];
  }
  
  static {
    EMPTY_TYPES = new Type[0];
    FieldType[] values = values();
    VALUES = new FieldType[values.length];
    for (FieldType type : values)
      VALUES[type.id] = type; 
  }
  
  private static Type getGenericSuperList(Class<?> clazz) {
    Type[] genericInterfaces = clazz.getGenericInterfaces();
    for (Type genericInterface : genericInterfaces) {
      if (genericInterface instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType)genericInterface;
        Class<?> rawType = (Class)parameterizedType.getRawType();
        if (List.class.isAssignableFrom(rawType))
          return genericInterface; 
      } 
    } 
    Type type = clazz.getGenericSuperclass();
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType)type;
      Class<?> rawType = (Class)parameterizedType.getRawType();
      if (List.class.isAssignableFrom(rawType))
        return type; 
    } 
    return null;
  }
  
  private static Type getListParameter(Class<?> clazz, Type[] realTypes) {
    label41: while (clazz != List.class) {
      Type genericType = getGenericSuperList(clazz);
      if (genericType instanceof ParameterizedType) {
        ParameterizedType parameterizedType = (ParameterizedType)genericType;
        Type[] superArgs = parameterizedType.getActualTypeArguments();
        for (int i = 0; i < superArgs.length; i++) {
          Type superArg = superArgs[i];
          if (superArg instanceof TypeVariable) {
            TypeVariable[] arrayOfTypeVariable = (TypeVariable[])clazz.getTypeParameters();
            if (realTypes.length != arrayOfTypeVariable.length)
              throw new RuntimeException("Type array mismatch"); 
            boolean foundReplacement = false;
            for (int j = 0; j < arrayOfTypeVariable.length; j++) {
              if (superArg == arrayOfTypeVariable[j]) {
                Type realType = realTypes[j];
                superArgs[i] = realType;
                foundReplacement = true;
                break;
              } 
            } 
            if (!foundReplacement)
              throw new RuntimeException("Unable to find replacement for " + superArg); 
          } 
        } 
        Class<?> parent = (Class)parameterizedType.getRawType();
        realTypes = superArgs;
        clazz = parent;
        continue;
      } 
      realTypes = EMPTY_TYPES;
      for (Class<?> iface : clazz.getInterfaces()) {
        if (List.class.isAssignableFrom(iface)) {
          clazz = iface;
          continue label41;
        } 
      } 
      clazz = clazz.getSuperclass();
    } 
    if (realTypes.length != 1)
      throw new RuntimeException("Unable to identify parameter type for List<T>"); 
    return realTypes[0];
  }
  
  enum Collection {
    SCALAR(false),
    VECTOR(true),
    PACKED_VECTOR(true),
    MAP(false);
    
    private final boolean isList;
    
    Collection(boolean isList) {
      this.isList = isList;
    }
    
    public boolean isList() {
      return this.isList;
    }
  }
}
