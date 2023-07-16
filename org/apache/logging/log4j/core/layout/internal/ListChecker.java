package org.apache.logging.log4j.core.layout.internal;

public interface ListChecker {
  public static final NoopChecker NOOP_CHECKER = new NoopChecker();
  
  boolean check(String paramString);
  
  public static class NoopChecker implements ListChecker {
    public boolean check(String key) {
      return true;
    }
    
    public String toString() {
      return "";
    }
  }
}
