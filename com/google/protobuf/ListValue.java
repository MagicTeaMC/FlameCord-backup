package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListValue extends GeneratedMessageV3 implements ListValueOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int VALUES_FIELD_NUMBER = 1;
  
  private List<Value> values_;
  
  private byte memoizedIsInitialized;
  
  private ListValue(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private ListValue() {
    this.memoizedIsInitialized = -1;
    this.values_ = Collections.emptyList();
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new ListValue();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return StructProto.internal_static_google_protobuf_ListValue_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return StructProto.internal_static_google_protobuf_ListValue_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)ListValue.class, (Class)Builder.class);
  }
  
  public List<Value> getValuesList() {
    return this.values_;
  }
  
  public List<? extends ValueOrBuilder> getValuesOrBuilderList() {
    return (List)this.values_;
  }
  
  public int getValuesCount() {
    return this.values_.size();
  }
  
  public Value getValues(int index) {
    return this.values_.get(index);
  }
  
  public ValueOrBuilder getValuesOrBuilder(int index) {
    return this.values_.get(index);
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
    for (int i = 0; i < this.values_.size(); i++)
      output.writeMessage(1, this.values_.get(i)); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    for (int i = 0; i < this.values_.size(); i++)
      size += 
        CodedOutputStream.computeMessageSize(1, this.values_.get(i)); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof ListValue))
      return super.equals(obj); 
    ListValue other = (ListValue)obj;
    if (!getValuesList().equals(other.getValuesList()))
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
    if (getValuesCount() > 0) {
      hash = 37 * hash + 1;
      hash = 53 * hash + getValuesList().hashCode();
    } 
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static ListValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static ListValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static ListValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static ListValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static ListValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static ListValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static ListValue parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<ListValue>parseWithIOException(PARSER, input);
  }
  
  public static ListValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<ListValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static ListValue parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<ListValue>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static ListValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<ListValue>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static ListValue parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<ListValue>parseWithIOException(PARSER, input);
  }
  
  public static ListValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<ListValue>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(ListValue prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ListValueOrBuilder {
    private int bitField0_;
    
    private List<Value> values_;
    
    private RepeatedFieldBuilderV3<Value, Value.Builder, ValueOrBuilder> valuesBuilder_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return StructProto.internal_static_google_protobuf_ListValue_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return StructProto.internal_static_google_protobuf_ListValue_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)ListValue.class, (Class)Builder.class);
    }
    
    private Builder() {
      this
        .values_ = Collections.emptyList();
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.values_ = Collections.emptyList();
    }
    
    public Builder clear() {
      super.clear();
      if (this.valuesBuilder_ == null) {
        this.values_ = Collections.emptyList();
      } else {
        this.values_ = null;
        this.valuesBuilder_.clear();
      } 
      this.bitField0_ &= 0xFFFFFFFE;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return StructProto.internal_static_google_protobuf_ListValue_descriptor;
    }
    
    public ListValue getDefaultInstanceForType() {
      return ListValue.getDefaultInstance();
    }
    
    public ListValue build() {
      ListValue result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public ListValue buildPartial() {
      ListValue result = new ListValue(this);
      int from_bitField0_ = this.bitField0_;
      if (this.valuesBuilder_ == null) {
        if ((this.bitField0_ & 0x1) != 0) {
          this.values_ = Collections.unmodifiableList(this.values_);
          this.bitField0_ &= 0xFFFFFFFE;
        } 
        result.values_ = this.values_;
      } else {
        result.values_ = this.valuesBuilder_.build();
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
      if (other instanceof ListValue)
        return mergeFrom((ListValue)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(ListValue other) {
      if (other == ListValue.getDefaultInstance())
        return this; 
      if (this.valuesBuilder_ == null) {
        if (!other.values_.isEmpty()) {
          if (this.values_.isEmpty()) {
            this.values_ = other.values_;
            this.bitField0_ &= 0xFFFFFFFE;
          } else {
            ensureValuesIsMutable();
            this.values_.addAll(other.values_);
          } 
          onChanged();
        } 
      } else if (!other.values_.isEmpty()) {
        if (this.valuesBuilder_.isEmpty()) {
          this.valuesBuilder_.dispose();
          this.valuesBuilder_ = null;
          this.values_ = other.values_;
          this.bitField0_ &= 0xFFFFFFFE;
          this.valuesBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getValuesFieldBuilder() : null;
        } else {
          this.valuesBuilder_.addAllMessages(other.values_);
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
          Value m;
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              continue;
            case 10:
              m = input.<Value>readMessage(Value.parser(), extensionRegistry);
              if (this.valuesBuilder_ == null) {
                ensureValuesIsMutable();
                this.values_.add(m);
                continue;
              } 
              this.valuesBuilder_.addMessage(m);
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
    
    private void ensureValuesIsMutable() {
      if ((this.bitField0_ & 0x1) == 0) {
        this.values_ = new ArrayList<>(this.values_);
        this.bitField0_ |= 0x1;
      } 
    }
    
    public List<Value> getValuesList() {
      if (this.valuesBuilder_ == null)
        return Collections.unmodifiableList(this.values_); 
      return this.valuesBuilder_.getMessageList();
    }
    
    public int getValuesCount() {
      if (this.valuesBuilder_ == null)
        return this.values_.size(); 
      return this.valuesBuilder_.getCount();
    }
    
    public Value getValues(int index) {
      if (this.valuesBuilder_ == null)
        return this.values_.get(index); 
      return this.valuesBuilder_.getMessage(index);
    }
    
    public Builder setValues(int index, Value value) {
      if (this.valuesBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureValuesIsMutable();
        this.values_.set(index, value);
        onChanged();
      } else {
        this.valuesBuilder_.setMessage(index, value);
      } 
      return this;
    }
    
    public Builder setValues(int index, Value.Builder builderForValue) {
      if (this.valuesBuilder_ == null) {
        ensureValuesIsMutable();
        this.values_.set(index, builderForValue.build());
        onChanged();
      } else {
        this.valuesBuilder_.setMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addValues(Value value) {
      if (this.valuesBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureValuesIsMutable();
        this.values_.add(value);
        onChanged();
      } else {
        this.valuesBuilder_.addMessage(value);
      } 
      return this;
    }
    
    public Builder addValues(int index, Value value) {
      if (this.valuesBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureValuesIsMutable();
        this.values_.add(index, value);
        onChanged();
      } else {
        this.valuesBuilder_.addMessage(index, value);
      } 
      return this;
    }
    
    public Builder addValues(Value.Builder builderForValue) {
      if (this.valuesBuilder_ == null) {
        ensureValuesIsMutable();
        this.values_.add(builderForValue.build());
        onChanged();
      } else {
        this.valuesBuilder_.addMessage(builderForValue.build());
      } 
      return this;
    }
    
    public Builder addValues(int index, Value.Builder builderForValue) {
      if (this.valuesBuilder_ == null) {
        ensureValuesIsMutable();
        this.values_.add(index, builderForValue.build());
        onChanged();
      } else {
        this.valuesBuilder_.addMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addAllValues(Iterable<? extends Value> values) {
      if (this.valuesBuilder_ == null) {
        ensureValuesIsMutable();
        AbstractMessageLite.Builder.addAll(values, this.values_);
        onChanged();
      } else {
        this.valuesBuilder_.addAllMessages(values);
      } 
      return this;
    }
    
    public Builder clearValues() {
      if (this.valuesBuilder_ == null) {
        this.values_ = Collections.emptyList();
        this.bitField0_ &= 0xFFFFFFFE;
        onChanged();
      } else {
        this.valuesBuilder_.clear();
      } 
      return this;
    }
    
    public Builder removeValues(int index) {
      if (this.valuesBuilder_ == null) {
        ensureValuesIsMutable();
        this.values_.remove(index);
        onChanged();
      } else {
        this.valuesBuilder_.remove(index);
      } 
      return this;
    }
    
    public Value.Builder getValuesBuilder(int index) {
      return getValuesFieldBuilder().getBuilder(index);
    }
    
    public ValueOrBuilder getValuesOrBuilder(int index) {
      if (this.valuesBuilder_ == null)
        return this.values_.get(index); 
      return this.valuesBuilder_.getMessageOrBuilder(index);
    }
    
    public List<? extends ValueOrBuilder> getValuesOrBuilderList() {
      if (this.valuesBuilder_ != null)
        return this.valuesBuilder_.getMessageOrBuilderList(); 
      return Collections.unmodifiableList((List)this.values_);
    }
    
    public Value.Builder addValuesBuilder() {
      return getValuesFieldBuilder().addBuilder(
          Value.getDefaultInstance());
    }
    
    public Value.Builder addValuesBuilder(int index) {
      return getValuesFieldBuilder().addBuilder(index, 
          Value.getDefaultInstance());
    }
    
    public List<Value.Builder> getValuesBuilderList() {
      return getValuesFieldBuilder().getBuilderList();
    }
    
    private RepeatedFieldBuilderV3<Value, Value.Builder, ValueOrBuilder> getValuesFieldBuilder() {
      if (this.valuesBuilder_ == null) {
        this
          
          .valuesBuilder_ = new RepeatedFieldBuilderV3<>(this.values_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean());
        this.values_ = null;
      } 
      return this.valuesBuilder_;
    }
    
    public final Builder setUnknownFields(UnknownFieldSet unknownFields) {
      return super.setUnknownFields(unknownFields);
    }
    
    public final Builder mergeUnknownFields(UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }
  }
  
  private static final ListValue DEFAULT_INSTANCE = new ListValue();
  
  public static ListValue getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<ListValue> PARSER = new AbstractParser<ListValue>() {
      public ListValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        ListValue.Builder builder = ListValue.newBuilder();
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
  
  public static Parser<ListValue> parser() {
    return PARSER;
  }
  
  public Parser<ListValue> getParserForType() {
    return PARSER;
  }
  
  public ListValue getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
