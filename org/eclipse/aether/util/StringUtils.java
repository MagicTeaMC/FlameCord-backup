package org.eclipse.aether.util;

@Deprecated
public final class StringUtils {
  public static boolean isEmpty(String string) {
    return (string == null || string.length() <= 0);
  }
}
