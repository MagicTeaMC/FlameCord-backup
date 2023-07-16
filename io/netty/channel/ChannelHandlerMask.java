package io.netty.channel;

import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.net.SocketAddress;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.Map;
import java.util.WeakHashMap;

final class ChannelHandlerMask {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(ChannelHandlerMask.class);
  
  static final int MASK_EXCEPTION_CAUGHT = 1;
  
  static final int MASK_CHANNEL_REGISTERED = 2;
  
  static final int MASK_CHANNEL_UNREGISTERED = 4;
  
  static final int MASK_CHANNEL_ACTIVE = 8;
  
  static final int MASK_CHANNEL_INACTIVE = 16;
  
  static final int MASK_CHANNEL_READ = 32;
  
  static final int MASK_CHANNEL_READ_COMPLETE = 64;
  
  static final int MASK_USER_EVENT_TRIGGERED = 128;
  
  static final int MASK_CHANNEL_WRITABILITY_CHANGED = 256;
  
  static final int MASK_BIND = 512;
  
  static final int MASK_CONNECT = 1024;
  
  static final int MASK_DISCONNECT = 2048;
  
  static final int MASK_CLOSE = 4096;
  
  static final int MASK_DEREGISTER = 8192;
  
  static final int MASK_READ = 16384;
  
  static final int MASK_WRITE = 32768;
  
  static final int MASK_FLUSH = 65536;
  
  static final int MASK_ONLY_INBOUND = 510;
  
  private static final int MASK_ALL_INBOUND = 511;
  
  static final int MASK_ONLY_OUTBOUND = 130560;
  
  private static final int MASK_ALL_OUTBOUND = 130561;
  
  private static final FastThreadLocal<Map<Class<? extends ChannelHandler>, Integer>> MASKS = new FastThreadLocal<Map<Class<? extends ChannelHandler>, Integer>>() {
      protected Map<Class<? extends ChannelHandler>, Integer> initialValue() {
        return new WeakHashMap<Class<? extends ChannelHandler>, Integer>(32);
      }
    };
  
  static int mask(Class<? extends ChannelHandler> clazz) {
    Map<Class<? extends ChannelHandler>, Integer> cache = (Map<Class<? extends ChannelHandler>, Integer>)MASKS.get();
    Integer mask = cache.get(clazz);
    if (mask == null) {
      mask = Integer.valueOf(mask0(clazz));
      cache.put(clazz, mask);
    } 
    return mask.intValue();
  }
  
  private static int mask0(Class<? extends ChannelHandler> handlerType) {
    int mask = 1;
    try {
      if (ChannelInboundHandler.class.isAssignableFrom(handlerType)) {
        mask |= 0x1FF;
        if (isSkippable(handlerType, "channelRegistered", new Class[] { ChannelHandlerContext.class }))
          mask &= 0xFFFFFFFD; 
        if (isSkippable(handlerType, "channelUnregistered", new Class[] { ChannelHandlerContext.class }))
          mask &= 0xFFFFFFFB; 
        if (isSkippable(handlerType, "channelActive", new Class[] { ChannelHandlerContext.class }))
          mask &= 0xFFFFFFF7; 
        if (isSkippable(handlerType, "channelInactive", new Class[] { ChannelHandlerContext.class }))
          mask &= 0xFFFFFFEF; 
        if (isSkippable(handlerType, "channelRead", new Class[] { ChannelHandlerContext.class, Object.class }))
          mask &= 0xFFFFFFDF; 
        if (isSkippable(handlerType, "channelReadComplete", new Class[] { ChannelHandlerContext.class }))
          mask &= 0xFFFFFFBF; 
        if (isSkippable(handlerType, "channelWritabilityChanged", new Class[] { ChannelHandlerContext.class }))
          mask &= 0xFFFFFEFF; 
        if (isSkippable(handlerType, "userEventTriggered", new Class[] { ChannelHandlerContext.class, Object.class }))
          mask &= 0xFFFFFF7F; 
      } 
      if (ChannelOutboundHandler.class.isAssignableFrom(handlerType)) {
        mask |= 0x1FE01;
        if (isSkippable(handlerType, "bind", new Class[] { ChannelHandlerContext.class, SocketAddress.class, ChannelPromise.class }))
          mask &= 0xFFFFFDFF; 
        if (isSkippable(handlerType, "connect", new Class[] { ChannelHandlerContext.class, SocketAddress.class, SocketAddress.class, ChannelPromise.class }))
          mask &= 0xFFFFFBFF; 
        if (isSkippable(handlerType, "disconnect", new Class[] { ChannelHandlerContext.class, ChannelPromise.class }))
          mask &= 0xFFFFF7FF; 
        if (isSkippable(handlerType, "close", new Class[] { ChannelHandlerContext.class, ChannelPromise.class }))
          mask &= 0xFFFFEFFF; 
        if (isSkippable(handlerType, "deregister", new Class[] { ChannelHandlerContext.class, ChannelPromise.class }))
          mask &= 0xFFFFDFFF; 
        if (isSkippable(handlerType, "read", new Class[] { ChannelHandlerContext.class }))
          mask &= 0xFFFFBFFF; 
        if (isSkippable(handlerType, "write", new Class[] { ChannelHandlerContext.class, Object.class, ChannelPromise.class }))
          mask &= 0xFFFF7FFF; 
        if (isSkippable(handlerType, "flush", new Class[] { ChannelHandlerContext.class }))
          mask &= 0xFFFEFFFF; 
      } 
      if (isSkippable(handlerType, "exceptionCaught", new Class[] { ChannelHandlerContext.class, Throwable.class }))
        mask &= 0xFFFFFFFE; 
    } catch (Exception e) {
      PlatformDependent.throwException(e);
    } 
    return mask;
  }
  
  private static boolean isSkippable(final Class<?> handlerType, final String methodName, Class<?>... paramTypes) throws Exception {
    return ((Boolean)AccessController.<Boolean>doPrivileged(new PrivilegedExceptionAction<Boolean>() {
          public Boolean run() throws Exception {
            Method m;
            try {
              m = handlerType.getMethod(methodName, paramTypes);
            } catch (NoSuchMethodException e) {
              if (ChannelHandlerMask.logger.isDebugEnabled())
                ChannelHandlerMask.logger.debug("Class {} missing method {}, assume we can not skip execution", new Object[] { this.val$handlerType, this.val$methodName, e }); 
              return Boolean.valueOf(false);
            } 
            return Boolean.valueOf(m.isAnnotationPresent((Class)ChannelHandlerMask.Skip.class));
          }
        })).booleanValue();
  }
  
  @Target({ElementType.METHOD})
  @Retention(RetentionPolicy.RUNTIME)
  static @interface Skip {}
}
