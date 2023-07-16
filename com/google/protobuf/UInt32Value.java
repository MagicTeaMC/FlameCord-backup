package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class UInt32Value extends GeneratedMessageV3 implements UInt32ValueOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int VALUE_FIELD_NUMBER = 1;
  
  private int value_;
  
  private byte memoizedIsInitialized;
  
  private UInt32Value(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private UInt32Value() {
    this.memoizedIsInitialized = -1;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new UInt32Value();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return WrappersProto.internal_static_google_protobuf_UInt32Value_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return WrappersProto.internal_static_google_protobuf_UInt32Value_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)UInt32Value.class, (Class)Builder.class);
  }
  
  public int getValue() {
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
    if (this.value_ != 0)
      output.writeUInt32(1, this.value_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (this.value_ != 0)
      size += 
        CodedOutputStream.computeUInt32Size(1, this.value_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof UInt32Value))
      return super.equals(obj); 
    UInt32Value other = (UInt32Value)obj;
    if (getValue() != other
      .getValue())
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
    hash = 53 * hash + getValue();
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static UInt32Value parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static UInt32Value parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static UInt32Value parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static UInt32Value parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static UInt32Value parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static UInt32Value parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static UInt32Value parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<UInt32Value>parseWithIOException(PARSER, input);
  }
  
  public static UInt32Value parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<UInt32Value>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static UInt32Value parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<UInt32Value>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static UInt32Value parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<UInt32Value>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static UInt32Value parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<UInt32Value>parseWithIOException(PARSER, input);
  }
  
  public static UInt32Value parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<UInt32Value>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(UInt32Value prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements UInt32ValueOrBuilder {
    private int value_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_UInt32Value_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_UInt32Value_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)UInt32Value.class, (Class)Builder.class);
    }
    
    private Builder() {}
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }
    
    public Builder clear() {
      super.clear();
      this.value_ = 0;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return WrappersProto.internal_static_google_protobuf_UInt32Value_descriptor;
    }
    
    public UInt32Value getDefaultInstanceForType() {
      return UInt32Value.getDefaultInstance();
    }
    
    public UInt32Value build() {
      UInt32Value result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public UInt32Value buildPartial() {
      UInt32Value result = new UInt32Value(this);
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
      if (other instanceof UInt32Value)
        return mergeFrom((UInt32Value)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(UInt32Value other) {
      if (other == UInt32Value.getDefaultInstance())
        return this; 
      if (other.getValue() != 0)
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
            case 8:
              this.value_ = input.readUInt32();
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
    
    public int getValue() {
      return this.value_;
    }
    
    public Builder setValue(int value) {
      this.value_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearValue() {
      this.value_ = 0;
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
  
  private static final UInt32Value DEFAULT_INSTANCE = new UInt32Value();
  
  public static UInt32Value getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  public static UInt32Value of(int value) {
    return newBuilder().setValue(value).build();
  }
  
  private static final Parser<UInt32Value> PARSER = new AbstractParser<UInt32Value>() {
      public UInt32Value parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        UInt32Value.Builder builder = UInt32Value.newBuilder();
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
  
  public static Parser<UInt32Value> parser() {
    return PARSER;
  }
  
  public Parser<UInt32Value> getParserForType() {
    return PARSER;
  }
  
  public UInt32Value getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
