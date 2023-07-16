package org.fusesource.jansi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import org.fusesource.jansi.io.AnsiOutputStream;

public class AnsiPrintStream extends PrintStream {
  public AnsiPrintStream(AnsiOutputStream out, boolean autoFlush) {
    super((OutputStream)out, autoFlush);
  }
  
  public AnsiPrintStream(AnsiOutputStream out, boolean autoFlush, String encoding) throws UnsupportedEncodingException {
    super((OutputStream)out, autoFlush, encoding);
  }
  
  protected AnsiOutputStream getOut() {
    return (AnsiOutputStream)this.out;
  }
  
  public AnsiType getType() {
    return getOut().getType();
  }
  
  public AnsiColors getColors() {
    return getOut().getColors();
  }
  
  public AnsiMode getMode() {
    return getOut().getMode();
  }
  
  public void setMode(AnsiMode ansiMode) {
    getOut().setMode(ansiMode);
  }
  
  public boolean isResetAtUninstall() {
    return getOut().isResetAtUninstall();
  }
  
  public void setResetAtUninstall(boolean resetAtClose) {
    getOut().setResetAtUninstall(resetAtClose);
  }
  
  public int getTerminalWidth() {
    return getOut().getTerminalWidth();
  }
  
  public void install() throws IOException {
    getOut().install();
  }
  
  public void uninstall() throws IOException {
    AnsiOutputStream out = getOut();
    if (out != null)
      out.uninstall(); 
  }
  
  public String toString() {
    return "AnsiPrintStream{type=" + 
      getType() + ", colors=" + 
      getColors() + ", mode=" + 
      getMode() + ", resetAtUninstall=" + 
      isResetAtUninstall() + "}";
  }
}
