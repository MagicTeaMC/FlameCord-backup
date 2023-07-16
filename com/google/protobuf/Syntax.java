package com.google.protobuf;

public enum Syntax implements ProtocolMessageEnum {
  SYNTAX_PROTO2(0),
  SYNTAX_PROTO3(1),
  UNRECOGNIZED(-1);
  
  public static final int SYNTAX_PROTO2_VALUE = 0;
  
  public static final int SYNTAX_PROTO3_VALUE = 1;
  
  private static final Internal.EnumLiteMap<Syntax> internalValueMap;
  
  private static final Syntax[] VALUES;
  
  private final int value;
  
  public final int getNumber() {
    if (this == UNRECOGNIZED)
      throw new IllegalArgumentException("Can't get the number of an unknown enum value."); 
    return this.value;
  }
  
  public static Syntax forNumber(int value) {
    switch (value) {
      case 0:
        return SYNTAX_PROTO2;
      case 1:
        return SYNTAX_PROTO3;
    } 
    return null;
  }
  
  public static Internal.EnumLiteMap<Syntax> internalGetValueMap() {
    return internalValueMap;
  }
  
  static {
    internalValueMap = new Internal.EnumLiteMap<Syntax>() {
        public Syntax findValueByNumber(int number) {
          return Syntax.forNumber(number);
        }
      };
    VALUES = values();
  }
  
  public final Descriptors.EnumValueDescriptor getValueDescriptor() {
    if (this == UNRECOGNIZED)
      throw new IllegalStateException("Can't get the descriptor of an unrecognized enum value."); 
    return getDescriptor().getValues().get(ordinal());
  }
  
  public final Descriptors.EnumDescriptor getDescriptorForType() {
    return getDescriptor();
  }
  
  public static final Descriptors.EnumDescriptor getDescriptor() {
    return TypeProto.getDescriptor().getEnumTypes().get(0);
  }
  
  Syntax(int value) {
    this.value = value;
  }
}
