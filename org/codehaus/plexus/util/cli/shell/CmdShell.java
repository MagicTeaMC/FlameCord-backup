package org.codehaus.plexus.util.cli.shell;

import java.util.Arrays;
import java.util.List;

public class CmdShell extends Shell {
  public CmdShell() {
    setShellCommand("cmd.exe");
    setQuotedExecutableEnabled(true);
    setShellArgs(new String[] { "/X", "/C" });
  }
  
  public List<String> getCommandLine(String executable, String[] arguments) {
    StringBuilder sb = new StringBuilder();
    sb.append("\"");
    sb.append(super.getCommandLine(executable, arguments).get(0));
    sb.append("\"");
    return Arrays.asList(new String[] { sb.toString() });
  }
}
