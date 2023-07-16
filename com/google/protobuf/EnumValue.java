package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class EnumValue extends GeneratedMessageV3 implements EnumValueOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int NAME_FIELD_NUMBER = 1;
  
  private volatile Object name_;
  
  public static final int NUMBER_FIELD_NUMBER = 2;
  
  private int number_;
  
  public static final int OPTIONS_FIELD_NUMBER = 3;
  
  private List<Option> options_;
  
  private byte memoizedIsInitialized;
  
  private EnumValue(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private EnumValue() {
    this.memoizedIsInitialized = -1;
    this.name_ = "";
    this.options_ = Collections.emptyList();
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new EnumValue();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return TypeProto.internal_static_google_protobuf_EnumValue_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return TypeProto.internal_static_google_protobuf_EnumValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)EnumValue.class, (Class)Builder.class);
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
  
  public int getNumber() {
    return this.number_;
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
    if (this.number_ != 0)
      output.writeInt32(2, this.number_); 
    for (int i = 0; i < this.options_.size(); i++)
      output.writeMessage(3, this.options_.get(i)); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (!GeneratedMessageV3.isStringEmpty(this.name_))
      size += GeneratedMessageV3.computeStringSize(1, this.name_); 
    if (this.number_ != 0)
      size += 
        CodedOutputStream.computeInt32Size(2, this.number_); 
    for (int i = 0; i < this.options_.size(); i++)
      size += 
        CodedOutputStream.computeMessageSize(3, this.options_.get(i)); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof EnumValue))
      return super.equals(obj); 
    EnumValue other = (EnumValue)obj;
    if (!getName().equals(other.getName()))
      return false; 
    if (getNumber() != other
      .getNumber())
      return false; 
    if (!getOptionsList().equals(other.getOptionsList()))
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
    hash = 53 * hash + getNumber();
    if (getOptionsCount() > 0) {
      hash = 37 * hash + 3;
      hash = 53 * hash + getOptionsList().hashCode();
    } 
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static EnumValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static EnumValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static EnumValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static EnumValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static EnumValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static EnumValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static EnumValue parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<EnumValue>parseWithIOException(PARSER, input);
  }
  
  public static EnumValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<EnumValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static EnumValue parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<EnumValue>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static EnumValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<EnumValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static EnumValue parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<EnumValue>parseWithIOException(PARSER, input);
  }
  
  public static EnumValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<EnumValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(EnumValue prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements EnumValueOrBuilder {
    private int bitField0_;
    
    private Object name_;
    
    private int number_;
    
    private List<Option> options_;
    
    private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return TypeProto.internal_static_google_protobuf_EnumValue_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_EnumValue_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)EnumValue.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.name_ = "";
      this
        .options_ = Collections.emptyList();
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.name_ = "";
      this.options_ = Collections.emptyList();
    }
    
    public Builder clear() {
      super.clear();
      this.name_ = "";
      this.number_ = 0;
      if (this.optionsBuilder_ == null) {
        this.options_ = Collections.emptyList();
      } else {
        this.options_ = null;
        this.optionsBuilder_.clear();
      } 
      this.bitField0_ &= 0xFFFFFFFE;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return TypeProto.internal_static_google_protobuf_EnumValue_descriptor;
    }
    
    public EnumValue getDefaultInstanceForType() {
      return EnumValue.getDefaultInstance();
    }
    
    public EnumValue build() {
      EnumValue result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public EnumValue buildPartial() {
      EnumValue result = new EnumValue(this);
      int from_bitField0_ = this.bitField0_;
      result.name_ = this.name_;
      result.number_ = this.number_;
      if (this.optionsBuilder_ == null) {
        if ((this.bitField0_ & 0x1) != 0) {
          this.options_ = Collections.unmodifiableList(this.options_);
          this.bitField0_ &= 0xFFFFFFFE;
        } 
        result.options_ = this.options_;
      } else {
        result.options_ = this.optionsBuilder_.build();
      } 
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
      if (other instanceof EnumValue)
        return mergeFrom((EnumValue)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(EnumValue other) {
      if (other == EnumValue.getDefaultInstance())
        return this; 
      if (!other.getName().isEmpty()) {
        this.name_ = other.name_;
        onChanged();
      } 
      if (other.getNumber() != 0)
        setNumber(other.getNumber()); 
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
            case 16:
              this.number_ = input.readInt32();
              continue;
            case 26:
              m = input.<Option>readMessage(Option.parser(), extensionRegistry);
              if (this.optionsBuilder_ == null) {
                ensureOptionsIsMutable();
                this.options_.add(m);
                continue;
              } 
              this.optionsBuilder_.addMessage(m);
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
      this.name_ = EnumValue.getDefaultInstance().getName();
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
    
    public int getNumber() {
      return this.number_;
    }
    
    public Builder setNumber(int value) {
      this.number_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearNumber() {
      this.number_ = 0;
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
      return getOptionsFieldBuilder().addBuilder(
          Option.getDefaultInstance());
    }
    
    public Option.Builder addOptionsBuilder(int index) {
      return getOptionsFieldBuilder().addBuilder(index, 
          Option.getDefaultInstance());
    }
    
    public List<Option.Builder> getOptionsBuilderList() {
      return getOptionsFieldBuilder().getBuilderList();
    }
    
    private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> getOptionsFieldBuilder() {
      if (this.optionsBuilder_ == null) {
        this
          
          .optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean());
        this.options_ = null;
      } 
      return this.optionsBuilder_;
    }
    
    public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }
    
    public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }
  }
  
  private static final EnumValue DEFAULT_INSTANCE = new EnumValue();
  
  public static EnumValue getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<EnumValue> PARSER = new AbstractParser<EnumValue>() {
      public EnumValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        EnumValue.Builder builder = EnumValue.newBuilder();
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
  
  public static Parser<EnumValue> parser() {
    return PARSER;
  }
  
  public Parser<EnumValue> getParserForType() {
    return PARSER;
  }
  
  public EnumValue getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
