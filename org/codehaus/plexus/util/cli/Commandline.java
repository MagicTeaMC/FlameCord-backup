package org.codehaus.plexus.util.cli;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;
import org.codehaus.plexus.util.cli.shell.BourneShell;
import org.codehaus.plexus.util.cli.shell.CmdShell;
import org.codehaus.plexus.util.cli.shell.CommandShell;
import org.codehaus.plexus.util.cli.shell.Shell;

public class Commandline implements Cloneable {
  protected static final String OS_NAME = "os.name";
  
  protected static final String WINDOWS = "Windows";
  
  protected Vector<Arg> arguments = new Vector<Arg>();
  
  protected Map<String, String> envVars = Collections.synchronizedMap(new LinkedHashMap<String, String>());
  
  private long pid = -1L;
  
  private Shell shell;
  
  protected String executable;
  
  private File workingDir;
  
  public Commandline(String toProcess, Shell shell) {
    this.shell = shell;
    String[] tmp = new String[0];
    try {
      tmp = CommandLineUtils.translateCommandline(toProcess);
    } catch (Exception e) {
      System.err.println("Error translating Commandline.");
    } 
    if (tmp != null && tmp.length > 0) {
      setExecutable(tmp[0]);
      for (int i = 1; i < tmp.length; i++)
        createArgument().setValue(tmp[i]); 
    } 
  }
  
  public Commandline(Shell shell) {
    this.shell = shell;
  }
  
  public Commandline(String toProcess) {
    setDefaultShell();
    String[] tmp = new String[0];
    try {
      tmp = CommandLineUtils.translateCommandline(toProcess);
    } catch (Exception e) {
      System.err.println("Error translating Commandline.");
    } 
    if (tmp != null && tmp.length > 0) {
      setExecutable(tmp[0]);
      for (int i = 1; i < tmp.length; i++)
        createArgument().setValue(tmp[i]); 
    } 
  }
  
  public Commandline() {
    setDefaultShell();
  }
  
  public long getPid() {
    if (this.pid == -1L)
      this.pid = Long.parseLong(String.valueOf(System.currentTimeMillis())); 
    return this.pid;
  }
  
  public void setPid(long pid) {
    this.pid = pid;
  }
  
  public class Marker {
    private int position;
    
    private int realPos = -1;
    
    Marker(int position) {
      this.position = position;
    }
    
    public int getPosition() {
      if (this.realPos == -1) {
        this.realPos = (Commandline.this.getLiteralExecutable() == null) ? 0 : 1;
        for (int i = 0; i < this.position; i++) {
          Arg arg = Commandline.this.arguments.elementAt(i);
          this.realPos += (arg.getParts()).length;
        } 
      } 
      return this.realPos;
    }
  }
  
  private void setDefaultShell() {
    if (Os.isFamily("windows")) {
      if (Os.isFamily("win9x")) {
        setShell((Shell)new CommandShell());
      } else {
        setShell((Shell)new CmdShell());
      } 
    } else {
      setShell((Shell)new BourneShell());
    } 
  }
  
  public Argument createArgument() {
    return createArgument(false);
  }
  
  public Argument createArgument(boolean insertAtStart) {
    Argument argument = new Argument();
    if (insertAtStart) {
      this.arguments.insertElementAt(argument, 0);
    } else {
      this.arguments.addElement(argument);
    } 
    return argument;
  }
  
  public Arg createArg() {
    return createArg(false);
  }
  
  public Arg createArg(boolean insertAtStart) {
    Arg argument = new Argument();
    if (insertAtStart) {
      this.arguments.insertElementAt(argument, 0);
    } else {
      this.arguments.addElement(argument);
    } 
    return argument;
  }
  
  public void addArg(Arg argument) {
    addArg(argument, false);
  }
  
  public void addArg(Arg argument, boolean insertAtStart) {
    if (insertAtStart) {
      this.arguments.insertElementAt(argument, 0);
    } else {
      this.arguments.addElement(argument);
    } 
  }
  
  public void setExecutable(String executable) {
    this.shell.setExecutable(executable);
    this.executable = executable;
  }
  
  public String getLiteralExecutable() {
    return this.executable;
  }
  
  public String getExecutable() {
    String exec = this.shell.getExecutable();
    if (exec == null)
      exec = this.executable; 
    return exec;
  }
  
  public void addArguments(String[] line) {
    for (String aLine : line)
      createArgument().setValue(aLine); 
  }
  
  public void addEnvironment(String name, String value) {
    this.envVars.put(name, value);
  }
  
  public void addSystemEnvironment() throws Exception {
    Properties systemEnvVars = CommandLineUtils.getSystemEnvVars();
    for (Object o : systemEnvVars.keySet()) {
      String key = (String)o;
      if (!this.envVars.containsKey(key))
        addEnvironment(key, systemEnvVars.getProperty(key)); 
    } 
  }
  
  public String[] getEnvironmentVariables() throws CommandLineException {
    try {
      addSystemEnvironment();
    } catch (Exception e) {
      throw new CommandLineException("Error setting up environmental variables", e);
    } 
    String[] environmentVars = new String[this.envVars.size()];
    int i = 0;
    for (String o : this.envVars.keySet()) {
      String name = o;
      String value = this.envVars.get(name);
      environmentVars[i] = name + "=" + value;
      i++;
    } 
    return environmentVars;
  }
  
  public String[] getCommandline() {
    if (Os.isFamily("windows"))
      return getShellCommandline(); 
    return getRawCommandline();
  }
  
  public String[] getRawCommandline() {
    String[] args = getArguments();
    String executable = getLiteralExecutable();
    if (executable == null)
      return args; 
    String[] result = new String[args.length + 1];
    result[0] = executable;
    System.arraycopy(args, 0, result, 1, args.length);
    return result;
  }
  
  public String[] getShellCommandline() {
    verifyShellState();
    return (String[])getShell().getShellCommandLine(getArguments()).toArray((Object[])new String[0]);
  }
  
  public String[] getArguments() {
    Vector<String> result = new Vector<String>(this.arguments.size() * 2);
    for (int i = 0; i < this.arguments.size(); i++) {
      Arg arg = this.arguments.elementAt(i);
      String[] s = arg.getParts();
      if (s != null)
        for (String value : s)
          result.addElement(value);  
    } 
    String[] res = new String[result.size()];
    result.copyInto((Object[])res);
    return res;
  }
  
  public String toString() {
    return StringUtils.join((Object[])getShellCommandline(), " ");
  }
  
  public int size() {
    return (getCommandline()).length;
  }
  
  public Object clone() {
    Commandline c = new Commandline((Shell)this.shell.clone());
    c.executable = this.executable;
    c.workingDir = this.workingDir;
    c.addArguments(getArguments());
    return c;
  }
  
  public void clear() {
    this.executable = null;
    this.workingDir = null;
    this.shell.setExecutable(null);
    this.shell.clearArguments();
    this.arguments.removeAllElements();
  }
  
  public void clearArgs() {
    this.arguments.removeAllElements();
  }
  
  public Marker createMarker() {
    return new Marker(this.arguments.size());
  }
  
  public void setWorkingDirectory(String path) {
    this.shell.setWorkingDirectory(path);
    this.workingDir = new File(path);
  }
  
  public void setWorkingDirectory(File workingDirectory) {
    this.shell.setWorkingDirectory(workingDirectory);
    this.workingDir = workingDirectory;
  }
  
  public File getWorkingDirectory() {
    File workDir = this.shell.getWorkingDirectory();
    if (workDir == null)
      workDir = this.workingDir; 
    return workDir;
  }
  
  public Process execute() throws CommandLineException {
    Process process;
    verifyShellState();
    String[] environment = getEnvironmentVariables();
    File workingDir = this.shell.getWorkingDirectory();
    try {
      if (workingDir == null) {
        process = Runtime.getRuntime().exec(getCommandline(), environment, workingDir);
      } else {
        if (!workingDir.exists())
          throw new CommandLineException("Working directory \"" + workingDir.getPath() + "\" does not exist!"); 
        if (!workingDir.isDirectory())
          throw new CommandLineException("Path \"" + workingDir.getPath() + "\" does not specify a directory."); 
        process = Runtime.getRuntime().exec(getCommandline(), environment, workingDir);
      } 
    } catch (IOException ex) {
      throw new CommandLineException("Error while executing process.", ex);
    } 
    return process;
  }
  
  private void verifyShellState() {
    if (this.shell.getWorkingDirectory() == null)
      this.shell.setWorkingDirectory(this.workingDir); 
    if (this.shell.getOriginalExecutable() == null)
      this.shell.setExecutable(this.executable); 
  }
  
  public Properties getSystemEnvVars() throws Exception {
    return CommandLineUtils.getSystemEnvVars();
  }
  
  public void setShell(Shell shell) {
    this.shell = shell;
  }
  
  public Shell getShell() {
    return this.shell;
  }
  
  public static String[] translateCommandline(String toProcess) throws Exception {
    return CommandLineUtils.translateCommandline(toProcess);
  }
  
  public static String quoteArgument(String argument) throws CommandLineException {
    return CommandLineUtils.quote(argument);
  }
  
  public static String toString(String[] line) {
    return CommandLineUtils.toString(line);
  }
  
  public static class Argument implements Arg {
    private String[] parts;
    
    public void setValue(String value) {
      if (value != null)
        this.parts = new String[] { value }; 
    }
    
    public void setLine(String line) {
      if (line == null)
        return; 
      try {
        this.parts = CommandLineUtils.translateCommandline(line);
      } catch (Exception e) {
        System.err.println("Error translating Commandline.");
      } 
    }
    
    public void setFile(File value) {
      this.parts = new String[] { value.getAbsolutePath() };
    }
    
    public String[] getParts() {
      return this.parts;
    }
  }
}
