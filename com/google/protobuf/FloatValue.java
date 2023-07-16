package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class FloatValue extends GeneratedMessageV3 implements FloatValueOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int VALUE_FIELD_NUMBER = 1;
  
  private float value_;
  
  private byte memoizedIsInitialized;
  
  private FloatValue(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private FloatValue() {
    this.memoizedIsInitialized = -1;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new FloatValue();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return WrappersProto.internal_static_google_protobuf_FloatValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)FloatValue.class, (Class)Builder.class);
  }
  
  public float getValue() {
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
    if (Float.floatToRawIntBits(this.value_) != 0)
      output.writeFloat(1, this.value_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (Float.floatToRawIntBits(this.value_) != 0)
      size += 
        CodedOutputStream.computeFloatSize(1, this.value_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof FloatValue))
      return super.equals(obj); 
    FloatValue other = (FloatValue)obj;
    if (Float.floatToIntBits(getValue()) != 
      Float.floatToIntBits(other
        .getValue()))
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
    hash = 53 * hash + Float.floatToIntBits(
        getValue());
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static FloatValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static FloatValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static FloatValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static FloatValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static FloatValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static FloatValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static FloatValue parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<FloatValue>parseWithIOException(PARSER, input);
  }
  
  public static FloatValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<FloatValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static FloatValue parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<FloatValue>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static FloatValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<FloatValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static FloatValue parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<FloatValue>parseWithIOException(PARSER, input);
  }
  
  public static FloatValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<FloatValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(FloatValue prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FloatValueOrBuilder {
    private float value_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_FloatValue_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)FloatValue.class, (Class)Builder.class);
    }
    
    private Builder() {}
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }
    
    public Builder clear() {
      super.clear();
      this.value_ = 0.0F;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return WrappersProto.internal_static_google_protobuf_FloatValue_descriptor;
    }
    
    public FloatValue getDefaultInstanceForType() {
      return FloatValue.getDefaultInstance();
    }
    
    public FloatValue build() {
      FloatValue result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public FloatValue buildPartial() {
      FloatValue result = new FloatValue(this);
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
      if (other instanceof FloatValue)
        return mergeFrom((FloatValue)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(FloatValue other) {
      if (other == FloatValue.getDefaultInstance())
        return this; 
      if (other.getValue() != 0.0F)
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
            case 13:
              this.value_ = input.readFloat();
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
    
    public float getValue() {
      return this.value_;
    }
    
    public Builder setValue(float value) {
      this.value_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearValue() {
      this.value_ = 0.0F;
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
  
  private static final FloatValue DEFAULT_INSTANCE = new FloatValue();
  
  public static FloatValue getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  public static FloatValue of(float value) {
    return newBuilder().setValue(value).build();
  }
  
  private static final Parser<FloatValue> PARSER = new AbstractParser<FloatValue>() {
      public FloatValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        FloatValue.Builder builder = FloatValue.newBuilder();
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
  
  public static Parser<FloatValue> parser() {
    return PARSER;
  }
  
  public Parser<FloatValue> getParserForType() {
    return PARSER;
  }
  
  public FloatValue getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
