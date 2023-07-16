package io.netty.util.internal;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;

final class CleanerJava9 implements Cleaner {
  static {
    Method method;
    Throwable error;
  }
  
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(CleanerJava9.class);
  
  private static final Method INVOKE_CLEANER;
  
  static {
    if (PlatformDependent0.hasUnsafe()) {
      final ByteBuffer buffer = ByteBuffer.allocateDirect(1);
      Object maybeInvokeMethod = AccessController.doPrivileged(new PrivilegedAction() {
            public Object run() {
              try {
                Method m = PlatformDependent0.UNSAFE.getClass().getDeclaredMethod("invokeCleaner", new Class[] { ByteBuffer.class });
                m.invoke(PlatformDependent0.UNSAFE, new Object[] { this.val$buffer });
                return m;
              } catch (NoSuchMethodException e) {
                return e;
              } catch (InvocationTargetException e) {
                return e;
              } catch (IllegalAccessException e) {
                return e;
              } 
            }
          });
      if (maybeInvokeMethod instanceof Throwable) {
        method = null;
        error = (Throwable)maybeInvokeMethod;
      } else {
        method = (Method)maybeInvokeMethod;
        error = null;
      } 
    } else {
      method = null;
      error = new UnsupportedOperationException("sun.misc.Unsafe unavailable");
    } 
    if (error == null) {
      logger.debug("java.nio.ByteBuffer.cleaner(): available");
    } else {
      logger.debug("java.nio.ByteBuffer.cleaner(): unavailable", error);
    } 
    INVOKE_CLEANER = method;
  }
  
  static boolean isSupported() {
    return (INVOKE_CLEANER != null);
  }
  
  public void freeDirectBuffer(ByteBuffer buffer) {
    if (System.getSecurityManager() == null) {
      try {
        INVOKE_CLEANER.invoke(PlatformDependent0.UNSAFE, new Object[] { buffer });
      } catch (Throwable cause) {
        PlatformDependent0.throwException(cause);
      } 
    } else {
      freeDirectBufferPrivileged(buffer);
    } 
  }
  
  private static void freeDirectBufferPrivileged(final ByteBuffer buffer) {
    Exception error = AccessController.<Exception>doPrivileged(new PrivilegedAction<Exception>() {
          public Exception run() {
            try {
              CleanerJava9.INVOKE_CLEANER.invoke(PlatformDependent0.UNSAFE, new Object[] { this.val$buffer });
            } catch (InvocationTargetException e) {
              return e;
            } catch (IllegalAccessException e) {
              return e;
            } 
            return null;
          }
        });
    if (error != null)
      PlatformDependent0.throwException(error); 
  }
}
