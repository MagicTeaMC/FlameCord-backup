package org.jline.terminal;

import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicReference;
import org.jline.terminal.impl.AbstractPosixTerminal;
import org.jline.terminal.impl.AbstractTerminal;
import org.jline.terminal.impl.DumbTerminal;
import org.jline.terminal.impl.ExecPty;
import org.jline.terminal.impl.ExternalTerminal;
import org.jline.terminal.impl.PosixPtyTerminal;
import org.jline.terminal.impl.PosixSysTerminal;
import org.jline.terminal.spi.JansiSupport;
import org.jline.terminal.spi.JnaSupport;
import org.jline.terminal.spi.Pty;
import org.jline.utils.Log;
import org.jline.utils.OSUtils;

public final class TerminalBuilder {
  public static final String PROP_ENCODING = "org.jline.terminal.encoding";
  
  public static final String PROP_CODEPAGE = "org.jline.terminal.codepage";
  
  public static final String PROP_TYPE = "org.jline.terminal.type";
  
  public static final String PROP_JNA = "org.jline.terminal.jna";
  
  public static final String PROP_JANSI = "org.jline.terminal.jansi";
  
  public static final String PROP_EXEC = "org.jline.terminal.exec";
  
  public static final String PROP_DUMB = "org.jline.terminal.dumb";
  
  public static final String PROP_DUMB_COLOR = "org.jline.terminal.dumb.color";
  
  public static final String PROP_NON_BLOCKING_READS = "org.jline.terminal.pty.nonBlockingReads";
  
  public static final String PROP_COLOR_DISTANCE = "org.jline.utils.colorDistance";
  
  public static final String PROP_DISABLE_ALTERNATE_CHARSET = "org.jline.utils.disableAlternateCharset";
  
  public static Terminal terminal() throws IOException {
    return builder().build();
  }
  
  public static TerminalBuilder builder() {
    return new TerminalBuilder();
  }
  
  private static final AtomicReference<Terminal> SYSTEM_TERMINAL = new AtomicReference<>();
  
  private static final AtomicReference<Terminal> TERMINAL_OVERRIDE = new AtomicReference<>();
  
  private String name;
  
  private InputStream in;
  
  private OutputStream out;
  
  private String type;
  
  private Charset encoding;
  
  private int codepage;
  
  private Boolean system;
  
  private Boolean jna;
  
  private Boolean jansi;
  
  private Boolean exec;
  
  private Boolean dumb;
  
  private Boolean color;
  
  private Attributes attributes;
  
  private Size size;
  
  private boolean nativeSignals = false;
  
  private Terminal.SignalHandler signalHandler = Terminal.SignalHandler.SIG_DFL;
  
  private boolean paused = false;
  
  public TerminalBuilder name(String name) {
    this.name = name;
    return this;
  }
  
  public TerminalBuilder streams(InputStream in, OutputStream out) {
    this.in = in;
    this.out = out;
    return this;
  }
  
  public TerminalBuilder system(boolean system) {
    this.system = Boolean.valueOf(system);
    return this;
  }
  
  public TerminalBuilder jna(boolean jna) {
    this.jna = Boolean.valueOf(jna);
    return this;
  }
  
  public TerminalBuilder jansi(boolean jansi) {
    this.jansi = Boolean.valueOf(jansi);
    return this;
  }
  
  public TerminalBuilder exec(boolean exec) {
    this.exec = Boolean.valueOf(exec);
    return this;
  }
  
  public TerminalBuilder dumb(boolean dumb) {
    this.dumb = Boolean.valueOf(dumb);
    return this;
  }
  
  public TerminalBuilder type(String type) {
    this.type = type;
    return this;
  }
  
  public TerminalBuilder color(boolean color) {
    this.color = Boolean.valueOf(color);
    return this;
  }
  
  public TerminalBuilder encoding(String encoding) throws UnsupportedCharsetException {
    return encoding((encoding != null) ? Charset.forName(encoding) : null);
  }
  
  public TerminalBuilder encoding(Charset encoding) {
    this.encoding = encoding;
    return this;
  }
  
  @Deprecated
  public TerminalBuilder codepage(int codepage) {
    this.codepage = codepage;
    return this;
  }
  
  public TerminalBuilder attributes(Attributes attributes) {
    this.attributes = attributes;
    return this;
  }
  
  public TerminalBuilder size(Size size) {
    this.size = size;
    return this;
  }
  
  public TerminalBuilder nativeSignals(boolean nativeSignals) {
    this.nativeSignals = nativeSignals;
    return this;
  }
  
  public TerminalBuilder signalHandler(Terminal.SignalHandler signalHandler) {
    this.signalHandler = signalHandler;
    return this;
  }
  
  public TerminalBuilder paused(boolean paused) {
    this.paused = paused;
    return this;
  }
  
  public Terminal build() throws IOException {
    Terminal override = TERMINAL_OVERRIDE.get();
    Terminal terminal = (override != null) ? override : doBuild();
    if (override != null)
      Log.debug(() -> "Overriding terminal with global value set by TerminalBuilder.setTerminalOverride"); 
    Log.debug(() -> "Using terminal " + terminal.getClass().getSimpleName());
    if (terminal instanceof AbstractPosixTerminal)
      Log.debug(() -> "Using pty " + ((AbstractPosixTerminal)terminal).getPty().getClass().getSimpleName()); 
    return terminal;
  }
  
  private Terminal doBuild() throws IOException {
    String name = this.name;
    if (name == null)
      name = "JLine terminal"; 
    Charset encoding = this.encoding;
    if (encoding == null) {
      String charsetName = System.getProperty("org.jline.terminal.encoding");
      if (charsetName != null && Charset.isSupported(charsetName))
        encoding = Charset.forName(charsetName); 
    } 
    int codepage = this.codepage;
    if (codepage <= 0) {
      String str = System.getProperty("org.jline.terminal.codepage");
      if (str != null)
        codepage = Integer.parseInt(str); 
    } 
    String type = this.type;
    if (type == null)
      type = System.getProperty("org.jline.terminal.type"); 
    if (type == null)
      type = System.getenv("TERM"); 
    Boolean jna = this.jna;
    if (jna == null)
      jna = getBoolean("org.jline.terminal.jna", Boolean.valueOf(true)); 
    Boolean jansi = this.jansi;
    if (jansi == null)
      jansi = getBoolean("org.jline.terminal.jansi", Boolean.valueOf(true)); 
    Boolean exec = this.exec;
    if (exec == null)
      exec = getBoolean("org.jline.terminal.exec", Boolean.valueOf(true)); 
    Boolean dumb = this.dumb;
    if (dumb == null)
      dumb = getBoolean("org.jline.terminal.dumb", null); 
    if ((this.system != null && this.system.booleanValue()) || (this.system == null && this.in == null && this.out == null)) {
      PosixSysTerminal posixSysTerminal;
      DumbTerminal dumbTerminal;
      if (this.system != null && ((this.in != null && !this.in.equals(System.in)) || (this.out != null && !this.out.equals(System.out))))
        throw new IllegalArgumentException("Cannot create a system terminal using non System streams"); 
      Terminal terminal = null;
      IllegalStateException exception = new IllegalStateException("Unable to create a system terminal");
      TerminalBuilderSupport tbs = new TerminalBuilderSupport(jna.booleanValue(), jansi.booleanValue());
      if (tbs.isConsoleInput() && tbs.isConsoleOutput()) {
        if (this.attributes != null || this.size != null)
          Log.warn(new Object[] { "Attributes and size fields are ignored when creating a system terminal" }); 
        if (OSUtils.IS_WINDOWS) {
          boolean ansiPassThrough = OSUtils.IS_CONEMU;
          if (tbs.hasJnaSupport())
            try {
              terminal = tbs.getJnaSupport().winSysTerminal(name, type, ansiPassThrough, encoding, codepage, this.nativeSignals, this.signalHandler, this.paused);
            } catch (Throwable t) {
              Log.debug(new Object[] { "Error creating JNA based terminal: ", t.getMessage(), t });
              exception.addSuppressed(t);
            }  
          if (terminal == null && tbs.hasJansiSupport())
            try {
              terminal = tbs.getJansiSupport().winSysTerminal(name, type, ansiPassThrough, encoding, codepage, this.nativeSignals, this.signalHandler, this.paused);
            } catch (Throwable t) {
              Log.debug(new Object[] { "Error creating JANSI based terminal: ", t.getMessage(), t });
              exception.addSuppressed(t);
            }  
          if (terminal == null && exec.booleanValue() && (OSUtils.IS_CYGWIN || OSUtils.IS_MSYSTEM))
            try {
              if ("xterm".equals(type) && this.type == null && System.getProperty("org.jline.terminal.type") == null)
                type = "xterm-256color"; 
              posixSysTerminal = new PosixSysTerminal(name, type, tbs.getExecPty(), encoding, this.nativeSignals, this.signalHandler);
            } catch (IOException e) {
              Log.debug(new Object[] { "Error creating EXEC based terminal: ", e.getMessage(), e });
              exception.addSuppressed(e);
            }  
          if (posixSysTerminal == null && !jna.booleanValue() && !jansi.booleanValue() && (dumb == null || !dumb.booleanValue()))
            throw new IllegalStateException("Unable to create a system terminal. On windows, either JNA or JANSI library is required.  Make sure to add one of those in the classpath."); 
        } else {
          if (tbs.hasJnaSupport())
            try {
              Pty pty = tbs.getJnaSupport().current();
              posixSysTerminal = new PosixSysTerminal(name, type, pty, encoding, this.nativeSignals, this.signalHandler);
            } catch (Throwable t) {
              Log.debug(new Object[] { "Error creating JNA based terminal: ", t.getMessage(), t });
              exception.addSuppressed(t);
            }  
          if (posixSysTerminal == null && tbs.hasJansiSupport())
            try {
              Pty pty = tbs.getJansiSupport().current();
              posixSysTerminal = new PosixSysTerminal(name, type, pty, encoding, this.nativeSignals, this.signalHandler);
            } catch (Throwable t) {
              Log.debug(new Object[] { "Error creating JANSI based terminal: ", t.getMessage(), t });
              exception.addSuppressed(t);
            }  
          if (posixSysTerminal == null && exec.booleanValue())
            try {
              posixSysTerminal = new PosixSysTerminal(name, type, tbs.getExecPty(), encoding, this.nativeSignals, this.signalHandler);
            } catch (Throwable t) {
              Log.debug(new Object[] { "Error creating EXEC based terminal: ", t.getMessage(), t });
              exception.addSuppressed(t);
            }  
        } 
        if (posixSysTerminal instanceof AbstractTerminal) {
          AbstractTerminal t = (AbstractTerminal)posixSysTerminal;
          if (SYSTEM_TERMINAL.compareAndSet(null, t)) {
            t.setOnClose(() -> SYSTEM_TERMINAL.compareAndSet(t, null));
          } else {
            exception.addSuppressed(new IllegalStateException("A system terminal is already running. Make sure to use the created system Terminal on the LineReaderBuilder if you're using one or that previously created system Terminals have been correctly closed."));
            posixSysTerminal.close();
            posixSysTerminal = null;
          } 
        } 
      } 
      if (posixSysTerminal == null && (dumb == null || dumb.booleanValue())) {
        Boolean color = this.color;
        if (color == null) {
          color = getBoolean("org.jline.terminal.dumb.color", Boolean.valueOf(false));
          if (!color.booleanValue())
            color = Boolean.valueOf((System.getenv("INSIDE_EMACS") != null)); 
          if (!color.booleanValue()) {
            String command = getParentProcessCommand();
            color = Boolean.valueOf((command != null && command.contains("idea")));
          } 
          if (!color.booleanValue())
            color = Boolean.valueOf((tbs.isConsoleOutput() && System.getenv("TERM") != null)); 
          if (!color.booleanValue() && dumb == null)
            if (Log.isDebugEnabled()) {
              Log.warn(new Object[] { "input is tty: ", Boolean.valueOf(tbs.isConsoleInput()) });
              Log.warn(new Object[] { "output is tty: ", Boolean.valueOf(tbs.isConsoleOutput()) });
              Log.warn(new Object[] { "Creating a dumb terminal", exception });
            } else {
              Log.warn(new Object[] { "Unable to create a system terminal, creating a dumb terminal (enable debug logging for more information)" });
            }  
        } 
        dumbTerminal = new DumbTerminal(name, color.booleanValue() ? "dumb-color" : "dumb", new FileInputStream(FileDescriptor.in), new FileOutputStream(FileDescriptor.out), encoding, this.signalHandler);
      } 
      if (dumbTerminal == null)
        throw exception; 
      return (Terminal)dumbTerminal;
    } 
    if (jna.booleanValue())
      try {
        Pty pty = ((JnaSupport)load(JnaSupport.class)).open(this.attributes, this.size);
        return (Terminal)new PosixPtyTerminal(name, type, pty, this.in, this.out, encoding, this.signalHandler, this.paused);
      } catch (Throwable t) {
        Log.debug(new Object[] { "Error creating JNA based terminal: ", t.getMessage(), t });
      }  
    if (jansi.booleanValue())
      try {
        Pty pty = ((JansiSupport)load(JansiSupport.class)).open(this.attributes, this.size);
        return (Terminal)new PosixPtyTerminal(name, type, pty, this.in, this.out, encoding, this.signalHandler, this.paused);
      } catch (Throwable t) {
        Log.debug(new Object[] { "Error creating JANSI based terminal: ", t.getMessage(), t });
      }  
    return (Terminal)new ExternalTerminal(name, type, this.in, this.out, encoding, this.signalHandler, this.paused, this.attributes, this.size);
  }
  
  private static String getParentProcessCommand() {
    try {
      Class<?> phClass = Class.forName("java.lang.ProcessHandle");
      Object current = phClass.getMethod("current", new Class[0]).invoke(null, new Object[0]);
      Object parent = ((Optional)phClass.getMethod("parent", new Class[0]).invoke(current, new Object[0])).orElse(null);
      Method infoMethod = phClass.getMethod("info", new Class[0]);
      Object info = infoMethod.invoke(parent, new Object[0]);
      Object command = ((Optional)infoMethod.getReturnType().getMethod("command", new Class[0]).invoke(info, new Object[0])).orElse(null);
      return (String)command;
    } catch (Throwable t) {
      return null;
    } 
  }
  
  private static Boolean getBoolean(String name, Boolean def) {
    try {
      String str = System.getProperty(name);
      if (str != null)
        return Boolean.valueOf(Boolean.parseBoolean(str)); 
    } catch (IllegalArgumentException|NullPointerException illegalArgumentException) {}
    return def;
  }
  
  private static <S> S load(Class<S> clazz) {
    return ServiceLoader.<S>load(clazz, clazz.getClassLoader()).iterator().next();
  }
  
  @Deprecated
  public static void setTerminalOverride(Terminal terminal) {
    TERMINAL_OVERRIDE.set(terminal);
  }
  
  private static class TerminalBuilderSupport {
    private JansiSupport jansiSupport = null;
    
    private JnaSupport jnaSupport = null;
    
    private boolean jnaFullSupport;
    
    private boolean jansiFullSupport;
    
    private Pty pty = null;
    
    private boolean consoleOutput;
    
    TerminalBuilderSupport(boolean jna, boolean jansi) {
      if (jna)
        try {
          this.jnaSupport = (JnaSupport)TerminalBuilder.load((Class)JnaSupport.class);
          this.consoleOutput = this.jnaSupport.isConsoleOutput();
          this.jnaFullSupport = true;
        } catch (Throwable e) {
          Log.debug(new Object[] { "jnaSupport.isConsoleOutput(): ", e });
        }  
      if (jansi)
        try {
          this.jansiSupport = (JansiSupport)TerminalBuilder.load((Class)JansiSupport.class);
          this.consoleOutput = this.jansiSupport.isConsoleOutput();
          this.jansiFullSupport = true;
        } catch (Throwable e) {
          Log.debug(new Object[] { "jansiSupport.isConsoleOutput(): ", e });
        }  
      if (!this.jnaFullSupport && !this.jansiFullSupport)
        try {
          this.pty = ExecPty.current();
          this.consoleOutput = true;
        } catch (Exception e) {
          Log.debug(new Object[] { "ExecPty.current(): ", e });
        }  
    }
    
    public boolean isConsoleOutput() {
      return this.consoleOutput;
    }
    
    public boolean isConsoleInput() {
      if (this.jnaFullSupport)
        return this.jnaSupport.isConsoleInput(); 
      if (this.jansiFullSupport)
        return this.jansiSupport.isConsoleInput(); 
      if (this.pty != null)
        return true; 
      return false;
    }
    
    public boolean hasJnaSupport() {
      return (this.jnaSupport != null);
    }
    
    public boolean hasJansiSupport() {
      return (this.jansiSupport != null);
    }
    
    public JnaSupport getJnaSupport() {
      return this.jnaSupport;
    }
    
    public JansiSupport getJansiSupport() {
      return this.jansiSupport;
    }
    
    public Pty getExecPty() throws IOException {
      if (this.pty == null)
        this.pty = ExecPty.current(); 
      return this.pty;
    }
  }
}
