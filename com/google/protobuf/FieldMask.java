package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.List;

public final class FieldMask extends GeneratedMessageV3 implements FieldMaskOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int PATHS_FIELD_NUMBER = 1;
  
  private LazyStringList paths_;
  
  private byte memoizedIsInitialized;
  
  private FieldMask(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private FieldMask() {
    this.memoizedIsInitialized = -1;
    this.paths_ = LazyStringArrayList.EMPTY;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new FieldMask();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return FieldMaskProto.internal_static_google_protobuf_FieldMask_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)FieldMask.class, (Class)Builder.class);
  }
  
  public ProtocolStringList getPathsList() {
    return this.paths_;
  }
  
  public int getPathsCount() {
    return this.paths_.size();
  }
  
  public String getPaths(int index) {
    return this.paths_.get(index);
  }
  
  public ByteString getPathsBytes(int index) {
    return this.paths_.getByteString(index);
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
    for (int i = 0; i < this.paths_.size(); i++)
      GeneratedMessageV3.writeString(output, 1, this.paths_.getRaw(i)); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    int dataSize = 0;
    for (int i = 0; i < this.paths_.size(); i++)
      dataSize += computeStringSizeNoTag(this.paths_.getRaw(i)); 
    size += dataSize;
    size += 1 * getPathsList().size();
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof FieldMask))
      return super.equals(obj); 
    FieldMask other = (FieldMask)obj;
    if (!getPathsList().equals(other.getPathsList()))
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
    if (getPathsCount() > 0) {
      hash = 37 * hash + 1;
      hash = 53 * hash + getPathsList().hashCode();
    } 
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static FieldMask parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static FieldMask parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static FieldMask parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static FieldMask parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static FieldMask parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static FieldMask parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static FieldMask parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<FieldMask>parseWithIOException(PARSER, input);
  }
  
  public static FieldMask parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<FieldMask>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static FieldMask parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<FieldMask>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static FieldMask parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<FieldMask>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static FieldMask parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<FieldMask>parseWithIOException(PARSER, input);
  }
  
  public static FieldMask parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<FieldMask>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(FieldMask prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements FieldMaskOrBuilder {
    private int bitField0_;
    
    private LazyStringList paths_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return FieldMaskProto.internal_static_google_protobuf_FieldMask_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)FieldMask.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.paths_ = LazyStringArrayList.EMPTY;
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.paths_ = LazyStringArrayList.EMPTY;
    }
    
    public Builder clear() {
      super.clear();
      this.paths_ = LazyStringArrayList.EMPTY;
      this.bitField0_ &= 0xFFFFFFFE;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return FieldMaskProto.internal_static_google_protobuf_FieldMask_descriptor;
    }
    
    public FieldMask getDefaultInstanceForType() {
      return FieldMask.getDefaultInstance();
    }
    
    public FieldMask build() {
      FieldMask result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public FieldMask buildPartial() {
      FieldMask result = new FieldMask(this);
      int from_bitField0_ = this.bitField0_;
      if ((this.bitField0_ & 0x1) != 0) {
        this.paths_ = this.paths_.getUnmodifiableView();
        this.bitField0_ &= 0xFFFFFFFE;
      } 
      result.paths_ = this.paths_;
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
      if (other instanceof FieldMask)
        return mergeFrom((FieldMask)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(FieldMask other) {
      if (other == FieldMask.getDefaultInstance())
        return this; 
      if (!other.paths_.isEmpty()) {
        if (this.paths_.isEmpty()) {
          this.paths_ = other.paths_;
          this.bitField0_ &= 0xFFFFFFFE;
        } else {
          ensurePathsIsMutable();
          this.paths_.addAll(other.paths_);
        } 
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
          String s;
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              continue;
            case 10:
              s = input.readStringRequireUtf8();
              ensurePathsIsMutable();
              this.paths_.add(s);
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
    
    private void ensurePathsIsMutable() {
      if ((this.bitField0_ & 0x1) == 0) {
        this.paths_ = new LazyStringArrayList(this.paths_);
        this.bitField0_ |= 0x1;
      } 
    }
    
    public ProtocolStringList getPathsList() {
      return this.paths_.getUnmodifiableView();
    }
    
    public int getPathsCount() {
      return this.paths_.size();
    }
    
    public String getPaths(int index) {
      return this.paths_.get(index);
    }
    
    public ByteString getPathsBytes(int index) {
      return this.paths_.getByteString(index);
    }
    
    public Builder setPaths(int index, String value) {
      if (value == null)
        throw new NullPointerException(); 
      ensurePathsIsMutable();
      this.paths_.set(index, value);
      onChanged();
      return this;
    }
    
    public Builder addPaths(String value) {
      if (value == null)
        throw new NullPointerException(); 
      ensurePathsIsMutable();
      this.paths_.add(value);
      onChanged();
      return this;
    }
    
    public Builder addAllPaths(Iterable<String> values) {
      ensurePathsIsMutable();
      AbstractMessageLite.Builder.addAll(values, this.paths_);
      onChanged();
      return this;
    }
    
    public Builder clearPaths() {
      this.paths_ = LazyStringArrayList.EMPTY;
      this.bitField0_ &= 0xFFFFFFFE;
      onChanged();
      return this;
    }
    
    public Builder addPathsBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      ensurePathsIsMutable();
      this.paths_.add(value);
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
  
  private static final FieldMask DEFAULT_INSTANCE = new FieldMask();
  
  public static FieldMask getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<FieldMask> PARSER = new AbstractParser<FieldMask>() {
      public FieldMask parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        FieldMask.Builder builder = FieldMask.newBuilder();
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
  
  public static Parser<FieldMask> parser() {
    return PARSER;
  }
  
  public Parser<FieldMask> getParserForType() {
    return PARSER;
  }
  
  public FieldMask getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
