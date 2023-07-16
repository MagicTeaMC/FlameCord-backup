package org.jline.terminal.impl.jansi.win;

import java.io.BufferedWriter;
import java.io.IOError;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.function.IntConsumer;
import org.fusesource.jansi.internal.Kernel32;
import org.jline.terminal.Cursor;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.impl.AbstractWindowsTerminal;
import org.jline.utils.InfoCmp;
import org.jline.utils.OSUtils;

public class JansiWinSysTerminal extends AbstractWindowsTerminal {
  private static final long consoleOut = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
  
  private static final long consoleIn = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
  
  private char[] focus;
  
  private char[] mouse;
  
  public static JansiWinSysTerminal createTerminal(String name, String type, boolean ansiPassThrough, Charset encoding, int codepage, boolean nativeSignals, Terminal.SignalHandler signalHandler, boolean paused) throws IOException {
    WindowsAnsiWriter windowsAnsiWriter;
    int[] mode = new int[1];
    if (ansiPassThrough) {
      if (type == null)
        type = OSUtils.IS_CONEMU ? "windows-conemu" : "windows"; 
      JansiWinConsoleWriter jansiWinConsoleWriter = new JansiWinConsoleWriter();
    } else {
      if (Kernel32.GetConsoleMode(consoleOut, mode) == 0)
        throw new IOException("Failed to get console mode: " + getLastErrorMessage()); 
      if (Kernel32.SetConsoleMode(consoleOut, mode[0] | 0x4) != 0) {
        if (type == null)
          type = "windows-vtp"; 
        JansiWinConsoleWriter jansiWinConsoleWriter = new JansiWinConsoleWriter();
      } else if (OSUtils.IS_CONEMU) {
        if (type == null)
          type = "windows-conemu"; 
        JansiWinConsoleWriter jansiWinConsoleWriter = new JansiWinConsoleWriter();
      } else {
        if (type == null)
          type = "windows"; 
        windowsAnsiWriter = new WindowsAnsiWriter(new BufferedWriter((Writer)new JansiWinConsoleWriter()));
      } 
    } 
    if (Kernel32.GetConsoleMode(consoleIn, mode) == 0)
      throw new IOException("Failed to get console mode: " + getLastErrorMessage()); 
    JansiWinSysTerminal terminal = new JansiWinSysTerminal((Writer)windowsAnsiWriter, name, type, encoding, codepage, nativeSignals, signalHandler);
    if (!paused)
      terminal.resume(); 
    return terminal;
  }
  
  public static boolean isWindowsConsole() {
    int[] mode = new int[1];
    return (Kernel32.GetConsoleMode(consoleOut, mode) != 0 && Kernel32.GetConsoleMode(consoleIn, mode) != 0);
  }
  
  public static boolean isConsoleOutput() {
    int[] mode = new int[1];
    return (Kernel32.GetConsoleMode(consoleOut, mode) != 0);
  }
  
  public static boolean isConsoleInput() {
    int[] mode = new int[1];
    return (Kernel32.GetConsoleMode(consoleIn, mode) != 0);
  }
  
  JansiWinSysTerminal(Writer writer, String name, String type, Charset encoding, int codepage, boolean nativeSignals, Terminal.SignalHandler signalHandler) throws IOException {
    super(writer, name, type, encoding, codepage, nativeSignals, signalHandler);
    this.focus = new char[] { '\033', '[', ' ' };
    this.mouse = new char[] { '\033', '[', 'M', ' ', ' ', ' ' };
  }
  
  protected int getConsoleMode() {
    long console = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
    int[] mode = new int[1];
    if (Kernel32.GetConsoleMode(console, mode) == 0)
      return -1; 
    return mode[0];
  }
  
  protected void setConsoleMode(int mode) {
    long console = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
    Kernel32.SetConsoleMode(console, mode);
  }
  
  public Size getSize() {
    long outputHandle = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
    Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
    Kernel32.GetConsoleScreenBufferInfo(outputHandle, info);
    return new Size(info.windowWidth(), info.windowHeight());
  }
  
  public Size getBufferSize() {
    long outputHandle = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
    Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
    Kernel32.GetConsoleScreenBufferInfo(outputHandle, info);
    return new Size(info.size.x, info.size.y);
  }
  
  protected boolean processConsoleInput() throws IOException {
    Kernel32.INPUT_RECORD[] events;
    long console = Kernel32.GetStdHandle(Kernel32.STD_INPUT_HANDLE);
    if (console != Kernel32.INVALID_HANDLE_VALUE && Kernel32.WaitForSingleObject(console, 100) == 0) {
      events = Kernel32.readConsoleInputHelper(console, 1, false);
    } else {
      return false;
    } 
    boolean flush = false;
    for (Kernel32.INPUT_RECORD event : events) {
      if (event.eventType == Kernel32.INPUT_RECORD.KEY_EVENT) {
        Kernel32.KEY_EVENT_RECORD keyEvent = event.keyEvent;
        processKeyEvent(keyEvent.keyDown, keyEvent.keyCode, keyEvent.uchar, keyEvent.controlKeyState);
        flush = true;
      } else if (event.eventType == Kernel32.INPUT_RECORD.WINDOW_BUFFER_SIZE_EVENT) {
        raise(Terminal.Signal.WINCH);
      } else if (event.eventType == Kernel32.INPUT_RECORD.MOUSE_EVENT) {
        processMouseEvent(event.mouseEvent);
        flush = true;
      } else if (event.eventType == Kernel32.INPUT_RECORD.FOCUS_EVENT) {
        processFocusEvent(event.focusEvent.setFocus);
      } 
    } 
    return flush;
  }
  
  private void processFocusEvent(boolean hasFocus) throws IOException {
    if (this.focusTracking) {
      this.focus[2] = hasFocus ? 'I' : 'O';
      this.slaveInputPipe.write(this.focus);
    } 
  }
  
  private void processMouseEvent(Kernel32.MOUSE_EVENT_RECORD mouseEvent) throws IOException {
    int dwEventFlags = mouseEvent.eventFlags;
    int dwButtonState = mouseEvent.buttonState;
    if (this.tracking == Terminal.MouseTracking.Off || (this.tracking == Terminal.MouseTracking.Normal && dwEventFlags == Kernel32.MOUSE_EVENT_RECORD.MOUSE_MOVED) || (this.tracking == Terminal.MouseTracking.Button && dwEventFlags == Kernel32.MOUSE_EVENT_RECORD.MOUSE_MOVED && dwButtonState == 0))
      return; 
    int cb = 0;
    dwEventFlags &= Kernel32.MOUSE_EVENT_RECORD.DOUBLE_CLICK ^ 0xFFFFFFFF;
    if (dwEventFlags == Kernel32.MOUSE_EVENT_RECORD.MOUSE_WHEELED) {
      cb |= 0x40;
      if (dwButtonState >> 16 < 0)
        cb |= 0x1; 
    } else {
      if (dwEventFlags == Kernel32.MOUSE_EVENT_RECORD.MOUSE_HWHEELED)
        return; 
      if ((dwButtonState & Kernel32.MOUSE_EVENT_RECORD.FROM_LEFT_1ST_BUTTON_PRESSED) != 0) {
        cb |= 0x0;
      } else if ((dwButtonState & Kernel32.MOUSE_EVENT_RECORD.RIGHTMOST_BUTTON_PRESSED) != 0) {
        cb |= 0x1;
      } else if ((dwButtonState & Kernel32.MOUSE_EVENT_RECORD.FROM_LEFT_2ND_BUTTON_PRESSED) != 0) {
        cb |= 0x2;
      } else {
        cb |= 0x3;
      } 
    } 
    int cx = mouseEvent.mousePosition.x;
    int cy = mouseEvent.mousePosition.y;
    this.mouse[3] = (char)(32 + cb);
    this.mouse[4] = (char)(32 + cx + 1);
    this.mouse[5] = (char)(32 + cy + 1);
    this.slaveInputPipe.write(this.mouse);
  }
  
  public Cursor getCursorPosition(IntConsumer discarded) {
    Kernel32.CONSOLE_SCREEN_BUFFER_INFO info = new Kernel32.CONSOLE_SCREEN_BUFFER_INFO();
    long console = Kernel32.GetStdHandle(Kernel32.STD_OUTPUT_HANDLE);
    if (Kernel32.GetConsoleScreenBufferInfo(console, info) == 0)
      throw new IOError(new IOException("Could not get the cursor position: " + getLastErrorMessage())); 
    return new Cursor(info.cursorPosition.x, info.cursorPosition.y);
  }
  
  public void disableScrolling() {
    this.strings.remove(InfoCmp.Capability.insert_line);
    this.strings.remove(InfoCmp.Capability.parm_insert_line);
    this.strings.remove(InfoCmp.Capability.delete_line);
    this.strings.remove(InfoCmp.Capability.parm_delete_line);
  }
  
  static String getLastErrorMessage() {
    int errorCode = Kernel32.GetLastError();
    return getErrorMessage(errorCode);
  }
  
  static String getErrorMessage(int errorCode) {
    int bufferSize = 160;
    byte[] data = new byte[bufferSize];
    Kernel32.FormatMessageW(Kernel32.FORMAT_MESSAGE_FROM_SYSTEM, 0L, errorCode, 0, data, bufferSize, null);
    return (new String(data, StandardCharsets.UTF_16LE)).trim();
  }
}
