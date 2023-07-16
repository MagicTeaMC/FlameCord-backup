package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Option extends GeneratedMessageV3 implements OptionOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int NAME_FIELD_NUMBER = 1;
  
  private volatile Object name_;
  
  public static final int VALUE_FIELD_NUMBER = 2;
  
  private Any value_;
  
  private byte memoizedIsInitialized;
  
  private Option(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private Option() {
    this.memoizedIsInitialized = -1;
    this.name_ = "";
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new Option();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return TypeProto.internal_static_google_protobuf_Option_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return TypeProto.internal_static_google_protobuf_Option_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Option.class, (Class)Builder.class);
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
  
  public boolean hasValue() {
    return (this.value_ != null);
  }
  
  public Any getValue() {
    return (this.value_ == null) ? Any.getDefaultInstance() : this.value_;
  }
  
  public AnyOrBuilder getValueOrBuilder() {
    return getValue();
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
    if (!GeneratedMessageV3.isStringEmpty(this.name_))
      GeneratedMessageV3.writeString(output, 1, this.name_); 
    if (this.value_ != null)
      output.writeMessage(2, getValue()); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (!GeneratedMessageV3.isStringEmpty(this.name_))
      size += GeneratedMessageV3.computeStringSize(1, this.name_); 
    if (this.value_ != null)
      size += 
        CodedOutputStream.computeMessageSize(2, getValue()); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Option))
      return super.equals(obj); 
    Option other = (Option)obj;
    if (!getName().equals(other.getName()))
      return false; 
    if (hasValue() != other.hasValue())
      return false; 
    if (hasValue() && 
      
      !getValue().equals(other.getValue()))
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
    hash = 53 * hash + getName().hashCode();
    if (hasValue()) {
      hash = 37 * hash + 2;
      hash = 53 * hash + getValue().hashCode();
    } 
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static Option parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Option parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Option parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Option parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Option parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Option parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Option parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Option>parseWithIOException(PARSER, input);
  }
  
  public static Option parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Option>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Option parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Option>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static Option parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Option>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Option parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Option>parseWithIOException(PARSER, input);
  }
  
  public static Option parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Option>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(Option prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements OptionOrBuilder {
    private Object name_;
    
    private Any value_;
    
    private SingleFieldBuilderV3<Any, Any.Builder, AnyOrBuilder> valueBuilder_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return TypeProto.internal_static_google_protobuf_Option_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_Option_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)Option.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.name_ = "";
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.name_ = "";
    }
    
    public Builder clear() {
      super.clear();
      this.name_ = "";
      if (this.valueBuilder_ == null) {
        this.value_ = null;
      } else {
        this.value_ = null;
        this.valueBuilder_ = null;
      } 
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return TypeProto.internal_static_google_protobuf_Option_descriptor;
    }
    
    public Option getDefaultInstanceForType() {
      return Option.getDefaultInstance();
    }
    
    public Option build() {
      Option result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public Option buildPartial() {
      Option result = new Option(this);
      result.name_ = this.name_;
      if (this.valueBuilder_ == null) {
        result.value_ = this.value_;
      } else {
        result.value_ = this.valueBuilder_.build();
      } 
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
      if (other instanceof Option)
        return mergeFrom((Option)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(Option other) {
      if (other == Option.getDefaultInstance())
        return this; 
      if (!other.getName().isEmpty()) {
        this.name_ = other.name_;
        onChanged();
      } 
      if (other.hasValue())
        mergeValue(other.getValue()); 
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
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              continue;
            case 10:
              this.name_ = input.readStringRequireUtf8();
              continue;
            case 18:
              input.readMessage(getValueFieldBuilder().getBuilder(), extensionRegistry);
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
      this.name_ = Option.getDefaultInstance().getName();
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
    
    public boolean hasValue() {
      return (this.valueBuilder_ != null || this.value_ != null);
    }
    
    public Any getValue() {
      if (this.valueBuilder_ == null)
        return (this.value_ == null) ? Any.getDefaultInstance() : this.value_; 
      return this.valueBuilder_.getMessage();
    }
    
    public Builder setValue(Any value) {
      if (this.valueBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        this.value_ = value;
        onChanged();
      } else {
        this.valueBuilder_.setMessage(value);
      } 
      return this;
    }
    
    public Builder setValue(Any.Builder builderForValue) {
      if (this.valueBuilder_ == null) {
        this.value_ = builderForValue.build();
        onChanged();
      } else {
        this.valueBuilder_.setMessage(builderForValue.build());
      } 
      return this;
    }
    
    public Builder mergeValue(Any value) {
      if (this.valueBuilder_ == null) {
        if (this.value_ != null) {
          this
            .value_ = Any.newBuilder(this.value_).mergeFrom(value).buildPartial();
        } else {
          this.value_ = value;
        } 
        onChanged();
      } else {
        this.valueBuilder_.mergeFrom(value);
      } 
      return this;
    }
    
    public Builder clearValue() {
      if (this.valueBuilder_ == null) {
        this.value_ = null;
        onChanged();
      } else {
        this.value_ = null;
        this.valueBuilder_ = null;
      } 
      return this;
    }
    
    public Any.Builder getValueBuilder() {
      onChanged();
      return getValueFieldBuilder().getBuilder();
    }
    
    public AnyOrBuilder getValueOrBuilder() {
      if (this.valueBuilder_ != null)
        return this.valueBuilder_.getMessageOrBuilder(); 
      return (this.value_ == null) ? 
        Any.getDefaultInstance() : this.value_;
    }
    
    private SingleFieldBuilderV3<Any, Any.Builder, AnyOrBuilder> getValueFieldBuilder() {
      if (this.valueBuilder_ == null) {
        this
          
          .valueBuilder_ = new SingleFieldBuilderV3<>(getValue(), getParentForChildren(), isClean());
        this.value_ = null;
      } 
      return this.valueBuilder_;
    }
    
    public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }
    
    public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }
  }
  
  private static final Option DEFAULT_INSTANCE = new Option();
  
  public static Option getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<Option> PARSER = new AbstractParser<Option>() {
      public Option parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        Option.Builder builder = Option.newBuilder();
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
  
  public static Parser<Option> parser() {
    return PARSER;
  }
  
  public Parser<Option> getParserForType() {
    return PARSER;
  }
  
  public Option getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
