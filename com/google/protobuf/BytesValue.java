package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class BytesValue extends GeneratedMessageV3 implements BytesValueOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int VALUE_FIELD_NUMBER = 1;
  
  private ByteString value_;
  
  private byte memoizedIsInitialized;
  
  private BytesValue(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private BytesValue() {
    this.memoizedIsInitialized = -1;
    this.value_ = ByteString.EMPTY;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new BytesValue();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return WrappersProto.internal_static_google_protobuf_BytesValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)BytesValue.class, (Class)Builder.class);
  }
  
  public ByteString getValue() {
    return this.value_;
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
    if (!this.value_.isEmpty())
      output.writeBytes(1, this.value_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (!this.value_.isEmpty())
      size += 
        CodedOutputStream.computeBytesSize(1, this.value_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof BytesValue))
      return super.equals(obj); 
    BytesValue other = (BytesValue)obj;
    if (!getValue().equals(other.getValue()))
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
    hash = 53 * hash + getValue().hashCode();
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static BytesValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static BytesValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static BytesValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static BytesValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static BytesValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static BytesValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static BytesValue parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<BytesValue>parseWithIOException(PARSER, input);
  }
  
  public static BytesValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<BytesValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static BytesValue parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<BytesValue>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static BytesValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<BytesValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static BytesValue parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<BytesValue>parseWithIOException(PARSER, input);
  }
  
  public static BytesValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<BytesValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(BytesValue prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements BytesValueOrBuilder {
    private ByteString value_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_BytesValue_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)BytesValue.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.value_ = ByteString.EMPTY;
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.value_ = ByteString.EMPTY;
    }
    
    public Builder clear() {
      super.clear();
      this.value_ = ByteString.EMPTY;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return WrappersProto.internal_static_google_protobuf_BytesValue_descriptor;
    }
    
    public BytesValue getDefaultInstanceForType() {
      return BytesValue.getDefaultInstance();
    }
    
    public BytesValue build() {
      BytesValue result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public BytesValue buildPartial() {
      BytesValue result = new BytesValue(this);
      result.value_ = this.value_;
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
      if (other instanceof BytesValue)
        return mergeFrom((BytesValue)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(BytesValue other) {
      if (other == BytesValue.getDefaultInstance())
        return this; 
      if (other.getValue() != ByteString.EMPTY)
        setValue(other.getValue()); 
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
              this.value_ = input.readBytes();
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
    
    public ByteString getValue() {
      return this.value_;
    }
    
    public Builder setValue(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      this.value_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearValue() {
      this.value_ = BytesValue.getDefaultInstance().getValue();
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
  
  private static final BytesValue DEFAULT_INSTANCE = new BytesValue();
  
  public static BytesValue getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  public static BytesValue of(ByteString value) {
    return newBuilder().setValue(value).build();
  }
  
  private static final Parser<BytesValue> PARSER = new AbstractParser<BytesValue>() {
      public BytesValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        BytesValue.Builder builder = BytesValue.newBuilder();
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
  
  public static Parser<BytesValue> parser() {
    return PARSER;
  }
  
  public Parser<BytesValue> getParserForType() {
    return PARSER;
  }
  
  public BytesValue getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
