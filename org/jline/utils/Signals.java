package org.jline.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Objects;

public final class Signals {
  public static Object register(String name, Runnable handler) {
    Objects.requireNonNull(handler);
    return register(name, handler, handler.getClass().getClassLoader());
  }
  
  public static Object register(String name, Runnable handler, ClassLoader loader) {
    try {
      Class<?> signalHandlerClass = Class.forName("sun.misc.SignalHandler");
      Object signalHandler = Proxy.newProxyInstance(loader, new Class[] { signalHandlerClass }, (proxy, method, args) -> {
            if (method.getDeclaringClass() == Object.class) {
              if ("toString".equals(method.getName()))
                return handler.toString(); 
            } else if (method.getDeclaringClass() == signalHandlerClass) {
              Log.trace(());
              handler.run();
            } 
            return null;
          });
      return doRegister(name, signalHandler);
    } catch (Exception e) {
      Log.debug(new Object[] { "Error registering handler for signal ", name, e });
      return null;
    } 
  }
  
  public static Object registerDefault(String name) {
    try {
      Class<?> signalHandlerClass = Class.forName("sun.misc.SignalHandler");
      return doRegister(name, signalHandlerClass.getField("SIG_DFL").get(null));
    } catch (Exception e) {
      Log.debug(new Object[] { "Error registering default handler for signal ", name, e });
      return null;
    } 
  }
  
  public static void unregister(String name, Object previous) {
    try {
      if (previous != null)
        doRegister(name, previous); 
    } catch (Exception e) {
      Log.debug(new Object[] { "Error unregistering handler for signal ", name, e });
    } 
  }
  
  private static Object doRegister(String name, Object handler) throws Exception {
    Object signal;
    Log.trace(() -> "Registering signal " + name + " with handler " + toString(handler));
    Class<?> signalClass = Class.forName("sun.misc.Signal");
    Constructor<?> constructor = signalClass.getConstructor(new Class[] { String.class });
    try {
      signal = constructor.newInstance(new Object[] { name });
    } catch (InvocationTargetException e) {
      if (e.getCause() instanceof IllegalArgumentException) {
        Log.trace(() -> "Ignoring unsupported signal " + name);
      } else {
        Log.debug(new Object[] { "Error registering handler for signal ", name, e });
      } 
      return null;
    } 
    Class<?> signalHandlerClass = Class.forName("sun.misc.SignalHandler");
    return signalClass.getMethod("handle", new Class[] { signalClass, signalHandlerClass }).invoke(null, new Object[] { signal, handler });
  }
  
  private static String toString(Object handler) {
    try {
      Class<?> signalHandlerClass = Class.forName("sun.misc.SignalHandler");
      if (handler == signalHandlerClass.getField("SIG_DFL").get(null))
        return "SIG_DFL"; 
      if (handler == signalHandlerClass.getField("SIG_IGN").get(null))
        return "SIG_IGN"; 
    } catch (Throwable throwable) {}
    return (handler != null) ? handler.toString() : "null";
  }
}
