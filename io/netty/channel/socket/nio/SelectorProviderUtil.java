package io.netty.channel.socket.nio;

import io.netty.channel.socket.InternetProtocolFamily;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ProtocolFamily;
import java.nio.channels.spi.SelectorProvider;

final class SelectorProviderUtil {
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(SelectorProviderUtil.class);
  
  @SuppressJava6Requirement(reason = "Usage guarded by java version check")
  static Method findOpenMethod(String methodName) {
    if (PlatformDependent.javaVersion() >= 15)
      try {
        return SelectorProvider.class.getMethod(methodName, new Class[] { ProtocolFamily.class });
      } catch (Throwable e) {
        logger.debug("SelectorProvider.{}(ProtocolFamily) not available, will use default", methodName, e);
      }  
    return null;
  }
  
  @SuppressJava6Requirement(reason = "Usage guarded by java version check")
  static <C extends java.nio.channels.Channel> C newChannel(Method method, SelectorProvider provider, InternetProtocolFamily family) throws IOException {
    if (family != null && method != null)
      try {
        return (C)method.invoke(provider, new Object[] { ProtocolFamilyConverter.convert(family) });
      } catch (InvocationTargetException e) {
        throw new IOException(e);
      } catch (IllegalAccessException e) {
        throw new IOException(e);
      }  
    return null;
  }
}
