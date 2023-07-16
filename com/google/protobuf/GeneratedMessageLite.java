package com.google.protobuf;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class GeneratedMessageLite<MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends GeneratedMessageLite.Builder<MessageType, BuilderType>> extends AbstractMessageLite<MessageType, BuilderType> {
  static final int UNINITIALIZED_SERIALIZED_SIZE = 2147483647;
  
  private static final int MUTABLE_FLAG_MASK = -2147483648;
  
  private static final int MEMOIZED_SERIALIZED_SIZE_MASK = 2147483647;
  
  private int memoizedSerializedSize = -1;
  
  static final int UNINITIALIZED_HASH_CODE = 0;
  
  protected UnknownFieldSetLite unknownFields = UnknownFieldSetLite.getDefaultInstance();
  
  boolean isMutable() {
    return ((this.memoizedSerializedSize & Integer.MIN_VALUE) != 0);
  }
  
  void markImmutable() {
    this.memoizedSerializedSize &= Integer.MAX_VALUE;
  }
  
  int getMemoizedHashCode() {
    return this.memoizedHashCode;
  }
  
  void setMemoizedHashCode(int value) {
    this.memoizedHashCode = value;
  }
  
  void clearMemoizedHashCode() {
    this.memoizedHashCode = 0;
  }
  
  boolean hashCodeIsNotMemoized() {
    return (0 == getMemoizedHashCode());
  }
  
  public final Parser<MessageType> getParserForType() {
    return (Parser<MessageType>)dynamicMethod(MethodToInvoke.GET_PARSER);
  }
  
  public final MessageType getDefaultInstanceForType() {
    return (MessageType)dynamicMethod(MethodToInvoke.GET_DEFAULT_INSTANCE);
  }
  
  public final BuilderType newBuilderForType() {
    return (BuilderType)dynamicMethod(MethodToInvoke.NEW_BUILDER);
  }
  
  MessageType newMutableInstance() {
    return (MessageType)dynamicMethod(MethodToInvoke.NEW_MUTABLE_INSTANCE);
  }
  
  public String toString() {
    return MessageLiteToString.toString(this, super.toString());
  }
  
  public int hashCode() {
    if (isMutable())
      return computeHashCode(); 
    if (hashCodeIsNotMemoized())
      setMemoizedHashCode(computeHashCode()); 
    return getMemoizedHashCode();
  }
  
  int computeHashCode() {
    return Protobuf.getInstance().<GeneratedMessageLite<MessageType, BuilderType>>schemaFor(this).hashCode(this);
  }
  
  public boolean equals(Object other) {
    if (this == other)
      return true; 
    if (other == null)
      return false; 
    if (getClass() != other.getClass())
      return false; 
    return Protobuf.getInstance().<GeneratedMessageLite<MessageType, BuilderType>>schemaFor(this).equals(this, (GeneratedMessageLite<MessageType, BuilderType>)other);
  }
  
  private final void ensureUnknownFieldsInitialized() {
    if (this.unknownFields == UnknownFieldSetLite.getDefaultInstance())
      this.unknownFields = UnknownFieldSetLite.newInstance(); 
  }
  
  protected boolean parseUnknownField(int tag, CodedInputStream input) throws IOException {
    if (WireFormat.getTagWireType(tag) == 4)
      return false; 
    ensureUnknownFieldsInitialized();
    return this.unknownFields.mergeFieldFrom(tag, input);
  }
  
  protected void mergeVarintField(int tag, int value) {
    ensureUnknownFieldsInitialized();
    this.unknownFields.mergeVarintField(tag, value);
  }
  
  protected void mergeLengthDelimitedField(int fieldNumber, ByteString value) {
    ensureUnknownFieldsInitialized();
    this.unknownFields.mergeLengthDelimitedField(fieldNumber, value);
  }
  
  protected void makeImmutable() {
    Protobuf.getInstance().<GeneratedMessageLite<MessageType, BuilderType>>schemaFor(this).makeImmutable(this);
    markImmutable();
  }
  
  protected final <MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> BuilderType createBuilder() {
    return (BuilderType)dynamicMethod(MethodToInvoke.NEW_BUILDER);
  }
  
  protected final <MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> BuilderType createBuilder(MessageType prototype) {
    return createBuilder().mergeFrom(prototype);
  }
  
  public final boolean isInitialized() {
    return isInitialized(this, Boolean.TRUE.booleanValue());
  }
  
  public final BuilderType toBuilder() {
    Builder<GeneratedMessageLite<MessageType, BuilderType>, BuilderType> builder = (Builder)dynamicMethod(MethodToInvoke.NEW_BUILDER);
    return builder.mergeFrom(this);
  }
  
  public enum MethodToInvoke {
    GET_MEMOIZED_IS_INITIALIZED, SET_MEMOIZED_IS_INITIALIZED, BUILD_MESSAGE_INFO, NEW_MUTABLE_INSTANCE, NEW_BUILDER, GET_DEFAULT_INSTANCE, GET_PARSER;
  }
  
  @CanIgnoreReturnValue
  protected Object dynamicMethod(MethodToInvoke method, Object arg0) {
    return dynamicMethod(method, arg0, (Object)null);
  }
  
  protected Object dynamicMethod(MethodToInvoke method) {
    return dynamicMethod(method, (Object)null, (Object)null);
  }
  
  void clearMemoizedSerializedSize() {
    setMemoizedSerializedSize(2147483647);
  }
  
  int getMemoizedSerializedSize() {
    return this.memoizedSerializedSize & Integer.MAX_VALUE;
  }
  
  void setMemoizedSerializedSize(int size) {
    if (size < 0)
      throw new IllegalStateException("serialized size must be non-negative, was " + size); 
    this.memoizedSerializedSize = this.memoizedSerializedSize & Integer.MIN_VALUE | size & Integer.MAX_VALUE;
  }
  
  public void writeTo(CodedOutputStream output) throws IOException {
    Protobuf.getInstance()
      .<GeneratedMessageLite<MessageType, BuilderType>>schemaFor(this)
      .writeTo(this, CodedOutputStreamWriter.forCodedOutput(output));
  }
  
  int getSerializedSize(Schema<?> schema) {
    if (isMutable()) {
      int i = computeSerializedSize(schema);
      if (i < 0)
        throw new IllegalStateException("serialized size must be non-negative, was " + i); 
      return i;
    } 
    if (getMemoizedSerializedSize() != Integer.MAX_VALUE)
      return getMemoizedSerializedSize(); 
    int size = computeSerializedSize(schema);
    setMemoizedSerializedSize(size);
    return size;
  }
  
  public int getSerializedSize() {
    return getSerializedSize((Schema)null);
  }
  
  private int computeSerializedSize(Schema<?> nullableSchema) {
    if (nullableSchema == null)
      return Protobuf.getInstance().<GeneratedMessageLite<MessageType, BuilderType>>schemaFor(this).getSerializedSize(this); 
    return nullableSchema
      .getSerializedSize(this);
  }
  
  Object buildMessageInfo() throws Exception {
    return dynamicMethod(MethodToInvoke.BUILD_MESSAGE_INFO);
  }
  
  private static Map<Object, GeneratedMessageLite<?, ?>> defaultInstanceMap = new ConcurrentHashMap<>();
  
  static <T extends GeneratedMessageLite<?, ?>> T getDefaultInstance(Class<T> clazz) {
    GeneratedMessageLite<?, ?> generatedMessageLite = defaultInstanceMap.get(clazz);
    if (generatedMessageLite == null) {
      try {
        Class.forName(clazz.getName(), true, clazz.getClassLoader());
      } catch (ClassNotFoundException e) {
        throw new IllegalStateException("Class initialization cannot fail.", e);
      } 
      generatedMessageLite = defaultInstanceMap.get(clazz);
    } 
    if (generatedMessageLite == null) {
      generatedMessageLite = (GeneratedMessageLite<?, ?>)((GeneratedMessageLite)UnsafeUtil.<GeneratedMessageLite>allocateInstance(clazz)).getDefaultInstanceForType();
      if (generatedMessageLite == null)
        throw new IllegalStateException(); 
      defaultInstanceMap.put(clazz, generatedMessageLite);
    } 
    return (T)generatedMessageLite;
  }
  
  protected static <T extends GeneratedMessageLite<?, ?>> void registerDefaultInstance(Class<T> clazz, T defaultInstance) {
    defaultInstance.markImmutable();
    defaultInstanceMap.put(clazz, (GeneratedMessageLite<?, ?>)defaultInstance);
  }
  
  protected static Object newMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
    return new RawMessageInfo(defaultInstance, info, objects);
  }
  
  protected final void mergeUnknownFields(UnknownFieldSetLite unknownFields) {
    this.unknownFields = UnknownFieldSetLite.mutableCopyOf(this.unknownFields, unknownFields);
  }
  
  public static abstract class Builder<MessageType extends GeneratedMessageLite<MessageType, BuilderType>, BuilderType extends Builder<MessageType, BuilderType>> extends AbstractMessageLite.Builder<MessageType, BuilderType> {
    private final MessageType defaultInstance;
    
    protected MessageType instance;
    
    protected Builder(MessageType defaultInstance) {
      this.defaultInstance = defaultInstance;
      if (defaultInstance.isMutable())
        throw new IllegalArgumentException("Default instance must be immutable."); 
      this.instance = newMutableInstance();
    }
    
    private MessageType newMutableInstance() {
      return (MessageType)this.defaultInstance.newMutableInstance();
    }
    
    protected final void copyOnWrite() {
      if (!this.instance.isMutable())
        copyOnWriteInternal(); 
    }
    
    protected void copyOnWriteInternal() {
      MessageType newInstance = newMutableInstance();
      mergeFromInstance(newInstance, this.instance);
      this.instance = newInstance;
    }
    
    public final boolean isInitialized() {
      return GeneratedMessageLite.isInitialized(this.instance, false);
    }
    
    public final BuilderType clear() {
      if (this.defaultInstance.isMutable())
        throw new IllegalArgumentException("Default instance must be immutable."); 
      this.instance = newMutableInstance();
      return (BuilderType)this;
    }
    
    public BuilderType clone() {
      BuilderType builder = (BuilderType)getDefaultInstanceForType().newBuilderForType();
      ((Builder)builder).instance = buildPartial();
      return builder;
    }
    
    public MessageType buildPartial() {
      if (!this.instance.isMutable())
        return this.instance; 
      this.instance.makeImmutable();
      return this.instance;
    }
    
    public final MessageType build() {
      MessageType result = buildPartial();
      if (!result.isInitialized())
        throw newUninitializedMessageException(result); 
      return result;
    }
    
    protected BuilderType internalMergeFrom(MessageType message) {
      return mergeFrom(message);
    }
    
    public BuilderType mergeFrom(MessageType message) {
      if (getDefaultInstanceForType().equals(message))
        return (BuilderType)this; 
      copyOnWrite();
      mergeFromInstance(this.instance, message);
      return (BuilderType)this;
    }
    
    private static <MessageType> void mergeFromInstance(MessageType dest, MessageType src) {
      Protobuf.getInstance().<MessageType>schemaFor(dest).mergeFrom(dest, src);
    }
    
    public MessageType getDefaultInstanceForType() {
      return this.defaultInstance;
    }
    
    public BuilderType mergeFrom(byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      copyOnWrite();
      try {
        Protobuf.getInstance().<MessageType>schemaFor(this.instance).mergeFrom(this.instance, input, offset, offset + length, new ArrayDecoders.Registers(extensionRegistry));
      } catch (InvalidProtocolBufferException e) {
        throw e;
      } catch (IndexOutOfBoundsException e) {
        throw InvalidProtocolBufferException.truncatedMessage();
      } catch (IOException e) {
        throw new RuntimeException("Reading from byte array should not throw IOException.", e);
      } 
      return (BuilderType)this;
    }
    
    public BuilderType mergeFrom(byte[] input, int offset, int length) throws InvalidProtocolBufferException {
      return mergeFrom(input, offset, length, ExtensionRegistryLite.getEmptyRegistry());
    }
    
    public BuilderType mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      copyOnWrite();
      try {
        Protobuf.getInstance().<MessageType>schemaFor(this.instance).mergeFrom(this.instance, 
            CodedInputStreamReader.forCodedInput(input), extensionRegistry);
      } catch (RuntimeException e) {
        if (e.getCause() instanceof IOException)
          throw (IOException)e.getCause(); 
        throw e;
      } 
      return (BuilderType)this;
    }
  }
  
  public static abstract class ExtendableMessage<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>> extends GeneratedMessageLite<MessageType, BuilderType> implements ExtendableMessageOrBuilder<MessageType, BuilderType> {
    protected FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = FieldSet.emptySet();
    
    protected final void mergeExtensionFields(MessageType other) {
      if (this.extensions.isImmutable())
        this.extensions = this.extensions.clone(); 
      this.extensions.mergeFrom(((ExtendableMessage)other).extensions);
    }
    
    protected <MessageType extends MessageLite> boolean parseUnknownField(MessageType defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
      int fieldNumber = WireFormat.getTagFieldNumber(tag);
      GeneratedMessageLite.GeneratedExtension<MessageType, ?> extension = extensionRegistry.findLiteExtensionByNumber(defaultInstance, fieldNumber);
      return parseExtension(input, extensionRegistry, extension, tag, fieldNumber);
    }
    
    private boolean parseExtension(CodedInputStream input, ExtensionRegistryLite extensionRegistry, GeneratedMessageLite.GeneratedExtension<?, ?> extension, int tag, int fieldNumber) throws IOException {
      int wireType = WireFormat.getTagWireType(tag);
      boolean unknown = false;
      boolean packed = false;
      if (extension == null) {
        unknown = true;
      } else if (wireType == 
        FieldSet.getWireFormatForFieldType(extension.descriptor
          .getLiteType(), false)) {
        packed = false;
      } else if (extension.descriptor.isRepeated && extension.descriptor.type
        .isPackable() && wireType == 
        
        FieldSet.getWireFormatForFieldType(extension.descriptor
          .getLiteType(), true)) {
        packed = true;
      } else {
        unknown = true;
      } 
      if (unknown)
        return parseUnknownField(tag, input); 
      FieldSet<GeneratedMessageLite.ExtensionDescriptor> unused = ensureExtensionsAreMutable();
      if (packed) {
        int length = input.readRawVarint32();
        int limit = input.pushLimit(length);
        if (extension.descriptor.getLiteType() == WireFormat.FieldType.ENUM) {
          while (input.getBytesUntilLimit() > 0) {
            int rawValue = input.readEnum();
            Object value = extension.descriptor.getEnumType().findValueByNumber(rawValue);
            if (value == null)
              return true; 
            this.extensions.addRepeatedField(extension.descriptor, extension
                .singularToFieldSetType(value));
          } 
        } else {
          while (input.getBytesUntilLimit() > 0) {
            Object value = FieldSet.readPrimitiveField(input, extension.descriptor
                .getLiteType(), false);
            this.extensions.addRepeatedField(extension.descriptor, value);
          } 
        } 
        input.popLimit(limit);
      } else {
        Object value;
        MessageLite.Builder subBuilder;
        int rawValue;
        switch (extension.descriptor.getLiteJavaType()) {
          case MESSAGE:
            subBuilder = null;
            if (!extension.descriptor.isRepeated()) {
              MessageLite existingValue = (MessageLite)this.extensions.getField(extension.descriptor);
              if (existingValue != null)
                subBuilder = existingValue.toBuilder(); 
            } 
            if (subBuilder == null)
              subBuilder = extension.getMessageDefaultInstance().newBuilderForType(); 
            if (extension.descriptor.getLiteType() == WireFormat.FieldType.GROUP) {
              input.readGroup(extension.getNumber(), subBuilder, extensionRegistry);
            } else {
              input.readMessage(subBuilder, extensionRegistry);
            } 
            value = subBuilder.build();
            break;
          case ENUM:
            rawValue = input.readEnum();
            value = extension.descriptor.getEnumType().findValueByNumber(rawValue);
            if (value == null) {
              mergeVarintField(fieldNumber, rawValue);
              return true;
            } 
            break;
          default:
            value = FieldSet.readPrimitiveField(input, extension.descriptor
                .getLiteType(), false);
            break;
        } 
        if (extension.descriptor.isRepeated()) {
          this.extensions.addRepeatedField(extension.descriptor, extension
              .singularToFieldSetType(value));
        } else {
          this.extensions.setField(extension.descriptor, extension.singularToFieldSetType(value));
        } 
      } 
      return true;
    }
    
    protected <MessageType extends MessageLite> boolean parseUnknownFieldAsMessageSet(MessageType defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry, int tag) throws IOException {
      if (tag == WireFormat.MESSAGE_SET_ITEM_TAG) {
        mergeMessageSetExtensionFromCodedStream(defaultInstance, input, extensionRegistry);
        return true;
      } 
      int wireType = WireFormat.getTagWireType(tag);
      if (wireType == 2)
        return parseUnknownField(defaultInstance, input, extensionRegistry, tag); 
      return input.skipField(tag);
    }
    
    private <MessageType extends MessageLite> void mergeMessageSetExtensionFromCodedStream(MessageType defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
      int typeId = 0;
      ByteString rawBytes = null;
      GeneratedMessageLite.GeneratedExtension<?, ?> extension = null;
      while (true) {
        int tag = input.readTag();
        if (tag == 0)
          break; 
        if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
          typeId = input.readUInt32();
          if (typeId != 0)
            extension = extensionRegistry.findLiteExtensionByNumber(defaultInstance, typeId); 
          continue;
        } 
        if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
          if (typeId != 0 && 
            extension != null) {
            eagerlyMergeMessageSetExtension(input, extension, extensionRegistry, typeId);
            rawBytes = null;
            continue;
          } 
          rawBytes = input.readBytes();
          continue;
        } 
        if (!input.skipField(tag))
          break; 
      } 
      input.checkLastTagWas(WireFormat.MESSAGE_SET_ITEM_END_TAG);
      if (rawBytes != null && typeId != 0)
        if (extension != null) {
          mergeMessageSetExtensionFromBytes(rawBytes, extensionRegistry, extension);
        } else if (rawBytes != null) {
          mergeLengthDelimitedField(typeId, rawBytes);
        }  
    }
    
    private void eagerlyMergeMessageSetExtension(CodedInputStream input, GeneratedMessageLite.GeneratedExtension<?, ?> extension, ExtensionRegistryLite extensionRegistry, int typeId) throws IOException {
      int fieldNumber = typeId;
      int tag = WireFormat.makeTag(typeId, 2);
      boolean unused = parseExtension(input, extensionRegistry, extension, tag, fieldNumber);
    }
    
    private void mergeMessageSetExtensionFromBytes(ByteString rawBytes, ExtensionRegistryLite extensionRegistry, GeneratedMessageLite.GeneratedExtension<?, ?> extension) throws IOException {
      MessageLite.Builder subBuilder = null;
      MessageLite existingValue = (MessageLite)this.extensions.getField(extension.descriptor);
      if (existingValue != null)
        subBuilder = existingValue.toBuilder(); 
      if (subBuilder == null)
        subBuilder = extension.getMessageDefaultInstance().newBuilderForType(); 
      subBuilder.mergeFrom(rawBytes, extensionRegistry);
      MessageLite value = subBuilder.build();
      ensureExtensionsAreMutable()
        .setField(extension.descriptor, extension.singularToFieldSetType(value));
    }
    
    @CanIgnoreReturnValue
    FieldSet<GeneratedMessageLite.ExtensionDescriptor> ensureExtensionsAreMutable() {
      if (this.extensions.isImmutable())
        this.extensions = this.extensions.clone(); 
      return this.extensions;
    }
    
    private void verifyExtensionContainingType(GeneratedMessageLite.GeneratedExtension<MessageType, ?> extension) {
      if (extension.getContainingTypeDefaultInstance() != getDefaultInstanceForType())
        throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings."); 
    }
    
    public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extension) {
      GeneratedMessageLite.GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
      verifyExtensionContainingType(extensionLite);
      return this.extensions.hasField(extensionLite.descriptor);
    }
    
    public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extension) {
      GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = (GeneratedMessageLite.GeneratedExtension)GeneratedMessageLite.checkIsLite((ExtensionLite)extension);
      verifyExtensionContainingType(extensionLite);
      return this.extensions.getRepeatedFieldCount(extensionLite.descriptor);
    }
    
    public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extension) {
      GeneratedMessageLite.GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
      verifyExtensionContainingType(extensionLite);
      Object value = this.extensions.getField(extensionLite.descriptor);
      if (value == null)
        return extensionLite.defaultValue; 
      return (Type)extensionLite.fromFieldSetType(value);
    }
    
    public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extension, int index) {
      GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = (GeneratedMessageLite.GeneratedExtension)GeneratedMessageLite.checkIsLite((ExtensionLite)extension);
      verifyExtensionContainingType(extensionLite);
      return (Type)extensionLite
        .singularFromFieldSetType(this.extensions
          .getRepeatedField(extensionLite.descriptor, index));
    }
    
    protected boolean extensionsAreInitialized() {
      return this.extensions.isInitialized();
    }
    
    protected class ExtensionWriter {
      private final Iterator<Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object>> iter = GeneratedMessageLite.ExtendableMessage.this.extensions.iterator();
      
      private Map.Entry<GeneratedMessageLite.ExtensionDescriptor, Object> next;
      
      private final boolean messageSetWireFormat;
      
      private ExtensionWriter(boolean messageSetWireFormat) {
        if (this.iter.hasNext())
          this.next = this.iter.next(); 
        this.messageSetWireFormat = messageSetWireFormat;
      }
      
      public void writeUntil(int end, CodedOutputStream output) throws IOException {
        while (this.next != null && ((GeneratedMessageLite.ExtensionDescriptor)this.next.getKey()).getNumber() < end) {
          GeneratedMessageLite.ExtensionDescriptor extension = this.next.getKey();
          if (this.messageSetWireFormat && extension
            .getLiteJavaType() == WireFormat.JavaType.MESSAGE && 
            !extension.isRepeated()) {
            output.writeMessageSetExtension(extension.getNumber(), (MessageLite)this.next.getValue());
          } else {
            FieldSet.writeField(extension, this.next.getValue(), output);
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
  }
  
  public static abstract class ExtendableBuilder<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>> extends Builder<MessageType, BuilderType> implements ExtendableMessageOrBuilder<MessageType, BuilderType> {
    protected ExtendableBuilder(MessageType defaultInstance) {
      super(defaultInstance);
    }
    
    void internalSetExtensionSet(FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) {
      copyOnWrite();
      ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions = extensions;
    }
    
    protected void copyOnWriteInternal() {
      super.copyOnWriteInternal();
      if (((GeneratedMessageLite.ExtendableMessage)this.instance).extensions != FieldSet.emptySet())
        ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions = ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions.clone(); 
    }
    
    private FieldSet<GeneratedMessageLite.ExtensionDescriptor> ensureExtensionsAreMutable() {
      FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions;
      if (extensions.isImmutable()) {
        extensions = extensions.clone();
        ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions = extensions;
      } 
      return extensions;
    }
    
    public final MessageType buildPartial() {
      if (!((GeneratedMessageLite.ExtendableMessage)this.instance).isMutable())
        return this.instance; 
      ((GeneratedMessageLite.ExtendableMessage)this.instance).extensions.makeImmutable();
      return super.buildPartial();
    }
    
    private void verifyExtensionContainingType(GeneratedMessageLite.GeneratedExtension<MessageType, ?> extension) {
      if (extension.getContainingTypeDefaultInstance() != getDefaultInstanceForType())
        throw new IllegalArgumentException("This extension is for a different message type.  Please make sure that you are not suppressing any generics type warnings."); 
    }
    
    public final <Type> boolean hasExtension(ExtensionLite<MessageType, Type> extension) {
      return ((GeneratedMessageLite.ExtendableMessage)this.instance).hasExtension(extension);
    }
    
    public final <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> extension) {
      return ((GeneratedMessageLite.ExtendableMessage)this.instance).getExtensionCount(extension);
    }
    
    public final <Type> Type getExtension(ExtensionLite<MessageType, Type> extension) {
      return (Type)((GeneratedMessageLite.ExtendableMessage)this.instance).getExtension(extension);
    }
    
    public final <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> extension, int index) {
      return (Type)((GeneratedMessageLite.ExtendableMessage)this.instance).getExtension(extension, index);
    }
    
    public final <Type> BuilderType setExtension(ExtensionLite<MessageType, Type> extension, Type value) {
      GeneratedMessageLite.GeneratedExtension<MessageType, Type> extensionLite = GeneratedMessageLite.checkIsLite(extension);
      verifyExtensionContainingType(extensionLite);
      copyOnWrite();
      ensureExtensionsAreMutable()
        .setField(extensionLite.descriptor, extensionLite.toFieldSetType(value));
      return (BuilderType)this;
    }
    
    public final <Type> BuilderType setExtension(ExtensionLite<MessageType, List<Type>> extension, int index, Type value) {
      GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = (GeneratedMessageLite.GeneratedExtension)GeneratedMessageLite.checkIsLite((ExtensionLite)extension);
      verifyExtensionContainingType(extensionLite);
      copyOnWrite();
      ensureExtensionsAreMutable()
        .setRepeatedField(extensionLite.descriptor, index, extensionLite
          .singularToFieldSetType(value));
      return (BuilderType)this;
    }
    
    public final <Type> BuilderType addExtension(ExtensionLite<MessageType, List<Type>> extension, Type value) {
      GeneratedMessageLite.GeneratedExtension<MessageType, List<Type>> extensionLite = (GeneratedMessageLite.GeneratedExtension)GeneratedMessageLite.checkIsLite((ExtensionLite)extension);
      verifyExtensionContainingType(extensionLite);
      copyOnWrite();
      ensureExtensionsAreMutable()
        .addRepeatedField(extensionLite.descriptor, extensionLite.singularToFieldSetType(value));
      return (BuilderType)this;
    }
    
    public final BuilderType clearExtension(ExtensionLite<MessageType, ?> extension) {
      GeneratedMessageLite.GeneratedExtension<MessageType, ?> extensionLite = GeneratedMessageLite.checkIsLite((ExtensionLite)extension);
      verifyExtensionContainingType(extensionLite);
      copyOnWrite();
      ensureExtensionsAreMutable().clearField(extensionLite.descriptor);
      return (BuilderType)this;
    }
  }
  
  public static <ContainingType extends MessageLite, Type> GeneratedExtension<ContainingType, Type> newSingularGeneratedExtension(ContainingType containingTypeDefaultInstance, Type defaultValue, MessageLite messageDefaultInstance, Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, Class singularType) {
    return new GeneratedExtension<>(containingTypeDefaultInstance, defaultValue, messageDefaultInstance, new ExtensionDescriptor(enumTypeMap, number, type, false, false), singularType);
  }
  
  public static <ContainingType extends MessageLite, Type> GeneratedExtension<ContainingType, Type> newRepeatedGeneratedExtension(ContainingType containingTypeDefaultInstance, MessageLite messageDefaultInstance, Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, boolean isPacked, Class singularType) {
    List<?> list = Collections.emptyList();
    return new GeneratedExtension<>(containingTypeDefaultInstance, (Type)list, messageDefaultInstance, new ExtensionDescriptor(enumTypeMap, number, type, true, isPacked), singularType);
  }
  
  static final class ExtensionDescriptor implements FieldSet.FieldDescriptorLite<ExtensionDescriptor> {
    final Internal.EnumLiteMap<?> enumTypeMap;
    
    final int number;
    
    final WireFormat.FieldType type;
    
    final boolean isRepeated;
    
    final boolean isPacked;
    
    ExtensionDescriptor(Internal.EnumLiteMap<?> enumTypeMap, int number, WireFormat.FieldType type, boolean isRepeated, boolean isPacked) {
      this.enumTypeMap = enumTypeMap;
      this.number = number;
      this.type = type;
      this.isRepeated = isRepeated;
      this.isPacked = isPacked;
    }
    
    public int getNumber() {
      return this.number;
    }
    
    public WireFormat.FieldType getLiteType() {
      return this.type;
    }
    
    public WireFormat.JavaType getLiteJavaType() {
      return this.type.getJavaType();
    }
    
    public boolean isRepeated() {
      return this.isRepeated;
    }
    
    public boolean isPacked() {
      return this.isPacked;
    }
    
    public Internal.EnumLiteMap<?> getEnumType() {
      return this.enumTypeMap;
    }
    
    public MessageLite.Builder internalMergeFrom(MessageLite.Builder to, MessageLite from) {
      return ((GeneratedMessageLite.Builder<GeneratedMessageLite, MessageLite.Builder>)to).mergeFrom((GeneratedMessageLite)from);
    }
    
    public int compareTo(ExtensionDescriptor other) {
      return this.number - other.number;
    }
  }
  
  static Method getMethodOrDie(Class clazz, String name, Class... params) {
    try {
      return clazz.getMethod(name, params);
    } catch (NoSuchMethodException e) {
      throw new RuntimeException("Generated message class \"" + clazz
          .getName() + "\" missing method \"" + name + "\".", e);
    } 
  }
  
  static Object invokeOrDie(Method method, Object object, Object... params) {
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
  
  public static class GeneratedExtension<ContainingType extends MessageLite, Type> extends ExtensionLite<ContainingType, Type> {
    final ContainingType containingTypeDefaultInstance;
    
    final Type defaultValue;
    
    final MessageLite messageDefaultInstance;
    
    final GeneratedMessageLite.ExtensionDescriptor descriptor;
    
    GeneratedExtension(ContainingType containingTypeDefaultInstance, Type defaultValue, MessageLite messageDefaultInstance, GeneratedMessageLite.ExtensionDescriptor descriptor, Class singularType) {
      if (containingTypeDefaultInstance == null)
        throw new IllegalArgumentException("Null containingTypeDefaultInstance"); 
      if (descriptor.getLiteType() == WireFormat.FieldType.MESSAGE && messageDefaultInstance == null)
        throw new IllegalArgumentException("Null messageDefaultInstance"); 
      this.containingTypeDefaultInstance = containingTypeDefaultInstance;
      this.defaultValue = defaultValue;
      this.messageDefaultInstance = messageDefaultInstance;
      this.descriptor = descriptor;
    }
    
    public ContainingType getContainingTypeDefaultInstance() {
      return this.containingTypeDefaultInstance;
    }
    
    public int getNumber() {
      return this.descriptor.getNumber();
    }
    
    public MessageLite getMessageDefaultInstance() {
      return this.messageDefaultInstance;
    }
    
    Object fromFieldSetType(Object value) {
      if (this.descriptor.isRepeated()) {
        if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
          List<Object> result = new ArrayList();
          for (Object element : value)
            result.add(singularFromFieldSetType(element)); 
          return result;
        } 
        return value;
      } 
      return singularFromFieldSetType(value);
    }
    
    Object singularFromFieldSetType(Object value) {
      if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM)
        return this.descriptor.enumTypeMap.findValueByNumber(((Integer)value).intValue()); 
      return value;
    }
    
    Object toFieldSetType(Object value) {
      if (this.descriptor.isRepeated()) {
        if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM) {
          List<Object> result = new ArrayList();
          for (Object element : value)
            result.add(singularToFieldSetType(element)); 
          return result;
        } 
        return value;
      } 
      return singularToFieldSetType(value);
    }
    
    Object singularToFieldSetType(Object value) {
      if (this.descriptor.getLiteJavaType() == WireFormat.JavaType.ENUM)
        return Integer.valueOf(((Internal.EnumLite)value).getNumber()); 
      return value;
    }
    
    public WireFormat.FieldType getLiteType() {
      return this.descriptor.getLiteType();
    }
    
    public boolean isRepeated() {
      return this.descriptor.isRepeated;
    }
    
    public Type getDefaultValue() {
      return this.defaultValue;
    }
  }
  
  protected static final class SerializedForm implements Serializable {
    private static final long serialVersionUID = 0L;
    
    private final Class<?> messageClass;
    
    private final String messageClassName;
    
    private final byte[] asBytes;
    
    public static SerializedForm of(MessageLite message) {
      return new SerializedForm(message);
    }
    
    SerializedForm(MessageLite regularForm) {
      this.messageClass = regularForm.getClass();
      this.messageClassName = this.messageClass.getName();
      this.asBytes = regularForm.toByteArray();
    }
    
    protected Object readResolve() throws ObjectStreamException {
      try {
        Class<?> messageClass = resolveMessageClass();
        Field defaultInstanceField = messageClass.getDeclaredField("DEFAULT_INSTANCE");
        defaultInstanceField.setAccessible(true);
        MessageLite defaultInstance = (MessageLite)defaultInstanceField.get(null);
        return defaultInstance.newBuilderForType().mergeFrom(this.asBytes).buildPartial();
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("Unable to find proto buffer class: " + this.messageClassName, e);
      } catch (NoSuchFieldException e) {
        return readResolveFallback();
      } catch (SecurityException e) {
        throw new RuntimeException("Unable to call DEFAULT_INSTANCE in " + this.messageClassName, e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Unable to call parsePartialFrom", e);
      } catch (InvalidProtocolBufferException e) {
        throw new RuntimeException("Unable to understand proto buffer", e);
      } 
    }
    
    @Deprecated
    private Object readResolveFallback() throws ObjectStreamException {
      try {
        Class<?> messageClass = resolveMessageClass();
        Field defaultInstanceField = messageClass.getDeclaredField("defaultInstance");
        defaultInstanceField.setAccessible(true);
        MessageLite defaultInstance = (MessageLite)defaultInstanceField.get(null);
        return defaultInstance.newBuilderForType()
          .mergeFrom(this.asBytes)
          .buildPartial();
      } catch (ClassNotFoundException e) {
        throw new RuntimeException("Unable to find proto buffer class: " + this.messageClassName, e);
      } catch (NoSuchFieldException e) {
        throw new RuntimeException("Unable to find defaultInstance in " + this.messageClassName, e);
      } catch (SecurityException e) {
        throw new RuntimeException("Unable to call defaultInstance in " + this.messageClassName, e);
      } catch (IllegalAccessException e) {
        throw new RuntimeException("Unable to call parsePartialFrom", e);
      } catch (InvalidProtocolBufferException e) {
        throw new RuntimeException("Unable to understand proto buffer", e);
      } 
    }
    
    private Class<?> resolveMessageClass() throws ClassNotFoundException {
      return (this.messageClass != null) ? this.messageClass : Class.forName(this.messageClassName);
    }
  }
  
  private static <MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>, T> GeneratedExtension<MessageType, T> checkIsLite(ExtensionLite<MessageType, T> extension) {
    if (!extension.isLite())
      throw new IllegalArgumentException("Expected a lite extension."); 
    return (GeneratedExtension<MessageType, T>)extension;
  }
  
  protected static final <T extends GeneratedMessageLite<T, ?>> boolean isInitialized(T message, boolean shouldMemoize) {
    byte memoizedIsInitialized = ((Byte)message.dynamicMethod(MethodToInvoke.GET_MEMOIZED_IS_INITIALIZED)).byteValue();
    if (memoizedIsInitialized == 1)
      return true; 
    if (memoizedIsInitialized == 0)
      return false; 
    boolean isInitialized = Protobuf.getInstance().<T>schemaFor(message).isInitialized(message);
    if (shouldMemoize)
      Object object = message.dynamicMethod(MethodToInvoke.SET_MEMOIZED_IS_INITIALIZED, isInitialized ? message : null); 
    return isInitialized;
  }
  
  protected static Internal.IntList emptyIntList() {
    return IntArrayList.emptyList();
  }
  
  protected static Internal.IntList mutableCopy(Internal.IntList list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  protected static Internal.LongList emptyLongList() {
    return LongArrayList.emptyList();
  }
  
  protected static Internal.LongList mutableCopy(Internal.LongList list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  protected static Internal.FloatList emptyFloatList() {
    return FloatArrayList.emptyList();
  }
  
  protected static Internal.FloatList mutableCopy(Internal.FloatList list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  protected static Internal.DoubleList emptyDoubleList() {
    return DoubleArrayList.emptyList();
  }
  
  protected static Internal.DoubleList mutableCopy(Internal.DoubleList list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  protected static Internal.BooleanList emptyBooleanList() {
    return BooleanArrayList.emptyList();
  }
  
  protected static Internal.BooleanList mutableCopy(Internal.BooleanList list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  protected static <E> Internal.ProtobufList<E> emptyProtobufList() {
    return ProtobufArrayList.emptyList();
  }
  
  protected static <E> Internal.ProtobufList<E> mutableCopy(Internal.ProtobufList<E> list) {
    int size = list.size();
    return list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
  }
  
  protected static class DefaultInstanceBasedParser<T extends GeneratedMessageLite<T, ?>> extends AbstractParser<T> {
    private final T defaultInstance;
    
    public DefaultInstanceBasedParser(T defaultInstance) {
      this.defaultInstance = defaultInstance;
    }
    
    public T parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return GeneratedMessageLite.parsePartialFrom(this.defaultInstance, input, extensionRegistry);
    }
    
    public T parsePartialFrom(byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
      return GeneratedMessageLite.parsePartialFrom(this.defaultInstance, input, offset, length, extensionRegistry);
    }
  }
  
  static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T instance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    T result = (T)instance.newMutableInstance();
    try {
      Schema<T> schema = Protobuf.getInstance().schemaFor(result);
      schema.mergeFrom(result, CodedInputStreamReader.forCodedInput(input), extensionRegistry);
      schema.makeImmutable(result);
    } catch (InvalidProtocolBufferException e) {
      if (e.getThrownFromInputStream())
        e = new InvalidProtocolBufferException(e); 
      throw e.setUnfinishedMessage(result);
    } catch (UninitializedMessageException e) {
      throw e.asInvalidProtocolBufferException().setUnfinishedMessage(result);
    } catch (IOException e) {
      if (e.getCause() instanceof InvalidProtocolBufferException)
        throw (InvalidProtocolBufferException)e.getCause(); 
      throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(result);
    } catch (RuntimeException e) {
      if (e.getCause() instanceof InvalidProtocolBufferException)
        throw (InvalidProtocolBufferException)e.getCause(); 
      throw e;
    } 
    return result;
  }
  
  private static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T instance, byte[] input, int offset, int length, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    T result = (T)instance.newMutableInstance();
    try {
      Schema<T> schema = Protobuf.getInstance().schemaFor(result);
      schema.mergeFrom(result, input, offset, offset + length, new ArrayDecoders.Registers(extensionRegistry));
      schema.makeImmutable(result);
    } catch (InvalidProtocolBufferException e) {
      if (e.getThrownFromInputStream())
        e = new InvalidProtocolBufferException(e); 
      throw e.setUnfinishedMessage(result);
    } catch (UninitializedMessageException e) {
      throw e.asInvalidProtocolBufferException().setUnfinishedMessage(result);
    } catch (IOException e) {
      if (e.getCause() instanceof InvalidProtocolBufferException)
        throw (InvalidProtocolBufferException)e.getCause(); 
      throw (new InvalidProtocolBufferException(e)).setUnfinishedMessage(result);
    } catch (IndexOutOfBoundsException e) {
      throw InvalidProtocolBufferException.truncatedMessage().setUnfinishedMessage(result);
    } 
    return result;
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, CodedInputStream input) throws InvalidProtocolBufferException {
    return parsePartialFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry());
  }
  
  private static <T extends GeneratedMessageLite<T, ?>> T checkMessageInitialized(T message) throws InvalidProtocolBufferException {
    if (message != null && !message.isInitialized())
      throw message
        .newUninitializedMessageException()
        .asInvalidProtocolBufferException()
        .setUnfinishedMessage(message); 
    return message;
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return checkMessageInitialized(
        parseFrom(defaultInstance, CodedInputStream.newInstance(data), extensionRegistry));
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteBuffer data) throws InvalidProtocolBufferException {
    return parseFrom(defaultInstance, data, ExtensionRegistryLite.getEmptyRegistry());
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteString data) throws InvalidProtocolBufferException {
    return checkMessageInitialized(
        parseFrom(defaultInstance, data, ExtensionRegistryLite.getEmptyRegistry()));
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return checkMessageInitialized(parsePartialFrom(defaultInstance, data, extensionRegistry));
  }
  
  private static <T extends GeneratedMessageLite<T, ?>> T parsePartialFrom(T defaultInstance, ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    CodedInputStream input = data.newCodedInput();
    T message = parsePartialFrom(defaultInstance, input, extensionRegistry);
    try {
      input.checkLastTagWas(0);
    } catch (InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(message);
    } 
    return message;
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, byte[] data) throws InvalidProtocolBufferException {
    return checkMessageInitialized(parsePartialFrom(defaultInstance, data, 0, data.length, 
          ExtensionRegistryLite.getEmptyRegistry()));
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return checkMessageInitialized(
        parsePartialFrom(defaultInstance, data, 0, data.length, extensionRegistry));
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, InputStream input) throws InvalidProtocolBufferException {
    return checkMessageInitialized(
        parsePartialFrom(defaultInstance, 
          
          CodedInputStream.newInstance(input), 
          ExtensionRegistryLite.getEmptyRegistry()));
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return checkMessageInitialized(
        parsePartialFrom(defaultInstance, CodedInputStream.newInstance(input), extensionRegistry));
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, CodedInputStream input) throws InvalidProtocolBufferException {
    return parseFrom(defaultInstance, input, ExtensionRegistryLite.getEmptyRegistry());
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseFrom(T defaultInstance, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return checkMessageInitialized(parsePartialFrom(defaultInstance, input, extensionRegistry));
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseDelimitedFrom(T defaultInstance, InputStream input) throws InvalidProtocolBufferException {
    return checkMessageInitialized(
        parsePartialDelimitedFrom(defaultInstance, input, 
          ExtensionRegistryLite.getEmptyRegistry()));
  }
  
  protected static <T extends GeneratedMessageLite<T, ?>> T parseDelimitedFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    return checkMessageInitialized(
        parsePartialDelimitedFrom(defaultInstance, input, extensionRegistry));
  }
  
  private static <T extends GeneratedMessageLite<T, ?>> T parsePartialDelimitedFrom(T defaultInstance, InputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
    int size;
    try {
      int firstByte = input.read();
      if (firstByte == -1)
        return null; 
      size = CodedInputStream.readRawVarint32(firstByte, input);
    } catch (InvalidProtocolBufferException e) {
      if (e.getThrownFromInputStream())
        e = new InvalidProtocolBufferException(e); 
      throw e;
    } catch (IOException e) {
      throw new InvalidProtocolBufferException(e);
    } 
    InputStream limitedInput = new AbstractMessageLite.Builder.LimitedInputStream(input, size);
    CodedInputStream codedInput = CodedInputStream.newInstance(limitedInput);
    T message = parsePartialFrom(defaultInstance, codedInput, extensionRegistry);
    try {
      codedInput.checkLastTagWas(0);
    } catch (InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(message);
    } 
    return message;
  }
  
  protected abstract Object dynamicMethod(MethodToInvoke paramMethodToInvoke, Object paramObject1, Object paramObject2);
  
  public static interface ExtendableMessageOrBuilder<MessageType extends ExtendableMessage<MessageType, BuilderType>, BuilderType extends ExtendableBuilder<MessageType, BuilderType>> extends MessageLiteOrBuilder {
    <Type> boolean hasExtension(ExtensionLite<MessageType, Type> param1ExtensionLite);
    
    <Type> int getExtensionCount(ExtensionLite<MessageType, List<Type>> param1ExtensionLite);
    
    <Type> Type getExtension(ExtensionLite<MessageType, Type> param1ExtensionLite);
    
    <Type> Type getExtension(ExtensionLite<MessageType, List<Type>> param1ExtensionLite, int param1Int);
  }
}
