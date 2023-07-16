package io.netty.resolver.dns;

import io.netty.channel.AddressedEnvelope;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.dns.AbstractDnsOptPseudoRrRecord;
import io.netty.handler.codec.dns.DnsQuery;
import io.netty.handler.codec.dns.DnsQuestion;
import io.netty.handler.codec.dns.DnsRecord;
import io.netty.handler.codec.dns.DnsRecordType;
import io.netty.handler.codec.dns.DnsResponse;
import io.netty.handler.codec.dns.DnsSection;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

abstract class DnsQueryContext {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(DnsQueryContext.class);
  
  private static final long ID_REUSE_ON_TIMEOUT_DELAY_MILLIS = SystemPropertyUtil.getLong("io.netty.resolver.dns.idReuseOnTimeoutDelayMillis", 10000L);
  
  private final Future<? extends Channel> channelReadyFuture;
  
  private final Channel channel;
  
  private final InetSocketAddress nameServerAddr;
  
  private final DnsQueryContextManager queryContextManager;
  
  private final Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise;
  
  private final DnsQuestion question;
  
  private final DnsRecord[] additionals;
  
  private final DnsRecord optResource;
  
  private final boolean recursionDesired;
  
  private volatile Future<?> timeoutFuture;
  
  static {
    logger.debug("-Dio.netty.resolver.dns.idReuseOnTimeoutDelayMillis: {}", Long.valueOf(ID_REUSE_ON_TIMEOUT_DELAY_MILLIS));
  }
  
  private int id = -1;
  
  DnsQueryContext(Channel channel, Future<? extends Channel> channelReadyFuture, InetSocketAddress nameServerAddr, DnsQueryContextManager queryContextManager, int maxPayLoadSize, boolean recursionDesired, DnsQuestion question, DnsRecord[] additionals, Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>> promise) {
    this.channel = (Channel)ObjectUtil.checkNotNull(channel, "channel");
    this.queryContextManager = (DnsQueryContextManager)ObjectUtil.checkNotNull(queryContextManager, "queryContextManager");
    this.channelReadyFuture = (Future<? extends Channel>)ObjectUtil.checkNotNull(channelReadyFuture, "channelReadyFuture");
    this.nameServerAddr = (InetSocketAddress)ObjectUtil.checkNotNull(nameServerAddr, "nameServerAddr");
    this.question = (DnsQuestion)ObjectUtil.checkNotNull(question, "question");
    this.additionals = (DnsRecord[])ObjectUtil.checkNotNull(additionals, "additionals");
    this.promise = (Promise<AddressedEnvelope<DnsResponse, InetSocketAddress>>)ObjectUtil.checkNotNull(promise, "promise");
    this.recursionDesired = recursionDesired;
    if (maxPayLoadSize > 0 && 
      
      !hasOptRecord(additionals)) {
      this.optResource = (DnsRecord)new AbstractDnsOptPseudoRrRecord(maxPayLoadSize, 0, 0) {
        
        };
    } else {
      this.optResource = null;
    } 
  }
  
  private static boolean hasOptRecord(DnsRecord[] additionals) {
    if (additionals != null && additionals.length > 0)
      for (DnsRecord additional : additionals) {
        if (additional.type() == DnsRecordType.OPT)
          return true; 
      }  
    return false;
  }
  
  final boolean isDone() {
    return this.promise.isDone();
  }
  
  final DnsQuestion question() {
    return this.question;
  }
  
  final ChannelFuture writeQuery(long queryTimeoutMillis, boolean flush) {
    assert this.id == -1 : getClass().getSimpleName() + ".writeQuery(...) can only be executed once.";
    this.id = this.queryContextManager.add(this.nameServerAddr, this);
    this.promise.addListener((GenericFutureListener)new FutureListener<AddressedEnvelope<DnsResponse, InetSocketAddress>>() {
          public void operationComplete(Future<AddressedEnvelope<DnsResponse, InetSocketAddress>> future) {
            Future<?> timeoutFuture = DnsQueryContext.this.timeoutFuture;
            if (timeoutFuture != null) {
              DnsQueryContext.this.timeoutFuture = null;
              timeoutFuture.cancel(false);
            } 
            Throwable cause = future.cause();
            if (cause instanceof DnsNameResolverTimeoutException || cause instanceof java.util.concurrent.CancellationException) {
              DnsQueryContext.this.channel.eventLoop().schedule(new Runnable() {
                    public void run() {
                      DnsQueryContext.this.removeFromContextManager(DnsQueryContext.this.nameServerAddr);
                    }
                  },  DnsQueryContext.ID_REUSE_ON_TIMEOUT_DELAY_MILLIS, TimeUnit.MILLISECONDS);
            } else {
              DnsQueryContext.this.removeFromContextManager(DnsQueryContext.this.nameServerAddr);
            } 
          }
        });
    DnsQuestion question = question();
    DnsQuery query = newQuery(this.id, this.nameServerAddr);
    query.setRecursionDesired(this.recursionDesired);
    query.addRecord(DnsSection.QUESTION, (DnsRecord)question);
    for (DnsRecord record : this.additionals)
      query.addRecord(DnsSection.ADDITIONAL, record); 
    if (this.optResource != null)
      query.addRecord(DnsSection.ADDITIONAL, this.optResource); 
    if (logger.isDebugEnabled())
      logger.debug("{} WRITE: {}, [{}: {}], {}", new Object[] { this.channel, 
            protocol(), Integer.valueOf(this.id), this.nameServerAddr, question }); 
    return sendQuery(this.nameServerAddr, query, queryTimeoutMillis, flush);
  }
  
  private void removeFromContextManager(InetSocketAddress nameServerAddr) {
    DnsQueryContext self = this.queryContextManager.remove(nameServerAddr, this.id);
    assert self == this : "Removed DnsQueryContext is not the correct instance";
  }
  
  private ChannelFuture sendQuery(final InetSocketAddress nameServerAddr, final DnsQuery query, final long queryTimeoutMillis, boolean flush) {
    final ChannelPromise writePromise = this.channel.newPromise();
    if (this.channelReadyFuture.isSuccess()) {
      writeQuery(nameServerAddr, query, queryTimeoutMillis, flush, writePromise);
    } else {
      Throwable cause = this.channelReadyFuture.cause();
      if (cause != null) {
        failQuery(query, cause, writePromise);
      } else {
        this.channelReadyFuture.addListener(new GenericFutureListener<Future<? super Channel>>() {
              public void operationComplete(Future<? super Channel> future) {
                if (future.isSuccess()) {
                  DnsQueryContext.this.writeQuery(nameServerAddr, query, queryTimeoutMillis, true, writePromise);
                } else {
                  Throwable cause = future.cause();
                  DnsQueryContext.this.failQuery(query, cause, writePromise);
                } 
              }
            });
      } 
    } 
    return (ChannelFuture)writePromise;
  }
  
  private void failQuery(DnsQuery query, Throwable cause, ChannelPromise writePromise) {
    try {
      this.promise.tryFailure(cause);
      writePromise.tryFailure(cause);
    } finally {
      ReferenceCountUtil.release(query);
    } 
  }
  
  private void writeQuery(InetSocketAddress nameServerAddr, DnsQuery query, final long queryTimeoutMillis, boolean flush, ChannelPromise promise) {
    final ChannelFuture writeFuture = flush ? this.channel.writeAndFlush(query, promise) : this.channel.write(query, promise);
    if (writeFuture.isDone()) {
      onQueryWriteCompletion(queryTimeoutMillis, writeFuture);
    } else {
      writeFuture.addListener((GenericFutureListener)new ChannelFutureListener() {
            public void operationComplete(ChannelFuture future) {
              DnsQueryContext.this.onQueryWriteCompletion(queryTimeoutMillis, writeFuture);
            }
          });
    } 
  }
  
  private void onQueryWriteCompletion(final long queryTimeoutMillis, ChannelFuture writeFuture) {
    if (!writeFuture.isSuccess()) {
      finishFailure("failed to send a query '" + this.id + "' via " + protocol(), writeFuture.cause(), false);
      return;
    } 
    if (queryTimeoutMillis > 0L)
      this.timeoutFuture = (Future<?>)this.channel.eventLoop().schedule(new Runnable() {
            public void run() {
              if (DnsQueryContext.this.promise.isDone())
                return; 
              DnsQueryContext.this.finishFailure("query '" + DnsQueryContext.this.id + "' via " + DnsQueryContext.this.protocol() + " timed out after " + queryTimeoutMillis + " milliseconds", null, true);
            }
          }queryTimeoutMillis, TimeUnit.MILLISECONDS); 
  }
  
  void finishSuccess(AddressedEnvelope<? extends DnsResponse, InetSocketAddress> envelope) {
    DnsResponse res = (DnsResponse)envelope.content();
    if (res.count(DnsSection.QUESTION) != 1) {
      logger.warn("{} Received a DNS response with invalid number of questions. Expected: 1, found: {}", this.channel, envelope);
    } else if (!question().equals(res.recordAt(DnsSection.QUESTION))) {
      logger.warn("{} Received a mismatching DNS response. Expected: [{}], found: {}", new Object[] { this.channel, 
            question(), envelope });
    } else if (trySuccess(envelope)) {
      return;
    } 
    envelope.release();
  }
  
  private boolean trySuccess(AddressedEnvelope<? extends DnsResponse, InetSocketAddress> envelope) {
    return this.promise.trySuccess(envelope);
  }
  
  final boolean finishFailure(String message, Throwable cause, boolean timeout) {
    DnsNameResolverException e;
    if (this.promise.isDone())
      return false; 
    DnsQuestion question = question();
    StringBuilder buf = new StringBuilder(message.length() + 128);
    buf.append('[')
      .append(this.id)
      .append(": ")
      .append(this.nameServerAddr)
      .append("] ")
      .append(question)
      .append(' ')
      .append(message)
      .append(" (no stack trace available)");
    if (timeout) {
      e = new DnsNameResolverTimeoutException(this.nameServerAddr, question, buf.toString());
    } else {
      e = new DnsNameResolverException(this.nameServerAddr, question, buf.toString(), cause);
    } 
    return this.promise.tryFailure(e);
  }
  
  protected abstract DnsQuery newQuery(int paramInt, InetSocketAddress paramInetSocketAddress);
  
  protected abstract String protocol();
}
