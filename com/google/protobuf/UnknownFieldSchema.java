package com.google.protobuf;

import java.io.IOException;

@CheckReturnValue
abstract class UnknownFieldSchema<T, B> {
  abstract boolean shouldDiscardUnknownFields(Reader paramReader);
  
  abstract void addVarint(B paramB, int paramInt, long paramLong);
  
  abstract void addFixed32(B paramB, int paramInt1, int paramInt2);
  
  abstract void addFixed64(B paramB, int paramInt, long paramLong);
  
  abstract void addLengthDelimited(B paramB, int paramInt, ByteString paramByteString);
  
  abstract void addGroup(B paramB, int paramInt, T paramT);
  
  abstract B newBuilder();
  
  abstract T toImmutable(B paramB);
  
  abstract void setToMessage(Object paramObject, T paramT);
  
  abstract T getFromMessage(Object paramObject);
  
  abstract B getBuilderFromMessage(Object paramObject);
  
  abstract void setBuilderToMessage(Object paramObject, B paramB);
  
  abstract void makeImmutable(Object paramObject);
  
  final boolean mergeOneFieldFrom(B unknownFields, Reader reader) throws IOException {
    B subFields;
    int endGroupTag, tag = reader.getTag();
    int fieldNumber = WireFormat.getTagFieldNumber(tag);
    switch (WireFormat.getTagWireType(tag)) {
      case 0:
        addVarint(unknownFields, fieldNumber, reader.readInt64());
        return true;
      case 5:
        addFixed32(unknownFields, fieldNumber, reader.readFixed32());
        return true;
      case 1:
        addFixed64(unknownFields, fieldNumber, reader.readFixed64());
        return true;
      case 2:
        addLengthDelimited(unknownFields, fieldNumber, reader.readBytes());
        return true;
      case 3:
        subFields = newBuilder();
        endGroupTag = WireFormat.makeTag(fieldNumber, 4);
        mergeFrom(subFields, reader);
        if (endGroupTag != reader.getTag())
          throw InvalidProtocolBufferException.invalidEndTag(); 
        addGroup(unknownFields, fieldNumber, toImmutable(subFields));
        return true;
      case 4:
        return false;
    } 
    throw InvalidProtocolBufferException.invalidWireType();
  }
  
  final void mergeFrom(B unknownFields, Reader reader) throws IOException {
    do {
    
    } while (reader.getFieldNumber() != Integer.MAX_VALUE && 
      mergeOneFieldFrom(unknownFields, reader));
  }
  
  abstract void writeTo(T paramT, Writer paramWriter) throws IOException;
  
  abstract void writeAsMessageSetTo(T paramT, Writer paramWriter) throws IOException;
  
  abstract T merge(T paramT1, T paramT2);
  
  abstract int getSerializedSizeAsMessageSet(T paramT);
  
  abstract int getSerializedSize(T paramT);
}
