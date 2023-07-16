package net.minecrell.terminalconsole;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.apache.logging.log4j.LogManager;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;

public abstract class SimpleTerminalConsole {
  protected abstract boolean isRunning();
  
  protected abstract void runCommand(String paramString);
  
  protected abstract void shutdown();
  
  protected void processInput(String input) {
    String command = input.trim();
    if (!command.isEmpty())
      runCommand(command); 
  }
  
  protected LineReader buildReader(LineReaderBuilder builder) {
    LineReader reader = builder.build();
    reader.setOpt(LineReader.Option.DISABLE_EVENT_EXPANSION);
    reader.unsetOpt(LineReader.Option.INSERT_TAB);
    return reader;
  }
  
  public void start() {
    try {
      Terminal terminal = TerminalConsoleAppender.getTerminal();
      if (terminal != null) {
        readCommands(terminal);
      } else {
        readCommands(System.in);
      } 
    } catch (IOException e) {
      LogManager.getLogger("TerminalConsole").error("Failed to read console input", e);
    } 
  }
  
  private void readCommands(Terminal terminal) {
    LineReader reader = buildReader(LineReaderBuilder.builder().terminal(terminal));
    TerminalConsoleAppender.setReader(reader);
    try {
      while (isRunning()) {
        String line;
        try {
          line = reader.readLine("> ");
        } catch (EndOfFileException ignored) {
          continue;
        } 
        if (line == null)
          break; 
        processInput(line);
      } 
    } catch (UserInterruptException e) {
      String line;
      shutdown();
    } finally {
      TerminalConsoleAppender.setReader(null);
    } 
  }
  
  private void readCommands(InputStream in) throws IOException {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
      String line;
      while (isRunning() && (line = reader.readLine()) != null)
        processInput(line); 
    } 
  }
}
