package org.apache.logging.log4j.util;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;

final class PrivateSecurityManagerStackTraceUtil {
  private static final PrivateSecurityManager SECURITY_MANAGER;
  
  static {
    PrivateSecurityManager psm;
    try {
      SecurityManager sm = System.getSecurityManager();
      if (sm != null)
        sm.checkPermission(new RuntimePermission("createSecurityManager")); 
      psm = new PrivateSecurityManager();
    } catch (SecurityException ignored) {
      psm = null;
    } 
    SECURITY_MANAGER = psm;
  }
  
  static boolean isEnabled() {
    return (SECURITY_MANAGER != null);
  }
  
  static Deque<Class<?>> getCurrentStackTrace() {
    Class<?>[] array = SECURITY_MANAGER.getClassContext();
    Deque<Class<?>> classes = new ArrayDeque<>(array.length);
    Collections.addAll(classes, array);
    return classes;
  }
  
  private static final class PrivateSecurityManager extends SecurityManager {
    private PrivateSecurityManager() {}
    
    protected Class<?>[] getClassContext() {
      return super.getClassContext();
    }
  }
}
