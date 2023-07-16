package org.jline.terminal.impl;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.terminal.Attributes;
import org.jline.terminal.Size;
import org.jline.terminal.spi.Pty;
import org.jline.utils.ExecHelper;
import org.jline.utils.OSUtils;

public class ExecPty extends AbstractPty implements Pty {
  private final String name;
  
  private final boolean system;
  
  public static Pty current() throws IOException {
    try {
      String result = ExecHelper.exec(true, new String[] { OSUtils.TTY_COMMAND });
      return new ExecPty(result.trim(), true);
    } catch (IOException e) {
      throw new IOException("Not a tty", e);
    } 
  }
  
  protected ExecPty(String name, boolean system) {
    this.name = name;
    this.system = system;
  }
  
  public void close() throws IOException {}
  
  public String getName() {
    return this.name;
  }
  
  public InputStream getMasterInput() {
    throw new UnsupportedOperationException();
  }
  
  public OutputStream getMasterOutput() {
    throw new UnsupportedOperationException();
  }
  
  protected InputStream doGetSlaveInput() throws IOException {
    return this.system ? 
      new FileInputStream(FileDescriptor.in) : 
      new FileInputStream(getName());
  }
  
  public OutputStream getSlaveOutput() throws IOException {
    return this.system ? 
      new FileOutputStream(FileDescriptor.out) : 
      new FileOutputStream(getName());
  }
  
  public Attributes getAttr() throws IOException {
    String cfg = doGetConfig();
    return doGetAttr(cfg);
  }
  
  protected void doSetAttr(Attributes attr) throws IOException {
    List<String> commands = getFlagsToSet(attr, getAttr());
    if (!commands.isEmpty()) {
      commands.add(0, OSUtils.STTY_COMMAND);
      if (!this.system) {
        commands.add(1, OSUtils.STTY_F_OPTION);
        commands.add(2, getName());
      } 
      ExecHelper.exec(this.system, commands.<String>toArray(new String[commands.size()]));
    } 
  }
  
  protected List<String> getFlagsToSet(Attributes attr, Attributes current) {
    List<String> commands = new ArrayList<>();
    for (Attributes.InputFlag flag : Attributes.InputFlag.values()) {
      if (attr.getInputFlag(flag) != current.getInputFlag(flag))
        commands.add((attr.getInputFlag(flag) ? flag.name() : ("-" + flag.name())).toLowerCase()); 
    } 
    for (Attributes.OutputFlag flag : Attributes.OutputFlag.values()) {
      if (attr.getOutputFlag(flag) != current.getOutputFlag(flag))
        commands.add((attr.getOutputFlag(flag) ? flag.name() : ("-" + flag.name())).toLowerCase()); 
    } 
    for (Attributes.ControlFlag flag : Attributes.ControlFlag.values()) {
      if (attr.getControlFlag(flag) != current.getControlFlag(flag))
        commands.add((attr.getControlFlag(flag) ? flag.name() : ("-" + flag.name())).toLowerCase()); 
    } 
    for (Attributes.LocalFlag flag : Attributes.LocalFlag.values()) {
      if (attr.getLocalFlag(flag) != current.getLocalFlag(flag))
        commands.add((attr.getLocalFlag(flag) ? flag.name() : ("-" + flag.name())).toLowerCase()); 
    } 
    String undef = System.getProperty("os.name").toLowerCase().startsWith("hp") ? "^-" : "undef";
    for (Attributes.ControlChar cchar : Attributes.ControlChar.values()) {
      int v = attr.getControlChar(cchar);
      if (v >= 0 && v != current.getControlChar(cchar)) {
        String str = "";
        commands.add(cchar.name().toLowerCase().substring(1));
        if (cchar == Attributes.ControlChar.VMIN || cchar == Attributes.ControlChar.VTIME) {
          commands.add(Integer.toString(v));
        } else if (v == 0) {
          commands.add(undef);
        } else {
          if (v >= 128) {
            v -= 128;
            str = str + "M-";
          } 
          if (v < 32 || v == 127) {
            v ^= 0x40;
            str = str + "^";
          } 
          str = str + (char)v;
          commands.add(str);
        } 
      } 
    } 
    return commands;
  }
  
  public Size getSize() throws IOException {
    String cfg = doGetConfig();
    return doGetSize(cfg);
  }
  
  protected String doGetConfig() throws IOException {
    return this.system ? 
      ExecHelper.exec(true, new String[] { OSUtils.STTY_COMMAND, "-a" }) : ExecHelper.exec(false, new String[] { OSUtils.STTY_COMMAND, OSUtils.STTY_F_OPTION, getName(), "-a" });
  }
  
  static Attributes doGetAttr(String cfg) throws IOException {
    Attributes attributes = new Attributes();
    for (Attributes.InputFlag flag : Attributes.InputFlag.values()) {
      Boolean value = doGetFlag(cfg, (Enum<?>)flag);
      if (value != null)
        attributes.setInputFlag(flag, value.booleanValue()); 
    } 
    for (Attributes.OutputFlag flag : Attributes.OutputFlag.values()) {
      Boolean value = doGetFlag(cfg, (Enum<?>)flag);
      if (value != null)
        attributes.setOutputFlag(flag, value.booleanValue()); 
    } 
    for (Attributes.ControlFlag flag : Attributes.ControlFlag.values()) {
      Boolean value = doGetFlag(cfg, (Enum<?>)flag);
      if (value != null)
        attributes.setControlFlag(flag, value.booleanValue()); 
    } 
    for (Attributes.LocalFlag flag : Attributes.LocalFlag.values()) {
      Boolean value = doGetFlag(cfg, (Enum<?>)flag);
      if (value != null)
        attributes.setLocalFlag(flag, value.booleanValue()); 
    } 
    for (Attributes.ControlChar cchar : Attributes.ControlChar.values()) {
      String name = cchar.name().toLowerCase().substring(1);
      if ("reprint".endsWith(name))
        name = "(?:reprint|rprnt)"; 
      Matcher matcher = Pattern.compile("[\\s;]" + name + "\\s*=\\s*(.+?)[\\s;]").matcher(cfg);
      if (matcher.find())
        attributes.setControlChar(cchar, parseControlChar(matcher.group(1).toUpperCase())); 
    } 
    return attributes;
  }
  
  private static Boolean doGetFlag(String cfg, Enum<?> flag) {
    Matcher matcher = Pattern.compile("(?:^|[\\s;])(\\-?" + flag.name().toLowerCase() + ")(?:[\\s;]|$)").matcher(cfg);
    return matcher.find() ? Boolean.valueOf(!matcher.group(1).startsWith("-")) : null;
  }
  
  static int parseControlChar(String str) {
    if ("<UNDEF>".equals(str))
      return -1; 
    if ("DEL".equalsIgnoreCase(str))
      return 127; 
    if (str.charAt(0) == '0')
      return Integer.parseInt(str, 8); 
    if (str.charAt(0) >= '1' && str.charAt(0) <= '9')
      return Integer.parseInt(str, 10); 
    if (str.charAt(0) == '^') {
      if (str.charAt(1) == '?')
        return 127; 
      return str.charAt(1) - 64;
    } 
    if (str.charAt(0) == 'M' && str.charAt(1) == '-') {
      if (str.charAt(2) == '^') {
        if (str.charAt(3) == '?')
          return 255; 
        return str.charAt(3) - 64 + 128;
      } 
      return str.charAt(2) + 128;
    } 
    return str.charAt(0);
  }
  
  static Size doGetSize(String cfg) throws IOException {
    return new Size(doGetInt("columns", cfg), doGetInt("rows", cfg));
  }
  
  static int doGetInt(String name, String cfg) throws IOException {
    String[] patterns = { "\\b([0-9]+)\\s+" + name + "\\b", "\\b" + name + "\\s+([0-9]+)\\b", "\\b" + name + "\\s*=\\s*([0-9]+)\\b" };
    for (String pattern : patterns) {
      Matcher matcher = Pattern.compile(pattern).matcher(cfg);
      if (matcher.find())
        return Integer.parseInt(matcher.group(1)); 
    } 
    throw new IOException("Unable to parse " + name);
  }
  
  public void setSize(Size size) throws IOException {
    if (this.system) {
      ExecHelper.exec(true, new String[] { OSUtils.STTY_COMMAND, "columns", 
            
            Integer.toString(size.getColumns()), "rows", 
            Integer.toString(size.getRows()) });
    } else {
      ExecHelper.exec(false, new String[] { OSUtils.STTY_COMMAND, OSUtils.STTY_F_OPTION, 
            
            getName(), "columns", 
            Integer.toString(size.getColumns()), "rows", 
            Integer.toString(size.getRows()) });
    } 
  }
  
  public String toString() {
    return "ExecPty[" + getName() + (this.system ? ", system]" : "]");
  }
}
