package com.google.protobuf;

public abstract class Extension<ContainingType extends MessageLite, Type> extends ExtensionLite<ContainingType, Type> {
  final boolean isLite() {
    return false;
  }
  
  protected enum ExtensionType {
    IMMUTABLE, MUTABLE, PROTO1;
  }
  
  public enum MessageType {
    PROTO1, PROTO2;
  }
  
  public MessageType getMessageType() {
    return MessageType.PROTO2;
  }
  
  public abstract Message getMessageDefaultInstance();
  
  public abstract Descriptors.FieldDescriptor getDescriptor();
  
  protected abstract ExtensionType getExtensionType();
  
  protected abstract Object fromReflectionType(Object paramObject);
  
  protected abstract Object singularFromReflectionType(Object paramObject);
  
  protected abstract Object toReflectionType(Object paramObject);
  
  protected abstract Object singularToReflectionType(Object paramObject);
}
