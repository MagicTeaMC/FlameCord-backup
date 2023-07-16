package com.google.protobuf;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

@CheckReturnValue
final class MessageSetSchema<T> implements Schema<T> {
  private final MessageLite defaultInstance;
  
  private final UnknownFieldSchema<?, ?> unknownFieldSchema;
  
  private final boolean hasExtensions;
  
  private final ExtensionSchema<?> extensionSchema;
  
  private MessageSetSchema(UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MessageLite defaultInstance) {
    this.unknownFieldSchema = unknownFieldSchema;
    this.hasExtensions = extensionSchema.hasExtensions(defaultInstance);
    this.extensionSchema = extensionSchema;
    this.defaultInstance = defaultInstance;
  }
  
  static <T> MessageSetSchema<T> newSchema(UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MessageLite defaultInstance) {
    return new MessageSetSchema<>(unknownFieldSchema, extensionSchema, defaultInstance);
  }
  
  public T newInstance() {
    if (this.defaultInstance instanceof GeneratedMessageLite)
      return (T)((GeneratedMessageLite)this.defaultInstance).newMutableInstance(); 
    return (T)this.defaultInstance.newBuilderForType().buildPartial();
  }
  
  public boolean equals(T message, T other) {
    Object messageUnknown = this.unknownFieldSchema.getFromMessage(message);
    Object otherUnknown = this.unknownFieldSchema.getFromMessage(other);
    if (!messageUnknown.equals(otherUnknown))
      return false; 
    if (this.hasExtensions) {
      FieldSet<?> messageExtensions = this.extensionSchema.getExtensions(message);
      FieldSet<?> otherExtensions = this.extensionSchema.getExtensions(other);
      return messageExtensions.equals(otherExtensions);
    } 
    return true;
  }
  
  public int hashCode(T message) {
    int hashCode = this.unknownFieldSchema.getFromMessage(message).hashCode();
    if (this.hasExtensions) {
      FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
      hashCode = hashCode * 53 + extensions.hashCode();
    } 
    return hashCode;
  }
  
  public void mergeFrom(T message, T other) {
    SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, message, other);
    if (this.hasExtensions)
      SchemaUtil.mergeExtensions(this.extensionSchema, message, other); 
  }
  
  public void writeTo(T message, Writer writer) throws IOException {
    FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
    Iterator<?> iterator = extensions.iterator();
    while (iterator.hasNext()) {
      Map.Entry<?, ?> extension = (Map.Entry<?, ?>)iterator.next();
      FieldSet.FieldDescriptorLite<?> fd = (FieldSet.FieldDescriptorLite)extension.getKey();
      if (fd.getLiteJavaType() != WireFormat.JavaType.MESSAGE || fd.isRepeated() || fd.isPacked())
        throw new IllegalStateException("Found invalid MessageSet item."); 
      if (extension instanceof LazyField.LazyEntry) {
        writer.writeMessageSetItem(fd
            .getNumber(), ((LazyField.LazyEntry)extension).getField().toByteString());
        continue;
      } 
      writer.writeMessageSetItem(fd.getNumber(), extension.getValue());
    } 
    writeUnknownFieldsHelper(this.unknownFieldSchema, message, writer);
  }
  
  private <UT, UB> void writeUnknownFieldsHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, T message, Writer writer) throws IOException {
    unknownFieldSchema.writeAsMessageSetTo(unknownFieldSchema.getFromMessage(message), writer);
  }
  
  public void mergeFrom(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
    UnknownFieldSetLite unknownFields = ((GeneratedMessageLite)message).unknownFields;
    if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
      unknownFields = UnknownFieldSetLite.newInstance();
      ((GeneratedMessageLite)message).unknownFields = unknownFields;
    } 
    FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
    GeneratedMessageLite.GeneratedExtension<?, ?> extension = null;
    while (position < limit) {
      position = ArrayDecoders.decodeVarint32(data, position, registers);
      int startTag = registers.int1;
      if (startTag != WireFormat.MESSAGE_SET_ITEM_TAG) {
        if (WireFormat.getTagWireType(startTag) == 2) {
          extension = (GeneratedMessageLite.GeneratedExtension<?, ?>)this.extensionSchema.findExtensionByNumber(registers.extensionRegistry, this.defaultInstance, 
              
              WireFormat.getTagFieldNumber(startTag));
          if (extension != null) {
            position = ArrayDecoders.decodeMessageField(
                Protobuf.getInstance().schemaFor(extension
                  .getMessageDefaultInstance().getClass()), data, position, limit, registers);
            extensions.setField(extension.descriptor, registers.object1);
            continue;
          } 
          position = ArrayDecoders.decodeUnknownField(startTag, data, position, limit, unknownFields, registers);
          continue;
        } 
        position = ArrayDecoders.skipField(startTag, data, position, limit, registers);
        continue;
      } 
      int typeId = 0;
      ByteString rawBytes = null;
      while (position < limit) {
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        int tag = registers.int1;
        int number = WireFormat.getTagFieldNumber(tag);
        int wireType = WireFormat.getTagWireType(tag);
        switch (number) {
          case 2:
            if (wireType == 0) {
              position = ArrayDecoders.decodeVarint32(data, position, registers);
              typeId = registers.int1;
              extension = (GeneratedMessageLite.GeneratedExtension<?, ?>)this.extensionSchema.findExtensionByNumber(registers.extensionRegistry, this.defaultInstance, typeId);
              continue;
            } 
            break;
          case 3:
            if (extension != null) {
              position = ArrayDecoders.decodeMessageField(
                  Protobuf.getInstance().schemaFor(extension
                    .getMessageDefaultInstance().getClass()), data, position, limit, registers);
              extensions.setField(extension.descriptor, registers.object1);
              continue;
            } 
            if (wireType == 2) {
              position = ArrayDecoders.decodeBytes(data, position, registers);
              rawBytes = (ByteString)registers.object1;
              continue;
            } 
            break;
        } 
        if (tag == WireFormat.MESSAGE_SET_ITEM_END_TAG)
          break; 
        position = ArrayDecoders.skipField(tag, data, position, limit, registers);
      } 
      if (rawBytes != null)
        unknownFields.storeField(
            WireFormat.makeTag(typeId, 2), rawBytes); 
    } 
    if (position != limit)
      throw InvalidProtocolBufferException.parseFailure(); 
  }
  
  public void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
    mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, message, reader, extensionRegistry);
  }
  
  private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, ExtensionSchema<ET> extensionSchema, T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
    UB unknownFields = unknownFieldSchema.getBuilderFromMessage(message);
    FieldSet<ET> extensions = extensionSchema.getMutableExtensions(message);
    try {
      while (true) {
        int number = reader.getFieldNumber();
        if (number == Integer.MAX_VALUE)
          return; 
        if (parseMessageSetItemOrUnknownField(reader, extensionRegistry, extensionSchema, extensions, unknownFieldSchema, unknownFields))
          continue; 
        break;
      } 
      return;
    } finally {
      unknownFieldSchema.setBuilderToMessage(message, unknownFields);
    } 
  }
  
  public void makeImmutable(T message) {
    this.unknownFieldSchema.makeImmutable(message);
    this.extensionSchema.makeImmutable(message);
  }
  
  private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> boolean parseMessageSetItemOrUnknownField(Reader reader, ExtensionRegistryLite extensionRegistry, ExtensionSchema<ET> extensionSchema, FieldSet<ET> extensions, UnknownFieldSchema<UT, UB> unknownFieldSchema, UB unknownFields) throws IOException {
    int startTag = reader.getTag();
    if (startTag != WireFormat.MESSAGE_SET_ITEM_TAG) {
      if (WireFormat.getTagWireType(startTag) == 2) {
        Object object = extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, 
            WireFormat.getTagFieldNumber(startTag));
        if (object != null) {
          extensionSchema.parseLengthPrefixedMessageSetItem(reader, object, extensionRegistry, extensions);
          return true;
        } 
        return unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader);
      } 
      return reader.skipField();
    } 
    int typeId = 0;
    ByteString rawBytes = null;
    Object extension = null;
    while (true) {
      int number = reader.getFieldNumber();
      if (number == Integer.MAX_VALUE)
        break; 
      int tag = reader.getTag();
      if (tag == WireFormat.MESSAGE_SET_TYPE_ID_TAG) {
        typeId = reader.readUInt32();
        extension = extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, typeId);
        continue;
      } 
      if (tag == WireFormat.MESSAGE_SET_MESSAGE_TAG) {
        if (extension != null) {
          extensionSchema.parseLengthPrefixedMessageSetItem(reader, extension, extensionRegistry, extensions);
          continue;
        } 
        rawBytes = reader.readBytes();
        continue;
      } 
      if (!reader.skipField())
        break; 
    } 
    if (reader.getTag() != WireFormat.MESSAGE_SET_ITEM_END_TAG)
      throw InvalidProtocolBufferException.invalidEndTag(); 
    if (rawBytes != null)
      if (extension != null) {
        extensionSchema.parseMessageSetItem(rawBytes, extension, extensionRegistry, extensions);
      } else {
        unknownFieldSchema.addLengthDelimited(unknownFields, typeId, rawBytes);
      }  
    return true;
  }
  
  public final boolean isInitialized(T message) {
    FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
    return extensions.isInitialized();
  }
  
  public int getSerializedSize(T message) {
    int size = 0;
    size += getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
    if (this.hasExtensions)
      size += this.extensionSchema.getExtensions(message).getMessageSetSerializedSize(); 
    return size;
  }
  
  private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> schema, T message) {
    UT unknowns = schema.getFromMessage(message);
    return schema.getSerializedSizeAsMessageSet(unknowns);
  }
}
