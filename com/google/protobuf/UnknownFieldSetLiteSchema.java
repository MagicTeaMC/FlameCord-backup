package com.google.protobuf;

import java.io.IOException;

@CheckReturnValue
class UnknownFieldSetLiteSchema extends UnknownFieldSchema<UnknownFieldSetLite, UnknownFieldSetLite> {
  boolean shouldDiscardUnknownFields(Reader reader) {
    return false;
  }
  
  UnknownFieldSetLite newBuilder() {
    return UnknownFieldSetLite.newInstance();
  }
  
  void addVarint(UnknownFieldSetLite fields, int number, long value) {
    fields.storeField(WireFormat.makeTag(number, 0), Long.valueOf(value));
  }
  
  void addFixed32(UnknownFieldSetLite fields, int number, int value) {
    fields.storeField(WireFormat.makeTag(number, 5), Integer.valueOf(value));
  }
  
  void addFixed64(UnknownFieldSetLite fields, int number, long value) {
    fields.storeField(WireFormat.makeTag(number, 1), Long.valueOf(value));
  }
  
  void addLengthDelimited(UnknownFieldSetLite fields, int number, ByteString value) {
    fields.storeField(WireFormat.makeTag(number, 2), value);
  }
  
  void addGroup(UnknownFieldSetLite fields, int number, UnknownFieldSetLite subFieldSet) {
    fields.storeField(WireFormat.makeTag(number, 3), subFieldSet);
  }
  
  UnknownFieldSetLite toImmutable(UnknownFieldSetLite fields) {
    fields.makeImmutable();
    return fields;
  }
  
  void setToMessage(Object message, UnknownFieldSetLite fields) {
    ((GeneratedMessageLite)message).unknownFields = fields;
  }
  
  UnknownFieldSetLite getFromMessage(Object message) {
    return ((GeneratedMessageLite)message).unknownFields;
  }
  
  UnknownFieldSetLite getBuilderFromMessage(Object message) {
    UnknownFieldSetLite unknownFields = getFromMessage(message);
    if (unknownFields == UnknownFieldSetLite.getDefaultInstance()) {
      unknownFields = UnknownFieldSetLite.newInstance();
      setToMessage(message, unknownFields);
    } 
    return unknownFields;
  }
  
  void setBuilderToMessage(Object message, UnknownFieldSetLite fields) {
    setToMessage(message, fields);
  }
  
  void makeImmutable(Object message) {
    getFromMessage(message).makeImmutable();
  }
  
  void writeTo(UnknownFieldSetLite fields, Writer writer) throws IOException {
    fields.writeTo(writer);
  }
  
  void writeAsMessageSetTo(UnknownFieldSetLite fields, Writer writer) throws IOException {
    fields.writeAsMessageSetTo(writer);
  }
  
  UnknownFieldSetLite merge(UnknownFieldSetLite target, UnknownFieldSetLite source) {
    if (UnknownFieldSetLite.getDefaultInstance().equals(source))
      return target; 
    if (UnknownFieldSetLite.getDefaultInstance().equals(target))
      return UnknownFieldSetLite.mutableCopyOf(target, source); 
    return target.mergeFrom(source);
  }
  
  int getSerializedSize(UnknownFieldSetLite unknowns) {
    return unknowns.getSerializedSize();
  }
  
  int getSerializedSizeAsMessageSet(UnknownFieldSetLite unknowns) {
    return unknowns.getSerializedSizeAsMessageSet();
  }
}
