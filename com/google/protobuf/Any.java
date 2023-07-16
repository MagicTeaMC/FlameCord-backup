package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Any extends GeneratedMessageV3 implements AnyOrBuilder {
  private static final long serialVersionUID = 0L;
  
  private volatile Message cachedUnpackValue;
  
  public static final int TYPE_URL_FIELD_NUMBER = 1;
  
  private volatile Object typeUrl_;
  
  public static final int VALUE_FIELD_NUMBER = 2;
  
  private ByteString value_;
  
  private byte memoizedIsInitialized;
  
  private Any(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private Any() {
    this.memoizedIsInitialized = -1;
    this.typeUrl_ = "";
    this.value_ = ByteString.EMPTY;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new Any();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return AnyProto.internal_static_google_protobuf_Any_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return AnyProto.internal_static_google_protobuf_Any_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Any.class, (Class)Builder.class);
  }
  
  private static String getTypeUrl(String typeUrlPrefix, Descriptors.Descriptor descriptor) {
    return typeUrlPrefix.endsWith("/") ? (typeUrlPrefix + descriptor.getFullName()) : (typeUrlPrefix + "/" + descriptor.getFullName());
  }
  
  private static String getTypeNameFromTypeUrl(String typeUrl) {
    int pos = typeUrl.lastIndexOf('/');
    return (pos == -1) ? "" : typeUrl.substring(pos + 1);
  }
  
  public static <T extends Message> Any pack(T message) {
    return newBuilder().setTypeUrl(getTypeUrl("type.googleapis.com", message.getDescriptorForType())).setValue(message.toByteString()).build();
  }
  
  public static <T extends Message> Any pack(T message, String typeUrlPrefix) {
    return newBuilder().setTypeUrl(getTypeUrl(typeUrlPrefix, message.getDescriptorForType())).setValue(message.toByteString()).build();
  }
  
  public <T extends Message> boolean is(Class<T> clazz) {
    Message message = Internal.<Message>getDefaultInstance(clazz);
    return getTypeNameFromTypeUrl(getTypeUrl()).equals(message.getDescriptorForType().getFullName());
  }
  
  public <T extends Message> T unpack(Class<T> clazz) throws InvalidProtocolBufferException {
    boolean invalidClazz = false;
    if (this.cachedUnpackValue != null) {
      if (this.cachedUnpackValue.getClass() == clazz)
        return (T)this.cachedUnpackValue; 
      invalidClazz = true;
    } 
    if (invalidClazz || !is(clazz))
      throw new InvalidProtocolBufferException("Type of the Any message does not match the given class."); 
    Message message1 = Internal.<Message>getDefaultInstance(clazz);
    Message message2 = message1.getParserForType().parseFrom(getValue());
    this.cachedUnpackValue = message2;
    return (T)message2;
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
    if (!GeneratedMessageV3.isStringEmpty(this.typeUrl_))
      GeneratedMessageV3.writeString(output, 1, this.typeUrl_); 
    if (!this.value_.isEmpty())
      output.writeBytes(2, this.value_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (!GeneratedMessageV3.isStringEmpty(this.typeUrl_))
      size += GeneratedMessageV3.computeStringSize(1, this.typeUrl_); 
    if (!this.value_.isEmpty())
      size += 
        CodedOutputStream.computeBytesSize(2, this.value_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Any))
      return super.equals(obj); 
    Any other = (Any)obj;
    if (!getTypeUrl().equals(other.getTypeUrl()))
      return false; 
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
    hash = 53 * hash + getTypeUrl().hashCode();
    hash = 37 * hash + 2;
    hash = 53 * hash + getValue().hashCode();
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static Any parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Any parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Any parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Any parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Any parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Any parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Any parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Any>parseWithIOException(PARSER, input);
  }
  
  public static Any parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Any>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Any parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Any>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static Any parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Any>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Any parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Any>parseWithIOException(PARSER, input);
  }
  
  public static Any parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Any>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(Any prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements AnyOrBuilder {
    private Object typeUrl_;
    
    private ByteString value_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return AnyProto.internal_static_google_protobuf_Any_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return AnyProto.internal_static_google_protobuf_Any_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)Any.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.typeUrl_ = "";
      this.value_ = ByteString.EMPTY;
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.typeUrl_ = "";
      this.value_ = ByteString.EMPTY;
    }
    
    public Builder clear() {
      super.clear();
      this.typeUrl_ = "";
      this.value_ = ByteString.EMPTY;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return AnyProto.internal_static_google_protobuf_Any_descriptor;
    }
    
    public Any getDefaultInstanceForType() {
      return Any.getDefaultInstance();
    }
    
    public Any build() {
      Any result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public Any buildPartial() {
      Any result = new Any(this);
      result.typeUrl_ = this.typeUrl_;
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
      if (other instanceof Any)
        return mergeFrom((Any)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(Any other) {
      if (other == Any.getDefaultInstance())
        return this; 
      if (!other.getTypeUrl().isEmpty()) {
        this.typeUrl_ = other.typeUrl_;
        onChanged();
      } 
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
              this.typeUrl_ = input.readStringRequireUtf8();
              continue;
            case 18:
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
      this.typeUrl_ = Any.getDefaultInstance().getTypeUrl();
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
      this.value_ = Any.getDefaultInstance().getValue();
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
  
  private static final Any DEFAULT_INSTANCE = new Any();
  
  public static Any getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<Any> PARSER = new AbstractParser<Any>() {
      public Any parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        Any.Builder builder = Any.newBuilder();
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
  
  public static Parser<Any> parser() {
    return PARSER;
  }
  
  public Parser<Any> getParserForType() {
    return PARSER;
  }
  
  public Any getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
