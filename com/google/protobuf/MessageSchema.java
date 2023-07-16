package com.google.protobuf;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import sun.misc.Unsafe;

@CheckReturnValue
final class MessageSchema<T> implements Schema<T> {
  private static final int INTS_PER_FIELD = 3;
  
  private static final int OFFSET_BITS = 20;
  
  private static final int OFFSET_MASK = 1048575;
  
  private static final int FIELD_TYPE_MASK = 267386880;
  
  private static final int REQUIRED_MASK = 268435456;
  
  private static final int ENFORCE_UTF8_MASK = 536870912;
  
  private static final int NO_PRESENCE_SENTINEL = 1048575;
  
  private static final int[] EMPTY_INT_ARRAY = new int[0];
  
  static final int ONEOF_TYPE_OFFSET = 51;
  
  private static final Unsafe UNSAFE = UnsafeUtil.getUnsafe();
  
  private final int[] buffer;
  
  private final Object[] objects;
  
  private final int minFieldNumber;
  
  private final int maxFieldNumber;
  
  private final MessageLite defaultInstance;
  
  private final boolean hasExtensions;
  
  private final boolean lite;
  
  private final boolean proto3;
  
  private final boolean useCachedSizeField;
  
  private final int[] intArray;
  
  private final int checkInitializedCount;
  
  private final int repeatedFieldOffsetStart;
  
  private final NewInstanceSchema newInstanceSchema;
  
  private final ListFieldSchema listFieldSchema;
  
  private final UnknownFieldSchema<?, ?> unknownFieldSchema;
  
  private final ExtensionSchema<?> extensionSchema;
  
  private final MapFieldSchema mapFieldSchema;
  
  private MessageSchema(int[] buffer, Object[] objects, int minFieldNumber, int maxFieldNumber, MessageLite defaultInstance, boolean proto3, boolean useCachedSizeField, int[] intArray, int checkInitialized, int mapFieldPositions, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
    this.buffer = buffer;
    this.objects = objects;
    this.minFieldNumber = minFieldNumber;
    this.maxFieldNumber = maxFieldNumber;
    this.lite = defaultInstance instanceof GeneratedMessageLite;
    this.proto3 = proto3;
    this.hasExtensions = (extensionSchema != null && extensionSchema.hasExtensions(defaultInstance));
    this.useCachedSizeField = useCachedSizeField;
    this.intArray = intArray;
    this.checkInitializedCount = checkInitialized;
    this.repeatedFieldOffsetStart = mapFieldPositions;
    this.newInstanceSchema = newInstanceSchema;
    this.listFieldSchema = listFieldSchema;
    this.unknownFieldSchema = unknownFieldSchema;
    this.extensionSchema = extensionSchema;
    this.defaultInstance = defaultInstance;
    this.mapFieldSchema = mapFieldSchema;
  }
  
  static <T> MessageSchema<T> newSchema(Class<T> messageClass, MessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
    if (messageInfo instanceof RawMessageInfo)
      return newSchemaForRawMessageInfo((RawMessageInfo)messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema); 
    return newSchemaForMessageInfo((StructuralMessageInfo)messageInfo, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
  }
  
  static <T> MessageSchema<T> newSchemaForRawMessageInfo(RawMessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
    int oneofCount, minFieldNumber, maxFieldNumber, numEntries, mapFieldCount, checkInitialized, intArray[], objectsPosition;
    boolean isProto3 = (messageInfo.getSyntax() == ProtoSyntax.PROTO3);
    String info = messageInfo.getStringInfo();
    int length = info.length();
    int i = 0;
    int next = info.charAt(i++);
    if (next >= 55296) {
      int result = next & 0x1FFF;
      int shift = 13;
      while ((next = info.charAt(i++)) >= 55296) {
        result |= (next & 0x1FFF) << shift;
        shift += 13;
      } 
      next = result | next << shift;
    } 
    int unusedFlags = next;
    next = info.charAt(i++);
    if (next >= 55296) {
      int result = next & 0x1FFF;
      int shift = 13;
      while ((next = info.charAt(i++)) >= 55296) {
        result |= (next & 0x1FFF) << shift;
        shift += 13;
      } 
      next = result | next << shift;
    } 
    int fieldCount = next;
    if (fieldCount == 0) {
      oneofCount = 0;
      int hasBitsCount = 0;
      minFieldNumber = 0;
      maxFieldNumber = 0;
      numEntries = 0;
      mapFieldCount = 0;
      int repeatedFieldCount = 0;
      checkInitialized = 0;
      intArray = EMPTY_INT_ARRAY;
      objectsPosition = 0;
    } else {
      next = info.charAt(i++);
      if (next >= 55296) {
        int result = next & 0x1FFF;
        int shift = 13;
        while ((next = info.charAt(i++)) >= 55296) {
          result |= (next & 0x1FFF) << shift;
          shift += 13;
        } 
        next = result | next << shift;
      } 
      oneofCount = next;
      next = info.charAt(i++);
      if (next >= 55296) {
        int result = next & 0x1FFF;
        int shift = 13;
        while ((next = info.charAt(i++)) >= 55296) {
          result |= (next & 0x1FFF) << shift;
          shift += 13;
        } 
        next = result | next << shift;
      } 
      int hasBitsCount = next;
      next = info.charAt(i++);
      if (next >= 55296) {
        int result = next & 0x1FFF;
        int shift = 13;
        while ((next = info.charAt(i++)) >= 55296) {
          result |= (next & 0x1FFF) << shift;
          shift += 13;
        } 
        next = result | next << shift;
      } 
      minFieldNumber = next;
      next = info.charAt(i++);
      if (next >= 55296) {
        int result = next & 0x1FFF;
        int shift = 13;
        while ((next = info.charAt(i++)) >= 55296) {
          result |= (next & 0x1FFF) << shift;
          shift += 13;
        } 
        next = result | next << shift;
      } 
      maxFieldNumber = next;
      next = info.charAt(i++);
      if (next >= 55296) {
        int result = next & 0x1FFF;
        int shift = 13;
        while ((next = info.charAt(i++)) >= 55296) {
          result |= (next & 0x1FFF) << shift;
          shift += 13;
        } 
        next = result | next << shift;
      } 
      numEntries = next;
      next = info.charAt(i++);
      if (next >= 55296) {
        int result = next & 0x1FFF;
        int shift = 13;
        while ((next = info.charAt(i++)) >= 55296) {
          result |= (next & 0x1FFF) << shift;
          shift += 13;
        } 
        next = result | next << shift;
      } 
      mapFieldCount = next;
      next = info.charAt(i++);
      if (next >= 55296) {
        int result = next & 0x1FFF;
        int shift = 13;
        while ((next = info.charAt(i++)) >= 55296) {
          result |= (next & 0x1FFF) << shift;
          shift += 13;
        } 
        next = result | next << shift;
      } 
      int repeatedFieldCount = next;
      next = info.charAt(i++);
      if (next >= 55296) {
        int result = next & 0x1FFF;
        int shift = 13;
        while ((next = info.charAt(i++)) >= 55296) {
          result |= (next & 0x1FFF) << shift;
          shift += 13;
        } 
        next = result | next << shift;
      } 
      checkInitialized = next;
      intArray = new int[checkInitialized + mapFieldCount + repeatedFieldCount];
      objectsPosition = oneofCount * 2 + hasBitsCount;
    } 
    Unsafe unsafe = UNSAFE;
    Object[] messageInfoObjects = messageInfo.getObjects();
    int checkInitializedPosition = 0;
    Class<?> messageClass = messageInfo.getDefaultInstance().getClass();
    int[] buffer = new int[numEntries * 3];
    Object[] objects = new Object[numEntries * 2];
    int mapFieldIndex = checkInitialized;
    int repeatedFieldIndex = checkInitialized + mapFieldCount;
    int bufferIndex = 0;
    while (i < length) {
      int fieldOffset, presenceMaskShift, presenceFieldOffset;
      next = info.charAt(i++);
      if (next >= 55296) {
        int result = next & 0x1FFF;
        int shift = 13;
        while ((next = info.charAt(i++)) >= 55296) {
          result |= (next & 0x1FFF) << shift;
          shift += 13;
        } 
        next = result | next << shift;
      } 
      int fieldNumber = next;
      next = info.charAt(i++);
      if (next >= 55296) {
        int result = next & 0x1FFF;
        int shift = 13;
        while ((next = info.charAt(i++)) >= 55296) {
          result |= (next & 0x1FFF) << shift;
          shift += 13;
        } 
        next = result | next << shift;
      } 
      int fieldTypeWithExtraBits = next;
      int fieldType = fieldTypeWithExtraBits & 0xFF;
      if ((fieldTypeWithExtraBits & 0x400) != 0)
        intArray[checkInitializedPosition++] = bufferIndex; 
      if (fieldType >= 51) {
        Field oneofField, oneofCaseField;
        next = info.charAt(i++);
        if (next >= 55296) {
          int result = next & 0x1FFF;
          int shift = 13;
          while ((next = info.charAt(i++)) >= 55296) {
            result |= (next & 0x1FFF) << shift;
            shift += 13;
          } 
          next = result | next << shift;
        } 
        int oneofIndex = next;
        int oneofFieldType = fieldType - 51;
        if (oneofFieldType == 9 || oneofFieldType == 17) {
          objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
        } else if (oneofFieldType == 12 && 
          !isProto3) {
          objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
        } 
        int index = oneofIndex * 2;
        Object o = messageInfoObjects[index];
        if (o instanceof Field) {
          oneofField = (Field)o;
        } else {
          oneofField = reflectField(messageClass, (String)o);
          messageInfoObjects[index] = oneofField;
        } 
        fieldOffset = (int)unsafe.objectFieldOffset(oneofField);
        index++;
        o = messageInfoObjects[index];
        if (o instanceof Field) {
          oneofCaseField = (Field)o;
        } else {
          oneofCaseField = reflectField(messageClass, (String)o);
          messageInfoObjects[index] = oneofCaseField;
        } 
        presenceFieldOffset = (int)unsafe.objectFieldOffset(oneofCaseField);
        presenceMaskShift = 0;
      } else {
        Field field = reflectField(messageClass, (String)messageInfoObjects[objectsPosition++]);
        if (fieldType == 9 || fieldType == 17) {
          objects[bufferIndex / 3 * 2 + 1] = field.getType();
        } else if (fieldType == 27 || fieldType == 49) {
          objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++];
        } else if (fieldType == 12 || fieldType == 30 || fieldType == 44) {
          if (!isProto3)
            objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++]; 
        } else if (fieldType == 50) {
          intArray[mapFieldIndex++] = bufferIndex;
          objects[bufferIndex / 3 * 2] = messageInfoObjects[objectsPosition++];
          if ((fieldTypeWithExtraBits & 0x800) != 0)
            objects[bufferIndex / 3 * 2 + 1] = messageInfoObjects[objectsPosition++]; 
        } 
        fieldOffset = (int)unsafe.objectFieldOffset(field);
        boolean hasHasBit = ((fieldTypeWithExtraBits & 0x1000) == 4096);
        if (hasHasBit && fieldType <= 17) {
          Field hasBitsField;
          next = info.charAt(i++);
          if (next >= 55296) {
            int result = next & 0x1FFF;
            int shift = 13;
            while ((next = info.charAt(i++)) >= 55296) {
              result |= (next & 0x1FFF) << shift;
              shift += 13;
            } 
            next = result | next << shift;
          } 
          int hasBitsIndex = next;
          int index = oneofCount * 2 + hasBitsIndex / 32;
          Object o = messageInfoObjects[index];
          if (o instanceof Field) {
            hasBitsField = (Field)o;
          } else {
            hasBitsField = reflectField(messageClass, (String)o);
            messageInfoObjects[index] = hasBitsField;
          } 
          presenceFieldOffset = (int)unsafe.objectFieldOffset(hasBitsField);
          presenceMaskShift = hasBitsIndex % 32;
        } else {
          presenceFieldOffset = 1048575;
          presenceMaskShift = 0;
        } 
        if (fieldType >= 18 && fieldType <= 49)
          intArray[repeatedFieldIndex++] = fieldOffset; 
      } 
      buffer[bufferIndex++] = fieldNumber;
      buffer[bufferIndex++] = (((fieldTypeWithExtraBits & 0x200) != 0) ? true : false) | (((fieldTypeWithExtraBits & 0x100) != 0) ? true : false) | fieldType << 20 | fieldOffset;
      buffer[bufferIndex++] = presenceMaskShift << 20 | presenceFieldOffset;
    } 
    return new MessageSchema<>(buffer, objects, minFieldNumber, maxFieldNumber, messageInfo
        
        .getDefaultInstance(), isProto3, false, intArray, checkInitialized, checkInitialized + mapFieldCount, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
  }
  
  private static Field reflectField(Class<?> messageClass, String fieldName) {
    try {
      return messageClass.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      Field[] fields = messageClass.getDeclaredFields();
      for (Field field : fields) {
        if (fieldName.equals(field.getName()))
          return field; 
      } 
      throw new RuntimeException("Field " + fieldName + " for " + messageClass
          
          .getName() + " not found. Known fields are " + 
          
          Arrays.toString(fields));
    } 
  }
  
  static <T> MessageSchema<T> newSchemaForMessageInfo(StructuralMessageInfo messageInfo, NewInstanceSchema newInstanceSchema, ListFieldSchema listFieldSchema, UnknownFieldSchema<?, ?> unknownFieldSchema, ExtensionSchema<?> extensionSchema, MapFieldSchema mapFieldSchema) {
    int minFieldNumber, maxFieldNumber;
    boolean isProto3 = (messageInfo.getSyntax() == ProtoSyntax.PROTO3);
    FieldInfo[] fis = messageInfo.getFields();
    if (fis.length == 0) {
      minFieldNumber = 0;
      maxFieldNumber = 0;
    } else {
      minFieldNumber = fis[0].getFieldNumber();
      maxFieldNumber = fis[fis.length - 1].getFieldNumber();
    } 
    int numEntries = fis.length;
    int[] buffer = new int[numEntries * 3];
    Object[] objects = new Object[numEntries * 2];
    int mapFieldCount = 0;
    int repeatedFieldCount = 0;
    for (FieldInfo fi : fis) {
      if (fi.getType() == FieldType.MAP) {
        mapFieldCount++;
      } else if (fi.getType().id() >= 18 && fi.getType().id() <= 49) {
        repeatedFieldCount++;
      } 
    } 
    int[] mapFieldPositions = (mapFieldCount > 0) ? new int[mapFieldCount] : null;
    int[] repeatedFieldOffsets = (repeatedFieldCount > 0) ? new int[repeatedFieldCount] : null;
    mapFieldCount = 0;
    repeatedFieldCount = 0;
    int[] checkInitialized = messageInfo.getCheckInitialized();
    if (checkInitialized == null)
      checkInitialized = EMPTY_INT_ARRAY; 
    int checkInitializedIndex = 0;
    int fieldIndex = 0;
    for (int bufferIndex = 0; fieldIndex < fis.length; bufferIndex += 3) {
      FieldInfo fi = fis[fieldIndex];
      int fieldNumber = fi.getFieldNumber();
      storeFieldData(fi, buffer, bufferIndex, objects);
      if (checkInitializedIndex < checkInitialized.length && checkInitialized[checkInitializedIndex] == fieldNumber)
        checkInitialized[checkInitializedIndex++] = bufferIndex; 
      if (fi.getType() == FieldType.MAP) {
        mapFieldPositions[mapFieldCount++] = bufferIndex;
      } else if (fi.getType().id() >= 18 && fi.getType().id() <= 49) {
        repeatedFieldOffsets[repeatedFieldCount++] = 
          (int)UnsafeUtil.objectFieldOffset(fi.getField());
      } 
      fieldIndex++;
    } 
    if (mapFieldPositions == null)
      mapFieldPositions = EMPTY_INT_ARRAY; 
    if (repeatedFieldOffsets == null)
      repeatedFieldOffsets = EMPTY_INT_ARRAY; 
    int[] combined = new int[checkInitialized.length + mapFieldPositions.length + repeatedFieldOffsets.length];
    System.arraycopy(checkInitialized, 0, combined, 0, checkInitialized.length);
    System.arraycopy(mapFieldPositions, 0, combined, checkInitialized.length, mapFieldPositions.length);
    System.arraycopy(repeatedFieldOffsets, 0, combined, checkInitialized.length + mapFieldPositions.length, repeatedFieldOffsets.length);
    return new MessageSchema<>(buffer, objects, minFieldNumber, maxFieldNumber, messageInfo
        
        .getDefaultInstance(), isProto3, true, combined, checkInitialized.length, checkInitialized.length + mapFieldPositions.length, newInstanceSchema, listFieldSchema, unknownFieldSchema, extensionSchema, mapFieldSchema);
  }
  
  private static void storeFieldData(FieldInfo fi, int[] buffer, int bufferIndex, Object[] objects) {
    int fieldOffset, typeId, presenceMaskShift, presenceFieldOffset;
    OneofInfo oneof = fi.getOneof();
    if (oneof != null) {
      typeId = fi.getType().id() + 51;
      fieldOffset = (int)UnsafeUtil.objectFieldOffset(oneof.getValueField());
      presenceFieldOffset = (int)UnsafeUtil.objectFieldOffset(oneof.getCaseField());
      presenceMaskShift = 0;
    } else {
      FieldType type = fi.getType();
      fieldOffset = (int)UnsafeUtil.objectFieldOffset(fi.getField());
      typeId = type.id();
      if (!type.isList() && !type.isMap()) {
        Field presenceField = fi.getPresenceField();
        if (presenceField == null) {
          presenceFieldOffset = 1048575;
        } else {
          presenceFieldOffset = (int)UnsafeUtil.objectFieldOffset(presenceField);
        } 
        presenceMaskShift = Integer.numberOfTrailingZeros(fi.getPresenceMask());
      } else if (fi.getCachedSizeField() == null) {
        presenceFieldOffset = 0;
        presenceMaskShift = 0;
      } else {
        presenceFieldOffset = (int)UnsafeUtil.objectFieldOffset(fi.getCachedSizeField());
        presenceMaskShift = 0;
      } 
    } 
    buffer[bufferIndex] = fi.getFieldNumber();
    buffer[bufferIndex + 1] = (
      fi.isEnforceUtf8() ? true : false) | (
      fi.isRequired() ? true : false) | typeId << 20 | fieldOffset;
    buffer[bufferIndex + 2] = presenceMaskShift << 20 | presenceFieldOffset;
    Object<?> messageFieldClass = (Object<?>)fi.getMessageFieldClass();
    if (fi.getMapDefaultEntry() != null) {
      objects[bufferIndex / 3 * 2] = fi.getMapDefaultEntry();
      if (messageFieldClass != null) {
        objects[bufferIndex / 3 * 2 + 1] = messageFieldClass;
      } else if (fi.getEnumVerifier() != null) {
        objects[bufferIndex / 3 * 2 + 1] = fi.getEnumVerifier();
      } 
    } else if (messageFieldClass != null) {
      objects[bufferIndex / 3 * 2 + 1] = messageFieldClass;
    } else if (fi.getEnumVerifier() != null) {
      objects[bufferIndex / 3 * 2 + 1] = fi.getEnumVerifier();
    } 
  }
  
  public T newInstance() {
    return (T)this.newInstanceSchema.newInstance(this.defaultInstance);
  }
  
  public boolean equals(T message, T other) {
    int bufferLength = this.buffer.length;
    for (int pos = 0; pos < bufferLength; pos += 3) {
      if (!equals(message, other, pos))
        return false; 
    } 
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
  
  private boolean equals(T message, T other, int pos) {
    int typeAndOffset = typeAndOffsetAt(pos);
    long offset = offset(typeAndOffset);
    switch (type(typeAndOffset)) {
      case 0:
        return (arePresentForEquals(message, other, pos) && 
          Double.doubleToLongBits(UnsafeUtil.getDouble(message, offset)) == 
          Double.doubleToLongBits(UnsafeUtil.getDouble(other, offset)));
      case 1:
        return (arePresentForEquals(message, other, pos) && 
          Float.floatToIntBits(UnsafeUtil.getFloat(message, offset)) == 
          Float.floatToIntBits(UnsafeUtil.getFloat(other, offset)));
      case 2:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset));
      case 3:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset));
      case 4:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
      case 5:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset));
      case 6:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
      case 7:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getBoolean(message, offset) == UnsafeUtil.getBoolean(other, offset));
      case 8:
        return (arePresentForEquals(message, other, pos) && 
          SchemaUtil.safeEquals(
            UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset)));
      case 9:
        return (arePresentForEquals(message, other, pos) && 
          SchemaUtil.safeEquals(
            UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset)));
      case 10:
        return (arePresentForEquals(message, other, pos) && 
          SchemaUtil.safeEquals(
            UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset)));
      case 11:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
      case 12:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
      case 13:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
      case 14:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset));
      case 15:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getInt(message, offset) == UnsafeUtil.getInt(other, offset));
      case 16:
        return (arePresentForEquals(message, other, pos) && 
          UnsafeUtil.getLong(message, offset) == UnsafeUtil.getLong(other, offset));
      case 17:
        return (arePresentForEquals(message, other, pos) && 
          SchemaUtil.safeEquals(
            UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset)));
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
      case 49:
        return SchemaUtil.safeEquals(
            UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
      case 50:
        return SchemaUtil.safeEquals(
            UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset));
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
      case 59:
      case 60:
      case 61:
      case 62:
      case 63:
      case 64:
      case 65:
      case 66:
      case 67:
      case 68:
        return (isOneofCaseEqual(message, other, pos) && 
          SchemaUtil.safeEquals(
            UnsafeUtil.getObject(message, offset), UnsafeUtil.getObject(other, offset)));
    } 
    return true;
  }
  
  public int hashCode(T message) {
    int hashCode = 0;
    int bufferLength = this.buffer.length;
    for (int pos = 0; pos < bufferLength; pos += 3) {
      int protoHash;
      Object submessage;
      int typeAndOffset = typeAndOffsetAt(pos);
      int entryNumber = numberAt(pos);
      long offset = offset(typeAndOffset);
      switch (type(typeAndOffset)) {
        case 0:
          hashCode = hashCode * 53 + Internal.hashLong(
              Double.doubleToLongBits(UnsafeUtil.getDouble(message, offset)));
          break;
        case 1:
          hashCode = hashCode * 53 + Float.floatToIntBits(UnsafeUtil.getFloat(message, offset));
          break;
        case 2:
          hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
          break;
        case 3:
          hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
          break;
        case 4:
          hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
          break;
        case 5:
          hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
          break;
        case 6:
          hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
          break;
        case 7:
          hashCode = hashCode * 53 + Internal.hashBoolean(UnsafeUtil.getBoolean(message, offset));
          break;
        case 8:
          hashCode = hashCode * 53 + ((String)UnsafeUtil.getObject(message, offset)).hashCode();
          break;
        case 9:
          protoHash = 37;
          submessage = UnsafeUtil.getObject(message, offset);
          if (submessage != null)
            protoHash = submessage.hashCode(); 
          hashCode = 53 * hashCode + protoHash;
          break;
        case 10:
          hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
          break;
        case 11:
          hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
          break;
        case 12:
          hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
          break;
        case 13:
          hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
          break;
        case 14:
          hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
          break;
        case 15:
          hashCode = hashCode * 53 + UnsafeUtil.getInt(message, offset);
          break;
        case 16:
          hashCode = hashCode * 53 + Internal.hashLong(UnsafeUtil.getLong(message, offset));
          break;
        case 17:
          protoHash = 37;
          submessage = UnsafeUtil.getObject(message, offset);
          if (submessage != null)
            protoHash = submessage.hashCode(); 
          hashCode = 53 * hashCode + protoHash;
          break;
        case 18:
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 26:
        case 27:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
        case 40:
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
        case 46:
        case 47:
        case 48:
        case 49:
          hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
          break;
        case 50:
          hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode();
          break;
        case 51:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + Internal.hashLong(Double.doubleToLongBits(oneofDoubleAt(message, offset))); 
          break;
        case 52:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + Float.floatToIntBits(oneofFloatAt(message, offset)); 
          break;
        case 53:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + Internal.hashLong(oneofLongAt(message, offset)); 
          break;
        case 54:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + Internal.hashLong(oneofLongAt(message, offset)); 
          break;
        case 55:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + oneofIntAt(message, offset); 
          break;
        case 56:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + Internal.hashLong(oneofLongAt(message, offset)); 
          break;
        case 57:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + oneofIntAt(message, offset); 
          break;
        case 58:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + Internal.hashBoolean(oneofBooleanAt(message, offset)); 
          break;
        case 59:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + ((String)UnsafeUtil.getObject(message, offset)).hashCode(); 
          break;
        case 60:
          if (isOneofPresent(message, entryNumber, pos)) {
            Object object = UnsafeUtil.getObject(message, offset);
            hashCode = 53 * hashCode + object.hashCode();
          } 
          break;
        case 61:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + UnsafeUtil.getObject(message, offset).hashCode(); 
          break;
        case 62:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + oneofIntAt(message, offset); 
          break;
        case 63:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + oneofIntAt(message, offset); 
          break;
        case 64:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + oneofIntAt(message, offset); 
          break;
        case 65:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + Internal.hashLong(oneofLongAt(message, offset)); 
          break;
        case 66:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + oneofIntAt(message, offset); 
          break;
        case 67:
          if (isOneofPresent(message, entryNumber, pos))
            hashCode = hashCode * 53 + Internal.hashLong(oneofLongAt(message, offset)); 
          break;
        case 68:
          if (isOneofPresent(message, entryNumber, pos)) {
            Object object = UnsafeUtil.getObject(message, offset);
            hashCode = 53 * hashCode + object.hashCode();
          } 
          break;
      } 
    } 
    hashCode = hashCode * 53 + this.unknownFieldSchema.getFromMessage(message).hashCode();
    if (this.hasExtensions)
      hashCode = hashCode * 53 + this.extensionSchema.getExtensions(message).hashCode(); 
    return hashCode;
  }
  
  public void mergeFrom(T message, T other) {
    checkMutable(message);
    if (other == null)
      throw new NullPointerException(); 
    for (int i = 0; i < this.buffer.length; i += 3)
      mergeSingleField(message, other, i); 
    SchemaUtil.mergeUnknownFields(this.unknownFieldSchema, message, other);
    if (this.hasExtensions)
      SchemaUtil.mergeExtensions(this.extensionSchema, message, other); 
  }
  
  private void mergeSingleField(T message, T other, int pos) {
    int typeAndOffset = typeAndOffsetAt(pos);
    long offset = offset(typeAndOffset);
    int number = numberAt(pos);
    switch (type(typeAndOffset)) {
      case 0:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putDouble(message, offset, UnsafeUtil.getDouble(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 1:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putFloat(message, offset, UnsafeUtil.getFloat(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 2:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 3:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 4:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 5:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 6:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 7:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putBoolean(message, offset, UnsafeUtil.getBoolean(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 8:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 9:
        mergeMessage(message, other, pos);
        break;
      case 10:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 11:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 12:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 13:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 14:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 15:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putInt(message, offset, UnsafeUtil.getInt(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 16:
        if (isFieldPresent(other, pos)) {
          UnsafeUtil.putLong(message, offset, UnsafeUtil.getLong(other, offset));
          setFieldPresent(message, pos);
        } 
        break;
      case 17:
        mergeMessage(message, other, pos);
        break;
      case 18:
      case 19:
      case 20:
      case 21:
      case 22:
      case 23:
      case 24:
      case 25:
      case 26:
      case 27:
      case 28:
      case 29:
      case 30:
      case 31:
      case 32:
      case 33:
      case 34:
      case 35:
      case 36:
      case 37:
      case 38:
      case 39:
      case 40:
      case 41:
      case 42:
      case 43:
      case 44:
      case 45:
      case 46:
      case 47:
      case 48:
      case 49:
        this.listFieldSchema.mergeListsAt(message, other, offset);
        break;
      case 50:
        SchemaUtil.mergeMap(this.mapFieldSchema, message, other, offset);
        break;
      case 51:
      case 52:
      case 53:
      case 54:
      case 55:
      case 56:
      case 57:
      case 58:
      case 59:
        if (isOneofPresent(other, number, pos)) {
          UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
          setOneofPresent(message, number, pos);
        } 
        break;
      case 60:
        mergeOneofMessage(message, other, pos);
        break;
      case 61:
      case 62:
      case 63:
      case 64:
      case 65:
      case 66:
      case 67:
        if (isOneofPresent(other, number, pos)) {
          UnsafeUtil.putObject(message, offset, UnsafeUtil.getObject(other, offset));
          setOneofPresent(message, number, pos);
        } 
        break;
      case 68:
        mergeOneofMessage(message, other, pos);
        break;
    } 
  }
  
  private void mergeMessage(T targetParent, T sourceParent, int pos) {
    if (!isFieldPresent(sourceParent, pos))
      return; 
    int typeAndOffset = typeAndOffsetAt(pos);
    long offset = offset(typeAndOffset);
    Object source = UNSAFE.getObject(sourceParent, offset);
    if (source == null)
      throw new IllegalStateException("Source subfield " + 
          numberAt(pos) + " is present but null: " + sourceParent); 
    Schema<Object> fieldSchema = getMessageFieldSchema(pos);
    if (!isFieldPresent(targetParent, pos)) {
      if (!isMutable(source)) {
        UNSAFE.putObject(targetParent, offset, source);
      } else {
        Object copyOfSource = fieldSchema.newInstance();
        fieldSchema.mergeFrom(copyOfSource, source);
        UNSAFE.putObject(targetParent, offset, copyOfSource);
      } 
      setFieldPresent(targetParent, pos);
      return;
    } 
    Object target = UNSAFE.getObject(targetParent, offset);
    if (!isMutable(target)) {
      Object newInstance = fieldSchema.newInstance();
      fieldSchema.mergeFrom(newInstance, target);
      UNSAFE.putObject(targetParent, offset, newInstance);
      target = newInstance;
    } 
    fieldSchema.mergeFrom(target, source);
  }
  
  private void mergeOneofMessage(T targetParent, T sourceParent, int pos) {
    int number = numberAt(pos);
    if (!isOneofPresent(sourceParent, number, pos))
      return; 
    long offset = offset(typeAndOffsetAt(pos));
    Object source = UNSAFE.getObject(sourceParent, offset);
    if (source == null)
      throw new IllegalStateException("Source subfield " + 
          numberAt(pos) + " is present but null: " + sourceParent); 
    Schema<Object> fieldSchema = getMessageFieldSchema(pos);
    if (!isOneofPresent(targetParent, number, pos)) {
      if (!isMutable(source)) {
        UNSAFE.putObject(targetParent, offset, source);
      } else {
        Object copyOfSource = fieldSchema.newInstance();
        fieldSchema.mergeFrom(copyOfSource, source);
        UNSAFE.putObject(targetParent, offset, copyOfSource);
      } 
      setOneofPresent(targetParent, number, pos);
      return;
    } 
    Object target = UNSAFE.getObject(targetParent, offset);
    if (!isMutable(target)) {
      Object newInstance = fieldSchema.newInstance();
      fieldSchema.mergeFrom(newInstance, target);
      UNSAFE.putObject(targetParent, offset, newInstance);
      target = newInstance;
    } 
    fieldSchema.mergeFrom(target, source);
  }
  
  public int getSerializedSize(T message) {
    return this.proto3 ? getSerializedSizeProto3(message) : getSerializedSizeProto2(message);
  }
  
  private int getSerializedSizeProto2(T message) {
    int size = 0;
    Unsafe unsafe = UNSAFE;
    int currentPresenceFieldOffset = 1048575;
    int currentPresenceField = 0;
    for (int i = 0; i < this.buffer.length; i += 3) {
      int fieldSize, typeAndOffset = typeAndOffsetAt(i);
      int number = numberAt(i);
      int fieldType = type(typeAndOffset);
      int presenceMaskAndOffset = 0;
      int presenceMask = 0;
      if (fieldType <= 17) {
        presenceMaskAndOffset = this.buffer[i + 2];
        int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
        presenceMask = 1 << presenceMaskAndOffset >>> 20;
        if (presenceFieldOffset != currentPresenceFieldOffset) {
          currentPresenceFieldOffset = presenceFieldOffset;
          currentPresenceField = unsafe.getInt(message, presenceFieldOffset);
        } 
      } else if (this.useCachedSizeField && fieldType >= FieldType.DOUBLE_LIST_PACKED
        .id() && fieldType <= FieldType.SINT64_LIST_PACKED
        .id()) {
        presenceMaskAndOffset = this.buffer[i + 2] & 0xFFFFF;
      } 
      long offset = offset(typeAndOffset);
      switch (fieldType) {
        case 0:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeDoubleSize(number, 0.0D); 
          break;
        case 1:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeFloatSize(number, 0.0F); 
          break;
        case 2:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeInt64Size(number, unsafe.getLong(message, offset)); 
          break;
        case 3:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeUInt64Size(number, unsafe.getLong(message, offset)); 
          break;
        case 4:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeInt32Size(number, unsafe.getInt(message, offset)); 
          break;
        case 5:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeFixed64Size(number, 0L); 
          break;
        case 6:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeFixed32Size(number, 0); 
          break;
        case 7:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeBoolSize(number, true); 
          break;
        case 8:
          if ((currentPresenceField & presenceMask) != 0) {
            Object value = unsafe.getObject(message, offset);
            if (value instanceof ByteString) {
              size += CodedOutputStream.computeBytesSize(number, (ByteString)value);
              break;
            } 
            size += CodedOutputStream.computeStringSize(number, (String)value);
          } 
          break;
        case 9:
          if ((currentPresenceField & presenceMask) != 0) {
            Object value = unsafe.getObject(message, offset);
            size += SchemaUtil.computeSizeMessage(number, value, getMessageFieldSchema(i));
          } 
          break;
        case 10:
          if ((currentPresenceField & presenceMask) != 0) {
            ByteString value = (ByteString)unsafe.getObject(message, offset);
            size += CodedOutputStream.computeBytesSize(number, value);
          } 
          break;
        case 11:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeUInt32Size(number, unsafe.getInt(message, offset)); 
          break;
        case 12:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeEnumSize(number, unsafe.getInt(message, offset)); 
          break;
        case 13:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeSFixed32Size(number, 0); 
          break;
        case 14:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeSFixed64Size(number, 0L); 
          break;
        case 15:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeSInt32Size(number, unsafe.getInt(message, offset)); 
          break;
        case 16:
          if ((currentPresenceField & presenceMask) != 0)
            size += CodedOutputStream.computeSInt64Size(number, unsafe.getLong(message, offset)); 
          break;
        case 17:
          if ((currentPresenceField & presenceMask) != 0)
            size += 
              CodedOutputStream.computeGroupSize(number, (MessageLite)unsafe
                
                .getObject(message, offset), 
                getMessageFieldSchema(i)); 
          break;
        case 18:
          size += 
            SchemaUtil.computeSizeFixed64List(number, (List)unsafe
              .getObject(message, offset), false);
          break;
        case 19:
          size += 
            SchemaUtil.computeSizeFixed32List(number, (List)unsafe
              .getObject(message, offset), false);
          break;
        case 20:
          size += 
            SchemaUtil.computeSizeInt64List(number, (List<Long>)unsafe
              .getObject(message, offset), false);
          break;
        case 21:
          size += 
            SchemaUtil.computeSizeUInt64List(number, (List<Long>)unsafe
              .getObject(message, offset), false);
          break;
        case 22:
          size += 
            SchemaUtil.computeSizeInt32List(number, (List<Integer>)unsafe
              .getObject(message, offset), false);
          break;
        case 23:
          size += 
            SchemaUtil.computeSizeFixed64List(number, (List)unsafe
              .getObject(message, offset), false);
          break;
        case 24:
          size += 
            SchemaUtil.computeSizeFixed32List(number, (List)unsafe
              .getObject(message, offset), false);
          break;
        case 25:
          size += 
            SchemaUtil.computeSizeBoolList(number, (List)unsafe
              .getObject(message, offset), false);
          break;
        case 26:
          size += 
            SchemaUtil.computeSizeStringList(number, (List)unsafe.getObject(message, offset));
          break;
        case 27:
          size += 
            SchemaUtil.computeSizeMessageList(number, (List)unsafe
              .getObject(message, offset), getMessageFieldSchema(i));
          break;
        case 28:
          size += 
            SchemaUtil.computeSizeByteStringList(number, (List<ByteString>)unsafe
              .getObject(message, offset));
          break;
        case 29:
          size += 
            SchemaUtil.computeSizeUInt32List(number, (List<Integer>)unsafe
              .getObject(message, offset), false);
          break;
        case 30:
          size += 
            SchemaUtil.computeSizeEnumList(number, (List<Integer>)unsafe
              .getObject(message, offset), false);
          break;
        case 31:
          size += 
            SchemaUtil.computeSizeFixed32List(number, (List)unsafe
              .getObject(message, offset), false);
          break;
        case 32:
          size += 
            SchemaUtil.computeSizeFixed64List(number, (List)unsafe
              .getObject(message, offset), false);
          break;
        case 33:
          size += 
            SchemaUtil.computeSizeSInt32List(number, (List<Integer>)unsafe
              .getObject(message, offset), false);
          break;
        case 34:
          size += 
            SchemaUtil.computeSizeSInt64List(number, (List<Long>)unsafe
              .getObject(message, offset), false);
          break;
        case 35:
          fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 36:
          fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 37:
          fieldSize = SchemaUtil.computeSizeInt64ListNoTag((List<Long>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 38:
          fieldSize = SchemaUtil.computeSizeUInt64ListNoTag((List<Long>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 39:
          fieldSize = SchemaUtil.computeSizeInt32ListNoTag((List<Integer>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 40:
          fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 41:
          fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 42:
          fieldSize = SchemaUtil.computeSizeBoolListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 43:
          fieldSize = SchemaUtil.computeSizeUInt32ListNoTag((List<Integer>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 44:
          fieldSize = SchemaUtil.computeSizeEnumListNoTag((List<Integer>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 45:
          fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 46:
          fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 47:
          fieldSize = SchemaUtil.computeSizeSInt32ListNoTag((List<Integer>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 48:
          fieldSize = SchemaUtil.computeSizeSInt64ListNoTag((List<Long>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, presenceMaskAndOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 49:
          size += 
            SchemaUtil.computeSizeGroupList(number, (List<MessageLite>)unsafe
              
              .getObject(message, offset), 
              getMessageFieldSchema(i));
          break;
        case 50:
          size += this.mapFieldSchema
            .getSerializedSize(number, unsafe
              .getObject(message, offset), getMapFieldDefaultEntry(i));
          break;
        case 51:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeDoubleSize(number, 0.0D); 
          break;
        case 52:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeFloatSize(number, 0.0F); 
          break;
        case 53:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeInt64Size(number, oneofLongAt(message, offset)); 
          break;
        case 54:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeUInt64Size(number, oneofLongAt(message, offset)); 
          break;
        case 55:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeInt32Size(number, oneofIntAt(message, offset)); 
          break;
        case 56:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeFixed64Size(number, 0L); 
          break;
        case 57:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeFixed32Size(number, 0); 
          break;
        case 58:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeBoolSize(number, true); 
          break;
        case 59:
          if (isOneofPresent(message, number, i)) {
            Object value = unsafe.getObject(message, offset);
            if (value instanceof ByteString) {
              size += CodedOutputStream.computeBytesSize(number, (ByteString)value);
              break;
            } 
            size += CodedOutputStream.computeStringSize(number, (String)value);
          } 
          break;
        case 60:
          if (isOneofPresent(message, number, i)) {
            Object value = unsafe.getObject(message, offset);
            size += SchemaUtil.computeSizeMessage(number, value, getMessageFieldSchema(i));
          } 
          break;
        case 61:
          if (isOneofPresent(message, number, i))
            size += 
              CodedOutputStream.computeBytesSize(number, (ByteString)unsafe
                .getObject(message, offset)); 
          break;
        case 62:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeUInt32Size(number, oneofIntAt(message, offset)); 
          break;
        case 63:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeEnumSize(number, oneofIntAt(message, offset)); 
          break;
        case 64:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeSFixed32Size(number, 0); 
          break;
        case 65:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeSFixed64Size(number, 0L); 
          break;
        case 66:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeSInt32Size(number, oneofIntAt(message, offset)); 
          break;
        case 67:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeSInt64Size(number, oneofLongAt(message, offset)); 
          break;
        case 68:
          if (isOneofPresent(message, number, i))
            size += 
              CodedOutputStream.computeGroupSize(number, (MessageLite)unsafe
                
                .getObject(message, offset), 
                getMessageFieldSchema(i)); 
          break;
      } 
    } 
    size += getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
    if (this.hasExtensions)
      size += this.extensionSchema.getExtensions(message).getSerializedSize(); 
    return size;
  }
  
  private int getSerializedSizeProto3(T message) {
    Unsafe unsafe = UNSAFE;
    int size = 0;
    for (int i = 0; i < this.buffer.length; i += 3) {
      int fieldSize, typeAndOffset = typeAndOffsetAt(i);
      int fieldType = type(typeAndOffset);
      int number = numberAt(i);
      long offset = offset(typeAndOffset);
      int cachedSizeOffset = (fieldType >= FieldType.DOUBLE_LIST_PACKED.id() && fieldType <= FieldType.SINT64_LIST_PACKED.id()) ? (this.buffer[i + 2] & 0xFFFFF) : 0;
      switch (fieldType) {
        case 0:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeDoubleSize(number, 0.0D); 
          break;
        case 1:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeFloatSize(number, 0.0F); 
          break;
        case 2:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeInt64Size(number, UnsafeUtil.getLong(message, offset)); 
          break;
        case 3:
          if (isFieldPresent(message, i))
            size += 
              CodedOutputStream.computeUInt64Size(number, UnsafeUtil.getLong(message, offset)); 
          break;
        case 4:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeInt32Size(number, UnsafeUtil.getInt(message, offset)); 
          break;
        case 5:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeFixed64Size(number, 0L); 
          break;
        case 6:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeFixed32Size(number, 0); 
          break;
        case 7:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeBoolSize(number, true); 
          break;
        case 8:
          if (isFieldPresent(message, i)) {
            Object value = UnsafeUtil.getObject(message, offset);
            if (value instanceof ByteString) {
              size += CodedOutputStream.computeBytesSize(number, (ByteString)value);
              break;
            } 
            size += CodedOutputStream.computeStringSize(number, (String)value);
          } 
          break;
        case 9:
          if (isFieldPresent(message, i)) {
            Object value = UnsafeUtil.getObject(message, offset);
            size += SchemaUtil.computeSizeMessage(number, value, getMessageFieldSchema(i));
          } 
          break;
        case 10:
          if (isFieldPresent(message, i)) {
            ByteString value = (ByteString)UnsafeUtil.getObject(message, offset);
            size += CodedOutputStream.computeBytesSize(number, value);
          } 
          break;
        case 11:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeUInt32Size(number, UnsafeUtil.getInt(message, offset)); 
          break;
        case 12:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeEnumSize(number, UnsafeUtil.getInt(message, offset)); 
          break;
        case 13:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeSFixed32Size(number, 0); 
          break;
        case 14:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeSFixed64Size(number, 0L); 
          break;
        case 15:
          if (isFieldPresent(message, i))
            size += CodedOutputStream.computeSInt32Size(number, UnsafeUtil.getInt(message, offset)); 
          break;
        case 16:
          if (isFieldPresent(message, i))
            size += 
              CodedOutputStream.computeSInt64Size(number, UnsafeUtil.getLong(message, offset)); 
          break;
        case 17:
          if (isFieldPresent(message, i))
            size += 
              CodedOutputStream.computeGroupSize(number, 
                
                (MessageLite)UnsafeUtil.getObject(message, offset), 
                getMessageFieldSchema(i)); 
          break;
        case 18:
          size += SchemaUtil.computeSizeFixed64List(number, listAt(message, offset), false);
          break;
        case 19:
          size += SchemaUtil.computeSizeFixed32List(number, listAt(message, offset), false);
          break;
        case 20:
          size += 
            SchemaUtil.computeSizeInt64List(number, (List)listAt(message, offset), false);
          break;
        case 21:
          size += 
            SchemaUtil.computeSizeUInt64List(number, (List)listAt(message, offset), false);
          break;
        case 22:
          size += 
            SchemaUtil.computeSizeInt32List(number, 
              (List)listAt(message, offset), false);
          break;
        case 23:
          size += SchemaUtil.computeSizeFixed64List(number, listAt(message, offset), false);
          break;
        case 24:
          size += SchemaUtil.computeSizeFixed32List(number, listAt(message, offset), false);
          break;
        case 25:
          size += SchemaUtil.computeSizeBoolList(number, listAt(message, offset), false);
          break;
        case 26:
          size += SchemaUtil.computeSizeStringList(number, listAt(message, offset));
          break;
        case 27:
          size += 
            SchemaUtil.computeSizeMessageList(number, 
              listAt(message, offset), getMessageFieldSchema(i));
          break;
        case 28:
          size += 
            SchemaUtil.computeSizeByteStringList(number, 
              (List)listAt(message, offset));
          break;
        case 29:
          size += 
            SchemaUtil.computeSizeUInt32List(number, 
              (List)listAt(message, offset), false);
          break;
        case 30:
          size += 
            SchemaUtil.computeSizeEnumList(number, 
              (List)listAt(message, offset), false);
          break;
        case 31:
          size += SchemaUtil.computeSizeFixed32List(number, listAt(message, offset), false);
          break;
        case 32:
          size += SchemaUtil.computeSizeFixed64List(number, listAt(message, offset), false);
          break;
        case 33:
          size += 
            SchemaUtil.computeSizeSInt32List(number, 
              (List)listAt(message, offset), false);
          break;
        case 34:
          size += 
            SchemaUtil.computeSizeSInt64List(number, (List)listAt(message, offset), false);
          break;
        case 35:
          fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 36:
          fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 37:
          fieldSize = SchemaUtil.computeSizeInt64ListNoTag((List<Long>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 38:
          fieldSize = SchemaUtil.computeSizeUInt64ListNoTag((List<Long>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 39:
          fieldSize = SchemaUtil.computeSizeInt32ListNoTag((List<Integer>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 40:
          fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 41:
          fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 42:
          fieldSize = SchemaUtil.computeSizeBoolListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 43:
          fieldSize = SchemaUtil.computeSizeUInt32ListNoTag((List<Integer>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 44:
          fieldSize = SchemaUtil.computeSizeEnumListNoTag((List<Integer>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 45:
          fieldSize = SchemaUtil.computeSizeFixed32ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 46:
          fieldSize = SchemaUtil.computeSizeFixed64ListNoTag((List)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 47:
          fieldSize = SchemaUtil.computeSizeSInt32ListNoTag((List<Integer>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 48:
          fieldSize = SchemaUtil.computeSizeSInt64ListNoTag((List<Long>)unsafe
              .getObject(message, offset));
          if (fieldSize > 0) {
            if (this.useCachedSizeField)
              unsafe.putInt(message, cachedSizeOffset, fieldSize); 
            size += 
              CodedOutputStream.computeTagSize(number) + 
              CodedOutputStream.computeUInt32SizeNoTag(fieldSize) + fieldSize;
          } 
          break;
        case 49:
          size += 
            SchemaUtil.computeSizeGroupList(number, 
              (List)listAt(message, offset), getMessageFieldSchema(i));
          break;
        case 50:
          size += this.mapFieldSchema
            .getSerializedSize(number, 
              UnsafeUtil.getObject(message, offset), getMapFieldDefaultEntry(i));
          break;
        case 51:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeDoubleSize(number, 0.0D); 
          break;
        case 52:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeFloatSize(number, 0.0F); 
          break;
        case 53:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeInt64Size(number, oneofLongAt(message, offset)); 
          break;
        case 54:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeUInt64Size(number, oneofLongAt(message, offset)); 
          break;
        case 55:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeInt32Size(number, oneofIntAt(message, offset)); 
          break;
        case 56:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeFixed64Size(number, 0L); 
          break;
        case 57:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeFixed32Size(number, 0); 
          break;
        case 58:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeBoolSize(number, true); 
          break;
        case 59:
          if (isOneofPresent(message, number, i)) {
            Object value = UnsafeUtil.getObject(message, offset);
            if (value instanceof ByteString) {
              size += CodedOutputStream.computeBytesSize(number, (ByteString)value);
              break;
            } 
            size += CodedOutputStream.computeStringSize(number, (String)value);
          } 
          break;
        case 60:
          if (isOneofPresent(message, number, i)) {
            Object value = UnsafeUtil.getObject(message, offset);
            size += SchemaUtil.computeSizeMessage(number, value, getMessageFieldSchema(i));
          } 
          break;
        case 61:
          if (isOneofPresent(message, number, i))
            size += 
              CodedOutputStream.computeBytesSize(number, 
                (ByteString)UnsafeUtil.getObject(message, offset)); 
          break;
        case 62:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeUInt32Size(number, oneofIntAt(message, offset)); 
          break;
        case 63:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeEnumSize(number, oneofIntAt(message, offset)); 
          break;
        case 64:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeSFixed32Size(number, 0); 
          break;
        case 65:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeSFixed64Size(number, 0L); 
          break;
        case 66:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeSInt32Size(number, oneofIntAt(message, offset)); 
          break;
        case 67:
          if (isOneofPresent(message, number, i))
            size += CodedOutputStream.computeSInt64Size(number, oneofLongAt(message, offset)); 
          break;
        case 68:
          if (isOneofPresent(message, number, i))
            size += 
              CodedOutputStream.computeGroupSize(number, 
                
                (MessageLite)UnsafeUtil.getObject(message, offset), 
                getMessageFieldSchema(i)); 
          break;
      } 
    } 
    size += getUnknownFieldsSerializedSize(this.unknownFieldSchema, message);
    return size;
  }
  
  private <UT, UB> int getUnknownFieldsSerializedSize(UnknownFieldSchema<UT, UB> schema, T message) {
    UT unknowns = schema.getFromMessage(message);
    return schema.getSerializedSize(unknowns);
  }
  
  private static List<?> listAt(Object message, long offset) {
    return (List)UnsafeUtil.getObject(message, offset);
  }
  
  public void writeTo(T message, Writer writer) throws IOException {
    if (writer.fieldOrder() == Writer.FieldOrder.DESCENDING) {
      writeFieldsInDescendingOrder(message, writer);
    } else if (this.proto3) {
      writeFieldsInAscendingOrderProto3(message, writer);
    } else {
      writeFieldsInAscendingOrderProto2(message, writer);
    } 
  }
  
  private void writeFieldsInAscendingOrderProto2(T message, Writer writer) throws IOException {
    Iterator<? extends Map.Entry<?, ?>> extensionIterator = null;
    Map.Entry<?, ?> nextExtension = null;
    if (this.hasExtensions) {
      FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
      if (!extensions.isEmpty()) {
        extensionIterator = extensions.iterator();
        nextExtension = extensionIterator.next();
      } 
    } 
    int currentPresenceFieldOffset = 1048575;
    int currentPresenceField = 0;
    int bufferLength = this.buffer.length;
    Unsafe unsafe = UNSAFE;
    for (int pos = 0; pos < bufferLength; pos += 3) {
      int typeAndOffset = typeAndOffsetAt(pos);
      int number = numberAt(pos);
      int fieldType = type(typeAndOffset);
      int presenceMaskAndOffset = 0;
      int presenceMask = 0;
      if (fieldType <= 17) {
        presenceMaskAndOffset = this.buffer[pos + 2];
        int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
        if (presenceFieldOffset != currentPresenceFieldOffset) {
          currentPresenceFieldOffset = presenceFieldOffset;
          currentPresenceField = unsafe.getInt(message, presenceFieldOffset);
        } 
        presenceMask = 1 << presenceMaskAndOffset >>> 20;
      } 
      while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) <= number) {
        this.extensionSchema.serializeExtension(writer, nextExtension);
        nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
      } 
      long offset = offset(typeAndOffset);
      switch (fieldType) {
        case 0:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeDouble(number, doubleAt(message, offset)); 
          break;
        case 1:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeFloat(number, floatAt(message, offset)); 
          break;
        case 2:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeInt64(number, unsafe.getLong(message, offset)); 
          break;
        case 3:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeUInt64(number, unsafe.getLong(message, offset)); 
          break;
        case 4:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeInt32(number, unsafe.getInt(message, offset)); 
          break;
        case 5:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeFixed64(number, unsafe.getLong(message, offset)); 
          break;
        case 6:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeFixed32(number, unsafe.getInt(message, offset)); 
          break;
        case 7:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeBool(number, booleanAt(message, offset)); 
          break;
        case 8:
          if ((currentPresenceField & presenceMask) != 0)
            writeString(number, unsafe.getObject(message, offset), writer); 
          break;
        case 9:
          if ((currentPresenceField & presenceMask) != 0) {
            Object value = unsafe.getObject(message, offset);
            writer.writeMessage(number, value, getMessageFieldSchema(pos));
          } 
          break;
        case 10:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeBytes(number, (ByteString)unsafe.getObject(message, offset)); 
          break;
        case 11:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeUInt32(number, unsafe.getInt(message, offset)); 
          break;
        case 12:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeEnum(number, unsafe.getInt(message, offset)); 
          break;
        case 13:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeSFixed32(number, unsafe.getInt(message, offset)); 
          break;
        case 14:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeSFixed64(number, unsafe.getLong(message, offset)); 
          break;
        case 15:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeSInt32(number, unsafe.getInt(message, offset)); 
          break;
        case 16:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeSInt64(number, unsafe.getLong(message, offset)); 
          break;
        case 17:
          if ((currentPresenceField & presenceMask) != 0)
            writer.writeGroup(number, unsafe
                .getObject(message, offset), getMessageFieldSchema(pos)); 
          break;
        case 18:
          SchemaUtil.writeDoubleList(
              numberAt(pos), (List<Double>)unsafe.getObject(message, offset), writer, false);
          break;
        case 19:
          SchemaUtil.writeFloatList(
              numberAt(pos), (List<Float>)unsafe.getObject(message, offset), writer, false);
          break;
        case 20:
          SchemaUtil.writeInt64List(
              numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, false);
          break;
        case 21:
          SchemaUtil.writeUInt64List(
              numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, false);
          break;
        case 22:
          SchemaUtil.writeInt32List(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
          break;
        case 23:
          SchemaUtil.writeFixed64List(
              numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, false);
          break;
        case 24:
          SchemaUtil.writeFixed32List(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
          break;
        case 25:
          SchemaUtil.writeBoolList(
              numberAt(pos), (List<Boolean>)unsafe.getObject(message, offset), writer, false);
          break;
        case 26:
          SchemaUtil.writeStringList(
              numberAt(pos), (List<String>)unsafe.getObject(message, offset), writer);
          break;
        case 27:
          SchemaUtil.writeMessageList(
              numberAt(pos), (List)unsafe
              .getObject(message, offset), writer, 
              
              getMessageFieldSchema(pos));
          break;
        case 28:
          SchemaUtil.writeBytesList(
              numberAt(pos), (List<ByteString>)unsafe.getObject(message, offset), writer);
          break;
        case 29:
          SchemaUtil.writeUInt32List(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
          break;
        case 30:
          SchemaUtil.writeEnumList(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
          break;
        case 31:
          SchemaUtil.writeSFixed32List(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
          break;
        case 32:
          SchemaUtil.writeSFixed64List(
              numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, false);
          break;
        case 33:
          SchemaUtil.writeSInt32List(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, false);
          break;
        case 34:
          SchemaUtil.writeSInt64List(
              numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, false);
          break;
        case 35:
          SchemaUtil.writeDoubleList(
              numberAt(pos), (List<Double>)unsafe.getObject(message, offset), writer, true);
          break;
        case 36:
          SchemaUtil.writeFloatList(
              numberAt(pos), (List<Float>)unsafe.getObject(message, offset), writer, true);
          break;
        case 37:
          SchemaUtil.writeInt64List(
              numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, true);
          break;
        case 38:
          SchemaUtil.writeUInt64List(
              numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, true);
          break;
        case 39:
          SchemaUtil.writeInt32List(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
          break;
        case 40:
          SchemaUtil.writeFixed64List(
              numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, true);
          break;
        case 41:
          SchemaUtil.writeFixed32List(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
          break;
        case 42:
          SchemaUtil.writeBoolList(
              numberAt(pos), (List<Boolean>)unsafe.getObject(message, offset), writer, true);
          break;
        case 43:
          SchemaUtil.writeUInt32List(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
          break;
        case 44:
          SchemaUtil.writeEnumList(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
          break;
        case 45:
          SchemaUtil.writeSFixed32List(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
          break;
        case 46:
          SchemaUtil.writeSFixed64List(
              numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, true);
          break;
        case 47:
          SchemaUtil.writeSInt32List(
              numberAt(pos), (List<Integer>)unsafe.getObject(message, offset), writer, true);
          break;
        case 48:
          SchemaUtil.writeSInt64List(
              numberAt(pos), (List<Long>)unsafe.getObject(message, offset), writer, true);
          break;
        case 49:
          SchemaUtil.writeGroupList(
              numberAt(pos), (List)unsafe
              .getObject(message, offset), writer, 
              
              getMessageFieldSchema(pos));
          break;
        case 50:
          writeMapHelper(writer, number, unsafe.getObject(message, offset), pos);
          break;
        case 51:
          if (isOneofPresent(message, number, pos))
            writer.writeDouble(number, oneofDoubleAt(message, offset)); 
          break;
        case 52:
          if (isOneofPresent(message, number, pos))
            writer.writeFloat(number, oneofFloatAt(message, offset)); 
          break;
        case 53:
          if (isOneofPresent(message, number, pos))
            writer.writeInt64(number, oneofLongAt(message, offset)); 
          break;
        case 54:
          if (isOneofPresent(message, number, pos))
            writer.writeUInt64(number, oneofLongAt(message, offset)); 
          break;
        case 55:
          if (isOneofPresent(message, number, pos))
            writer.writeInt32(number, oneofIntAt(message, offset)); 
          break;
        case 56:
          if (isOneofPresent(message, number, pos))
            writer.writeFixed64(number, oneofLongAt(message, offset)); 
          break;
        case 57:
          if (isOneofPresent(message, number, pos))
            writer.writeFixed32(number, oneofIntAt(message, offset)); 
          break;
        case 58:
          if (isOneofPresent(message, number, pos))
            writer.writeBool(number, oneofBooleanAt(message, offset)); 
          break;
        case 59:
          if (isOneofPresent(message, number, pos))
            writeString(number, unsafe.getObject(message, offset), writer); 
          break;
        case 60:
          if (isOneofPresent(message, number, pos)) {
            Object value = unsafe.getObject(message, offset);
            writer.writeMessage(number, value, getMessageFieldSchema(pos));
          } 
          break;
        case 61:
          if (isOneofPresent(message, number, pos))
            writer.writeBytes(number, (ByteString)unsafe.getObject(message, offset)); 
          break;
        case 62:
          if (isOneofPresent(message, number, pos))
            writer.writeUInt32(number, oneofIntAt(message, offset)); 
          break;
        case 63:
          if (isOneofPresent(message, number, pos))
            writer.writeEnum(number, oneofIntAt(message, offset)); 
          break;
        case 64:
          if (isOneofPresent(message, number, pos))
            writer.writeSFixed32(number, oneofIntAt(message, offset)); 
          break;
        case 65:
          if (isOneofPresent(message, number, pos))
            writer.writeSFixed64(number, oneofLongAt(message, offset)); 
          break;
        case 66:
          if (isOneofPresent(message, number, pos))
            writer.writeSInt32(number, oneofIntAt(message, offset)); 
          break;
        case 67:
          if (isOneofPresent(message, number, pos))
            writer.writeSInt64(number, oneofLongAt(message, offset)); 
          break;
        case 68:
          if (isOneofPresent(message, number, pos))
            writer.writeGroup(number, unsafe
                .getObject(message, offset), getMessageFieldSchema(pos)); 
          break;
      } 
    } 
    while (nextExtension != null) {
      this.extensionSchema.serializeExtension(writer, nextExtension);
      nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
    } 
    writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
  }
  
  private void writeFieldsInAscendingOrderProto3(T message, Writer writer) throws IOException {
    Iterator<? extends Map.Entry<?, ?>> extensionIterator = null;
    Map.Entry<?, ?> nextExtension = null;
    if (this.hasExtensions) {
      FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
      if (!extensions.isEmpty()) {
        extensionIterator = extensions.iterator();
        nextExtension = extensionIterator.next();
      } 
    } 
    int bufferLength = this.buffer.length;
    for (int pos = 0; pos < bufferLength; pos += 3) {
      int typeAndOffset = typeAndOffsetAt(pos);
      int number = numberAt(pos);
      while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) <= number) {
        this.extensionSchema.serializeExtension(writer, nextExtension);
        nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
      } 
      switch (type(typeAndOffset)) {
        case 0:
          if (isFieldPresent(message, pos))
            writer.writeDouble(number, doubleAt(message, offset(typeAndOffset))); 
          break;
        case 1:
          if (isFieldPresent(message, pos))
            writer.writeFloat(number, floatAt(message, offset(typeAndOffset))); 
          break;
        case 2:
          if (isFieldPresent(message, pos))
            writer.writeInt64(number, longAt(message, offset(typeAndOffset))); 
          break;
        case 3:
          if (isFieldPresent(message, pos))
            writer.writeUInt64(number, longAt(message, offset(typeAndOffset))); 
          break;
        case 4:
          if (isFieldPresent(message, pos))
            writer.writeInt32(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 5:
          if (isFieldPresent(message, pos))
            writer.writeFixed64(number, longAt(message, offset(typeAndOffset))); 
          break;
        case 6:
          if (isFieldPresent(message, pos))
            writer.writeFixed32(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 7:
          if (isFieldPresent(message, pos))
            writer.writeBool(number, booleanAt(message, offset(typeAndOffset))); 
          break;
        case 8:
          if (isFieldPresent(message, pos))
            writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer); 
          break;
        case 9:
          if (isFieldPresent(message, pos)) {
            Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
            writer.writeMessage(number, value, getMessageFieldSchema(pos));
          } 
          break;
        case 10:
          if (isFieldPresent(message, pos))
            writer.writeBytes(number, 
                (ByteString)UnsafeUtil.getObject(message, offset(typeAndOffset))); 
          break;
        case 11:
          if (isFieldPresent(message, pos))
            writer.writeUInt32(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 12:
          if (isFieldPresent(message, pos))
            writer.writeEnum(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 13:
          if (isFieldPresent(message, pos))
            writer.writeSFixed32(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 14:
          if (isFieldPresent(message, pos))
            writer.writeSFixed64(number, longAt(message, offset(typeAndOffset))); 
          break;
        case 15:
          if (isFieldPresent(message, pos))
            writer.writeSInt32(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 16:
          if (isFieldPresent(message, pos))
            writer.writeSInt64(number, longAt(message, offset(typeAndOffset))); 
          break;
        case 17:
          if (isFieldPresent(message, pos))
            writer.writeGroup(number, 
                
                UnsafeUtil.getObject(message, offset(typeAndOffset)), 
                getMessageFieldSchema(pos)); 
          break;
        case 18:
          SchemaUtil.writeDoubleList(
              numberAt(pos), 
              (List<Double>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 19:
          SchemaUtil.writeFloatList(
              numberAt(pos), 
              (List<Float>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 20:
          SchemaUtil.writeInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 21:
          SchemaUtil.writeUInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 22:
          SchemaUtil.writeInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 23:
          SchemaUtil.writeFixed64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 24:
          SchemaUtil.writeFixed32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 25:
          SchemaUtil.writeBoolList(
              numberAt(pos), 
              (List<Boolean>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 26:
          SchemaUtil.writeStringList(
              numberAt(pos), 
              (List<String>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
          break;
        case 27:
          SchemaUtil.writeMessageList(
              numberAt(pos), 
              (List)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, 
              
              getMessageFieldSchema(pos));
          break;
        case 28:
          SchemaUtil.writeBytesList(
              numberAt(pos), 
              (List<ByteString>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
          break;
        case 29:
          SchemaUtil.writeUInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 30:
          SchemaUtil.writeEnumList(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 31:
          SchemaUtil.writeSFixed32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 32:
          SchemaUtil.writeSFixed64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 33:
          SchemaUtil.writeSInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 34:
          SchemaUtil.writeSInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 35:
          SchemaUtil.writeDoubleList(
              numberAt(pos), 
              (List<Double>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 36:
          SchemaUtil.writeFloatList(
              numberAt(pos), 
              (List<Float>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 37:
          SchemaUtil.writeInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 38:
          SchemaUtil.writeUInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 39:
          SchemaUtil.writeInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 40:
          SchemaUtil.writeFixed64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 41:
          SchemaUtil.writeFixed32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 42:
          SchemaUtil.writeBoolList(
              numberAt(pos), 
              (List<Boolean>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 43:
          SchemaUtil.writeUInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 44:
          SchemaUtil.writeEnumList(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 45:
          SchemaUtil.writeSFixed32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 46:
          SchemaUtil.writeSFixed64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 47:
          SchemaUtil.writeSInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 48:
          SchemaUtil.writeSInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 49:
          SchemaUtil.writeGroupList(
              numberAt(pos), 
              (List)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, 
              
              getMessageFieldSchema(pos));
          break;
        case 50:
          writeMapHelper(writer, number, UnsafeUtil.getObject(message, offset(typeAndOffset)), pos);
          break;
        case 51:
          if (isOneofPresent(message, number, pos))
            writer.writeDouble(number, oneofDoubleAt(message, offset(typeAndOffset))); 
          break;
        case 52:
          if (isOneofPresent(message, number, pos))
            writer.writeFloat(number, oneofFloatAt(message, offset(typeAndOffset))); 
          break;
        case 53:
          if (isOneofPresent(message, number, pos))
            writer.writeInt64(number, oneofLongAt(message, offset(typeAndOffset))); 
          break;
        case 54:
          if (isOneofPresent(message, number, pos))
            writer.writeUInt64(number, oneofLongAt(message, offset(typeAndOffset))); 
          break;
        case 55:
          if (isOneofPresent(message, number, pos))
            writer.writeInt32(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 56:
          if (isOneofPresent(message, number, pos))
            writer.writeFixed64(number, oneofLongAt(message, offset(typeAndOffset))); 
          break;
        case 57:
          if (isOneofPresent(message, number, pos))
            writer.writeFixed32(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 58:
          if (isOneofPresent(message, number, pos))
            writer.writeBool(number, oneofBooleanAt(message, offset(typeAndOffset))); 
          break;
        case 59:
          if (isOneofPresent(message, number, pos))
            writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer); 
          break;
        case 60:
          if (isOneofPresent(message, number, pos)) {
            Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
            writer.writeMessage(number, value, getMessageFieldSchema(pos));
          } 
          break;
        case 61:
          if (isOneofPresent(message, number, pos))
            writer.writeBytes(number, 
                (ByteString)UnsafeUtil.getObject(message, offset(typeAndOffset))); 
          break;
        case 62:
          if (isOneofPresent(message, number, pos))
            writer.writeUInt32(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 63:
          if (isOneofPresent(message, number, pos))
            writer.writeEnum(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 64:
          if (isOneofPresent(message, number, pos))
            writer.writeSFixed32(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 65:
          if (isOneofPresent(message, number, pos))
            writer.writeSFixed64(number, oneofLongAt(message, offset(typeAndOffset))); 
          break;
        case 66:
          if (isOneofPresent(message, number, pos))
            writer.writeSInt32(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 67:
          if (isOneofPresent(message, number, pos))
            writer.writeSInt64(number, oneofLongAt(message, offset(typeAndOffset))); 
          break;
        case 68:
          if (isOneofPresent(message, number, pos))
            writer.writeGroup(number, 
                
                UnsafeUtil.getObject(message, offset(typeAndOffset)), 
                getMessageFieldSchema(pos)); 
          break;
      } 
    } 
    while (nextExtension != null) {
      this.extensionSchema.serializeExtension(writer, nextExtension);
      nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
    } 
    writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
  }
  
  private void writeFieldsInDescendingOrder(T message, Writer writer) throws IOException {
    writeUnknownInMessageTo(this.unknownFieldSchema, message, writer);
    Iterator<? extends Map.Entry<?, ?>> extensionIterator = null;
    Map.Entry<?, ?> nextExtension = null;
    if (this.hasExtensions) {
      FieldSet<?> extensions = this.extensionSchema.getExtensions(message);
      if (!extensions.isEmpty()) {
        extensionIterator = extensions.descendingIterator();
        nextExtension = extensionIterator.next();
      } 
    } 
    for (int pos = this.buffer.length - 3; pos >= 0; pos -= 3) {
      int typeAndOffset = typeAndOffsetAt(pos);
      int number = numberAt(pos);
      while (nextExtension != null && this.extensionSchema.extensionNumber(nextExtension) > number) {
        this.extensionSchema.serializeExtension(writer, nextExtension);
        nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
      } 
      switch (type(typeAndOffset)) {
        case 0:
          if (isFieldPresent(message, pos))
            writer.writeDouble(number, doubleAt(message, offset(typeAndOffset))); 
          break;
        case 1:
          if (isFieldPresent(message, pos))
            writer.writeFloat(number, floatAt(message, offset(typeAndOffset))); 
          break;
        case 2:
          if (isFieldPresent(message, pos))
            writer.writeInt64(number, longAt(message, offset(typeAndOffset))); 
          break;
        case 3:
          if (isFieldPresent(message, pos))
            writer.writeUInt64(number, longAt(message, offset(typeAndOffset))); 
          break;
        case 4:
          if (isFieldPresent(message, pos))
            writer.writeInt32(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 5:
          if (isFieldPresent(message, pos))
            writer.writeFixed64(number, longAt(message, offset(typeAndOffset))); 
          break;
        case 6:
          if (isFieldPresent(message, pos))
            writer.writeFixed32(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 7:
          if (isFieldPresent(message, pos))
            writer.writeBool(number, booleanAt(message, offset(typeAndOffset))); 
          break;
        case 8:
          if (isFieldPresent(message, pos))
            writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer); 
          break;
        case 9:
          if (isFieldPresent(message, pos)) {
            Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
            writer.writeMessage(number, value, getMessageFieldSchema(pos));
          } 
          break;
        case 10:
          if (isFieldPresent(message, pos))
            writer.writeBytes(number, 
                (ByteString)UnsafeUtil.getObject(message, offset(typeAndOffset))); 
          break;
        case 11:
          if (isFieldPresent(message, pos))
            writer.writeUInt32(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 12:
          if (isFieldPresent(message, pos))
            writer.writeEnum(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 13:
          if (isFieldPresent(message, pos))
            writer.writeSFixed32(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 14:
          if (isFieldPresent(message, pos))
            writer.writeSFixed64(number, longAt(message, offset(typeAndOffset))); 
          break;
        case 15:
          if (isFieldPresent(message, pos))
            writer.writeSInt32(number, intAt(message, offset(typeAndOffset))); 
          break;
        case 16:
          if (isFieldPresent(message, pos))
            writer.writeSInt64(number, longAt(message, offset(typeAndOffset))); 
          break;
        case 17:
          if (isFieldPresent(message, pos))
            writer.writeGroup(number, 
                
                UnsafeUtil.getObject(message, offset(typeAndOffset)), 
                getMessageFieldSchema(pos)); 
          break;
        case 18:
          SchemaUtil.writeDoubleList(
              numberAt(pos), 
              (List<Double>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 19:
          SchemaUtil.writeFloatList(
              numberAt(pos), 
              (List<Float>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 20:
          SchemaUtil.writeInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 21:
          SchemaUtil.writeUInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 22:
          SchemaUtil.writeInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 23:
          SchemaUtil.writeFixed64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 24:
          SchemaUtil.writeFixed32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 25:
          SchemaUtil.writeBoolList(
              numberAt(pos), 
              (List<Boolean>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 26:
          SchemaUtil.writeStringList(
              numberAt(pos), 
              (List<String>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
          break;
        case 27:
          SchemaUtil.writeMessageList(
              numberAt(pos), 
              (List)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, 
              
              getMessageFieldSchema(pos));
          break;
        case 28:
          SchemaUtil.writeBytesList(
              numberAt(pos), 
              (List<ByteString>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer);
          break;
        case 29:
          SchemaUtil.writeUInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 30:
          SchemaUtil.writeEnumList(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 31:
          SchemaUtil.writeSFixed32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 32:
          SchemaUtil.writeSFixed64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 33:
          SchemaUtil.writeSInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 34:
          SchemaUtil.writeSInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, false);
          break;
        case 35:
          SchemaUtil.writeDoubleList(
              numberAt(pos), 
              (List<Double>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 36:
          SchemaUtil.writeFloatList(
              numberAt(pos), 
              (List<Float>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 37:
          SchemaUtil.writeInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 38:
          SchemaUtil.writeUInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 39:
          SchemaUtil.writeInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 40:
          SchemaUtil.writeFixed64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 41:
          SchemaUtil.writeFixed32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 42:
          SchemaUtil.writeBoolList(
              numberAt(pos), 
              (List<Boolean>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 43:
          SchemaUtil.writeUInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 44:
          SchemaUtil.writeEnumList(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 45:
          SchemaUtil.writeSFixed32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 46:
          SchemaUtil.writeSFixed64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 47:
          SchemaUtil.writeSInt32List(
              numberAt(pos), 
              (List<Integer>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 48:
          SchemaUtil.writeSInt64List(
              numberAt(pos), 
              (List<Long>)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, true);
          break;
        case 49:
          SchemaUtil.writeGroupList(
              numberAt(pos), 
              (List)UnsafeUtil.getObject(message, offset(typeAndOffset)), writer, 
              
              getMessageFieldSchema(pos));
          break;
        case 50:
          writeMapHelper(writer, number, UnsafeUtil.getObject(message, offset(typeAndOffset)), pos);
          break;
        case 51:
          if (isOneofPresent(message, number, pos))
            writer.writeDouble(number, oneofDoubleAt(message, offset(typeAndOffset))); 
          break;
        case 52:
          if (isOneofPresent(message, number, pos))
            writer.writeFloat(number, oneofFloatAt(message, offset(typeAndOffset))); 
          break;
        case 53:
          if (isOneofPresent(message, number, pos))
            writer.writeInt64(number, oneofLongAt(message, offset(typeAndOffset))); 
          break;
        case 54:
          if (isOneofPresent(message, number, pos))
            writer.writeUInt64(number, oneofLongAt(message, offset(typeAndOffset))); 
          break;
        case 55:
          if (isOneofPresent(message, number, pos))
            writer.writeInt32(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 56:
          if (isOneofPresent(message, number, pos))
            writer.writeFixed64(number, oneofLongAt(message, offset(typeAndOffset))); 
          break;
        case 57:
          if (isOneofPresent(message, number, pos))
            writer.writeFixed32(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 58:
          if (isOneofPresent(message, number, pos))
            writer.writeBool(number, oneofBooleanAt(message, offset(typeAndOffset))); 
          break;
        case 59:
          if (isOneofPresent(message, number, pos))
            writeString(number, UnsafeUtil.getObject(message, offset(typeAndOffset)), writer); 
          break;
        case 60:
          if (isOneofPresent(message, number, pos)) {
            Object value = UnsafeUtil.getObject(message, offset(typeAndOffset));
            writer.writeMessage(number, value, getMessageFieldSchema(pos));
          } 
          break;
        case 61:
          if (isOneofPresent(message, number, pos))
            writer.writeBytes(number, 
                (ByteString)UnsafeUtil.getObject(message, offset(typeAndOffset))); 
          break;
        case 62:
          if (isOneofPresent(message, number, pos))
            writer.writeUInt32(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 63:
          if (isOneofPresent(message, number, pos))
            writer.writeEnum(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 64:
          if (isOneofPresent(message, number, pos))
            writer.writeSFixed32(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 65:
          if (isOneofPresent(message, number, pos))
            writer.writeSFixed64(number, oneofLongAt(message, offset(typeAndOffset))); 
          break;
        case 66:
          if (isOneofPresent(message, number, pos))
            writer.writeSInt32(number, oneofIntAt(message, offset(typeAndOffset))); 
          break;
        case 67:
          if (isOneofPresent(message, number, pos))
            writer.writeSInt64(number, oneofLongAt(message, offset(typeAndOffset))); 
          break;
        case 68:
          if (isOneofPresent(message, number, pos))
            writer.writeGroup(number, 
                
                UnsafeUtil.getObject(message, offset(typeAndOffset)), 
                getMessageFieldSchema(pos)); 
          break;
      } 
    } 
    while (nextExtension != null) {
      this.extensionSchema.serializeExtension(writer, nextExtension);
      nextExtension = extensionIterator.hasNext() ? extensionIterator.next() : null;
    } 
  }
  
  private <K, V> void writeMapHelper(Writer writer, int number, Object mapField, int pos) throws IOException {
    if (mapField != null)
      writer.writeMap(number, this.mapFieldSchema
          
          .forMapMetadata(getMapFieldDefaultEntry(pos)), this.mapFieldSchema
          .forMapData(mapField)); 
  }
  
  private <UT, UB> void writeUnknownInMessageTo(UnknownFieldSchema<UT, UB> schema, T message, Writer writer) throws IOException {
    schema.writeTo(schema.getFromMessage(message), writer);
  }
  
  public void mergeFrom(T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
    if (extensionRegistry == null)
      throw new NullPointerException(); 
    checkMutable(message);
    mergeFromHelper(this.unknownFieldSchema, this.extensionSchema, message, reader, extensionRegistry);
  }
  
  private <UT, UB, ET extends FieldSet.FieldDescriptorLite<ET>> void mergeFromHelper(UnknownFieldSchema<UT, UB> unknownFieldSchema, ExtensionSchema<ET> extensionSchema, T message, Reader reader, ExtensionRegistryLite extensionRegistry) throws IOException {
    UB unknownFields = null;
    FieldSet<ET> extensions = null;
    try {
      while (true) {
        int number = reader.getFieldNumber();
        int pos = positionForFieldNumber(number);
        if (pos < 0) {
          if (number == Integer.MAX_VALUE)
            return; 
          Object extension = !this.hasExtensions ? null : extensionSchema.findExtensionByNumber(extensionRegistry, this.defaultInstance, number);
          if (extension != null) {
            if (extensions == null)
              extensions = extensionSchema.getMutableExtensions(message); 
            unknownFields = extensionSchema.parseExtension(message, reader, extension, extensionRegistry, extensions, unknownFields, unknownFieldSchema);
            continue;
          } 
          if (unknownFieldSchema.shouldDiscardUnknownFields(reader)) {
            if (reader.skipField())
              continue; 
          } else {
            if (unknownFields == null)
              unknownFields = unknownFieldSchema.getBuilderFromMessage(message); 
            if (unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader))
              continue; 
          } 
          return;
        } 
        int typeAndOffset = typeAndOffsetAt(pos);
        try {
          MessageLite messageLite3;
          int j;
          MessageLite messageLite2;
          List<Integer> enumList;
          MessageLite messageLite1;
          int enumValue;
          MessageLite current;
          Internal.EnumVerifier enumVerifier;
          switch (type(typeAndOffset)) {
            case 0:
              UnsafeUtil.putDouble(message, offset(typeAndOffset), reader.readDouble());
              setFieldPresent(message, pos);
              continue;
            case 1:
              UnsafeUtil.putFloat(message, offset(typeAndOffset), reader.readFloat());
              setFieldPresent(message, pos);
              continue;
            case 2:
              UnsafeUtil.putLong(message, offset(typeAndOffset), reader.readInt64());
              setFieldPresent(message, pos);
              continue;
            case 3:
              UnsafeUtil.putLong(message, offset(typeAndOffset), reader.readUInt64());
              setFieldPresent(message, pos);
              continue;
            case 4:
              UnsafeUtil.putInt(message, offset(typeAndOffset), reader.readInt32());
              setFieldPresent(message, pos);
              continue;
            case 5:
              UnsafeUtil.putLong(message, offset(typeAndOffset), reader.readFixed64());
              setFieldPresent(message, pos);
              continue;
            case 6:
              UnsafeUtil.putInt(message, offset(typeAndOffset), reader.readFixed32());
              setFieldPresent(message, pos);
              continue;
            case 7:
              UnsafeUtil.putBoolean(message, offset(typeAndOffset), reader.readBool());
              setFieldPresent(message, pos);
              continue;
            case 8:
              readString(message, typeAndOffset, reader);
              setFieldPresent(message, pos);
              continue;
            case 9:
              messageLite3 = (MessageLite)mutableMessageFieldForMerge(message, pos);
              reader.mergeMessageField(messageLite3, 
                  getMessageFieldSchema(pos), extensionRegistry);
              storeMessageField(message, pos, messageLite3);
              continue;
            case 10:
              UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readBytes());
              setFieldPresent(message, pos);
              continue;
            case 11:
              UnsafeUtil.putInt(message, offset(typeAndOffset), reader.readUInt32());
              setFieldPresent(message, pos);
              continue;
            case 12:
              j = reader.readEnum();
              enumVerifier = getEnumFieldVerifier(pos);
              if (enumVerifier == null || enumVerifier.isInRange(j)) {
                UnsafeUtil.putInt(message, offset(typeAndOffset), j);
                setFieldPresent(message, pos);
                continue;
              } 
              unknownFields = SchemaUtil.storeUnknownEnum(message, number, j, unknownFields, unknownFieldSchema);
              continue;
            case 13:
              UnsafeUtil.putInt(message, offset(typeAndOffset), reader.readSFixed32());
              setFieldPresent(message, pos);
              continue;
            case 14:
              UnsafeUtil.putLong(message, offset(typeAndOffset), reader.readSFixed64());
              setFieldPresent(message, pos);
              continue;
            case 15:
              UnsafeUtil.putInt(message, offset(typeAndOffset), reader.readSInt32());
              setFieldPresent(message, pos);
              continue;
            case 16:
              UnsafeUtil.putLong(message, offset(typeAndOffset), reader.readSInt64());
              setFieldPresent(message, pos);
              continue;
            case 17:
              messageLite2 = (MessageLite)mutableMessageFieldForMerge(message, pos);
              reader.mergeGroupField(messageLite2, 
                  getMessageFieldSchema(pos), extensionRegistry);
              storeMessageField(message, pos, messageLite2);
              continue;
            case 18:
              reader.readDoubleList(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 19:
              reader.readFloatList(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 20:
              reader.readInt64List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 21:
              reader.readUInt64List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 22:
              reader.readInt32List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 23:
              reader.readFixed64List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 24:
              reader.readFixed32List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 25:
              reader.readBoolList(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 26:
              readStringList(message, typeAndOffset, reader);
              continue;
            case 27:
              readMessageList(message, typeAndOffset, reader, 
                  
                  getMessageFieldSchema(pos), extensionRegistry);
              continue;
            case 28:
              reader.readBytesList(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 29:
              reader.readUInt32List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 30:
              enumList = this.listFieldSchema.mutableListAt(message, offset(typeAndOffset));
              reader.readEnumList(enumList);
              unknownFields = SchemaUtil.filterUnknownEnumList(message, number, enumList, 
                  
                  getEnumFieldVerifier(pos), unknownFields, unknownFieldSchema);
              continue;
            case 31:
              reader.readSFixed32List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 32:
              reader.readSFixed64List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 33:
              reader.readSInt32List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 34:
              reader.readSInt64List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 35:
              reader.readDoubleList(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 36:
              reader.readFloatList(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 37:
              reader.readInt64List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 38:
              reader.readUInt64List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 39:
              reader.readInt32List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 40:
              reader.readFixed64List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 41:
              reader.readFixed32List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 42:
              reader.readBoolList(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 43:
              reader.readUInt32List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 44:
              enumList = this.listFieldSchema.mutableListAt(message, offset(typeAndOffset));
              reader.readEnumList(enumList);
              unknownFields = SchemaUtil.filterUnknownEnumList(message, number, enumList, 
                  
                  getEnumFieldVerifier(pos), unknownFields, unknownFieldSchema);
              continue;
            case 45:
              reader.readSFixed32List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 46:
              reader.readSFixed64List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 47:
              reader.readSInt32List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 48:
              reader.readSInt64List(this.listFieldSchema
                  .mutableListAt(message, offset(typeAndOffset)));
              continue;
            case 49:
              readGroupList(message, 
                  
                  offset(typeAndOffset), reader, 
                  
                  getMessageFieldSchema(pos), extensionRegistry);
              continue;
            case 50:
              mergeMap(message, pos, getMapFieldDefaultEntry(pos), extensionRegistry, reader);
              continue;
            case 51:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Double.valueOf(reader.readDouble()));
              setOneofPresent(message, number, pos);
              continue;
            case 52:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Float.valueOf(reader.readFloat()));
              setOneofPresent(message, number, pos);
              continue;
            case 53:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Long.valueOf(reader.readInt64()));
              setOneofPresent(message, number, pos);
              continue;
            case 54:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Long.valueOf(reader.readUInt64()));
              setOneofPresent(message, number, pos);
              continue;
            case 55:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Integer.valueOf(reader.readInt32()));
              setOneofPresent(message, number, pos);
              continue;
            case 56:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Long.valueOf(reader.readFixed64()));
              setOneofPresent(message, number, pos);
              continue;
            case 57:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Integer.valueOf(reader.readFixed32()));
              setOneofPresent(message, number, pos);
              continue;
            case 58:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Boolean.valueOf(reader.readBool()));
              setOneofPresent(message, number, pos);
              continue;
            case 59:
              readString(message, typeAndOffset, reader);
              setOneofPresent(message, number, pos);
              continue;
            case 60:
              messageLite1 = (MessageLite)mutableOneofMessageFieldForMerge(message, number, pos);
              reader.mergeMessageField(messageLite1, 
                  getMessageFieldSchema(pos), extensionRegistry);
              storeOneofMessageField(message, number, pos, messageLite1);
              continue;
            case 61:
              UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readBytes());
              setOneofPresent(message, number, pos);
              continue;
            case 62:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Integer.valueOf(reader.readUInt32()));
              setOneofPresent(message, number, pos);
              continue;
            case 63:
              enumValue = reader.readEnum();
              enumVerifier = getEnumFieldVerifier(pos);
              if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
                UnsafeUtil.putObject(message, offset(typeAndOffset), Integer.valueOf(enumValue));
                setOneofPresent(message, number, pos);
                continue;
              } 
              unknownFields = SchemaUtil.storeUnknownEnum(message, number, enumValue, unknownFields, unknownFieldSchema);
              continue;
            case 64:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Integer.valueOf(reader.readSFixed32()));
              setOneofPresent(message, number, pos);
              continue;
            case 65:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Long.valueOf(reader.readSFixed64()));
              setOneofPresent(message, number, pos);
              continue;
            case 66:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Integer.valueOf(reader.readSInt32()));
              setOneofPresent(message, number, pos);
              continue;
            case 67:
              UnsafeUtil.putObject(message, 
                  offset(typeAndOffset), Long.valueOf(reader.readSInt64()));
              setOneofPresent(message, number, pos);
              continue;
            case 68:
              current = (MessageLite)mutableOneofMessageFieldForMerge(message, number, pos);
              reader.mergeGroupField(current, 
                  getMessageFieldSchema(pos), extensionRegistry);
              storeOneofMessageField(message, number, pos, current);
              continue;
          } 
          if (unknownFields == null)
            unknownFields = unknownFieldSchema.getBuilderFromMessage(message); 
          if (!unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader))
            return; 
        } catch (InvalidWireTypeException e) {
          if (unknownFieldSchema.shouldDiscardUnknownFields(reader)) {
            if (!reader.skipField())
              return; 
            continue;
          } 
          if (unknownFields == null)
            unknownFields = unknownFieldSchema.getBuilderFromMessage(message); 
          if (!unknownFieldSchema.mergeOneFieldFrom(unknownFields, reader))
            break; 
        } 
      } 
      return;
    } finally {
      for (int i = this.checkInitializedCount; i < this.repeatedFieldOffsetStart; i++)
        unknownFields = filterMapUnknownEnumValues(message, this.intArray[i], unknownFields, unknownFieldSchema, message); 
      if (unknownFields != null)
        unknownFieldSchema.setBuilderToMessage(message, unknownFields); 
    } 
  }
  
  static UnknownFieldSetLite getMutableUnknownFields(Object message) {
    UnknownFieldSetLite unknownFields = ((GeneratedMessageLite)message).unknownFields;
    if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
      unknownFields = UnknownFieldSetLite.newInstance();
      ((GeneratedMessageLite)message).unknownFields = unknownFields;
    } 
    return unknownFields;
  }
  
  private int decodeMapEntryValue(byte[] data, int position, int limit, WireFormat.FieldType fieldType, Class<?> messageType, ArrayDecoders.Registers registers) throws IOException {
    switch (fieldType) {
      case BOOL:
        position = ArrayDecoders.decodeVarint64(data, position, registers);
        registers.object1 = Boolean.valueOf((registers.long1 != 0L));
        return position;
      case BYTES:
        position = ArrayDecoders.decodeBytes(data, position, registers);
        return position;
      case DOUBLE:
        registers.object1 = Double.valueOf(ArrayDecoders.decodeDouble(data, position));
        position += 8;
        return position;
      case FIXED32:
      case SFIXED32:
        registers.object1 = Integer.valueOf(ArrayDecoders.decodeFixed32(data, position));
        position += 4;
        return position;
      case FIXED64:
      case SFIXED64:
        registers.object1 = Long.valueOf(ArrayDecoders.decodeFixed64(data, position));
        position += 8;
        return position;
      case FLOAT:
        registers.object1 = Float.valueOf(ArrayDecoders.decodeFloat(data, position));
        position += 4;
        return position;
      case ENUM:
      case INT32:
      case UINT32:
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        registers.object1 = Integer.valueOf(registers.int1);
        return position;
      case INT64:
      case UINT64:
        position = ArrayDecoders.decodeVarint64(data, position, registers);
        registers.object1 = Long.valueOf(registers.long1);
        return position;
      case MESSAGE:
        position = ArrayDecoders.decodeMessageField(Protobuf.getInstance().schemaFor(messageType), data, position, limit, registers);
        return position;
      case SINT32:
        position = ArrayDecoders.decodeVarint32(data, position, registers);
        registers.object1 = Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1));
        return position;
      case SINT64:
        position = ArrayDecoders.decodeVarint64(data, position, registers);
        registers.object1 = Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1));
        return position;
      case STRING:
        position = ArrayDecoders.decodeStringRequireUtf8(data, position, registers);
        return position;
    } 
    throw new RuntimeException("unsupported field type.");
  }
  
  private <K, V> int decodeMapEntry(byte[] data, int position, int limit, MapEntryLite.Metadata<K, V> metadata, Map<K, V> target, ArrayDecoders.Registers registers) throws IOException {
    position = ArrayDecoders.decodeVarint32(data, position, registers);
    int length = registers.int1;
    if (length < 0 || length > limit - position)
      throw InvalidProtocolBufferException.truncatedMessage(); 
    int end = position + length;
    K key = metadata.defaultKey;
    V value = metadata.defaultValue;
    while (position < end) {
      int tag = data[position++];
      if (tag < 0) {
        position = ArrayDecoders.decodeVarint32(tag, data, position, registers);
        tag = registers.int1;
      } 
      int fieldNumber = tag >>> 3;
      int wireType = tag & 0x7;
      switch (fieldNumber) {
        case 1:
          if (wireType == metadata.keyType.getWireType()) {
            position = decodeMapEntryValue(data, position, limit, metadata.keyType, null, registers);
            key = (K)registers.object1;
            continue;
          } 
          break;
        case 2:
          if (wireType == metadata.valueType.getWireType()) {
            position = decodeMapEntryValue(data, position, limit, metadata.valueType, metadata.defaultValue
                
                .getClass(), registers);
            value = (V)registers.object1;
            continue;
          } 
          break;
      } 
      position = ArrayDecoders.skipField(tag, data, position, limit, registers);
    } 
    if (position != end)
      throw InvalidProtocolBufferException.parseFailure(); 
    target.put(key, value);
    return end;
  }
  
  private int parseRepeatedField(T message, byte[] data, int position, int limit, int tag, int number, int wireType, int bufferPosition, long typeAndOffset, int fieldType, long fieldOffset, ArrayDecoders.Registers registers) throws IOException {
    Internal.ProtobufList<?> list = (Internal.ProtobufList)UNSAFE.getObject(message, fieldOffset);
    if (!list.isModifiable()) {
      int size = list.size();
      list = list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
      UNSAFE.putObject(message, fieldOffset, list);
    } 
    switch (fieldType) {
      case 18:
      case 35:
        if (wireType == 2) {
          position = ArrayDecoders.decodePackedDoubleList(data, position, list, registers);
          break;
        } 
        if (wireType == 1)
          position = ArrayDecoders.decodeDoubleList(tag, data, position, limit, list, registers); 
        break;
      case 19:
      case 36:
        if (wireType == 2) {
          position = ArrayDecoders.decodePackedFloatList(data, position, list, registers);
          break;
        } 
        if (wireType == 5)
          position = ArrayDecoders.decodeFloatList(tag, data, position, limit, list, registers); 
        break;
      case 20:
      case 21:
      case 37:
      case 38:
        if (wireType == 2) {
          position = ArrayDecoders.decodePackedVarint64List(data, position, list, registers);
          break;
        } 
        if (wireType == 0)
          position = ArrayDecoders.decodeVarint64List(tag, data, position, limit, list, registers); 
        break;
      case 22:
      case 29:
      case 39:
      case 43:
        if (wireType == 2) {
          position = ArrayDecoders.decodePackedVarint32List(data, position, list, registers);
          break;
        } 
        if (wireType == 0)
          position = ArrayDecoders.decodeVarint32List(tag, data, position, limit, list, registers); 
        break;
      case 23:
      case 32:
      case 40:
      case 46:
        if (wireType == 2) {
          position = ArrayDecoders.decodePackedFixed64List(data, position, list, registers);
          break;
        } 
        if (wireType == 1)
          position = ArrayDecoders.decodeFixed64List(tag, data, position, limit, list, registers); 
        break;
      case 24:
      case 31:
      case 41:
      case 45:
        if (wireType == 2) {
          position = ArrayDecoders.decodePackedFixed32List(data, position, list, registers);
          break;
        } 
        if (wireType == 5)
          position = ArrayDecoders.decodeFixed32List(tag, data, position, limit, list, registers); 
        break;
      case 25:
      case 42:
        if (wireType == 2) {
          position = ArrayDecoders.decodePackedBoolList(data, position, list, registers);
          break;
        } 
        if (wireType == 0)
          position = ArrayDecoders.decodeBoolList(tag, data, position, limit, list, registers); 
        break;
      case 26:
        if (wireType == 2) {
          if ((typeAndOffset & 0x20000000L) == 0L) {
            position = ArrayDecoders.decodeStringList(tag, data, position, limit, list, registers);
            break;
          } 
          position = ArrayDecoders.decodeStringListRequireUtf8(tag, data, position, limit, list, registers);
        } 
        break;
      case 27:
        if (wireType == 2)
          position = ArrayDecoders.decodeMessageList(
              getMessageFieldSchema(bufferPosition), tag, data, position, limit, list, registers); 
        break;
      case 28:
        if (wireType == 2)
          position = ArrayDecoders.decodeBytesList(tag, data, position, limit, list, registers); 
        break;
      case 30:
      case 44:
        if (wireType == 2) {
          position = ArrayDecoders.decodePackedVarint32List(data, position, list, registers);
        } else if (wireType == 0) {
          position = ArrayDecoders.decodeVarint32List(tag, data, position, limit, list, registers);
        } else {
          break;
        } 
        SchemaUtil.filterUnknownEnumList(message, number, (List)list, 
            
            getEnumFieldVerifier(bufferPosition), (Object)null, this.unknownFieldSchema);
        break;
      case 33:
      case 47:
        if (wireType == 2) {
          position = ArrayDecoders.decodePackedSInt32List(data, position, list, registers);
          break;
        } 
        if (wireType == 0)
          position = ArrayDecoders.decodeSInt32List(tag, data, position, limit, list, registers); 
        break;
      case 34:
      case 48:
        if (wireType == 2) {
          position = ArrayDecoders.decodePackedSInt64List(data, position, list, registers);
          break;
        } 
        if (wireType == 0)
          position = ArrayDecoders.decodeSInt64List(tag, data, position, limit, list, registers); 
        break;
      case 49:
        if (wireType == 3)
          position = ArrayDecoders.decodeGroupList(
              getMessageFieldSchema(bufferPosition), tag, data, position, limit, list, registers); 
        break;
    } 
    return position;
  }
  
  private <K, V> int parseMapField(T message, byte[] data, int position, int limit, int bufferPosition, long fieldOffset, ArrayDecoders.Registers registers) throws IOException {
    Unsafe unsafe = UNSAFE;
    Object mapDefaultEntry = getMapFieldDefaultEntry(bufferPosition);
    Object mapField = unsafe.getObject(message, fieldOffset);
    if (this.mapFieldSchema.isImmutable(mapField)) {
      Object oldMapField = mapField;
      mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
      this.mapFieldSchema.mergeFrom(mapField, oldMapField);
      unsafe.putObject(message, fieldOffset, mapField);
    } 
    return decodeMapEntry(data, position, limit, this.mapFieldSchema
        
        .forMapMetadata(mapDefaultEntry), this.mapFieldSchema
        .forMutableMapData(mapField), registers);
  }
  
  private int parseOneofField(T message, byte[] data, int position, int limit, int tag, int number, int wireType, int typeAndOffset, int fieldType, long fieldOffset, int bufferPosition, ArrayDecoders.Registers registers) throws IOException {
    Unsafe unsafe = UNSAFE;
    long oneofCaseOffset = (this.buffer[bufferPosition + 2] & 0xFFFFF);
    switch (fieldType) {
      case 51:
        if (wireType == 1) {
          unsafe.putObject(message, fieldOffset, Double.valueOf(ArrayDecoders.decodeDouble(data, position)));
          position += 8;
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 52:
        if (wireType == 5) {
          unsafe.putObject(message, fieldOffset, Float.valueOf(ArrayDecoders.decodeFloat(data, position)));
          position += 4;
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 53:
      case 54:
        if (wireType == 0) {
          position = ArrayDecoders.decodeVarint64(data, position, registers);
          unsafe.putObject(message, fieldOffset, Long.valueOf(registers.long1));
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 55:
      case 62:
        if (wireType == 0) {
          position = ArrayDecoders.decodeVarint32(data, position, registers);
          unsafe.putObject(message, fieldOffset, Integer.valueOf(registers.int1));
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 56:
      case 65:
        if (wireType == 1) {
          unsafe.putObject(message, fieldOffset, Long.valueOf(ArrayDecoders.decodeFixed64(data, position)));
          position += 8;
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 57:
      case 64:
        if (wireType == 5) {
          unsafe.putObject(message, fieldOffset, Integer.valueOf(ArrayDecoders.decodeFixed32(data, position)));
          position += 4;
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 58:
        if (wireType == 0) {
          position = ArrayDecoders.decodeVarint64(data, position, registers);
          unsafe.putObject(message, fieldOffset, Boolean.valueOf((registers.long1 != 0L)));
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 59:
        if (wireType == 2) {
          position = ArrayDecoders.decodeVarint32(data, position, registers);
          int length = registers.int1;
          if (length == 0) {
            unsafe.putObject(message, fieldOffset, "");
          } else {
            if ((typeAndOffset & 0x20000000) != 0 && 
              !Utf8.isValidUtf8(data, position, position + length))
              throw InvalidProtocolBufferException.invalidUtf8(); 
            String value = new String(data, position, length, Internal.UTF_8);
            unsafe.putObject(message, fieldOffset, value);
            position += length;
          } 
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 60:
        if (wireType == 2) {
          Object current = mutableOneofMessageFieldForMerge(message, number, bufferPosition);
          position = ArrayDecoders.mergeMessageField(current, 
              getMessageFieldSchema(bufferPosition), data, position, limit, registers);
          storeOneofMessageField(message, number, bufferPosition, current);
        } 
        break;
      case 61:
        if (wireType == 2) {
          position = ArrayDecoders.decodeBytes(data, position, registers);
          unsafe.putObject(message, fieldOffset, registers.object1);
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 63:
        if (wireType == 0) {
          position = ArrayDecoders.decodeVarint32(data, position, registers);
          int enumValue = registers.int1;
          Internal.EnumVerifier enumVerifier = getEnumFieldVerifier(bufferPosition);
          if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
            unsafe.putObject(message, fieldOffset, Integer.valueOf(enumValue));
            unsafe.putInt(message, oneofCaseOffset, number);
            break;
          } 
          getMutableUnknownFields(message).storeField(tag, Long.valueOf(enumValue));
        } 
        break;
      case 66:
        if (wireType == 0) {
          position = ArrayDecoders.decodeVarint32(data, position, registers);
          unsafe.putObject(message, fieldOffset, Integer.valueOf(CodedInputStream.decodeZigZag32(registers.int1)));
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 67:
        if (wireType == 0) {
          position = ArrayDecoders.decodeVarint64(data, position, registers);
          unsafe.putObject(message, fieldOffset, Long.valueOf(CodedInputStream.decodeZigZag64(registers.long1)));
          unsafe.putInt(message, oneofCaseOffset, number);
        } 
        break;
      case 68:
        if (wireType == 3) {
          Object current = mutableOneofMessageFieldForMerge(message, number, bufferPosition);
          int endTag = tag & 0xFFFFFFF8 | 0x4;
          position = ArrayDecoders.mergeGroupField(current, 
              
              getMessageFieldSchema(bufferPosition), data, position, limit, endTag, registers);
          storeOneofMessageField(message, number, bufferPosition, current);
        } 
        break;
    } 
    return position;
  }
  
  private Schema getMessageFieldSchema(int pos) {
    int index = pos / 3 * 2;
    Schema<?> schema = (Schema)this.objects[index];
    if (schema != null)
      return schema; 
    schema = Protobuf.getInstance().schemaFor((Class)this.objects[index + 1]);
    this.objects[index] = schema;
    return schema;
  }
  
  private Object getMapFieldDefaultEntry(int pos) {
    return this.objects[pos / 3 * 2];
  }
  
  private Internal.EnumVerifier getEnumFieldVerifier(int pos) {
    return (Internal.EnumVerifier)this.objects[pos / 3 * 2 + 1];
  }
  
  @CanIgnoreReturnValue
  int parseProto2Message(T message, byte[] data, int position, int limit, int endGroup, ArrayDecoders.Registers registers) throws IOException {
    checkMutable(message);
    Unsafe unsafe = UNSAFE;
    int currentPresenceFieldOffset = 1048575;
    int currentPresenceField = 0;
    int tag = 0;
    int oldNumber = -1;
    int pos = 0;
    while (position < limit) {
      tag = data[position++];
      if (tag < 0) {
        position = ArrayDecoders.decodeVarint32(tag, data, position, registers);
        tag = registers.int1;
      } 
      int number = tag >>> 3;
      int wireType = tag & 0x7;
      if (number > oldNumber) {
        pos = positionForFieldNumber(number, pos / 3);
      } else {
        pos = positionForFieldNumber(number);
      } 
      oldNumber = number;
      if (pos == -1) {
        pos = 0;
      } else {
        int typeAndOffset = this.buffer[pos + 1];
        int fieldType = type(typeAndOffset);
        long fieldOffset = offset(typeAndOffset);
        if (fieldType <= 17) {
          int presenceMaskAndOffset = this.buffer[pos + 2];
          int presenceMask = 1 << presenceMaskAndOffset >>> 20;
          int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
          if (presenceFieldOffset != currentPresenceFieldOffset) {
            if (currentPresenceFieldOffset != 1048575)
              unsafe.putInt(message, currentPresenceFieldOffset, currentPresenceField); 
            currentPresenceFieldOffset = presenceFieldOffset;
            currentPresenceField = unsafe.getInt(message, presenceFieldOffset);
          } 
          switch (fieldType) {
            case 0:
              if (wireType == 1) {
                UnsafeUtil.putDouble(message, fieldOffset, ArrayDecoders.decodeDouble(data, position));
                position += 8;
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 1:
              if (wireType == 5) {
                UnsafeUtil.putFloat(message, fieldOffset, ArrayDecoders.decodeFloat(data, position));
                position += 4;
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 2:
            case 3:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                unsafe.putLong(message, fieldOffset, registers.long1);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 4:
            case 11:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                unsafe.putInt(message, fieldOffset, registers.int1);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 5:
            case 14:
              if (wireType == 1) {
                unsafe.putLong(message, fieldOffset, ArrayDecoders.decodeFixed64(data, position));
                position += 8;
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 6:
            case 13:
              if (wireType == 5) {
                unsafe.putInt(message, fieldOffset, ArrayDecoders.decodeFixed32(data, position));
                position += 4;
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 7:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                UnsafeUtil.putBoolean(message, fieldOffset, (registers.long1 != 0L));
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 8:
              if (wireType == 2) {
                if ((typeAndOffset & 0x20000000) == 0) {
                  position = ArrayDecoders.decodeString(data, position, registers);
                } else {
                  position = ArrayDecoders.decodeStringRequireUtf8(data, position, registers);
                } 
                unsafe.putObject(message, fieldOffset, registers.object1);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 9:
              if (wireType == 2) {
                Object current = mutableMessageFieldForMerge(message, pos);
                position = ArrayDecoders.mergeMessageField(current, 
                    getMessageFieldSchema(pos), data, position, limit, registers);
                storeMessageField(message, pos, current);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 10:
              if (wireType == 2) {
                position = ArrayDecoders.decodeBytes(data, position, registers);
                unsafe.putObject(message, fieldOffset, registers.object1);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 12:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                int enumValue = registers.int1;
                Internal.EnumVerifier enumVerifier = getEnumFieldVerifier(pos);
                if (enumVerifier == null || enumVerifier.isInRange(enumValue)) {
                  unsafe.putInt(message, fieldOffset, enumValue);
                  currentPresenceField |= presenceMask;
                  continue;
                } 
                getMutableUnknownFields(message).storeField(tag, Long.valueOf(enumValue));
                continue;
              } 
              break;
            case 15:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                unsafe.putInt(message, fieldOffset, 
                    CodedInputStream.decodeZigZag32(registers.int1));
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 16:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                unsafe.putLong(message, fieldOffset, 
                    CodedInputStream.decodeZigZag64(registers.long1));
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 17:
              if (wireType == 3) {
                Object current = mutableMessageFieldForMerge(message, pos);
                int endTag = number << 3 | 0x4;
                position = ArrayDecoders.mergeGroupField(current, 
                    
                    getMessageFieldSchema(pos), data, position, limit, endTag, registers);
                storeMessageField(message, pos, current);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
          } 
        } else if (fieldType == 27) {
          if (wireType == 2) {
            Internal.ProtobufList<?> list = (Internal.ProtobufList)unsafe.getObject(message, fieldOffset);
            if (!list.isModifiable()) {
              int size = list.size();
              list = list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
              unsafe.putObject(message, fieldOffset, list);
            } 
            position = ArrayDecoders.decodeMessageList(
                getMessageFieldSchema(pos), tag, data, position, limit, list, registers);
            continue;
          } 
        } else if (fieldType <= 49) {
          int oldPosition = position;
          position = parseRepeatedField(message, data, position, limit, tag, number, wireType, pos, typeAndOffset, fieldType, fieldOffset, registers);
          if (position != oldPosition)
            continue; 
        } else if (fieldType == 50) {
          if (wireType == 2) {
            int oldPosition = position;
            position = parseMapField(message, data, position, limit, pos, fieldOffset, registers);
            if (position != oldPosition)
              continue; 
          } 
        } else {
          int oldPosition = position;
          position = parseOneofField(message, data, position, limit, tag, number, wireType, typeAndOffset, fieldType, fieldOffset, pos, registers);
          if (position != oldPosition)
            continue; 
        } 
      } 
      if (tag == endGroup && endGroup != 0)
        break; 
      if (this.hasExtensions && registers.extensionRegistry != 
        ExtensionRegistryLite.getEmptyRegistry()) {
        position = ArrayDecoders.decodeExtensionOrUnknownField(tag, data, position, limit, message, this.defaultInstance, (UnknownFieldSchema)this.unknownFieldSchema, registers);
        continue;
      } 
      position = ArrayDecoders.decodeUnknownField(tag, data, position, limit, 
          getMutableUnknownFields(message), registers);
    } 
    if (currentPresenceFieldOffset != 1048575)
      unsafe.putInt(message, currentPresenceFieldOffset, currentPresenceField); 
    UnknownFieldSetLite unknownFields = null;
    for (int i = this.checkInitializedCount; i < this.repeatedFieldOffsetStart; i++)
      unknownFields = (UnknownFieldSetLite)filterMapUnknownEnumValues(message, this.intArray[i], unknownFields, this.unknownFieldSchema, message); 
    if (unknownFields != null)
      this.unknownFieldSchema
        .setBuilderToMessage(message, unknownFields); 
    if (endGroup == 0) {
      if (position != limit)
        throw InvalidProtocolBufferException.parseFailure(); 
    } else if (position > limit || tag != endGroup) {
      throw InvalidProtocolBufferException.parseFailure();
    } 
    return position;
  }
  
  private Object mutableMessageFieldForMerge(T message, int pos) {
    Schema<Object> fieldSchema = getMessageFieldSchema(pos);
    long offset = offset(typeAndOffsetAt(pos));
    if (!isFieldPresent(message, pos))
      return fieldSchema.newInstance(); 
    Object current = UNSAFE.getObject(message, offset);
    if (isMutable(current))
      return current; 
    Object newMessage = fieldSchema.newInstance();
    if (current != null)
      fieldSchema.mergeFrom(newMessage, current); 
    return newMessage;
  }
  
  private void storeMessageField(T message, int pos, Object field) {
    UNSAFE.putObject(message, offset(typeAndOffsetAt(pos)), field);
    setFieldPresent(message, pos);
  }
  
  private Object mutableOneofMessageFieldForMerge(T message, int fieldNumber, int pos) {
    Schema<Object> fieldSchema = getMessageFieldSchema(pos);
    if (!isOneofPresent(message, fieldNumber, pos))
      return fieldSchema.newInstance(); 
    Object current = UNSAFE.getObject(message, offset(typeAndOffsetAt(pos)));
    if (isMutable(current))
      return current; 
    Object newMessage = fieldSchema.newInstance();
    if (current != null)
      fieldSchema.mergeFrom(newMessage, current); 
    return newMessage;
  }
  
  private void storeOneofMessageField(T message, int fieldNumber, int pos, Object field) {
    UNSAFE.putObject(message, offset(typeAndOffsetAt(pos)), field);
    setOneofPresent(message, fieldNumber, pos);
  }
  
  @CanIgnoreReturnValue
  private int parseProto3Message(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
    checkMutable(message);
    Unsafe unsafe = UNSAFE;
    int currentPresenceFieldOffset = 1048575;
    int currentPresenceField = 0;
    int tag = 0;
    int oldNumber = -1;
    int pos = 0;
    while (position < limit) {
      tag = data[position++];
      if (tag < 0) {
        position = ArrayDecoders.decodeVarint32(tag, data, position, registers);
        tag = registers.int1;
      } 
      int number = tag >>> 3;
      int wireType = tag & 0x7;
      if (number > oldNumber) {
        pos = positionForFieldNumber(number, pos / 3);
      } else {
        pos = positionForFieldNumber(number);
      } 
      oldNumber = number;
      if (pos == -1) {
        pos = 0;
      } else {
        int typeAndOffset = this.buffer[pos + 1];
        int fieldType = type(typeAndOffset);
        long fieldOffset = offset(typeAndOffset);
        if (fieldType <= 17) {
          int presenceMaskAndOffset = this.buffer[pos + 2];
          int presenceMask = 1 << presenceMaskAndOffset >>> 20;
          int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
          if (presenceFieldOffset != currentPresenceFieldOffset) {
            if (currentPresenceFieldOffset != 1048575)
              unsafe.putInt(message, currentPresenceFieldOffset, currentPresenceField); 
            if (presenceFieldOffset != 1048575)
              currentPresenceField = unsafe.getInt(message, presenceFieldOffset); 
            currentPresenceFieldOffset = presenceFieldOffset;
          } 
          switch (fieldType) {
            case 0:
              if (wireType == 1) {
                UnsafeUtil.putDouble(message, fieldOffset, ArrayDecoders.decodeDouble(data, position));
                position += 8;
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 1:
              if (wireType == 5) {
                UnsafeUtil.putFloat(message, fieldOffset, ArrayDecoders.decodeFloat(data, position));
                position += 4;
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 2:
            case 3:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                unsafe.putLong(message, fieldOffset, registers.long1);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 4:
            case 11:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                unsafe.putInt(message, fieldOffset, registers.int1);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 5:
            case 14:
              if (wireType == 1) {
                unsafe.putLong(message, fieldOffset, ArrayDecoders.decodeFixed64(data, position));
                position += 8;
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 6:
            case 13:
              if (wireType == 5) {
                unsafe.putInt(message, fieldOffset, ArrayDecoders.decodeFixed32(data, position));
                position += 4;
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 7:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                UnsafeUtil.putBoolean(message, fieldOffset, (registers.long1 != 0L));
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 8:
              if (wireType == 2) {
                if ((typeAndOffset & 0x20000000) == 0) {
                  position = ArrayDecoders.decodeString(data, position, registers);
                } else {
                  position = ArrayDecoders.decodeStringRequireUtf8(data, position, registers);
                } 
                unsafe.putObject(message, fieldOffset, registers.object1);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 9:
              if (wireType == 2) {
                Object current = mutableMessageFieldForMerge(message, pos);
                position = ArrayDecoders.mergeMessageField(current, 
                    getMessageFieldSchema(pos), data, position, limit, registers);
                storeMessageField(message, pos, current);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 10:
              if (wireType == 2) {
                position = ArrayDecoders.decodeBytes(data, position, registers);
                unsafe.putObject(message, fieldOffset, registers.object1);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 12:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                unsafe.putInt(message, fieldOffset, registers.int1);
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 15:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint32(data, position, registers);
                unsafe.putInt(message, fieldOffset, 
                    CodedInputStream.decodeZigZag32(registers.int1));
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
            case 16:
              if (wireType == 0) {
                position = ArrayDecoders.decodeVarint64(data, position, registers);
                unsafe.putLong(message, fieldOffset, 
                    CodedInputStream.decodeZigZag64(registers.long1));
                currentPresenceField |= presenceMask;
                continue;
              } 
              break;
          } 
        } else if (fieldType == 27) {
          if (wireType == 2) {
            Internal.ProtobufList<?> list = (Internal.ProtobufList)unsafe.getObject(message, fieldOffset);
            if (!list.isModifiable()) {
              int size = list.size();
              list = list.mutableCopyWithCapacity((size == 0) ? 10 : (size * 2));
              unsafe.putObject(message, fieldOffset, list);
            } 
            position = ArrayDecoders.decodeMessageList(
                getMessageFieldSchema(pos), tag, data, position, limit, list, registers);
            continue;
          } 
        } else if (fieldType <= 49) {
          int oldPosition = position;
          position = parseRepeatedField(message, data, position, limit, tag, number, wireType, pos, typeAndOffset, fieldType, fieldOffset, registers);
          if (position != oldPosition)
            continue; 
        } else if (fieldType == 50) {
          if (wireType == 2) {
            int oldPosition = position;
            position = parseMapField(message, data, position, limit, pos, fieldOffset, registers);
            if (position != oldPosition)
              continue; 
          } 
        } else {
          int oldPosition = position;
          position = parseOneofField(message, data, position, limit, tag, number, wireType, typeAndOffset, fieldType, fieldOffset, pos, registers);
          if (position != oldPosition)
            continue; 
        } 
      } 
      position = ArrayDecoders.decodeUnknownField(tag, data, position, limit, 
          getMutableUnknownFields(message), registers);
    } 
    if (currentPresenceFieldOffset != 1048575)
      unsafe.putInt(message, currentPresenceFieldOffset, currentPresenceField); 
    if (position != limit)
      throw InvalidProtocolBufferException.parseFailure(); 
    return position;
  }
  
  public void mergeFrom(T message, byte[] data, int position, int limit, ArrayDecoders.Registers registers) throws IOException {
    if (this.proto3) {
      parseProto3Message(message, data, position, limit, registers);
    } else {
      parseProto2Message(message, data, position, limit, 0, registers);
    } 
  }
  
  public void makeImmutable(T message) {
    if (!isMutable(message))
      return; 
    if (message instanceof GeneratedMessageLite) {
      GeneratedMessageLite<?, ?> generatedMessage = (GeneratedMessageLite<?, ?>)message;
      generatedMessage.clearMemoizedSerializedSize();
      generatedMessage.clearMemoizedHashCode();
      generatedMessage.markImmutable();
    } 
    int bufferLength = this.buffer.length;
    for (int pos = 0; pos < bufferLength; pos += 3) {
      Object mapField;
      int typeAndOffset = typeAndOffsetAt(pos);
      long offset = offset(typeAndOffset);
      switch (type(typeAndOffset)) {
        case 9:
        case 17:
          if (isFieldPresent(message, pos))
            getMessageFieldSchema(pos).makeImmutable(UNSAFE.getObject(message, offset)); 
          break;
        case 18:
        case 19:
        case 20:
        case 21:
        case 22:
        case 23:
        case 24:
        case 25:
        case 26:
        case 27:
        case 28:
        case 29:
        case 30:
        case 31:
        case 32:
        case 33:
        case 34:
        case 35:
        case 36:
        case 37:
        case 38:
        case 39:
        case 40:
        case 41:
        case 42:
        case 43:
        case 44:
        case 45:
        case 46:
        case 47:
        case 48:
        case 49:
          this.listFieldSchema.makeImmutableListAt(message, offset);
          break;
        case 50:
          mapField = UNSAFE.getObject(message, offset);
          if (mapField != null)
            UNSAFE.putObject(message, offset, this.mapFieldSchema.toImmutable(mapField)); 
          break;
      } 
    } 
    this.unknownFieldSchema.makeImmutable(message);
    if (this.hasExtensions)
      this.extensionSchema.makeImmutable(message); 
  }
  
  private final <K, V> void mergeMap(Object message, int pos, Object mapDefaultEntry, ExtensionRegistryLite extensionRegistry, Reader reader) throws IOException {
    long offset = offset(typeAndOffsetAt(pos));
    Object mapField = UnsafeUtil.getObject(message, offset);
    if (mapField == null) {
      mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
      UnsafeUtil.putObject(message, offset, mapField);
    } else if (this.mapFieldSchema.isImmutable(mapField)) {
      Object oldMapField = mapField;
      mapField = this.mapFieldSchema.newMapField(mapDefaultEntry);
      this.mapFieldSchema.mergeFrom(mapField, oldMapField);
      UnsafeUtil.putObject(message, offset, mapField);
    } 
    reader.readMap(this.mapFieldSchema
        .forMutableMapData(mapField), this.mapFieldSchema
        .forMapMetadata(mapDefaultEntry), extensionRegistry);
  }
  
  private <UT, UB> UB filterMapUnknownEnumValues(Object message, int pos, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema, Object containerMessage) {
    int fieldNumber = numberAt(pos);
    long offset = offset(typeAndOffsetAt(pos));
    Object mapField = UnsafeUtil.getObject(message, offset);
    if (mapField == null)
      return unknownFields; 
    Internal.EnumVerifier enumVerifier = getEnumFieldVerifier(pos);
    if (enumVerifier == null)
      return unknownFields; 
    Map<?, ?> mapData = this.mapFieldSchema.forMutableMapData(mapField);
    unknownFields = filterUnknownEnumMap(pos, fieldNumber, mapData, enumVerifier, unknownFields, unknownFieldSchema, containerMessage);
    return unknownFields;
  }
  
  private <K, V, UT, UB> UB filterUnknownEnumMap(int pos, int number, Map<K, V> mapData, Internal.EnumVerifier enumVerifier, UB unknownFields, UnknownFieldSchema<UT, UB> unknownFieldSchema, Object containerMessage) {
    MapEntryLite.Metadata<K, V> metadata = (MapEntryLite.Metadata)this.mapFieldSchema.forMapMetadata(getMapFieldDefaultEntry(pos));
    for (Iterator<Map.Entry<K, V>> it = mapData.entrySet().iterator(); it.hasNext(); ) {
      Map.Entry<K, V> entry = it.next();
      if (!enumVerifier.isInRange(((Integer)entry.getValue()).intValue())) {
        if (unknownFields == null)
          unknownFields = unknownFieldSchema.getBuilderFromMessage(containerMessage); 
        int entrySize = MapEntryLite.computeSerializedSize(metadata, entry.getKey(), entry.getValue());
        ByteString.CodedBuilder codedBuilder = ByteString.newCodedBuilder(entrySize);
        CodedOutputStream codedOutput = codedBuilder.getCodedOutput();
        try {
          MapEntryLite.writeTo(codedOutput, metadata, entry.getKey(), entry.getValue());
        } catch (IOException e) {
          throw new RuntimeException(e);
        } 
        unknownFieldSchema.addLengthDelimited(unknownFields, number, codedBuilder.build());
        it.remove();
      } 
    } 
    return unknownFields;
  }
  
  public final boolean isInitialized(T message) {
    int currentPresenceFieldOffset = 1048575;
    int currentPresenceField = 0;
    for (int i = 0; i < this.checkInitializedCount; i++) {
      int pos = this.intArray[i];
      int number = numberAt(pos);
      int typeAndOffset = typeAndOffsetAt(pos);
      int presenceMaskAndOffset = this.buffer[pos + 2];
      int presenceFieldOffset = presenceMaskAndOffset & 0xFFFFF;
      int presenceMask = 1 << presenceMaskAndOffset >>> 20;
      if (presenceFieldOffset != currentPresenceFieldOffset) {
        currentPresenceFieldOffset = presenceFieldOffset;
        if (currentPresenceFieldOffset != 1048575)
          currentPresenceField = UNSAFE.getInt(message, presenceFieldOffset); 
      } 
      if (isRequired(typeAndOffset) && 
        !isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask))
        return false; 
      switch (type(typeAndOffset)) {
        case 9:
        case 17:
          if (isFieldPresent(message, pos, currentPresenceFieldOffset, currentPresenceField, presenceMask) && 
            
            !isInitialized(message, typeAndOffset, getMessageFieldSchema(pos)))
            return false; 
          break;
        case 27:
        case 49:
          if (!isListInitialized(message, typeAndOffset, pos))
            return false; 
          break;
        case 60:
        case 68:
          if (isOneofPresent(message, number, pos) && 
            !isInitialized(message, typeAndOffset, getMessageFieldSchema(pos)))
            return false; 
          break;
        case 50:
          if (!isMapInitialized(message, typeAndOffset, pos))
            return false; 
          break;
      } 
    } 
    if (this.hasExtensions && 
      !this.extensionSchema.getExtensions(message).isInitialized())
      return false; 
    return true;
  }
  
  private static boolean isInitialized(Object message, int typeAndOffset, Schema<Object> schema) {
    Object nested = UnsafeUtil.getObject(message, offset(typeAndOffset));
    return schema.isInitialized(nested);
  }
  
  private <N> boolean isListInitialized(Object message, int typeAndOffset, int pos) {
    List<N> list = (List<N>)UnsafeUtil.getObject(message, offset(typeAndOffset));
    if (list.isEmpty())
      return true; 
    Schema<N> schema = getMessageFieldSchema(pos);
    for (int i = 0; i < list.size(); i++) {
      N nested = list.get(i);
      if (!schema.isInitialized(nested))
        return false; 
    } 
    return true;
  }
  
  private boolean isMapInitialized(T message, int typeAndOffset, int pos) {
    Map<?, ?> map = this.mapFieldSchema.forMapData(UnsafeUtil.getObject(message, offset(typeAndOffset)));
    if (map.isEmpty())
      return true; 
    Object mapDefaultEntry = getMapFieldDefaultEntry(pos);
    MapEntryLite.Metadata<?, ?> metadata = this.mapFieldSchema.forMapMetadata(mapDefaultEntry);
    if (metadata.valueType.getJavaType() != WireFormat.JavaType.MESSAGE)
      return true; 
    Schema<?> schema = null;
    for (Object nested : map.values()) {
      if (schema == null)
        schema = Protobuf.getInstance().schemaFor(nested.getClass()); 
      if (!schema.isInitialized(nested))
        return false; 
    } 
    return true;
  }
  
  private void writeString(int fieldNumber, Object value, Writer writer) throws IOException {
    if (value instanceof String) {
      writer.writeString(fieldNumber, (String)value);
    } else {
      writer.writeBytes(fieldNumber, (ByteString)value);
    } 
  }
  
  private void readString(Object message, int typeAndOffset, Reader reader) throws IOException {
    if (isEnforceUtf8(typeAndOffset)) {
      UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readStringRequireUtf8());
    } else if (this.lite) {
      UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readString());
    } else {
      UnsafeUtil.putObject(message, offset(typeAndOffset), reader.readBytes());
    } 
  }
  
  private void readStringList(Object message, int typeAndOffset, Reader reader) throws IOException {
    if (isEnforceUtf8(typeAndOffset)) {
      reader.readStringListRequireUtf8(this.listFieldSchema
          .mutableListAt(message, offset(typeAndOffset)));
    } else {
      reader.readStringList(this.listFieldSchema.mutableListAt(message, offset(typeAndOffset)));
    } 
  }
  
  private <E> void readMessageList(Object message, int typeAndOffset, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
    long offset = offset(typeAndOffset);
    reader.readMessageList(this.listFieldSchema
        .mutableListAt(message, offset), schema, extensionRegistry);
  }
  
  private <E> void readGroupList(Object message, long offset, Reader reader, Schema<E> schema, ExtensionRegistryLite extensionRegistry) throws IOException {
    reader.readGroupList(this.listFieldSchema
        .mutableListAt(message, offset), schema, extensionRegistry);
  }
  
  private int numberAt(int pos) {
    return this.buffer[pos];
  }
  
  private int typeAndOffsetAt(int pos) {
    return this.buffer[pos + 1];
  }
  
  private int presenceMaskAndOffsetAt(int pos) {
    return this.buffer[pos + 2];
  }
  
  private static int type(int value) {
    return (value & 0xFF00000) >>> 20;
  }
  
  private static boolean isRequired(int value) {
    return ((value & 0x10000000) != 0);
  }
  
  private static boolean isEnforceUtf8(int value) {
    return ((value & 0x20000000) != 0);
  }
  
  private static long offset(int value) {
    return (value & 0xFFFFF);
  }
  
  private static boolean isMutable(Object message) {
    if (message == null)
      return false; 
    if (message instanceof GeneratedMessageLite)
      return ((GeneratedMessageLite)message).isMutable(); 
    return true;
  }
  
  private static void checkMutable(Object message) {
    if (!isMutable(message))
      throw new IllegalArgumentException("Mutating immutable message: " + message); 
  }
  
  private static <T> double doubleAt(T message, long offset) {
    return UnsafeUtil.getDouble(message, offset);
  }
  
  private static <T> float floatAt(T message, long offset) {
    return UnsafeUtil.getFloat(message, offset);
  }
  
  private static <T> int intAt(T message, long offset) {
    return UnsafeUtil.getInt(message, offset);
  }
  
  private static <T> long longAt(T message, long offset) {
    return UnsafeUtil.getLong(message, offset);
  }
  
  private static <T> boolean booleanAt(T message, long offset) {
    return UnsafeUtil.getBoolean(message, offset);
  }
  
  private static <T> double oneofDoubleAt(T message, long offset) {
    return ((Double)UnsafeUtil.getObject(message, offset)).doubleValue();
  }
  
  private static <T> float oneofFloatAt(T message, long offset) {
    return ((Float)UnsafeUtil.getObject(message, offset)).floatValue();
  }
  
  private static <T> int oneofIntAt(T message, long offset) {
    return ((Integer)UnsafeUtil.getObject(message, offset)).intValue();
  }
  
  private static <T> long oneofLongAt(T message, long offset) {
    return ((Long)UnsafeUtil.getObject(message, offset)).longValue();
  }
  
  private static <T> boolean oneofBooleanAt(T message, long offset) {
    return ((Boolean)UnsafeUtil.getObject(message, offset)).booleanValue();
  }
  
  private boolean arePresentForEquals(T message, T other, int pos) {
    return (isFieldPresent(message, pos) == isFieldPresent(other, pos));
  }
  
  private boolean isFieldPresent(T message, int pos, int presenceFieldOffset, int presenceField, int presenceMask) {
    if (presenceFieldOffset == 1048575)
      return isFieldPresent(message, pos); 
    return ((presenceField & presenceMask) != 0);
  }
  
  private boolean isFieldPresent(T message, int pos) {
    int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
    long presenceFieldOffset = (presenceMaskAndOffset & 0xFFFFF);
    if (presenceFieldOffset == 1048575L) {
      Object value;
      int typeAndOffset = typeAndOffsetAt(pos);
      long offset = offset(typeAndOffset);
      switch (type(typeAndOffset)) {
        case 0:
          return (Double.doubleToRawLongBits(UnsafeUtil.getDouble(message, offset)) != 0L);
        case 1:
          return (Float.floatToRawIntBits(UnsafeUtil.getFloat(message, offset)) != 0);
        case 2:
          return (UnsafeUtil.getLong(message, offset) != 0L);
        case 3:
          return (UnsafeUtil.getLong(message, offset) != 0L);
        case 4:
          return (UnsafeUtil.getInt(message, offset) != 0);
        case 5:
          return (UnsafeUtil.getLong(message, offset) != 0L);
        case 6:
          return (UnsafeUtil.getInt(message, offset) != 0);
        case 7:
          return UnsafeUtil.getBoolean(message, offset);
        case 8:
          value = UnsafeUtil.getObject(message, offset);
          if (value instanceof String)
            return !((String)value).isEmpty(); 
          if (value instanceof ByteString)
            return !ByteString.EMPTY.equals(value); 
          throw new IllegalArgumentException();
        case 9:
          return (UnsafeUtil.getObject(message, offset) != null);
        case 10:
          return !ByteString.EMPTY.equals(UnsafeUtil.getObject(message, offset));
        case 11:
          return (UnsafeUtil.getInt(message, offset) != 0);
        case 12:
          return (UnsafeUtil.getInt(message, offset) != 0);
        case 13:
          return (UnsafeUtil.getInt(message, offset) != 0);
        case 14:
          return (UnsafeUtil.getLong(message, offset) != 0L);
        case 15:
          return (UnsafeUtil.getInt(message, offset) != 0);
        case 16:
          return (UnsafeUtil.getLong(message, offset) != 0L);
        case 17:
          return (UnsafeUtil.getObject(message, offset) != null);
      } 
      throw new IllegalArgumentException();
    } 
    int presenceMask = 1 << presenceMaskAndOffset >>> 20;
    return ((UnsafeUtil.getInt(message, (presenceMaskAndOffset & 0xFFFFF)) & presenceMask) != 0);
  }
  
  private void setFieldPresent(T message, int pos) {
    int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
    long presenceFieldOffset = (presenceMaskAndOffset & 0xFFFFF);
    if (presenceFieldOffset == 1048575L)
      return; 
    int presenceMask = 1 << presenceMaskAndOffset >>> 20;
    UnsafeUtil.putInt(message, presenceFieldOffset, 
        
        UnsafeUtil.getInt(message, presenceFieldOffset) | presenceMask);
  }
  
  private boolean isOneofPresent(T message, int fieldNumber, int pos) {
    int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
    return (UnsafeUtil.getInt(message, (presenceMaskAndOffset & 0xFFFFF)) == fieldNumber);
  }
  
  private boolean isOneofCaseEqual(T message, T other, int pos) {
    int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
    return 
      (UnsafeUtil.getInt(message, (presenceMaskAndOffset & 0xFFFFF)) == UnsafeUtil.getInt(other, (presenceMaskAndOffset & 0xFFFFF)));
  }
  
  private void setOneofPresent(T message, int fieldNumber, int pos) {
    int presenceMaskAndOffset = presenceMaskAndOffsetAt(pos);
    UnsafeUtil.putInt(message, (presenceMaskAndOffset & 0xFFFFF), fieldNumber);
  }
  
  private int positionForFieldNumber(int number) {
    if (number >= this.minFieldNumber && number <= this.maxFieldNumber)
      return slowPositionForFieldNumber(number, 0); 
    return -1;
  }
  
  private int positionForFieldNumber(int number, int min) {
    if (number >= this.minFieldNumber && number <= this.maxFieldNumber)
      return slowPositionForFieldNumber(number, min); 
    return -1;
  }
  
  private int slowPositionForFieldNumber(int number, int min) {
    int max = this.buffer.length / 3 - 1;
    while (min <= max) {
      int mid = max + min >>> 1;
      int pos = mid * 3;
      int midFieldNumber = numberAt(pos);
      if (number == midFieldNumber)
        return pos; 
      if (number < midFieldNumber) {
        max = mid - 1;
        continue;
      } 
      min = mid + 1;
    } 
    return -1;
  }
  
  int getSchemaSize() {
    return this.buffer.length * 3;
  }
}
