package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Method extends GeneratedMessageV3 implements MethodOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int NAME_FIELD_NUMBER = 1;
  
  private volatile Object name_;
  
  public static final int REQUEST_TYPE_URL_FIELD_NUMBER = 2;
  
  private volatile Object requestTypeUrl_;
  
  public static final int REQUEST_STREAMING_FIELD_NUMBER = 3;
  
  private boolean requestStreaming_;
  
  public static final int RESPONSE_TYPE_URL_FIELD_NUMBER = 4;
  
  private volatile Object responseTypeUrl_;
  
  public static final int RESPONSE_STREAMING_FIELD_NUMBER = 5;
  
  private boolean responseStreaming_;
  
  public static final int OPTIONS_FIELD_NUMBER = 6;
  
  private List<Option> options_;
  
  public static final int SYNTAX_FIELD_NUMBER = 7;
  
  private int syntax_;
  
  private byte memoizedIsInitialized;
  
  private Method(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private Method() {
    this.memoizedIsInitialized = -1;
    this.name_ = "";
    this.requestTypeUrl_ = "";
    this.responseTypeUrl_ = "";
    this.options_ = Collections.emptyList();
    this.syntax_ = 0;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new Method();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return ApiProto.internal_static_google_protobuf_Method_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return ApiProto.internal_static_google_protobuf_Method_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Method.class, (Class)Builder.class);
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
  
  public String getRequestTypeUrl() {
    Object ref = this.requestTypeUrl_;
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    this.requestTypeUrl_ = s;
    return s;
  }
  
  public ByteString getRequestTypeUrlBytes() {
    Object ref = this.requestTypeUrl_;
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      this.requestTypeUrl_ = b;
      return b;
    } 
    return (ByteString)ref;
  }
  
  public boolean getRequestStreaming() {
    return this.requestStreaming_;
  }
  
  public String getResponseTypeUrl() {
    Object ref = this.responseTypeUrl_;
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    this.responseTypeUrl_ = s;
    return s;
  }
  
  public ByteString getResponseTypeUrlBytes() {
    Object ref = this.responseTypeUrl_;
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      this.responseTypeUrl_ = b;
      return b;
    } 
    return (ByteString)ref;
  }
  
  public boolean getResponseStreaming() {
    return this.responseStreaming_;
  }
  
  public List<Option> getOptionsList() {
    return this.options_;
  }
  
  public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
    return (List)this.options_;
  }
  
  public int getOptionsCount() {
    return this.options_.size();
  }
  
  public Option getOptions(int index) {
    return this.options_.get(index);
  }
  
  public OptionOrBuilder getOptionsOrBuilder(int index) {
    return this.options_.get(index);
  }
  
  public int getSyntaxValue() {
    return this.syntax_;
  }
  
  public Syntax getSyntax() {
    Syntax result = Syntax.valueOf(this.syntax_);
    return (result == null) ? Syntax.UNRECOGNIZED : result;
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
    if (!GeneratedMessageV3.isStringEmpty(this.requestTypeUrl_))
      GeneratedMessageV3.writeString(output, 2, this.requestTypeUrl_); 
    if (this.requestStreaming_)
      output.writeBool(3, this.requestStreaming_); 
    if (!GeneratedMessageV3.isStringEmpty(this.responseTypeUrl_))
      GeneratedMessageV3.writeString(output, 4, this.responseTypeUrl_); 
    if (this.responseStreaming_)
      output.writeBool(5, this.responseStreaming_); 
    for (int i = 0; i < this.options_.size(); i++)
      output.writeMessage(6, this.options_.get(i)); 
    if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber())
      output.writeEnum(7, this.syntax_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (!GeneratedMessageV3.isStringEmpty(this.name_))
      size += GeneratedMessageV3.computeStringSize(1, this.name_); 
    if (!GeneratedMessageV3.isStringEmpty(this.requestTypeUrl_))
      size += GeneratedMessageV3.computeStringSize(2, this.requestTypeUrl_); 
    if (this.requestStreaming_)
      size += 
        CodedOutputStream.computeBoolSize(3, this.requestStreaming_); 
    if (!GeneratedMessageV3.isStringEmpty(this.responseTypeUrl_))
      size += GeneratedMessageV3.computeStringSize(4, this.responseTypeUrl_); 
    if (this.responseStreaming_)
      size += 
        CodedOutputStream.computeBoolSize(5, this.responseStreaming_); 
    for (int i = 0; i < this.options_.size(); i++)
      size += 
        CodedOutputStream.computeMessageSize(6, this.options_.get(i)); 
    if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber())
      size += 
        CodedOutputStream.computeEnumSize(7, this.syntax_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Method))
      return super.equals(obj); 
    Method other = (Method)obj;
    if (!getName().equals(other.getName()))
      return false; 
    if (!getRequestTypeUrl().equals(other.getRequestTypeUrl()))
      return false; 
    if (getRequestStreaming() != other
      .getRequestStreaming())
      return false; 
    if (!getResponseTypeUrl().equals(other.getResponseTypeUrl()))
      return false; 
    if (getResponseStreaming() != other
      .getResponseStreaming())
      return false; 
    if (!getOptionsList().equals(other.getOptionsList()))
      return false; 
    if (this.syntax_ != other.syntax_)
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
    hash = 53 * hash + getRequestTypeUrl().hashCode();
    hash = 37 * hash + 3;
    hash = 53 * hash + Internal.hashBoolean(
        getRequestStreaming());
    hash = 37 * hash + 4;
    hash = 53 * hash + getResponseTypeUrl().hashCode();
    hash = 37 * hash + 5;
    hash = 53 * hash + Internal.hashBoolean(
        getResponseStreaming());
    if (getOptionsCount() > 0) {
      hash = 37 * hash + 6;
      hash = 53 * hash + getOptionsList().hashCode();
    } 
    hash = 37 * hash + 7;
    hash = 53 * hash + this.syntax_;
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static Method parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Method parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Method parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Method parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Method parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Method parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Method parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Method>parseWithIOException(PARSER, input);
  }
  
  public static Method parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Method>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Method parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Method>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static Method parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Method>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Method parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Method>parseWithIOException(PARSER, input);
  }
  
  public static Method parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Method>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(Method prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements MethodOrBuilder {
    private int bitField0_;
    
    private Object name_;
    
    private Object requestTypeUrl_;
    
    private boolean requestStreaming_;
    
    private Object responseTypeUrl_;
    
    private boolean responseStreaming_;
    
    private List<Option> options_;
    
    private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
    
    private int syntax_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return ApiProto.internal_static_google_protobuf_Method_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return ApiProto.internal_static_google_protobuf_Method_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)Method.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.name_ = "";
      this.requestTypeUrl_ = "";
      this.responseTypeUrl_ = "";
      this
        .options_ = Collections.emptyList();
      this.syntax_ = 0;
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.name_ = "";
      this.requestTypeUrl_ = "";
      this.responseTypeUrl_ = "";
      this.options_ = Collections.emptyList();
      this.syntax_ = 0;
    }
    
    public Builder clear() {
      super.clear();
      this.name_ = "";
      this.requestTypeUrl_ = "";
      this.requestStreaming_ = false;
      this.responseTypeUrl_ = "";
      this.responseStreaming_ = false;
      if (this.optionsBuilder_ == null) {
        this.options_ = Collections.emptyList();
      } else {
        this.options_ = null;
        this.optionsBuilder_.clear();
      } 
      this.bitField0_ &= 0xFFFFFFFE;
      this.syntax_ = 0;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return ApiProto.internal_static_google_protobuf_Method_descriptor;
    }
    
    public Method getDefaultInstanceForType() {
      return Method.getDefaultInstance();
    }
    
    public Method build() {
      Method result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public Method buildPartial() {
      Method result = new Method(this);
      int from_bitField0_ = this.bitField0_;
      result.name_ = this.name_;
      result.requestTypeUrl_ = this.requestTypeUrl_;
      result.requestStreaming_ = this.requestStreaming_;
      result.responseTypeUrl_ = this.responseTypeUrl_;
      result.responseStreaming_ = this.responseStreaming_;
      if (this.optionsBuilder_ == null) {
        if ((this.bitField0_ & 0x1) != 0) {
          this.options_ = Collections.unmodifiableList(this.options_);
          this.bitField0_ &= 0xFFFFFFFE;
        } 
        result.options_ = this.options_;
      } else {
        result.options_ = this.optionsBuilder_.build();
      } 
      result.syntax_ = this.syntax_;
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
      if (other instanceof Method)
        return mergeFrom((Method)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(Method other) {
      if (other == Method.getDefaultInstance())
        return this; 
      if (!other.getName().isEmpty()) {
        this.name_ = other.name_;
        onChanged();
      } 
      if (!other.getRequestTypeUrl().isEmpty()) {
        this.requestTypeUrl_ = other.requestTypeUrl_;
        onChanged();
      } 
      if (other.getRequestStreaming())
        setRequestStreaming(other.getRequestStreaming()); 
      if (!other.getResponseTypeUrl().isEmpty()) {
        this.responseTypeUrl_ = other.responseTypeUrl_;
        onChanged();
      } 
      if (other.getResponseStreaming())
        setResponseStreaming(other.getResponseStreaming()); 
      if (this.optionsBuilder_ == null) {
        if (!other.options_.isEmpty()) {
          if (this.options_.isEmpty()) {
            this.options_ = other.options_;
            this.bitField0_ &= 0xFFFFFFFE;
          } else {
            ensureOptionsIsMutable();
            this.options_.addAll(other.options_);
          } 
          onChanged();
        } 
      } else if (!other.options_.isEmpty()) {
        if (this.optionsBuilder_.isEmpty()) {
          this.optionsBuilder_.dispose();
          this.optionsBuilder_ = null;
          this.options_ = other.options_;
          this.bitField0_ &= 0xFFFFFFFE;
          this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getOptionsFieldBuilder() : null;
        } else {
          this.optionsBuilder_.addAllMessages(other.options_);
        } 
      } 
      if (other.syntax_ != 0)
        setSyntaxValue(other.getSyntaxValue()); 
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
          Option m;
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              continue;
            case 10:
              this.name_ = input.readStringRequireUtf8();
              continue;
            case 18:
              this.requestTypeUrl_ = input.readStringRequireUtf8();
              continue;
            case 24:
              this.requestStreaming_ = input.readBool();
              continue;
            case 34:
              this.responseTypeUrl_ = input.readStringRequireUtf8();
              continue;
            case 40:
              this.responseStreaming_ = input.readBool();
              continue;
            case 50:
              m = input.<Option>readMessage(Option.parser(), extensionRegistry);
              if (this.optionsBuilder_ == null) {
                ensureOptionsIsMutable();
                this.options_.add(m);
                continue;
              } 
              this.optionsBuilder_.addMessage(m);
              continue;
            case 56:
              this.syntax_ = input.readEnum();
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
      this.name_ = Method.getDefaultInstance().getName();
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
    
    public String getRequestTypeUrl() {
      Object ref = this.requestTypeUrl_;
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.requestTypeUrl_ = s;
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getRequestTypeUrlBytes() {
      Object ref = this.requestTypeUrl_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.requestTypeUrl_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setRequestTypeUrl(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.requestTypeUrl_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearRequestTypeUrl() {
      this.requestTypeUrl_ = Method.getDefaultInstance().getRequestTypeUrl();
      onChanged();
      return this;
    }
    
    public Builder setRequestTypeUrlBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.requestTypeUrl_ = value;
      onChanged();
      return this;
    }
    
    public boolean getRequestStreaming() {
      return this.requestStreaming_;
    }
    
    public Builder setRequestStreaming(boolean value) {
      this.requestStreaming_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearRequestStreaming() {
      this.requestStreaming_ = false;
      onChanged();
      return this;
    }
    
    public String getResponseTypeUrl() {
      Object ref = this.responseTypeUrl_;
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.responseTypeUrl_ = s;
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getResponseTypeUrlBytes() {
      Object ref = this.responseTypeUrl_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.responseTypeUrl_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setResponseTypeUrl(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.responseTypeUrl_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearResponseTypeUrl() {
      this.responseTypeUrl_ = Method.getDefaultInstance().getResponseTypeUrl();
      onChanged();
      return this;
    }
    
    public Builder setResponseTypeUrlBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.responseTypeUrl_ = value;
      onChanged();
      return this;
    }
    
    public boolean getResponseStreaming() {
      return this.responseStreaming_;
    }
    
    public Builder setResponseStreaming(boolean value) {
      this.responseStreaming_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearResponseStreaming() {
      this.responseStreaming_ = false;
      onChanged();
      return this;
    }
    
    private void ensureOptionsIsMutable() {
      if ((this.bitField0_ & 0x1) == 0) {
        this.options_ = new ArrayList<>(this.options_);
        this.bitField0_ |= 0x1;
      } 
    }
    
    public List<Option> getOptionsList() {
      if (this.optionsBuilder_ == null)
        return Collections.unmodifiableList(this.options_); 
      return this.optionsBuilder_.getMessageList();
    }
    
    public int getOptionsCount() {
      if (this.optionsBuilder_ == null)
        return this.options_.size(); 
      return this.optionsBuilder_.getCount();
    }
    
    public Option getOptions(int index) {
      if (this.optionsBuilder_ == null)
        return this.options_.get(index); 
      return this.optionsBuilder_.getMessage(index);
    }
    
    public Builder setOptions(int index, Option value) {
      if (this.optionsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureOptionsIsMutable();
        this.options_.set(index, value);
        onChanged();
      } else {
        this.optionsBuilder_.setMessage(index, value);
      } 
      return this;
    }
    
    public Builder setOptions(int index, Option.Builder builderForValue) {
      if (this.optionsBuilder_ == null) {
        ensureOptionsIsMutable();
        this.options_.set(index, builderForValue.build());
        onChanged();
      } else {
        this.optionsBuilder_.setMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addOptions(Option value) {
      if (this.optionsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureOptionsIsMutable();
        this.options_.add(value);
        onChanged();
      } else {
        this.optionsBuilder_.addMessage(value);
      } 
      return this;
    }
    
    public Builder addOptions(int index, Option value) {
      if (this.optionsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureOptionsIsMutable();
        this.options_.add(index, value);
        onChanged();
      } else {
        this.optionsBuilder_.addMessage(index, value);
      } 
      return this;
    }
    
    public Builder addOptions(Option.Builder builderForValue) {
      if (this.optionsBuilder_ == null) {
        ensureOptionsIsMutable();
        this.options_.add(builderForValue.build());
        onChanged();
      } else {
        this.optionsBuilder_.addMessage(builderForValue.build());
      } 
      return this;
    }
    
    public Builder addOptions(int index, Option.Builder builderForValue) {
      if (this.optionsBuilder_ == null) {
        ensureOptionsIsMutable();
        this.options_.add(index, builderForValue.build());
        onChanged();
      } else {
        this.optionsBuilder_.addMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addAllOptions(Iterable<? extends Option> values) {
      if (this.optionsBuilder_ == null) {
        ensureOptionsIsMutable();
        AbstractMessageLite.Builder.addAll(values, this.options_);
        onChanged();
      } else {
        this.optionsBuilder_.addAllMessages(values);
      } 
      return this;
    }
    
    public Builder clearOptions() {
      if (this.optionsBuilder_ == null) {
        this.options_ = Collections.emptyList();
        this.bitField0_ &= 0xFFFFFFFE;
        onChanged();
      } else {
        this.optionsBuilder_.clear();
      } 
      return this;
    }
    
    public Builder removeOptions(int index) {
      if (this.optionsBuilder_ == null) {
        ensureOptionsIsMutable();
        this.options_.remove(index);
        onChanged();
      } else {
        this.optionsBuilder_.remove(index);
      } 
      return this;
    }
    
    public Option.Builder getOptionsBuilder(int index) {
      return getOptionsFieldBuilder().getBuilder(index);
    }
    
    public OptionOrBuilder getOptionsOrBuilder(int index) {
      if (this.optionsBuilder_ == null)
        return this.options_.get(index); 
      return this.optionsBuilder_.getMessageOrBuilder(index);
    }
    
    public List<? extends OptionOrBuilder> getOptionsOrBuilderList() {
      if (this.optionsBuilder_ != null)
        return this.optionsBuilder_.getMessageOrBuilderList(); 
      return Collections.unmodifiableList((List)this.options_);
    }
    
    public Option.Builder addOptionsBuilder() {
      return getOptionsFieldBuilder().addBuilder(Option.getDefaultInstance());
    }
    
    public Option.Builder addOptionsBuilder(int index) {
      return getOptionsFieldBuilder().addBuilder(index, Option.getDefaultInstance());
    }
    
    public List<Option.Builder> getOptionsBuilderList() {
      return getOptionsFieldBuilder().getBuilderList();
    }
    
    private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() {
      if (this.optionsBuilder_ == null) {
        this.optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean());
        this.options_ = null;
      } 
      return this.optionsBuilder_;
    }
    
    public int getSyntaxValue() {
      return this.syntax_;
    }
    
    public Builder setSyntaxValue(int value) {
      this.syntax_ = value;
      onChanged();
      return this;
    }
    
    public Syntax getSyntax() {
      Syntax result = Syntax.valueOf(this.syntax_);
      return (result == null) ? Syntax.UNRECOGNIZED : result;
    }
    
    public Builder setSyntax(Syntax value) {
      if (value == null)
        throw new NullPointerException(); 
      this.syntax_ = value.getNumber();
      onChanged();
      return this;
    }
    
    public Builder clearSyntax() {
      this.syntax_ = 0;
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
  
  private static final Method DEFAULT_INSTANCE = new Method();
  
  public static Method getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<Method> PARSER = new AbstractParser<Method>() {
      public Method parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        Method.Builder builder = Method.newBuilder();
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
  
  public static Parser<Method> parser() {
    return PARSER;
  }
  
  public Parser<Method> getParserForType() {
    return PARSER;
  }
  
  public Method getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
