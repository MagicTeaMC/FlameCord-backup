package org.eclipse.sisu.inject;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.ProvisionException;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Logs {
  public static final String NEW_LINE;
  
  private static final String SISU = "Sisu";
  
  private static final Sink SINK;
  
  static {
    String newLine;
    boolean toConsole;
    Sink sink;
    try {
      newLine = System.getProperty("line.separator", "\n");
      String debug = System.getProperty("sisu.debug", "false");
      toConsole = !(!"".equals(debug) && !"true".equalsIgnoreCase(debug));
    } catch (RuntimeException runtimeException) {
      newLine = "\n";
      toConsole = false;
    } 
    NEW_LINE = newLine;
    try {
      sink = toConsole ? new ConsoleSink() : new SLF4JSink();
    } catch (RuntimeException runtimeException) {
      sink = new JULSink();
    } catch (LinkageError linkageError) {
      sink = new JULSink();
    } 
    SINK = sink;
  }
  
  public static final boolean TRACE_ENABLED = SINK.isTraceEnabled();
  
  public static void trace(String format, Object arg1, Object arg2) {
    if (TRACE_ENABLED)
      SINK.trace(format(format(format, arg1), arg2), (arg2 instanceof Throwable) ? (Throwable)arg2 : null); 
  }
  
  public static void warn(String format, Object arg1, Object arg2) {
    SINK.warn(format(format(format, arg1), arg2), (arg2 instanceof Throwable) ? (Throwable)arg2 : null);
  }
  
  public static void catchThrowable(Throwable problem) {
    for (Throwable cause = problem; cause != null; cause = cause.getCause()) {
      if (cause instanceof ThreadDeath || cause instanceof VirtualMachineError)
        throw (Error)cause; 
    } 
  }
  
  public static void throwUnchecked(Throwable problem) {
    if (problem instanceof RuntimeException)
      throw (RuntimeException)problem; 
    if (problem instanceof Error)
      throw (Error)problem; 
    throw (RuntimeException)RuntimeException.class.cast(new ProvisionException(problem.toString(), problem));
  }
  
  public static String identityToString(Object object) {
    return (object == null) ? null : (String.valueOf(object.getClass().getName()) + '@' + 
      Integer.toHexString(System.identityHashCode(object)));
  }
  
  public static String toString(Module module) {
    StringBuilder buf = new StringBuilder(identityToString(module));
    buf.append(NEW_LINE).append(NEW_LINE);
    buf.append("-----[elements]----------------------------------------------------------------").append(NEW_LINE);
    int i = 0;
    Iterator<Element> iterator = Elements.getElements(new Module[] { module }).iterator();
    while (iterator.hasNext()) {
      Element e = iterator.next();
      buf.append(i++).append(". ").append(e).append(NEW_LINE);
    } 
    return buf.append("-------------------------------------------------------------------------------").append(NEW_LINE).toString();
  }
  
  public static String toString(Injector injector) {
    StringBuilder buf = new StringBuilder(identityToString(injector));
    if (injector.getParent() != null)
      buf.append(" parent: ").append(identityToString(injector.getParent())); 
    buf.append(NEW_LINE).append(NEW_LINE);
    buf.append("-----[explicit bindings]-------------------------------------------------------").append(NEW_LINE);
    int i = 0;
    Map<Key<?>, Binding<?>> explicitBindings = injector.getBindings();
    for (Binding<?> b : explicitBindings.values())
      buf.append(i++).append(". ").append(b).append(NEW_LINE); 
    buf.append("-----[implicit bindings]-------------------------------------------------------").append(NEW_LINE);
    for (Binding<?> b : (Iterable<Binding<?>>)injector.getAllBindings().values()) {
      if (!explicitBindings.containsKey(b.getKey()))
        buf.append(i++).append(". ").append(b).append(NEW_LINE); 
    } 
    return buf.append("-------------------------------------------------------------------------------").append(NEW_LINE).toString();
  }
  
  private static String format(String format, Object arg) {
    int len = format.length();
    boolean detailed = true;
    int cursor = 0;
    for (char prevChar = ' '; cursor < len; prevChar = currChar, cursor++) {
      char currChar = format.charAt(cursor);
      if (prevChar == '{' && currChar == '}')
        break; 
      if (prevChar == '<' && currChar == '>') {
        detailed = false;
        break;
      } 
    } 
    if (cursor >= len)
      return format; 
    StringBuilder buf = new StringBuilder();
    if (--cursor > 0)
      buf.append(format.substring(0, cursor)); 
    try {
      buf.append(detailed ? arg : identityToString(arg));
    } catch (RuntimeException e) {
      buf.append(e);
    } 
    cursor += 2;
    if (cursor < len)
      buf.append(format.substring(cursor, len)); 
    return buf.toString();
  }
  
  static final class ConsoleSink implements Sink {
    private static final String TRACE = "TRACE: Sisu - ";
    
    private static final String WARN = "WARN: Sisu - ";
    
    public boolean isTraceEnabled() {
      return true;
    }
    
    public void trace(String message, Throwable cause) {
      System.out.println("TRACE: Sisu - " + message);
      if (cause != null)
        cause.printStackTrace(System.out); 
    }
    
    public void warn(String message, Throwable cause) {
      System.err.println("WARN: Sisu - " + message);
      if (cause != null)
        cause.printStackTrace(System.err); 
    }
  }
  
  static final class JULSink implements Sink {
    private static final Logger logger = Logger.getLogger("Sisu");
    
    public boolean isTraceEnabled() {
      return logger.isLoggable(Level.FINER);
    }
    
    public void trace(String message, Throwable cause) {
      logger.log(Level.FINER, message, cause);
    }
    
    public void warn(String message, Throwable cause) {
      logger.log(Level.WARNING, message, cause);
    }
  }
  
  static final class SLF4JSink implements Sink {
    private static final Logger logger = LoggerFactory.getLogger("Sisu");
    
    public boolean isTraceEnabled() {
      return logger.isTraceEnabled();
    }
    
    public void trace(String message, Throwable cause) {
      logger.trace(message, cause);
    }
    
    public void warn(String message, Throwable cause) {
      logger.warn(message, cause);
    }
  }
  
  private static interface Sink {
    boolean isTraceEnabled();
    
    void trace(String param1String, Throwable param1Throwable);
    
    void warn(String param1String, Throwable param1Throwable);
  }
}
