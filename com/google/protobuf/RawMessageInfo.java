package com.google.protobuf;

@CheckReturnValue
final class RawMessageInfo implements MessageInfo {
  private final MessageLite defaultInstance;
  
  private final String info;
  
  private final Object[] objects;
  
  private final int flags;
  
  RawMessageInfo(MessageLite defaultInstance, String info, Object[] objects) {
    this.defaultInstance = defaultInstance;
    this.info = info;
    this.objects = objects;
    int position = 0;
    int value = info.charAt(position++);
    if (value < 55296) {
      this.flags = value;
    } else {
      int result = value & 0x1FFF;
      int shift = 13;
      while ((value = info.charAt(position++)) >= 55296) {
        result |= (value & 0x1FFF) << shift;
        shift += 13;
      } 
      this.flags = result | value << shift;
    } 
  }
  
  String getStringInfo() {
    return this.info;
  }
  
  Object[] getObjects() {
    return this.objects;
  }
  
  public MessageLite getDefaultInstance() {
    return this.defaultInstance;
  }
  
  public ProtoSyntax getSyntax() {
    return ((this.flags & 0x1) == 1) ? ProtoSyntax.PROTO2 : ProtoSyntax.PROTO3;
  }
  
  public boolean isMessageSetWireFormat() {
    return ((this.flags & 0x2) == 2);
  }
}
