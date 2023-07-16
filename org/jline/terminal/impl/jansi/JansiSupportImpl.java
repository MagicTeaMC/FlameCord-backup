package org.jline.terminal.impl.jansi;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.fusesource.jansi.AnsiConsole;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.jansi.freebsd.FreeBsdNativePty;
import org.jline.terminal.impl.jansi.linux.LinuxNativePty;
import org.jline.terminal.impl.jansi.osx.OsXNativePty;
import org.jline.terminal.impl.jansi.win.JansiWinSysTerminal;
import org.jline.terminal.spi.JansiSupport;
import org.jline.terminal.spi.Pty;
import org.jline.utils.OSUtils;

public class JansiSupportImpl implements JansiSupport {
  static final int JANSI_MAJOR_VERSION;
  
  static final int JANSI_MINOR_VERSION;
  
  static {
    int major = 0, minor = 0;
    try {
      String v = null;
      try {
        InputStream is = AnsiConsole.class.getResourceAsStream("jansi.properties");
        try {
          if (is != null) {
            Properties props = new Properties();
            props.load(is);
            v = props.getProperty("version");
          } 
          if (is != null)
            is.close(); 
        } catch (Throwable throwable) {
          if (is != null)
            try {
              is.close();
            } catch (Throwable throwable1) {
              throwable.addSuppressed(throwable1);
            }  
          throw throwable;
        } 
      } catch (IOException iOException) {}
      if (v == null)
        v = AnsiConsole.class.getPackage().getImplementationVersion(); 
      if (v != null) {
        Matcher m = Pattern.compile("([0-9]+)\\.([0-9]+)([\\.-]\\S+)?").matcher(v);
        if (m.matches()) {
          major = Integer.parseInt(m.group(1));
          minor = Integer.parseInt(m.group(2));
        } 
      } 
    } catch (Throwable throwable) {}
    JANSI_MAJOR_VERSION = major;
    JANSI_MINOR_VERSION = minor;
  }
  
  public static int getJansiMajorVersion() {
    return JANSI_MAJOR_VERSION;
  }
  
  public static int getJansiMinorVersion() {
    return JANSI_MINOR_VERSION;
  }
  
  public static boolean isAtLeast(int major, int minor) {
    return (JANSI_MAJOR_VERSION > major || (JANSI_MAJOR_VERSION == major && JANSI_MINOR_VERSION >= minor));
  }
  
  public Pty current() throws IOException {
    String osName = System.getProperty("os.name");
    if (osName.startsWith("Linux"))
      return (Pty)LinuxNativePty.current(); 
    if (osName.startsWith("Mac") || osName.startsWith("Darwin"))
      return (Pty)OsXNativePty.current(); 
    if (!osName.startsWith("Solaris") && !osName.startsWith("SunOS"))
      if (osName.startsWith("FreeBSD") && 
        isAtLeast(1, 16))
        return (Pty)FreeBsdNativePty.current();  
    throw new UnsupportedOperationException();
  }
  
  public Pty open(Attributes attributes, Size size) throws IOException {
    if (isAtLeast(1, 16)) {
      String osName = System.getProperty("os.name");
      if (osName.startsWith("Linux"))
        return (Pty)LinuxNativePty.open(attributes, size); 
      if (osName.startsWith("Mac") || osName.startsWith("Darwin"))
        return (Pty)OsXNativePty.open(attributes, size); 
      if (!osName.startsWith("Solaris") && !osName.startsWith("SunOS"))
        if (osName.startsWith("FreeBSD"))
          return (Pty)FreeBsdNativePty.open(attributes, size);  
    } 
    throw new UnsupportedOperationException();
  }
  
  public Terminal winSysTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, int codepage, boolean nativeSignals, Terminal.SignalHandler signalHandler) throws IOException {
    return winSysTerminal(name, type, ansiPassThrough, encoding, codepage, nativeSignals, signalHandler, false);
  }
  
  public Terminal winSysTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, int codepage, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused) throws IOException {
    if (isAtLeast(1, 12)) {
      JansiWinSysTerminal terminal = JansiWinSysTerminal.createTerminal(name, type, ansiPassThrough, encoding, codepage, nativeSignals, signalHandler, paused);
      if (!isAtLeast(1, 16))
        terminal.disableScrolling(); 
      return (Terminal)terminal;
    } 
    throw new UnsupportedOperationException();
  }
  
  public boolean isWindowsConsole() {
    return JansiWinSysTerminal.isWindowsConsole();
  }
  
  public boolean isConsoleOutput() {
    if (OSUtils.IS_CYGWIN || OSUtils.IS_MSYSTEM) {
      if (isAtLeast(2, 1))
        return JansiNativePty.isConsoleOutput(); 
      throw new UnsupportedOperationException();
    } 
    if (OSUtils.IS_WINDOWS)
      return JansiWinSysTerminal.isConsoleOutput(); 
    return JansiNativePty.isConsoleOutput();
  }
  
  public boolean isConsoleInput() {
    if (OSUtils.IS_CYGWIN || OSUtils.IS_MSYSTEM) {
      if (isAtLeast(2, 1))
        return JansiNativePty.isConsoleInput(); 
      throw new UnsupportedOperationException();
    } 
    if (OSUtils.IS_WINDOWS)
      return JansiWinSysTerminal.isConsoleInput(); 
    return JansiNativePty.isConsoleInput();
  }
}
