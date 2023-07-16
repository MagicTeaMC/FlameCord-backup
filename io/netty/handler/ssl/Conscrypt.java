package io.netty.handler.ssl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.net.ssl.SSLEngine;

final class Conscrypt {
  private static final Method IS_CONSCRYPT_SSLENGINE;
  
  static boolean isAvailable() {
    return (IS_CONSCRYPT_SSLENGINE != null);
  }
  
  static boolean isEngineSupported(SSLEngine engine) {
    try {
      return (IS_CONSCRYPT_SSLENGINE != null && ((Boolean)IS_CONSCRYPT_SSLENGINE.invoke(null, new Object[] { engine })).booleanValue());
    } catch (IllegalAccessException ignore) {
      return false;
    } catch (InvocationTargetException ex) {
      throw new RuntimeException(ex);
    } 
  }
  
  static {
    // Byte code:
    //   0: aconst_null
    //   1: astore_0
    //   2: invokestatic javaVersion : ()I
    //   5: bipush #8
    //   7: if_icmplt -> 18
    //   10: invokestatic javaVersion : ()I
    //   13: bipush #15
    //   15: if_icmplt -> 24
    //   18: invokestatic isAndroid : ()Z
    //   21: ifeq -> 73
    //   24: ldc 'org.conscrypt.OpenSSLProvider'
    //   26: iconst_1
    //   27: ldc io/netty/handler/ssl/ConscryptAlpnSslEngine
    //   29: invokestatic getClassLoader : (Ljava/lang/Class;)Ljava/lang/ClassLoader;
    //   32: invokestatic forName : (Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;
    //   35: astore_1
    //   36: aload_1
    //   37: invokevirtual newInstance : ()Ljava/lang/Object;
    //   40: pop
    //   41: ldc 'org.conscrypt.Conscrypt'
    //   43: iconst_1
    //   44: ldc io/netty/handler/ssl/ConscryptAlpnSslEngine
    //   46: invokestatic getClassLoader : (Ljava/lang/Class;)Ljava/lang/ClassLoader;
    //   49: invokestatic forName : (Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;
    //   52: astore_2
    //   53: aload_2
    //   54: ldc 'isConscrypt'
    //   56: iconst_1
    //   57: anewarray java/lang/Class
    //   60: dup
    //   61: iconst_0
    //   62: ldc javax/net/ssl/SSLEngine
    //   64: aastore
    //   65: invokevirtual getMethod : (Ljava/lang/String;[Ljava/lang/Class;)Ljava/lang/reflect/Method;
    //   68: astore_0
    //   69: goto -> 73
    //   72: astore_1
    //   73: aload_0
    //   74: putstatic io/netty/handler/ssl/Conscrypt.IS_CONSCRYPT_SSLENGINE : Ljava/lang/reflect/Method;
    //   77: return
    // Line number table:
    //   Java source line number -> byte code offset
    //   #33	-> 0
    //   #35	-> 2
    //   #38	-> 10
    //   #40	-> 24
    //   #41	-> 29
    //   #40	-> 32
    //   #42	-> 36
    //   #44	-> 41
    //   #45	-> 46
    //   #44	-> 49
    //   #46	-> 53
    //   #49	-> 69
    //   #47	-> 72
    //   #51	-> 73
    //   #52	-> 77
    // Local variable table:
    //   start	length	slot	name	descriptor
    //   36	33	1	providerClass	Ljava/lang/Class;
    //   53	16	2	conscryptClass	Ljava/lang/Class;
    //   2	75	0	isConscryptSSLEngine	Ljava/lang/reflect/Method;
    // Local variable type table:
    //   start	length	slot	name	signature
    //   36	33	1	providerClass	Ljava/lang/Class<*>;
    //   53	16	2	conscryptClass	Ljava/lang/Class<*>;
    // Exception table:
    //   from	to	target	type
    //   24	69	72	java/lang/Throwable
  }
}
