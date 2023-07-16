package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class DoubleValue extends GeneratedMessageV3 implements DoubleValueOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int VALUE_FIELD_NUMBER = 1;
  
  private double value_;
  
  private byte memoizedIsInitialized;
  
  private DoubleValue(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private DoubleValue() {
    this.memoizedIsInitialized = -1;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new DoubleValue();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return WrappersProto.internal_static_google_protobuf_DoubleValue_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return WrappersProto.internal_static_google_protobuf_DoubleValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)DoubleValue.class, (Class)Builder.class);
  }
  
  public double getValue() {
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
    if (Double.doubleToRawLongBits(this.value_) != 0L)
      output.writeDouble(1, this.value_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (Double.doubleToRawLongBits(this.value_) != 0L)
      size += 
        CodedOutputStream.computeDoubleSize(1, this.value_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof DoubleValue))
      return super.equals(obj); 
    DoubleValue other = (DoubleValue)obj;
    if (Double.doubleToLongBits(getValue()) != 
      Double.doubleToLongBits(other
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
    hash = 53 * hash + Internal.hashLong(
        Double.doubleToLongBits(getValue()));
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static DoubleValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static DoubleValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static DoubleValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static DoubleValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static DoubleValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static DoubleValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static DoubleValue parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<DoubleValue>parseWithIOException(PARSER, input);
  }
  
  public static DoubleValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<DoubleValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static DoubleValue parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<DoubleValue>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static DoubleValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<DoubleValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static DoubleValue parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<DoubleValue>parseWithIOException(PARSER, input);
  }
  
  public static DoubleValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<DoubleValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(DoubleValue prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements DoubleValueOrBuilder {
    private double value_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_DoubleValue_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_DoubleValue_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)DoubleValue.class, (Class)Builder.class);
    }
    
    private Builder() {}
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }
    
    public Builder clear() {
      super.clear();
      this.value_ = 0.0D;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return WrappersProto.internal_static_google_protobuf_DoubleValue_descriptor;
    }
    
    public DoubleValue getDefaultInstanceForType() {
      return DoubleValue.getDefaultInstance();
    }
    
    public DoubleValue build() {
      DoubleValue result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public DoubleValue buildPartial() {
      DoubleValue result = new DoubleValue(this);
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
      if (other instanceof DoubleValue)
        return mergeFrom((DoubleValue)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(DoubleValue other) {
      if (other == DoubleValue.getDefaultInstance())
        return this; 
      if (other.getValue() != 0.0D)
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
            case 9:
              this.value_ = input.readDouble();
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
    
    public double getValue() {
      return this.value_;
    }
    
    public Builder setValue(double value) {
      this.value_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearValue() {
      this.value_ = 0.0D;
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
  
  private static final DoubleValue DEFAULT_INSTANCE = new DoubleValue();
  
  public static DoubleValue getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  public static DoubleValue of(double value) {
    return newBuilder().setValue(value).build();
  }
  
  private static final Parser<DoubleValue> PARSER = new AbstractParser<DoubleValue>() {
      public DoubleValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        DoubleValue.Builder builder = DoubleValue.newBuilder();
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
  
  public static Parser<DoubleValue> parser() {
    return PARSER;
  }
  
  public Parser<DoubleValue> getParserForType() {
    return PARSER;
  }
  
  public DoubleValue getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
