package org.jline.terminal;

import java.io.Closeable;
import java.io.Flushable;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;
import org.jline.terminal.impl.NativeSignalHandler;
import org.jline.utils.ColorPalette;
import org.jline.utils.InfoCmp;
import org.jline.utils.NonBlockingReader;

public interface Terminal extends Closeable, Flushable {
  public static final String TYPE_DUMB = "dumb";
  
  public static final String TYPE_DUMB_COLOR = "dumb-color";
  
  String getName();
  
  SignalHandler handle(Signal paramSignal, SignalHandler paramSignalHandler);
  
  void raise(Signal paramSignal);
  
  NonBlockingReader reader();
  
  PrintWriter writer();
  
  Charset encoding();
  
  InputStream input();
  
  OutputStream output();
  
  boolean canPauseResume();
  
  void pause();
  
  void pause(boolean paramBoolean) throws InterruptedException;
  
  void resume();
  
  boolean paused();
  
  Attributes enterRawMode();
  
  boolean echo();
  
  boolean echo(boolean paramBoolean);
  
  Attributes getAttributes();
  
  void setAttributes(Attributes paramAttributes);
  
  Size getSize();
  
  void setSize(Size paramSize);
  
  public enum Signal {
    INT, QUIT, TSTP, CONT, INFO, WINCH;
  }
  
  public static interface SignalHandler {
    public static final SignalHandler SIG_DFL = (SignalHandler)NativeSignalHandler.SIG_DFL;
    
    public static final SignalHandler SIG_IGN = (SignalHandler)NativeSignalHandler.SIG_IGN;
    
    void handle(Terminal.Signal param1Signal);
  }
  
  default int getWidth() {
    return getSize().getColumns();
  }
  
  default int getHeight() {
    return getSize().getRows();
  }
  
  default Size getBufferSize() {
    return getSize();
  }
  
  void flush();
  
  String getType();
  
  boolean puts(InfoCmp.Capability paramCapability, Object... paramVarArgs);
  
  boolean getBooleanCapability(InfoCmp.Capability paramCapability);
  
  Integer getNumericCapability(InfoCmp.Capability paramCapability);
  
  String getStringCapability(InfoCmp.Capability paramCapability);
  
  Cursor getCursorPosition(IntConsumer paramIntConsumer);
  
  boolean hasMouseSupport();
  
  boolean trackMouse(MouseTracking paramMouseTracking);
  
  MouseEvent readMouseEvent();
  
  MouseEvent readMouseEvent(IntSupplier paramIntSupplier);
  
  boolean hasFocusSupport();
  
  boolean trackFocus(boolean paramBoolean);
  
  ColorPalette getPalette();
  
  public enum MouseTracking {
    Off, Normal, Button, Any;
  }
}
