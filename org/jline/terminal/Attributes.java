package org.jline.terminal;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Attributes {
  public enum ControlChar {
    VEOF, VEOL, VEOL2, VERASE, VWERASE, VKILL, VREPRINT, VINTR, VQUIT, VSUSP, VDSUSP, VSTART, VSTOP, VLNEXT, VDISCARD, VMIN, VTIME, VSTATUS;
  }
  
  public enum InputFlag {
    IGNBRK, BRKINT, IGNPAR, PARMRK, INPCK, ISTRIP, INLCR, IGNCR, ICRNL, IXON, IXOFF, IXANY, IMAXBEL, IUTF8;
  }
  
  public enum OutputFlag {
    OPOST, ONLCR, OXTABS, ONOEOT, OCRNL, ONOCR, ONLRET, OFILL, NLDLY, TABDLY, CRDLY, FFDLY, BSDLY, VTDLY, OFDEL;
  }
  
  public enum ControlFlag {
    CIGNORE, CS5, CS6, CS7, CS8, CSTOPB, CREAD, PARENB, PARODD, HUPCL, CLOCAL, CCTS_OFLOW, CRTS_IFLOW, CDTR_IFLOW, CDSR_OFLOW, CCAR_OFLOW;
  }
  
  public enum LocalFlag {
    ECHOKE, ECHOE, ECHOK, ECHO, ECHONL, ECHOPRT, ECHOCTL, ISIG, ICANON, ALTWERASE, IEXTEN, EXTPROC, TOSTOP, FLUSHO, NOKERNINFO, PENDIN, NOFLSH;
  }
  
  final EnumSet<InputFlag> iflag = EnumSet.noneOf(InputFlag.class);
  
  final EnumSet<OutputFlag> oflag = EnumSet.noneOf(OutputFlag.class);
  
  final EnumSet<ControlFlag> cflag = EnumSet.noneOf(ControlFlag.class);
  
  final EnumSet<LocalFlag> lflag = EnumSet.noneOf(LocalFlag.class);
  
  final EnumMap<ControlChar, Integer> cchars = new EnumMap<>(ControlChar.class);
  
  public Attributes(Attributes attr) {
    copy(attr);
  }
  
  public EnumSet<InputFlag> getInputFlags() {
    return this.iflag;
  }
  
  public void setInputFlags(EnumSet<InputFlag> flags) {
    this.iflag.clear();
    this.iflag.addAll(flags);
  }
  
  public boolean getInputFlag(InputFlag flag) {
    return this.iflag.contains(flag);
  }
  
  public void setInputFlags(EnumSet<InputFlag> flags, boolean value) {
    if (value) {
      this.iflag.addAll(flags);
    } else {
      this.iflag.removeAll(flags);
    } 
  }
  
  public void setInputFlag(InputFlag flag, boolean value) {
    if (value) {
      this.iflag.add(flag);
    } else {
      this.iflag.remove(flag);
    } 
  }
  
  public EnumSet<OutputFlag> getOutputFlags() {
    return this.oflag;
  }
  
  public void setOutputFlags(EnumSet<OutputFlag> flags) {
    this.oflag.clear();
    this.oflag.addAll(flags);
  }
  
  public boolean getOutputFlag(OutputFlag flag) {
    return this.oflag.contains(flag);
  }
  
  public void setOutputFlags(EnumSet<OutputFlag> flags, boolean value) {
    if (value) {
      this.oflag.addAll(flags);
    } else {
      this.oflag.removeAll(flags);
    } 
  }
  
  public void setOutputFlag(OutputFlag flag, boolean value) {
    if (value) {
      this.oflag.add(flag);
    } else {
      this.oflag.remove(flag);
    } 
  }
  
  public EnumSet<ControlFlag> getControlFlags() {
    return this.cflag;
  }
  
  public void setControlFlags(EnumSet<ControlFlag> flags) {
    this.cflag.clear();
    this.cflag.addAll(flags);
  }
  
  public boolean getControlFlag(ControlFlag flag) {
    return this.cflag.contains(flag);
  }
  
  public void setControlFlags(EnumSet<ControlFlag> flags, boolean value) {
    if (value) {
      this.cflag.addAll(flags);
    } else {
      this.cflag.removeAll(flags);
    } 
  }
  
  public void setControlFlag(ControlFlag flag, boolean value) {
    if (value) {
      this.cflag.add(flag);
    } else {
      this.cflag.remove(flag);
    } 
  }
  
  public EnumSet<LocalFlag> getLocalFlags() {
    return this.lflag;
  }
  
  public void setLocalFlags(EnumSet<LocalFlag> flags) {
    this.lflag.clear();
    this.lflag.addAll(flags);
  }
  
  public boolean getLocalFlag(LocalFlag flag) {
    return this.lflag.contains(flag);
  }
  
  public void setLocalFlags(EnumSet<LocalFlag> flags, boolean value) {
    if (value) {
      this.lflag.addAll(flags);
    } else {
      this.lflag.removeAll(flags);
    } 
  }
  
  public void setLocalFlag(LocalFlag flag, boolean value) {
    if (value) {
      this.lflag.add(flag);
    } else {
      this.lflag.remove(flag);
    } 
  }
  
  public EnumMap<ControlChar, Integer> getControlChars() {
    return this.cchars;
  }
  
  public void setControlChars(EnumMap<ControlChar, Integer> chars) {
    this.cchars.clear();
    this.cchars.putAll(chars);
  }
  
  public int getControlChar(ControlChar c) {
    Integer v = this.cchars.get(c);
    return (v != null) ? v.intValue() : -1;
  }
  
  public void setControlChar(ControlChar c, int value) {
    this.cchars.put(c, Integer.valueOf(value));
  }
  
  public void copy(Attributes attributes) {
    setControlFlags(attributes.getControlFlags());
    setInputFlags(attributes.getInputFlags());
    setLocalFlags(attributes.getLocalFlags());
    setOutputFlags(attributes.getOutputFlags());
    setControlChars(attributes.getControlChars());
  }
  
  public String toString() {
    return "Attributes[lflags: " + 
      append(this.lflag) + ", iflags: " + 
      append(this.iflag) + ", oflags: " + 
      append(this.oflag) + ", cflags: " + 
      append(this.cflag) + ", cchars: " + 
      append(EnumSet.allOf((Class)ControlChar.class), this::display) + "]";
  }
  
  private String display(ControlChar c) {
    String value;
    int ch = getControlChar(c);
    if (c == ControlChar.VMIN || c == ControlChar.VTIME) {
      value = Integer.toString(ch);
    } else if (ch < 0) {
      value = "<undef>";
    } else if (ch < 32) {
      value = "^" + (char)(ch + 65 - 1);
    } else if (ch == 127) {
      value = "^?";
    } else if (ch >= 128) {
      value = String.format("\\u%04x", new Object[] { Integer.valueOf(ch) });
    } else {
      value = String.valueOf((char)ch);
    } 
    return c.name().toLowerCase().substring(1) + "=" + value;
  }
  
  private <T extends Enum<T>> String append(EnumSet<T> set) {
    return append(set, e -> e.name().toLowerCase());
  }
  
  private <T extends Enum<T>> String append(EnumSet<T> set, Function<T, String> toString) {
    return set.stream().<CharSequence>map((Function)toString).collect(Collectors.joining(" "));
  }
  
  public Attributes() {}
}
