package io.netty.handler.ssl;

import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.SuppressJava6Requirement;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.function.BiFunction;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

@SuppressJava6Requirement(reason = "Usage guarded by java version check")
final class BouncyCastleAlpnSslUtils {
  static {
    Method getParameters, setParameters, setApplicationProtocols, getApplicationProtocol, getHandshakeApplicationProtocol, setHandshakeApplicationProtocolSelector, getHandshakeApplicationProtocolSelector, bcApplicationProtocolSelectorSelect;
    Class bcApplicationProtocolSelector;
  }
  
  private static final InternalLogger logger = InternalLoggerFactory.getInstance(BouncyCastleAlpnSslUtils.class);
  
  private static final Method SET_PARAMETERS;
  
  private static final Method GET_PARAMETERS;
  
  private static final Method SET_APPLICATION_PROTOCOLS;
  
  private static final Method GET_APPLICATION_PROTOCOL;
  
  private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL;
  
  private static final Method SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
  
  private static final Method GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR;
  
  private static final Class BC_APPLICATION_PROTOCOL_SELECTOR;
  
  private static final Method BC_APPLICATION_PROTOCOL_SELECTOR_SELECT;
  
  static {
    try {
      Class<?> bcSslEngine = Class.forName("org.bouncycastle.jsse.BCSSLEngine");
      final Class<?> testBCSslEngine = bcSslEngine;
      bcApplicationProtocolSelector = Class.forName("org.bouncycastle.jsse.BCApplicationProtocolSelector");
      final Class<?> testBCApplicationProtocolSelector = bcApplicationProtocolSelector;
      bcApplicationProtocolSelectorSelect = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
              return testBCApplicationProtocolSelector.getMethod("select", new Class[] { Object.class, List.class });
            }
          });
      SSLContext context = SslUtils.getSSLContext("BCJSSE");
      SSLEngine engine = context.createSSLEngine();
      getParameters = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
              return testBCSslEngine.getMethod("getParameters", new Class[0]);
            }
          });
      Object bcSslParameters = getParameters.invoke(engine, new Object[0]);
      final Class<?> bCSslParametersClass = bcSslParameters.getClass();
      setParameters = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
              return testBCSslEngine.getMethod("setParameters", new Class[] { this.val$bCSslParametersClass });
            }
          });
      setParameters.invoke(engine, new Object[] { bcSslParameters });
      setApplicationProtocols = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
              return bCSslParametersClass.getMethod("setApplicationProtocols", new Class[] { String[].class });
            }
          });
      setApplicationProtocols.invoke(bcSslParameters, new Object[] { EmptyArrays.EMPTY_STRINGS });
      getApplicationProtocol = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
              return testBCSslEngine.getMethod("getApplicationProtocol", new Class[0]);
            }
          });
      getApplicationProtocol.invoke(engine, new Object[0]);
      getHandshakeApplicationProtocol = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
              return testBCSslEngine.getMethod("getHandshakeApplicationProtocol", new Class[0]);
            }
          });
      getHandshakeApplicationProtocol.invoke(engine, new Object[0]);
      setHandshakeApplicationProtocolSelector = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
              return testBCSslEngine.getMethod("setBCHandshakeApplicationProtocolSelector", new Class[] { this.val$testBCApplicationProtocolSelector });
            }
          });
      getHandshakeApplicationProtocolSelector = AccessController.<Method>doPrivileged(new PrivilegedExceptionAction<Method>() {
            public Method run() throws Exception {
              return testBCSslEngine.getMethod("getBCHandshakeApplicationProtocolSelector", new Class[0]);
            }
          });
      getHandshakeApplicationProtocolSelector.invoke(engine, new Object[0]);
    } catch (Throwable t) {
      logger.error("Unable to initialize BouncyCastleAlpnSslUtils.", t);
      setParameters = null;
      getParameters = null;
      setApplicationProtocols = null;
      getApplicationProtocol = null;
      getHandshakeApplicationProtocol = null;
      setHandshakeApplicationProtocolSelector = null;
      getHandshakeApplicationProtocolSelector = null;
      bcApplicationProtocolSelectorSelect = null;
      bcApplicationProtocolSelector = null;
    } 
    SET_PARAMETERS = setParameters;
    GET_PARAMETERS = getParameters;
    SET_APPLICATION_PROTOCOLS = setApplicationProtocols;
    GET_APPLICATION_PROTOCOL = getApplicationProtocol;
    GET_HANDSHAKE_APPLICATION_PROTOCOL = getHandshakeApplicationProtocol;
    SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = setHandshakeApplicationProtocolSelector;
    GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR = getHandshakeApplicationProtocolSelector;
    BC_APPLICATION_PROTOCOL_SELECTOR_SELECT = bcApplicationProtocolSelectorSelect;
    BC_APPLICATION_PROTOCOL_SELECTOR = bcApplicationProtocolSelector;
  }
  
  static String getApplicationProtocol(SSLEngine sslEngine) {
    try {
      return (String)GET_APPLICATION_PROTOCOL.invoke(sslEngine, new Object[0]);
    } catch (UnsupportedOperationException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    } 
  }
  
  static void setApplicationProtocols(SSLEngine engine, List<String> supportedProtocols) {
    String[] protocolArray = supportedProtocols.<String>toArray(EmptyArrays.EMPTY_STRINGS);
    try {
      Object bcSslParameters = GET_PARAMETERS.invoke(engine, new Object[0]);
      SET_APPLICATION_PROTOCOLS.invoke(bcSslParameters, new Object[] { protocolArray });
      SET_PARAMETERS.invoke(engine, new Object[] { bcSslParameters });
    } catch (UnsupportedOperationException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    } 
    if (PlatformDependent.javaVersion() >= 9)
      JdkAlpnSslUtils.setApplicationProtocols(engine, supportedProtocols); 
  }
  
  static String getHandshakeApplicationProtocol(SSLEngine sslEngine) {
    try {
      return (String)GET_HANDSHAKE_APPLICATION_PROTOCOL.invoke(sslEngine, new Object[0]);
    } catch (UnsupportedOperationException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    } 
  }
  
  static void setHandshakeApplicationProtocolSelector(SSLEngine engine, final BiFunction<SSLEngine, List<String>, String> selector) {
    try {
      Object selectorProxyInstance = Proxy.newProxyInstance(BouncyCastleAlpnSslUtils.class
          .getClassLoader(), new Class[] { BC_APPLICATION_PROTOCOL_SELECTOR }, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
              if (method.getName().equals("select"))
                try {
                  return selector.apply(args[0], args[1]);
                } catch (ClassCastException e) {
                  throw new RuntimeException("BCApplicationProtocolSelector select method parameter of invalid type.", e);
                }  
              throw new UnsupportedOperationException(String.format("Method '%s' not supported.", new Object[] { method
                      .getName() }));
            }
          });
      SET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, new Object[] { selectorProxyInstance });
    } catch (UnsupportedOperationException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    } 
  }
  
  static BiFunction<SSLEngine, List<String>, String> getHandshakeApplicationProtocolSelector(SSLEngine engine) {
    try {
      final Object selector = GET_HANDSHAKE_APPLICATION_PROTOCOL_SELECTOR.invoke(engine, new Object[0]);
      return new BiFunction<SSLEngine, List<String>, String>() {
          public String apply(SSLEngine sslEngine, List<String> strings) {
            try {
              return (String)BouncyCastleAlpnSslUtils.BC_APPLICATION_PROTOCOL_SELECTOR_SELECT.invoke(selector, new Object[] { sslEngine, strings });
            } catch (Exception e) {
              throw new RuntimeException("Could not call getHandshakeApplicationProtocolSelector", e);
            } 
          }
        };
    } catch (UnsupportedOperationException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new IllegalStateException(ex);
    } 
  }
}
