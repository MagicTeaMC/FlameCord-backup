package org.jline.terminal.impl;

import java.io.IOError;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.function.IntConsumer;
import org.jline.terminal.Attributes;
import org.jline.terminal.Cursor;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.terminal.spi.Pty;

public abstract class AbstractPosixTerminal extends AbstractTerminal {
  protected final Pty pty;
  
  protected final Attributes originalAttributes;
  
  public AbstractPosixTerminal(String name, String type, Pty pty) throws IOException {
    this(name, type, pty, null, Terminal.SignalHandler.SIG_DFL);
  }
  
  public AbstractPosixTerminal(String name, String type, Pty pty, Charset encoding, Terminal.SignalHandler signalHandler) throws IOException {
    super(name, type, encoding, signalHandler);
    Objects.requireNonNull(pty);
    this.pty = pty;
    this.originalAttributes = this.pty.getAttr();
  }
  
  public Pty getPty() {
    return this.pty;
  }
  
  public Attributes getAttributes() {
    try {
      return this.pty.getAttr();
    } catch (IOException e) {
      throw new IOError(e);
    } 
  }
  
  public void setAttributes(Attributes attr) {
    try {
      this.pty.setAttr(attr);
    } catch (IOException e) {
      throw new IOError(e);
    } 
  }
  
  public Size getSize() {
    try {
      return this.pty.getSize();
    } catch (IOException e) {
      throw new IOError(e);
    } 
  }
  
  public void setSize(Size size) {
    try {
      this.pty.setSize(size);
    } catch (IOException e) {
      throw new IOError(e);
    } 
  }
  
  protected void doClose() throws IOException {
    super.doClose();
    this.pty.setAttr(this.originalAttributes);
    this.pty.close();
  }
  
  public Cursor getCursorPosition(IntConsumer discarded) {
    return CursorSupport.getCursorPosition(this, discarded);
  }
}
