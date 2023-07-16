package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingPumpReader;
import org.jline.utils.NonBlockingReader;
import org.jline.utils.ShutdownHooks;
import org.jline.utils.Signals;
import org.jline.utils.WriterOutputStream;

public abstract class AbstractWindowsTerminal extends AbstractTerminal {
  public static final String TYPE_WINDOWS = "windows";
  
  public static final String TYPE_WINDOWS_256_COLOR = "windows-256color";
  
  public static final String TYPE_WINDOWS_CONEMU = "windows-conemu";
  
  public static final String TYPE_WINDOWS_VTP = "windows-vtp";
  
  public static final int ENABLE_VIRTUAL_TERMINAL_PROCESSING = 4;
  
  private static final int UTF8_CODE_PAGE = 65001;
  
  protected static final int ENABLE_PROCESSED_INPUT = 1;
  
  protected static final int ENABLE_LINE_INPUT = 2;
  
  protected static final int ENABLE_ECHO_INPUT = 4;
  
  protected static final int ENABLE_WINDOW_INPUT = 8;
  
  protected static final int ENABLE_MOUSE_INPUT = 16;
  
  protected static final int ENABLE_INSERT_MODE = 32;
  
  protected static final int ENABLE_QUICK_EDIT_MODE = 64;
  
  protected final Writer slaveInputPipe;
  
  protected final NonBlockingInputStream input;
  
  protected final OutputStream output;
  
  protected final NonBlockingReader reader;
  
  protected final PrintWriter writer;
  
  protected final Map<Terminal.Signal, Object> nativeHandlers = new HashMap<>();
  
  protected final ShutdownHooks.Task closer;
  
  protected final Attributes attributes = new Attributes();
  
  protected final int originalConsoleMode;
  
  protected final Object lock = new Object();
  
  protected boolean paused = true;
  
  protected Thread pump;
  
  protected Terminal.MouseTracking tracking = Terminal.MouseTracking.Off;
  
  protected boolean focusTracking = false;
  
  private volatile boolean closing;
  
  static final int SHIFT_FLAG = 1;
  
  static final int ALT_FLAG = 2;
  
  static final int CTRL_FLAG = 4;
  
  static final int RIGHT_ALT_PRESSED = 1;
  
  static final int LEFT_ALT_PRESSED = 2;
  
  static final int RIGHT_CTRL_PRESSED = 4;
  
  static final int LEFT_CTRL_PRESSED = 8;
  
  static final int SHIFT_PRESSED = 16;
  
  static final int NUMLOCK_ON = 32;
  
  static final int SCROLLLOCK_ON = 64;
  
  static final int CAPSLOCK_ON = 128;
  
  public AbstractWindowsTerminal(Writer writer, String name, String type, Charset encoding, int codepage, boolean nativeSignals, Terminal.SignalHandler signalHandler) throws IOException {
    super(name, type, selectCharset(encoding, codepage), signalHandler);
    NonBlockingPumpReader reader = NonBlocking.nonBlockingPumpReader();
    this.slaveInputPipe = reader.getWriter();
    this.reader = (NonBlockingReader)reader;
    this.input = NonBlocking.nonBlockingStream((NonBlockingReader)reader, encoding());
    this.writer = new PrintWriter(writer);
    this.output = (OutputStream)new WriterOutputStream(writer, encoding());
    parseInfoCmp();
    this.originalConsoleMode = getConsoleMode();
    this.attributes.setLocalFlag(Attributes.LocalFlag.ISIG, true);
    this.attributes.setControlChar(Attributes.ControlChar.VINTR, ctrl('C'));
    this.attributes.setControlChar(Attributes.ControlChar.VEOF, ctrl('D'));
    this.attributes.setControlChar(Attributes.ControlChar.VSUSP, ctrl('Z'));
    if (nativeSignals)
      for (Terminal.Signal signal : Terminal.Signal.values()) {
        if (signalHandler == Terminal.SignalHandler.SIG_DFL) {
          this.nativeHandlers.put(signal, Signals.registerDefault(signal.name()));
        } else {
          this.nativeHandlers.put(signal, Signals.register(signal.name(), () -> raise(signal)));
        } 
      }  
    this.closer = this::close;
    ShutdownHooks.add(this.closer);
    if ("windows-conemu".equals(getType()) && 
      !Boolean.getBoolean("org.jline.terminal.conemu.disable-activate")) {
      writer.write("\033[9999E");
      writer.flush();
    } 
  }
  
  private static Charset selectCharset(Charset encoding, int codepage) {
    if (encoding != null)
      return encoding; 
    if (codepage >= 0)
      return getCodepageCharset(codepage); 
    return StandardCharsets.UTF_8;
  }
  
  private static Charset getCodepageCharset(int codepage) {
    if (codepage == 65001)
      return StandardCharsets.UTF_8; 
    String charsetMS = "ms" + codepage;
    if (Charset.isSupported(charsetMS))
      return Charset.forName(charsetMS); 
    String charsetCP = "cp" + codepage;
    if (Charset.isSupported(charsetCP))
      return Charset.forName(charsetCP); 
    return Charset.defaultCharset();
  }
  
  public Terminal.SignalHandler handle(Terminal.Signal signal, Terminal.SignalHandler handler) {
    Terminal.SignalHandler prev = super.handle(signal, handler);
    if (prev != handler)
      if (handler == Terminal.SignalHandler.SIG_DFL) {
        Signals.registerDefault(signal.name());
      } else {
        Signals.register(signal.name(), () -> raise(signal));
      }  
    return prev;
  }
  
  public NonBlockingReader reader() {
    return this.reader;
  }
  
  public PrintWriter writer() {
    return this.writer;
  }
  
  public InputStream input() {
    return (InputStream)this.input;
  }
  
  public OutputStream output() {
    return this.output;
  }
  
  public Attributes getAttributes() {
    int mode = getConsoleMode();
    if ((mode & 0x4) != 0)
      this.attributes.setLocalFlag(Attributes.LocalFlag.ECHO, true); 
    if ((mode & 0x2) != 0)
      this.attributes.setLocalFlag(Attributes.LocalFlag.ICANON, true); 
    return new Attributes(this.attributes);
  }
  
  public void setAttributes(Attributes attr) {
    this.attributes.copy(attr);
    updateConsoleMode();
  }
  
  protected void updateConsoleMode() {
    int mode = 8;
    if (this.attributes.getLocalFlag(Attributes.LocalFlag.ECHO))
      mode |= 0x4; 
    if (this.attributes.getLocalFlag(Attributes.LocalFlag.ICANON))
      mode |= 0x2; 
    if (this.tracking != Terminal.MouseTracking.Off)
      mode |= 0x10; 
    setConsoleMode(mode);
  }
  
  protected int ctrl(char key) {
    return Character.toUpperCase(key) & 0x1F;
  }
  
  public void setSize(Size size) {
    throw new UnsupportedOperationException("Can not resize windows terminal");
  }
  
  protected void doClose() throws IOException {
    super.doClose();
    this.closing = true;
    if (this.pump != null)
      this.pump.interrupt(); 
    ShutdownHooks.remove(this.closer);
    for (Map.Entry<Terminal.Signal, Object> entry : this.nativeHandlers.entrySet())
      Signals.unregister(((Terminal.Signal)entry.getKey()).name(), entry.getValue()); 
    this.reader.close();
    this.writer.close();
    setConsoleMode(this.originalConsoleMode);
  }
  
  protected void processKeyEvent(boolean isKeyDown, short virtualKeyCode, char ch, int controlKeyState) throws IOException {
    boolean isCtrl = ((controlKeyState & 0xC) > 0);
    boolean isAlt = ((controlKeyState & 0x3) > 0);
    boolean isShift = ((controlKeyState & 0x10) > 0);
    if (isKeyDown && ch != '\003') {
      if (ch != '\000' && (controlKeyState & 0x1F) == 9) {
        processInputChar(ch);
      } else {
        String keySeq = getEscapeSequence(virtualKeyCode, (isCtrl ? 4 : 0) + (isAlt ? 2 : 0) + (isShift ? 1 : 0));
        if (keySeq != null) {
          for (char c : keySeq.toCharArray())
            processInputChar(c); 
          return;
        } 
        if (ch > '\000') {
          if (isAlt)
            processInputChar('\033'); 
          if (isCtrl && ch != ' ' && ch != '\n' && ch != '') {
            processInputChar((char)((ch == '?') ? '' : (Character.toUpperCase(ch) & 0x1F)));
          } else {
            processInputChar(ch);
          } 
        } else if (isCtrl) {
          if (virtualKeyCode >= 65 && virtualKeyCode <= 90) {
            ch = (char)(virtualKeyCode - 64);
          } else if (virtualKeyCode == 191) {
            ch = '';
          } 
          if (ch > '\000') {
            if (isAlt)
              processInputChar('\033'); 
            processInputChar(ch);
          } 
        } 
      } 
    } else if (isKeyDown && ch == '\003') {
      processInputChar('\003');
    } else if (virtualKeyCode == 18 && ch > '\000') {
      processInputChar(ch);
    } 
  }
  
  protected String getEscapeSequence(short keyCode, int keyState) {
    String escapeSequence = null;
    switch (keyCode) {
      case 8:
        escapeSequence = ((keyState & 0x2) > 0) ? "\\E^H" : getRawSequence(InfoCmp.Capability.key_backspace);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 9:
        escapeSequence = ((keyState & 0x1) > 0) ? getRawSequence(InfoCmp.Capability.key_btab) : null;
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 33:
        escapeSequence = getRawSequence(InfoCmp.Capability.key_ppage);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 34:
        escapeSequence = getRawSequence(InfoCmp.Capability.key_npage);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 35:
        escapeSequence = (keyState > 0) ? "\\E[1;%p1%dF" : getRawSequence(InfoCmp.Capability.key_end);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 36:
        escapeSequence = (keyState > 0) ? "\\E[1;%p1%dH" : getRawSequence(InfoCmp.Capability.key_home);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 37:
        escapeSequence = (keyState > 0) ? "\\E[1;%p1%dD" : getRawSequence(InfoCmp.Capability.key_left);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 38:
        escapeSequence = (keyState > 0) ? "\\E[1;%p1%dA" : getRawSequence(InfoCmp.Capability.key_up);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 39:
        escapeSequence = (keyState > 0) ? "\\E[1;%p1%dC" : getRawSequence(InfoCmp.Capability.key_right);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 40:
        escapeSequence = (keyState > 0) ? "\\E[1;%p1%dB" : getRawSequence(InfoCmp.Capability.key_down);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 45:
        escapeSequence = getRawSequence(InfoCmp.Capability.key_ic);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 46:
        escapeSequence = getRawSequence(InfoCmp.Capability.key_dc);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 112:
        escapeSequence = (keyState > 0) ? "\\E[1;%p1%dP" : getRawSequence(InfoCmp.Capability.key_f1);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 113:
        escapeSequence = (keyState > 0) ? "\\E[1;%p1%dQ" : getRawSequence(InfoCmp.Capability.key_f2);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 114:
        escapeSequence = (keyState > 0) ? "\\E[1;%p1%dR" : getRawSequence(InfoCmp.Capability.key_f3);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 115:
        escapeSequence = (keyState > 0) ? "\\E[1;%p1%dS" : getRawSequence(InfoCmp.Capability.key_f4);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 116:
        escapeSequence = (keyState > 0) ? "\\E[15;%p1%d~" : getRawSequence(InfoCmp.Capability.key_f5);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 117:
        escapeSequence = (keyState > 0) ? "\\E[17;%p1%d~" : getRawSequence(InfoCmp.Capability.key_f6);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 118:
        escapeSequence = (keyState > 0) ? "\\E[18;%p1%d~" : getRawSequence(InfoCmp.Capability.key_f7);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 119:
        escapeSequence = (keyState > 0) ? "\\E[19;%p1%d~" : getRawSequence(InfoCmp.Capability.key_f8);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 120:
        escapeSequence = (keyState > 0) ? "\\E[20;%p1%d~" : getRawSequence(InfoCmp.Capability.key_f9);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 121:
        escapeSequence = (keyState > 0) ? "\\E[21;%p1%d~" : getRawSequence(InfoCmp.Capability.key_f10);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 122:
        escapeSequence = (keyState > 0) ? "\\E[23;%p1%d~" : getRawSequence(InfoCmp.Capability.key_f11);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
      case 123:
        escapeSequence = (keyState > 0) ? "\\E[24;%p1%d~" : getRawSequence(InfoCmp.Capability.key_f12);
        return Curses.tputs(escapeSequence, new Object[] { Integer.valueOf(keyState + 1) });
    } 
    return null;
  }
  
  protected String getRawSequence(InfoCmp.Capability cap) {
    return this.strings.get(cap);
  }
  
  public boolean hasFocusSupport() {
    return true;
  }
  
  public boolean trackFocus(boolean tracking) {
    this.focusTracking = tracking;
    return true;
  }
  
  public boolean canPauseResume() {
    return true;
  }
  
  public void pause() {
    synchronized (this.lock) {
      this.paused = true;
    } 
  }
  
  public void pause(boolean wait) throws InterruptedException {
    Thread p;
    synchronized (this.lock) {
      this.paused = true;
      p = this.pump;
    } 
    if (p != null) {
      p.interrupt();
      p.join();
    } 
  }
  
  public void resume() {
    synchronized (this.lock) {
      this.paused = false;
      if (this.pump == null) {
        this.pump = new Thread(this::pump, "WindowsStreamPump");
        this.pump.setDaemon(true);
        this.pump.start();
      } 
    } 
  }
  
  public boolean paused() {
    synchronized (this.lock) {
      return this.paused;
    } 
  }
  
  protected void pump() {
    try {
      while (!this.closing) {
        synchronized (this.lock) {
          if (this.paused) {
            this.pump = null;
            break;
          } 
        } 
        if (processConsoleInput())
          this.slaveInputPipe.flush(); 
      } 
    } catch (IOException e) {
      if (!this.closing) {
        Log.warn(new Object[] { "Error in WindowsStreamPump", e });
        try {
          close();
        } catch (IOException e1) {
          Log.warn(new Object[] { "Error closing terminal", e });
        } 
      } 
    } finally {
      synchronized (this.lock) {
        this.pump = null;
      } 
    } 
  }
  
  public void processInputChar(char c) throws IOException {
    if (this.attributes.getLocalFlag(Attributes.LocalFlag.ISIG)) {
      if (c == this.attributes.getControlChar(Attributes.ControlChar.VINTR)) {
        raise(Terminal.Signal.INT);
        return;
      } 
      if (c == this.attributes.getControlChar(Attributes.ControlChar.VQUIT)) {
        raise(Terminal.Signal.QUIT);
        return;
      } 
      if (c == this.attributes.getControlChar(Attributes.ControlChar.VSUSP)) {
        raise(Terminal.Signal.TSTP);
        return;
      } 
      if (c == this.attributes.getControlChar(Attributes.ControlChar.VSTATUS))
        raise(Terminal.Signal.INFO); 
    } 
    if (c == '\r') {
      if (this.attributes.getInputFlag(Attributes.InputFlag.IGNCR))
        return; 
      if (this.attributes.getInputFlag(Attributes.InputFlag.ICRNL))
        c = '\n'; 
    } else if (c == '\n' && this.attributes.getInputFlag(Attributes.InputFlag.INLCR)) {
      c = '\r';
    } 
    this.slaveInputPipe.write(c);
  }
  
  public boolean trackMouse(Terminal.MouseTracking tracking) {
    this.tracking = tracking;
    updateConsoleMode();
    return true;
  }
  
  protected abstract int getConsoleMode();
  
  protected abstract void setConsoleMode(int paramInt);
  
  protected abstract boolean processConsoleInput() throws IOException;
}
