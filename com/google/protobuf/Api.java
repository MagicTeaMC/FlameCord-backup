package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Api extends GeneratedMessageV3 implements ApiOrBuilder {
  private static final long serialVersionUID = 0L;
  
  public static final int NAME_FIELD_NUMBER = 1;
  
  private volatile Object name_;
  
  public static final int METHODS_FIELD_NUMBER = 2;
  
  private List<Method> methods_;
  
  public static final int OPTIONS_FIELD_NUMBER = 3;
  
  private List<Option> options_;
  
  public static final int VERSION_FIELD_NUMBER = 4;
  
  private volatile Object version_;
  
  public static final int SOURCE_CONTEXT_FIELD_NUMBER = 5;
  
  private SourceContext sourceContext_;
  
  public static final int MIXINS_FIELD_NUMBER = 6;
  
  private List<Mixin> mixins_;
  
  public static final int SYNTAX_FIELD_NUMBER = 7;
  
  private int syntax_;
  
  private byte memoizedIsInitialized;
  
  private Api(GeneratedMessageV3.Builder<?> builder) {
    super(builder);
    this.memoizedIsInitialized = -1;
  }
  
  private Api() {
    this.memoizedIsInitialized = -1;
    this.name_ = "";
    this.methods_ = Collections.emptyList();
    this.options_ = Collections.emptyList();
    this.version_ = "";
    this.mixins_ = Collections.emptyList();
    this.syntax_ = 0;
  }
  
  protected Object newInstance(GeneratedMessageV3.UnusedPrivateParameter unused) {
    return new Api();
  }
  
  public final UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  public static final Descriptors.Descriptor getDescriptor() {
    return ApiProto.internal_static_google_protobuf_Api_descriptor;
  }
  
  protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
    return ApiProto.internal_static_google_protobuf_Api_fieldAccessorTable.ensureFieldAccessorsInitialized((Class)Api.class, (Class)Builder.class);
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
  
  public List<Method> getMethodsList() {
    return this.methods_;
  }
  
  public List<? extends MethodOrBuilder> getMethodsOrBuilderList() {
    return (List)this.methods_;
  }
  
  public int getMethodsCount() {
    return this.methods_.size();
  }
  
  public Method getMethods(int index) {
    return this.methods_.get(index);
  }
  
  public MethodOrBuilder getMethodsOrBuilder(int index) {
    return this.methods_.get(index);
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
  
  public String getVersion() {
    Object ref = this.version_;
    if (ref instanceof String)
      return (String)ref; 
    ByteString bs = (ByteString)ref;
    String s = bs.toStringUtf8();
    this.version_ = s;
    return s;
  }
  
  public ByteString getVersionBytes() {
    Object ref = this.version_;
    if (ref instanceof String) {
      ByteString b = ByteString.copyFromUtf8((String)ref);
      this.version_ = b;
      return b;
    } 
    return (ByteString)ref;
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
  
  public List<Mixin> getMixinsList() {
    return this.mixins_;
  }
  
  public List<? extends MixinOrBuilder> getMixinsOrBuilderList() {
    return (List)this.mixins_;
  }
  
  public int getMixinsCount() {
    return this.mixins_.size();
  }
  
  public Mixin getMixins(int index) {
    return this.mixins_.get(index);
  }
  
  public MixinOrBuilder getMixinsOrBuilder(int index) {
    return this.mixins_.get(index);
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
    for (i = 0; i < this.methods_.size(); i++)
      output.writeMessage(2, this.methods_.get(i)); 
    for (i = 0; i < this.options_.size(); i++)
      output.writeMessage(3, this.options_.get(i)); 
    if (!GeneratedMessageV3.isStringEmpty(this.version_))
      GeneratedMessageV3.writeString(output, 4, this.version_); 
    if (this.sourceContext_ != null)
      output.writeMessage(5, getSourceContext()); 
    for (i = 0; i < this.mixins_.size(); i++)
      output.writeMessage(6, this.mixins_.get(i)); 
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
    int i;
    for (i = 0; i < this.methods_.size(); i++)
      size += 
        CodedOutputStream.computeMessageSize(2, this.methods_.get(i)); 
    for (i = 0; i < this.options_.size(); i++)
      size += 
        CodedOutputStream.computeMessageSize(3, this.options_.get(i)); 
    if (!GeneratedMessageV3.isStringEmpty(this.version_))
      size += GeneratedMessageV3.computeStringSize(4, this.version_); 
    if (this.sourceContext_ != null)
      size += 
        CodedOutputStream.computeMessageSize(5, getSourceContext()); 
    for (i = 0; i < this.mixins_.size(); i++)
      size += 
        CodedOutputStream.computeMessageSize(6, this.mixins_.get(i)); 
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
    if (!(obj instanceof Api))
      return super.equals(obj); 
    Api other = (Api)obj;
    if (!getName().equals(other.getName()))
      return false; 
    if (!getMethodsList().equals(other.getMethodsList()))
      return false; 
    if (!getOptionsList().equals(other.getOptionsList()))
      return false; 
    if (!getVersion().equals(other.getVersion()))
      return false; 
    if (hasSourceContext() != other.hasSourceContext())
      return false; 
    if (hasSourceContext() && 
      
      !getSourceContext().equals(other.getSourceContext()))
      return false; 
    if (!getMixinsList().equals(other.getMixinsList()))
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
    if (getMethodsCount() > 0) {
      hash = 37 * hash + 2;
      hash = 53 * hash + getMethodsList().hashCode();
    } 
    if (getOptionsCount() > 0) {
      hash = 37 * hash + 3;
      hash = 53 * hash + getOptionsList().hashCode();
    } 
    hash = 37 * hash + 4;
    hash = 53 * hash + getVersion().hashCode();
    if (hasSourceContext()) {
      hash = 37 * hash + 5;
      hash = 53 * hash + getSourceContext().hashCode();
    } 
    if (getMixinsCount() > 0) {
      hash = 37 * hash + 6;
      hash = 53 * hash + getMixinsList().hashCode();
    } 
    hash = 37 * hash + 7;
    hash = 53 * hash + this.syntax_;
    hash = 29 * hash + getUnknownFields().hashCode();
    this.memoizedHashCode = hash;
    return hash;
  }
  
  public static Api parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Api parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Api parseFrom(ByteString data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Api parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Api parseFrom(byte[] data) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  
  public static Api parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  
  public static Api parseFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Api>parseWithIOException(PARSER, input);
  }
  
  public static Api parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Api>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Api parseDelimitedFrom(InputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Api>parseDelimitedWithIOException(PARSER, input);
  }
  
  public static Api parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Api>parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  
  public static Api parseFrom(CodedInputStream input) throws IOException {
    return 
      GeneratedMessageV3.<Api>parseWithIOException(PARSER, input);
  }
  
  public static Api parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
    return 
      GeneratedMessageV3.<Api>parseWithIOException(PARSER, input, extensionRegistry);
  }
  
  public Builder newBuilderForType() {
    return newBuilder();
  }
  
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  
  public static Builder newBuilder(Api prototype) {
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
  
  public static final class Builder extends GeneratedMessageV3.Builder<Builder> implements ApiOrBuilder {
    private int bitField0_;
    
    private Object name_;
    
    private List<Method> methods_;
    
    private RepeatedFieldBuilderV3<Method, Method.Builder, MethodOrBuilder> methodsBuilder_;
    
    private List<Option> options_;
    
    private RepeatedFieldBuilderV3<Option, Option.Builder, OptionOrBuilder> optionsBuilder_;
    
    private Object version_;
    
    private SourceContext sourceContext_;
    
    private SingleFieldBuilderV3<SourceContext, SourceContext.Builder, SourceContextOrBuilder> sourceContextBuilder_;
    
    private List<Mixin> mixins_;
    
    private RepeatedFieldBuilderV3<Mixin, Mixin.Builder, MixinOrBuilder> mixinsBuilder_;
    
    private int syntax_;
    
    public static final Descriptors.Descriptor getDescriptor() {
      return ApiProto.internal_static_google_protobuf_Api_descriptor;
    }
    
    protected GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable() {
      return ApiProto.internal_static_google_protobuf_Api_fieldAccessorTable
        .ensureFieldAccessorsInitialized((Class)Api.class, (Class)Builder.class);
    }
    
    private Builder() {
      this.name_ = "";
      this
        .methods_ = Collections.emptyList();
      this
        .options_ = Collections.emptyList();
      this.version_ = "";
      this
        .mixins_ = Collections.emptyList();
      this.syntax_ = 0;
    }
    
    private Builder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      this.name_ = "";
      this.methods_ = Collections.emptyList();
      this.options_ = Collections.emptyList();
      this.version_ = "";
      this.mixins_ = Collections.emptyList();
      this.syntax_ = 0;
    }
    
    public Builder clear() {
      super.clear();
      this.name_ = "";
      if (this.methodsBuilder_ == null) {
        this.methods_ = Collections.emptyList();
      } else {
        this.methods_ = null;
        this.methodsBuilder_.clear();
      } 
      this.bitField0_ &= 0xFFFFFFFE;
      if (this.optionsBuilder_ == null) {
        this.options_ = Collections.emptyList();
      } else {
        this.options_ = null;
        this.optionsBuilder_.clear();
      } 
      this.bitField0_ &= 0xFFFFFFFD;
      this.version_ = "";
      if (this.sourceContextBuilder_ == null) {
        this.sourceContext_ = null;
      } else {
        this.sourceContext_ = null;
        this.sourceContextBuilder_ = null;
      } 
      if (this.mixinsBuilder_ == null) {
        this.mixins_ = Collections.emptyList();
      } else {
        this.mixins_ = null;
        this.mixinsBuilder_.clear();
      } 
      this.bitField0_ &= 0xFFFFFFFB;
      this.syntax_ = 0;
      return this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return ApiProto.internal_static_google_protobuf_Api_descriptor;
    }
    
    public Api getDefaultInstanceForType() {
      return Api.getDefaultInstance();
    }
    
    public Api build() {
      Api result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    public Api buildPartial() {
      Api result = new Api(this);
      int from_bitField0_ = this.bitField0_;
      result.name_ = this.name_;
      if (this.methodsBuilder_ == null) {
        if ((this.bitField0_ & 0x1) != 0) {
          this.methods_ = Collections.unmodifiableList(this.methods_);
          this.bitField0_ &= 0xFFFFFFFE;
        } 
        result.methods_ = this.methods_;
      } else {
        result.methods_ = this.methodsBuilder_.build();
      } 
      if (this.optionsBuilder_ == null) {
        if ((this.bitField0_ & 0x2) != 0) {
          this.options_ = Collections.unmodifiableList(this.options_);
          this.bitField0_ &= 0xFFFFFFFD;
        } 
        result.options_ = this.options_;
      } else {
        result.options_ = this.optionsBuilder_.build();
      } 
      result.version_ = this.version_;
      if (this.sourceContextBuilder_ == null) {
        result.sourceContext_ = this.sourceContext_;
      } else {
        result.sourceContext_ = this.sourceContextBuilder_.build();
      } 
      if (this.mixinsBuilder_ == null) {
        if ((this.bitField0_ & 0x4) != 0) {
          this.mixins_ = Collections.unmodifiableList(this.mixins_);
          this.bitField0_ &= 0xFFFFFFFB;
        } 
        result.mixins_ = this.mixins_;
      } else {
        result.mixins_ = this.mixinsBuilder_.build();
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
      if (other instanceof Api)
        return mergeFrom((Api)other); 
      super.mergeFrom(other);
      return this;
    }
    
    public Builder mergeFrom(Api other) {
      if (other == Api.getDefaultInstance())
        return this; 
      if (!other.getName().isEmpty()) {
        this.name_ = other.name_;
        onChanged();
      } 
      if (this.methodsBuilder_ == null) {
        if (!other.methods_.isEmpty()) {
          if (this.methods_.isEmpty()) {
            this.methods_ = other.methods_;
            this.bitField0_ &= 0xFFFFFFFE;
          } else {
            ensureMethodsIsMutable();
            this.methods_.addAll(other.methods_);
          } 
          onChanged();
        } 
      } else if (!other.methods_.isEmpty()) {
        if (this.methodsBuilder_.isEmpty()) {
          this.methodsBuilder_.dispose();
          this.methodsBuilder_ = null;
          this.methods_ = other.methods_;
          this.bitField0_ &= 0xFFFFFFFE;
          this.methodsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getMethodsFieldBuilder() : null;
        } else {
          this.methodsBuilder_.addAllMessages(other.methods_);
        } 
      } 
      if (this.optionsBuilder_ == null) {
        if (!other.options_.isEmpty()) {
          if (this.options_.isEmpty()) {
            this.options_ = other.options_;
            this.bitField0_ &= 0xFFFFFFFD;
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
          this.bitField0_ &= 0xFFFFFFFD;
          this.optionsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getOptionsFieldBuilder() : null;
        } else {
          this.optionsBuilder_.addAllMessages(other.options_);
        } 
      } 
      if (!other.getVersion().isEmpty()) {
        this.version_ = other.version_;
        onChanged();
      } 
      if (other.hasSourceContext())
        mergeSourceContext(other.getSourceContext()); 
      if (this.mixinsBuilder_ == null) {
        if (!other.mixins_.isEmpty()) {
          if (this.mixins_.isEmpty()) {
            this.mixins_ = other.mixins_;
            this.bitField0_ &= 0xFFFFFFFB;
          } else {
            ensureMixinsIsMutable();
            this.mixins_.addAll(other.mixins_);
          } 
          onChanged();
        } 
      } else if (!other.mixins_.isEmpty()) {
        if (this.mixinsBuilder_.isEmpty()) {
          this.mixinsBuilder_.dispose();
          this.mixinsBuilder_ = null;
          this.mixins_ = other.mixins_;
          this.bitField0_ &= 0xFFFFFFFB;
          this.mixinsBuilder_ = GeneratedMessageV3.alwaysUseFieldBuilders ? getMixinsFieldBuilder() : null;
        } else {
          this.mixinsBuilder_.addAllMessages(other.mixins_);
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
          Method method;
          Option option;
          Mixin m;
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              continue;
            case 10:
              this.name_ = input.readStringRequireUtf8();
              continue;
            case 18:
              method = input.<Method>readMessage(Method.parser(), extensionRegistry);
              if (this.methodsBuilder_ == null) {
                ensureMethodsIsMutable();
                this.methods_.add(method);
                continue;
              } 
              this.methodsBuilder_.addMessage(method);
              continue;
            case 26:
              option = input.<Option>readMessage(Option.parser(), extensionRegistry);
              if (this.optionsBuilder_ == null) {
                ensureOptionsIsMutable();
                this.options_.add(option);
                continue;
              } 
              this.optionsBuilder_.addMessage(option);
              continue;
            case 34:
              this.version_ = input.readStringRequireUtf8();
              continue;
            case 42:
              input.readMessage(getSourceContextFieldBuilder().getBuilder(), extensionRegistry);
              continue;
            case 50:
              m = input.<Mixin>readMessage(Mixin.parser(), extensionRegistry);
              if (this.mixinsBuilder_ == null) {
                ensureMixinsIsMutable();
                this.mixins_.add(m);
                continue;
              } 
              this.mixinsBuilder_.addMessage(m);
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
      this.name_ = Api.getDefaultInstance().getName();
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
    
    private void ensureMethodsIsMutable() {
      if ((this.bitField0_ & 0x1) == 0) {
        this.methods_ = new ArrayList<>(this.methods_);
        this.bitField0_ |= 0x1;
      } 
    }
    
    public List<Method> getMethodsList() {
      if (this.methodsBuilder_ == null)
        return Collections.unmodifiableList(this.methods_); 
      return this.methodsBuilder_.getMessageList();
    }
    
    public int getMethodsCount() {
      if (this.methodsBuilder_ == null)
        return this.methods_.size(); 
      return this.methodsBuilder_.getCount();
    }
    
    public Method getMethods(int index) {
      if (this.methodsBuilder_ == null)
        return this.methods_.get(index); 
      return this.methodsBuilder_.getMessage(index);
    }
    
    public Builder setMethods(int index, Method value) {
      if (this.methodsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureMethodsIsMutable();
        this.methods_.set(index, value);
        onChanged();
      } else {
        this.methodsBuilder_.setMessage(index, value);
      } 
      return this;
    }
    
    public Builder setMethods(int index, Method.Builder builderForValue) {
      if (this.methodsBuilder_ == null) {
        ensureMethodsIsMutable();
        this.methods_.set(index, builderForValue.build());
        onChanged();
      } else {
        this.methodsBuilder_.setMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addMethods(Method value) {
      if (this.methodsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureMethodsIsMutable();
        this.methods_.add(value);
        onChanged();
      } else {
        this.methodsBuilder_.addMessage(value);
      } 
      return this;
    }
    
    public Builder addMethods(int index, Method value) {
      if (this.methodsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureMethodsIsMutable();
        this.methods_.add(index, value);
        onChanged();
      } else {
        this.methodsBuilder_.addMessage(index, value);
      } 
      return this;
    }
    
    public Builder addMethods(Method.Builder builderForValue) {
      if (this.methodsBuilder_ == null) {
        ensureMethodsIsMutable();
        this.methods_.add(builderForValue.build());
        onChanged();
      } else {
        this.methodsBuilder_.addMessage(builderForValue.build());
      } 
      return this;
    }
    
    public Builder addMethods(int index, Method.Builder builderForValue) {
      if (this.methodsBuilder_ == null) {
        ensureMethodsIsMutable();
        this.methods_.add(index, builderForValue.build());
        onChanged();
      } else {
        this.methodsBuilder_.addMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addAllMethods(Iterable<? extends Method> values) {
      if (this.methodsBuilder_ == null) {
        ensureMethodsIsMutable();
        AbstractMessageLite.Builder.addAll(values, this.methods_);
        onChanged();
      } else {
        this.methodsBuilder_.addAllMessages(values);
      } 
      return this;
    }
    
    public Builder clearMethods() {
      if (this.methodsBuilder_ == null) {
        this.methods_ = Collections.emptyList();
        this.bitField0_ &= 0xFFFFFFFE;
        onChanged();
      } else {
        this.methodsBuilder_.clear();
      } 
      return this;
    }
    
    public Builder removeMethods(int index) {
      if (this.methodsBuilder_ == null) {
        ensureMethodsIsMutable();
        this.methods_.remove(index);
        onChanged();
      } else {
        this.methodsBuilder_.remove(index);
      } 
      return this;
    }
    
    public Method.Builder getMethodsBuilder(int index) {
      return getMethodsFieldBuilder().getBuilder(index);
    }
    
    public MethodOrBuilder getMethodsOrBuilder(int index) {
      if (this.methodsBuilder_ == null)
        return this.methods_.get(index); 
      return this.methodsBuilder_.getMessageOrBuilder(index);
    }
    
    public List<? extends MethodOrBuilder> getMethodsOrBuilderList() {
      if (this.methodsBuilder_ != null)
        return this.methodsBuilder_.getMessageOrBuilderList(); 
      return Collections.unmodifiableList((List)this.methods_);
    }
    
    public Method.Builder addMethodsBuilder() {
      return getMethodsFieldBuilder().addBuilder(Method.getDefaultInstance());
    }
    
    public Method.Builder addMethodsBuilder(int index) {
      return getMethodsFieldBuilder().addBuilder(index, Method.getDefaultInstance());
    }
    
    public List<Method.Builder> getMethodsBuilderList() {
      return getMethodsFieldBuilder().getBuilderList();
    }
    
    private RepeatedFieldBuilderV3<Method, Method.Builder, MethodOrBuilder> getMethodsFieldBuilder() {
      if (this.methodsBuilder_ == null) {
        this.methodsBuilder_ = new RepeatedFieldBuilderV3<>(this.methods_, ((this.bitField0_ & 0x1) != 0), getParentForChildren(), isClean());
        this.methods_ = null;
      } 
      return this.methodsBuilder_;
    }
    
    private void ensureOptionsIsMutable() {
      if ((this.bitField0_ & 0x2) == 0) {
        this.options_ = new ArrayList<>(this.options_);
        this.bitField0_ |= 0x2;
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
        this.bitField0_ &= 0xFFFFFFFD;
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
        this.optionsBuilder_ = new RepeatedFieldBuilderV3<>(this.options_, ((this.bitField0_ & 0x2) != 0), getParentForChildren(), isClean());
        this.options_ = null;
      } 
      return this.optionsBuilder_;
    }
    
    public String getVersion() {
      Object ref = this.version_;
      if (!(ref instanceof String)) {
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.version_ = s;
        return s;
      } 
      return (String)ref;
    }
    
    public ByteString getVersionBytes() {
      Object ref = this.version_;
      if (ref instanceof String) {
        ByteString b = ByteString.copyFromUtf8((String)ref);
        this.version_ = b;
        return b;
      } 
      return (ByteString)ref;
    }
    
    public Builder setVersion(String value) {
      if (value == null)
        throw new NullPointerException(); 
      this.version_ = value;
      onChanged();
      return this;
    }
    
    public Builder clearVersion() {
      this.version_ = Api.getDefaultInstance().getVersion();
      onChanged();
      return this;
    }
    
    public Builder setVersionBytes(ByteString value) {
      if (value == null)
        throw new NullPointerException(); 
      AbstractMessageLite.checkByteStringIsUtf8(value);
      this.version_ = value;
      onChanged();
      return this;
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
    
    private void ensureMixinsIsMutable() {
      if ((this.bitField0_ & 0x4) == 0) {
        this.mixins_ = new ArrayList<>(this.mixins_);
        this.bitField0_ |= 0x4;
      } 
    }
    
    public List<Mixin> getMixinsList() {
      if (this.mixinsBuilder_ == null)
        return Collections.unmodifiableList(this.mixins_); 
      return this.mixinsBuilder_.getMessageList();
    }
    
    public int getMixinsCount() {
      if (this.mixinsBuilder_ == null)
        return this.mixins_.size(); 
      return this.mixinsBuilder_.getCount();
    }
    
    public Mixin getMixins(int index) {
      if (this.mixinsBuilder_ == null)
        return this.mixins_.get(index); 
      return this.mixinsBuilder_.getMessage(index);
    }
    
    public Builder setMixins(int index, Mixin value) {
      if (this.mixinsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureMixinsIsMutable();
        this.mixins_.set(index, value);
        onChanged();
      } else {
        this.mixinsBuilder_.setMessage(index, value);
      } 
      return this;
    }
    
    public Builder setMixins(int index, Mixin.Builder builderForValue) {
      if (this.mixinsBuilder_ == null) {
        ensureMixinsIsMutable();
        this.mixins_.set(index, builderForValue.build());
        onChanged();
      } else {
        this.mixinsBuilder_.setMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addMixins(Mixin value) {
      if (this.mixinsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureMixinsIsMutable();
        this.mixins_.add(value);
        onChanged();
      } else {
        this.mixinsBuilder_.addMessage(value);
      } 
      return this;
    }
    
    public Builder addMixins(int index, Mixin value) {
      if (this.mixinsBuilder_ == null) {
        if (value == null)
          throw new NullPointerException(); 
        ensureMixinsIsMutable();
        this.mixins_.add(index, value);
        onChanged();
      } else {
        this.mixinsBuilder_.addMessage(index, value);
      } 
      return this;
    }
    
    public Builder addMixins(Mixin.Builder builderForValue) {
      if (this.mixinsBuilder_ == null) {
        ensureMixinsIsMutable();
        this.mixins_.add(builderForValue.build());
        onChanged();
      } else {
        this.mixinsBuilder_.addMessage(builderForValue.build());
      } 
      return this;
    }
    
    public Builder addMixins(int index, Mixin.Builder builderForValue) {
      if (this.mixinsBuilder_ == null) {
        ensureMixinsIsMutable();
        this.mixins_.add(index, builderForValue.build());
        onChanged();
      } else {
        this.mixinsBuilder_.addMessage(index, builderForValue.build());
      } 
      return this;
    }
    
    public Builder addAllMixins(Iterable<? extends Mixin> values) {
      if (this.mixinsBuilder_ == null) {
        ensureMixinsIsMutable();
        AbstractMessageLite.Builder.addAll(values, this.mixins_);
        onChanged();
      } else {
        this.mixinsBuilder_.addAllMessages(values);
      } 
      return this;
    }
    
    public Builder clearMixins() {
      if (this.mixinsBuilder_ == null) {
        this.mixins_ = Collections.emptyList();
        this.bitField0_ &= 0xFFFFFFFB;
        onChanged();
      } else {
        this.mixinsBuilder_.clear();
      } 
      return this;
    }
    
    public Builder removeMixins(int index) {
      if (this.mixinsBuilder_ == null) {
        ensureMixinsIsMutable();
        this.mixins_.remove(index);
        onChanged();
      } else {
        this.mixinsBuilder_.remove(index);
      } 
      return this;
    }
    
    public Mixin.Builder getMixinsBuilder(int index) {
      return getMixinsFieldBuilder().getBuilder(index);
    }
    
    public MixinOrBuilder getMixinsOrBuilder(int index) {
      if (this.mixinsBuilder_ == null)
        return this.mixins_.get(index); 
      return this.mixinsBuilder_.getMessageOrBuilder(index);
    }
    
    public List<? extends MixinOrBuilder> getMixinsOrBuilderList() {
      if (this.mixinsBuilder_ != null)
        return this.mixinsBuilder_.getMessageOrBuilderList(); 
      return Collections.unmodifiableList((List)this.mixins_);
    }
    
    public Mixin.Builder addMixinsBuilder() {
      return getMixinsFieldBuilder().addBuilder(Mixin.getDefaultInstance());
    }
    
    public Mixin.Builder addMixinsBuilder(int index) {
      return getMixinsFieldBuilder().addBuilder(index, Mixin.getDefaultInstance());
    }
    
    public List<Mixin.Builder> getMixinsBuilderList() {
      return getMixinsFieldBuilder().getBuilderList();
    }
    
    private RepeatedFieldBuilderV3<Mixin, Mixin.Builder, MixinOrBuilder> getMixinsFieldBuilder() {
      if (this.mixinsBuilder_ == null) {
        this.mixinsBuilder_ = new RepeatedFieldBuilderV3<>(this.mixins_, ((this.bitField0_ & 0x4) != 0), getParentForChildren(), isClean());
        this.mixins_ = null;
      } 
      return this.mixinsBuilder_;
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
  
  private static final Api DEFAULT_INSTANCE = new Api();
  
  public static Api getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }
  
  private static final Parser<Api> PARSER = new AbstractParser<Api>() {
      public Api parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        Api.Builder builder = Api.newBuilder();
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
  
  public static Parser<Api> parser() {
    return PARSER;
  }
  
  public Parser<Api> getParserForType() {
    return PARSER;
  }
  
  public Api getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }
}
