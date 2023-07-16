package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Mixin extends GeneratedMessageV3 implements MixinOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int NAME_FIELD_NUMBER = 1;
  
  private volatile Object name_;
  
  public static final int ROOT_FIELD_NUMBER = 2;
  
  private volatile Object root_;
  
  private byte memoizedIsInitialized;
  
  private Mixin(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private Mixin() {
    this.memoizedIsInitialized = -1;
    this.name_ = "";
    this.root_ = "";
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new Mixin();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return ApiProto.internal_static_google_protobuf_Mixin_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return ApiProto.internal_static_google_protobuf_Mixin_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Mixin.class, (Class)Builder.class);
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
  
  public String getRoot() {
    Object ref = this.root_;
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    this.root_ = s;
    return s;
  }
  
  public ByteString getRootBytes() {
    Object ref = this.root_;
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      this.root_ = b;
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
    if (!GeneratedMessageV3.isStringEmpty(this.name_))
      GeneratedMessageV3.writeString(output, 1, this.name_); 
    if (!GeneratedMessageV3.isStringEmpty(this.root_))
      GeneratedMessageV3.writeString(output, 2, this.root_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (!GeneratedMessageV3.isStringEmpty(this.name_))
      size += GeneratedMessageV3.computeStringSize(1, this.name_); 
    if (!GeneratedMessageV3.isStringEmpty(this.root_))
      size += GeneratedMessageV3.computeStringSize(2, this.root_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Mixin))
      return super.equals(obj); 
    Mixin other = (Mixin)obj;
    if (!getName().equals(other.getName()))
      return false; 
    if (!getRoot().equals(other.getRoot()))
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
    hash = 37 * hash + 2;
    hash = 53 * hash + getRoot().hashCode();
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static Mixin parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Mixin parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Mixin parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Mixin parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Mixin parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Mixin parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Mixin parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Mixin>parseWithIOException(PARSER, input);
  }
  
  public static Mixin parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Mixin>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Mixin parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Mixin>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static Mixin parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Mixin>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Mixin parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Mixin>parseWithIOException(PARSER, input);
  }
  
  public static Mixin parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Mixin>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(Mixin prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MixinOrBuilder {
    private Object name_;
    
    private Object root_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return ApiProto.internal_static_google_protobuf_Mixin_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return ApiProto.internal_static_google_protobuf_Mixin_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)Mixin.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.name_ = "";
      this.root_ = "";
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.name_ = "";
      this.root_ = "";
    }
    
    public Builder clear() {
      super.clear();
      this.name_ = "";
      this.root_ = "";
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return ApiProto.internal_static_google_protobuf_Mixin_descriptor;
    }
    
    public Mixin getDefaultInstanceForType() {
      return Mixin.getDefaultInstance();
    }
    
    public Mixin build() {
      Mixin result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public Mixin buildPartial() {
      Mixin result = new Mixin(this);
      result.name_ = this.name_;
      result.root_ = this.root_;
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
      if (other instanceof Mixin)
        return mergeFrom((Mixin)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(Mixin other) {
      if (other == Mixin.getDefaultInstance())
        return this; 
      if (!other.getName().isEmpty()) {
        this.name_ = other.name_;
        onChanged();
      } 
      if (!other.getRoot().isEmpty()) {
        this.root_ = other.root_;
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
              this.name_ = input.readStringRequireUtf8();
              continue;
            case 18:
              this.root_ = input.readStringRequireUtf8();
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
      this.name_ = Mixin.getDefaultInstance().getName();
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
    
    public String getRoot() {
      Object ref = this.root_;
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.root_ = s;
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getRootBytes() {
      Object ref = this.root_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.root_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setRoot(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.root_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearRoot() {
      this.root_ = Mixin.getDefaultInstance().getRoot();
      onChanged();
      return this;
    }
    
    public Builder setRootBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.root_ = value;
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
  
  private static final Mixin DEFAULT_INSTANCE = new Mixin();
  
  public static Mixin getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<Mixin> PARSER = new AbstractParser<Mixin>() {
      public Mixin parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        Mixin.Builder builder = Mixin.newBuilder();
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
  
  public static Parser<Mixin> parser() {
    return PARSER;
  }
  
  public Parser<Mixin> getParserForType() {
    return PARSER;
  }
  
  public Mixin getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
