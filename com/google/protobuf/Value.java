package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Value extends GeneratedMessageV3 implements ValueOrBuilder {
  private static final long serialVersionUID = 0L;
  
  private int kindCase_;
  
  private Object kind_;
  
  public static final int NULL_VALUE_FIELD_NUMBER = 1;
  
  public static final int NUMBER_VALUE_FIELD_NUMBER = 2;
  
  public static final int STRING_VALUE_FIELD_NUMBER = 3;
  
  public static final int BOOL_VALUE_FIELD_NUMBER = 4;
  
  public static final int STRUCT_VALUE_FIELD_NUMBER = 5;
  
  public static final int LIST_VALUE_FIELD_NUMBER = 6;
  
  private byte memoizedIsInitialized;
  
  private Value(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.kindCase_ = 0;
    this.memoizedIsInitialized = -1;
  }
  
  private Value() {
    this.kindCase_ = 0;
    this.memoizedIsInitialized = -1;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new Value();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return StructProto.internal_static_google_protobuf_Value_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return StructProto.internal_static_google_protobuf_Value_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Value.class, (Class)Builder.class);
  }
  
  public enum KindCase implements Internal.EnumLite, AbstractMessageLite.InternalOneOfEnum {
    NULL_VALUE(1),
    NUMBER_VALUE(2),
    STRING_VALUE(3),
    BOOL_VALUE(4),
    STRUCT_VALUE(5),
    LIST_VALUE(6),
    KIND_NOT_SET(0);
    
    private final int value;
    
    KindCase(int value) {
      this.value = value;
    }
    
    public static KindCase forNumber(int value) {
      switch (value) {
        case 1:
          return NULL_VALUE;
        case 2:
          return NUMBER_VALUE;
        case 3:
          return STRING_VALUE;
        case 4:
          return BOOL_VALUE;
        case 5:
          return STRUCT_VALUE;
        case 6:
          return LIST_VALUE;
        case 0:
          return KIND_NOT_SET;
      } 
      return null;
    }
    
    public int getNumber() {
      return this.value;
    }
  }
  
  public KindCase getKindCase() {
    return KindCase.forNumber(this.kindCase_);
  }
  
  public boolean hasNullValue() {
    return (this.kindCase_ == 1);
  }
  
  public int getNullValueValue() {
    if (this.kindCase_ == 1)
      return ((Integer)this.kind_).intValue(); 
    return 0;
  }
  
  public NullValue getNullValue() {
    if (this.kindCase_ == 1) {
      NullValue result = NullValue.valueOf(((Integer)this.kind_).intValue());
      return (result == null) ? NullValue.UNRECOGNIZED : result;
    } 
    return NullValue.NULL_VALUE;
  }
  
  public boolean hasNumberValue() {
    return (this.kindCase_ == 2);
  }
  
  public double getNumberValue() {
    if (this.kindCase_ == 2)
      return ((Double)this.kind_).doubleValue(); 
    return 0.0D;
  }
  
  public boolean hasStringValue() {
    return (this.kindCase_ == 3);
  }
  
  public String getStringValue() {
    Object ref = "";
    if (this.kindCase_ == 3)
      ref = this.kind_; 
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    if (this.kindCase_ == 3)
      this.kind_ = s; 
    return s;
  }
  
  public ByteString getStringValueBytes() {
    Object ref = "";
    if (this.kindCase_ == 3)
      ref = this.kind_; 
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      if (this.kindCase_ == 3)
        this.kind_ = b; 
      return b;
    } 
    return (ByteString)ref;
  }
  
  public boolean hasBoolValue() {
    return (this.kindCase_ == 4);
  }
  
  public boolean getBoolValue() {
    if (this.kindCase_ == 4)
      return ((Boolean)this.kind_).booleanValue(); 
    return false;
  }
  
  public boolean hasStructValue() {
    return (this.kindCase_ == 5);
  }
  
  public Struct getStructValue() {
    if (this.kindCase_ == 5)
      return (Struct)this.kind_; 
    return Struct.getDefaultInstance();
  }
  
  public StructOrBuilder getStructValueOrBuilder() {
    if (this.kindCase_ == 5)
      return (Struct)this.kind_; 
    return Struct.getDefaultInstance();
  }
  
  public boolean hasListValue() {
    return (this.kindCase_ == 6);
  }
  
  public ListValue getListValue() {
    if (this.kindCase_ == 6)
      return (ListValue)this.kind_; 
    return ListValue.getDefaultInstance();
  }
  
  public ListValueOrBuilder getListValueOrBuilder() {
    if (this.kindCase_ == 6)
      return (ListValue)this.kind_; 
    return ListValue.getDefaultInstance();
  }
  
  public final boolean isInitialized() {
    byte isInitialized = this.memoizedIsInitialized;
    if (isInitialized == 1)
      return true; 
    if (isInitialized == 0)
      return false; 
    this.memoizedIsInitialized = 1;
    return true;
  }
  
  public void writeTo(CodedOutputStream output) throws IOException {
    if (this.kindCase_ == 1)
      output.writeEnum(1, ((Integer)this.kind_).intValue()); 
    if (this.kindCase_ == 2)
      output.writeDouble(2, ((Double)this.kind_)
          .doubleValue()); 
    if (this.kindCase_ == 3)
      GeneratedMessageV3.writeString(output, 3, this.kind_); 
    if (this.kindCase_ == 4)
      output.writeBool(4, ((Boolean)this.kind_)
          .booleanValue()); 
    if (this.kindCase_ == 5)
      output.writeMessage(5, (Struct)this.kind_); 
    if (this.kindCase_ == 6)
      output.writeMessage(6, (ListValue)this.kind_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (this.kindCase_ == 1)
      size += 
        CodedOutputStream.computeEnumSize(1, ((Integer)this.kind_).intValue()); 
    if (this.kindCase_ == 2)
      size += 
        CodedOutputStream.computeDoubleSize(2, ((Double)this.kind_)
          .doubleValue()); 
    if (this.kindCase_ == 3)
      size += GeneratedMessageV3.computeStringSize(3, this.kind_); 
    if (this.kindCase_ == 4)
      size += 
        CodedOutputStream.computeBoolSize(4, ((Boolean)this.kind_)
          .booleanValue()); 
    if (this.kindCase_ == 5)
      size += 
        CodedOutputStream.computeMessageSize(5, (Struct)this.kind_); 
    if (this.kindCase_ == 6)
      size += 
        CodedOutputStream.computeMessageSize(6, (ListValue)this.kind_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Value))
      return super.equals(obj); 
    Value other = (Value)obj;
    if (!getKindCase().equals(other.getKindCase()))
      return false; 
    switch (this.kindCase_) {
      case 1:
        if (getNullValueValue() != other
          .getNullValueValue())
          return false; 
        break;
      case 2:
        if (Double.doubleToLongBits(getNumberValue()) != 
          Double.doubleToLongBits(other
            .getNumberValue()))
          return false; 
        break;
      case 3:
        if (!getStringValue().equals(other.getStringValue()))
          return false; 
        break;
      case 4:
        if (getBoolValue() != other
          .getBoolValue())
          return false; 
        break;
      case 5:
        if (!getStructValue().equals(other.getStructValue()))
          return false; 
        break;
      case 6:
        if (!getListValue().equals(other.getListValue()))
          return false; 
        break;
    } 
    if (!getUnknownFields().equals(other.getUnknownFields()))
      return false; 
    return true;
  }
  
  public int hashCode() {
    if (this.memoizedHashCode != 0)
      return this.memoizedHashCode; 
    int hash = 41;
    hash = 19 * hash + getDescriptor().hashCode();
    switch (this.kindCase_) {
      case 1:
        hash = 37 * hash + 1;
        hash = 53 * hash + getNullValueValue();
        break;
      case 2:
        hash = 37 * hash + 2;
        hash = 53 * hash + Internal.hashLong(
            Double.doubleToLongBits(getNumberValue()));
        break;
      case 3:
        hash = 37 * hash + 3;
        hash = 53 * hash + getStringValue().hashCode();
        break;
      case 4:
        hash = 37 * hash + 4;
        hash = 53 * hash + Internal.hashBoolean(
            getBoolValue());
        break;
      case 5:
        hash = 37 * hash + 5;
        hash = 53 * hash + getStructValue().hashCode();
        break;
      case 6:
        hash = 37 * hash + 6;
        hash = 53 * hash + getListValue().hashCode();
        break;
    } 
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Value parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Value>parseWithIOException(PARSER, input);
  }
  
  public static Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Value>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Value parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Value>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Value>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Value parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Value>parseWithIOException(PARSER, input);
  }
  
  public static Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Value>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(Value prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  
  public Builder toBuilder() {
    return (this == DEFAULT_INSTANCE) ? new Builder() : (new Builder())
      .mergeFrom(this);
  }
  
  protected Builder newBuilderForType(GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ValueOrBuilder {
    private int kindCase_;
    
    private Object kind_;
    
    private SingleFieldBuilderV3<Struct, Struct.Builder, StructOrBuilder> structValueBuilder_;
    
    private SingleFieldBuilderV3<ListValue, ListValue.Builder, ListValueOrBuilder> listValueBuilder_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return StructProto.internal_static_google_protobuf_Value_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return StructProto.internal_static_google_protobuf_Value_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)Value.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.kindCase_ = 0;
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.kindCase_ = 0;
    }
    
    public Builder clear() {
      super.clear();
      if (this.structValueBuilder_ != null)
        this.structValueBuilder_.clear(); 
      if (this.listValueBuilder_ != null)
        this.listValueBuilder_.clear(); 
      this.kindCase_ = 0;
      this.kind_ = null;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return StructProto.internal_static_google_protobuf_Value_descriptor;
    }
    
    public Value getDefaultInstanceForType() {
      return Value.getDefaultInstance();
    }
    
    public Value build() {
      Value result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public Value buildPartial() {
      Value result = new Value(this);
      if (this.kindCase_ == 1)
        result.kind_ = this.kind_; 
      if (this.kindCase_ == 2)
        result.kind_ = this.kind_; 
      if (this.kindCase_ == 3)
        result.kind_ = this.kind_; 
      if (this.kindCase_ == 4)
        result.kind_ = this.kind_; 
      if (this.kindCase_ == 5)
        if (this.structValueBuilder_ == null) {
          result.kind_ = this.kind_;
        } else {
          result.kind_ = this.structValueBuilder_.build();
        }  
      if (this.kindCase_ == 6)
        if (this.listValueBuilder_ == null) {
          result.kind_ = this.kind_;
        } else {
          result.kind_ = this.listValueBuilder_.build();
        }  
      result.kindCase_ = this.kindCase_;
      onBuilt();
      return result;
    }
    
    public Builder clone() {
      return super.clone();
    }
    
    public Builder setField(Descriptors.FieldDescriptor field, Object value) {
      return super.setField(field, value);
    }
    
    public Builder clearField(Descriptors.FieldDescriptor field) {
      return super.clearField(field);
    }
    
    public Builder clearOneof(Descriptors.OneofDescriptor oneof) {
      return super.clearOneof(oneof);
    }
    
    public Builder setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
      return super.setRepeatedField(field, index, value);
    }
    
    public Builder addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
      return super.addRepeatedField(field, value);
    }
    
    public Builder mergeFrom(Message other) {
      if (other instanceof Value)
        return mergeFrom((Value)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(Value other) {
      if (other == Value.getDefaultInstance())
        return this; 
      switch (other.getKindCase()) {
        case NULL_VALUE:
          setNullValueValue(other.getNullValueValue());
          break;
        case NUMBER_VALUE:
          setNumberValue(other.getNumberValue());
          break;
        case STRING_VALUE:
          this.kindCase_ = 3;
          this.kind_ = other.kind_;
          onChanged();
          break;
        case BOOL_VALUE:
          setBoolValue(other.getBoolValue());
          break;
        case STRUCT_VALUE:
          mergeStructValue(other.getStructValue());
          break;
        case LIST_VALUE:
          mergeListValue(other.getListValue());
          break;
      } 
      mergeUnknownFields(other.getUnknownFields());
      onChanged();
      return this;
    }
    
    public final boolean isInitialized() {
      return true;
    }
    
    public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      if (extensionRegistry == null)
        throw new NullPointerException(); 
      try {
        boolean done = false;
        while (!done) {
          int rawValue;
          String s;
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              continue;
            case 8:
              rawValue = input.readEnum();
              this.kindCase_ = 1;
              this.kind_ = Integer.valueOf(rawValue);
              continue;
            case 17:
              this.kind_ = Double.valueOf(input.readDouble());
              this.kindCase_ = 2;
              continue;
            case 26:
              s = input.readStringRequireUtf8();
              this.kindCase_ = 3;
              this.kind_ = s;
              continue;
            case 32:
              this.kind_ = Boolean.valueOf(input.readBool());
              this.kindCase_ = 4;
              continue;
            case 42:
              input.readMessage(getStructValueFieldBuilder().getBuilder(), extensionRegistry);
              this.kindCase_ = 5;
              continue;
            case 50:
              input.readMessage(getListValueFieldBuilder().getBuilder(), extensionRegistry);
              this.kindCase_ = 6;
              continue;
          } 
          if (!parseUnknownField(input, extensionRegistry, tag))
            done = true; 
        } 
      } catch (InvalidProtocolBufferException e) {
        throw e.unwrapIOException();
      } finally {
        onChanged();
      } 
      return this;
    }
    
    public Value.KindCase getKindCase() {
      return Value.KindCase.forNumber(this.kindCase_);
    }
    
    public Builder clearKind() {
      this.kindCase_ = 0;
      this.kind_ = null;
      onChanged();
      return this;
    }
    
    public boolean hasNullValue() {
      return (this.kindCase_ == 1);
    }
    
    public int getNullValueValue() {
      if (this.kindCase_ == 1)
        return ((Integer)this.kind_).intValue(); 
      return 0;
    }
    
    public Builder setNullValueValue(int value) {
      this.kindCase_ = 1;
      this.kind_ = Integer.valueOf(value);
      onChanged();
      return this;
    }
    
    public NullValue getNullValue() {
      if (this.kindCase_ == 1) {
        NullValue result = NullValue.valueOf(((Integer)this.kind_)
            .intValue());
        return (result == null) ? NullValue.UNRECOGNIZED : result;
      } 
      return NullValue.NULL_VALUE;
    }
    
    public Builder setNullValue(NullValue value) {
      if (value == null)
        throw new NullPointerException(); 
      this.kindCase_ = 1;
      this.kind_ = Integer.valueOf(value.getNumber());
      onChanged();
      return this;
    }
    
    public Builder clearNullValue() {
      if (this.kindCase_ == 1) {
        this.kindCase_ = 0;
        this.kind_ = null;
        onChanged();
      } 
      return this;
    }
    
    public boolean hasNumberValue() {
      return (this.kindCase_ == 2);
    }
    
    public double getNumberValue() {
      if (this.kindCase_ == 2)
        return ((Double)this.kind_).doubleValue(); 
      return 0.0D;
    }
    
    public Builder setNumberValue(double value) {
      this.kindCase_ = 2;
      this.kind_ = Double.valueOf(value);
      onChanged();
      return this;
    }
    
    public Builder clearNumberValue() {
      if (this.kindCase_ == 2) {
        this.kindCase_ = 0;
        this.kind_ = null;
        onChanged();
      } 
      return this;
    }
    
    public boolean hasStringValue() {
      return (this.kindCase_ == 3);
    }
    
    public String getStringValue() {
      Object ref = "";
      if (this.kindCase_ == 3)
        ref = this.kind_; 
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        if (this.kindCase_ == 3)
          this.kind_ = s; 
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getStringValueBytes() {
      Object ref = "";
      if (this.kindCase_ == 3)
        ref = this.kind_; 
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        if (this.kindCase_ == 3)
          this.kind_ = b; 
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setStringValue(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.kindCase_ = 3;
      this.kind_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearStringValue() {
      if (this.kindCase_ == 3) {
        this.kindCase_ = 0;
        this.kind_ = null;
        onChanged();
      } 
      return this;
    }
    
    public Builder setStringValueBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.kindCase_ = 3;
      this.kind_ = value;
      onChanged();
      return this;
    }
    
    public boolean hasBoolValue() {
      return (this.kindCase_ == 4);
    }
    
    public boolean getBoolValue() {
      if (this.kindCase_ == 4)
        return ((Boolean)this.kind_).booleanValue(); 
      return false;
    }
    
    public Builder setBoolValue(boolean value) {
      this.kindCase_ = 4;
      this.kind_ = Boolean.valueOf(value);
      onChanged();
      return this;
    }
    
    public Builder clearBoolValue() {
      if (this.kindCase_ == 4) {
        this.kindCase_ = 0;
        this.kind_ = null;
        onChanged();
      } 
      return this;
    }
    
    public boolean hasStructValue() {
      return (this.kindCase_ == 5);
    }
    
    public Struct getStructValue() {
      if (this.structValueBuilder_ == null) {
        if (this.kindCase_ == 5)
          return (Struct)this.kind_; 
        return Struct.getDefaultInstance();
      } 
      if (this.kindCase_ == 5)
        return this.structValueBuilder_.getMessage(); 
      return Struct.getDefaultInstance();
    }
    
    public Builder setStructValue(Struct value) {
      if (this.structValueBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        this.kind_ = value;
        onChanged();
      } else {
        this.structValueBuilder_.setMessage(value);
      } 
      this.kindCase_ = 5;
      return this;
    }
    
    public Builder setStructValue(Struct.Builder builderForValue) {
      if (this.structValueBuilder_ == null) {
        this.kind_ = builderForValue.build();
        onChanged();
      } else {
        this.structValueBuilder_.setMessage(builderForValue.build());
      } 
      this.kindCase_ = 5;
      return this;
    }
    
    public Builder mergeStructValue(Struct value) {
      if (this.structValueBuilder_ == null) {
        if (this.kindCase_ == 5 && this.kind_ != 
          Struct.getDefaultInstance()) {
          this
            .kind_ = Struct.newBuilder((Struct)this.kind_).mergeFrom(value).buildPartial();
        } else {
          this.kind_ = value;
        } 
        onChanged();
      } else if (this.kindCase_ == 5) {
        this.structValueBuilder_.mergeFrom(value);
      } else {
        this.structValueBuilder_.setMessage(value);
      } 
      this.kindCase_ = 5;
      return this;
    }
    
    public Builder clearStructValue() {
      if (this.structValueBuilder_ == null) {
        if (this.kindCase_ == 5) {
          this.kindCase_ = 0;
          this.kind_ = null;
          onChanged();
        } 
      } else {
        if (this.kindCase_ == 5) {
          this.kindCase_ = 0;
          this.kind_ = null;
        } 
        this.structValueBuilder_.clear();
      } 
      return this;
    }
    
    public Struct.Builder getStructValueBuilder() {
      return getStructValueFieldBuilder().getBuilder();
    }
    
    public StructOrBuilder getStructValueOrBuilder() {
      if (this.kindCase_ == 5 && this.structValueBuilder_ != null)
        return this.structValueBuilder_.getMessageOrBuilder(); 
      if (this.kindCase_ == 5)
        return (Struct)this.kind_; 
      return Struct.getDefaultInstance();
    }
    
    private SingleFieldBuilderV3<Struct, Struct.Builder, StructOrBuilder> getStructValueFieldBuilder() {
      if (this.structValueBuilder_ == null) {
        if (this.kindCase_ != 5)
          this.kind_ = Struct.getDefaultInstance(); 
        this
          
          .structValueBuilder_ = new SingleFieldBuilderV3<>((Struct)this.kind_, getParentForChildren(), isClean());
        this.kind_ = null;
      } 
      this.kindCase_ = 5;
      onChanged();
      return this.structValueBuilder_;
    }
    
    public boolean hasListValue() {
      return (this.kindCase_ == 6);
    }
    
    public ListValue getListValue() {
      if (this.listValueBuilder_ == null) {
        if (this.kindCase_ == 6)
          return (ListValue)this.kind_; 
        return ListValue.getDefaultInstance();
      } 
      if (this.kindCase_ == 6)
        return this.listValueBuilder_.getMessage(); 
      return ListValue.getDefaultInstance();
    }
    
    public Builder setListValue(ListValue value) {
      if (this.listValueBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        this.kind_ = value;
        onChanged();
      } else {
        this.listValueBuilder_.setMessage(value);
      } 
      this.kindCase_ = 6;
      return this;
    }
    
    public Builder setListValue(ListValue.Builder builderForValue) {
      if (this.listValueBuilder_ == null) {
        this.kind_ = builderForValue.build();
        onChanged();
      } else {
        this.listValueBuilder_.setMessage(builderForValue.build());
      } 
      this.kindCase_ = 6;
      return this;
    }
    
    public Builder mergeListValue(ListValue value) {
      if (this.listValueBuilder_ == null) {
        if (this.kindCase_ == 6 && this.kind_ != 
          ListValue.getDefaultInstance()) {
          this
            .kind_ = ListValue.newBuilder((ListValue)this.kind_).mergeFrom(value).buildPartial();
        } else {
          this.kind_ = value;
        } 
        onChanged();
      } else if (this.kindCase_ == 6) {
        this.listValueBuilder_.mergeFrom(value);
      } else {
        this.listValueBuilder_.setMessage(value);
      } 
      this.kindCase_ = 6;
      return this;
    }
    
    public Builder clearListValue() {
      if (this.listValueBuilder_ == null) {
        if (this.kindCase_ == 6) {
          this.kindCase_ = 0;
          this.kind_ = null;
          onChanged();
        } 
      } else {
        if (this.kindCase_ == 6) {
          this.kindCase_ = 0;
          this.kind_ = null;
        } 
        this.listValueBuilder_.clear();
      } 
      return this;
    }
    
    public ListValue.Builder getListValueBuilder() {
      return getListValueFieldBuilder().getBuilder();
    }
    
    public ListValueOrBuilder getListValueOrBuilder() {
      if (this.kindCase_ == 6 && this.listValueBuilder_ != null)
        return this.listValueBuilder_.getMessageOrBuilder(); 
      if (this.kindCase_ == 6)
        return (ListValue)this.kind_; 
      return ListValue.getDefaultInstance();
    }
    
    private SingleFieldBuilderV3<ListValue, ListValue.Builder, ListValueOrBuilder> getListValueFieldBuilder() {
      if (this.listValueBuilder_ == null) {
        if (this.kindCase_ != 6)
          this.kind_ = ListValue.getDefaultInstance(); 
        this
          
          .listValueBuilder_ = new SingleFieldBuilderV3<>((ListValue)this.kind_, getParentForChildren(), isClean());
        this.kind_ = null;
      } 
      this.kindCase_ = 6;
      onChanged();
      return this.listValueBuilder_;
    }
    
    public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }
    
    public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }
  }
  
  private static final Value DEFAULT_INSTANCE = new Value();
  
  public static Value getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<Value> PARSER = new AbstractParser<Value>() {
      public Value parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        Value.Builder builder = Value.newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (IOException e) {
          throw (new InvalidProtocolBufferException(e))
            .setUnfinishedMessage(builder.buildPartial());
        } 
        return builder.buildPartial();
      }
    };
  
  public static Parser<Value> parser() {
    return PARSER;
  }
  
  public Parser<Value> getParserForType() {
    return PARSER;
  }
  
  public Value getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
