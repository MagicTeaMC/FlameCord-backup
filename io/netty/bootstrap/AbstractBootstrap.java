package io.netty.bootstrap;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFactory;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelPromise;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.ReflectiveChannelFactory;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.SocketUtils;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBootstrap<B extends AbstractBootstrap<B, C>, C extends Channel> implements Cloneable {
  private static final Map.Entry<ChannelOption<?>, Object>[] EMPTY_OPTION_ARRAY = (Map.Entry<ChannelOption<?>, Object>[])new Map.Entry[0];
  
  private static final Map.Entry<AttributeKey<?>, Object>[] EMPTY_ATTRIBUTE_ARRAY = (Map.Entry<AttributeKey<?>, Object>[])new Map.Entry[0];
  
  volatile EventLoopGroup group;
  
  private volatile ChannelFactory<? extends C> channelFactory;
  
  private volatile SocketAddress localAddress;
  
  private final Map<ChannelOption<?>, Object> options = new LinkedHashMap<ChannelOption<?>, Object>();
  
  private final Map<AttributeKey<?>, Object> attrs = new ConcurrentHashMap<AttributeKey<?>, Object>();
  
  private volatile ChannelHandler handler;
  
  AbstractBootstrap(AbstractBootstrap<B, C> bootstrap) {
    this.group = bootstrap.group;
    this.channelFactory = bootstrap.channelFactory;
    this.handler = bootstrap.handler;
    this.localAddress = bootstrap.localAddress;
    synchronized (bootstrap.options) {
      this.options.putAll(bootstrap.options);
    } 
    this.attrs.putAll(bootstrap.attrs);
  }
  
  public B group(EventLoopGroup group) {
    ObjectUtil.checkNotNull(group, "group");
    if (this.group != null)
      throw new IllegalStateException("group set already"); 
    this.group = group;
    return self();
  }
  
  private B self() {
    return (B)this;
  }
  
  public B channel(Class<? extends C> channelClass) {
    return channelFactory((ChannelFactory<? extends C>)new ReflectiveChannelFactory(
          (Class)ObjectUtil.checkNotNull(channelClass, "channelClass")));
  }
  
  @Deprecated
  public B channelFactory(ChannelFactory<? extends C> channelFactory) {
    ObjectUtil.checkNotNull(channelFactory, "channelFactory");
    if (this.channelFactory != null)
      throw new IllegalStateException("channelFactory set already"); 
    this.channelFactory = channelFactory;
    return self();
  }
  
  public B channelFactory(ChannelFactory<? extends C> channelFactory) {
    return channelFactory((ChannelFactory<? extends C>)channelFactory);
  }
  
  public B localAddress(SocketAddress localAddress) {
    this.localAddress = localAddress;
    return self();
  }
  
  public B localAddress(int inetPort) {
    return localAddress(new InetSocketAddress(inetPort));
  }
  
  public B localAddress(String inetHost, int inetPort) {
    return localAddress(SocketUtils.socketAddress(inetHost, inetPort));
  }
  
  public B localAddress(InetAddress inetHost, int inetPort) {
    return localAddress(new InetSocketAddress(inetHost, inetPort));
  }
  
  public <T> B option(ChannelOption<T> option, T value) {
    ObjectUtil.checkNotNull(option, "option");
    synchronized (this.options) {
      if (value == null) {
        this.options.remove(option);
      } else {
        this.options.put(option, value);
      } 
    } 
    return self();
  }
  
  public <T> B attr(AttributeKey<T> key, T value) {
    ObjectUtil.checkNotNull(key, "key");
    if (value == null) {
      this.attrs.remove(key);
    } else {
      this.attrs.put(key, value);
    } 
    return self();
  }
  
  public B validate() {
    if (this.group == null)
      throw new IllegalStateException("group not set"); 
    if (this.channelFactory == null)
      throw new IllegalStateException("channel or channelFactory not set"); 
    return self();
  }
  
  public ChannelFuture register() {
    validate();
    return initAndRegister();
  }
  
  public ChannelFuture bind() {
    validate();
    SocketAddress localAddress = this.localAddress;
    if (localAddress == null)
      throw new IllegalStateException("localAddress not set"); 
    return doBind(localAddress);
  }
  
  public ChannelFuture bind(int inetPort) {
    return bind(new InetSocketAddress(inetPort));
  }
  
  public ChannelFuture bind(String inetHost, int inetPort) {
    return bind(SocketUtils.socketAddress(inetHost, inetPort));
  }
  
  public ChannelFuture bind(InetAddress inetHost, int inetPort) {
    return bind(new InetSocketAddress(inetHost, inetPort));
  }
  
  public ChannelFuture bind(SocketAddress localAddress) {
    validate();
    return doBind((SocketAddress)ObjectUtil.checkNotNull(localAddress, "localAddress"));
  }
  
  private ChannelFuture doBind(final SocketAddress localAddress) {
    final ChannelFuture regFuture = initAndRegister();
    final Channel channel = regFuture.channel();
    if (regFuture.cause() != null)
      return regFuture; 
    if (regFuture.isDone()) {
      ChannelPromise channelPromise = channel.newPromise();
      doBind0(regFuture, channel, localAddress, channelPromise);
      return (ChannelFuture)channelPromise;
    } 
    final PendingRegistrationPromise promise = new PendingRegistrationPromise(channel);
    regFuture.addListener((GenericFutureListener)new ChannelFutureListener() {
          public void operationComplete(ChannelFuture future) throws Exception {
            Throwable cause = future.cause();
            if (cause != null) {
              promise.setFailure(cause);
            } else {
              promise.registered();
              AbstractBootstrap.doBind0(regFuture, channel, localAddress, (ChannelPromise)promise);
            } 
          }
        });
    return (ChannelFuture)promise;
  }
  
  final ChannelFuture initAndRegister() {
    Channel channel = null;
    try {
      channel = (Channel)this.channelFactory.newChannel();
      init(channel);
    } catch (Throwable t) {
      if (channel != null) {
        channel.unsafe().closeForcibly();
        return (ChannelFuture)(new DefaultChannelPromise(channel, (EventExecutor)GlobalEventExecutor.INSTANCE)).setFailure(t);
      } 
      return (ChannelFuture)(new DefaultChannelPromise((Channel)new FailedChannel(), (EventExecutor)GlobalEventExecutor.INSTANCE)).setFailure(t);
    } 
    ChannelFuture regFuture = config().group().register(channel);
    if (regFuture.cause() != null)
      if (channel.isRegistered()) {
        channel.close();
      } else {
        channel.unsafe().closeForcibly();
      }  
    return regFuture;
  }
  
  private static void doBind0(final ChannelFuture regFuture, final Channel channel, final SocketAddress localAddress, final ChannelPromise promise) {
    channel.eventLoop().execute(new Runnable() {
          public void run() {
            if (regFuture.isSuccess()) {
              channel.bind(localAddress, promise).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
            } else {
              promise.setFailure(regFuture.cause());
            } 
          }
        });
  }
  
  public B handler(ChannelHandler handler) {
    this.handler = (ChannelHandler)ObjectUtil.checkNotNull(handler, "handler");
    return self();
  }
  
  @Deprecated
  public final EventLoopGroup group() {
    return this.group;
  }
  
  final Map.Entry<ChannelOption<?>, Object>[] newOptionsArray() {
    return newOptionsArray(this.options);
  }
  
  static Map.Entry<ChannelOption<?>, Object>[] newOptionsArray(Map<ChannelOption<?>, Object> options) {
    synchronized (options) {
      return (Map.Entry<ChannelOption<?>, Object>[])(new LinkedHashMap<Object, Object>(options)).entrySet().toArray((Object[])EMPTY_OPTION_ARRAY);
    } 
  }
  
  final Map.Entry<AttributeKey<?>, Object>[] newAttributesArray() {
    return newAttributesArray(attrs0());
  }
  
  static Map.Entry<AttributeKey<?>, Object>[] newAttributesArray(Map<AttributeKey<?>, Object> attributes) {
    return (Map.Entry<AttributeKey<?>, Object>[])attributes.entrySet().toArray((Object[])EMPTY_ATTRIBUTE_ARRAY);
  }
  
  final Map<ChannelOption<?>, Object> options0() {
    return this.options;
  }
  
  final Map<AttributeKey<?>, Object> attrs0() {
    return this.attrs;
  }
  
  final SocketAddress localAddress() {
    return this.localAddress;
  }
  
  final ChannelFactory<? extends C> channelFactory() {
    return this.channelFactory;
  }
  
  final ChannelHandler handler() {
    return this.handler;
  }
  
  final Map<ChannelOption<?>, Object> options() {
    synchronized (this.options) {
      return copiedMap(this.options);
    } 
  }
  
  final Map<AttributeKey<?>, Object> attrs() {
    return copiedMap(this.attrs);
  }
  
  static <K, V> Map<K, V> copiedMap(Map<K, V> map) {
    if (map.isEmpty())
      return Collections.emptyMap(); 
    return Collections.unmodifiableMap(new HashMap<K, V>(map));
  }
  
  static void setAttributes(Channel channel, Map.Entry<AttributeKey<?>, Object>[] attrs) {
    for (Map.Entry<AttributeKey<?>, Object> e : attrs) {
      AttributeKey<Object> key = (AttributeKey<Object>)e.getKey();
      channel.attr(key).set(e.getValue());
    } 
  }
  
  static void setChannelOptions(Channel channel, Map.Entry<ChannelOption<?>, Object>[] options, InternalLogger logger) {
    for (Map.Entry<ChannelOption<?>, Object> e : options)
      setChannelOption(channel, e.getKey(), e.getValue(), logger); 
  }
  
  private static void setChannelOption(Channel channel, ChannelOption<?> option, Object value, InternalLogger logger) {
    try {
      if (!channel.config().setOption(option, value))
        logger.warn("Unknown channel option '{}' for channel '{}'", option, channel); 
    } catch (Throwable t) {
      logger.warn("Failed to set channel option '{}' with value '{}' for channel '{}'", new Object[] { option, value, channel, t });
    } 
  }
  
  public String toString() {
    StringBuilder buf = (new StringBuilder()).append(StringUtil.simpleClassName(this)).append('(').append(config()).append(')');
    return buf.toString();
  }
  
  AbstractBootstrap() {}
  
  public abstract B clone();
  
  abstract void init(Channel paramChannel) throws Exception;
  
  public abstract AbstractBootstrapConfig<B, C> config();
  
  static final class PendingRegistrationPromise extends DefaultChannelPromise {
    private volatile boolean registered;
    
    PendingRegistrationPromise(Channel channel) {
      super(channel);
    }
    
    void registered() {
      this.registered = true;
    }
    
    protected EventExecutor executor() {
      if (this.registered)
        return super.executor(); 
      return (EventExecutor)GlobalEventExecutor.INSTANCE;
    }
  }
}
