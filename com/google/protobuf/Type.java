package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Type extends GeneratedMessageV3 implements TypeOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int NAME_FIELD_NUMBER = 1;
  
  private volatile Object name_;
  
  public static final int FIELDS_FIELD_NUMBER = 2;
  
  private List<Field> fields_;
  
  public static final int ONEOFS_FIELD_NUMBER = 3;
  
  private LazyStringList oneofs_;
  
  public static final int OPTIONS_FIELD_NUMBER = 4;
  
  private List<Option> options_;
  
  public static final int SOURCE_CONTEXT_FIELD_NUMBER = 5;
  
  private SourceContext sourceContext_;
  
  public static final int SYNTAX_FIELD_NUMBER = 6;
  
  private int syntax_;
  
  private byte memoizedIsInitialized;
  
  private Type(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private Type() {
    this.memoizedIsInitialized = -1;
    this.name_ = "";
    this.fields_ = Collections.emptyList();
    this.oneofs_ = LazyStringArrayList.EMPTY;
    this.options_ = Collections.emptyList();
    this.syntax_ = 0;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new Type();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return TypeProto.internal_static_google_protobuf_Type_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return TypeProto.internal_static_google_protobuf_Type_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Type.class, (Class)Builder.class);
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
  
  public List<Field> getFieldsList() {
    return this.fields_;
  }
  
  public List<? extends FieldOrBuilder> getFieldsOrBuilderList() {
    return (List)this.fields_;
  }
  
  public int getFieldsCount() {
    return this.fields_.size();
  }
  
  public Field getFields(int index) {
    return this.fields_.get(index);
  }
  
  public FieldOrBuilder getFieldsOrBuilder(int index) {
    return this.fields_.get(index);
  }
  
  public ProtocolStringList getOneofsList() {
    return this.oneofs_;
  }
  
  public int getOneofsCount() {
    return this.oneofs_.size();
  }
  
  public String getOneofs(int index) {
    return this.oneofs_.get(index);
  }
  
  public ByteString getOneofsBytes(int index) {
    return this.oneofs_.getByteString(index);
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
  
  public boolean hasSourceContext() {
    return (this.sourceContext_ != null);
  }
  
  public SourceContext getSourceContext() {
    return (this.sourceContext_ == null) ? SourceContext.getDefaultInstance() : this.sourceContext_;
  }
  
  public SourceContextOrBuilder getSourceContextOrBuilder() {
    return getSourceContext();
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
    int i;
    for (i = 0; i < this.fields_.size(); i++)
      output.writeMessage(2, this.fields_.get(i)); 
    for (i = 0; i < this.oneofs_.size(); i++)
      GeneratedMessageV3.writeString(output, 3, this.oneofs_.getRaw(i)); 
    for (i = 0; i < this.options_.size(); i++)
      output.writeMessage(4, this.options_.get(i)); 
    if (this.sourceContext_ != null)
      output.writeMessage(5, getSourceContext()); 
    if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber())
      output.writeEnum(6, this.syntax_); 
    getUnknownFields().writeTo(output);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    size = 0;
    if (!GeneratedMessageV3.isStringEmpty(this.name_))
      size += GeneratedMessageV3.computeStringSize(1, this.name_); 
    for (int j = 0; j < this.fields_.size(); j++)
      size += 
        CodedOutputStream.computeMessageSize(2, this.fields_.get(j)); 
    int dataSize = 0;
    for (int k = 0; k < this.oneofs_.size(); k++)
      dataSize += computeStringSizeNoTag(this.oneofs_.getRaw(k)); 
    size += dataSize;
    size += 1 * getOneofsList().size();
    for (int i = 0; i < this.options_.size(); i++)
      size += 
        CodedOutputStream.computeMessageSize(4, this.options_.get(i)); 
    if (this.sourceContext_ != null)
      size += 
        CodedOutputStream.computeMessageSize(5, getSourceContext()); 
    if (this.syntax_ != Syntax.SYNTAX_PROTO2.getNumber())
      size += 
        CodedOutputStream.computeEnumSize(6, this.syntax_); 
    size += getUnknownFields().getSerializedSize();
    this.memoizedSize = size;
    return size;
  }
  
  public boolean equals(Object obj) {
    if (obj == this)
      return true; 
    if (!(obj instanceof Type))
      return super.equals(obj); 
    Type other = (Type)obj;
    if (!getName().equals(other.getName()))
      return false; 
    if (!getFieldsList().equals(other.getFieldsList()))
      return false; 
    if (!getOneofsList().equals(other.getOneofsList()))
      return false; 
    if (!getOptionsList().equals(other.getOptionsList()))
      return false; 
    if (hasSourceContext() != other.hasSourceContext())
      return false; 
    if (hasSourceContext() && 
      
      !getSourceContext().equals(other.getSourceContext()))
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
    if (getFieldsCount() > 0) {
      hash = 37 * hash + 2;
      hash = 53 * hash + getFieldsList().hashCode();
    } 
    if (getOneofsCount() > 0) {
      hash = 37 * hash + 3;
      hash = 53 * hash + getOneofsList().hashCode();
    } 
    if (getOptionsCount() > 0) {
      hash = 37 * hash + 4;
      hash = 53 * hash + getOptionsList().hashCode();
    } 
    if (hasSourceContext()) {
      hash = 37 * hash + 5;
      hash = 53 * hash + getSourceContext().hashCode();
    } 
    hash = 37 * hash + 6;
    hash = 53 * hash + this.syntax_;
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static Type parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Type parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Type parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Type parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Type parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Type parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Type parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Type>parseWithIOException(PARSER, input);
  }
  
  public static Type parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Type>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Type parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Type>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static Type parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Type>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Type parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Type>parseWithIOException(PARSER, input);
  }
  
  public static Type parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Type>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(Type prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements TypeOrBuilder {
    private int bitField0_;
    
    private Object name_;
    
    private List<Field> fields_;
    
    private RepeatedFieldBuilderV3<Field, Field.Builder, FieldOrBuilder> fieldsBuilder_;
    
    private LazyStringList oneofs_;
    
    private List<Option> options_;
    
    private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
    
    private SourceContext sourceContext_;
    
    private SingleFieldBuilderV3<SourceContext, SourceContext.Builder, SourceContextOrBuilder> sourceContextBuilder_;
    
    private int syntax_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return TypeProto.internal_static_google_protobuf_Type_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return TypeProto.internal_static_google_protobuf_Type_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)Type.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.name_ = "";
      this
        .fields_ = Collections.emptyList();
      this.oneofs_ = LazyStringArrayList.EMPTY;
      this
        .options_ = Collections.emptyList();
      this.syntax_ = 0;
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.name_ = "";
      this.fields_ = Collections.emptyList();
      this.oneofs_ = LazyStringArrayList.EMPTY;
      this.options_ = Collections.emptyList();
      this.syntax_ = 0;
    }
    
    public Builder clear() {
      super.clear();
      this.name_ = "";
      if (this.fieldsBuilder_ == null) {
        this.fields_ = Collections.emptyList();
      } else {
        this.fields_ = null;
        this.fieldsBuilder_.clear();
      } 
      this.bitField0_ &= 0xFFFFFFFE;
      this.oneofs_ = LazyStringArrayList.EMPTY;
      this.bitField0_ &= 0xFFFFFFFD;
      if (this.optionsBuilder_ == null) {
        this.options_ = Collections.emptyList();
      } else {
        this.options_ = null;
        this.optionsBuilder_.clear();
      } 
      this.bitField0_ &= 0xFFFFFFFB;
      if (this.sourceContextBuilder_ == null) {
        this.sourceContext_ = null;
      } else {
        this.sourceContext_ = null;
        this.sourceContextBuilder_ = null;
      } 
      this.syntax_ = 0;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return TypeProto.internal_static_google_protobuf_Type_descriptor;
    }
    
    public Type getDefaultInstanceForType() {
      return Type.getDefaultInstance();
    }
    
    public Type build() {
      Type result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public Type buildPartial() {
      Type result = new Type(this);
      int from_bitField0_ = this.bitField0_;
      result.name_ = this.name_;
      if (this.fieldsBuilder_ == null) {
        if ((this.bitField0_ & 0x1) != 0) {
          this.fields_ = Collections.unmodifiableList(this.fields_);
          this.bitField0_ &= 0xFFFFFFFE;
        } 
        result.fields_ = this.fields_;
      } else {
        result.fields_ = this.fieldsBuilder_.build();
      } 
      if ((this.bitField0_ & 0x2) != 0) {
        this.oneofs_ = this.oneofs_.getUnmodifiableView();
        this.bitField0_ &= 0xFFFFFFFD;
      } 
      result.oneofs_ = this.oneofs_;
      if (this.optionsBuilder_ == null) {
        if ((this.bitField0_ & 0x4) != 0) {
          this.options_ = Collections.unmodifiableList(this.options_);
          this.bitField0_ &= 0xFFFFFFFB;
        } 
        result.options_ = this.options_;
      } else {
        result.options_ = this.optionsBuilder_.build();
      } 
      if (this.sourceContextBuilder_ == null) {
        result.sourceContext_ = this.sourceContext_;
      } else {
        result.sourceContext_ = this.sourceContextBuilder_.build();
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
      if (other instanceof Type)
        return mergeFrom((Type)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(Type other) {
      if (other == Type.getDefaultInstance())
        return this; 
      if (!other.getName().isEmpty()) {
        this.name_ = other.name_;
        onChanged();
      } 
      if (this.fieldsBuilder_ == null) {
        if (!other.fields_.isEmpty()) {
          if (this.fields_.isEmpty()) {
            this.fields_ = other.fields_;
            this.bitField0_ &= 0xFFFFFFFE;
          } else {
            ensureFieldsIsMutable();
            this.fields_.addAll(other.fields_);
          } 
          onChanged();
        } 
      } else if (!other.fields_.isEmpty()) {
        if (this.fieldsBuilder_.isEmpty()) {
          this.fieldsBuilder_.dispose();
          this.fieldsBuilder_ = null;
          this.fields_ = other.fields_;
          this.bitField0_ &= 0xFFFFFFFE;
          this.fieldsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getFieldsFieldBuilder() : null;
        } else {
          this.fieldsBuilder_.addAllMessages(other.fields_);
        } 
      } 
      if (!other.oneofs_.isEmpty()) {
        if (this.oneofs_.isEmpty()) {
          this.oneofs_ = other.oneofs_;
          this.bitField0_ &= 0xFFFFFFFD;
        } else {
          ensureOneofsIsMutable();
          this.oneofs_.addAll(other.oneofs_);
        } 
        onChanged();
      } 
      if (this.optionsBuilder_ == null) {
        if (!other.options_.isEmpty()) {
          if (this.options_.isEmpty()) {
            this.options_ = other.options_;
            this.bitField0_ &= 0xFFFFFFFB;
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
          this.bitField0_ &= 0xFFFFFFFB;
          this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getOptionsFieldBuilder() : null;
        } else {
          this.optionsBuilder_.addAllMessages(other.options_);
        } 
      } 
      if (other.hasSourceContext())
        mergeSourceContext(other.getSourceContext()); 
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
          Field field;
          String s;
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
              field = input.<Field>readMessage(Field.parser(), extensionRegistry);
              if (this.fieldsBuilder_ == null) {
                ensureFieldsIsMutable();
                this.fields_.add(field);
                continue;
              } 
              this.fieldsBuilder_.addMessage(field);
              continue;
            case 26:
              s = input.readStringRequireUtf8();
              ensureOneofsIsMutable();
              this.oneofs_.add(s);
              continue;
            case 34:
              m = input.<Option>readMessage(Option.parser(), extensionRegistry);
              if (this.optionsBuilder_ == null) {
                ensureOptionsIsMutable();
                this.options_.add(m);
                continue;
              } 
              this.optionsBuilder_.addMessage(m);
              continue;
            case 42:
              input.readMessage(getSourceContextFieldBuilder().getBuilder(), extensionRegistry);
              continue;
            case 48:
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
      this.name_ = Type.getDefaultInstance().getName();
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
    
    private void ensureFieldsIsMutable() {
      if ((this.bitField0_ & 0x1) == 0) {
        this.fields_ = new ArrayList<>(this.fields_);
        this.bitField0_ |= 0x1;
      } 
    }
    
    public List<Field> getFieldsList() {
      if (this.fieldsBuilder_ == null)
        return Collections.unmodifiableList(this.fields_); 
      return this.fieldsBuilder_.getMessageList();
    }
    
    public int getFieldsCount() {
      if (this.fieldsBuilder_ == null)
        return this.fields_.size(); 
      return this.fieldsBuilder_.getCount();
    }
    
    public Field getFields(int index) {
      if (this.fieldsBuilder_ == null)
        return this.fields_.get(index); 
      return this.fieldsBuilder_.getMessage(index);
    }
    
    public Builder setFields(int index, Field value) {
      if (this.fieldsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureFieldsIsMutable();
        this.fields_.set(index, value);
        onChanged();
      } else {
        this.fieldsBuilder_.setMessage(index, value);
      } 
      return this;
    }
    
    public Builder setFields(int index, Field.Builder builderForValue) {
      if (this.fieldsBuilder_ == null) {
        ensureFieldsIsMutable();
        this.fields_.set(index, builderForValue.build());
        onChanged();
      } else {
        this.fieldsBuilder_.setMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addFields(Field value) {
      if (this.fieldsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureFieldsIsMutable();
        this.fields_.add(value);
        onChanged();
      } else {
        this.fieldsBuilder_.addMessage(value);
      } 
      return this;
    }
    
    public Builder addFields(int index, Field value) {
      if (this.fieldsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureFieldsIsMutable();
        this.fields_.add(index, value);
        onChanged();
      } else {
        this.fieldsBuilder_.addMessage(index, value);
      } 
      return this;
    }
    
    public Builder addFields(Field.Builder builderForValue) {
      if (this.fieldsBuilder_ == null) {
        ensureFieldsIsMutable();
        this.fields_.add(builderForValue.build());
        onChanged();
      } else {
        this.fieldsBuilder_.addMessage(builderForValue.build());
      } 
      return this;
    }
    
    public Builder addFields(int index, Field.Builder builderForValue) {
      if (this.fieldsBuilder_ == null) {
        ensureFieldsIsMutable();
        this.fields_.add(index, builderForValue.build());
        onChanged();
      } else {
        this.fieldsBuilder_.addMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addAllFields(Iterable<? extends Field> values) {
      if (this.fieldsBuilder_ == null) {
        ensureFieldsIsMutable();
        AbstractMessageLite.Builder.addAll(values, this.fields_);
        onChanged();
      } else {
        this.fieldsBuilder_.addAllMessages(values);
      } 
      return this;
    }
    
    public Builder clearFields() {
      if (this.fieldsBuilder_ == null) {
        this.fields_ = Collections.emptyList();
        this.bitField0_ &= 0xFFFFFFFE;
        onChanged();
      } else {
        this.fieldsBuilder_.clear();
      } 
      return this;
    }
    
    public Builder removeFields(int index) {
      if (this.fieldsBuilder_ == null) {
        ensureFieldsIsMutable();
        this.fields_.remove(index);
        onChanged();
      } else {
        this.fieldsBuilder_.remove(index);
      } 
      return this;
    }
    
    public Field.Builder getFieldsBuilder(int index) {
      return getFieldsFieldBuilder().getBuilder(index);
    }
    
    public FieldOrBuilder getFieldsOrBuilder(int index) {
      if (this.fieldsBuilder_ == null)
        return this.fields_.get(index); 
      return this.fieldsBuilder_.getMessageOrBuilder(index);
    }
    
    public List<? extends FieldOrBuilder> getFieldsOrBuilderList() {
      if (this.fieldsBuilder_ != null)
        return this.fieldsBuilder_.getMessageOrBuilderList(); 
      return Collections.unmodifiableList((List)this.fields_);
    }
    
    public Field.Builder addFieldsBuilder() {
      return getFieldsFieldBuilder().addBuilder(Field.getDefaultInstance());
    }
    
    public Field.Builder addFieldsBuilder(int index) {
      return getFieldsFieldBuilder().addBuilder(index, Field.getDefaultInstance());
    }
    
    public List<Field.Builder> getFieldsBuilderList() {
      return getFieldsFieldBuilder().getBuilderList();
    }
    
    private RepeatedFieldBuilderV3<Field, Field.Builder, FieldOrBuilder> getFieldsFieldBuilder() {
      if (this.fieldsBuilder_ == null) {
        this.fieldsBuilder_ = new RepeatedFieldBuilderV3<>(this.fields_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean());
        this.fields_ = null;
      } 
      return this.fieldsBuilder_;
    }
    
    private void ensureOneofsIsMutable() {
      if ((this.bitField0_ & 0x2) == 0) {
        this.oneofs_ = new LazyStringArrayList(this.oneofs_);
        this.bitField0_ |= 0x2;
      } 
    }
    
    public ProtocolStringList getOneofsList() {
      return this.oneofs_.getUnmodifiableView();
    }
    
    public int getOneofsCount() {
      return this.oneofs_.size();
    }
    
    public String getOneofs(int index) {
      return this.oneofs_.get(index);
    }
    
    public ByteString getOneofsBytes(int index) {
      return this.oneofs_.getByteString(index);
    }
    
    public Builder setOneofs(int index, String value) {
      if (value == null)
        throw new NullPointerException(); 
      ensureOneofsIsMutable();
      this.oneofs_.set(index, value);
      onChanged();
      return this;
    }
    
    public Builder addOneofs(String value) {
      if (value == null)
        throw new NullPointerException(); 
      ensureOneofsIsMutable();
      this.oneofs_.add(value);
      onChanged();
      return this;
    }
    
    public Builder addAllOneofs(Iterable<String> values) {
      ensureOneofsIsMutable();
      AbstractMessageLite.Builder.addAll(values, this.oneofs_);
      onChanged();
      return this;
    }
    
    public Builder clearOneofs() {
      this.oneofs_ = LazyStringArrayList.EMPTY;
      this.bitField0_ &= 0xFFFFFFFD;
      onChanged();
      return this;
    }
    
    public Builder addOneofsBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      ensureOneofsIsMutable();
      this.oneofs_.add(value);
      onChanged();
      return this;
    }
    
    private void ensureOptionsIsMutable() {
      if ((this.bitField0_ & 0x4) == 0) {
        this.options_ = new ArrayList<>(this.options_);
        this.bitField0_ |= 0x4;
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
        this.bitField0_ &= 0xFFFFFFFB;
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
        this.optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, ((this.bitField0_ & 0x4) != 0), getParentForChildren(), isClean());
        this.options_ = null;
      } 
      return this.optionsBuilder_;
    }
    
    public boolean hasSourceContext() {
      return (this.sourceContextBuilder_ != null || this.sourceContext_ != null);
    }
    
    public SourceContext getSourceContext() {
      if (this.sourceContextBuilder_ == null)
        return (this.sourceContext_ == null) ? SourceContext.getDefaultInstance() : this.sourceContext_; 
      return this.sourceContextBuilder_.getMessage();
    }
    
    public Builder setSourceContext(SourceContext value) {
      if (this.sourceContextBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        this.sourceContext_ = value;
        onChanged();
      } else {
        this.sourceContextBuilder_.setMessage(value);
      } 
      return this;
    }
    
    public Builder setSourceContext(SourceContext.Builder builderForValue) {
      if (this.sourceContextBuilder_ == null) {
        this.sourceContext_ = builderForValue.build();
        onChanged();
      } else {
        this.sourceContextBuilder_.setMessage(builderForValue.build());
      } 
      return this;
    }
    
    public Builder mergeSourceContext(SourceContext value) {
      if (this.sourceContextBuilder_ == null) {
        if (this.sourceContext_ != null) {
          this.sourceContext_ = SourceContext.newBuilder(this.sourceContext_).mergeFrom(value).buildPartial();
        } else {
          this.sourceContext_ = value;
        } 
        onChanged();
      } else {
        this.sourceContextBuilder_.mergeFrom(value);
      } 
      return this;
    }
    
    public Builder clearSourceContext() {
      if (this.sourceContextBuilder_ == null) {
        this.sourceContext_ = null;
        onChanged();
      } else {
        this.sourceContext_ = null;
        this.sourceContextBuilder_ = null;
      } 
      return this;
    }
    
    public SourceContext.Builder getSourceContextBuilder() {
      onChanged();
      return getSourceContextFieldBuilder().getBuilder();
    }
    
    public SourceContextOrBuilder getSourceContextOrBuilder() {
      if (this.sourceContextBuilder_ != null)
        return this.sourceContextBuilder_.getMessageOrBuilder(); 
      return (this.sourceContext_ == null) ? SourceContext.getDefaultInstance() : this.sourceContext_;
    }
    
    private SingleFieldBuilderV3<SourceContext, SourceContext.Builder, SourceContextOrBuilder> getSourceContextFieldBuilder() {
      if (this.sourceContextBuilder_ == null) {
        this.sourceContextBuilder_ = new SingleFieldBuilderV3<>(getSourceContext(), getParentForChildren(), isClean());
        this.sourceContext_ = null;
      } 
      return this.sourceContextBuilder_;
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
  
  private static final Type DEFAULT_INSTANCE = new Type();
  
  public static Type getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<Type> PARSER = new AbstractParser<Type>() {
      public Type parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        Type.Builder builder = Type.newBuilder();
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
  
  public static Parser<Type> parser() {
    return PARSER;
  }
  
  public Parser<Type> getParserForType() {
    return PARSER;
  }
  
  public Type getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
