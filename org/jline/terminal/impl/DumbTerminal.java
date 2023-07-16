package org.jline.terminal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.Terminal;
import org.jline.utils.NonBlocking;
import org.jline.utils.NonBlockingInputStream;
import org.jline.utils.NonBlockingReader;

public class DumbTerminal extends AbstractTerminal {
  private final NonBlockingInputStream input;
  
  private final OutputStream output;
  
  private final NonBlockingReader reader;
  
  private final PrintWriter writer;
  
  private final Attributes attributes;
  
  private final Size size;
  
  public DumbTerminal(InputStream in, OutputStream out) throws IOException {
    this("dumb", "dumb", in, out, (Charset)null);
  }
  
  public DumbTerminal(String name, String type, InputStream in, OutputStream out, Charset encoding) throws IOException {
    this(name, type, in, out, encoding, Terminal.SignalHandler.SIG_DFL);
  }
  
  public DumbTerminal(String name, String type, InputStream in, OutputStream out, Charset encoding, Terminal.SignalHandler signalHandler) throws IOException {
    super(name, type, encoding, signalHandler);
    final NonBlockingInputStream nbis = NonBlocking.nonBlocking(getName(), in);
    this.input = new NonBlockingInputStream() {
        public int read(long timeout, boolean isPeek) throws IOException {
          // Byte code:
          //   0: aload_0
          //   1: getfield val$nbis : Lorg/jline/utils/NonBlockingInputStream;
          //   4: lload_1
          //   5: iload_3
          //   6: invokevirtual read : (JZ)I
          //   9: istore #4
          //   11: aload_0
          //   12: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   15: invokestatic access$000 : (Lorg/jline/terminal/impl/DumbTerminal;)Lorg/jline/terminal/Attributes;
          //   18: getstatic org/jline/terminal/Attributes$LocalFlag.ISIG : Lorg/jline/terminal/Attributes$LocalFlag;
          //   21: invokevirtual getLocalFlag : (Lorg/jline/terminal/Attributes$LocalFlag;)Z
          //   24: ifeq -> 151
          //   27: iload #4
          //   29: aload_0
          //   30: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   33: invokestatic access$000 : (Lorg/jline/terminal/impl/DumbTerminal;)Lorg/jline/terminal/Attributes;
          //   36: getstatic org/jline/terminal/Attributes$ControlChar.VINTR : Lorg/jline/terminal/Attributes$ControlChar;
          //   39: invokevirtual getControlChar : (Lorg/jline/terminal/Attributes$ControlChar;)I
          //   42: if_icmpne -> 58
          //   45: aload_0
          //   46: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   49: getstatic org/jline/terminal/Terminal$Signal.INT : Lorg/jline/terminal/Terminal$Signal;
          //   52: invokevirtual raise : (Lorg/jline/terminal/Terminal$Signal;)V
          //   55: goto -> 0
          //   58: iload #4
          //   60: aload_0
          //   61: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   64: invokestatic access$000 : (Lorg/jline/terminal/impl/DumbTerminal;)Lorg/jline/terminal/Attributes;
          //   67: getstatic org/jline/terminal/Attributes$ControlChar.VQUIT : Lorg/jline/terminal/Attributes$ControlChar;
          //   70: invokevirtual getControlChar : (Lorg/jline/terminal/Attributes$ControlChar;)I
          //   73: if_icmpne -> 89
          //   76: aload_0
          //   77: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   80: getstatic org/jline/terminal/Terminal$Signal.QUIT : Lorg/jline/terminal/Terminal$Signal;
          //   83: invokevirtual raise : (Lorg/jline/terminal/Terminal$Signal;)V
          //   86: goto -> 0
          //   89: iload #4
          //   91: aload_0
          //   92: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   95: invokestatic access$000 : (Lorg/jline/terminal/impl/DumbTerminal;)Lorg/jline/terminal/Attributes;
          //   98: getstatic org/jline/terminal/Attributes$ControlChar.VSUSP : Lorg/jline/terminal/Attributes$ControlChar;
          //   101: invokevirtual getControlChar : (Lorg/jline/terminal/Attributes$ControlChar;)I
          //   104: if_icmpne -> 120
          //   107: aload_0
          //   108: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   111: getstatic org/jline/terminal/Terminal$Signal.TSTP : Lorg/jline/terminal/Terminal$Signal;
          //   114: invokevirtual raise : (Lorg/jline/terminal/Terminal$Signal;)V
          //   117: goto -> 0
          //   120: iload #4
          //   122: aload_0
          //   123: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   126: invokestatic access$000 : (Lorg/jline/terminal/impl/DumbTerminal;)Lorg/jline/terminal/Attributes;
          //   129: getstatic org/jline/terminal/Attributes$ControlChar.VSTATUS : Lorg/jline/terminal/Attributes$ControlChar;
          //   132: invokevirtual getControlChar : (Lorg/jline/terminal/Attributes$ControlChar;)I
          //   135: if_icmpne -> 151
          //   138: aload_0
          //   139: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   142: getstatic org/jline/terminal/Terminal$Signal.INFO : Lorg/jline/terminal/Terminal$Signal;
          //   145: invokevirtual raise : (Lorg/jline/terminal/Terminal$Signal;)V
          //   148: goto -> 0
          //   151: iload #4
          //   153: bipush #13
          //   155: if_icmpne -> 200
          //   158: aload_0
          //   159: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   162: invokestatic access$000 : (Lorg/jline/terminal/impl/DumbTerminal;)Lorg/jline/terminal/Attributes;
          //   165: getstatic org/jline/terminal/Attributes$InputFlag.IGNCR : Lorg/jline/terminal/Attributes$InputFlag;
          //   168: invokevirtual getInputFlag : (Lorg/jline/terminal/Attributes$InputFlag;)Z
          //   171: ifeq -> 177
          //   174: goto -> 0
          //   177: aload_0
          //   178: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   181: invokestatic access$000 : (Lorg/jline/terminal/impl/DumbTerminal;)Lorg/jline/terminal/Attributes;
          //   184: getstatic org/jline/terminal/Attributes$InputFlag.ICRNL : Lorg/jline/terminal/Attributes$InputFlag;
          //   187: invokevirtual getInputFlag : (Lorg/jline/terminal/Attributes$InputFlag;)Z
          //   190: ifeq -> 227
          //   193: bipush #10
          //   195: istore #4
          //   197: goto -> 227
          //   200: iload #4
          //   202: bipush #10
          //   204: if_icmpne -> 227
          //   207: aload_0
          //   208: getfield this$0 : Lorg/jline/terminal/impl/DumbTerminal;
          //   211: invokestatic access$000 : (Lorg/jline/terminal/impl/DumbTerminal;)Lorg/jline/terminal/Attributes;
          //   214: getstatic org/jline/terminal/Attributes$InputFlag.INLCR : Lorg/jline/terminal/Attributes$InputFlag;
          //   217: invokevirtual getInputFlag : (Lorg/jline/terminal/Attributes$InputFlag;)Z
          //   220: ifeq -> 227
          //   223: bipush #13
          //   225: istore #4
          //   227: iload #4
          //   229: ireturn
          // Line number table:
          //   Java source line number -> byte code offset
          //   #49	-> 0
          //   #50	-> 11
          //   #51	-> 27
          //   #52	-> 45
          //   #53	-> 55
          //   #54	-> 58
          //   #55	-> 76
          //   #56	-> 86
          //   #57	-> 89
          //   #58	-> 107
          //   #59	-> 117
          //   #60	-> 120
          //   #61	-> 138
          //   #62	-> 148
          //   #65	-> 151
          //   #66	-> 158
          //   #67	-> 174
          //   #69	-> 177
          //   #70	-> 193
          //   #72	-> 200
          //   #73	-> 223
          //   #75	-> 227
          // Local variable table:
          //   start	length	slot	name	descriptor
          //   11	219	4	c	I
          //   0	230	0	this	Lorg/jline/terminal/impl/DumbTerminal$1;
          //   0	230	1	timeout	J
          //   0	230	3	isPeek	Z
        }
      };
    this.output = out;
    this.reader = NonBlocking.nonBlocking(getName(), (InputStream)this.input, encoding());
    this.writer = new PrintWriter(new OutputStreamWriter(this.output, encoding()));
    this.attributes = new Attributes();
    this.attributes.setControlChar(Attributes.ControlChar.VERASE, 127);
    this.attributes.setControlChar(Attributes.ControlChar.VWERASE, 23);
    this.attributes.setControlChar(Attributes.ControlChar.VKILL, 21);
    this.attributes.setControlChar(Attributes.ControlChar.VLNEXT, 22);
    this.size = new Size();
    parseInfoCmp();
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
    Attributes attr = new Attributes();
    attr.copy(this.attributes);
    return attr;
  }
  
  public void setAttributes(Attributes attr) {
    this.attributes.copy(attr);
  }
  
  public Size getSize() {
    Size sz = new Size();
    sz.copy(this.size);
    return sz;
  }
  
  public void setSize(Size sz) {
    this.size.copy(sz);
  }
}
