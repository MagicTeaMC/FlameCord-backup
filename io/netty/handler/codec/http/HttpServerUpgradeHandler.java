package io.netty.handler.codec.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.ObjectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HttpServerUpgradeHandler extends HttpObjectAggregator {
  private final SourceCodec sourceCodec;
  
  private final UpgradeCodecFactory upgradeCodecFactory;
  
  private final boolean validateHeaders;
  
  private boolean handlingUpgrade;
  
  public static final class UpgradeEvent implements ReferenceCounted {
    private final CharSequence protocol;
    
    private final FullHttpRequest upgradeRequest;
    
    UpgradeEvent(CharSequence protocol, FullHttpRequest upgradeRequest) {
      this.protocol = protocol;
      this.upgradeRequest = upgradeRequest;
    }
    
    public CharSequence protocol() {
      return this.protocol;
    }
    
    public FullHttpRequest upgradeRequest() {
      return this.upgradeRequest;
    }
    
    public int refCnt() {
      return this.upgradeRequest.refCnt();
    }
    
    public UpgradeEvent retain() {
      this.upgradeRequest.retain();
      return this;
    }
    
    public UpgradeEvent retain(int increment) {
      this.upgradeRequest.retain(increment);
      return this;
    }
    
    public UpgradeEvent touch() {
      this.upgradeRequest.touch();
      return this;
    }
    
    public UpgradeEvent touch(Object hint) {
      this.upgradeRequest.touch(hint);
      return this;
    }
    
    public boolean release() {
      return this.upgradeRequest.release();
    }
    
    public boolean release(int decrement) {
      return this.upgradeRequest.release(decrement);
    }
    
    public String toString() {
      return "UpgradeEvent [protocol=" + this.protocol + ", upgradeRequest=" + this.upgradeRequest + ']';
    }
  }
  
  public HttpServerUpgradeHandler(SourceCodec sourceCodec, UpgradeCodecFactory upgradeCodecFactory) {
    this(sourceCodec, upgradeCodecFactory, 0);
  }
  
  public HttpServerUpgradeHandler(SourceCodec sourceCodec, UpgradeCodecFactory upgradeCodecFactory, int maxContentLength) {
    this(sourceCodec, upgradeCodecFactory, maxContentLength, true);
  }
  
  public HttpServerUpgradeHandler(SourceCodec sourceCodec, UpgradeCodecFactory upgradeCodecFactory, int maxContentLength, boolean validateHeaders) {
    super(maxContentLength);
    this.sourceCodec = (SourceCodec)ObjectUtil.checkNotNull(sourceCodec, "sourceCodec");
    this.upgradeCodecFactory = (UpgradeCodecFactory)ObjectUtil.checkNotNull(upgradeCodecFactory, "upgradeCodecFactory");
    this.validateHeaders = validateHeaders;
  }
  
  protected void decode(ChannelHandlerContext ctx, HttpObject msg, List<Object> out) throws Exception {
    FullHttpRequest fullRequest;
    if (!this.handlingUpgrade)
      if (msg instanceof HttpRequest) {
        HttpRequest req = (HttpRequest)msg;
        if (req.headers().contains((CharSequence)HttpHeaderNames.UPGRADE) && 
          shouldHandleUpgradeRequest(req)) {
          this.handlingUpgrade = true;
        } else {
          ReferenceCountUtil.retain(msg);
          ctx.fireChannelRead(msg);
          return;
        } 
      } else {
        ReferenceCountUtil.retain(msg);
        ctx.fireChannelRead(msg);
        return;
      }  
    if (msg instanceof FullHttpRequest) {
      fullRequest = (FullHttpRequest)msg;
      ReferenceCountUtil.retain(msg);
      out.add(msg);
    } else {
      super.decode(ctx, msg, out);
      if (out.isEmpty())
        return; 
      assert out.size() == 1;
      this.handlingUpgrade = false;
      fullRequest = (FullHttpRequest)out.get(0);
    } 
    if (upgrade(ctx, fullRequest))
      out.clear(); 
  }
  
  protected boolean shouldHandleUpgradeRequest(HttpRequest req) {
    return true;
  }
  
  private boolean upgrade(ChannelHandlerContext ctx, FullHttpRequest request) {
    List<CharSequence> requestedProtocols = splitHeader(request.headers().get((CharSequence)HttpHeaderNames.UPGRADE));
    int numRequestedProtocols = requestedProtocols.size();
    UpgradeCodec upgradeCodec = null;
    CharSequence upgradeProtocol = null;
    for (int i = 0; i < numRequestedProtocols; i++) {
      CharSequence p = requestedProtocols.get(i);
      UpgradeCodec c = this.upgradeCodecFactory.newUpgradeCodec(p);
      if (c != null) {
        upgradeProtocol = p;
        upgradeCodec = c;
        break;
      } 
    } 
    if (upgradeCodec == null)
      return false; 
    List<String> connectionHeaderValues = request.headers().getAll((CharSequence)HttpHeaderNames.CONNECTION);
    if (connectionHeaderValues == null || connectionHeaderValues.isEmpty())
      return false; 
    StringBuilder concatenatedConnectionValue = new StringBuilder(connectionHeaderValues.size() * 10);
    for (CharSequence connectionHeaderValue : connectionHeaderValues)
      concatenatedConnectionValue.append(connectionHeaderValue).append(','); 
    concatenatedConnectionValue.setLength(concatenatedConnectionValue.length() - 1);
    Collection<CharSequence> requiredHeaders = upgradeCodec.requiredUpgradeHeaders();
    List<CharSequence> values = splitHeader(concatenatedConnectionValue);
    if (!AsciiString.containsContentEqualsIgnoreCase(values, (CharSequence)HttpHeaderNames.UPGRADE) || 
      !AsciiString.containsAllContentEqualsIgnoreCase(values, requiredHeaders))
      return false; 
    for (CharSequence requiredHeader : requiredHeaders) {
      if (!request.headers().contains(requiredHeader))
        return false; 
    } 
    FullHttpResponse upgradeResponse = createUpgradeResponse(upgradeProtocol);
    if (!upgradeCodec.prepareUpgradeResponse(ctx, request, upgradeResponse.headers()))
      return false; 
    UpgradeEvent event = new UpgradeEvent(upgradeProtocol, request);
    try {
      ChannelFuture writeComplete = ctx.writeAndFlush(upgradeResponse);
      this.sourceCodec.upgradeFrom(ctx);
      upgradeCodec.upgradeTo(ctx, request);
      ctx.pipeline().remove((ChannelHandler)this);
      ctx.fireUserEventTriggered(event.retain());
      writeComplete.addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
    } finally {
      event.release();
    } 
    return true;
  }
  
  private FullHttpResponse createUpgradeResponse(CharSequence upgradeProtocol) {
    DefaultFullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.SWITCHING_PROTOCOLS, Unpooled.EMPTY_BUFFER, this.validateHeaders);
    res.headers().add((CharSequence)HttpHeaderNames.CONNECTION, HttpHeaderValues.UPGRADE);
    res.headers().add((CharSequence)HttpHeaderNames.UPGRADE, upgradeProtocol);
    return res;
  }
  
  private static List<CharSequence> splitHeader(CharSequence header) {
    StringBuilder builder = new StringBuilder(header.length());
    List<CharSequence> protocols = new ArrayList<CharSequence>(4);
    for (int i = 0; i < header.length(); i++) {
      char c = header.charAt(i);
      if (!Character.isWhitespace(c))
        if (c == ',') {
          protocols.add(builder.toString());
          builder.setLength(0);
        } else {
          builder.append(c);
        }  
    } 
    if (builder.length() > 0)
      protocols.add(builder.toString()); 
    return protocols;
  }
  
  public static interface UpgradeCodecFactory {
    HttpServerUpgradeHandler.UpgradeCodec newUpgradeCodec(CharSequence param1CharSequence);
  }
  
  public static interface UpgradeCodec {
    Collection<CharSequence> requiredUpgradeHeaders();
    
    boolean prepareUpgradeResponse(ChannelHandlerContext param1ChannelHandlerContext, FullHttpRequest param1FullHttpRequest, HttpHeaders param1HttpHeaders);
    
    void upgradeTo(ChannelHandlerContext param1ChannelHandlerContext, FullHttpRequest param1FullHttpRequest);
  }
  
  public static interface SourceCodec {
    void upgradeFrom(ChannelHandlerContext param1ChannelHandlerContext);
  }
}
