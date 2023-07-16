package io.netty.handler.codec.protobuf;

import com.google.protobuf.ExtensionRegistry;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.MessageLite;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.util.List;

@Sharable
public class ProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {
  private static final boolean HAS_PARSER;
  
  private final MessageLite prototype;
  
  private final ExtensionRegistryLite extensionRegistry;
  
  static {
    boolean hasParser = false;
    try {
      MessageLite.class.getDeclaredMethod("getParserForType", new Class[0]);
      hasParser = true;
    } catch (Throwable throwable) {}
    HAS_PARSER = hasParser;
  }
  
  public ProtobufDecoder(MessageLite prototype) {
    this(prototype, (ExtensionRegistry)null);
  }
  
  public ProtobufDecoder(MessageLite prototype, ExtensionRegistry extensionRegistry) {
    this(prototype, (ExtensionRegistryLite)extensionRegistry);
  }
  
  public ProtobufDecoder(MessageLite prototype, ExtensionRegistryLite extensionRegistry) {
    this.prototype = ((MessageLite)ObjectUtil.checkNotNull(prototype, "prototype")).getDefaultInstanceForType();
    this.extensionRegistry = extensionRegistry;
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
    byte[] array;
    int offset, length = msg.readableBytes();
    if (msg.hasArray()) {
      array = msg.array();
      offset = msg.arrayOffset() + msg.readerIndex();
    } else {
      array = ByteBufUtil.getBytes(msg, msg.readerIndex(), length, false);
      offset = 0;
    } 
    if (this.extensionRegistry == null) {
      if (HAS_PARSER) {
        out.add(this.prototype.getParserForType().parseFrom(array, offset, length));
      } else {
        out.add(this.prototype.newBuilderForType().mergeFrom(array, offset, length).build());
      } 
    } else if (HAS_PARSER) {
      out.add(this.prototype.getParserForType().parseFrom(array, offset, length, this.extensionRegistry));
    } else {
      out.add(this.prototype.newBuilderForType().mergeFrom(array, offset, length, this.extensionRegistry)
          .build());
    } 
  }
}
