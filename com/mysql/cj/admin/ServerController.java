package com.mysql.cj.admin;

import com.mysql.cj.Constants;
import com.mysql.cj.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

public class ServerController {
  public static final String BASEDIR_KEY = "basedir";
  
  public static final String DATADIR_KEY = "datadir";
  
  public static final String DEFAULTS_FILE_KEY = "defaults-file";
  
  public static final String EXECUTABLE_NAME_KEY = "executable";
  
  public static final String EXECUTABLE_PATH_KEY = "executablePath";
  
  private Process serverProcess = null;
  
  private Properties serverProps = null;
  
  public ServerController(String baseDir) {
    setBaseDir(baseDir);
  }
  
  public ServerController(String basedir, String datadir) {}
  
  public void setBaseDir(String baseDir) {
    getServerProps().setProperty("basedir", baseDir);
  }
  
  public void setDataDir(String dataDir) {
    getServerProps().setProperty("datadir", dataDir);
  }
  
  public Process start() throws IOException {
    if (this.serverProcess != null)
      throw new IllegalArgumentException("Server already started"); 
    this.serverProcess = Runtime.getRuntime().exec(getCommandLine());
    return this.serverProcess;
  }
  
  public void stop(boolean forceIfNecessary) throws IOException {
    if (this.serverProcess != null) {
      String basedir = getServerProps().getProperty("basedir");
      StringBuilder pathBuf = new StringBuilder(basedir);
      if (!basedir.endsWith(File.separator))
        pathBuf.append(File.separator); 
      pathBuf.append("bin");
      pathBuf.append(File.separator);
      pathBuf.append("mysqladmin shutdown");
      System.out.println(pathBuf.toString());
      Process mysqladmin = Runtime.getRuntime().exec(pathBuf.toString());
      int exitStatus = -1;
      try {
        exitStatus = mysqladmin.waitFor();
      } catch (InterruptedException interruptedException) {}
      if (exitStatus != 0 && forceIfNecessary)
        forceStop(); 
    } 
  }
  
  public void forceStop() {
    if (this.serverProcess != null) {
      this.serverProcess.destroy();
      this.serverProcess = null;
    } 
  }
  
  public synchronized Properties getServerProps() {
    if (this.serverProps == null)
      this.serverProps = new Properties(); 
    return this.serverProps;
  }
  
  private String getCommandLine() {
    StringBuilder commandLine = new StringBuilder(getFullExecutablePath());
    commandLine.append(buildOptionalCommandLine());
    return commandLine.toString();
  }
  
  private String getFullExecutablePath() {
    StringBuilder pathBuf = new StringBuilder();
    String optionalExecutablePath = getServerProps().getProperty("executablePath");
    if (optionalExecutablePath == null) {
      String basedir = getServerProps().getProperty("basedir");
      pathBuf.append(basedir);
      if (!basedir.endsWith(File.separator))
        pathBuf.append(File.separatorChar); 
      if (runningOnWindows()) {
        pathBuf.append("bin");
      } else {
        pathBuf.append("libexec");
      } 
      pathBuf.append(File.separatorChar);
    } else {
      pathBuf.append(optionalExecutablePath);
      if (!optionalExecutablePath.endsWith(File.separator))
        pathBuf.append(File.separatorChar); 
    } 
    String executableName = getServerProps().getProperty("executable", "mysqld");
    pathBuf.append(executableName);
    return pathBuf.toString();
  }
  
  private String buildOptionalCommandLine() {
    StringBuilder commandLineBuf = new StringBuilder();
    if (this.serverProps != null)
      for (Iterator<Object> iter = this.serverProps.keySet().iterator(); iter.hasNext(); ) {
        String key = (String)iter.next();
        String value = this.serverProps.getProperty(key);
        if (!isNonCommandLineArgument(key)) {
          if (value != null && value.length() > 0) {
            commandLineBuf.append(" \"");
            commandLineBuf.append("--");
            commandLineBuf.append(key);
            commandLineBuf.append("=");
            commandLineBuf.append(value);
            commandLineBuf.append("\"");
            continue;
          } 
          commandLineBuf.append(" --");
          commandLineBuf.append(key);
        } 
      }  
    return commandLineBuf.toString();
  }
  
  private boolean isNonCommandLineArgument(String propName) {
    return (propName.equals("executable") || propName.equals("executablePath"));
  }
  
  private boolean runningOnWindows() {
    return (StringUtils.indexOfIgnoreCase(Constants.OS_NAME, "WINDOWS") != -1);
  }
}
