package io.netty.handler.codec.compression;

import com.aayushatharva.brotli4j.encoder.BrotliEncoderChannel;
import com.aayushatharva.brotli4j.encoder.Encoder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.AttributeKey;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.WritableByteChannel;

@Sharable
public final class BrotliEncoder extends MessageToByteEncoder<ByteBuf> {
  private static final AttributeKey<Writer> ATTR = AttributeKey.valueOf("BrotliEncoderWriter");
  
  private final Encoder.Parameters parameters;
  
  private final boolean isSharable;
  
  private Writer writer;
  
  public BrotliEncoder() {
    this(BrotliOptions.DEFAULT);
  }
  
  public BrotliEncoder(BrotliOptions brotliOptions) {
    this(brotliOptions.parameters());
  }
  
  public BrotliEncoder(Encoder.Parameters parameters) {
    this(parameters, true);
  }
  
  public BrotliEncoder(Encoder.Parameters parameters, boolean isSharable) {
    this.parameters = (Encoder.Parameters)ObjectUtil.checkNotNull(parameters, "Parameters");
    this.isSharable = isSharable;
  }
  
  public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
    Writer writer = new Writer(this.parameters, ctx);
    if (this.isSharable) {
      ctx.channel().attr(ATTR).set(writer);
    } else {
      this.writer = writer;
    } 
    super.handlerAdded(ctx);
  }
  
  public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
    finish(ctx);
    super.handlerRemoved(ctx);
  }
  
  protected void encode(ChannelHandlerContext ctx, ByteBuf msg, ByteBuf out) throws Exception {}
  
  protected ByteBuf allocateBuffer(ChannelHandlerContext ctx, ByteBuf msg, boolean preferDirect) throws Exception {
    Writer writer;
    if (!msg.isReadable())
      return Unpooled.EMPTY_BUFFER; 
    if (this.isSharable) {
      writer = (Writer)ctx.channel().attr(ATTR).get();
    } else {
      writer = this.writer;
    } 
    if (writer == null)
      return Unpooled.EMPTY_BUFFER; 
    writer.encode(msg, preferDirect);
    return writer.writableBuffer;
  }
  
  public boolean isSharable() {
    return this.isSharable;
  }
  
  public void finish(ChannelHandlerContext ctx) throws IOException {
    finishEncode(ctx, ctx.newPromise());
  }
  
  private ChannelFuture finishEncode(ChannelHandlerContext ctx, ChannelPromise promise) throws IOException {
    Writer writer;
    if (this.isSharable) {
      writer = (Writer)ctx.channel().attr(ATTR).getAndSet(null);
    } else {
      writer = this.writer;
    } 
    if (writer != null) {
      writer.close();
      this.writer = null;
    } 
    return (ChannelFuture)promise;
  }
  
  public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
    ChannelFuture f = finishEncode(ctx, ctx.newPromise());
    EncoderUtil.closeAfterFinishEncode(ctx, f, promise);
  }
  
  private static final class Writer implements WritableByteChannel {
    private ByteBuf writableBuffer;
    
    private final BrotliEncoderChannel brotliEncoderChannel;
    
    private final ChannelHandlerContext ctx;
    
    private boolean isClosed;
    
    private Writer(Encoder.Parameters parameters, ChannelHandlerContext ctx) throws IOException {
      this.brotliEncoderChannel = new BrotliEncoderChannel(this, parameters);
      this.ctx = ctx;
    }
    
    private void encode(ByteBuf msg, boolean preferDirect) throws Exception {
      try {
        allocate(preferDirect);
        this.brotliEncoderChannel.write(msg.nioBuffer());
        this.brotliEncoderChannel.flush();
      } catch (Exception e) {
        ReferenceCountUtil.release(msg);
        throw e;
      } 
    }
    
    private void allocate(boolean preferDirect) {
      if (preferDirect) {
        this.writableBuffer = this.ctx.alloc().ioBuffer();
      } else {
        this.writableBuffer = this.ctx.alloc().buffer();
      } 
    }
    
    public int write(ByteBuffer src) throws IOException {
      if (!isOpen())
        throw new ClosedChannelException(); 
      return this.writableBuffer.writeBytes(src).readableBytes();
    }
    
    public boolean isOpen() {
      return !this.isClosed;
    }
    
    public void close() {
      final ChannelPromise promise = this.ctx.newPromise();
      this.ctx.executor().execute(new Runnable() {
            public void run() {
              try {
                BrotliEncoder.Writer.this.finish(promise);
              } catch (IOException ex) {
                promise.setFailure(new IllegalStateException("Failed to finish encoding", ex));
              } 
            }
          });
    }
    
    public void finish(ChannelPromise promise) throws IOException {
      if (!this.isClosed) {
        allocate(true);
        try {
          this.brotliEncoderChannel.close();
          this.isClosed = true;
        } catch (Exception ex) {
          promise.setFailure(ex);
          ReferenceCountUtil.release(this.writableBuffer);
          return;
        } 
        this.ctx.writeAndFlush(this.writableBuffer, promise);
      } 
    }
  }
}
