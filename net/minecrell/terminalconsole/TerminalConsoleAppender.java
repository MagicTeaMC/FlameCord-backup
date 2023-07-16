package net.minecrell.terminalconsole;

import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.util.PropertiesUtil;
import org.jline.reader.LineReader;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

@Plugin(name = "TerminalConsole", category = "Core", elementType = "appender", printObject = true)
public final class TerminalConsoleAppender extends AbstractAppender {
  public static final String PLUGIN_NAME = "TerminalConsole";
  
  static final String PROPERTY_PREFIX = "terminal";
  
  public static final String JLINE_OVERRIDE_PROPERTY = "terminal.jline";
  
  public static final String ANSI_OVERRIDE_PROPERTY = "terminal.ansi";
  
  private static final Boolean ANSI_OVERRIDE = getOptionalBooleanProperty("terminal.ansi");
  
  private static final PrintStream stdout = System.out;
  
  private static boolean initialized;
  
  private static Terminal terminal;
  
  private static LineReader reader;
  
  public static synchronized Terminal getTerminal() {
    return terminal;
  }
  
  public static synchronized LineReader getReader() {
    return reader;
  }
  
  public static synchronized void setReader(LineReader newReader) {
    if (newReader != null && newReader.getTerminal() != terminal)
      throw new IllegalArgumentException("Reader was not created with TerminalConsoleAppender.getTerminal()"); 
    reader = newReader;
  }
  
  public static boolean isAnsiSupported() {
    if (!initialized)
      initializeTerminal(); 
    return (ANSI_OVERRIDE != null) ? ANSI_OVERRIDE.booleanValue() : ((terminal != null));
  }
  
  protected TerminalConsoleAppender(String name, Filter filter, Layout<? extends Serializable> layout, boolean ignoreExceptions) {
    super(name, filter, layout, ignoreExceptions);
    if (!initialized)
      initializeTerminal(); 
  }
  
  private static synchronized void initializeTerminal() {
    if (!initialized) {
      initialized = true;
      Boolean jlineOverride = getOptionalBooleanProperty("terminal.jline");
      boolean dumb = (jlineOverride == Boolean.TRUE || System.getProperty("java.class.path").contains("idea_rt.jar"));
      if (jlineOverride != Boolean.FALSE)
        try {
          terminal = TerminalBuilder.builder().dumb(dumb).build();
        } catch (IllegalStateException e) {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.warn("Advanced terminal features are not available in this environment", e);
          } else {
            LOGGER.warn("Advanced terminal features are not available in this environment");
          } 
        } catch (IOException e) {
          LOGGER.error("Failed to initialize terminal. Falling back to standard console", e);
        }  
    } 
  }
  
  public void append(LogEvent event) {
    print(getLayout().toSerializable(event).toString());
  }
  
  private static synchronized void print(String text) {
    if (terminal != null) {
      if (reader != null) {
        reader.printAbove(text);
      } else {
        terminal.writer().print(text);
        terminal.writer().flush();
      } 
    } else {
      stdout.print(text);
    } 
  }
  
  public static synchronized void close() throws IOException {
    if (initialized) {
      initialized = false;
      reader = null;
      if (terminal != null)
        try {
          terminal.close();
        } finally {
          terminal = null;
        }  
    } 
  }
  
  @PluginFactory
  public static TerminalConsoleAppender createAppender(@Required(message = "No name provided for TerminalConsoleAppender") @PluginAttribute("name") String name, @PluginElement("Filter") Filter filter, @PluginElement("Layout") Layout<? extends Serializable> layout, @PluginAttribute(value = "ignoreExceptions", defaultBoolean = true) boolean ignoreExceptions) {
    PatternLayout patternLayout;
    if (layout == null)
      patternLayout = PatternLayout.createDefaultLayout(); 
    return new TerminalConsoleAppender(name, filter, (Layout<? extends Serializable>)patternLayout, ignoreExceptions);
  }
  
  private static Boolean getOptionalBooleanProperty(String name) {
    String value = PropertiesUtil.getProperties().getStringProperty(name);
    if (value == null)
      return null; 
    if (value.equalsIgnoreCase("true"))
      return Boolean.TRUE; 
    if (value.equalsIgnoreCase("false"))
      return Boolean.FALSE; 
    LOGGER.warn("Invalid value for boolean input property '{}': {}", name, value);
    return null;
  }
}
