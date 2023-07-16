package org.codehaus.plexus.util.cli.shell;

public class CommandShell extends Shell {
  public CommandShell() {
    setShellCommand("command.com");
    setShellArgs(new String[] { "/C" });
  }
}
