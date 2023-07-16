package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class GeneratedMessageV3 extends AbstractMessage implements Serializable {
  private static final long serialVersionUID = 1L;
  
  protected static boolean alwaysUseFieldBuilders = false;
  
  protected UnknownFieldSet unknownFields;
  
  protected GeneratedMessageV3() {
    this.unknownFields = UnknownFieldSet.getDefaultInstance();
  }
  
  protected GeneratedMessageV3(Builder<?> builder) {
    this.unknownFields = builder.getUnknownFields();
  }
  
  public Parser<? extends GeneratedMessageV3> getParserForType() {
    throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
  }
  
  static void enableAlwaysUseFieldBuildersForTesting() {
    setAlwaysUseFieldBuildersForTesting(true);
  }
  
  static void setAlwaysUseFieldBuildersForTesting(boolean useBuilders) {
    alwaysUseFieldBuilders = useBuilders;
  }
  
  public Descriptors.Descriptor getDescriptorForType() {
    return (internalGetFieldAccessorTable()).descriptor;
  }
  
  @Deprecated
  protected void mergeFromAndMakeImmutableInternal(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    Schema<GeneratedMessageV3> schema = Protobuf.getInstance().schemaFor(this);
    try {
      schema.mergeFrom(this, CodedInputStreamReader.forCodedInput(input), extensionRegistry);
    } catch (InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (IOException e) {
      throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(this);
    } 
    schema.makeImmutable(this);
  }
  
  private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable(boolean getBytesForString) {
    TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<>();
    Descriptors.Descriptor descriptor = (internalGetFieldAccessorTable()).descriptor;
    List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
    for (int i = 0; i < fields.size(); i++) {
      Descriptors.FieldDescriptor field = fields.get(i);
      Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
      if (oneofDescriptor != null) {
        i += oneofDescriptor.getFieldCount() - 1;
        if (!hasOneof(oneofDescriptor))
          continue; 
        field = getOneofFieldDescriptor(oneofDescriptor);
      } else {
        if (field.isRepeated()) {
          List<?> value = (List)getField(field);
          if (!value.isEmpty())
            result.put(field, value); 
          continue;
        } 
        if (!hasField(field))
          continue; 
      } 
      if (getBytesForString && field.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
        result.put(field, getFieldRaw(field));
      } else {
        result.put(field, getField(field));
      } 
      continue;
    } 
    return result;
  }
  
  public boolean isInitialized() {
    for (Descriptors.FieldDescriptor field : getDescriptorForType().getFields()) {
      if (field.isRequired() && 
        !hasField(field))
        return false; 
      if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
        if (field.isRepeated()) {
          List<Message> messageList = (List<Message>)getField(field);
          for (Message element : messageList) {
            if (!element.isInitialized())
              return false; 
          } 
          continue;
        } 
        if (hasField(field) && !((Message)getField(field)).isInitialized())
          return false; 
      } 
    } 
    return true;
  }
  
  public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
    return Collections.unmodifiableMap(
        getAllFieldsMutable(false));
  }
  
  Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
    return Collections.unmodifiableMap(
        getAllFieldsMutable(true));
  }
  
  public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
    return internalGetFieldAccessorTable().getOneof(oneof).has(this);
  }
  
  public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
    return internalGetFieldAccessorTable().getOneof(oneof).get(this);
  }
  
  public boolean hasField(Descriptors.FieldDescriptor field) {
    return internalGetFieldAccessorTable().getField(field).has(this);
  }
  
  public Object getField(Descriptors.FieldDescriptor field) {
    return internalGetFieldAccessorTable().getField(field).get(this);
  }
  
  Object getFieldRaw(Descriptors.FieldDescriptor field) {
    return internalGetFieldAccessorTable().getField(field).getRaw(this);
  }
  
  public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
    return internalGetFieldAccessorTable().getField(field)
      .getRepeatedCount(this);
  }
  
  public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
    return internalGetFieldAccessorTable().getField(field)
      .getRepeated(this, index);
  }
  
  public UnknownFieldSet getUnknownFields() {
    return this.unknownFields;
  }
  
  protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
    if (input.shouldDiscardUnknownFields())
      return input.skipField(tag); 
    return unknownFields.mergeFieldFrom(tag, input);
  }
  
  protected boolean parseUnknownFieldProto3(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
    return parseUnknownField(input, unknownFields, extensionRegistry, tag);
  }
  
  protected static <M extends Message> M parseWithIOException(Parser<M> parser, InputStream input) throws IOException {
    try {
      return parser.parseFrom(input);
    } catch (InvalidProtocolBufferException e) {
      throw e.unwrapIOException();
    } 
  }
  
  protected static <M extends Message> M parseWithIOException(Parser<M> parser, InputStream input, ExtensionRegistryLite extensions) throws IOException {
    try {
      return parser.parseFrom(input, extensions);
    } catch (InvalidProtocolBufferException e) {
      throw e.unwrapIOException();
    } 
  }
  
  protected static <M extends Message> M parseWithIOException(Parser<M> parser, CodedInputStream input) throws IOException {
    try {
      return parser.parseFrom(input);
    } catch (InvalidProtocolBufferException e) {
      throw e.unwrapIOException();
    } 
  }
  
  protected static <M extends Message> M parseWithIOException(Parser<M> parser, CodedInputStream input, ExtensionRegistryLite extensions) throws IOException {
    try {
      return parser.parseFrom(input, extensions);
    } catch (InvalidProtocolBufferException e) {
      throw e.unwrapIOException();
    } 
  }
  
  protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> parser, InputStream input) throws IOException {
    try {
      return parser.parseDelimitedFrom(input);
    } catch (InvalidProtocolBufferException e) {
      throw e.unwrapIOException();
    } 
  }
  
  protected static <M extends Message> M parseDelimitedWithIOException(Parser<M> parser, InputStream input, ExtensionRegistryLite extensions) throws IOException {
    try {
      return parser.parseDelimitedFrom(input, extensions);
    } catch (InvalidProtocolBufferException e) {
      throw e.unwrapIOException();
    } 
  }
  
  protected static boolean canUseUnsafe() {
    return (UnsafeUtil.hasUnsafeArrayOperations() && UnsafeUtil.hasUnsafeByteBufferOperations());
  }
  
  protected static Internal.IntList emptyIntList() {
    return IntArrayList.emptyList();
  }
  
  protected static Internal.IntList newIntList() {
    return new IntArrayList();
  }
  
  protected static Internal.IntList mutableCopy(Internal.IntList list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  protected static Internal.LongList emptyLongList() {
    return LongArrayList.emptyList();
  }
  
  protected static Internal.LongList newLongList() {
    return new LongArrayList();
  }
  
  protected static Internal.LongList mutableCopy(Internal.LongList list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  protected static Internal.FloatList emptyFloatList() {
    return FloatArrayList.emptyList();
  }
  
  protected static Internal.FloatList newFloatList() {
    return new FloatArrayList();
  }
  
  protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  protected static Internal.DoubleList emptyDoubleList() {
    return DoubleArrayList.emptyList();
  }
  
  protected static Internal.DoubleList newDoubleList() {
    return new DoubleArrayList();
  }
  
  protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  protected static Internal.BooleanList emptyBooleanList() {
    return BooleanArrayList.emptyList();
  }
  
  protected static Internal.BooleanList newBooleanList() {
    return new BooleanArrayList();
  }
  
  protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  public void writeTo(CodedOutputStream output) throws IOException {
    MessageReflection.writeMessageTo(this, getAllFieldsRaw(), output, false);
  }
  
  public int getSerializedSize() {
    int size = this.memoizedSize;
    if (size != -1)
      return size; 
    this.memoizedSize = MessageReflection.getSerializedSize(this, 
        getAllFieldsRaw());
    return this.memoizedSize;
  }
  
  protected static final class UnusedPrivateParameter {
    static final UnusedPrivateParameter INSTANCE = new UnusedPrivateParameter();
  }
  
  protected Object newInstance(UnusedPrivateParameter unused) {
    throw new UnsupportedOperationException("This method must be overridden by the subclass.");
  }
  
  protected void makeExtensionsImmutable() {}
  
  protected Message.Builder newBuilderForType(final AbstractMessage.BuilderParent parent) {
    return newBuilderForType(new BuilderParent() {
          public void markDirty() {
            parent.markDirty();
          }
        });
  }
  
  public static abstract class Builder<BuilderType extends Builder<BuilderType>> extends AbstractMessage.Builder<BuilderType> {
    private GeneratedMessageV3.BuilderParent builderParent;
    
    private BuilderParentImpl meAsParent;
    
    private boolean isClean;
    
    private Object unknownFieldsOrBuilder = UnknownFieldSet.getDefaultInstance();
    
    protected Builder() {
      this((GeneratedMessageV3.BuilderParent)null);
    }
    
    protected Builder(GeneratedMessageV3.BuilderParent builderParent) {
      this.builderParent = builderParent;
    }
    
    void dispose() {
      this.builderParent = null;
    }
    
    protected void onBuilt() {
      if (this.builderParent != null)
        markClean(); 
    }
    
    protected void markClean() {
      this.isClean = true;
    }
    
    protected boolean isClean() {
      return this.isClean;
    }
    
    public BuilderType clone() {
      Builder builder = (Builder)getDefaultInstanceForType().newBuilderForType();
      builder.mergeFrom(buildPartial());
      return (BuilderType)builder;
    }
    
    public BuilderType clear() {
      this.unknownFieldsOrBuilder = UnknownFieldSet.getDefaultInstance();
      onChanged();
      return (BuilderType)this;
    }
    
    public Descriptors.Descriptor getDescriptorForType() {
      return (internalGetFieldAccessorTable()).descriptor;
    }
    
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      return Collections.unmodifiableMap(getAllFieldsMutable());
    }
    
    private Map<Descriptors.FieldDescriptor, Object> getAllFieldsMutable() {
      TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<>();
      Descriptors.Descriptor descriptor = (internalGetFieldAccessorTable()).descriptor;
      List<Descriptors.FieldDescriptor> fields = descriptor.getFields();
      for (int i = 0; i < fields.size(); i++) {
        Descriptors.FieldDescriptor field = fields.get(i);
        Descriptors.OneofDescriptor oneofDescriptor = field.getContainingOneof();
        if (oneofDescriptor != null) {
          i += oneofDescriptor.getFieldCount() - 1;
          if (!hasOneof(oneofDescriptor))
            continue; 
          field = getOneofFieldDescriptor(oneofDescriptor);
        } else {
          if (field.isRepeated()) {
            List<?> value = (List)getField(field);
            if (!value.isEmpty())
              result.put(field, value); 
            continue;
          } 
          if (!hasField(field))
            continue; 
        } 
        result.put(field, getField(field));
        continue;
      } 
      return result;
    }
    
    public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
      return internalGetFieldAccessorTable().getField(field).newBuilder();
    }
    
    public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
      return internalGetFieldAccessorTable().getField(field).getBuilder(this);
    }
    
    public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
      return internalGetFieldAccessorTable().getField(field).getRepeatedBuilder(this, index);
    }
    
    public boolean hasOneof(Descriptors.OneofDescriptor oneof) {
      return internalGetFieldAccessorTable().getOneof(oneof).has(this);
    }
    
    public Descriptors.FieldDescriptor getOneofFieldDescriptor(Descriptors.OneofDescriptor oneof) {
      return internalGetFieldAccessorTable().getOneof(oneof).get(this);
    }
    
    public boolean hasField(Descriptors.FieldDescriptor field) {
      return internalGetFieldAccessorTable().getField(field).has(this);
    }
    
    public Object getField(Descriptors.FieldDescriptor field) {
      Object object = internalGetFieldAccessorTable().getField(field).get(this);
      if (field.isRepeated())
        return Collections.unmodifiableList((List)object); 
      return object;
    }
    
    public BuilderType setField(Descriptors.FieldDescriptor field, Object value) {
      internalGetFieldAccessorTable().getField(field).set(this, value);
      return (BuilderType)this;
    }
    
    public BuilderType clearField(Descriptors.FieldDescriptor field) {
      internalGetFieldAccessorTable().getField(field).clear(this);
      return (BuilderType)this;
    }
    
    public BuilderType clearOneof(Descriptors.OneofDescriptor oneof) {
      internalGetFieldAccessorTable().getOneof(oneof).clear(this);
      return (BuilderType)this;
    }
    
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
      return internalGetFieldAccessorTable().getField(field)
        .getRepeatedCount(this);
    }
    
    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
      return internalGetFieldAccessorTable().getField(field)
        .getRepeated(this, index);
    }
    
    public BuilderType setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
      internalGetFieldAccessorTable().getField(field)
        .setRepeated(this, index, value);
      return (BuilderType)this;
    }
    
    public BuilderType addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
      internalGetFieldAccessorTable().getField(field).addRepeated(this, value);
      return (BuilderType)this;
    }
    
    private BuilderType setUnknownFieldsInternal(UnknownFieldSet unknownFields) {
      this.unknownFieldsOrBuilder = unknownFields;
      onChanged();
      return (BuilderType)this;
    }
    
    public BuilderType setUnknownFields(UnknownFieldSet unknownFields) {
      return setUnknownFieldsInternal(unknownFields);
    }
    
    protected BuilderType setUnknownFieldsProto3(UnknownFieldSet unknownFields) {
      return setUnknownFieldsInternal(unknownFields);
    }
    
    public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
      if (UnknownFieldSet.getDefaultInstance().equals(unknownFields))
        return (BuilderType)this; 
      if (UnknownFieldSet.getDefaultInstance().equals(this.unknownFieldsOrBuilder)) {
        this.unknownFieldsOrBuilder = unknownFields;
        onChanged();
        return (BuilderType)this;
      } 
      getUnknownFieldSetBuilder().mergeFrom(unknownFields);
      onChanged();
      return (BuilderType)this;
    }
    
    public boolean isInitialized() {
      for (Descriptors.FieldDescriptor field : getDescriptorForType().getFields()) {
        if (field.isRequired() && 
          !hasField(field))
          return false; 
        if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
          if (field.isRepeated()) {
            List<Message> messageList = (List<Message>)getField(field);
            for (Message element : messageList) {
              if (!element.isInitialized())
                return false; 
            } 
            continue;
          } 
          if (hasField(field) && 
            !((Message)getField(field)).isInitialized())
            return false; 
        } 
      } 
      return true;
    }
    
    public final UnknownFieldSet getUnknownFields() {
      if (this.unknownFieldsOrBuilder instanceof UnknownFieldSet)
        return (UnknownFieldSet)this.unknownFieldsOrBuilder; 
      return ((UnknownFieldSet.Builder)this.unknownFieldsOrBuilder).buildPartial();
    }
    
    protected boolean parseUnknownField(CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
      if (input.shouldDiscardUnknownFields())
        return input.skipField(tag); 
      return getUnknownFieldSetBuilder().mergeFieldFrom(tag, input);
    }
    
    protected final void mergeUnknownLengthDelimitedField(int number, ByteString bytes) {
      getUnknownFieldSetBuilder().mergeLengthDelimitedField(number, bytes);
    }
    
    protected final void mergeUnknownVarintField(int number, int value) {
      getUnknownFieldSetBuilder().mergeVarintField(number, value);
    }
    
    protected UnknownFieldSet.Builder getUnknownFieldSetBuilder() {
      if (this.unknownFieldsOrBuilder instanceof UnknownFieldSet)
        this.unknownFieldsOrBuilder = ((UnknownFieldSet)this.unknownFieldsOrBuilder).toBuilder(); 
      onChanged();
      return (UnknownFieldSet.Builder)this.unknownFieldsOrBuilder;
    }
    
    protected void setUnknownFieldSetBuilder(UnknownFieldSet.Builder builder) {
      this.unknownFieldsOrBuilder = builder;
      onChanged();
    }
    
    private class BuilderParentImpl implements GeneratedMessageV3.BuilderParent {
      private BuilderParentImpl() {}
      
      public void markDirty() {
        GeneratedMessageV3.Builder.this.onChanged();
      }
    }
    
    protected GeneratedMessageV3.BuilderParent getParentForChildren() {
      if (this.meAsParent == null)
        this.meAsParent = new BuilderParentImpl(); 
      return this.meAsParent;
    }
    
    protected final void onChanged() {
      if (this.isClean && this.builderParent != null) {
        this.builderParent.markDirty();
        this.isClean = false;
      } 
    }
    
    protected MapField internalGetMapField(int fieldNumber) {
      throw new RuntimeException("No map fields found in " + 
          getClass().getName());
    }
    
    protected MapField internalGetMutableMapField(int fieldNumber) {
      throw new RuntimeException("No map fields found in " + 
          getClass().getName());
    }
    
    protected abstract GeneratedMessageV3.FieldAccessorTable internalGetFieldAccessorTable();
  }
  
  public static abstract class ExtendableMessage<MessageType extends ExtendableMessage> extends GeneratedMessageV3 implements ExtendableMessageOrBuilder<MessageType> {
    private static final long serialVersionUID = 1L;
    
    private final FieldSet<Descriptors.FieldDescriptor> extensions;
    
    protected ExtendableMessage() {
      this.extensions = FieldSet.newFieldSet();
    }
    
    protected ExtendableMessage(GeneratedMessageV3.ExtendableBuilder<MessageType, ?> builder) {
      super(builder);
      this.extensions = builder.buildExtensions();
    }
    
    private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
      if (extension.getDescriptor().getContainingType() != 
        getDescriptorForType())
        throw new IllegalArgumentException("Extension is for type \"" + extension
            
            .getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + 
            
            getDescriptorForType().getFullName() + "\"."); 
    }
    
    public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
      Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
      verifyExtensionContainingType(extension);
      return this.extensions.hasField(extension.getDescriptor());
    }
    
    public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      return this.extensions.getRepeatedFieldCount(descriptor);
    }
    
    public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
      Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
      verifyExtensionContainingType(extension);
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      Object value = this.extensions.getField(descriptor);
      if (value == null) {
        if (descriptor.isRepeated())
          return (Type)Collections.emptyList(); 
        if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
          return (Type)extension.getMessageDefaultInstance(); 
        return (Type)extension.fromReflectionType(descriptor
            .getDefaultValue());
      } 
      return (Type)extension.fromReflectionType(value);
    }
    
    public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index) {
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      return (Type)extension.singularFromReflectionType(this.extensions
          .getRepeatedField(descriptor, index));
    }
    
    public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
      return hasExtension(extension);
    }
    
    public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
      return hasExtension(extension);
    }
    
    public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
      return getExtensionCount(extension);
    }
    
    public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension) {
      return getExtensionCount(extension);
    }
    
    public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
      return getExtension(extension);
    }
    
    public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
      return getExtension(extension);
    }
    
    public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
      return getExtension(extension, index);
    }
    
    public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index) {
      return getExtension(extension, index);
    }
    
    protected boolean extensionsAreInitialized() {
      return this.extensions.isInitialized();
    }
    
    public boolean isInitialized() {
      return (super.isInitialized() && extensionsAreInitialized());
    }
    
    protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
      return MessageReflection.mergeFieldFrom(input, 
          input.shouldDiscardUnknownFields() ? null : unknownFields, extensionRegistry, 
          getDescriptorForType(), new MessageReflection.ExtensionAdapter(this.extensions), tag);
    }
    
    protected boolean parseUnknownFieldProto3(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
      return parseUnknownField(input, unknownFields, extensionRegistry, tag);
    }
    
    protected void makeExtensionsImmutable() {
      this.extensions.makeImmutable();
    }
    
    protected class ExtensionWriter {
      private final Iterator<Map.Entry<Descriptors.FieldDescriptor, Object>> iter = GeneratedMessageV3.ExtendableMessage.this
        .extensions.iterator();
      
      private Map.Entry<Descriptors.FieldDescriptor, Object> next;
      
      private final boolean messageSetWireFormat;
      
      private ExtensionWriter(boolean messageSetWireFormat) {
        if (this.iter.hasNext())
          this.next = this.iter.next(); 
        this.messageSetWireFormat = messageSetWireFormat;
      }
      
      public void writeUntil(int end, CodedOutputStream output) throws IOException {
        while (this.next != null && ((Descriptors.FieldDescriptor)this.next.getKey()).getNumber() < end) {
          Descriptors.FieldDescriptor descriptor = this.next.getKey();
          if (this.messageSetWireFormat && descriptor.getLiteJavaType() == WireFormat.JavaType.MESSAGE && 
            
            !descriptor.isRepeated()) {
            if (this.next instanceof LazyField.LazyEntry) {
              output.writeRawMessageSetExtension(descriptor.getNumber(), ((LazyField.LazyEntry)this.next)
                  .getField().toByteString());
            } else {
              output.writeMessageSetExtension(descriptor.getNumber(), (Message)this.next
                  .getValue());
            } 
          } else {
            FieldSet.writeField(descriptor, this.next.getValue(), output);
          } 
          if (this.iter.hasNext()) {
            this.next = this.iter.next();
            continue;
          } 
          this.next = null;
        } 
      }
    }
    
    protected ExtensionWriter newExtensionWriter() {
      return new ExtensionWriter(false);
    }
    
    protected ExtensionWriter newMessageSetExtensionWriter() {
      return new ExtensionWriter(true);
    }
    
    protected int extensionsSerializedSize() {
      return this.extensions.getSerializedSize();
    }
    
    protected int extensionsSerializedSizeAsMessageSet() {
      return this.extensions.getMessageSetSerializedSize();
    }
    
    protected Map<Descriptors.FieldDescriptor, Object> getExtensionFields() {
      return this.extensions.getAllFields();
    }
    
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      Map<Descriptors.FieldDescriptor, Object> result = getAllFieldsMutable(false);
      result.putAll(getExtensionFields());
      return Collections.unmodifiableMap(result);
    }
    
    public Map<Descriptors.FieldDescriptor, Object> getAllFieldsRaw() {
      Map<Descriptors.FieldDescriptor, Object> result = getAllFieldsMutable(false);
      result.putAll(getExtensionFields());
      return Collections.unmodifiableMap(result);
    }
    
    public boolean hasField(Descriptors.FieldDescriptor field) {
      if (field.isExtension()) {
        verifyContainingType(field);
        return this.extensions.hasField(field);
      } 
      return super.hasField(field);
    }
    
    public Object getField(Descriptors.FieldDescriptor field) {
      if (field.isExtension()) {
        verifyContainingType(field);
        Object value = this.extensions.getField(field);
        if (value == null) {
          if (field.isRepeated())
            return Collections.emptyList(); 
          if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
            return DynamicMessage.getDefaultInstance(field.getMessageType()); 
          return field.getDefaultValue();
        } 
        return value;
      } 
      return super.getField(field);
    }
    
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
      if (field.isExtension()) {
        verifyContainingType(field);
        return this.extensions.getRepeatedFieldCount(field);
      } 
      return super.getRepeatedFieldCount(field);
    }
    
    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
      if (field.isExtension()) {
        verifyContainingType(field);
        return this.extensions.getRepeatedField(field, index);
      } 
      return super.getRepeatedField(field, index);
    }
    
    private void verifyContainingType(Descriptors.FieldDescriptor field) {
      if (field.getContainingType() != getDescriptorForType())
        throw new IllegalArgumentException("FieldDescriptor does not match message type."); 
    }
  }
  
  public static abstract class ExtendableBuilder<MessageType extends ExtendableMessage, BuilderType extends ExtendableBuilder<MessageType, BuilderType>> extends Builder<BuilderType> implements ExtendableMessageOrBuilder<MessageType> {
    private FieldSet.Builder<Descriptors.FieldDescriptor> extensions;
    
    protected ExtendableBuilder() {}
    
    protected ExtendableBuilder(GeneratedMessageV3.BuilderParent parent) {
      super(parent);
    }
    
    void internalSetExtensionSet(FieldSet<Descriptors.FieldDescriptor> extensions) {
      this.extensions = FieldSet.Builder.fromFieldSet(extensions);
    }
    
    public BuilderType clear() {
      this.extensions = null;
      return super.clear();
    }
    
    private void ensureExtensionsIsMutable() {
      if (this.extensions == null)
        this.extensions = FieldSet.newBuilder(); 
    }
    
    private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
      if (extension.getDescriptor().getContainingType() != 
        getDescriptorForType())
        throw new IllegalArgumentException("Extension is for type \"" + extension
            
            .getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + 
            
            getDescriptorForType().getFullName() + "\"."); 
    }
    
    public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
      Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
      verifyExtensionContainingType(extension);
      return (this.extensions == null) ? false : this.extensions.hasField(extension.getDescriptor());
    }
    
    public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      return (this.extensions == null) ? 0 : this.extensions.getRepeatedFieldCount(descriptor);
    }
    
    public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
      Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
      verifyExtensionContainingType(extension);
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      Object value = (this.extensions == null) ? null : this.extensions.getField(descriptor);
      if (value == null) {
        if (descriptor.isRepeated())
          return (Type)Collections.emptyList(); 
        if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
          return (Type)extension.getMessageDefaultInstance(); 
        return (Type)extension.fromReflectionType(descriptor
            .getDefaultValue());
      } 
      return (Type)extension.fromReflectionType(value);
    }
    
    public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index) {
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      if (this.extensions == null)
        throw new IndexOutOfBoundsException(); 
      return (Type)extension
        .singularFromReflectionType(this.extensions.getRepeatedField(descriptor, index));
    }
    
    public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extensionLite, Type value) {
      Extension<MessageType, Type> extension = GeneratedMessageV3.checkNotLite(extensionLite);
      verifyExtensionContainingType(extension);
      ensureExtensionsIsMutable();
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      this.extensions.setField(descriptor, extension.toReflectionType(value));
      onChanged();
      return (BuilderType)this;
    }
    
    public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index, Type value) {
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      ensureExtensionsIsMutable();
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      this.extensions.setRepeatedField(descriptor, index, extension
          
          .singularToReflectionType(value));
      onChanged();
      return (BuilderType)this;
    }
    
    public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extensionLite, Type value) {
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      ensureExtensionsIsMutable();
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      this.extensions.addRepeatedField(descriptor, extension
          .singularToReflectionType(value));
      onChanged();
      return (BuilderType)this;
    }
    
    public final BuilderType clearExtension(ExtensionLite<MessageType, ?> extensionLite) {
      Extension<MessageType, ?> extension = GeneratedMessageV3.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      ensureExtensionsIsMutable();
      this.extensions.clearField(extension.getDescriptor());
      onChanged();
      return (BuilderType)this;
    }
    
    public final <Type> boolean hasExtension(Extension<MessageType, Type> extension) {
      return hasExtension(extension);
    }
    
    public final <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
      return hasExtension(extension);
    }
    
    public final <Type> int getExtensionCount(Extension<MessageType, List<Type>> extension) {
      return getExtensionCount(extension);
    }
    
    public final <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension) {
      return getExtensionCount(extension);
    }
    
    public final <Type> Type getExtension(Extension<MessageType, Type> extension) {
      return getExtension(extension);
    }
    
    public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension) {
      return getExtension(extension);
    }
    
    public final <Type> Type getExtension(Extension<MessageType, List<Type>> extension, int index) {
      return getExtension(extension, index);
    }
    
    public final <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index) {
      return getExtension(extension, index);
    }
    
    public final <Type> BuilderType setExtension(Extension<MessageType, Type> extension, Type value) {
      return setExtension(extension, value);
    }
    
    public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> extension, Type value) {
      return setExtension(extension, value);
    }
    
    public final <Type> BuilderType setExtension(Extension<MessageType, List<Type>> extension, int index, Type value) {
      return setExtension(extension, index, value);
    }
    
    public <Type> BuilderType setExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, int index, Type value) {
      return setExtension(extension, index, value);
    }
    
    public final <Type> BuilderType addExtension(Extension<MessageType, List<Type>> extension, Type value) {
      return addExtension(extension, value);
    }
    
    public <Type> BuilderType addExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> extension, Type value) {
      return addExtension(extension, value);
    }
    
    public final <Type> BuilderType clearExtension(Extension<MessageType, ?> extension) {
      return clearExtension(extension);
    }
    
    public <Type> BuilderType clearExtension(GeneratedMessage.GeneratedExtension<MessageType, ?> extension) {
      return clearExtension(extension);
    }
    
    protected boolean extensionsAreInitialized() {
      return (this.extensions == null) ? true : this.extensions.isInitialized();
    }
    
    private FieldSet<Descriptors.FieldDescriptor> buildExtensions() {
      return (this.extensions == null) ? 
        FieldSet.<Descriptors.FieldDescriptor>emptySet() : this.extensions
        .buildPartial();
    }
    
    public boolean isInitialized() {
      return (super.isInitialized() && extensionsAreInitialized());
    }
    
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      Map<Descriptors.FieldDescriptor, Object> result = getAllFieldsMutable();
      if (this.extensions != null)
        result.putAll(this.extensions.getAllFields()); 
      return Collections.unmodifiableMap(result);
    }
    
    public Object getField(Descriptors.FieldDescriptor field) {
      if (field.isExtension()) {
        verifyContainingType(field);
        Object value = (this.extensions == null) ? null : this.extensions.getField(field);
        if (value == null) {
          if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
            return DynamicMessage.getDefaultInstance(field.getMessageType()); 
          return field.getDefaultValue();
        } 
        return value;
      } 
      return super.getField(field);
    }
    
    public Message.Builder getFieldBuilder(Descriptors.FieldDescriptor field) {
      if (field.isExtension()) {
        verifyContainingType(field);
        if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE)
          throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type."); 
        ensureExtensionsIsMutable();
        Object value = this.extensions.getFieldAllowBuilders(field);
        if (value == null) {
          Message.Builder builder = DynamicMessage.newBuilder(field.getMessageType());
          this.extensions.setField(field, builder);
          onChanged();
          return builder;
        } 
        if (value instanceof Message.Builder)
          return (Message.Builder)value; 
        if (value instanceof Message) {
          Message.Builder builder = ((Message)value).toBuilder();
          this.extensions.setField(field, builder);
          onChanged();
          return builder;
        } 
        throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
      } 
      return super.getFieldBuilder(field);
    }
    
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
      if (field.isExtension()) {
        verifyContainingType(field);
        return (this.extensions == null) ? 0 : this.extensions.getRepeatedFieldCount(field);
      } 
      return super.getRepeatedFieldCount(field);
    }
    
    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
      if (field.isExtension()) {
        verifyContainingType(field);
        if (this.extensions == null)
          throw new IndexOutOfBoundsException(); 
        return this.extensions.getRepeatedField(field, index);
      } 
      return super.getRepeatedField(field, index);
    }
    
    public Message.Builder getRepeatedFieldBuilder(Descriptors.FieldDescriptor field, int index) {
      if (field.isExtension()) {
        verifyContainingType(field);
        ensureExtensionsIsMutable();
        if (field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE)
          throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type."); 
        Object value = this.extensions.getRepeatedFieldAllowBuilders(field, index);
        if (value instanceof Message.Builder)
          return (Message.Builder)value; 
        if (value instanceof Message) {
          Message.Builder builder = ((Message)value).toBuilder();
          this.extensions.setRepeatedField(field, index, builder);
          onChanged();
          return builder;
        } 
        throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
      } 
      return super.getRepeatedFieldBuilder(field, index);
    }
    
    public boolean hasField(Descriptors.FieldDescriptor field) {
      if (field.isExtension()) {
        verifyContainingType(field);
        return (this.extensions == null) ? false : this.extensions.hasField(field);
      } 
      return super.hasField(field);
    }
    
    public BuilderType setField(Descriptors.FieldDescriptor field, Object value) {
      if (field.isExtension()) {
        verifyContainingType(field);
        ensureExtensionsIsMutable();
        this.extensions.setField(field, value);
        onChanged();
        return (BuilderType)this;
      } 
      return super.setField(field, value);
    }
    
    public BuilderType clearField(Descriptors.FieldDescriptor field) {
      if (field.isExtension()) {
        verifyContainingType(field);
        ensureExtensionsIsMutable();
        this.extensions.clearField(field);
        onChanged();
        return (BuilderType)this;
      } 
      return super.clearField(field);
    }
    
    public BuilderType setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
      if (field.isExtension()) {
        verifyContainingType(field);
        ensureExtensionsIsMutable();
        this.extensions.setRepeatedField(field, index, value);
        onChanged();
        return (BuilderType)this;
      } 
      return super.setRepeatedField(field, index, value);
    }
    
    public BuilderType addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
      if (field.isExtension()) {
        verifyContainingType(field);
        ensureExtensionsIsMutable();
        this.extensions.addRepeatedField(field, value);
        onChanged();
        return (BuilderType)this;
      } 
      return super.addRepeatedField(field, value);
    }
    
    public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
      if (field.isExtension())
        return DynamicMessage.newBuilder(field.getMessageType()); 
      return super.newBuilderForField(field);
    }
    
    protected final void mergeExtensionFields(GeneratedMessageV3.ExtendableMessage other) {
      if (other.extensions != null) {
        ensureExtensionsIsMutable();
        this.extensions.mergeFrom(other.extensions);
        onChanged();
      } 
    }
    
    protected boolean parseUnknownField(CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
      ensureExtensionsIsMutable();
      return MessageReflection.mergeFieldFrom(input, 
          
          input.shouldDiscardUnknownFields() ? null : getUnknownFieldSetBuilder(), extensionRegistry, 
          
          getDescriptorForType(), new MessageReflection.ExtensionBuilderAdapter(this.extensions), tag);
    }
    
    private void verifyContainingType(Descriptors.FieldDescriptor field) {
      if (field.getContainingType() != getDescriptorForType())
        throw new IllegalArgumentException("FieldDescriptor does not match message type."); 
    }
  }
  
  private static Method getMethodOrDie(Class clazz, String name, Class... params) {
    try {
      return clazz.getMethod(name, params);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Generated message class \"" + clazz
          .getName() + "\" missing method \"" + name + "\".", e);
    } 
  }
  
  @CanIgnoreReturnValue
  private static Object invokeOrDie(Method method, Object object, Object... params) {
    try {
      return method.invoke(object, params);
    } catch (IllegalAccessException e) {
      throw new RuntimeException("Couldn't use Java reflection to implement protocol message reflection.", e);
    } catch (InvocationTargetException e) {
      Throwable cause = e.getCause();
      if (cause instanceof RuntimeException)
        throw (RuntimeException)cause; 
      if (cause instanceof Error)
        throw (Error)cause; 
      throw new RuntimeException("Unexpected exception thrown by generated accessor method.", cause);
    } 
  }
  
  protected MapField internalGetMapField(int fieldNumber) {
    throw new RuntimeException("No map fields found in " + 
        getClass().getName());
  }
  
  public static final class FieldAccessorTable {
    private final Descriptors.Descriptor descriptor;
    
    private final FieldAccessor[] fields;
    
    private String[] camelCaseNames;
    
    private final OneofAccessor[] oneofs;
    
    private volatile boolean initialized;
    
    public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
      this(descriptor, camelCaseNames);
      ensureFieldAccessorsInitialized(messageClass, builderClass);
    }
    
    public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames) {
      this.descriptor = descriptor;
      this.camelCaseNames = camelCaseNames;
      this.fields = new FieldAccessor[descriptor.getFields().size()];
      this.oneofs = new OneofAccessor[descriptor.getOneofs().size()];
      this.initialized = false;
    }
    
    public FieldAccessorTable ensureFieldAccessorsInitialized(Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
      if (this.initialized)
        return this; 
      synchronized (this) {
        if (this.initialized)
          return this; 
        int fieldsSize = this.fields.length;
        for (int i = 0; i < fieldsSize; i++) {
          Descriptors.FieldDescriptor field = this.descriptor.getFields().get(i);
          String containingOneofCamelCaseName = null;
          if (field.getContainingOneof() != null)
            containingOneofCamelCaseName = this.camelCaseNames[fieldsSize + field.getContainingOneof().getIndex()]; 
          if (field.isRepeated()) {
            if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
              if (field.isMapField()) {
                this.fields[i] = new MapFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
              } else {
                this.fields[i] = new RepeatedMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
              } 
            } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
              this.fields[i] = new RepeatedEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
            } else {
              this.fields[i] = new RepeatedFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass);
            } 
          } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
            this.fields[i] = new SingularMessageFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
          } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
            this.fields[i] = new SingularEnumFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
          } else if (field.getJavaType() == Descriptors.FieldDescriptor.JavaType.STRING) {
            this.fields[i] = new SingularStringFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
          } else {
            this.fields[i] = new SingularFieldAccessor(field, this.camelCaseNames[i], messageClass, builderClass, containingOneofCamelCaseName);
          } 
        } 
        int oneofsSize = this.oneofs.length;
        for (int j = 0; j < oneofsSize; j++)
          this.oneofs[j] = new OneofAccessor(this.descriptor, j, this.camelCaseNames[j + fieldsSize], messageClass, builderClass); 
        this.initialized = true;
        this.camelCaseNames = null;
        return this;
      } 
    }
    
    private FieldAccessor getField(Descriptors.FieldDescriptor field) {
      if (field.getContainingType() != this.descriptor)
        throw new IllegalArgumentException("FieldDescriptor does not match message type."); 
      if (field.isExtension())
        throw new IllegalArgumentException("This type does not have extensions."); 
      return this.fields[field.getIndex()];
    }
    
    private OneofAccessor getOneof(Descriptors.OneofDescriptor oneof) {
      if (oneof.getContainingType() != this.descriptor)
        throw new IllegalArgumentException("OneofDescriptor does not match message type."); 
      return this.oneofs[oneof.getIndex()];
    }
    
    private static interface FieldAccessor {
      Object get(GeneratedMessageV3 param2GeneratedMessageV3);
      
      Object get(GeneratedMessageV3.Builder param2Builder);
      
      Object getRaw(GeneratedMessageV3 param2GeneratedMessageV3);
      
      Object getRaw(GeneratedMessageV3.Builder param2Builder);
      
      void set(GeneratedMessageV3.Builder param2Builder, Object param2Object);
      
      Object getRepeated(GeneratedMessageV3 param2GeneratedMessageV3, int param2Int);
      
      Object getRepeated(GeneratedMessageV3.Builder param2Builder, int param2Int);
      
      Object getRepeatedRaw(GeneratedMessageV3 param2GeneratedMessageV3, int param2Int);
      
      Object getRepeatedRaw(GeneratedMessageV3.Builder param2Builder, int param2Int);
      
      void setRepeated(GeneratedMessageV3.Builder param2Builder, int param2Int, Object param2Object);
      
      void addRepeated(GeneratedMessageV3.Builder param2Builder, Object param2Object);
      
      boolean has(GeneratedMessageV3 param2GeneratedMessageV3);
      
      boolean has(GeneratedMessageV3.Builder param2Builder);
      
      int getRepeatedCount(GeneratedMessageV3 param2GeneratedMessageV3);
      
      int getRepeatedCount(GeneratedMessageV3.Builder param2Builder);
      
      void clear(GeneratedMessageV3.Builder param2Builder);
      
      Message.Builder newBuilder();
      
      Message.Builder getBuilder(GeneratedMessageV3.Builder param2Builder);
      
      Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder param2Builder, int param2Int);
    }
    
    private static class OneofAccessor {
      private final Descriptors.Descriptor descriptor;
      
      private final Method caseMethod;
      
      private final Method caseMethodBuilder;
      
      private final Method clearMethod;
      
      private final Descriptors.FieldDescriptor fieldDescriptor;
      
      OneofAccessor(Descriptors.Descriptor descriptor, int oneofIndex, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
        this.descriptor = descriptor;
        Descriptors.OneofDescriptor oneofDescriptor = descriptor.getOneofs().get(oneofIndex);
        if (oneofDescriptor.isSynthetic()) {
          this.caseMethod = null;
          this.caseMethodBuilder = null;
          this.fieldDescriptor = oneofDescriptor.getFields().get(0);
        } else {
          this.caseMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Case", new Class[0]);
          this.caseMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Case", new Class[0]);
          this.fieldDescriptor = null;
        } 
        this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
      }
      
      public boolean has(GeneratedMessageV3 message) {
        if (this.fieldDescriptor != null)
          return message.hasField(this.fieldDescriptor); 
        if (((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber() == 0)
          return false; 
        return true;
      }
      
      public boolean has(GeneratedMessageV3.Builder builder) {
        if (this.fieldDescriptor != null)
          return builder.hasField(this.fieldDescriptor); 
        if (((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber() == 0)
          return false; 
        return true;
      }
      
      public Descriptors.FieldDescriptor get(GeneratedMessageV3 message) {
        if (this.fieldDescriptor != null)
          return message.hasField(this.fieldDescriptor) ? this.fieldDescriptor : null; 
        int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
        if (fieldNumber > 0)
          return this.descriptor.findFieldByNumber(fieldNumber); 
        return null;
      }
      
      public Descriptors.FieldDescriptor get(GeneratedMessageV3.Builder builder) {
        if (this.fieldDescriptor != null)
          return builder.hasField(this.fieldDescriptor) ? this.fieldDescriptor : null; 
        int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
        if (fieldNumber > 0)
          return this.descriptor.findFieldByNumber(fieldNumber); 
        return null;
      }
      
      public void clear(GeneratedMessageV3.Builder builder) {
        Object unused = GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]);
      }
    }
    
    private static class SingularFieldAccessor implements FieldAccessor {
      protected final Class<?> type;
      
      protected final Descriptors.FieldDescriptor field;
      
      protected final boolean isOneofField;
      
      protected final boolean hasHasMethod;
      
      protected final MethodInvoker invoker;
      
      private static final class ReflectionInvoker implements MethodInvoker {
        protected final Method getMethod;
        
        protected final Method getMethodBuilder;
        
        protected final Method setMethod;
        
        protected final Method hasMethod;
        
        protected final Method hasMethodBuilder;
        
        protected final Method clearMethod;
        
        protected final Method caseMethod;
        
        protected final Method caseMethodBuilder;
        
        ReflectionInvoker(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass, String containingOneofCamelCaseName, boolean isOneofField, boolean hasHasMethod) {
          this.getMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[0]);
          this.getMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[0]);
          Class<?> type = this.getMethod.getReturnType();
          this.setMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[] { type });
          this.hasMethod = hasHasMethod ? GeneratedMessageV3.getMethodOrDie(messageClass, "has" + camelCaseName, new Class[0]) : null;
          this
            .hasMethodBuilder = hasHasMethod ? GeneratedMessageV3.getMethodOrDie(builderClass, "has" + camelCaseName, new Class[0]) : null;
          this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
          this
            
            .caseMethod = isOneofField ? GeneratedMessageV3.getMethodOrDie(messageClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
          this
            
            .caseMethodBuilder = isOneofField ? GeneratedMessageV3.getMethodOrDie(builderClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
        }
        
        public Object get(GeneratedMessageV3 message) {
          return GeneratedMessageV3.invokeOrDie(this.getMethod, message, new Object[0]);
        }
        
        public Object get(GeneratedMessageV3.Builder<?> builder) {
          return GeneratedMessageV3.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
        }
        
        public int getOneofFieldNumber(GeneratedMessageV3 message) {
          return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
        }
        
        public int getOneofFieldNumber(GeneratedMessageV3.Builder<?> builder) {
          return ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
        }
        
        public void set(GeneratedMessageV3.Builder<?> builder, Object value) {
          Object unused = GeneratedMessageV3.invokeOrDie(this.setMethod, builder, new Object[] { value });
        }
        
        public boolean has(GeneratedMessageV3 message) {
          return ((Boolean)GeneratedMessageV3.invokeOrDie(this.hasMethod, message, new Object[0])).booleanValue();
        }
        
        public boolean has(GeneratedMessageV3.Builder<?> builder) {
          return ((Boolean)GeneratedMessageV3.invokeOrDie(this.hasMethodBuilder, builder, new Object[0])).booleanValue();
        }
        
        public void clear(GeneratedMessageV3.Builder<?> builder) {
          Object unused = GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]);
        }
      }
      
      SingularFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass, String containingOneofCamelCaseName) {
        this
          
          .isOneofField = (descriptor.getContainingOneof() != null && !descriptor.getContainingOneof().isSynthetic());
        this
          
          .hasHasMethod = (descriptor.getFile().getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO2 || descriptor.hasOptionalKeyword() || (!this.isOneofField && descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE));
        ReflectionInvoker reflectionInvoker = new ReflectionInvoker(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName, this.isOneofField, this.hasHasMethod);
        this.field = descriptor;
        this.type = reflectionInvoker.getMethod.getReturnType();
        this.invoker = getMethodInvoker(reflectionInvoker);
      }
      
      static MethodInvoker getMethodInvoker(ReflectionInvoker accessor) {
        return accessor;
      }
      
      public Object get(GeneratedMessageV3 message) {
        return this.invoker.get(message);
      }
      
      public Object get(GeneratedMessageV3.Builder<?> builder) {
        return this.invoker.get(builder);
      }
      
      public Object getRaw(GeneratedMessageV3 message) {
        return get(message);
      }
      
      public Object getRaw(GeneratedMessageV3.Builder builder) {
        return get(builder);
      }
      
      public void set(GeneratedMessageV3.Builder<?> builder, Object value) {
        this.invoker.set(builder, value);
      }
      
      public Object getRepeated(GeneratedMessageV3 message, int index) {
        throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
      }
      
      public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
        throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
      }
      
      public Object getRepeated(GeneratedMessageV3.Builder builder, int index) {
        throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
      }
      
      public Object getRepeatedRaw(GeneratedMessageV3.Builder builder, int index) {
        throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
      }
      
      public void setRepeated(GeneratedMessageV3.Builder builder, int index, Object value) {
        throw new UnsupportedOperationException("setRepeatedField() called on a singular field.");
      }
      
      public void addRepeated(GeneratedMessageV3.Builder builder, Object value) {
        throw new UnsupportedOperationException("addRepeatedField() called on a singular field.");
      }
      
      public boolean has(GeneratedMessageV3 message) {
        if (!this.hasHasMethod) {
          if (this.isOneofField)
            return (this.invoker.getOneofFieldNumber(message) == this.field.getNumber()); 
          return !get(message).equals(this.field.getDefaultValue());
        } 
        return this.invoker.has(message);
      }
      
      public boolean has(GeneratedMessageV3.Builder<?> builder) {
        if (!this.hasHasMethod) {
          if (this.isOneofField)
            return (this.invoker.getOneofFieldNumber(builder) == this.field.getNumber()); 
          return !get(builder).equals(this.field.getDefaultValue());
        } 
        return this.invoker.has(builder);
      }
      
      public int getRepeatedCount(GeneratedMessageV3 message) {
        throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
      }
      
      public int getRepeatedCount(GeneratedMessageV3.Builder builder) {
        throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
      }
      
      public void clear(GeneratedMessageV3.Builder<?> builder) {
        this.invoker.clear(builder);
      }
      
      public Message.Builder newBuilder() {
        throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
      }
      
      public Message.Builder getBuilder(GeneratedMessageV3.Builder builder) {
        throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
      }
      
      public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder builder, int index) {
        throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
      }
      
      private static interface MethodInvoker {
        Object get(GeneratedMessageV3 param3GeneratedMessageV3);
        
        Object get(GeneratedMessageV3.Builder<?> param3Builder);
        
        int getOneofFieldNumber(GeneratedMessageV3 param3GeneratedMessageV3);
        
        int getOneofFieldNumber(GeneratedMessageV3.Builder<?> param3Builder);
        
        void set(GeneratedMessageV3.Builder<?> param3Builder, Object param3Object);
        
        boolean has(GeneratedMessageV3 param3GeneratedMessageV3);
        
        boolean has(GeneratedMessageV3.Builder<?> param3Builder);
        
        void clear(GeneratedMessageV3.Builder<?> param3Builder);
      }
    }
    
    private static class RepeatedFieldAccessor implements FieldAccessor {
      protected final Class type;
      
      protected final MethodInvoker invoker;
      
      private static final class ReflectionInvoker implements MethodInvoker {
        protected final Method getMethod;
        
        protected final Method getMethodBuilder;
        
        protected final Method getRepeatedMethod;
        
        protected final Method getRepeatedMethodBuilder;
        
        protected final Method setRepeatedMethod;
        
        protected final Method addRepeatedMethod;
        
        protected final Method getCountMethod;
        
        protected final Method getCountMethodBuilder;
        
        protected final Method clearMethod;
        
        ReflectionInvoker(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
          this.getMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "List", new Class[0]);
          this.getMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "List", new Class[0]);
          this.getRepeatedMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[] { int.class });
          this
            .getRepeatedMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[] { int.class });
          Class<?> type = this.getRepeatedMethod.getReturnType();
          this
            .setRepeatedMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[] { int.class, type });
          this.addRepeatedMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "add" + camelCaseName, new Class[] { type });
          this.getCountMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Count", new Class[0]);
          this.getCountMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Count", new Class[0]);
          this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
        }
        
        public Object get(GeneratedMessageV3 message) {
          return GeneratedMessageV3.invokeOrDie(this.getMethod, message, new Object[0]);
        }
        
        public Object get(GeneratedMessageV3.Builder<?> builder) {
          return GeneratedMessageV3.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
        }
        
        public Object getRepeated(GeneratedMessageV3 message, int index) {
          return GeneratedMessageV3.invokeOrDie(this.getRepeatedMethod, message, new Object[] { Integer.valueOf(index) });
        }
        
        public Object getRepeated(GeneratedMessageV3.Builder<?> builder, int index) {
          return GeneratedMessageV3.invokeOrDie(this.getRepeatedMethodBuilder, builder, new Object[] { Integer.valueOf(index) });
        }
        
        public void setRepeated(GeneratedMessageV3.Builder<?> builder, int index, Object value) {
          Object unused = GeneratedMessageV3.invokeOrDie(this.setRepeatedMethod, builder, new Object[] { Integer.valueOf(index), value });
        }
        
        public void addRepeated(GeneratedMessageV3.Builder<?> builder, Object value) {
          Object unused = GeneratedMessageV3.invokeOrDie(this.addRepeatedMethod, builder, new Object[] { value });
        }
        
        public int getRepeatedCount(GeneratedMessageV3 message) {
          return ((Integer)GeneratedMessageV3.invokeOrDie(this.getCountMethod, message, new Object[0])).intValue();
        }
        
        public int getRepeatedCount(GeneratedMessageV3.Builder<?> builder) {
          return ((Integer)GeneratedMessageV3.invokeOrDie(this.getCountMethodBuilder, builder, new Object[0])).intValue();
        }
        
        public void clear(GeneratedMessageV3.Builder<?> builder) {
          Object unused = GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]);
        }
      }
      
      RepeatedFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
        ReflectionInvoker reflectionInvoker = new ReflectionInvoker(descriptor, camelCaseName, messageClass, builderClass);
        this.type = reflectionInvoker.getRepeatedMethod.getReturnType();
        this.invoker = getMethodInvoker(reflectionInvoker);
      }
      
      static MethodInvoker getMethodInvoker(ReflectionInvoker accessor) {
        return accessor;
      }
      
      public Object get(GeneratedMessageV3 message) {
        return this.invoker.get(message);
      }
      
      public Object get(GeneratedMessageV3.Builder<?> builder) {
        return this.invoker.get(builder);
      }
      
      public Object getRaw(GeneratedMessageV3 message) {
        return get(message);
      }
      
      public Object getRaw(GeneratedMessageV3.Builder builder) {
        return get(builder);
      }
      
      public void set(GeneratedMessageV3.Builder builder, Object value) {
        clear(builder);
        for (Object element : value)
          addRepeated(builder, element); 
      }
      
      public Object getRepeated(GeneratedMessageV3 message, int index) {
        return this.invoker.getRepeated(message, index);
      }
      
      public Object getRepeated(GeneratedMessageV3.Builder<?> builder, int index) {
        return this.invoker.getRepeated(builder, index);
      }
      
      public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
        return getRepeated(message, index);
      }
      
      public Object getRepeatedRaw(GeneratedMessageV3.Builder builder, int index) {
        return getRepeated(builder, index);
      }
      
      public void setRepeated(GeneratedMessageV3.Builder<?> builder, int index, Object value) {
        this.invoker.setRepeated(builder, index, value);
      }
      
      public void addRepeated(GeneratedMessageV3.Builder<?> builder, Object value) {
        this.invoker.addRepeated(builder, value);
      }
      
      public boolean has(GeneratedMessageV3 message) {
        throw new UnsupportedOperationException("hasField() called on a repeated field.");
      }
      
      public boolean has(GeneratedMessageV3.Builder builder) {
        throw new UnsupportedOperationException("hasField() called on a repeated field.");
      }
      
      public int getRepeatedCount(GeneratedMessageV3 message) {
        return this.invoker.getRepeatedCount(message);
      }
      
      public int getRepeatedCount(GeneratedMessageV3.Builder<?> builder) {
        return this.invoker.getRepeatedCount(builder);
      }
      
      public void clear(GeneratedMessageV3.Builder<?> builder) {
        this.invoker.clear(builder);
      }
      
      public Message.Builder newBuilder() {
        throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
      }
      
      public Message.Builder getBuilder(GeneratedMessageV3.Builder builder) {
        throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
      }
      
      public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder builder, int index) {
        throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
      }
      
      static interface MethodInvoker {
        Object get(GeneratedMessageV3 param3GeneratedMessageV3);
        
        Object get(GeneratedMessageV3.Builder<?> param3Builder);
        
        Object getRepeated(GeneratedMessageV3 param3GeneratedMessageV3, int param3Int);
        
        Object getRepeated(GeneratedMessageV3.Builder<?> param3Builder, int param3Int);
        
        void setRepeated(GeneratedMessageV3.Builder<?> param3Builder, int param3Int, Object param3Object);
        
        void addRepeated(GeneratedMessageV3.Builder<?> param3Builder, Object param3Object);
        
        int getRepeatedCount(GeneratedMessageV3 param3GeneratedMessageV3);
        
        int getRepeatedCount(GeneratedMessageV3.Builder<?> param3Builder);
        
        void clear(GeneratedMessageV3.Builder<?> param3Builder);
      }
    }
    
    private static class MapFieldAccessor implements FieldAccessor {
      private final Descriptors.FieldDescriptor field;
      
      private final Message mapEntryMessageDefaultInstance;
      
      MapFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
        this.field = descriptor;
        Method getDefaultInstanceMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "getDefaultInstance", new Class[0]);
        MapField<?, ?> defaultMapField = getMapField(
            (GeneratedMessageV3)GeneratedMessageV3.invokeOrDie(getDefaultInstanceMethod, null, new Object[0]));
        this
          .mapEntryMessageDefaultInstance = defaultMapField.getMapEntryMessageDefaultInstance();
      }
      
      private MapField<?, ?> getMapField(GeneratedMessageV3 message) {
        return message.internalGetMapField(this.field.getNumber());
      }
      
      private MapField<?, ?> getMapField(GeneratedMessageV3.Builder builder) {
        return builder.internalGetMapField(this.field.getNumber());
      }
      
      private MapField<?, ?> getMutableMapField(GeneratedMessageV3.Builder builder) {
        return builder.internalGetMutableMapField(this.field
            .getNumber());
      }
      
      private Message coerceType(Message value) {
        if (value == null)
          return null; 
        if (this.mapEntryMessageDefaultInstance.getClass().isInstance(value))
          return value; 
        return this.mapEntryMessageDefaultInstance.toBuilder().mergeFrom(value).build();
      }
      
      public Object get(GeneratedMessageV3 message) {
        List<Object> result = new ArrayList();
        for (int i = 0; i < getRepeatedCount(message); i++)
          result.add(getRepeated(message, i)); 
        return Collections.unmodifiableList(result);
      }
      
      public Object get(GeneratedMessageV3.Builder builder) {
        List<Object> result = new ArrayList();
        for (int i = 0; i < getRepeatedCount(builder); i++)
          result.add(getRepeated(builder, i)); 
        return Collections.unmodifiableList(result);
      }
      
      public Object getRaw(GeneratedMessageV3 message) {
        return get(message);
      }
      
      public Object getRaw(GeneratedMessageV3.Builder builder) {
        return get(builder);
      }
      
      public void set(GeneratedMessageV3.Builder builder, Object value) {
        clear(builder);
        for (Object entry : value)
          addRepeated(builder, entry); 
      }
      
      public Object getRepeated(GeneratedMessageV3 message, int index) {
        return getMapField(message).getList().get(index);
      }
      
      public Object getRepeated(GeneratedMessageV3.Builder builder, int index) {
        return getMapField(builder).getList().get(index);
      }
      
      public Object getRepeatedRaw(GeneratedMessageV3 message, int index) {
        return getRepeated(message, index);
      }
      
      public Object getRepeatedRaw(GeneratedMessageV3.Builder builder, int index) {
        return getRepeated(builder, index);
      }
      
      public void setRepeated(GeneratedMessageV3.Builder builder, int index, Object value) {
        getMutableMapField(builder).getMutableList().set(index, coerceType((Message)value));
      }
      
      public void addRepeated(GeneratedMessageV3.Builder builder, Object value) {
        getMutableMapField(builder).getMutableList().add(coerceType((Message)value));
      }
      
      public boolean has(GeneratedMessageV3 message) {
        throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
      }
      
      public boolean has(GeneratedMessageV3.Builder builder) {
        throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
      }
      
      public int getRepeatedCount(GeneratedMessageV3 message) {
        return getMapField(message).getList().size();
      }
      
      public int getRepeatedCount(GeneratedMessageV3.Builder builder) {
        return getMapField(builder).getList().size();
      }
      
      public void clear(GeneratedMessageV3.Builder builder) {
        getMutableMapField(builder).getMutableList().clear();
      }
      
      public Message.Builder newBuilder() {
        return this.mapEntryMessageDefaultInstance.newBuilderForType();
      }
      
      public Message.Builder getBuilder(GeneratedMessageV3.Builder builder) {
        throw new UnsupportedOperationException("Nested builder not supported for map fields.");
      }
      
      public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder builder, int index) {
        throw new UnsupportedOperationException("Map fields cannot be repeated");
      }
    }
    
    private static final class SingularEnumFieldAccessor extends SingularFieldAccessor {
      private Descriptors.EnumDescriptor enumDescriptor;
      
      private Method valueOfMethod;
      
      private Method getValueDescriptorMethod;
      
      private boolean supportUnknownEnumValue;
      
      private Method getValueMethod;
      
      private Method getValueMethodBuilder;
      
      private Method setValueMethod;
      
      SingularEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass, String containingOneofCamelCaseName) {
        super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
        this.enumDescriptor = descriptor.getEnumType();
        this.valueOfMethod = GeneratedMessageV3.getMethodOrDie(this.type, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
        this.getValueDescriptorMethod = GeneratedMessageV3.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
        this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
        if (this.supportUnknownEnumValue) {
          this
            .getValueMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[0]);
          this
            .getValueMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[0]);
          this
            .setValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[] { int.class });
        } 
      }
      
      public Object get(GeneratedMessageV3 message) {
        if (this.supportUnknownEnumValue) {
          int value = ((Integer)GeneratedMessageV3.invokeOrDie(this.getValueMethod, message, new Object[0])).intValue();
          return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
        } 
        return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.get(message), new Object[0]);
      }
      
      public Object get(GeneratedMessageV3.Builder builder) {
        if (this.supportUnknownEnumValue) {
          int value = ((Integer)GeneratedMessageV3.invokeOrDie(this.getValueMethodBuilder, builder, new Object[0])).intValue();
          return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
        } 
        return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.get(builder), new Object[0]);
      }
      
      public void set(GeneratedMessageV3.Builder builder, Object value) {
        if (this.supportUnknownEnumValue) {
          Object unused = GeneratedMessageV3.invokeOrDie(this.setValueMethod, builder, new Object[] { Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
          return;
        } 
        super.set(builder, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
      }
    }
    
    private static final class RepeatedEnumFieldAccessor extends RepeatedFieldAccessor {
      private Descriptors.EnumDescriptor enumDescriptor;
      
      private final Method valueOfMethod;
      
      private final Method getValueDescriptorMethod;
      
      private boolean supportUnknownEnumValue;
      
      private Method getRepeatedValueMethod;
      
      private Method getRepeatedValueMethodBuilder;
      
      private Method setRepeatedValueMethod;
      
      private Method addRepeatedValueMethod;
      
      RepeatedEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
        super(descriptor, camelCaseName, messageClass, builderClass);
        this.enumDescriptor = descriptor.getEnumType();
        this.valueOfMethod = GeneratedMessageV3.getMethodOrDie(this.type, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
        this.getValueDescriptorMethod = GeneratedMessageV3.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
        this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
        if (this.supportUnknownEnumValue) {
          this
            .getRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[] { int.class });
          this
            .getRepeatedValueMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[] { int.class });
          this
            .setRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[] { int.class, int.class });
          this
            .addRepeatedValueMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "add" + camelCaseName + "Value", new Class[] { int.class });
        } 
      }
      
      public Object get(GeneratedMessageV3 message) {
        List<Object> newList = new ArrayList();
        int size = getRepeatedCount(message);
        for (int i = 0; i < size; i++)
          newList.add(getRepeated(message, i)); 
        return Collections.unmodifiableList(newList);
      }
      
      public Object get(GeneratedMessageV3.Builder builder) {
        List<Object> newList = new ArrayList();
        int size = getRepeatedCount(builder);
        for (int i = 0; i < size; i++)
          newList.add(getRepeated(builder, i)); 
        return Collections.unmodifiableList(newList);
      }
      
      public Object getRepeated(GeneratedMessageV3 message, int index) {
        if (this.supportUnknownEnumValue) {
          int value = ((Integer)GeneratedMessageV3.invokeOrDie(this.getRepeatedValueMethod, message, new Object[] { Integer.valueOf(index) })).intValue();
          return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
        } 
        return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(message, index), new Object[0]);
      }
      
      public Object getRepeated(GeneratedMessageV3.Builder builder, int index) {
        if (this.supportUnknownEnumValue) {
          int value = ((Integer)GeneratedMessageV3.invokeOrDie(this.getRepeatedValueMethodBuilder, builder, new Object[] { Integer.valueOf(index) })).intValue();
          return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
        } 
        return GeneratedMessageV3.invokeOrDie(this.getValueDescriptorMethod, super.getRepeated(builder, index), new Object[0]);
      }
      
      public void setRepeated(GeneratedMessageV3.Builder builder, int index, Object value) {
        if (this.supportUnknownEnumValue) {
          Object unused = GeneratedMessageV3.invokeOrDie(this.setRepeatedValueMethod, builder, new Object[] { Integer.valueOf(index), 
                Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
          return;
        } 
        super.setRepeated(builder, index, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
      }
      
      public void addRepeated(GeneratedMessageV3.Builder builder, Object value) {
        if (this.supportUnknownEnumValue) {
          Object unused = GeneratedMessageV3.invokeOrDie(this.addRepeatedValueMethod, builder, new Object[] { Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
          return;
        } 
        super.addRepeated(builder, GeneratedMessageV3.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
      }
    }
    
    private static final class SingularStringFieldAccessor extends SingularFieldAccessor {
      private final Method getBytesMethod;
      
      private final Method getBytesMethodBuilder;
      
      private final Method setBytesMethodBuilder;
      
      SingularStringFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass, String containingOneofCamelCaseName) {
        super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
        this.getBytesMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Bytes", new Class[0]);
        this.getBytesMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Bytes", new Class[0]);
        this.setBytesMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "set" + camelCaseName + "Bytes", new Class[] { ByteString.class });
      }
      
      public Object getRaw(GeneratedMessageV3 message) {
        return GeneratedMessageV3.invokeOrDie(this.getBytesMethod, message, new Object[0]);
      }
      
      public Object getRaw(GeneratedMessageV3.Builder builder) {
        return GeneratedMessageV3.invokeOrDie(this.getBytesMethodBuilder, builder, new Object[0]);
      }
      
      public void set(GeneratedMessageV3.Builder builder, Object value) {
        if (value instanceof ByteString) {
          Object object = GeneratedMessageV3.invokeOrDie(this.setBytesMethodBuilder, builder, new Object[] { value });
        } else {
          super.set(builder, value);
        } 
      }
    }
    
    private static final class SingularMessageFieldAccessor extends SingularFieldAccessor {
      private final Method newBuilderMethod;
      
      private final Method getBuilderMethodBuilder;
      
      SingularMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass, String containingOneofCamelCaseName) {
        super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
        this.newBuilderMethod = GeneratedMessageV3.getMethodOrDie(this.type, "newBuilder", new Class[0]);
        this
          .getBuilderMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[0]);
      }
      
      private Object coerceType(Object value) {
        if (this.type.isInstance(value))
          return value; 
        return ((Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0]))
          .mergeFrom((Message)value)
          .buildPartial();
      }
      
      public void set(GeneratedMessageV3.Builder builder, Object value) {
        super.set(builder, coerceType(value));
      }
      
      public Message.Builder newBuilder() {
        return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
      }
      
      public Message.Builder getBuilder(GeneratedMessageV3.Builder builder) {
        return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[0]);
      }
    }
    
    private static final class RepeatedMessageFieldAccessor extends RepeatedFieldAccessor {
      private final Method newBuilderMethod;
      
      private final Method getBuilderMethodBuilder;
      
      RepeatedMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
        super(descriptor, camelCaseName, messageClass, builderClass);
        this.newBuilderMethod = GeneratedMessageV3.getMethodOrDie(this.type, "newBuilder", new Class[0]);
        this.getBuilderMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[] { int.class });
      }
      
      private Object coerceType(Object value) {
        if (this.type.isInstance(value))
          return value; 
        return ((Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0]))
          .mergeFrom((Message)value)
          .build();
      }
      
      public void setRepeated(GeneratedMessageV3.Builder builder, int index, Object value) {
        super.setRepeated(builder, index, coerceType(value));
      }
      
      public void addRepeated(GeneratedMessageV3.Builder builder, Object value) {
        super.addRepeated(builder, coerceType(value));
      }
      
      public Message.Builder newBuilder() {
        return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
      }
      
      public Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder builder, int index) {
        return (Message.Builder)GeneratedMessageV3.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[] { Integer.valueOf(index) });
      }
    }
  }
  
  private static class OneofAccessor {
    private final Descriptors.Descriptor descriptor;
    
    private final Method caseMethod;
    
    private final Method caseMethodBuilder;
    
    private final Method clearMethod;
    
    private final Descriptors.FieldDescriptor fieldDescriptor;
    
    OneofAccessor(Descriptors.Descriptor descriptor, int oneofIndex, String camelCaseName, Class<? extends GeneratedMessageV3> messageClass, Class<? extends GeneratedMessageV3.Builder> builderClass) {
      this.descriptor = descriptor;
      Descriptors.OneofDescriptor oneofDescriptor = descriptor.getOneofs().get(oneofIndex);
      if (oneofDescriptor.isSynthetic()) {
        this.caseMethod = null;
        this.caseMethodBuilder = null;
        this.fieldDescriptor = oneofDescriptor.getFields().get(0);
      } else {
        this.caseMethod = GeneratedMessageV3.getMethodOrDie(messageClass, "get" + camelCaseName + "Case", new Class[0]);
        this.caseMethodBuilder = GeneratedMessageV3.getMethodOrDie(builderClass, "get" + camelCaseName + "Case", new Class[0]);
        this.fieldDescriptor = null;
      } 
      this.clearMethod = GeneratedMessageV3.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
    }
    
    public boolean has(GeneratedMessageV3 message) {
      if (this.fieldDescriptor != null)
        return message.hasField(this.fieldDescriptor); 
      if (((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber() == 0)
        return false; 
      return true;
    }
    
    public boolean has(GeneratedMessageV3.Builder builder) {
      if (this.fieldDescriptor != null)
        return builder.hasField(this.fieldDescriptor); 
      if (((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber() == 0)
        return false; 
      return true;
    }
    
    public Descriptors.FieldDescriptor get(GeneratedMessageV3 message) {
      if (this.fieldDescriptor != null)
        return message.hasField(this.fieldDescriptor) ? this.fieldDescriptor : null; 
      int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
      if (fieldNumber > 0)
        return this.descriptor.findFieldByNumber(fieldNumber); 
      return null;
    }
    
    public Descriptors.FieldDescriptor get(GeneratedMessageV3.Builder builder) {
      if (this.fieldDescriptor != null)
        return builder.hasField(this.fieldDescriptor) ? this.fieldDescriptor : null; 
      int fieldNumber = ((Internal.EnumLite)GeneratedMessageV3.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
      if (fieldNumber > 0)
        return this.descriptor.findFieldByNumber(fieldNumber); 
      return null;
    }
    
    public void clear(GeneratedMessageV3.Builder builder) {
      Object unused = GeneratedMessageV3.invokeOrDie(this.clearMethod, builder, new Object[0]);
    }
  }
  
  protected Object writeReplace() throws ObjectStreamException {
    return new GeneratedMessageLite.SerializedForm(this);
  }
  
  private static <MessageType extends ExtendableMessage<MessageType>, T> Extension<MessageType, T> checkNotLite(ExtensionLite<MessageType, T> extension) {
    if (extension.isLite())
      throw new IllegalArgumentException("Expected non-lite extension."); 
    return (Extension<MessageType, T>)extension;
  }
  
  protected static boolean isStringEmpty(Object value) {
    if (value instanceof String)
      return ((String)value).isEmpty(); 
    return ((ByteString)value).isEmpty();
  }
  
  protected static int computeStringSize(int fieldNumber, Object value) {
    if (value instanceof String)
      return CodedOutputStream.computeStringSize(fieldNumber, (String)value); 
    return CodedOutputStream.computeBytesSize(fieldNumber, (ByteString)value);
  }
  
  protected static int computeStringSizeNoTag(Object value) {
    if (value instanceof String)
      return CodedOutputStream.computeStringSizeNoTag((String)value); 
    return CodedOutputStream.computeBytesSizeNoTag((ByteString)value);
  }
  
  protected static void writeString(CodedOutputStream output, int fieldNumber, Object value) throws IOException {
    if (value instanceof String) {
      output.writeString(fieldNumber, (String)value);
    } else {
      output.writeBytes(fieldNumber, (ByteString)value);
    } 
  }
  
  protected static void writeStringNoTag(CodedOutputStream output, Object value) throws IOException {
    if (value instanceof String) {
      output.writeStringNoTag((String)value);
    } else {
      output.writeBytesNoTag((ByteString)value);
    } 
  }
  
  protected static <V> void serializeIntegerMapTo(CodedOutputStream out, MapField<Integer, V> field, MapEntry<Integer, V> defaultEntry, int fieldNumber) throws IOException {
    Map<Integer, V> m = field.getMap();
    if (!out.isSerializationDeterministic()) {
      serializeMapTo(out, m, defaultEntry, fieldNumber);
      return;
    } 
    int[] keys = new int[m.size()];
    int index = 0;
    for (Iterator<Integer> iterator = m.keySet().iterator(); iterator.hasNext(); ) {
      int k = ((Integer)iterator.next()).intValue();
      keys[index++] = k;
    } 
    Arrays.sort(keys);
    for (int key : keys)
      out.writeMessage(fieldNumber, defaultEntry
          .newBuilderForType()
          .setKey(Integer.valueOf(key))
          .setValue(m.get(Integer.valueOf(key)))
          .build()); 
  }
  
  protected static <V> void serializeLongMapTo(CodedOutputStream out, MapField<Long, V> field, MapEntry<Long, V> defaultEntry, int fieldNumber) throws IOException {
    Map<Long, V> m = field.getMap();
    if (!out.isSerializationDeterministic()) {
      serializeMapTo(out, m, defaultEntry, fieldNumber);
      return;
    } 
    long[] keys = new long[m.size()];
    int index = 0;
    for (Iterator<Long> iterator = m.keySet().iterator(); iterator.hasNext(); ) {
      long k = ((Long)iterator.next()).longValue();
      keys[index++] = k;
    } 
    Arrays.sort(keys);
    for (long key : keys)
      out.writeMessage(fieldNumber, defaultEntry
          .newBuilderForType()
          .setKey(Long.valueOf(key))
          .setValue(m.get(Long.valueOf(key)))
          .build()); 
  }
  
  protected static <V> void serializeStringMapTo(CodedOutputStream out, MapField<String, V> field, MapEntry<String, V> defaultEntry, int fieldNumber) throws IOException {
    Map<String, V> m = field.getMap();
    if (!out.isSerializationDeterministic()) {
      serializeMapTo(out, m, defaultEntry, fieldNumber);
      return;
    } 
    String[] keys = new String[m.size()];
    keys = (String[])m.keySet().toArray((Object[])keys);
    Arrays.sort((Object[])keys);
    for (String key : keys)
      out.writeMessage(fieldNumber, defaultEntry
          .newBuilderForType()
          .setKey(key)
          .setValue(m.get(key))
          .build()); 
  }
  
  protected static <V> void serializeBooleanMapTo(CodedOutputStream out, MapField<Boolean, V> field, MapEntry<Boolean, V> defaultEntry, int fieldNumber) throws IOException {
    Map<Boolean, V> m = field.getMap();
    if (!out.isSerializationDeterministic()) {
      serializeMapTo(out, m, defaultEntry, fieldNumber);
      return;
    } 
    maybeSerializeBooleanEntryTo(out, m, defaultEntry, fieldNumber, false);
    maybeSerializeBooleanEntryTo(out, m, defaultEntry, fieldNumber, true);
  }
  
  private static <V> void maybeSerializeBooleanEntryTo(CodedOutputStream out, Map<Boolean, V> m, MapEntry<Boolean, V> defaultEntry, int fieldNumber, boolean key) throws IOException {
    if (m.containsKey(Boolean.valueOf(key)))
      out.writeMessage(fieldNumber, defaultEntry
          .newBuilderForType()
          .setKey(Boolean.valueOf(key))
          .setValue(m.get(Boolean.valueOf(key)))
          .build()); 
  }
  
  private static <K, V> void serializeMapTo(CodedOutputStream out, Map<K, V> m, MapEntry<K, V> defaultEntry, int fieldNumber) throws IOException {
    for (Map.Entry<K, V> entry : m.entrySet())
      out.writeMessage(fieldNumber, defaultEntry
          .newBuilderForType()
          .setKey(entry.getKey())
          .setValue(entry.getValue())
          .build()); 
  }
  
  protected abstract FieldAccessorTable internalGetFieldAccessorTable();
  
  protected abstract Message.Builder newBuilderForType(BuilderParent paramBuilderParent);
  
  static interface ExtensionDescriptorRetriever {
    Descriptors.FieldDescriptor getDescriptor();
  }
  
  public static interface ExtendableMessageOrBuilder<MessageType extends ExtendableMessage> extends MessageOrBuilder {
    Message getDefaultInstanceForType();
    
    <Type> boolean hasExtension(ExtensionLite<MessageType, Type> param1ExtensionLite);
    
    <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> param1ExtensionLite);
    
    <Type> Type getExtension(ExtensionLite<MessageType, Type> param1ExtensionLite);
    
    <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> param1ExtensionLite, int param1Int);
    
    <Type> boolean hasExtension(Extension<MessageType, Type> param1Extension);
    
    <Type> boolean hasExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> param1GeneratedExtension);
    
    <Type> int getExtensionCount(Extension<MessageType, List<Type>> param1Extension);
    
    <Type> int getExtensionCount(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> param1GeneratedExtension);
    
    <Type> Type getExtension(Extension<MessageType, Type> param1Extension);
    
    <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, Type> param1GeneratedExtension);
    
    <Type> Type getExtension(Extension<MessageType, List<Type>> param1Extension, int param1Int);
    
    <Type> Type getExtension(GeneratedMessage.GeneratedExtension<MessageType, List<Type>> param1GeneratedExtension, int param1Int);
  }
  
  protected static interface BuilderParent extends AbstractMessage.BuilderParent {}
  
  private static interface FieldAccessor {
    Object get(GeneratedMessageV3 param1GeneratedMessageV3);
    
    Object get(GeneratedMessageV3.Builder param1Builder);
    
    Object getRaw(GeneratedMessageV3 param1GeneratedMessageV3);
    
    Object getRaw(GeneratedMessageV3.Builder param1Builder);
    
    void set(GeneratedMessageV3.Builder param1Builder, Object param1Object);
    
    Object getRepeated(GeneratedMessageV3 param1GeneratedMessageV3, int param1Int);
    
    Object getRepeated(GeneratedMessageV3.Builder param1Builder, int param1Int);
    
    Object getRepeatedRaw(GeneratedMessageV3 param1GeneratedMessageV3, int param1Int);
    
    Object getRepeatedRaw(GeneratedMessageV3.Builder param1Builder, int param1Int);
    
    void setRepeated(GeneratedMessageV3.Builder param1Builder, int param1Int, Object param1Object);
    
    void addRepeated(GeneratedMessageV3.Builder param1Builder, Object param1Object);
    
    boolean has(GeneratedMessageV3 param1GeneratedMessageV3);
    
    boolean has(GeneratedMessageV3.Builder param1Builder);
    
    int getRepeatedCount(GeneratedMessageV3 param1GeneratedMessageV3);
    
    int getRepeatedCount(GeneratedMessageV3.Builder param1Builder);
    
    void clear(GeneratedMessageV3.Builder param1Builder);
    
    Message.Builder newBuilder();
    
    Message.Builder getBuilder(GeneratedMessageV3.Builder param1Builder);
    
    Message.Builder getRepeatedBuilder(GeneratedMessageV3.Builder param1Builder, int param1Int);
  }
}
