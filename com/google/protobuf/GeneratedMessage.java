package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public abstract class GeneratedMessage extends AbstractMessage implements Serializable {
  private static final long serialVersionUID = 1L;
  
  protected static boolean alwaysUseFieldBuilders = false;
  
  protected UnknownFieldSet unknownFields;
  
  protected GeneratedMessage() {
    this.unknownFields = UnknownFieldSet.getDefaultInstance();
  }
  
  protected GeneratedMessage(Builder<?> builder) {
    this.unknownFields = builder.getUnknownFields();
  }
  
  public Parser<? extends GeneratedMessage> getParserForType() {
    throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
  }
  
  static void enableAlwaysUseFieldBuildersForTesting() {
    alwaysUseFieldBuilders = true;
  }
  
  public Descriptors.Descriptor getDescriptorForType() {
    return (internalGetFieldAccessorTable()).descriptor;
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
    throw new UnsupportedOperationException("This is supposed to be overridden by subclasses.");
  }
  
  protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
    return unknownFields.mergeFieldFrom(tag, input);
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
  
  protected void makeExtensionsImmutable() {}
  
  protected Message.Builder newBuilderForType(final AbstractMessage.BuilderParent parent) {
    return newBuilderForType(new BuilderParent() {
          public void markDirty() {
            parent.markDirty();
          }
        });
  }
  
  public static abstract class Builder<BuilderType extends Builder<BuilderType>> extends AbstractMessage.Builder<BuilderType> {
    private GeneratedMessage.BuilderParent builderParent;
    
    private BuilderParentImpl meAsParent;
    
    private boolean isClean;
    
    private UnknownFieldSet unknownFields = UnknownFieldSet.getDefaultInstance();
    
    protected Builder() {
      this((GeneratedMessage.BuilderParent)null);
    }
    
    protected Builder(GeneratedMessage.BuilderParent builderParent) {
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
      this.unknownFields = UnknownFieldSet.getDefaultInstance();
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
    
    public BuilderType setUnknownFields(UnknownFieldSet unknownFields) {
      this.unknownFields = unknownFields;
      onChanged();
      return (BuilderType)this;
    }
    
    public BuilderType mergeUnknownFields(UnknownFieldSet unknownFields) {
      this
        
        .unknownFields = UnknownFieldSet.newBuilder(this.unknownFields).mergeFrom(unknownFields).build();
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
      return this.unknownFields;
    }
    
    protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
      return unknownFields.mergeFieldFrom(tag, input);
    }
    
    private class BuilderParentImpl implements GeneratedMessage.BuilderParent {
      private BuilderParentImpl() {}
      
      public void markDirty() {
        GeneratedMessage.Builder.this.onChanged();
      }
    }
    
    protected GeneratedMessage.BuilderParent getParentForChildren() {
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
    
    protected abstract GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable();
  }
  
  public static abstract class ExtendableMessage<MessageType extends ExtendableMessage> extends GeneratedMessage implements ExtendableMessageOrBuilder<MessageType> {
    private static final long serialVersionUID = 1L;
    
    private final FieldSet<Descriptors.FieldDescriptor> extensions;
    
    protected ExtendableMessage() {
      this.extensions = FieldSet.newFieldSet();
    }
    
    protected ExtendableMessage(GeneratedMessage.ExtendableBuilder<MessageType, ?> builder) {
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
      Extension<MessageType, Type> extension = GeneratedMessage.checkNotLite(extensionLite);
      verifyExtensionContainingType(extension);
      return this.extensions.hasField(extension.getDescriptor());
    }
    
    public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      return this.extensions.getRepeatedFieldCount(descriptor);
    }
    
    public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
      Extension<MessageType, Type> extension = GeneratedMessage.checkNotLite(extensionLite);
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
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
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
      return MessageReflection.mergeFieldFrom(input, unknownFields, extensionRegistry, 
          getDescriptorForType(), new MessageReflection.ExtensionAdapter(this.extensions), tag);
    }
    
    protected void makeExtensionsImmutable() {
      this.extensions.makeImmutable();
    }
    
    protected class ExtensionWriter {
      private final Iterator<Map.Entry<Descriptors.FieldDescriptor, Object>> iter = GeneratedMessage.ExtendableMessage.this
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
    private FieldSet<Descriptors.FieldDescriptor> extensions = FieldSet.emptySet();
    
    protected ExtendableBuilder(GeneratedMessage.BuilderParent parent) {
      super(parent);
    }
    
    void internalSetExtensionSet(FieldSet<Descriptors.FieldDescriptor> extensions) {
      this.extensions = extensions;
    }
    
    public BuilderType clear() {
      this.extensions = FieldSet.emptySet();
      return super.clear();
    }
    
    public BuilderType clone() {
      return super.clone();
    }
    
    private void ensureExtensionsIsMutable() {
      if (this.extensions.isImmutable())
        this.extensions = this.extensions.clone(); 
    }
    
    private void verifyExtensionContainingType(Extension<MessageType, ?> extension) {
      if (extension.getDescriptor().getContainingType() != 
        getDescriptorForType())
        throw new IllegalArgumentException("Extension is for type \"" + extension
            
            .getDescriptor().getContainingType().getFullName() + "\" which does not match message type \"" + 
            
            getDescriptorForType().getFullName() + "\"."); 
    }
    
    public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extensionLite) {
      Extension<MessageType, Type> extension = GeneratedMessage.checkNotLite(extensionLite);
      verifyExtensionContainingType(extension);
      return this.extensions.hasField(extension.getDescriptor());
    }
    
    public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extensionLite) {
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      return this.extensions.getRepeatedFieldCount(descriptor);
    }
    
    public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extensionLite) {
      Extension<MessageType, Type> extension = GeneratedMessage.checkNotLite(extensionLite);
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
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      return (Type)extension.singularFromReflectionType(this.extensions
          .getRepeatedField(descriptor, index));
    }
    
    public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extensionLite, Type value) {
      Extension<MessageType, Type> extension = GeneratedMessage.checkNotLite(extensionLite);
      verifyExtensionContainingType(extension);
      ensureExtensionsIsMutable();
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      this.extensions.setField(descriptor, extension.toReflectionType(value));
      onChanged();
      return (BuilderType)this;
    }
    
    public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extensionLite, int index, Type value) {
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      ensureExtensionsIsMutable();
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      this.extensions.setRepeatedField(descriptor, index, extension
          
          .singularToReflectionType(value));
      onChanged();
      return (BuilderType)this;
    }
    
    public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extensionLite, Type value) {
      Extension<MessageType, List<Type>> extension = (Extension)GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
      verifyExtensionContainingType(extension);
      ensureExtensionsIsMutable();
      Descriptors.FieldDescriptor descriptor = extension.getDescriptor();
      this.extensions.addRepeatedField(descriptor, extension
          .singularToReflectionType(value));
      onChanged();
      return (BuilderType)this;
    }
    
    public final <Type> BuilderType clearExtension(ExtensionLite<MessageType, ?> extensionLite) {
      Extension<MessageType, ?> extension = GeneratedMessage.checkNotLite((ExtensionLite)extensionLite);
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
      return this.extensions.isInitialized();
    }
    
    private FieldSet<Descriptors.FieldDescriptor> buildExtensions() {
      this.extensions.makeImmutable();
      return this.extensions;
    }
    
    public boolean isInitialized() {
      return (super.isInitialized() && extensionsAreInitialized());
    }
    
    protected boolean parseUnknownField(CodedInputStream input, UnknownFieldSet.Builder unknownFields, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
      return MessageReflection.mergeFieldFrom(input, unknownFields, extensionRegistry, 
          getDescriptorForType(), new MessageReflection.BuilderAdapter(this), tag);
    }
    
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
      Map<Descriptors.FieldDescriptor, Object> result = getAllFieldsMutable();
      result.putAll(this.extensions.getAllFields());
      return Collections.unmodifiableMap(result);
    }
    
    public Object getField(Descriptors.FieldDescriptor field) {
      if (field.isExtension()) {
        verifyContainingType(field);
        Object value = this.extensions.getField(field);
        if (value == null) {
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
    
    public boolean hasField(Descriptors.FieldDescriptor field) {
      if (field.isExtension()) {
        verifyContainingType(field);
        return this.extensions.hasField(field);
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
    
    protected final void mergeExtensionFields(GeneratedMessage.ExtendableMessage other) {
      ensureExtensionsIsMutable();
      this.extensions.mergeFrom(other.extensions);
      onChanged();
    }
    
    private void verifyContainingType(Descriptors.FieldDescriptor field) {
      if (field.getContainingType() != getDescriptorForType())
        throw new IllegalArgumentException("FieldDescriptor does not match message type."); 
    }
    
    protected ExtendableBuilder() {}
  }
  
  public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newMessageScopedGeneratedExtension(final Message scope, final int descriptorIndex, Class singularType, Message defaultInstance) {
    return new GeneratedExtension<>(new CachedDescriptorRetriever() {
          public Descriptors.FieldDescriptor loadDescriptor() {
            return scope.getDescriptorForType().getExtensions().get(descriptorIndex);
          }
        },  singularType, defaultInstance, Extension.ExtensionType.IMMUTABLE);
  }
  
  public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newFileScopedGeneratedExtension(Class singularType, Message defaultInstance) {
    return new GeneratedExtension<>(null, singularType, defaultInstance, Extension.ExtensionType.IMMUTABLE);
  }
  
  private static abstract class CachedDescriptorRetriever implements ExtensionDescriptorRetriever {
    private volatile Descriptors.FieldDescriptor descriptor;
    
    private CachedDescriptorRetriever() {}
    
    public Descriptors.FieldDescriptor getDescriptor() {
      if (this.descriptor == null)
        synchronized (this) {
          if (this.descriptor == null)
            this.descriptor = loadDescriptor(); 
        }  
      return this.descriptor;
    }
    
    protected abstract Descriptors.FieldDescriptor loadDescriptor();
  }
  
  public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newMessageScopedGeneratedExtension(final Message scope, final String name, Class singularType, Message defaultInstance) {
    return new GeneratedExtension<>(new CachedDescriptorRetriever() {
          protected Descriptors.FieldDescriptor loadDescriptor() {
            return scope.getDescriptorForType().findFieldByName(name);
          }
        },  singularType, defaultInstance, Extension.ExtensionType.MUTABLE);
  }
  
  public static <ContainingType extends Message, Type> GeneratedExtension<ContainingType, Type> newFileScopedGeneratedExtension(final Class singularType, Message defaultInstance, final String descriptorOuterClass, final String extensionName) {
    return new GeneratedExtension<>(new CachedDescriptorRetriever() {
          protected Descriptors.FieldDescriptor loadDescriptor() {
            try {
              Class<?> clazz = singularType.getClassLoader().loadClass(descriptorOuterClass);
              Descriptors.FileDescriptor file = (Descriptors.FileDescriptor)clazz.getField("descriptor").get(null);
              return file.findExtensionByName(extensionName);
            } catch (Exception e) {
              throw new RuntimeException("Cannot load descriptors: " + descriptorOuterClass + " is not a valid descriptor class name", e);
            } 
          }
        }singularType, defaultInstance, Extension.ExtensionType.MUTABLE);
  }
  
  public static class GeneratedExtension<ContainingType extends Message, Type> extends Extension<ContainingType, Type> {
    private GeneratedMessage.ExtensionDescriptorRetriever descriptorRetriever;
    
    private final Class singularType;
    
    private final Message messageDefaultInstance;
    
    private final Method enumValueOf;
    
    private final Method enumGetValueDescriptor;
    
    private final Extension.ExtensionType extensionType;
    
    GeneratedExtension(GeneratedMessage.ExtensionDescriptorRetriever descriptorRetriever, Class<?> singularType, Message messageDefaultInstance, Extension.ExtensionType extensionType) {
      if (Message.class.isAssignableFrom(singularType) && 
        !singularType.isInstance(messageDefaultInstance))
        throw new IllegalArgumentException("Bad messageDefaultInstance for " + singularType
            .getName()); 
      this.descriptorRetriever = descriptorRetriever;
      this.singularType = singularType;
      this.messageDefaultInstance = messageDefaultInstance;
      if (ProtocolMessageEnum.class.isAssignableFrom(singularType)) {
        this.enumValueOf = GeneratedMessage.getMethodOrDie(singularType, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
        this
          .enumGetValueDescriptor = GeneratedMessage.getMethodOrDie(singularType, "getValueDescriptor", new Class[0]);
      } else {
        this.enumValueOf = null;
        this.enumGetValueDescriptor = null;
      } 
      this.extensionType = extensionType;
    }
    
    public void internalInit(final Descriptors.FieldDescriptor descriptor) {
      if (this.descriptorRetriever != null)
        throw new IllegalStateException("Already initialized."); 
      this.descriptorRetriever = new GeneratedMessage.ExtensionDescriptorRetriever() {
          public Descriptors.FieldDescriptor getDescriptor() {
            return descriptor;
          }
        };
    }
    
    public Descriptors.FieldDescriptor getDescriptor() {
      if (this.descriptorRetriever == null)
        throw new IllegalStateException("getDescriptor() called before internalInit()"); 
      return this.descriptorRetriever.getDescriptor();
    }
    
    public Message getMessageDefaultInstance() {
      return this.messageDefaultInstance;
    }
    
    protected Extension.ExtensionType getExtensionType() {
      return this.extensionType;
    }
    
    protected Object fromReflectionType(Object value) {
      Descriptors.FieldDescriptor descriptor = getDescriptor();
      if (descriptor.isRepeated()) {
        if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE || descriptor
          .getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
          List<Object> result = new ArrayList();
          for (Object element : value)
            result.add(singularFromReflectionType(element)); 
          return result;
        } 
        return value;
      } 
      return singularFromReflectionType(value);
    }
    
    protected Object singularFromReflectionType(Object value) {
      Descriptors.FieldDescriptor descriptor = getDescriptor();
      switch (descriptor.getJavaType()) {
        case MESSAGE:
          if (this.singularType.isInstance(value))
            return value; 
          return this.messageDefaultInstance.newBuilderForType()
            .mergeFrom((Message)value).build();
        case ENUM:
          return GeneratedMessage.invokeOrDie(this.enumValueOf, null, new Object[] { value });
      } 
      return value;
    }
    
    protected Object toReflectionType(Object value) {
      Descriptors.FieldDescriptor descriptor = getDescriptor();
      if (descriptor.isRepeated()) {
        if (descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
          List<Object> result = new ArrayList();
          for (Object element : value)
            result.add(singularToReflectionType(element)); 
          return result;
        } 
        return value;
      } 
      return singularToReflectionType(value);
    }
    
    protected Object singularToReflectionType(Object value) {
      Descriptors.FieldDescriptor descriptor = getDescriptor();
      switch (descriptor.getJavaType()) {
        case ENUM:
          return GeneratedMessage.invokeOrDie(this.enumGetValueDescriptor, value, new Object[0]);
      } 
      return value;
    }
    
    public int getNumber() {
      return getDescriptor().getNumber();
    }
    
    public WireFormat.FieldType getLiteType() {
      return getDescriptor().getLiteType();
    }
    
    public boolean isRepeated() {
      return getDescriptor().isRepeated();
    }
    
    public Type getDefaultValue() {
      if (isRepeated())
        return (Type)Collections.emptyList(); 
      if (getDescriptor().getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE)
        return (Type)this.messageDefaultInstance; 
      return (Type)singularFromReflectionType(
          getDescriptor().getDefaultValue());
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
    
    public FieldAccessorTable(Descriptors.Descriptor descriptor, String[] camelCaseNames, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
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
    
    private boolean isMapFieldEnabled(Descriptors.FieldDescriptor field) {
      boolean result = true;
      return result;
    }
    
    public FieldAccessorTable ensureFieldAccessorsInitialized(Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
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
              if (field.isMapField() && isMapFieldEnabled(field)) {
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
          this.oneofs[j] = new OneofAccessor(this.descriptor, this.camelCaseNames[j + fieldsSize], messageClass, builderClass); 
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
      Object get(GeneratedMessage param2GeneratedMessage);
      
      Object get(GeneratedMessage.Builder param2Builder);
      
      Object getRaw(GeneratedMessage param2GeneratedMessage);
      
      Object getRaw(GeneratedMessage.Builder param2Builder);
      
      void set(GeneratedMessage.Builder param2Builder, Object param2Object);
      
      Object getRepeated(GeneratedMessage param2GeneratedMessage, int param2Int);
      
      Object getRepeated(GeneratedMessage.Builder param2Builder, int param2Int);
      
      Object getRepeatedRaw(GeneratedMessage param2GeneratedMessage, int param2Int);
      
      Object getRepeatedRaw(GeneratedMessage.Builder param2Builder, int param2Int);
      
      void setRepeated(GeneratedMessage.Builder param2Builder, int param2Int, Object param2Object);
      
      void addRepeated(GeneratedMessage.Builder param2Builder, Object param2Object);
      
      boolean has(GeneratedMessage param2GeneratedMessage);
      
      boolean has(GeneratedMessage.Builder param2Builder);
      
      int getRepeatedCount(GeneratedMessage param2GeneratedMessage);
      
      int getRepeatedCount(GeneratedMessage.Builder param2Builder);
      
      void clear(GeneratedMessage.Builder param2Builder);
      
      Message.Builder newBuilder();
      
      Message.Builder getBuilder(GeneratedMessage.Builder param2Builder);
      
      Message.Builder getRepeatedBuilder(GeneratedMessage.Builder param2Builder, int param2Int);
    }
    
    private static class OneofAccessor {
      private final Descriptors.Descriptor descriptor;
      
      private final Method caseMethod;
      
      private final Method caseMethodBuilder;
      
      private final Method clearMethod;
      
      OneofAccessor(Descriptors.Descriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
        this.descriptor = descriptor;
        this
          .caseMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Case", new Class[0]);
        this
          .caseMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Case", new Class[0]);
        this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
      }
      
      public boolean has(GeneratedMessage message) {
        if (((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber() == 0)
          return false; 
        return true;
      }
      
      public boolean has(GeneratedMessage.Builder builder) {
        if (((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber() == 0)
          return false; 
        return true;
      }
      
      public Descriptors.FieldDescriptor get(GeneratedMessage message) {
        int fieldNumber = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
        if (fieldNumber > 0)
          return this.descriptor.findFieldByNumber(fieldNumber); 
        return null;
      }
      
      public Descriptors.FieldDescriptor get(GeneratedMessage.Builder builder) {
        int fieldNumber = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
        if (fieldNumber > 0)
          return this.descriptor.findFieldByNumber(fieldNumber); 
        return null;
      }
      
      public void clear(GeneratedMessage.Builder builder) {
        GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]);
      }
    }
    
    private static boolean supportFieldPresence(Descriptors.FileDescriptor file) {
      return (file.getSyntax() == Descriptors.FileDescriptor.Syntax.PROTO2);
    }
    
    private static class SingularFieldAccessor implements FieldAccessor {
      protected final Class<?> type;
      
      protected final Method getMethod;
      
      protected final Method getMethodBuilder;
      
      protected final Method setMethod;
      
      protected final Method hasMethod;
      
      protected final Method hasMethodBuilder;
      
      protected final Method clearMethod;
      
      protected final Method caseMethod;
      
      protected final Method caseMethodBuilder;
      
      protected final Descriptors.FieldDescriptor field;
      
      protected final boolean isOneofField;
      
      protected final boolean hasHasMethod;
      
      SingularFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass, String containingOneofCamelCaseName) {
        this.field = descriptor;
        this.isOneofField = (descriptor.getContainingOneof() != null);
        this
          .hasHasMethod = (GeneratedMessage.FieldAccessorTable.supportFieldPresence(descriptor.getFile()) || (!this.isOneofField && descriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE));
        this.getMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[0]);
        this.getMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[0]);
        this.type = this.getMethod.getReturnType();
        this.setMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[] { this.type });
        this
          .hasMethod = this.hasHasMethod ? GeneratedMessage.getMethodOrDie(messageClass, "has" + camelCaseName, new Class[0]) : null;
        this
          .hasMethodBuilder = this.hasHasMethod ? GeneratedMessage.getMethodOrDie(builderClass, "has" + camelCaseName, new Class[0]) : null;
        this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
        this.caseMethod = this.isOneofField ? GeneratedMessage.getMethodOrDie(messageClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
        this.caseMethodBuilder = this.isOneofField ? GeneratedMessage.getMethodOrDie(builderClass, "get" + containingOneofCamelCaseName + "Case", new Class[0]) : null;
      }
      
      private int getOneofFieldNumber(GeneratedMessage message) {
        return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
      }
      
      private int getOneofFieldNumber(GeneratedMessage.Builder builder) {
        return ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
      }
      
      public Object get(GeneratedMessage message) {
        return GeneratedMessage.invokeOrDie(this.getMethod, message, new Object[0]);
      }
      
      public Object get(GeneratedMessage.Builder builder) {
        return GeneratedMessage.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
      }
      
      public Object getRaw(GeneratedMessage message) {
        return get(message);
      }
      
      public Object getRaw(GeneratedMessage.Builder builder) {
        return get(builder);
      }
      
      public void set(GeneratedMessage.Builder builder, Object value) {
        GeneratedMessage.invokeOrDie(this.setMethod, builder, new Object[] { value });
      }
      
      public Object getRepeated(GeneratedMessage message, int index) {
        throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
      }
      
      public Object getRepeatedRaw(GeneratedMessage message, int index) {
        throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
      }
      
      public Object getRepeated(GeneratedMessage.Builder builder, int index) {
        throw new UnsupportedOperationException("getRepeatedField() called on a singular field.");
      }
      
      public Object getRepeatedRaw(GeneratedMessage.Builder builder, int index) {
        throw new UnsupportedOperationException("getRepeatedFieldRaw() called on a singular field.");
      }
      
      public void setRepeated(GeneratedMessage.Builder builder, int index, Object value) {
        throw new UnsupportedOperationException("setRepeatedField() called on a singular field.");
      }
      
      public void addRepeated(GeneratedMessage.Builder builder, Object value) {
        throw new UnsupportedOperationException("addRepeatedField() called on a singular field.");
      }
      
      public boolean has(GeneratedMessage message) {
        if (!this.hasHasMethod) {
          if (this.isOneofField)
            return (getOneofFieldNumber(message) == this.field.getNumber()); 
          return !get(message).equals(this.field.getDefaultValue());
        } 
        return ((Boolean)GeneratedMessage.invokeOrDie(this.hasMethod, message, new Object[0])).booleanValue();
      }
      
      public boolean has(GeneratedMessage.Builder builder) {
        if (!this.hasHasMethod) {
          if (this.isOneofField)
            return (getOneofFieldNumber(builder) == this.field.getNumber()); 
          return !get(builder).equals(this.field.getDefaultValue());
        } 
        return ((Boolean)GeneratedMessage.invokeOrDie(this.hasMethodBuilder, builder, new Object[0])).booleanValue();
      }
      
      public int getRepeatedCount(GeneratedMessage message) {
        throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
      }
      
      public int getRepeatedCount(GeneratedMessage.Builder builder) {
        throw new UnsupportedOperationException("getRepeatedFieldSize() called on a singular field.");
      }
      
      public void clear(GeneratedMessage.Builder builder) {
        GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]);
      }
      
      public Message.Builder newBuilder() {
        throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
      }
      
      public Message.Builder getBuilder(GeneratedMessage.Builder builder) {
        throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
      }
      
      public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder builder, int index) {
        throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
      }
    }
    
    private static class RepeatedFieldAccessor implements FieldAccessor {
      protected final Class type;
      
      protected final Method getMethod;
      
      protected final Method getMethodBuilder;
      
      protected final Method getRepeatedMethod;
      
      protected final Method getRepeatedMethodBuilder;
      
      protected final Method setRepeatedMethod;
      
      protected final Method addRepeatedMethod;
      
      protected final Method getCountMethod;
      
      protected final Method getCountMethodBuilder;
      
      protected final Method clearMethod;
      
      RepeatedFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
        this.getMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "List", new Class[0]);
        this.getMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "List", new Class[0]);
        this
          .getRepeatedMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName, new Class[] { int.class });
        this
          .getRepeatedMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName, new Class[] { int.class });
        this.type = this.getRepeatedMethod.getReturnType();
        this
          .setRepeatedMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName, new Class[] { int.class, this.type });
        this
          .addRepeatedMethod = GeneratedMessage.getMethodOrDie(builderClass, "add" + camelCaseName, new Class[] { this.type });
        this
          .getCountMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Count", new Class[0]);
        this
          .getCountMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Count", new Class[0]);
        this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
      }
      
      public Object get(GeneratedMessage message) {
        return GeneratedMessage.invokeOrDie(this.getMethod, message, new Object[0]);
      }
      
      public Object get(GeneratedMessage.Builder builder) {
        return GeneratedMessage.invokeOrDie(this.getMethodBuilder, builder, new Object[0]);
      }
      
      public Object getRaw(GeneratedMessage message) {
        return get(message);
      }
      
      public Object getRaw(GeneratedMessage.Builder builder) {
        return get(builder);
      }
      
      public void set(GeneratedMessage.Builder builder, Object value) {
        clear(builder);
        for (Object element : value)
          addRepeated(builder, element); 
      }
      
      public Object getRepeated(GeneratedMessage message, int index) {
        return GeneratedMessage.invokeOrDie(this.getRepeatedMethod, message, new Object[] { Integer.valueOf(index) });
      }
      
      public Object getRepeated(GeneratedMessage.Builder builder, int index) {
        return GeneratedMessage.invokeOrDie(this.getRepeatedMethodBuilder, builder, new Object[] { Integer.valueOf(index) });
      }
      
      public Object getRepeatedRaw(GeneratedMessage message, int index) {
        return getRepeated(message, index);
      }
      
      public Object getRepeatedRaw(GeneratedMessage.Builder builder, int index) {
        return getRepeated(builder, index);
      }
      
      public void setRepeated(GeneratedMessage.Builder builder, int index, Object value) {
        GeneratedMessage.invokeOrDie(this.setRepeatedMethod, builder, new Object[] { Integer.valueOf(index), value });
      }
      
      public void addRepeated(GeneratedMessage.Builder builder, Object value) {
        GeneratedMessage.invokeOrDie(this.addRepeatedMethod, builder, new Object[] { value });
      }
      
      public boolean has(GeneratedMessage message) {
        throw new UnsupportedOperationException("hasField() called on a repeated field.");
      }
      
      public boolean has(GeneratedMessage.Builder builder) {
        throw new UnsupportedOperationException("hasField() called on a repeated field.");
      }
      
      public int getRepeatedCount(GeneratedMessage message) {
        return ((Integer)GeneratedMessage.invokeOrDie(this.getCountMethod, message, new Object[0])).intValue();
      }
      
      public int getRepeatedCount(GeneratedMessage.Builder builder) {
        return ((Integer)GeneratedMessage.invokeOrDie(this.getCountMethodBuilder, builder, new Object[0])).intValue();
      }
      
      public void clear(GeneratedMessage.Builder builder) {
        GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]);
      }
      
      public Message.Builder newBuilder() {
        throw new UnsupportedOperationException("newBuilderForField() called on a non-Message type.");
      }
      
      public Message.Builder getBuilder(GeneratedMessage.Builder builder) {
        throw new UnsupportedOperationException("getFieldBuilder() called on a non-Message type.");
      }
      
      public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder builder, int index) {
        throw new UnsupportedOperationException("getRepeatedFieldBuilder() called on a non-Message type.");
      }
    }
    
    private static class MapFieldAccessor implements FieldAccessor {
      private final Descriptors.FieldDescriptor field;
      
      private final Message mapEntryMessageDefaultInstance;
      
      MapFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
        this.field = descriptor;
        Method getDefaultInstanceMethod = GeneratedMessage.getMethodOrDie(messageClass, "getDefaultInstance", new Class[0]);
        MapField<?, ?> defaultMapField = getMapField(
            (GeneratedMessage)GeneratedMessage.invokeOrDie(getDefaultInstanceMethod, null, new Object[0]));
        this
          .mapEntryMessageDefaultInstance = defaultMapField.getMapEntryMessageDefaultInstance();
      }
      
      private MapField<?, ?> getMapField(GeneratedMessage message) {
        return message.internalGetMapField(this.field.getNumber());
      }
      
      private MapField<?, ?> getMapField(GeneratedMessage.Builder builder) {
        return builder.internalGetMapField(this.field.getNumber());
      }
      
      private MapField<?, ?> getMutableMapField(GeneratedMessage.Builder builder) {
        return builder.internalGetMutableMapField(this.field
            .getNumber());
      }
      
      public Object get(GeneratedMessage message) {
        List<Object> result = new ArrayList();
        for (int i = 0; i < getRepeatedCount(message); i++)
          result.add(getRepeated(message, i)); 
        return Collections.unmodifiableList(result);
      }
      
      public Object get(GeneratedMessage.Builder builder) {
        List<Object> result = new ArrayList();
        for (int i = 0; i < getRepeatedCount(builder); i++)
          result.add(getRepeated(builder, i)); 
        return Collections.unmodifiableList(result);
      }
      
      public Object getRaw(GeneratedMessage message) {
        return get(message);
      }
      
      public Object getRaw(GeneratedMessage.Builder builder) {
        return get(builder);
      }
      
      public void set(GeneratedMessage.Builder builder, Object value) {
        clear(builder);
        for (Object entry : value)
          addRepeated(builder, entry); 
      }
      
      public Object getRepeated(GeneratedMessage message, int index) {
        return getMapField(message).getList().get(index);
      }
      
      public Object getRepeated(GeneratedMessage.Builder builder, int index) {
        return getMapField(builder).getList().get(index);
      }
      
      public Object getRepeatedRaw(GeneratedMessage message, int index) {
        return getRepeated(message, index);
      }
      
      public Object getRepeatedRaw(GeneratedMessage.Builder builder, int index) {
        return getRepeated(builder, index);
      }
      
      public void setRepeated(GeneratedMessage.Builder builder, int index, Object value) {
        getMutableMapField(builder).getMutableList().set(index, (Message)value);
      }
      
      public void addRepeated(GeneratedMessage.Builder builder, Object value) {
        getMutableMapField(builder).getMutableList().add((Message)value);
      }
      
      public boolean has(GeneratedMessage message) {
        throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
      }
      
      public boolean has(GeneratedMessage.Builder builder) {
        throw new UnsupportedOperationException("hasField() is not supported for repeated fields.");
      }
      
      public int getRepeatedCount(GeneratedMessage message) {
        return getMapField(message).getList().size();
      }
      
      public int getRepeatedCount(GeneratedMessage.Builder builder) {
        return getMapField(builder).getList().size();
      }
      
      public void clear(GeneratedMessage.Builder builder) {
        getMutableMapField(builder).getMutableList().clear();
      }
      
      public Message.Builder newBuilder() {
        return this.mapEntryMessageDefaultInstance.newBuilderForType();
      }
      
      public Message.Builder getBuilder(GeneratedMessage.Builder builder) {
        throw new UnsupportedOperationException("Nested builder not supported for map fields.");
      }
      
      public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder builder, int index) {
        throw new UnsupportedOperationException("Nested builder not supported for map fields.");
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
      
      SingularEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass, String containingOneofCamelCaseName) {
        super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
        this.enumDescriptor = descriptor.getEnumType();
        this.valueOfMethod = GeneratedMessage.getMethodOrDie(this.type, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
        this
          .getValueDescriptorMethod = GeneratedMessage.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
        this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
        if (this.supportUnknownEnumValue) {
          this
            .getValueMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[0]);
          this
            .getValueMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[0]);
          this
            .setValueMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[] { int.class });
        } 
      }
      
      public Object get(GeneratedMessage message) {
        if (this.supportUnknownEnumValue) {
          int value = ((Integer)GeneratedMessage.invokeOrDie(this.getValueMethod, message, new Object[0])).intValue();
          return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
        } 
        return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.get(message), new Object[0]);
      }
      
      public Object get(GeneratedMessage.Builder builder) {
        if (this.supportUnknownEnumValue) {
          int value = ((Integer)GeneratedMessage.invokeOrDie(this.getValueMethodBuilder, builder, new Object[0])).intValue();
          return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
        } 
        return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super.get(builder), new Object[0]);
      }
      
      public void set(GeneratedMessage.Builder builder, Object value) {
        if (this.supportUnknownEnumValue) {
          GeneratedMessage.invokeOrDie(this.setValueMethod, builder, new Object[] { Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
          return;
        } 
        super.set(builder, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
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
      
      RepeatedEnumFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
        super(descriptor, camelCaseName, messageClass, builderClass);
        this.enumDescriptor = descriptor.getEnumType();
        this.valueOfMethod = GeneratedMessage.getMethodOrDie(this.type, "valueOf", new Class[] { Descriptors.EnumValueDescriptor.class });
        this
          .getValueDescriptorMethod = GeneratedMessage.getMethodOrDie(this.type, "getValueDescriptor", new Class[0]);
        this.supportUnknownEnumValue = descriptor.getFile().supportsUnknownEnumValue();
        if (this.supportUnknownEnumValue) {
          this
            .getRepeatedValueMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Value", new Class[] { int.class });
          this
            .getRepeatedValueMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Value", new Class[] { int.class });
          this
            .setRepeatedValueMethod = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName + "Value", new Class[] { int.class, int.class });
          this
            .addRepeatedValueMethod = GeneratedMessage.getMethodOrDie(builderClass, "add" + camelCaseName + "Value", new Class[] { int.class });
        } 
      }
      
      public Object get(GeneratedMessage message) {
        List<Object> newList = new ArrayList();
        int size = getRepeatedCount(message);
        for (int i = 0; i < size; i++)
          newList.add(getRepeated(message, i)); 
        return Collections.unmodifiableList(newList);
      }
      
      public Object get(GeneratedMessage.Builder builder) {
        List<Object> newList = new ArrayList();
        int size = getRepeatedCount(builder);
        for (int i = 0; i < size; i++)
          newList.add(getRepeated(builder, i)); 
        return Collections.unmodifiableList(newList);
      }
      
      public Object getRepeated(GeneratedMessage message, int index) {
        if (this.supportUnknownEnumValue) {
          int value = ((Integer)GeneratedMessage.invokeOrDie(this.getRepeatedValueMethod, message, new Object[] { Integer.valueOf(index) })).intValue();
          return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
        } 
        return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super
            .getRepeated(message, index), new Object[0]);
      }
      
      public Object getRepeated(GeneratedMessage.Builder builder, int index) {
        if (this.supportUnknownEnumValue) {
          int value = ((Integer)GeneratedMessage.invokeOrDie(this.getRepeatedValueMethodBuilder, builder, new Object[] { Integer.valueOf(index) })).intValue();
          return this.enumDescriptor.findValueByNumberCreatingIfUnknown(value);
        } 
        return GeneratedMessage.invokeOrDie(this.getValueDescriptorMethod, super
            .getRepeated(builder, index), new Object[0]);
      }
      
      public void setRepeated(GeneratedMessage.Builder builder, int index, Object value) {
        if (this.supportUnknownEnumValue) {
          GeneratedMessage.invokeOrDie(this.setRepeatedValueMethod, builder, new Object[] { Integer.valueOf(index), 
                Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
          return;
        } 
        super.setRepeated(builder, index, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
      }
      
      public void addRepeated(GeneratedMessage.Builder builder, Object value) {
        if (this.supportUnknownEnumValue) {
          GeneratedMessage.invokeOrDie(this.addRepeatedValueMethod, builder, new Object[] { Integer.valueOf(((Descriptors.EnumValueDescriptor)value).getNumber()) });
          return;
        } 
        super.addRepeated(builder, GeneratedMessage.invokeOrDie(this.valueOfMethod, null, new Object[] { value }));
      }
    }
    
    private static final class SingularStringFieldAccessor extends SingularFieldAccessor {
      private final Method getBytesMethod;
      
      private final Method getBytesMethodBuilder;
      
      private final Method setBytesMethodBuilder;
      
      SingularStringFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass, String containingOneofCamelCaseName) {
        super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
        this.getBytesMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Bytes", new Class[0]);
        this.getBytesMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Bytes", new Class[0]);
        this.setBytesMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "set" + camelCaseName + "Bytes", new Class[] { ByteString.class });
      }
      
      public Object getRaw(GeneratedMessage message) {
        return GeneratedMessage.invokeOrDie(this.getBytesMethod, message, new Object[0]);
      }
      
      public Object getRaw(GeneratedMessage.Builder builder) {
        return GeneratedMessage.invokeOrDie(this.getBytesMethodBuilder, builder, new Object[0]);
      }
      
      public void set(GeneratedMessage.Builder builder, Object value) {
        if (value instanceof ByteString) {
          GeneratedMessage.invokeOrDie(this.setBytesMethodBuilder, builder, new Object[] { value });
        } else {
          super.set(builder, value);
        } 
      }
    }
    
    private static final class SingularMessageFieldAccessor extends SingularFieldAccessor {
      private final Method newBuilderMethod;
      
      private final Method getBuilderMethodBuilder;
      
      SingularMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass, String containingOneofCamelCaseName) {
        super(descriptor, camelCaseName, messageClass, builderClass, containingOneofCamelCaseName);
        this.newBuilderMethod = GeneratedMessage.getMethodOrDie(this.type, "newBuilder", new Class[0]);
        this
          .getBuilderMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[0]);
      }
      
      private Object coerceType(Object value) {
        if (this.type.isInstance(value))
          return value; 
        return ((Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0]))
          .mergeFrom((Message)value).buildPartial();
      }
      
      public void set(GeneratedMessage.Builder builder, Object value) {
        super.set(builder, coerceType(value));
      }
      
      public Message.Builder newBuilder() {
        return (Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
      }
      
      public Message.Builder getBuilder(GeneratedMessage.Builder builder) {
        return (Message.Builder)GeneratedMessage.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[0]);
      }
    }
    
    private static final class RepeatedMessageFieldAccessor extends RepeatedFieldAccessor {
      private final Method newBuilderMethod;
      
      private final Method getBuilderMethodBuilder;
      
      RepeatedMessageFieldAccessor(Descriptors.FieldDescriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
        super(descriptor, camelCaseName, messageClass, builderClass);
        this.newBuilderMethod = GeneratedMessage.getMethodOrDie(this.type, "newBuilder", new Class[0]);
        this.getBuilderMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Builder", new Class[] { int.class });
      }
      
      private Object coerceType(Object value) {
        if (this.type.isInstance(value))
          return value; 
        return ((Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0]))
          .mergeFrom((Message)value).build();
      }
      
      public void setRepeated(GeneratedMessage.Builder builder, int index, Object value) {
        super.setRepeated(builder, index, coerceType(value));
      }
      
      public void addRepeated(GeneratedMessage.Builder builder, Object value) {
        super.addRepeated(builder, coerceType(value));
      }
      
      public Message.Builder newBuilder() {
        return (Message.Builder)GeneratedMessage.invokeOrDie(this.newBuilderMethod, null, new Object[0]);
      }
      
      public Message.Builder getRepeatedBuilder(GeneratedMessage.Builder builder, int index) {
        return (Message.Builder)GeneratedMessage.invokeOrDie(this.getBuilderMethodBuilder, builder, new Object[] { Integer.valueOf(index) });
      }
    }
  }
  
  private static class OneofAccessor {
    private final Descriptors.Descriptor descriptor;
    
    private final Method caseMethod;
    
    private final Method caseMethodBuilder;
    
    private final Method clearMethod;
    
    OneofAccessor(Descriptors.Descriptor descriptor, String camelCaseName, Class<? extends GeneratedMessage> messageClass, Class<? extends GeneratedMessage.Builder> builderClass) {
      this.descriptor = descriptor;
      this.caseMethod = GeneratedMessage.getMethodOrDie(messageClass, "get" + camelCaseName + "Case", new Class[0]);
      this.caseMethodBuilder = GeneratedMessage.getMethodOrDie(builderClass, "get" + camelCaseName + "Case", new Class[0]);
      this.clearMethod = GeneratedMessage.getMethodOrDie(builderClass, "clear" + camelCaseName, new Class[0]);
    }
    
    public boolean has(GeneratedMessage message) {
      if (((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber() == 0)
        return false; 
      return true;
    }
    
    public boolean has(GeneratedMessage.Builder builder) {
      if (((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber() == 0)
        return false; 
      return true;
    }
    
    public Descriptors.FieldDescriptor get(GeneratedMessage message) {
      int fieldNumber = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethod, message, new Object[0])).getNumber();
      if (fieldNumber > 0)
        return this.descriptor.findFieldByNumber(fieldNumber); 
      return null;
    }
    
    public Descriptors.FieldDescriptor get(GeneratedMessage.Builder builder) {
      int fieldNumber = ((Internal.EnumLite)GeneratedMessage.invokeOrDie(this.caseMethodBuilder, builder, new Object[0])).getNumber();
      if (fieldNumber > 0)
        return this.descriptor.findFieldByNumber(fieldNumber); 
      return null;
    }
    
    public void clear(GeneratedMessage.Builder builder) {
      GeneratedMessage.invokeOrDie(this.clearMethod, builder, new Object[0]);
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
    Object get(GeneratedMessage param1GeneratedMessage);
    
    Object get(GeneratedMessage.Builder param1Builder);
    
    Object getRaw(GeneratedMessage param1GeneratedMessage);
    
    Object getRaw(GeneratedMessage.Builder param1Builder);
    
    void set(GeneratedMessage.Builder param1Builder, Object param1Object);
    
    Object getRepeated(GeneratedMessage param1GeneratedMessage, int param1Int);
    
    Object getRepeated(GeneratedMessage.Builder param1Builder, int param1Int);
    
    Object getRepeatedRaw(GeneratedMessage param1GeneratedMessage, int param1Int);
    
    Object getRepeatedRaw(GeneratedMessage.Builder param1Builder, int param1Int);
    
    void setRepeated(GeneratedMessage.Builder param1Builder, int param1Int, Object param1Object);
    
    void addRepeated(GeneratedMessage.Builder param1Builder, Object param1Object);
    
    boolean has(GeneratedMessage param1GeneratedMessage);
    
    boolean has(GeneratedMessage.Builder param1Builder);
    
    int getRepeatedCount(GeneratedMessage param1GeneratedMessage);
    
    int getRepeatedCount(GeneratedMessage.Builder param1Builder);
    
    void clear(GeneratedMessage.Builder param1Builder);
    
    Message.Builder newBuilder();
    
    Message.Builder getBuilder(GeneratedMessage.Builder param1Builder);
    
    Message.Builder getRepeatedBuilder(GeneratedMessage.Builder param1Builder, int param1Int);
  }
}
