package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class StringValue extends GeneratedMessageV3 implements StringValueOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int VALUE_FIELD_NUMBER = 1;
  
  private volatile Object value_;
  
  private byte memoizedIsInitialized;
  
  private StringValue(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private StringValue() {
    this.memoizedIsInitialized = -1;
    this.value_ = "";
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new StringValue();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return WrappersProto.internal_static_google_protobuf_StringValue_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return WrappersProto.internal_static_google_protobuf_StringValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)StringValue.class, (Class)Builder.class);
  }
  
  public String getValue() {
    Object ref = this.value_;
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    this.value_ = s;
    return s;
  }
  
  public ByteString getValueBytes() {
    Object ref = this.value_;
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      this.value_ = b;
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
    if (!GeneratedMessageV3.isStringEmpty(this.value_))
      GeneratedMessageV3.writeString(output, 1, this.value_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (!GeneratedMessageV3.isStringEmpty(this.value_))
      size += GeneratedMessageV3.computeStringSize(1, this.value_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof StringValue))
      return super.equals(obj); 
    StringValue other = (StringValue)obj;
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
  
  public static StringValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static StringValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static StringValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static StringValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static StringValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static StringValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static StringValue parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<StringValue>parseWithIOException(PARSER, input);
  }
  
  public static StringValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<StringValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static StringValue parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<StringValue>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static StringValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<StringValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static StringValue parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<StringValue>parseWithIOException(PARSER, input);
  }
  
  public static StringValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<StringValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(StringValue prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements StringValueOrBuilder {
    private Object value_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return WrappersProto.internal_static_google_protobuf_StringValue_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return WrappersProto.internal_static_google_protobuf_StringValue_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)StringValue.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.value_ = "";
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.value_ = "";
    }
    
    public Builder clear() {
      super.clear();
      this.value_ = "";
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return WrappersProto.internal_static_google_protobuf_StringValue_descriptor;
    }
    
    public StringValue getDefaultInstanceForType() {
      return StringValue.getDefaultInstance();
    }
    
    public StringValue build() {
      StringValue result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public StringValue buildPartial() {
      StringValue result = new StringValue(this);
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
      if (other instanceof StringValue)
        return mergeFrom((StringValue)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(StringValue other) {
      if (other == StringValue.getDefaultInstance())
        return this; 
      if (!other.getValue().isEmpty()) {
        this.value_ = other.value_;
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
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              continue;
            case 10:
              this.value_ = input.readStringRequireUtf8();
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
    
    public String getValue() {
      Object ref = this.value_;
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.value_ = s;
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getValueBytes() {
      Object ref = this.value_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.value_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setValue(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.value_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearValue() {
      this.value_ = StringValue.getDefaultInstance().getValue();
      onChanged();
      return this;
    }
    
    public Builder setValueBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.value_ = value;
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
  
  private static final StringValue DEFAULT_INSTANCE = new StringValue();
  
  public static StringValue getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  public static StringValue of(String value) {
    return newBuilder().setValue(value).build();
  }
  
  private static final Parser<StringValue> PARSER = new AbstractParser<StringValue>() {
      public StringValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        StringValue.Builder builder = StringValue.newBuilder();
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
  
  public static Parser<StringValue> parser() {
    return PARSER;
  }
  
  public Parser<StringValue> getParserForType() {
    return PARSER;
  }
  
  public StringValue getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
