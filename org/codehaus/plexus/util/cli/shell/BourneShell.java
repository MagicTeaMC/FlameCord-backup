package org.codehaus.plexus.util.cli.shell;

import java.util.ArrayList;
import java.util.List;
import org.codehaus.plexus.util.Os;

public class BourneShell extends Shell {
  public BourneShell() {
    this(false);
  }
  
  public BourneShell(boolean isLoginShell) {
    setUnconditionalQuoting(true);
    setShellCommand("/bin/sh");
    setArgumentQuoteDelimiter('\'');
    setExecutableQuoteDelimiter('\'');
    setSingleQuotedArgumentEscaped(true);
    setSingleQuotedExecutableEscaped(false);
    setQuotedExecutableEnabled(true);
    setArgumentEscapePattern("'\\%s'");
    if (isLoginShell)
      addShellArg("-l"); 
  }
  
  public String getExecutable() {
    if (Os.isFamily("windows"))
      return super.getExecutable(); 
    return quoteOneItem(getOriginalExecutable(), true);
  }
  
  public List<String> getShellArgsList() {
    List<String> shellArgs = new ArrayList<String>();
    List<String> existingShellArgs = super.getShellArgsList();
    if (existingShellArgs != null && !existingShellArgs.isEmpty())
      shellArgs.addAll(existingShellArgs); 
    shellArgs.add("-c");
    return shellArgs;
  }
  
  public String[] getShellArgs() {
    String[] shellArgs = super.getShellArgs();
    if (shellArgs == null)
      shellArgs = new String[0]; 
    if (shellArgs.length > 0 && !shellArgs[shellArgs.length - 1].equals("-c")) {
      String[] newArgs = new String[shellArgs.length + 1];
      System.arraycopy(shellArgs, 0, newArgs, 0, shellArgs.length);
      newArgs[shellArgs.length] = "-c";
      shellArgs = newArgs;
    } 
    return shellArgs;
  }
  
  protected String getExecutionPreamble() {
    if (getWorkingDirectoryAsString() == null)
      return null; 
    String dir = getWorkingDirectoryAsString();
    StringBuilder sb = new StringBuilder();
    sb.append("cd ");
    sb.append(quoteOneItem(dir, false));
    sb.append(" && ");
    return sb.toString();
  }
  
  protected String quoteOneItem(String path, boolean isExecutable) {
    if (path == null)
      return null; 
    StringBuilder sb = new StringBuilder();
    sb.append("'");
    sb.append(path.replace("'", "'\"'\"'"));
    sb.append("'");
    return sb.toString();
  }
}
