package com.google.protobuf;

import java.io.IOException;

public final class WireFormat {
  static final int FIXED32_SIZE = 4;
  
  static final int FIXED64_SIZE = 8;
  
  static final int MAX_VARINT32_SIZE = 5;
  
  static final int MAX_VARINT64_SIZE = 10;
  
  static final int MAX_VARINT_SIZE = 10;
  
  public static final int WIRETYPE_VARINT = 0;
  
  public static final int WIRETYPE_FIXED64 = 1;
  
  public static final int WIRETYPE_LENGTH_DELIMITED = 2;
  
  public static final int WIRETYPE_START_GROUP = 3;
  
  public static final int WIRETYPE_END_GROUP = 4;
  
  public static final int WIRETYPE_FIXED32 = 5;
  
  static final int TAG_TYPE_BITS = 3;
  
  static final int TAG_TYPE_MASK = 7;
  
  static final int MESSAGE_SET_ITEM = 1;
  
  static final int MESSAGE_SET_TYPE_ID = 2;
  
  static final int MESSAGE_SET_MESSAGE = 3;
  
  public static int getTagWireType(int tag) {
    return tag & 0x7;
  }
  
  public static int getTagFieldNumber(int tag) {
    return tag >>> 3;
  }
  
  static int makeTag(int fieldNumber, int wireType) {
    return fieldNumber << 3 | wireType;
  }
  
  public enum JavaType {
    INT((String)Integer.valueOf(0)),
    LONG((String)Long.valueOf(0L)),
    FLOAT((String)Float.valueOf(0.0F)),
    DOUBLE((String)Double.valueOf(0.0D)),
    BOOLEAN((String)Boolean.valueOf(false)),
    STRING(""),
    BYTE_STRING((String)ByteString.EMPTY),
    ENUM(null),
    MESSAGE(null);
    
    private final Object defaultDefault;
    
    JavaType(Object defaultDefault) {
      this.defaultDefault = defaultDefault;
    }
    
    Object getDefaultDefault() {
      return this.defaultDefault;
    }
  }
  
  public enum FieldType {
    DOUBLE((String)WireFormat.JavaType.DOUBLE, 1),
    FLOAT((String)WireFormat.JavaType.FLOAT, 5),
    INT64((String)WireFormat.JavaType.LONG, 0),
    UINT64((String)WireFormat.JavaType.LONG, 0),
    INT32((String)WireFormat.JavaType.INT, 0),
    FIXED64((String)WireFormat.JavaType.LONG, 1),
    FIXED32((String)WireFormat.JavaType.INT, 5),
    BOOL((String)WireFormat.JavaType.BOOLEAN, 0),
    STRING((String)WireFormat.JavaType.STRING, 2) {
      public boolean isPackable() {
        return false;
      }
    },
    GROUP((String)WireFormat.JavaType.MESSAGE, 3) {
      public boolean isPackable() {
        return false;
      }
    },
    MESSAGE((String)WireFormat.JavaType.MESSAGE, 2) {
      public boolean isPackable() {
        return false;
      }
    },
    BYTES((String)WireFormat.JavaType.BYTE_STRING, 2) {
      public boolean isPackable() {
        return false;
      }
    },
    UINT32((String)WireFormat.JavaType.INT, 0),
    ENUM((String)WireFormat.JavaType.ENUM, 0),
    SFIXED32((String)WireFormat.JavaType.INT, 5),
    SFIXED64((String)WireFormat.JavaType.LONG, 1),
    SINT32((String)WireFormat.JavaType.INT, 0),
    SINT64((String)WireFormat.JavaType.LONG, 0);
    
    private final WireFormat.JavaType javaType;
    
    private final int wireType;
    
    FieldType(WireFormat.JavaType javaType, int wireType) {
      this.javaType = javaType;
      this.wireType = wireType;
    }
    
    public WireFormat.JavaType getJavaType() {
      return this.javaType;
    }
    
    public int getWireType() {
      return this.wireType;
    }
    
    public boolean isPackable() {
      return true;
    }
  }
  
  static final int MESSAGE_SET_ITEM_TAG = makeTag(1, 3);
  
  static final int MESSAGE_SET_ITEM_END_TAG = makeTag(1, 4);
  
  static final int MESSAGE_SET_TYPE_ID_TAG = makeTag(2, 0);
  
  static final int MESSAGE_SET_MESSAGE_TAG = makeTag(3, 2);
  
  enum Utf8Validation {
    LOOSE {
      Object readString(CodedInputStream input) throws IOException {
        return input.readString();
      }
    },
    STRICT {
      Object readString(CodedInputStream input) throws IOException {
        return input.readStringRequireUtf8();
      }
    },
    LAZY {
      Object readString(CodedInputStream input) throws IOException {
        return input.readBytes();
      }
    };
    
    abstract Object readString(CodedInputStream param1CodedInputStream) throws IOException;
  }
  
  static Object readPrimitiveField(CodedInputStream input, FieldType type, Utf8Validation utf8Validation) throws IOException {
    switch (type) {
      case DOUBLE:
        return Double.valueOf(input.readDouble());
      case FLOAT:
        return Float.valueOf(input.readFloat());
      case INT64:
        return Long.valueOf(input.readInt64());
      case UINT64:
        return Long.valueOf(input.readUInt64());
      case INT32:
        return Integer.valueOf(input.readInt32());
      case FIXED64:
        return Long.valueOf(input.readFixed64());
      case FIXED32:
        return Integer.valueOf(input.readFixed32());
      case BOOL:
        return Boolean.valueOf(input.readBool());
      case BYTES:
        return input.readBytes();
      case UINT32:
        return Integer.valueOf(input.readUInt32());
      case SFIXED32:
        return Integer.valueOf(input.readSFixed32());
      case SFIXED64:
        return Long.valueOf(input.readSFixed64());
      case SINT32:
        return Integer.valueOf(input.readSInt32());
      case SINT64:
        return Long.valueOf(input.readSInt64());
      case STRING:
        return utf8Validation.readString(input);
      case GROUP:
        throw new IllegalArgumentException("readPrimitiveField() cannot handle nested groups.");
      case MESSAGE:
        throw new IllegalArgumentException("readPrimitiveField() cannot handle embedded messages.");
      case ENUM:
        throw new IllegalArgumentException("readPrimitiveField() cannot handle enums.");
    } 
    throw new RuntimeException("There is no way to get here, but the compiler thinks otherwise.");
  }
}
