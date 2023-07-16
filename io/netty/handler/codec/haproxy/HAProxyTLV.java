package io.netty.handler.codec.haproxy;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufHolder;
import io.netty.buffer.DefaultByteBufHolder;
import io.netty.util.ReferenceCounted;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;

public class HAProxyTLV extends DefaultByteBufHolder {
  private final Type type;
  
  private final byte typeByteValue;
  
  int totalNumBytes() {
    return 3 + contentNumBytes();
  }
  
  int contentNumBytes() {
    return content().readableBytes();
  }
  
  public enum Type {
    PP2_TYPE_ALPN, PP2_TYPE_AUTHORITY, PP2_TYPE_SSL, PP2_TYPE_SSL_VERSION, PP2_TYPE_SSL_CN, PP2_TYPE_NETNS, OTHER;
    
    public static Type typeForByteValue(byte byteValue) {
      switch (byteValue) {
        case 1:
          return PP2_TYPE_ALPN;
        case 2:
          return PP2_TYPE_AUTHORITY;
        case 32:
          return PP2_TYPE_SSL;
        case 33:
          return PP2_TYPE_SSL_VERSION;
        case 34:
          return PP2_TYPE_SSL_CN;
        case 48:
          return PP2_TYPE_NETNS;
      } 
      return OTHER;
    }
    
    public static byte byteValueForType(Type type) {
      switch (type) {
        case PP2_TYPE_ALPN:
          return 1;
        case PP2_TYPE_AUTHORITY:
          return 2;
        case PP2_TYPE_SSL:
          return 32;
        case PP2_TYPE_SSL_VERSION:
          return 33;
        case PP2_TYPE_SSL_CN:
          return 34;
        case PP2_TYPE_NETNS:
          return 48;
      } 
      throw new IllegalArgumentException("unknown type: " + type);
    }
  }
  
  public HAProxyTLV(byte typeByteValue, ByteBuf content) {
    this(Type.typeForByteValue(typeByteValue), typeByteValue, content);
  }
  
  public HAProxyTLV(Type type, ByteBuf content) {
    this(type, Type.byteValueForType(type), content);
  }
  
  HAProxyTLV(Type type, byte typeByteValue, ByteBuf content) {
    super(content);
    this.type = (Type)ObjectUtil.checkNotNull(type, "type");
    this.typeByteValue = typeByteValue;
  }
  
  public Type type() {
    return this.type;
  }
  
  public byte typeByteValue() {
    return this.typeByteValue;
  }
  
  public HAProxyTLV copy() {
    return replace(content().copy());
  }
  
  public HAProxyTLV duplicate() {
    return replace(content().duplicate());
  }
  
  public HAProxyTLV retainedDuplicate() {
    return replace(content().retainedDuplicate());
  }
  
  public HAProxyTLV replace(ByteBuf content) {
    return new HAProxyTLV(this.type, this.typeByteValue, content);
  }
  
  public HAProxyTLV retain() {
    super.retain();
    return this;
  }
  
  public HAProxyTLV retain(int increment) {
    super.retain(increment);
    return this;
  }
  
  public HAProxyTLV touch() {
    super.touch();
    return this;
  }
  
  public HAProxyTLV touch(Object hint) {
    super.touch(hint);
    return this;
  }
  
  public String toString() {
    return StringUtil.simpleClassName(this) + "(type: " + 
      type() + ", typeByteValue: " + 
      typeByteValue() + ", content: " + 
      contentToString() + ')';
  }
}
