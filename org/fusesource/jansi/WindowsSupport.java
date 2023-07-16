package org.fusesource.jansi;

import java.io.UnsupportedEncodingException;
import org.fusesource.jansi.internal.Kernel32;

public class WindowsSupport {
  public static String getLastErrorMessage() {
    int errorCode = Kernel32.GetLastError();
    return getErrorMessage(errorCode);
  }
  
  public static String getErrorMessage(int errorCode) {
    int bufferSize = 160;
    byte[] data = new byte[bufferSize];
    Kernel32.FormatMessageW(Kernel32.FORMAT_MESSAGE_FROM_SYSTEM, 0L, errorCode, 0, data, bufferSize, null);
    try {
      return (new String(data, "UTF-16LE")).trim();
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException(e);
    } 
  }
}
