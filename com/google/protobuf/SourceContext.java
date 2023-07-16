package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class SourceContext extends GeneratedMessageV3 implements SourceContextOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int FILE_NAME_FIELD_NUMBER = 1;
  
  private volatile Object fileName_;
  
  private byte memoizedIsInitialized;
  
  private SourceContext(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private SourceContext() {
    this.memoizedIsInitialized = -1;
    this.fileName_ = "";
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new SourceContext();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return SourceContextProto.internal_static_google_protobuf_SourceContext_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)SourceContext.class, (Class)Builder.class);
  }
  
  public String getFileName() {
    Object ref = this.fileName_;
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    this.fileName_ = s;
    return s;
  }
  
  public ByteString getFileNameBytes() {
    Object ref = this.fileName_;
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      this.fileName_ = b;
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
    if (!GeneratedMessageV3.isStringEmpty(this.fileName_))
      GeneratedMessageV3.writeString(output, 1, this.fileName_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (!GeneratedMessageV3.isStringEmpty(this.fileName_))
      size += GeneratedMessageV3.computeStringSize(1, this.fileName_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof SourceContext))
      return super.equals(obj); 
    SourceContext other = (SourceContext)obj;
    if (!getFileName().equals(other.getFileName()))
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
    hash = 53 * hash + getFileName().hashCode();
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static SourceContext parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static SourceContext parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static SourceContext parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static SourceContext parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static SourceContext parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static SourceContext parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static SourceContext parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<SourceContext>parseWithIOException(PARSER, input);
  }
  
  public static SourceContext parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<SourceContext>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static SourceContext parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<SourceContext>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static SourceContext parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<SourceContext>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static SourceContext parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<SourceContext>parseWithIOException(PARSER, input);
  }
  
  public static SourceContext parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<SourceContext>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(SourceContext prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements SourceContextOrBuilder {
    private Object fileName_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return SourceContextProto.internal_static_google_protobuf_SourceContext_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)SourceContext.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.fileName_ = "";
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.fileName_ = "";
    }
    
    public Builder clear() {
      super.clear();
      this.fileName_ = "";
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return SourceContextProto.internal_static_google_protobuf_SourceContext_descriptor;
    }
    
    public SourceContext getDefaultInstanceForType() {
      return SourceContext.getDefaultInstance();
    }
    
    public SourceContext build() {
      SourceContext result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public SourceContext buildPartial() {
      SourceContext result = new SourceContext(this);
      result.fileName_ = this.fileName_;
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
      if (other instanceof SourceContext)
        return mergeFrom((SourceContext)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(SourceContext other) {
      if (other == SourceContext.getDefaultInstance())
        return this; 
      if (!other.getFileName().isEmpty()) {
        this.fileName_ = other.fileName_;
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
              this.fileName_ = input.readStringRequireUtf8();
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
    
    public String getFileName() {
      Object ref = this.fileName_;
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.fileName_ = s;
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getFileNameBytes() {
      Object ref = this.fileName_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.fileName_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setFileName(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.fileName_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearFileName() {
      this.fileName_ = SourceContext.getDefaultInstance().getFileName();
      onChanged();
      return this;
    }
    
    public Builder setFileNameBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.fileName_ = value;
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
  
  private static final SourceContext DEFAULT_INSTANCE = new SourceContext();
  
  public static SourceContext getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<SourceContext> PARSER = new AbstractParser<SourceContext>() {
      public SourceContext parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        SourceContext.Builder builder = SourceContext.newBuilder();
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
  
  public static Parser<SourceContext> parser() {
    return PARSER;
  }
  
  public Parser<SourceContext> getParserForType() {
    return PARSER;
  }
  
  public SourceContext getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
