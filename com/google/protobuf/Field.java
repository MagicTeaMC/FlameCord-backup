package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Field extends GeneratedMessageV3 implements FieldOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int KIND_FIELD_NUMBER = 1;
  
  private int kind_;
  
  public static final int CARDINALITY_FIELD_NUMBER = 2;
  
  private int cardinality_;
  
  public static final int NUMBER_FIELD_NUMBER = 3;
  
  private int number_;
  
  public static final int NAME_FIELD_NUMBER = 4;
  
  private volatile Object name_;
  
  public static final int TYPE_URL_FIELD_NUMBER = 6;
  
  private volatile Object typeUrl_;
  
  public static final int ONEOF_INDEX_FIELD_NUMBER = 7;
  
  private int oneofIndex_;
  
  public static final int PACKED_FIELD_NUMBER = 8;
  
  private boolean packed_;
  
  public static final int OPTIONS_FIELD_NUMBER = 9;
  
  private List<Option> options_;
  
  public static final int JSON_NAME_FIELD_NUMBER = 10;
  
  private volatile Object jsonName_;
  
  public static final int DEFAULT_VALUE_FIELD_NUMBER = 11;
  
  private volatile Object defaultValue_;
  
  private byte memoizedIsInitialized;
  
  private Field(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private Field() {
    this.memoizedIsInitialized = -1;
    this.kind_ = 0;
    this.cardinality_ = 0;
    this.name_ = "";
    this.typeUrl_ = "";
    this.options_ = Collections.emptyList();
    this.jsonName_ = "";
    this.defaultValue_ = "";
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new Field();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return TypeProto.internal_static_google_protobuf_Field_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return TypeProto.internal_static_google_protobuf_Field_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Field.class, (Class)Builder.class);
  }
  
  public enum Kind implements ProtocolMessageEnum {
    TYPE_UNKNOWN(0),
    TYPE_DOUBLE(1),
    TYPE_FLOAT(2),
    TYPE_INT64(3),
    TYPE_UINT64(4),
    TYPE_INT32(5),
    TYPE_FIXED64(6),
    TYPE_FIXED32(7),
    TYPE_BOOL(8),
    TYPE_STRING(9),
    TYPE_GROUP(10),
    TYPE_MESSAGE(11),
    TYPE_BYTES(12),
    TYPE_UINT32(13),
    TYPE_ENUM(14),
    TYPE_SFIXED32(15),
    TYPE_SFIXED64(16),
    TYPE_SINT32(17),
    TYPE_SINT64(18),
    UNRECOGNIZED(-1);
    
    public static final int TYPE_UNKNOWN_VALUE = 0;
    
    public static final int TYPE_DOUBLE_VALUE = 1;
    
    public static final int TYPE_FLOAT_VALUE = 2;
    
    public static final int TYPE_INT64_VALUE = 3;
    
    public static final int TYPE_UINT64_VALUE = 4;
    
    public static final int TYPE_INT32_VALUE = 5;
    
    public static final int TYPE_FIXED64_VALUE = 6;
    
    public static final int TYPE_FIXED32_VALUE = 7;
    
    public static final int TYPE_BOOL_VALUE = 8;
    
    public static final int TYPE_STRING_VALUE = 9;
    
    public static final int TYPE_GROUP_VALUE = 10;
    
    public static final int TYPE_MESSAGE_VALUE = 11;
    
    public static final int TYPE_BYTES_VALUE = 12;
    
    public static final int TYPE_UINT32_VALUE = 13;
    
    public static final int TYPE_ENUM_VALUE = 14;
    
    public static final int TYPE_SFIXED32_VALUE = 15;
    
    public static final int TYPE_SFIXED64_VALUE = 16;
    
    public static final int TYPE_SINT32_VALUE = 17;
    
    public static final int TYPE_SINT64_VALUE = 18;
    
    private static final Internal.EnumLiteMap<Kind> internalValueMap = new Internal.EnumLiteMap<Kind>() {
        public Field.Kind findValueByNumber(int number) {
          return Field.Kind.forNumber(number);
        }
      };
    
    private static final Kind[] VALUES = values();
    
    private final int value;
    
    public final int getNumber() {
      if (this == UNRECOGNIZED)
        throw new IllegalArgumentException("Can't get the number of an unknown enum value."); 
      return this.value;
    }
    
    public static Kind forNumber(int value) {
      switch (value) {
        case 0:
          return TYPE_UNKNOWN;
        case 1:
          return TYPE_DOUBLE;
        case 2:
          return TYPE_FLOAT;
        case 3:
          return TYPE_INT64;
        case 4:
          return TYPE_UINT64;
        case 5:
          return TYPE_INT32;
        case 6:
          return TYPE_FIXED64;
        case 7:
          return TYPE_FIXED32;
        case 8:
          return TYPE_BOOL;
        case 9:
          return TYPE_STRING;
        case 10:
          return TYPE_GROUP;
        case 11:
          return TYPE_MESSAGE;
        case 12:
          return TYPE_BYTES;
        case 13:
          return TYPE_UINT32;
        case 14:
          return TYPE_ENUM;
        case 15:
          return TYPE_SFIXED32;
        case 16:
          return TYPE_SFIXED64;
        case 17:
          return TYPE_SINT32;
        case 18:
          return TYPE_SINT64;
      } 
      return null;
    }
    
    public static Internal.EnumLiteMap<Kind> internalGetValueMap() {
      return internalValueMap;
    }
    
    static {
    
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
      return Field.getDescriptor().getEnumTypes().get(0);
    }
    
    Kind(int value) {
      this.value = value;
    }
  }
  
  public enum Cardinality implements ProtocolMessageEnum {
    CARDINALITY_UNKNOWN(0),
    CARDINALITY_OPTIONAL(1),
    CARDINALITY_REQUIRED(2),
    CARDINALITY_REPEATED(3),
    UNRECOGNIZED(-1);
    
    public static final int CARDINALITY_UNKNOWN_VALUE = 0;
    
    public static final int CARDINALITY_OPTIONAL_VALUE = 1;
    
    public static final int CARDINALITY_REQUIRED_VALUE = 2;
    
    public static final int CARDINALITY_REPEATED_VALUE = 3;
    
    private static final Internal.EnumLiteMap<Cardinality> internalValueMap = new Internal.EnumLiteMap<Cardinality>() {
        public Field.Cardinality findValueByNumber(int number) {
          return Field.Cardinality.forNumber(number);
        }
      };
    
    private static final Cardinality[] VALUES = values();
    
    private final int value;
    
    public final int getNumber() {
      if (this == UNRECOGNIZED)
        throw new IllegalArgumentException("Can't get the number of an unknown enum value."); 
      return this.value;
    }
    
    public static Cardinality forNumber(int value) {
      switch (value) {
        case 0:
          return CARDINALITY_UNKNOWN;
        case 1:
          return CARDINALITY_OPTIONAL;
        case 2:
          return CARDINALITY_REQUIRED;
        case 3:
          return CARDINALITY_REPEATED;
      } 
      return null;
    }
    
    public static Internal.EnumLiteMap<Cardinality> internalGetValueMap() {
      return internalValueMap;
    }
    
    static {
    
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
      return Field.getDescriptor().getEnumTypes().get(1);
    }
    
    Cardinality(int value) {
      this.value = value;
    }
  }
  
  public int getKindValue() {
    return this.kind_;
  }
  
  public Kind getKind() {
    Kind result = Kind.valueOf(this.kind_);
    return (result == null) ? Kind.UNRECOGNIZED : result;
  }
  
  public int getCardinalityValue() {
    return this.cardinality_;
  }
  
  public Cardinality getCardinality() {
    Cardinality result = Cardinality.valueOf(this.cardinality_);
    return (result == null) ? Cardinality.UNRECOGNIZED : result;
  }
  
  public int getNumber() {
    return this.number_;
  }
  
  public String getName() {
    Object ref = this.name_;
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    this.name_ = s;
    return s;
  }
  
  public ByteString getNameBytes() {
    Object ref = this.name_;
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      this.name_ = b;
      return b;
    } 
    return (ByteString)ref;
  }
  
  public String getTypeUrl() {
    Object ref = this.typeUrl_;
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    this.typeUrl_ = s;
    return s;
  }
  
  public ByteString getTypeUrlBytes() {
    Object ref = this.typeUrl_;
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      this.typeUrl_ = b;
      return b;
    } 
    return (ByteString)ref;
  }
  
  public int getOneofIndex() {
    return this.oneofIndex_;
  }
  
  public boolean getPacked() {
    return this.packed_;
  }
  
  public List<Option> getOptionsList() {
    return this.options_;
  }
  
  public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
    return (List)this.options_;
  }
  
  public int getOptionsCount() {
    return this.options_.size();
  }
  
  public Option getOptions(int index) {
    return this.options_.get(index);
  }
  
  public OptionOrBuilder getOptionsOrBuilder(int index) {
    return this.options_.get(index);
  }
  
  public String getJsonName() {
    Object ref = this.jsonName_;
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    this.jsonName_ = s;
    return s;
  }
  
  public ByteString getJsonNameBytes() {
    Object ref = this.jsonName_;
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      this.jsonName_ = b;
      return b;
    } 
    return (ByteString)ref;
  }
  
  public String getDefaultValue() {
    Object ref = this.defaultValue_;
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    this.defaultValue_ = s;
    return s;
  }
  
  public ByteString getDefaultValueBytes() {
    Object ref = this.defaultValue_;
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      this.defaultValue_ = b;
      return b;
    } 
    return (ByteString)ref;
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
    if (this.kind_ != Kind.TYPE_UNKNOWN.getNumber())
      output.writeEnum(1, this.kind_); 
    if (this.cardinality_ != Cardinality.CARDINALITY_UNKNOWN.getNumber())
      output.writeEnum(2, this.cardinality_); 
    if (this.number_ != 0)
      output.writeInt32(3, this.number_); 
    if (!GeneratedMessageV3.isStringEmpty(this.name_))
      GeneratedMessageV3.writeString(output, 4, this.name_); 
    if (!GeneratedMessageV3.isStringEmpty(this.typeUrl_))
      GeneratedMessageV3.writeString(output, 6, this.typeUrl_); 
    if (this.oneofIndex_ != 0)
      output.writeInt32(7, this.oneofIndex_); 
    if (this.packed_)
      output.writeBool(8, this.packed_); 
    for (int i = 0; i < this.options_.size(); i++)
      output.writeMessage(9, this.options_.get(i)); 
    if (!GeneratedMessageV3.isStringEmpty(this.jsonName_))
      GeneratedMessageV3.writeString(output, 10, this.jsonName_); 
    if (!GeneratedMessageV3.isStringEmpty(this.defaultValue_))
      GeneratedMessageV3.writeString(output, 11, this.defaultValue_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (this.kind_ != Kind.TYPE_UNKNOWN.getNumber())
      size += 
        CodedOutputStream.computeEnumSize(1, this.kind_); 
    if (this.cardinality_ != Cardinality.CARDINALITY_UNKNOWN.getNumber())
      size += 
        CodedOutputStream.computeEnumSize(2, this.cardinality_); 
    if (this.number_ != 0)
      size += 
        CodedOutputStream.computeInt32Size(3, this.number_); 
    if (!GeneratedMessageV3.isStringEmpty(this.name_))
      size += GeneratedMessageV3.computeStringSize(4, this.name_); 
    if (!GeneratedMessageV3.isStringEmpty(this.typeUrl_))
      size += GeneratedMessageV3.computeStringSize(6, this.typeUrl_); 
    if (this.oneofIndex_ != 0)
      size += 
        CodedOutputStream.computeInt32Size(7, this.oneofIndex_); 
    if (this.packed_)
      size += 
        CodedOutputStream.computeBoolSize(8, this.packed_); 
    for (int i = 0; i < this.options_.size(); i++)
      size += 
        CodedOutputStream.computeMessageSize(9, this.options_.get(i)); 
    if (!GeneratedMessageV3.isStringEmpty(this.jsonName_))
      size += GeneratedMessageV3.computeStringSize(10, this.jsonName_); 
    if (!GeneratedMessageV3.isStringEmpty(this.defaultValue_))
      size += GeneratedMessageV3.computeStringSize(11, this.defaultValue_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Field))
      return super.equals(obj); 
    Field other = (Field)obj;
    if (this.kind_ != other.kind_)
      return false; 
    if (this.cardinality_ != other.cardinality_)
      return false; 
    if (getNumber() != other
      .getNumber())
      return false; 
    if (!getName().equals(other.getName()))
      return false; 
    if (!getTypeUrl().equals(other.getTypeUrl()))
      return false; 
    if (getOneofIndex() != other
      .getOneofIndex())
      return false; 
    if (getPacked() != other
      .getPacked())
      return false; 
    if (!getOptionsList().equals(other.getOptionsList()))
      return false; 
    if (!getJsonName().equals(other.getJsonName()))
      return false; 
    if (!getDefaultValue().equals(other.getDefaultValue()))
      return false; 
    if (!getUnknownFields().equals(other.getUnknownFields()))
      return false; 
    return true;
  }
  
  public int hashCode() {
    if (this.memoizedHashCode != 0)
      return this.memoizedHashCode; 
    int hash = 41;
    hash = 19 * hash + getDescriptor().hashCode();
    hash = 37 * hash + 1;
    hash = 53 * hash + this.kind_;
    hash = 37 * hash + 2;
    hash = 53 * hash + this.cardinality_;
    hash = 37 * hash + 3;
    hash = 53 * hash + getNumber();
    hash = 37 * hash + 4;
    hash = 53 * hash + getName().hashCode();
    hash = 37 * hash + 6;
    hash = 53 * hash + getTypeUrl().hashCode();
    hash = 37 * hash + 7;
    hash = 53 * hash + getOneofIndex();
    hash = 37 * hash + 8;
    hash = 53 * hash + Internal.hashBoolean(
        getPacked());
    if (getOptionsCount() > 0) {
      hash = 37 * hash + 9;
      hash = 53 * hash + getOptionsList().hashCode();
    } 
    hash = 37 * hash + 10;
    hash = 53 * hash + getJsonName().hashCode();
    hash = 37 * hash + 11;
    hash = 53 * hash + getDefaultValue().hashCode();
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static Field parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Field parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Field parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Field parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Field parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Field parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Field parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Field>parseWithIOException(PARSER, input);
  }
  
  public static Field parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Field>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Field parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Field>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static Field parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Field>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Field parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Field>parseWithIOException(PARSER, input);
  }
  
  public static Field parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Field>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(Field prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FieldOrBuilder {
    private int bitField0_;
    
    private int kind_;
    
    private int cardinality_;
    
    private int number_;
    
    private Object name_;
    
    private Object typeUrl_;
    
    private int oneofIndex_;
    
    private boolean packed_;
    
    private List<Option> options_;
    
    private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
    
    private Object jsonName_;
    
    private Object defaultValue_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return TypeProto.internal_static_google_protobuf_Field_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_Field_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)Field.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.kind_ = 0;
      this.cardinality_ = 0;
      this.name_ = "";
      this.typeUrl_ = "";
      this
        .options_ = Collections.emptyList();
      this.jsonName_ = "";
      this.defaultValue_ = "";
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.kind_ = 0;
      this.cardinality_ = 0;
      this.name_ = "";
      this.typeUrl_ = "";
      this.options_ = Collections.emptyList();
      this.jsonName_ = "";
      this.defaultValue_ = "";
    }
    
    public Builder clear() {
      super.clear();
      this.kind_ = 0;
      this.cardinality_ = 0;
      this.number_ = 0;
      this.name_ = "";
      this.typeUrl_ = "";
      this.oneofIndex_ = 0;
      this.packed_ = false;
      if (this.optionsBuilder_ == null) {
        this.options_ = Collections.emptyList();
      } else {
        this.options_ = null;
        this.optionsBuilder_.clear();
      } 
      this.bitField0_ &= 0xFFFFFFFE;
      this.jsonName_ = "";
      this.defaultValue_ = "";
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return TypeProto.internal_static_google_protobuf_Field_descriptor;
    }
    
    public Field getDefaultInstanceForType() {
      return Field.getDefaultInstance();
    }
    
    public Field build() {
      Field result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public Field buildPartial() {
      Field result = new Field(this);
      int from_bitField0_ = this.bitField0_;
      result.kind_ = this.kind_;
      result.cardinality_ = this.cardinality_;
      result.number_ = this.number_;
      result.name_ = this.name_;
      result.typeUrl_ = this.typeUrl_;
      result.oneofIndex_ = this.oneofIndex_;
      result.packed_ = this.packed_;
      if (this.optionsBuilder_ == null) {
        if ((this.bitField0_ & 0x1) != 0) {
          this.options_ = Collections.unmodifiableList(this.options_);
          this.bitField0_ &= 0xFFFFFFFE;
        } 
        result.options_ = this.options_;
      } else {
        result.options_ = this.optionsBuilder_.build();
      } 
      result.jsonName_ = this.jsonName_;
      result.defaultValue_ = this.defaultValue_;
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
      if (other instanceof Field)
        return mergeFrom((Field)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(Field other) {
      if (other == Field.getDefaultInstance())
        return this; 
      if (other.kind_ != 0)
        setKindValue(other.getKindValue()); 
      if (other.cardinality_ != 0)
        setCardinalityValue(other.getCardinalityValue()); 
      if (other.getNumber() != 0)
        setNumber(other.getNumber()); 
      if (!other.getName().isEmpty()) {
        this.name_ = other.name_;
        onChanged();
      } 
      if (!other.getTypeUrl().isEmpty()) {
        this.typeUrl_ = other.typeUrl_;
        onChanged();
      } 
      if (other.getOneofIndex() != 0)
        setOneofIndex(other.getOneofIndex()); 
      if (other.getPacked())
        setPacked(other.getPacked()); 
      if (this.optionsBuilder_ == null) {
        if (!other.options_.isEmpty()) {
          if (this.options_.isEmpty()) {
            this.options_ = other.options_;
            this.bitField0_ &= 0xFFFFFFFE;
          } else {
            ensureOptionsIsMutable();
            this.options_.addAll(other.options_);
          } 
          onChanged();
        } 
      } else if (!other.options_.isEmpty()) {
        if (this.optionsBuilder_.isEmpty()) {
          this.optionsBuilder_.dispose();
          this.optionsBuilder_ = null;
          this.options_ = other.options_;
          this.bitField0_ &= 0xFFFFFFFE;
          this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getOptionsFieldBuilder() : null;
        } else {
          this.optionsBuilder_.addAllMessages(other.options_);
        } 
      } 
      if (!other.getJsonName().isEmpty()) {
        this.jsonName_ = other.jsonName_;
        onChanged();
      } 
      if (!other.getDefaultValue().isEmpty()) {
        this.defaultValue_ = other.defaultValue_;
        onChanged();
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
          Option m;
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              continue;
            case 8:
              this.kind_ = input.readEnum();
              continue;
            case 16:
              this.cardinality_ = input.readEnum();
              continue;
            case 24:
              this.number_ = input.readInt32();
              continue;
            case 34:
              this.name_ = input.readStringRequireUtf8();
              continue;
            case 50:
              this.typeUrl_ = input.readStringRequireUtf8();
              continue;
            case 56:
              this.oneofIndex_ = input.readInt32();
              continue;
            case 64:
              this.packed_ = input.readBool();
              continue;
            case 74:
              m = input.<Option>readMessage(Option.parser(), extensionRegistry);
              if (this.optionsBuilder_ == null) {
                ensureOptionsIsMutable();
                this.options_.add(m);
                continue;
              } 
              this.optionsBuilder_.addMessage(m);
              continue;
            case 82:
              this.jsonName_ = input.readStringRequireUtf8();
              continue;
            case 90:
              this.defaultValue_ = input.readStringRequireUtf8();
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
    
    public int getKindValue() {
      return this.kind_;
    }
    
    public Builder setKindValue(int value) {
      this.kind_ = value;
      onChanged();
      return this;
    }
    
    public Field.Kind getKind() {
      Field.Kind result = Field.Kind.valueOf(this.kind_);
      return (result == null) ? Field.Kind.UNRECOGNIZED : result;
    }
    
    public Builder setKind(Field.Kind value) {
      if (value == null)
        throw new NullPointerException(); 
      this.kind_ = value.getNumber();
      onChanged();
      return this;
    }
    
    public Builder clearKind() {
      this.kind_ = 0;
      onChanged();
      return this;
    }
    
    public int getCardinalityValue() {
      return this.cardinality_;
    }
    
    public Builder setCardinalityValue(int value) {
      this.cardinality_ = value;
      onChanged();
      return this;
    }
    
    public Field.Cardinality getCardinality() {
      Field.Cardinality result = Field.Cardinality.valueOf(this.cardinality_);
      return (result == null) ? Field.Cardinality.UNRECOGNIZED : result;
    }
    
    public Builder setCardinality(Field.Cardinality value) {
      if (value == null)
        throw new NullPointerException(); 
      this.cardinality_ = value.getNumber();
      onChanged();
      return this;
    }
    
    public Builder clearCardinality() {
      this.cardinality_ = 0;
      onChanged();
      return this;
    }
    
    public int getNumber() {
      return this.number_;
    }
    
    public Builder setNumber(int value) {
      this.number_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearNumber() {
      this.number_ = 0;
      onChanged();
      return this;
    }
    
    public String getName() {
      Object ref = this.name_;
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.name_ = s;
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getNameBytes() {
      Object ref = this.name_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.name_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setName(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.name_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearName() {
      this.name_ = Field.getDefaultInstance().getName();
      onChanged();
      return this;
    }
    
    public Builder setNameBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.name_ = value;
      onChanged();
      return this;
    }
    
    public String getTypeUrl() {
      Object ref = this.typeUrl_;
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.typeUrl_ = s;
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getTypeUrlBytes() {
      Object ref = this.typeUrl_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.typeUrl_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setTypeUrl(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.typeUrl_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearTypeUrl() {
      this.typeUrl_ = Field.getDefaultInstance().getTypeUrl();
      onChanged();
      return this;
    }
    
    public Builder setTypeUrlBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.typeUrl_ = value;
      onChanged();
      return this;
    }
    
    public int getOneofIndex() {
      return this.oneofIndex_;
    }
    
    public Builder setOneofIndex(int value) {
      this.oneofIndex_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearOneofIndex() {
      this.oneofIndex_ = 0;
      onChanged();
      return this;
    }
    
    public boolean getPacked() {
      return this.packed_;
    }
    
    public Builder setPacked(boolean value) {
      this.packed_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearPacked() {
      this.packed_ = false;
      onChanged();
      return this;
    }
    
    private void ensureOptionsIsMutable() {
      if ((this.bitField0_ & 0x1) == 0) {
        this.options_ = new ArrayList<>(this.options_);
        this.bitField0_ |= 0x1;
      } 
    }
    
    public List<Option> getOptionsList() {
      if (this.optionsBuilder_ == null)
        return Collections.unmodifiableList(this.options_); 
      return this.optionsBuilder_.getMessageList();
    }
    
    public int getOptionsCount() {
      if (this.optionsBuilder_ == null)
        return this.options_.size(); 
      return this.optionsBuilder_.getCount();
    }
    
    public Option getOptions(int index) {
      if (this.optionsBuilder_ == null)
        return this.options_.get(index); 
      return this.optionsBuilder_.getMessage(index);
    }
    
    public Builder setOptions(int index, Option value) {
      if (this.optionsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureOptionsIsMutable();
        this.options_.set(index, value);
        onChanged();
      } else {
        this.optionsBuilder_.setMessage(index, value);
      } 
      return this;
    }
    
    public Builder setOptions(int index, Option.Builder builderForValue) {
      if (this.optionsBuilder_ == null) {
        ensureOptionsIsMutable();
        this.options_.set(index, builderForValue.build());
        onChanged();
      } else {
        this.optionsBuilder_.setMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addOptions(Option value) {
      if (this.optionsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureOptionsIsMutable();
        this.options_.add(value);
        onChanged();
      } else {
        this.optionsBuilder_.addMessage(value);
      } 
      return this;
    }
    
    public Builder addOptions(int index, Option value) {
      if (this.optionsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureOptionsIsMutable();
        this.options_.add(index, value);
        onChanged();
      } else {
        this.optionsBuilder_.addMessage(index, value);
      } 
      return this;
    }
    
    public Builder addOptions(Option.Builder builderForValue) {
      if (this.optionsBuilder_ == null) {
        ensureOptionsIsMutable();
        this.options_.add(builderForValue.build());
        onChanged();
      } else {
        this.optionsBuilder_.addMessage(builderForValue.build());
      } 
      return this;
    }
    
    public Builder addOptions(int index, Option.Builder builderForValue) {
      if (this.optionsBuilder_ == null) {
        ensureOptionsIsMutable();
        this.options_.add(index, builderForValue.build());
        onChanged();
      } else {
        this.optionsBuilder_.addMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addAllOptions(Iterable<? extends Option> values) {
      if (this.optionsBuilder_ == null) {
        ensureOptionsIsMutable();
        AbstractMessageLite.Builder.addAll(values, this.options_);
        onChanged();
      } else {
        this.optionsBuilder_.addAllMessages(values);
      } 
      return this;
    }
    
    public Builder clearOptions() {
      if (this.optionsBuilder_ == null) {
        this.options_ = Collections.emptyList();
        this.bitField0_ &= 0xFFFFFFFE;
        onChanged();
      } else {
        this.optionsBuilder_.clear();
      } 
      return this;
    }
    
    public Builder removeOptions(int index) {
      if (this.optionsBuilder_ == null) {
        ensureOptionsIsMutable();
        this.options_.remove(index);
        onChanged();
      } else {
        this.optionsBuilder_.remove(index);
      } 
      return this;
    }
    
    public Option.Builder getOptionsBuilder(int index) {
      return getOptionsFieldBuilder().getBuilder(index);
    }
    
    public OptionOrBuilder getOptionsOrBuilder(int index) {
      if (this.optionsBuilder_ == null)
        return this.options_.get(index); 
      return this.optionsBuilder_.getMessageOrBuilder(index);
    }
    
    public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
      if (this.optionsBuilder_ != null)
        return this.optionsBuilder_.getMessageOrBuilderList(); 
      return Collections.unmodifiableList((List)this.options_);
    }
    
    public Option.Builder addOptionsBuilder() {
      return getOptionsFieldBuilder().addBuilder(Option.getDefaultInstance());
    }
    
    public Option.Builder addOptionsBuilder(int index) {
      return getOptionsFieldBuilder().addBuilder(index, Option.getDefaultInstance());
    }
    
    public List<Option.Builder> getOptionsBuilderList() {
      return getOptionsFieldBuilder().getBuilderList();
    }
    
    private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() {
      if (this.optionsBuilder_ == null) {
        this.optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean());
        this.options_ = null;
      } 
      return this.optionsBuilder_;
    }
    
    public String getJsonName() {
      Object ref = this.jsonName_;
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.jsonName_ = s;
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getJsonNameBytes() {
      Object ref = this.jsonName_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.jsonName_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setJsonName(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.jsonName_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearJsonName() {
      this.jsonName_ = Field.getDefaultInstance().getJsonName();
      onChanged();
      return this;
    }
    
    public Builder setJsonNameBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.jsonName_ = value;
      onChanged();
      return this;
    }
    
    public String getDefaultValue() {
      Object ref = this.defaultValue_;
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.defaultValue_ = s;
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getDefaultValueBytes() {
      Object ref = this.defaultValue_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.defaultValue_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setDefaultValue(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.defaultValue_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearDefaultValue() {
      this.defaultValue_ = Field.getDefaultInstance().getDefaultValue();
      onChanged();
      return this;
    }
    
    public Builder setDefaultValueBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.defaultValue_ = value;
      onChanged();
      return this;
    }
    
    public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }
    
    public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }
  }
  
  private static final Field DEFAULT_INSTANCE = new Field();
  
  public static Field getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<Field> PARSER = new AbstractParser<Field>() {
      public Field parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        Field.Builder builder = Field.newBuilder();
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
  
  public static Parser<Field> parser() {
    return PARSER;
  }
  
  public Parser<Field> getParserForType() {
    return PARSER;
  }
  
  public Field getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
