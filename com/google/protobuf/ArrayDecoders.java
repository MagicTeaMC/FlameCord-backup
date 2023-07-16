package com.google.protobuf;

import java.io.IOException;

@CheckReturnValue
final class ArrayDecoders {
  static final class Registers {
    public int int1;
    
    public long long1;
    
    public Object object1;
    
    public final ExtensionRegistryLite extensionRegistry;
    
    Registers() {
      this.extensionRegistry = ExtensionRegistryLite.getEmptyRegistry();
    }
    
    Registers(ExtensionRegistryLite extensionRegistry) {
      if (extensionRegistry == null)
        throw new NullPointerException(); 
      this.extensionRegistry = extensionRegistry;
    }
  }
  
  static int decodeVarint32(byte[] data, int position, Registers registers) {
    int value = data[position++];
    if (value >= 0) {
      registers.int1 = value;
      return position;
    } 
    return decodeVarint32(value, data, position, registers);
  }
  
  static int decodeVarint32(int firstByte, byte[] data, int position, Registers registers) {
    int value = firstByte & 0x7F;
    byte b2 = data[position++];
    if (b2 >= 0) {
      registers.int1 = value | b2 << 7;
      return position;
    } 
    value |= (b2 & Byte.MAX_VALUE) << 7;
    byte b3 = data[position++];
    if (b3 >= 0) {
      registers.int1 = value | b3 << 14;
      return position;
    } 
    value |= (b3 & Byte.MAX_VALUE) << 14;
    byte b4 = data[position++];
    if (b4 >= 0) {
      registers.int1 = value | b4 << 21;
      return position;
    } 
    value |= (b4 & Byte.MAX_VALUE) << 21;
    byte b5 = data[position++];
    if (b5 >= 0) {
      registers.int1 = value | b5 << 28;
      return position;
    } 
    value |= (b5 & Byte.MAX_VALUE) << 28;
    while (data[position++] < 0);
    registers.int1 = value;
    return position;
  }
  
  static int decodeVarint64(byte[] data, int position, Registers registers) {
    long value = data[position++];
    if (value >= 0L) {
      registers.long1 = value;
      return position;
    } 
    return decodeVarint64(value, data, position, registers);
  }
  
  static int decodeVarint64(long firstByte, byte[] data, int position, Registers registers) {
    long value = firstByte & 0x7FL;
    byte next = data[position++];
    int shift = 7;
    value |= (next & Byte.MAX_VALUE) << 7L;
    while (next < 0) {
      next = data[position++];
      shift += 7;
      value |= (next & Byte.MAX_VALUE) << shift;
    } 
    registers.long1 = value;
    return position;
  }
  
  static int decodeFixed32(byte[] data, int position) {
    return data[position] & 0xFF | (data[position + 1] & 0xFF) << 8 | (data[position + 2] & 0xFF) << 16 | (data[position + 3] & 0xFF) << 24;
  }
  
  static long decodeFixed64(byte[] data, int position) {
    return data[position] & 0xFFL | (data[position + 1] & 0xFFL) << 8L | (data[position + 2] & 0xFFL) << 16L | (data[position + 3] & 0xFFL) << 24L | (data[position + 4] & 0xFFL) << 32L | (data[position + 5] & 0xFFL) << 40L | (data[position + 6] & 0xFFL) << 48L | (data[position + 7] & 0xFFL) << 56L;
  }
  
  static double decodeDouble(byte[] data, int position) {
    return Double.longBitsToDouble(decodeFixed64(data, position));
  }
  
  static float decodeFloat(byte[] data, int position) {
    return Float.intBitsToFloat(decodeFixed32(data, position));
  }
  
  static int decodeString(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
    position = decodeVarint32(data, position, registers);
    int length = registers.int1;
    if (length < 0)
      throw InvalidProtocolBufferException.negativeSize(); 
    if (length == 0) {
      registers.object1 = "";
      return position;
    } 
    registers.object1 = new String(data, position, length, Internal.UTF_8);
    return position + length;
  }
  
  static int decodeStringRequireUtf8(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
    position = decodeVarint32(data, position, registers);
    int length = registers.int1;
    if (length < 0)
      throw InvalidProtocolBufferException.negativeSize(); 
    if (length == 0) {
      registers.object1 = "";
      return position;
    } 
    registers.object1 = Utf8.decodeUtf8(data, position, length);
    return position + length;
  }
  
  static int decodeBytes(byte[] data, int position, Registers registers) throws InvalidProtocolBufferException {
    position = decodeVarint32(data, position, registers);
    int length = registers.int1;
    if (length < 0)
      throw InvalidProtocolBufferException.negativeSize(); 
    if (length > data.length - position)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    if (length == 0) {
      registers.object1 = ByteString.EMPTY;
      return position;
    } 
    registers.object1 = ByteString.copyFrom(data, position, length);
    return position + length;
  }
  
  static int decodeMessageField(Schema<Object> schema, byte[] data, int position, int limit, Registers registers) throws IOException {
    Object msg = schema.newInstance();
    int offset = mergeMessageField(msg, schema, data, position, limit, registers);
    schema.makeImmutable(msg);
    registers.object1 = msg;
    return offset;
  }
  
  static int decodeGroupField(Schema<Object> schema, byte[] data, int position, int limit, int endGroup, Registers registers) throws IOException {
    Object msg = schema.newInstance();
    int offset = mergeGroupField(msg, schema, data, position, limit, endGroup, registers);
    schema.makeImmutable(msg);
    registers.object1 = msg;
    return offset;
  }
  
  static int mergeMessageField(Object msg, Schema<Object> schema, byte[] data, int position, int limit, Registers registers) throws IOException {
    int length = data[position++];
    if (length < 0) {
      position = decodeVarint32(length, data, position, registers);
      length = registers.int1;
    } 
    if (length < 0 || length > limit - position)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    schema.mergeFrom(msg, data, position, position + length, registers);
    registers.object1 = msg;
    return position + length;
  }
  
  static int mergeGroupField(Object msg, Schema schema, byte[] data, int position, int limit, int endGroup, Registers registers) throws IOException {
    MessageSchema<Object> messageSchema = (MessageSchema)schema;
    int endPosition = messageSchema.parseProto2Message(msg, data, position, limit, endGroup, registers);
    registers.object1 = msg;
    return endPosition;
  }
  
  static int decodeVarint32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
    IntArrayList output = (IntArrayList)list;
    position = decodeVarint32(data, position, registers);
    output.addInt(registers.int1);
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      position = decodeVarint32(data, nextPosition, registers);
      output.addInt(registers.int1);
    } 
    return position;
  }
  
  static int decodeVarint64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
    LongArrayList output = (LongArrayList)list;
    position = decodeVarint64(data, position, registers);
    output.addLong(registers.long1);
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      position = decodeVarint64(data, nextPosition, registers);
      output.addLong(registers.long1);
    } 
    return position;
  }
  
  static int decodeFixed32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
    IntArrayList output = (IntArrayList)list;
    output.addInt(decodeFixed32(data, position));
    position += 4;
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      output.addInt(decodeFixed32(data, nextPosition));
      position = nextPosition + 4;
    } 
    return position;
  }
  
  static int decodeFixed64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
    LongArrayList output = (LongArrayList)list;
    output.addLong(decodeFixed64(data, position));
    position += 8;
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      output.addLong(decodeFixed64(data, nextPosition));
      position = nextPosition + 8;
    } 
    return position;
  }
  
  static int decodeFloatList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
    FloatArrayList output = (FloatArrayList)list;
    output.addFloat(decodeFloat(data, position));
    position += 4;
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      output.addFloat(decodeFloat(data, nextPosition));
      position = nextPosition + 4;
    } 
    return position;
  }
  
  static int decodeDoubleList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
    DoubleArrayList output = (DoubleArrayList)list;
    output.addDouble(decodeDouble(data, position));
    position += 8;
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      output.addDouble(decodeDouble(data, nextPosition));
      position = nextPosition + 8;
    } 
    return position;
  }
  
  static int decodeBoolList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
    BooleanArrayList output = (BooleanArrayList)list;
    position = decodeVarint64(data, position, registers);
    output.addBoolean((registers.long1 != 0L));
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      position = decodeVarint64(data, nextPosition, registers);
      output.addBoolean((registers.long1 != 0L));
    } 
    return position;
  }
  
  static int decodeSInt32List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
    IntArrayList output = (IntArrayList)list;
    position = decodeVarint32(data, position, registers);
    output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      position = decodeVarint32(data, nextPosition, registers);
      output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
    } 
    return position;
  }
  
  static int decodeSInt64List(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) {
    LongArrayList output = (LongArrayList)list;
    position = decodeVarint64(data, position, registers);
    output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      position = decodeVarint64(data, nextPosition, registers);
      output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
    } 
    return position;
  }
  
  static int decodePackedVarint32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    IntArrayList output = (IntArrayList)list;
    position = decodeVarint32(data, position, registers);
    int fieldLimit = position + registers.int1;
    while (position < fieldLimit) {
      position = decodeVarint32(data, position, registers);
      output.addInt(registers.int1);
    } 
    if (position != fieldLimit)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    return position;
  }
  
  static int decodePackedVarint64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    LongArrayList output = (LongArrayList)list;
    position = decodeVarint32(data, position, registers);
    int fieldLimit = position + registers.int1;
    while (position < fieldLimit) {
      position = decodeVarint64(data, position, registers);
      output.addLong(registers.long1);
    } 
    if (position != fieldLimit)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    return position;
  }
  
  static int decodePackedFixed32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    IntArrayList output = (IntArrayList)list;
    position = decodeVarint32(data, position, registers);
    int fieldLimit = position + registers.int1;
    while (position < fieldLimit) {
      output.addInt(decodeFixed32(data, position));
      position += 4;
    } 
    if (position != fieldLimit)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    return position;
  }
  
  static int decodePackedFixed64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    LongArrayList output = (LongArrayList)list;
    position = decodeVarint32(data, position, registers);
    int fieldLimit = position + registers.int1;
    while (position < fieldLimit) {
      output.addLong(decodeFixed64(data, position));
      position += 8;
    } 
    if (position != fieldLimit)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    return position;
  }
  
  static int decodePackedFloatList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    FloatArrayList output = (FloatArrayList)list;
    position = decodeVarint32(data, position, registers);
    int fieldLimit = position + registers.int1;
    while (position < fieldLimit) {
      output.addFloat(decodeFloat(data, position));
      position += 4;
    } 
    if (position != fieldLimit)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    return position;
  }
  
  static int decodePackedDoubleList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    DoubleArrayList output = (DoubleArrayList)list;
    position = decodeVarint32(data, position, registers);
    int fieldLimit = position + registers.int1;
    while (position < fieldLimit) {
      output.addDouble(decodeDouble(data, position));
      position += 8;
    } 
    if (position != fieldLimit)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    return position;
  }
  
  static int decodePackedBoolList(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    BooleanArrayList output = (BooleanArrayList)list;
    position = decodeVarint32(data, position, registers);
    int fieldLimit = position + registers.int1;
    while (position < fieldLimit) {
      position = decodeVarint64(data, position, registers);
      output.addBoolean((registers.long1 != 0L));
    } 
    if (position != fieldLimit)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    return position;
  }
  
  static int decodePackedSInt32List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    IntArrayList output = (IntArrayList)list;
    position = decodeVarint32(data, position, registers);
    int fieldLimit = position + registers.int1;
    while (position < fieldLimit) {
      position = decodeVarint32(data, position, registers);
      output.addInt(CodedInputStream.decodeZigZag32(registers.int1));
    } 
    if (position != fieldLimit)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    return position;
  }
  
  static int decodePackedSInt64List(byte[] data, int position, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    LongArrayList output = (LongArrayList)list;
    position = decodeVarint32(data, position, registers);
    int fieldLimit = position + registers.int1;
    while (position < fieldLimit) {
      position = decodeVarint64(data, position, registers);
      output.addLong(CodedInputStream.decodeZigZag64(registers.long1));
    } 
    if (position != fieldLimit)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    return position;
  }
  
  static int decodeStringList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
    Internal.ProtobufList<String> output = (Internal.ProtobufList)list;
    position = decodeVarint32(data, position, registers);
    int length = registers.int1;
    if (length < 0)
      throw InvalidProtocolBufferException.negativeSize(); 
    if (length == 0) {
      output.add("");
    } else {
      String value = new String(data, position, length, Internal.UTF_8);
      output.add(value);
      position += length;
    } 
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      position = decodeVarint32(data, nextPosition, registers);
      int nextLength = registers.int1;
      if (nextLength < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      if (nextLength == 0) {
        output.add("");
        continue;
      } 
      String value = new String(data, position, nextLength, Internal.UTF_8);
      output.add(value);
      position += nextLength;
    } 
    return position;
  }
  
  static int decodeStringListRequireUtf8(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
    Internal.ProtobufList<String> output = (Internal.ProtobufList)list;
    position = decodeVarint32(data, position, registers);
    int length = registers.int1;
    if (length < 0)
      throw InvalidProtocolBufferException.negativeSize(); 
    if (length == 0) {
      output.add("");
    } else {
      if (!Utf8.isValidUtf8(data, position, position + length))
        throw InvalidProtocolBufferException.invalidUtf8(); 
      String value = new String(data, position, length, Internal.UTF_8);
      output.add(value);
      position += length;
    } 
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      position = decodeVarint32(data, nextPosition, registers);
      int nextLength = registers.int1;
      if (nextLength < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      if (nextLength == 0) {
        output.add("");
        continue;
      } 
      if (!Utf8.isValidUtf8(data, position, position + nextLength))
        throw InvalidProtocolBufferException.invalidUtf8(); 
      String value = new String(data, position, nextLength, Internal.UTF_8);
      output.add(value);
      position += nextLength;
    } 
    return position;
  }
  
  static int decodeBytesList(int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws InvalidProtocolBufferException {
    Internal.ProtobufList<ByteString> output = (Internal.ProtobufList)list;
    position = decodeVarint32(data, position, registers);
    int length = registers.int1;
    if (length < 0)
      throw InvalidProtocolBufferException.negativeSize(); 
    if (length > data.length - position)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    if (length == 0) {
      output.add(ByteString.EMPTY);
    } else {
      output.add(ByteString.copyFrom(data, position, length));
      position += length;
    } 
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      position = decodeVarint32(data, nextPosition, registers);
      int nextLength = registers.int1;
      if (nextLength < 0)
        throw InvalidProtocolBufferException.negativeSize(); 
      if (nextLength > data.length - position)
        throw InvalidProtocolBufferException.truncatedMessage(); 
      if (nextLength == 0) {
        output.add(ByteString.EMPTY);
        continue;
      } 
      output.add(ByteString.copyFrom(data, position, nextLength));
      position += nextLength;
    } 
    return position;
  }
  
  static int decodeMessageList(Schema<?> schema, int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    Internal.ProtobufList<Object> output = (Internal.ProtobufList)list;
    position = decodeMessageField(schema, data, position, limit, registers);
    output.add(registers.object1);
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      position = decodeMessageField(schema, data, nextPosition, limit, registers);
      output.add(registers.object1);
    } 
    return position;
  }
  
  static int decodeGroupList(Schema schema, int tag, byte[] data, int position, int limit, Internal.ProtobufList<?> list, Registers registers) throws IOException {
    Internal.ProtobufList<Object> output = (Internal.ProtobufList)list;
    int endgroup = tag & 0xFFFFFFF8 | 0x4;
    position = decodeGroupField(schema, data, position, limit, endgroup, registers);
    output.add(registers.object1);
    while (position < limit) {
      int nextPosition = decodeVarint32(data, position, registers);
      if (tag != registers.int1)
        break; 
      position = decodeGroupField(schema, data, nextPosition, limit, endgroup, registers);
      output.add(registers.object1);
    } 
    return position;
  }
  
  static int decodeExtensionOrUnknownField(int tag, byte[] data, int position, int limit, Object message, MessageLite defaultInstance, UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> unknownFieldSchema, Registers registers) throws IOException {
    int number = tag >>> 3;
    GeneratedMessageLite.GeneratedExtension<MessageLite, ?> extension = registers.extensionRegistry.findLiteExtensionByNumber(defaultInstance, number);
    if (extension == null)
      return decodeUnknownField(tag, data, position, limit, 
          MessageSchema.getMutableUnknownFields(message), registers); 
    FieldSet<GeneratedMessageLite.ExtensionDescriptor> unused = ((GeneratedMessageLite.ExtendableMessage)message).ensureExtensionsAreMutable();
    return decodeExtension(tag, data, position, limit, (GeneratedMessageLite.ExtendableMessage<?, ?>)message, extension, unknownFieldSchema, registers);
  }
  
  static int decodeExtension(int tag, byte[] data, int position, int limit, GeneratedMessageLite.ExtendableMessage<?, ?> message, GeneratedMessageLite.GeneratedExtension<?, ?> extension, UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> unknownFieldSchema, Registers registers) throws IOException {
    FieldSet<GeneratedMessageLite.ExtensionDescriptor> extensions = message.extensions;
    int fieldNumber = tag >>> 3;
    if (extension.descriptor.isRepeated() && extension.descriptor.isPacked()) {
      DoubleArrayList doubleArrayList;
      FloatArrayList floatArrayList;
      LongArrayList longArrayList3;
      IntArrayList intArrayList3;
      LongArrayList longArrayList2;
      IntArrayList intArrayList2;
      BooleanArrayList booleanArrayList;
      IntArrayList intArrayList1;
      LongArrayList longArrayList1;
      IntArrayList list;
      switch (extension.getLiteType()) {
        case DOUBLE:
          doubleArrayList = new DoubleArrayList();
          position = decodePackedDoubleList(data, position, doubleArrayList, registers);
          extensions.setField(extension.descriptor, doubleArrayList);
          return position;
        case FLOAT:
          floatArrayList = new FloatArrayList();
          position = decodePackedFloatList(data, position, floatArrayList, registers);
          extensions.setField(extension.descriptor, floatArrayList);
          return position;
        case INT64:
        case UINT64:
          longArrayList3 = new LongArrayList();
          position = decodePackedVarint64List(data, position, longArrayList3, registers);
          extensions.setField(extension.descriptor, longArrayList3);
          return position;
        case INT32:
        case UINT32:
          intArrayList3 = new IntArrayList();
          position = decodePackedVarint32List(data, position, intArrayList3, registers);
          extensions.setField(extension.descriptor, intArrayList3);
          return position;
        case FIXED64:
        case SFIXED64:
          longArrayList2 = new LongArrayList();
          position = decodePackedFixed64List(data, position, longArrayList2, registers);
          extensions.setField(extension.descriptor, longArrayList2);
          return position;
        case FIXED32:
        case SFIXED32:
          intArrayList2 = new IntArrayList();
          position = decodePackedFixed32List(data, position, intArrayList2, registers);
          extensions.setField(extension.descriptor, intArrayList2);
          return position;
        case BOOL:
          booleanArrayList = new BooleanArrayList();
          position = decodePackedBoolList(data, position, booleanArrayList, registers);
          extensions.setField(extension.descriptor, booleanArrayList);
          return position;
        case SINT32:
          intArrayList1 = new IntArrayList();
          position = decodePackedSInt32List(data, position, intArrayList1, registers);
          extensions.setField(extension.descriptor, intArrayList1);
          return position;
        case SINT64:
          longArrayList1 = new LongArrayList();
          position = decodePackedSInt64List(data, position, longArrayList1, registers);
          extensions.setField(extension.descriptor, longArrayList1);
          return position;
        case ENUM:
          list = new IntArrayList();
          position = decodePackedVarint32List(data, position, list, registers);
          SchemaUtil.filterUnknownEnumList(message, fieldNumber, list, extension.descriptor.getEnumType(), (UnknownFieldSetLite)null, unknownFieldSchema);
          extensions.setField(extension.descriptor, list);
          return position;
      } 
      throw new IllegalStateException("Type cannot be packed: " + extension.descriptor.getLiteType());
    } 
    Object value = null;
    if (extension.getLiteType() == WireFormat.FieldType.ENUM) {
      position = decodeVarint32(data, position, registers);
      Object enumValue = extension.descriptor.getEnumType().findValueByNumber(registers.int1);
      if (enumValue == null) {
        SchemaUtil.storeUnknownEnum(message, fieldNumber, registers.int1, null, unknownFieldSchema);
        return position;
      } 
      value = Integer.valueOf(registers.int1);
    } else {
      int endTag;
      Schema<?> fieldSchema;
      Schema<?> schema1;
      switch (extension.getLiteType()) {
        case DOUBLE:
          value = Double.valueOf(decodeDouble(data, position));
          position += 8;
          break;
        case FLOAT:
          value = Float.valueOf(decodeFloat(data, position));
          position += 4;
          break;
        case INT64:
        case UINT64:
          position = decodeVarint64(data, position, registers);
          value = Long.valueOf(registers.long1);
          break;
        case INT32:
        case UINT32:
          position = decodeVarint32(data, position, registers);
          value = Integer.valueOf(registers.int1);
          break;
        case FIXED64:
        case SFIXED64:
          value = Long.valueOf(decodeFixed64(data, position));
          position += 8;
          break;
        case FIXED32:
        case SFIXED32:
          value = Integer.valueOf(decodeFixed32(data, position));
          position += 4;
          break;
        case BOOL:
          position = decodeVarint64(data, position, registers);
          value = Boolean.valueOf((registers.long1 != 0L));
          break;
        case BYTES:
          position = decodeBytes(data, position, registers);
          value = registers.object1;
          break;
        case SINT32:
          position = decodeVarint32(data, position, registers);
          value = Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1));
          break;
        case SINT64:
          position = decodeVarint64(data, position, registers);
          value = Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1));
          break;
        case STRING:
          position = decodeString(data, position, registers);
          value = registers.object1;
          break;
        case GROUP:
          endTag = fieldNumber << 3 | 0x4;
          schema1 = Protobuf.getInstance().schemaFor(extension.getMessageDefaultInstance().getClass());
          if (extension.isRepeated()) {
            position = decodeGroupField(schema1, data, position, limit, endTag, registers);
            extensions.addRepeatedField(extension.descriptor, registers.object1);
          } else {
            Object oldValue = extensions.getField(extension.descriptor);
            if (oldValue == null) {
              oldValue = schema1.newInstance();
              extensions.setField(extension.descriptor, oldValue);
            } 
            position = mergeGroupField(oldValue, schema1, data, position, limit, endTag, registers);
          } 
          return position;
        case MESSAGE:
          fieldSchema = Protobuf.getInstance().schemaFor(extension.getMessageDefaultInstance().getClass());
          if (extension.isRepeated()) {
            position = decodeMessageField(fieldSchema, data, position, limit, registers);
            extensions.addRepeatedField(extension.descriptor, registers.object1);
          } else {
            Object oldValue = extensions.getField(extension.descriptor);
            if (oldValue == null) {
              oldValue = fieldSchema.newInstance();
              extensions.setField(extension.descriptor, oldValue);
            } 
            position = mergeMessageField(oldValue, fieldSchema, data, position, limit, registers);
          } 
          return position;
        case ENUM:
          throw new IllegalStateException("Shouldn't reach here.");
      } 
    } 
    if (extension.isRepeated()) {
      extensions.addRepeatedField(extension.descriptor, value);
    } else {
      extensions.setField(extension.descriptor, value);
    } 
    return position;
  }
  
  static int decodeUnknownField(int tag, byte[] data, int position, int limit, UnknownFieldSetLite unknownFields, Registers registers) throws InvalidProtocolBufferException {
    int length;
    UnknownFieldSetLite child;
    int endGroup;
    int lastTag;
    if (WireFormat.getTagFieldNumber(tag) == 0)
      throw InvalidProtocolBufferException.invalidTag(); 
    switch (WireFormat.getTagWireType(tag)) {
      case 0:
        position = decodeVarint64(data, position, registers);
        unknownFields.storeField(tag, Long.valueOf(registers.long1));
        return position;
      case 5:
        unknownFields.storeField(tag, Integer.valueOf(decodeFixed32(data, position)));
        return position + 4;
      case 1:
        unknownFields.storeField(tag, Long.valueOf(decodeFixed64(data, position)));
        return position + 8;
      case 2:
        position = decodeVarint32(data, position, registers);
        length = registers.int1;
        if (length < 0)
          throw InvalidProtocolBufferException.negativeSize(); 
        if (length > data.length - position)
          throw InvalidProtocolBufferException.truncatedMessage(); 
        if (length == 0) {
          unknownFields.storeField(tag, ByteString.EMPTY);
        } else {
          unknownFields.storeField(tag, ByteString.copyFrom(data, position, length));
        } 
        return position + length;
      case 3:
        child = UnknownFieldSetLite.newInstance();
        endGroup = tag & 0xFFFFFFF8 | 0x4;
        lastTag = 0;
        while (position < limit) {
          position = decodeVarint32(data, position, registers);
          lastTag = registers.int1;
          if (lastTag == endGroup)
            break; 
          position = decodeUnknownField(lastTag, data, position, limit, child, registers);
        } 
        if (position > limit || lastTag != endGroup)
          throw InvalidProtocolBufferException.parseFailure(); 
        unknownFields.storeField(tag, child);
        return position;
    } 
    throw InvalidProtocolBufferException.invalidTag();
  }
  
  static int skipField(int tag, byte[] data, int position, int limit, Registers registers) throws InvalidProtocolBufferException {
    int endGroup;
    int lastTag;
    if (WireFormat.getTagFieldNumber(tag) == 0)
      throw InvalidProtocolBufferException.invalidTag(); 
    switch (WireFormat.getTagWireType(tag)) {
      case 0:
        position = decodeVarint64(data, position, registers);
        return position;
      case 5:
        return position + 4;
      case 1:
        return position + 8;
      case 2:
        position = decodeVarint32(data, position, registers);
        return position + registers.int1;
      case 3:
        endGroup = tag & 0xFFFFFFF8 | 0x4;
        lastTag = 0;
        while (position < limit) {
          position = decodeVarint32(data, position, registers);
          lastTag = registers.int1;
          if (lastTag == endGroup)
            break; 
          position = skipField(lastTag, data, position, limit, registers);
        } 
        if (position > limit || lastTag != endGroup)
          throw InvalidProtocolBufferException.parseFailure(); 
        return position;
    } 
    throw InvalidProtocolBufferException.invalidTag();
  }
}
