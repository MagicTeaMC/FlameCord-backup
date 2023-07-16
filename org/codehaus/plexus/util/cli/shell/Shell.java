package org.codehaus.plexus.util.cli.shell;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.codehaus.plexus.util.StringUtils;

public class Shell implements Cloneable {
  private static final char[] DEFAULT_QUOTING_TRIGGER_CHARS = new char[] { ' ' };
  
  private String shellCommand;
  
  private List<String> shellArgs = new ArrayList<String>();
  
  private boolean quotedArgumentsEnabled = true;
  
  private boolean unconditionallyQuote = false;
  
  private String executable;
  
  private String workingDir;
  
  private boolean quotedExecutableEnabled = true;
  
  private boolean doubleQuotedArgumentEscaped = false;
  
  private boolean singleQuotedArgumentEscaped = false;
  
  private boolean doubleQuotedExecutableEscaped = false;
  
  private boolean singleQuotedExecutableEscaped = false;
  
  private char argQuoteDelimiter = '"';
  
  private char exeQuoteDelimiter = '"';
  
  private String argumentEscapePattern = "\\%s";
  
  public void setUnconditionalQuoting(boolean unconditionallyQuote) {
    this.unconditionallyQuote = unconditionallyQuote;
  }
  
  public void setShellCommand(String shellCommand) {
    this.shellCommand = shellCommand;
  }
  
  public String getShellCommand() {
    return this.shellCommand;
  }
  
  public void setShellArgs(String[] shellArgs) {
    this.shellArgs.clear();
    this.shellArgs.addAll(Arrays.asList(shellArgs));
  }
  
  public String[] getShellArgs() {
    if (this.shellArgs == null || this.shellArgs.isEmpty())
      return null; 
    return this.shellArgs.<String>toArray(new String[this.shellArgs.size()]);
  }
  
  public List<String> getCommandLine(String executable, String[] arguments) {
    return getRawCommandLine(executable, arguments);
  }
  
  protected String quoteOneItem(String inputString, boolean isExecutable) {
    char[] escapeChars = getEscapeChars(isSingleQuotedExecutableEscaped(), isDoubleQuotedExecutableEscaped());
    return StringUtils.quoteAndEscape(inputString, isExecutable ? getExecutableQuoteDelimiter() : getArgumentQuoteDelimiter(), escapeChars, getQuotingTriggerChars(), '\\', this.unconditionallyQuote);
  }
  
  protected List<String> getRawCommandLine(String executable, String[] arguments) {
    List<String> commandLine = new ArrayList<String>();
    StringBuilder sb = new StringBuilder();
    if (executable != null) {
      String preamble = getExecutionPreamble();
      if (preamble != null)
        sb.append(preamble); 
      if (isQuotedExecutableEnabled()) {
        sb.append(quoteOneItem(getOriginalExecutable(), true));
      } else {
        sb.append(getExecutable());
      } 
    } 
    for (String argument : arguments) {
      if (sb.length() > 0)
        sb.append(" "); 
      if (isQuotedArgumentsEnabled()) {
        sb.append(quoteOneItem(argument, false));
      } else {
        sb.append(argument);
      } 
    } 
    commandLine.add(sb.toString());
    return commandLine;
  }
  
  protected char[] getQuotingTriggerChars() {
    return DEFAULT_QUOTING_TRIGGER_CHARS;
  }
  
  protected String getExecutionPreamble() {
    return null;
  }
  
  protected char[] getEscapeChars(boolean includeSingleQuote, boolean includeDoubleQuote) {
    StringBuilder buf = new StringBuilder(2);
    if (includeSingleQuote)
      buf.append('\''); 
    if (includeDoubleQuote)
      buf.append('"'); 
    char[] result = new char[buf.length()];
    buf.getChars(0, buf.length(), result, 0);
    return result;
  }
  
  protected boolean isDoubleQuotedArgumentEscaped() {
    return this.doubleQuotedArgumentEscaped;
  }
  
  protected boolean isSingleQuotedArgumentEscaped() {
    return this.singleQuotedArgumentEscaped;
  }
  
  protected boolean isDoubleQuotedExecutableEscaped() {
    return this.doubleQuotedExecutableEscaped;
  }
  
  protected boolean isSingleQuotedExecutableEscaped() {
    return this.singleQuotedExecutableEscaped;
  }
  
  protected void setArgumentQuoteDelimiter(char argQuoteDelimiter) {
    this.argQuoteDelimiter = argQuoteDelimiter;
  }
  
  protected char getArgumentQuoteDelimiter() {
    return this.argQuoteDelimiter;
  }
  
  protected void setExecutableQuoteDelimiter(char exeQuoteDelimiter) {
    this.exeQuoteDelimiter = exeQuoteDelimiter;
  }
  
  protected char getExecutableQuoteDelimiter() {
    return this.exeQuoteDelimiter;
  }
  
  protected void setArgumentEscapePattern(String argumentEscapePattern) {
    this.argumentEscapePattern = argumentEscapePattern;
  }
  
  protected String getArgumentEscapePattern() {
    return this.argumentEscapePattern;
  }
  
  public List<String> getShellCommandLine(String[] arguments) {
    List<String> commandLine = new ArrayList<String>();
    if (getShellCommand() != null)
      commandLine.add(getShellCommand()); 
    if (getShellArgs() != null)
      commandLine.addAll(getShellArgsList()); 
    commandLine.addAll(getCommandLine(getOriginalExecutable(), arguments));
    return commandLine;
  }
  
  public List<String> getShellArgsList() {
    return this.shellArgs;
  }
  
  public void addShellArg(String arg) {
    this.shellArgs.add(arg);
  }
  
  public void setQuotedArgumentsEnabled(boolean quotedArgumentsEnabled) {
    this.quotedArgumentsEnabled = quotedArgumentsEnabled;
  }
  
  public boolean isQuotedArgumentsEnabled() {
    return this.quotedArgumentsEnabled;
  }
  
  public void setQuotedExecutableEnabled(boolean quotedExecutableEnabled) {
    this.quotedExecutableEnabled = quotedExecutableEnabled;
  }
  
  public boolean isQuotedExecutableEnabled() {
    return this.quotedExecutableEnabled;
  }
  
  public void setExecutable(String executable) {
    if (executable == null || executable.length() == 0)
      return; 
    this.executable = executable.replace('/', File.separatorChar).replace('\\', File.separatorChar);
  }
  
  public String getExecutable() {
    return this.executable;
  }
  
  public void setWorkingDirectory(String path) {
    if (path != null)
      this.workingDir = path; 
  }
  
  public void setWorkingDirectory(File workingDir) {
    if (workingDir != null)
      this.workingDir = workingDir.getAbsolutePath(); 
  }
  
  public File getWorkingDirectory() {
    return (this.workingDir == null) ? null : new File(this.workingDir);
  }
  
  public String getWorkingDirectoryAsString() {
    return this.workingDir;
  }
  
  public void clearArguments() {
    this.shellArgs.clear();
  }
  
  public Object clone() {
    Shell shell = new Shell();
    shell.setExecutable(getExecutable());
    shell.setWorkingDirectory(getWorkingDirectory());
    shell.setShellArgs(getShellArgs());
    return shell;
  }
  
  public String getOriginalExecutable() {
    return this.executable;
  }
  
  public List<String> getOriginalCommandLine(String executable, String[] arguments) {
    return getRawCommandLine(executable, arguments);
  }
  
  protected void setDoubleQuotedArgumentEscaped(boolean doubleQuotedArgumentEscaped) {
    this.doubleQuotedArgumentEscaped = doubleQuotedArgumentEscaped;
  }
  
  protected void setDoubleQuotedExecutableEscaped(boolean doubleQuotedExecutableEscaped) {
    this.doubleQuotedExecutableEscaped = doubleQuotedExecutableEscaped;
  }
  
  protected void setSingleQuotedArgumentEscaped(boolean singleQuotedArgumentEscaped) {
    this.singleQuotedArgumentEscaped = singleQuotedArgumentEscaped;
  }
  
  protected void setSingleQuotedExecutableEscaped(boolean singleQuotedExecutableEscaped) {
    this.singleQuotedExecutableEscaped = singleQuotedExecutableEscaped;
  }
}
