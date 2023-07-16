package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.decoder.DecoderJNI;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.util.internal.ObjectUtil;
import java.nio.ByteBuffer;
import java.util.List;

public final class BrotliDecoder extends ByteToMessageDecoder {
  private final int inputBufferSize;
  
  private DecoderJNI.Wrapper decoder;
  
  private boolean destroyed;
  
  private enum State {
    DONE, NEEDS_MORE_INPUT, ERROR;
  }
  
  static {
    try {
      Brotli.ensureAvailability();
    } catch (Throwable throwable) {
      throw new ExceptionInInitializerError(throwable);
    } 
  }
  
  public BrotliDecoder() {
    this(8192);
  }
  
  public BrotliDecoder(int inputBufferSize) {
    this.inputBufferSize = ObjectUtil.checkPositive(inputBufferSize, "inputBufferSize");
  }
  
  private ByteBuf pull(ByteBufAllocator alloc) {
    ByteBuffer nativeBuffer = this.decoder.pull();
    ByteBuf copy = alloc.buffer(nativeBuffer.remaining());
    copy.writeBytes(nativeBuffer);
    return copy;
  }
  
  private State decompress(ByteBuf input, List<Object> output, ByteBufAllocator alloc) {
    while (true) {
      ByteBuffer decoderInputBuffer;
      int readBytes;
      switch (this.decoder.getStatus()) {
        case DONE:
          return State.DONE;
        case OK:
          this.decoder.push(0);
          continue;
        case NEEDS_MORE_INPUT:
          if (this.decoder.hasOutput())
            output.add(pull(alloc)); 
          if (!input.isReadable())
            return State.NEEDS_MORE_INPUT; 
          decoderInputBuffer = this.decoder.getInputBuffer();
          decoderInputBuffer.clear();
          readBytes = readBytes(input, decoderInputBuffer);
          this.decoder.push(readBytes);
          continue;
        case NEEDS_MORE_OUTPUT:
          output.add(pull(alloc));
          continue;
      } 
      break;
    } 
    return State.ERROR;
  }
  
  private static int readBytes(ByteBuf in, ByteBuffer dest) {
    int limit = Math.min(in.readableBytes(), dest.remaining());
    ByteBuffer slice = dest.slice();
    slice.limit(limit);
    in.readBytes(slice);
    dest.position(dest.position() + limit);
    return limit;
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    this.decoder = new DecoderJNI.Wrapper(this.inputBufferSize);
  }
  
  protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
    if (this.destroyed) {
      in.skipBytes(in.readableBytes());
      return;
    } 
    if (!in.isReadable())
      return; 
    try {
      State state = decompress(in, out, ctx.alloc());
      if (state == State.DONE) {
        destroy();
      } else if (state == State.ERROR) {
        throw new DecompressionException("Brotli stream corrupted");
      } 
    } catch (Exception e) {
      destroy();
      throw e;
    } 
  }
  
  private void destroy() {
    if (!this.destroyed) {
      this.destroyed = true;
      this.decoder.destroy();
    } 
  }
  
  protected void handlerRemoved0(ChannelHandlerContext ctx) throws Exception {
    try {
      destroy();
    } finally {
      super.handlerRemoved0(ctx);
    } 
  }
  
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    try {
      destroy();
    } finally {
      super.channelInactive(ctx);
    } 
  }
}
