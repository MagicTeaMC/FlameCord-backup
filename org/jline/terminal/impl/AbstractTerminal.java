package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.charset.Charset;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.MouseEvent;
import org.jline.terminal.Terminal;
import org.jline.utils.ColorPalette;
import org.jline.utils.Curses;
import org.jline.utils.InfoCmp;
import org.jline.utils.Log;
import org.jline.utils.Status;

public abstract class AbstractTerminal implements Terminal {
  protected final String name;
  
  protected final String type;
  
  protected final Charset encoding;
  
  protected final Map<Terminal.Signal, Terminal.SignalHandler> handlers = new ConcurrentHashMap<>();
  
  protected final Set<InfoCmp.Capability> bools = new HashSet<>();
  
  protected final Map<InfoCmp.Capability, Integer> ints = new HashMap<>();
  
  protected final Map<InfoCmp.Capability, String> strings = new HashMap<>();
  
  protected final ColorPalette palette = new ColorPalette(this);
  
  protected Status status;
  
  protected Runnable onClose;
  
  private MouseEvent lastMouseEvent;
  
  public AbstractTerminal(String name, String type) throws IOException {
    this(name, type, (Charset)null, Terminal.SignalHandler.SIG_DFL);
  }
  
  public void setOnClose(Runnable onClose) {
    this.onClose = onClose;
  }
  
  public Status getStatus() {
    return getStatus(true);
  }
  
  public Status getStatus(boolean create) {
    if (this.status == null && create)
      this.status = new Status(this); 
    return this.status;
  }
  
  public Terminal.SignalHandler handle(Terminal.Signal signal, Terminal.SignalHandler handler) {
    Objects.requireNonNull(signal);
    Objects.requireNonNull(handler);
    return this.handlers.put(signal, handler);
  }
  
  public void raise(Terminal.Signal signal) {
    Objects.requireNonNull(signal);
    Terminal.SignalHandler handler = this.handlers.get(signal);
    if (handler != Terminal.SignalHandler.SIG_DFL && handler != Terminal.SignalHandler.SIG_IGN)
      handler.handle(signal); 
    if (this.status != null && signal == Terminal.Signal.WINCH)
      this.status.resize(); 
  }
  
  public final void close() throws IOException {
    try {
      doClose();
    } finally {
      if (this.onClose != null)
        this.onClose.run(); 
    } 
  }
  
  protected void doClose() throws IOException {
    if (this.status != null) {
      this.status.update(null);
      flush();
    } 
  }
  
  protected void echoSignal(Terminal.Signal signal) {
    Attributes.ControlChar cc = null;
    switch (signal) {
      case INT:
        cc = Attributes.ControlChar.VINTR;
        break;
      case QUIT:
        cc = Attributes.ControlChar.VQUIT;
        break;
      case TSTP:
        cc = Attributes.ControlChar.VSUSP;
        break;
    } 
    if (cc != null) {
      int vcc = getAttributes().getControlChar(cc);
      if (vcc > 0 && vcc < 32)
        writer().write(new char[] { '^', (char)(vcc + 64) }, 0, 2); 
    } 
  }
  
  public Attributes enterRawMode() {
    Attributes prvAttr = getAttributes();
    Attributes newAttr = new Attributes(prvAttr);
    newAttr.setLocalFlags(EnumSet.of(Attributes.LocalFlag.ICANON, Attributes.LocalFlag.ECHO, Attributes.LocalFlag.IEXTEN), false);
    newAttr.setInputFlags(EnumSet.of(Attributes.InputFlag.IXON, Attributes.InputFlag.ICRNL, Attributes.InputFlag.INLCR), false);
    newAttr.setControlChar(Attributes.ControlChar.VMIN, 0);
    newAttr.setControlChar(Attributes.ControlChar.VTIME, 1);
    setAttributes(newAttr);
    return prvAttr;
  }
  
  public boolean echo() {
    return getAttributes().getLocalFlag(Attributes.LocalFlag.ECHO);
  }
  
  public boolean echo(boolean echo) {
    Attributes attr = getAttributes();
    boolean prev = attr.getLocalFlag(Attributes.LocalFlag.ECHO);
    if (prev != echo) {
      attr.setLocalFlag(Attributes.LocalFlag.ECHO, echo);
      setAttributes(attr);
    } 
    return prev;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getType() {
    return this.type;
  }
  
  public String getKind() {
    return getClass().getSimpleName();
  }
  
  public Charset encoding() {
    return this.encoding;
  }
  
  public void flush() {
    writer().flush();
  }
  
  public boolean puts(InfoCmp.Capability capability, Object... params) {
    String str = getStringCapability(capability);
    if (str == null)
      return false; 
    Curses.tputs(writer(), str, params);
    return true;
  }
  
  public boolean getBooleanCapability(InfoCmp.Capability capability) {
    return this.bools.contains(capability);
  }
  
  public Integer getNumericCapability(InfoCmp.Capability capability) {
    return this.ints.get(capability);
  }
  
  public String getStringCapability(InfoCmp.Capability capability) {
    return this.strings.get(capability);
  }
  
  protected void parseInfoCmp() {
    String capabilities = null;
    try {
      capabilities = InfoCmp.getInfoCmp(this.type);
    } catch (Exception e) {
      Log.warn(new Object[] { "Unable to retrieve infocmp for type " + this.type, e });
    } 
    if (capabilities == null)
      capabilities = InfoCmp.getLoadedInfoCmp("ansi"); 
    InfoCmp.parseInfoCmp(capabilities, this.bools, this.ints, this.strings);
  }
  
  public Cursor getCursorPosition(IntConsumer discarded) {
    return null;
  }
  
  public AbstractTerminal(String name, String type, Charset encoding, Terminal.SignalHandler signalHandler) throws IOException {
    this
      
      .lastMouseEvent = new MouseEvent(MouseEvent.Type.Moved, MouseEvent.Button.NoButton, EnumSet.noneOf(MouseEvent.Modifier.class), 0, 0);
    this.name = name;
    this.type = (type != null) ? type : "ansi";
    this.encoding = (encoding != null) ? encoding : Charset.defaultCharset();
    for (Terminal.Signal signal : Terminal.Signal.values())
      this.handlers.put(signal, signalHandler); 
  }
  
  public boolean hasMouseSupport() {
    return MouseSupport.hasMouseSupport(this);
  }
  
  public boolean trackMouse(Terminal.MouseTracking tracking) {
    return MouseSupport.trackMouse(this, tracking);
  }
  
  public MouseEvent readMouseEvent() {
    return this.lastMouseEvent = MouseSupport.readMouse(this, this.lastMouseEvent);
  }
  
  public MouseEvent readMouseEvent(IntSupplier reader) {
    return this.lastMouseEvent = MouseSupport.readMouse(reader, this.lastMouseEvent);
  }
  
  public boolean hasFocusSupport() {
    return this.type.startsWith("xterm");
  }
  
  public boolean trackFocus(boolean tracking) {
    if (hasFocusSupport()) {
      writer().write(tracking ? "\033[?1004h" : "\033[?1004l");
      writer().flush();
      return true;
    } 
    return false;
  }
  
  protected void checkInterrupted() throws InterruptedIOException {
    if (Thread.interrupted())
      throw new InterruptedIOException(); 
  }
  
  public boolean canPauseResume() {
    return false;
  }
  
  public void pause() {}
  
  public void pause(boolean wait) throws InterruptedException {}
  
  public void resume() {}
  
  public boolean paused() {
    return false;
  }
  
  public ColorPalette getPalette() {
    return this.palette;
  }
}
