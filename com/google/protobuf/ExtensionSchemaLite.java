package com.google.protobuf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@CheckReturnValue
final class ExtensionSchemaLite extends ExtensionSchema<GeneratedMessageLite.ExtensionDescriptor> {
  boolean hasExtensions(MessageLite prototype) {
    return prototype instanceof GeneratedMessageLite.ExtendableMessage;
  }
  
  FieldSet<GeneratedMessageLite.ExtensionDescriptor> getExtensions(Object message) {
    return ((GeneratedMessageLite.ExtendableMessage)message).extensions;
  }
  
  void setExtensions(Object message, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) {
    ((GeneratedMessageLite.ExtendableMessage)message).extensions = extensions;
  }
  
  FieldSet<GeneratedMessageLite.ExtensionDescriptor> getMutableExtensions(Object message) {
    return ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
  }
  
  void makeImmutable(Object message) {
    getExtensions(message).makeImmutable();
  }
  
  <UT, UB> UB parseExtension(Object containerMessage, Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema) throws IOException {
    GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension<?, ?>)extensionObject;
    int fieldNumber = extension.getNumber();
    if (extension.descriptor.isRepeated() && extension.descriptor.isPacked()) {
      List<Float> list11;
      List<Long> list10;
      List<Integer> list9;
      List<Long> list8;
      List<Integer> list7;
      List<Boolean> list6;
      List<Integer> list5;
      List<Long> list4;
      List<Integer> list3;
      List<Long> list2;
      List<Integer> list1;
      List<Double> list22;
      List<Float> list21;
      List<Long> list20;
      List<Integer> list19;
      List<Long> list18;
      List<Integer> list17;
      List<Boolean> list16;
      List<Integer> list15;
      List<Long> list14;
      List<Integer> list13;
      List<Long> list12;
      List<Integer> list;
      Object<Double> value = null;
      switch (extension.getLiteType()) {
        case DOUBLE:
          list22 = new ArrayList<>();
          reader.readDoubleList(list22);
          value = (Object<Double>)list22;
          break;
        case FLOAT:
          list21 = new ArrayList<>();
          reader.readFloatList(list21);
          list11 = list21;
          break;
        case INT64:
          list20 = new ArrayList<>();
          reader.readInt64List(list20);
          list10 = list20;
          break;
        case UINT64:
          list20 = new ArrayList<>();
          reader.readUInt64List(list20);
          list10 = list20;
          break;
        case INT32:
          list19 = new ArrayList<>();
          reader.readInt32List(list19);
          list9 = list19;
          break;
        case FIXED64:
          list18 = new ArrayList<>();
          reader.readFixed64List(list18);
          list8 = list18;
          break;
        case FIXED32:
          list17 = new ArrayList<>();
          reader.readFixed32List(list17);
          list7 = list17;
          break;
        case BOOL:
          list16 = new ArrayList<>();
          reader.readBoolList(list16);
          list6 = list16;
          break;
        case UINT32:
          list15 = new ArrayList<>();
          reader.readUInt32List(list15);
          list5 = list15;
          break;
        case SFIXED32:
          list15 = new ArrayList<>();
          reader.readSFixed32List(list15);
          list5 = list15;
          break;
        case SFIXED64:
          list14 = new ArrayList<>();
          reader.readSFixed64List(list14);
          list4 = list14;
          break;
        case SINT32:
          list13 = new ArrayList<>();
          reader.readSInt32List(list13);
          list3 = list13;
          break;
        case SINT64:
          list12 = new ArrayList<>();
          reader.readSInt64List(list12);
          list2 = list12;
          break;
        case ENUM:
          list = new ArrayList<>();
          reader.readEnumList(list);
          unknownFields = SchemaUtil.filterUnknownEnumList(containerMessage, fieldNumber, list, extension.descriptor
              
              .getEnumType(), unknownFields, unknownFieldSchema);
          list1 = list;
          break;
        default:
          throw new IllegalStateException("Type cannot be packed: " + extension.descriptor
              .getLiteType());
      } 
      extensions.setField(extension.descriptor, list1);
    } else {
      Object value = null;
      if (extension.getLiteType() == WireFormat.FieldType.ENUM) {
        int number = reader.readInt32();
        Object enumValue = extension.descriptor.getEnumType().findValueByNumber(number);
        if (enumValue == null)
          return SchemaUtil.storeUnknownEnum(containerMessage, fieldNumber, number, unknownFields, unknownFieldSchema); 
        value = Integer.valueOf(number);
      } else {
        switch (extension.getLiteType()) {
          case DOUBLE:
            value = Double.valueOf(reader.readDouble());
            break;
          case FLOAT:
            value = Float.valueOf(reader.readFloat());
            break;
          case INT64:
            value = Long.valueOf(reader.readInt64());
            break;
          case UINT64:
            value = Long.valueOf(reader.readUInt64());
            break;
          case INT32:
            value = Integer.valueOf(reader.readInt32());
            break;
          case FIXED64:
            value = Long.valueOf(reader.readFixed64());
            break;
          case FIXED32:
            value = Integer.valueOf(reader.readFixed32());
            break;
          case BOOL:
            value = Boolean.valueOf(reader.readBool());
            break;
          case BYTES:
            value = reader.readBytes();
            break;
          case UINT32:
            value = Integer.valueOf(reader.readUInt32());
            break;
          case SFIXED32:
            value = Integer.valueOf(reader.readSFixed32());
            break;
          case SFIXED64:
            value = Long.valueOf(reader.readSFixed64());
            break;
          case SINT32:
            value = Integer.valueOf(reader.readSInt32());
            break;
          case SINT64:
            value = Long.valueOf(reader.readSInt64());
            break;
          case STRING:
            value = reader.readString();
            break;
          case GROUP:
            if (!extension.isRepeated()) {
              Object oldValue = extensions.getField(extension.descriptor);
              if (oldValue instanceof GeneratedMessageLite) {
                Schema<Object> extSchema = Protobuf.getInstance().schemaFor(oldValue);
                if (!((GeneratedMessageLite)oldValue).isMutable()) {
                  Object newValue = extSchema.newInstance();
                  extSchema.mergeFrom(newValue, oldValue);
                  extensions.setField(extension.descriptor, newValue);
                  oldValue = newValue;
                } 
                reader.mergeGroupField(oldValue, extSchema, extensionRegistry);
                return unknownFields;
              } 
            } 
            value = reader.readGroup(extension
                .getMessageDefaultInstance().getClass(), extensionRegistry);
            break;
          case MESSAGE:
            if (!extension.isRepeated()) {
              Object oldValue = extensions.getField(extension.descriptor);
              if (oldValue instanceof GeneratedMessageLite) {
                Schema<Object> extSchema = Protobuf.getInstance().schemaFor(oldValue);
                if (!((GeneratedMessageLite)oldValue).isMutable()) {
                  Object newValue = extSchema.newInstance();
                  extSchema.mergeFrom(newValue, oldValue);
                  extensions.setField(extension.descriptor, newValue);
                  oldValue = newValue;
                } 
                reader.mergeMessageField(oldValue, extSchema, extensionRegistry);
                return unknownFields;
              } 
            } 
            value = reader.readMessage(extension
                .getMessageDefaultInstance().getClass(), extensionRegistry);
            break;
          case ENUM:
            throw new IllegalStateException("Shouldn't reach here.");
        } 
      } 
      if (extension.isRepeated()) {
        extensions.addRepeatedField(extension.descriptor, value);
      } else {
        Object oldValue;
        switch (extension.getLiteType()) {
          case GROUP:
          case MESSAGE:
            oldValue = extensions.getField(extension.descriptor);
            if (oldValue != null)
              value = Internal.mergeMessage(oldValue, value); 
            break;
        } 
        extensions.setField(extension.descriptor, value);
      } 
    } 
    return unknownFields;
  }
  
  int extensionNumber(Map.Entry<?, ?> extension) {
    GeneratedMessageLite.ExtensionDescriptor descriptor = (GeneratedMessageLite.ExtensionDescriptor)extension.getKey();
    return descriptor.getNumber();
  }
  
  void serializeExtension(Writer writer, Map.Entry<?, ?> extension) throws IOException {
    GeneratedMessageLite.ExtensionDescriptor descriptor = (GeneratedMessageLite.ExtensionDescriptor)extension.getKey();
    if (descriptor.isRepeated()) {
      List<?> data;
      switch (descriptor.getLiteType()) {
        case DOUBLE:
          SchemaUtil.writeDoubleList(descriptor
              .getNumber(), (List<Double>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case FLOAT:
          SchemaUtil.writeFloatList(descriptor
              .getNumber(), (List<Float>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case INT64:
          SchemaUtil.writeInt64List(descriptor
              .getNumber(), (List<Long>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case UINT64:
          SchemaUtil.writeUInt64List(descriptor
              .getNumber(), (List<Long>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case INT32:
          SchemaUtil.writeInt32List(descriptor
              .getNumber(), (List<Integer>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case FIXED64:
          SchemaUtil.writeFixed64List(descriptor
              .getNumber(), (List<Long>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case FIXED32:
          SchemaUtil.writeFixed32List(descriptor
              .getNumber(), (List<Integer>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case BOOL:
          SchemaUtil.writeBoolList(descriptor
              .getNumber(), (List<Boolean>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case BYTES:
          SchemaUtil.writeBytesList(descriptor
              .getNumber(), (List<ByteString>)extension.getValue(), writer);
          break;
        case UINT32:
          SchemaUtil.writeUInt32List(descriptor
              .getNumber(), (List<Integer>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case SFIXED32:
          SchemaUtil.writeSFixed32List(descriptor
              .getNumber(), (List<Integer>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case SFIXED64:
          SchemaUtil.writeSFixed64List(descriptor
              .getNumber(), (List<Long>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case SINT32:
          SchemaUtil.writeSInt32List(descriptor
              .getNumber(), (List<Integer>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case SINT64:
          SchemaUtil.writeSInt64List(descriptor
              .getNumber(), (List<Long>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case ENUM:
          SchemaUtil.writeInt32List(descriptor
              .getNumber(), (List<Integer>)extension
              .getValue(), writer, descriptor
              
              .isPacked());
          break;
        case STRING:
          SchemaUtil.writeStringList(descriptor
              .getNumber(), (List<String>)extension.getValue(), writer);
          break;
        case GROUP:
          data = (List)extension.getValue();
          if (data != null && !data.isEmpty())
            SchemaUtil.writeGroupList(descriptor
                .getNumber(), (List)extension
                .getValue(), writer, 
                
                Protobuf.getInstance().schemaFor(data.get(0).getClass())); 
          break;
        case MESSAGE:
          data = (List)extension.getValue();
          if (data != null && !data.isEmpty())
            SchemaUtil.writeMessageList(descriptor
                .getNumber(), (List)extension
                .getValue(), writer, 
                
                Protobuf.getInstance().schemaFor(data.get(0).getClass())); 
          break;
      } 
    } else {
      switch (descriptor.getLiteType()) {
        case DOUBLE:
          writer.writeDouble(descriptor.getNumber(), ((Double)extension.getValue()).doubleValue());
          break;
        case FLOAT:
          writer.writeFloat(descriptor.getNumber(), ((Float)extension.getValue()).floatValue());
          break;
        case INT64:
          writer.writeInt64(descriptor.getNumber(), ((Long)extension.getValue()).longValue());
          break;
        case UINT64:
          writer.writeUInt64(descriptor.getNumber(), ((Long)extension.getValue()).longValue());
          break;
        case INT32:
          writer.writeInt32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
          break;
        case FIXED64:
          writer.writeFixed64(descriptor.getNumber(), ((Long)extension.getValue()).longValue());
          break;
        case FIXED32:
          writer.writeFixed32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
          break;
        case BOOL:
          writer.writeBool(descriptor.getNumber(), ((Boolean)extension.getValue()).booleanValue());
          break;
        case BYTES:
          writer.writeBytes(descriptor.getNumber(), (ByteString)extension.getValue());
          break;
        case UINT32:
          writer.writeUInt32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
          break;
        case SFIXED32:
          writer.writeSFixed32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
          break;
        case SFIXED64:
          writer.writeSFixed64(descriptor.getNumber(), ((Long)extension.getValue()).longValue());
          break;
        case SINT32:
          writer.writeSInt32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
          break;
        case SINT64:
          writer.writeSInt64(descriptor.getNumber(), ((Long)extension.getValue()).longValue());
          break;
        case ENUM:
          writer.writeInt32(descriptor.getNumber(), ((Integer)extension.getValue()).intValue());
          break;
        case STRING:
          writer.writeString(descriptor.getNumber(), (String)extension.getValue());
          break;
        case GROUP:
          writer.writeGroup(descriptor
              .getNumber(), extension
              .getValue(), 
              Protobuf.getInstance().schemaFor(extension.getValue().getClass()));
          break;
        case MESSAGE:
          writer.writeMessage(descriptor
              .getNumber(), extension
              .getValue(), 
              Protobuf.getInstance().schemaFor(extension.getValue().getClass()));
          break;
      } 
    } 
  }
  
  Object findExtensionByNumber(ExtensionRegistryLite extensionRegistry, MessageLite defaultInstance, int number) {
    return extensionRegistry.findLiteExtensionByNumber(defaultInstance, number);
  }
  
  void parseLengthPrefixedMessageSetItem(Reader reader, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) throws IOException {
    GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension<?, ?>)extensionObject;
    Object value = reader.readMessage(extension.getMessageDefaultInstance().getClass(), extensionRegistry);
    extensions.setField(extension.descriptor, value);
  }
  
  void parseMessageSetItem(ByteString data, Object extensionObject, ExtensionRegistryLite extensionRegistry, FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions) throws IOException {
    GeneratedMessageLite.GeneratedExtension<?, ?> extension = (GeneratedMessageLite.GeneratedExtension<?, ?>)extensionObject;
    MessageLite.Builder builder = extension.getMessageDefaultInstance().newBuilderForType();
    CodedInputStream input = data.newCodedInput();
    builder.mergeFrom(input, extensionRegistry);
    extensions.setField(extension.descriptor, builder.buildPartial());
    input.checkLastTagWas(0);
  }
}
