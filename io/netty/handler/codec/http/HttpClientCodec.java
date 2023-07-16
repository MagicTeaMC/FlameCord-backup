package io.netty.handler.codec.http;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.CombinedChannelDuplexHandler;
import io.netty.handler.codec.PrematureChannelClosureException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicLong;

public final class HttpClientCodec extends CombinedChannelDuplexHandler<HttpResponseDecoder, HttpRequestEncoder> implements HttpClientUpgradeHandler.SourceCodec {
  public static final boolean DEFAULT_FAIL_ON_MISSING_RESPONSE = false;
  
  public static final boolean DEFAULT_PARSE_HTTP_AFTER_CONNECT_REQUEST = false;
  
  private final Queue<HttpMethod> queue = new ArrayDeque<HttpMethod>();
  
  private final boolean parseHttpAfterConnectRequest;
  
  private boolean done;
  
  private final AtomicLong requestResponseCounter = new AtomicLong();
  
  private final boolean failOnMissingResponse;
  
  public HttpClientCodec() {
    this(4096, 8192, 8192, false);
  }
  
  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, false);
  }
  
  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, true);
  }
  
  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, validateHeaders, false);
  }
  
  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders, boolean parseHttpAfterConnectRequest) {
    init((ChannelInboundHandler)new Decoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders), (ChannelOutboundHandler)new Encoder());
    this.failOnMissingResponse = failOnMissingResponse;
    this.parseHttpAfterConnectRequest = parseHttpAfterConnectRequest;
  }
  
  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders, int initialBufferSize) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, validateHeaders, initialBufferSize, false);
  }
  
  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders, int initialBufferSize, boolean parseHttpAfterConnectRequest) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, validateHeaders, initialBufferSize, parseHttpAfterConnectRequest, false);
  }
  
  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders, int initialBufferSize, boolean parseHttpAfterConnectRequest, boolean allowDuplicateContentLengths) {
    this(maxInitialLineLength, maxHeaderSize, maxChunkSize, failOnMissingResponse, validateHeaders, initialBufferSize, parseHttpAfterConnectRequest, allowDuplicateContentLengths, true);
  }
  
  public HttpClientCodec(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean failOnMissingResponse, boolean validateHeaders, int initialBufferSize, boolean parseHttpAfterConnectRequest, boolean allowDuplicateContentLengths, boolean allowPartialChunks) {
    init((ChannelInboundHandler)new Decoder(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths, allowPartialChunks), (ChannelOutboundHandler)new Encoder());
    this.parseHttpAfterConnectRequest = parseHttpAfterConnectRequest;
    this.failOnMissingResponse = failOnMissingResponse;
  }
  
  public void prepareUpgradeFrom(ChannelHandlerContext ctx) {
    ((Encoder)outboundHandler()).upgraded = true;
  }
  
  public void upgradeFrom(ChannelHandlerContext ctx) {
    ChannelPipeline p = ctx.pipeline();
    p.remove((ChannelHandler)this);
  }
  
  public void setSingleDecode(boolean singleDecode) {
    ((HttpResponseDecoder)inboundHandler()).setSingleDecode(singleDecode);
  }
  
  public boolean isSingleDecode() {
    return ((HttpResponseDecoder)inboundHandler()).isSingleDecode();
  }
  
  private final class Encoder extends HttpRequestEncoder {
    boolean upgraded;
    
    private Encoder() {}
    
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
      if (this.upgraded) {
        out.add(msg);
        return;
      } 
      if (msg instanceof HttpRequest)
        HttpClientCodec.this.queue.offer(((HttpRequest)msg).method()); 
      super.encode(ctx, msg, out);
      if (HttpClientCodec.this.failOnMissingResponse && !HttpClientCodec.this.done)
        if (msg instanceof LastHttpContent)
          HttpClientCodec.this.requestResponseCounter.incrementAndGet();  
    }
  }
  
  private final class Decoder extends HttpResponseDecoder {
    Decoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders) {
      super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders);
    }
    
    Decoder(int maxInitialLineLength, int maxHeaderSize, int maxChunkSize, boolean validateHeaders, int initialBufferSize, boolean allowDuplicateContentLengths, boolean allowPartialChunks) {
      super(maxInitialLineLength, maxHeaderSize, maxChunkSize, validateHeaders, initialBufferSize, allowDuplicateContentLengths, allowPartialChunks);
    }
    
    protected void decode(ChannelHandlerContext ctx, ByteBuf buffer, List<Object> out) throws Exception {
      if (HttpClientCodec.this.done) {
        int readable = actualReadableBytes();
        if (readable == 0)
          return; 
        out.add(buffer.readBytes(readable));
      } else {
        int oldSize = out.size();
        super.decode(ctx, buffer, out);
        if (HttpClientCodec.this.failOnMissingResponse) {
          int size = out.size();
          for (int i = oldSize; i < size; i++)
            decrement(out.get(i)); 
        } 
      } 
    }
    
    private void decrement(Object msg) {
      if (msg == null)
        return; 
      if (msg instanceof LastHttpContent)
        HttpClientCodec.this.requestResponseCounter.decrementAndGet(); 
    }
    
    protected boolean isContentAlwaysEmpty(HttpMessage msg) {
      HttpMethod method = HttpClientCodec.this.queue.poll();
      HttpResponseStatus status = ((HttpResponse)msg).status();
      HttpStatusClass statusClass = status.codeClass();
      int statusCode = status.code();
      if (statusClass == HttpStatusClass.INFORMATIONAL)
        return super.isContentAlwaysEmpty(msg); 
      if (method != null) {
        char firstChar = method.name().charAt(0);
        switch (firstChar) {
          case 'H':
            if (HttpMethod.HEAD.equals(method))
              return true; 
            break;
          case 'C':
            if (statusCode == 200 && 
              HttpMethod.CONNECT.equals(method)) {
              if (!HttpClientCodec.this.parseHttpAfterConnectRequest) {
                HttpClientCodec.this.done = true;
                HttpClientCodec.this.queue.clear();
              } 
              return true;
            } 
            break;
        } 
      } 
      return super.isContentAlwaysEmpty(msg);
    }
    
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
      super.channelInactive(ctx);
      if (HttpClientCodec.this.failOnMissingResponse) {
        long missingResponses = HttpClientCodec.this.requestResponseCounter.get();
        if (missingResponses > 0L)
          ctx.fireExceptionCaught((Throwable)new PrematureChannelClosureException("channel gone inactive with " + missingResponses + " missing response(s)")); 
      } 
    }
  }
}
