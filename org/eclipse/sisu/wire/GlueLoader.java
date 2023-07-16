package org.eclipse.sisu.wire;

import com.google.inject.ProvisionException;
import com.google.inject.TypeLiteral;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.ConcurrentMap;
import javax.inject.Provider;
import org.eclipse.sisu.inject.Weak;

final class GlueLoader extends ClassLoader {
  private static final Object SYSTEM_LOADER_LOCK = new Object();
  
  private static final String PROVIDER_NAME = Provider.class.getName();
  
  private static final String GLUE_SUFFIX = "$__sisu__$";
  
  private static final String DYNAMIC = "dyn";
  
  private static final ConcurrentMap<Integer, GlueLoader> cachedGlue = Weak.concurrentValues();
  
  GlueLoader() {}
  
  GlueLoader(ClassLoader parent) {
    super(parent);
  }
  
  public static <T> T dynamicGlue(TypeLiteral<T> type, Provider<T> provider) {
    try {
      return dynamicGlue(type.getRawType()).getConstructor(new Class[] { Provider.class }).newInstance(new Object[] { provider });
    } catch (Exception e) {
      Throwable cause = (e instanceof java.lang.reflect.InvocationTargetException) ? e.getCause() : e;
      throw new ProvisionException("Error proxying: " + type, cause);
    } catch (LinkageError e) {
      throw new ProvisionException("Error proxying: " + type, e);
    } 
  }
  
  protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
    if (PROVIDER_NAME.equals(name))
      return Provider.class; 
    return super.loadClass(name, resolve);
  }
  
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    if (name.endsWith("$__sisu__$dyn")) {
      Class<?> facade = loadClass(unwrap(name));
      byte[] code = DynamicGlue.generateProxyClass(name.replace('.', '/'), facade);
      return defineClass(name, code, 0, code.length);
    } 
    throw new ClassNotFoundException(name);
  }
  
  private static Class<?> dynamicGlue(Class<?> facade) throws ClassNotFoundException {
    return glue(facade.getClassLoader()).loadClass(wrap(facade.getName(), "dyn"));
  }
  
  private static String wrap(String name, String kind) {
    StringBuilder buf = new StringBuilder();
    if (name.startsWith("java.") || name.startsWith("java/"))
      buf.append('$'); 
    return buf.append(name).append("$__sisu__$").append(kind).toString();
  }
  
  private static String unwrap(String name) {
    int head = ('$' == name.charAt(0)) ? 1 : 0;
    int tail = name.lastIndexOf("$__sisu__$");
    return (tail > 0) ? name.substring(head, tail) : name;
  }
  
  private static GlueLoader glue(ClassLoader parent) {
    int id = System.identityHashCode(parent);
    GlueLoader result = cachedGlue.get(Integer.valueOf(id));
    if (result == null || result.getParent() != parent)
      synchronized ((parent != null) ? parent : SYSTEM_LOADER_LOCK) {
        GlueLoader glue = createGlue(parent);
        while (true) {
          result = cachedGlue.putIfAbsent(Integer.valueOf(id++), glue);
          if (result == null)
            return glue; 
          if (result.getParent() == parent)
            return result; 
        } 
      }  
    return result;
  }
  
  private static GlueLoader createGlue(final ClassLoader parent) {
    return AccessController.<GlueLoader>doPrivileged(new PrivilegedAction<GlueLoader>() {
          public GlueLoader run() {
            return (parent != null) ? new GlueLoader(parent) : new GlueLoader();
          }
        });
  }
}
