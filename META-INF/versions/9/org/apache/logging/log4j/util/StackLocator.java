package META-INF.versions.9.org.apache.logging.log4j.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.apache.logging.log4j.util.PrivateSecurityManagerStackTraceUtil;

public class StackLocator {
  private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
  
  private static final StackWalker STACK_WALKER = StackWalker.getInstance();
  
  private static final org.apache.logging.log4j.util.StackLocator INSTANCE = new org.apache.logging.log4j.util.StackLocator();
  
  public static org.apache.logging.log4j.util.StackLocator getInstance() {
    return INSTANCE;
  }
  
  public Class<?> getCallerClass(Class<?> sentinelClass, Predicate<Class<?>> callerPredicate) {
    if (sentinelClass == null)
      throw new IllegalArgumentException("sentinelClass cannot be null"); 
    if (callerPredicate == null)
      throw new IllegalArgumentException("callerPredicate cannot be null"); 
    return WALKER.<Class<?>>walk(s -> (Class)s.map(StackWalker.StackFrame::getDeclaringClass).dropWhile(()).dropWhile(()).findFirst().orElse(null));
  }
  
  public Class<?> getCallerClass(String fqcn) {
    return getCallerClass(fqcn, "");
  }
  
  public Class<?> getCallerClass(String fqcn, String pkg) {
    return ((Optional)WALKER.<Optional>walk(s -> s.dropWhile(()).dropWhile(()).dropWhile(()).findFirst()))
      
      .map(StackWalker.StackFrame::getDeclaringClass)
      .orElse(null);
  }
  
  public Class<?> getCallerClass(Class<?> anchor) {
    return ((Optional)WALKER.<Optional>walk(s -> s.dropWhile(()).dropWhile(()).findFirst()))
      
      .map(StackWalker.StackFrame::getDeclaringClass).orElse(null);
  }
  
  public Class<?> getCallerClass(int depth) {
    return ((Optional)WALKER.<Optional>walk(s -> s.skip(depth).findFirst())).map(StackWalker.StackFrame::getDeclaringClass).orElse(null);
  }
  
  public Deque<Class<?>> getCurrentStackTrace() {
    if (PrivateSecurityManagerStackTraceUtil.isEnabled())
      return PrivateSecurityManagerStackTraceUtil.getCurrentStackTrace(); 
    Deque<Class<?>> stack = new ArrayDeque<>();
    return WALKER.<Deque<Class<?>>>walk(s -> {
          s.forEach(());
          return stack;
        });
  }
  
  public StackTraceElement calcLocation(String fqcnOfLogger) {
    return ((Optional)STACK_WALKER.<Optional>walk(s -> s.dropWhile(()).dropWhile(()).findFirst()))
      
      .map(StackWalker.StackFrame::toStackTraceElement).orElse(null);
  }
  
  public StackTraceElement getStackTraceElement(int depth) {
    return ((Optional)STACK_WALKER.<Optional>walk(s -> s.skip(depth).findFirst()))
      .map(StackWalker.StackFrame::toStackTraceElement).orElse(null);
  }
}
