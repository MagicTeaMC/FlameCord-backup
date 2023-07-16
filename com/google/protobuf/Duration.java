package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class Duration extends GeneratedMessageV3 implements DurationOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int SECONDS_FIELD_NUMBER = 1;
  
  private long seconds_;
  
  public static final int NANOS_FIELD_NUMBER = 2;
  
  private int nanos_;
  
  private byte memoizedIsInitialized;
  
  private Duration(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private Duration() {
    this.memoizedIsInitialized = -1;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new Duration();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return DurationProto.internal_static_google_protobuf_Duration_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return DurationProto.internal_static_google_protobuf_Duration_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Duration.class, (Class)Builder.class);
  }
  
  public long getSeconds() {
    return this.seconds_;
  }
  
  public int getNanos() {
    return this.nanos_;
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
    if (this.seconds_ != 0L)
      output.writeInt64(1, this.seconds_); 
    if (this.nanos_ != 0)
      output.writeInt32(2, this.nanos_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (this.seconds_ != 0L)
      size += 
        CodedOutputStream.computeInt64Size(1, this.seconds_); 
    if (this.nanos_ != 0)
      size += 
        CodedOutputStream.computeInt32Size(2, this.nanos_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Duration))
      return super.equals(obj); 
    Duration other = (Duration)obj;
    if (getSeconds() != other
      .getSeconds())
      return false; 
    if (getNanos() != other
      .getNanos())
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
        getSeconds());
    hash = 37 * hash + 2;
    hash = 53 * hash + getNanos();
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static Duration parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Duration parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Duration parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Duration parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Duration parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Duration parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Duration parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Duration>parseWithIOException(PARSER, input);
  }
  
  public static Duration parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Duration>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Duration parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Duration>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static Duration parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Duration>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Duration parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Duration>parseWithIOException(PARSER, input);
  }
  
  public static Duration parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Duration>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(Duration prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements DurationOrBuilder {
    private long seconds_;
    
    private int nanos_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return DurationProto.internal_static_google_protobuf_Duration_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return DurationProto.internal_static_google_protobuf_Duration_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)Duration.class, (Class)Builder.class);
    }
    
    private Builder() {}
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }
    
    public Builder clear() {
      super.clear();
      this.seconds_ = 0L;
      this.nanos_ = 0;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return DurationProto.internal_static_google_protobuf_Duration_descriptor;
    }
    
    public Duration getDefaultInstanceForType() {
      return Duration.getDefaultInstance();
    }
    
    public Duration build() {
      Duration result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public Duration buildPartial() {
      Duration result = new Duration(this);
      result.seconds_ = this.seconds_;
      result.nanos_ = this.nanos_;
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
      if (other instanceof Duration)
        return mergeFrom((Duration)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(Duration other) {
      if (other == Duration.getDefaultInstance())
        return this; 
      if (other.getSeconds() != 0L)
        setSeconds(other.getSeconds()); 
      if (other.getNanos() != 0)
        setNanos(other.getNanos()); 
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
              this.seconds_ = input.readInt64();
              continue;
            case 16:
              this.nanos_ = input.readInt32();
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
    
    public long getSeconds() {
      return this.seconds_;
    }
    
    public Builder setSeconds(long value) {
      this.seconds_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearSeconds() {
      this.seconds_ = 0L;
      onChanged();
      return this;
    }
    
    public int getNanos() {
      return this.nanos_;
    }
    
    public Builder setNanos(int value) {
      this.nanos_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearNanos() {
      this.nanos_ = 0;
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
  
  private static final Duration DEFAULT_INSTANCE = new Duration();
  
  public static Duration getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<Duration> PARSER = new AbstractParser<Duration>() {
      public Duration parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        Duration.Builder builder = Duration.newBuilder();
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
  
  public static Parser<Duration> parser() {
    return PARSER;
  }
  
  public Parser<Duration> getParserForType() {
    return PARSER;
  }
  
  public Duration getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
