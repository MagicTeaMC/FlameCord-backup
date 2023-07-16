package org.codehaus.plexus.util.cli;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;
import org.codehaus.plexus.util.Os;
import org.codehaus.plexus.util.StringUtils;

public abstract class CommandLineUtils {
  private static final long MILLIS_PER_SECOND = 1000L;
  
  private static final long NANOS_PER_SECOND = 1000000000L;
  
  public static class StringStreamConsumer implements StreamConsumer {
    private StringBuffer string = new StringBuffer();
    
    private String ls = System.getProperty("line.separator");
    
    public void consumeLine(String line) {
      this.string.append(line).append(this.ls);
    }
    
    public String getOutput() {
      return this.string.toString();
    }
  }
  
  public static int executeCommandLine(Commandline cl, StreamConsumer systemOut, StreamConsumer systemErr) throws CommandLineException {
    return executeCommandLine(cl, null, systemOut, systemErr, 0);
  }
  
  public static int executeCommandLine(Commandline cl, StreamConsumer systemOut, StreamConsumer systemErr, int timeoutInSeconds) throws CommandLineException {
    return executeCommandLine(cl, null, systemOut, systemErr, timeoutInSeconds);
  }
  
  public static int executeCommandLine(Commandline cl, InputStream systemIn, StreamConsumer systemOut, StreamConsumer systemErr) throws CommandLineException {
    return executeCommandLine(cl, systemIn, systemOut, systemErr, 0);
  }
  
  public static int executeCommandLine(Commandline cl, InputStream systemIn, StreamConsumer systemOut, StreamConsumer systemErr, int timeoutInSeconds) throws CommandLineException {
    CommandLineCallable future = executeCommandLineAsCallable(cl, systemIn, systemOut, systemErr, timeoutInSeconds);
    return future.call().intValue();
  }
  
  public static CommandLineCallable executeCommandLineAsCallable(Commandline cl, final InputStream systemIn, final StreamConsumer systemOut, final StreamConsumer systemErr, final int timeoutInSeconds) throws CommandLineException {
    if (cl == null)
      throw new IllegalArgumentException("cl cannot be null."); 
    final Process p = cl.execute();
    final Thread processHook = new Thread() {
        public void run() {
          p.destroy();
        }
      };
    ShutdownHookUtils.addShutDownHook(processHook);
    return new CommandLineCallable() {
        public Integer call() throws CommandLineException {
          StreamFeeder inputFeeder = null;
          StreamPumper outputPumper = null;
          StreamPumper errorPumper = null;
          boolean success = false;
          try {
            int returnValue;
            if (systemIn != null) {
              inputFeeder = new StreamFeeder(systemIn, p.getOutputStream());
              inputFeeder.start();
            } 
            outputPumper = new StreamPumper(p.getInputStream(), systemOut);
            outputPumper.start();
            errorPumper = new StreamPumper(p.getErrorStream(), systemErr);
            errorPumper.start();
            if (timeoutInSeconds <= 0) {
              returnValue = p.waitFor();
            } else {
              long now = System.nanoTime();
              long timeout = now + 1000000000L * timeoutInSeconds;
              while (CommandLineUtils.isAlive(p) && System.nanoTime() < timeout)
                Thread.sleep(999L); 
              if (CommandLineUtils.isAlive(p))
                throw new InterruptedException(String.format("Process timed out after %d seconds.", new Object[] { Integer.valueOf(this.val$timeoutInSeconds) })); 
              returnValue = p.exitValue();
            } 
            if (inputFeeder != null)
              inputFeeder.waitUntilDone(); 
            outputPumper.waitUntilDone();
            errorPumper.waitUntilDone();
            if (inputFeeder != null) {
              inputFeeder.close();
              CommandLineUtils.handleException(inputFeeder, "stdin");
            } 
            outputPumper.close();
            CommandLineUtils.handleException(outputPumper, "stdout");
            errorPumper.close();
            CommandLineUtils.handleException(errorPumper, "stderr");
            success = true;
            return Integer.valueOf(returnValue);
          } catch (InterruptedException ex) {
            throw new CommandLineTimeOutException("Error while executing external command, process killed.", ex);
          } finally {
            if (inputFeeder != null)
              inputFeeder.disable(); 
            if (outputPumper != null)
              outputPumper.disable(); 
            if (errorPumper != null)
              errorPumper.disable(); 
            try {
              ShutdownHookUtils.removeShutdownHook(processHook);
              processHook.run();
            } finally {
              try {
                if (inputFeeder != null) {
                  inputFeeder.close();
                  if (success) {
                    success = false;
                    CommandLineUtils.handleException(inputFeeder, "stdin");
                    success = true;
                  } 
                } 
              } finally {
                try {
                  if (outputPumper != null) {
                    outputPumper.close();
                    if (success) {
                      success = false;
                      CommandLineUtils.handleException(outputPumper, "stdout");
                      success = true;
                    } 
                  } 
                } finally {
                  if (errorPumper != null) {
                    errorPumper.close();
                    if (success)
                      CommandLineUtils.handleException(errorPumper, "stderr"); 
                  } 
                } 
              } 
            } 
          } 
        }
      };
  }
  
  private static void handleException(StreamPumper streamPumper, String streamName) throws CommandLineException {
    if (streamPumper.getException() != null)
      throw new CommandLineException(String.format("Failure processing %s.", new Object[] { streamName }), streamPumper.getException()); 
  }
  
  private static void handleException(StreamFeeder streamFeeder, String streamName) throws CommandLineException {
    if (streamFeeder.getException() != null)
      throw new CommandLineException(String.format("Failure processing %s.", new Object[] { streamName }), streamFeeder.getException()); 
  }
  
  public static Properties getSystemEnvVars() throws IOException {
    return getSystemEnvVars(!Os.isFamily("windows"));
  }
  
  public static Properties getSystemEnvVars(boolean caseSensitive) throws IOException {
    Properties envVars = new Properties();
    Map<String, String> envs = System.getenv();
    for (String key : envs.keySet()) {
      String value = envs.get(key);
      if (!caseSensitive)
        key = key.toUpperCase(Locale.ENGLISH); 
      envVars.put(key, value);
    } 
    return envVars;
  }
  
  public static boolean isAlive(Process p) {
    if (p == null)
      return false; 
    try {
      p.exitValue();
      return false;
    } catch (IllegalThreadStateException e) {
      return true;
    } 
  }
  
  public static String[] translateCommandline(String toProcess) throws Exception {
    if (toProcess == null || toProcess.length() == 0)
      return new String[0]; 
    int normal = 0;
    int inQuote = 1;
    int inDoubleQuote = 2;
    int state = 0;
    StringTokenizer tok = new StringTokenizer(toProcess, "\"' ", true);
    Vector<String> v = new Vector<String>();
    StringBuilder current = new StringBuilder();
    while (tok.hasMoreTokens()) {
      String nextTok = tok.nextToken();
      switch (state) {
        case 1:
          if ("'".equals(nextTok)) {
            state = 0;
            continue;
          } 
          current.append(nextTok);
          continue;
        case 2:
          if ("\"".equals(nextTok)) {
            state = 0;
            continue;
          } 
          current.append(nextTok);
          continue;
      } 
      if ("'".equals(nextTok)) {
        state = 1;
        continue;
      } 
      if ("\"".equals(nextTok)) {
        state = 2;
        continue;
      } 
      if (" ".equals(nextTok)) {
        if (current.length() != 0) {
          v.addElement(current.toString());
          current.setLength(0);
        } 
        continue;
      } 
      current.append(nextTok);
    } 
    if (current.length() != 0)
      v.addElement(current.toString()); 
    if (state == 1 || state == 2)
      throw new CommandLineException("unbalanced quotes in " + toProcess); 
    String[] args = new String[v.size()];
    v.copyInto((Object[])args);
    return args;
  }
  
  public static String quote(String argument) throws CommandLineException {
    return quote(argument, false, false, true);
  }
  
  public static String quote(String argument, boolean wrapExistingQuotes) throws CommandLineException {
    return quote(argument, false, false, wrapExistingQuotes);
  }
  
  public static String quote(String argument, boolean escapeSingleQuotes, boolean escapeDoubleQuotes, boolean wrapExistingQuotes) throws CommandLineException {
    if (argument.contains("\"")) {
      if (argument.contains("'"))
        throw new CommandLineException("Can't handle single and double quotes in same argument"); 
      if (escapeSingleQuotes)
        return "\\'" + argument + "\\'"; 
      if (wrapExistingQuotes)
        return '\'' + argument + '\''; 
    } else if (argument.contains("'")) {
      if (escapeDoubleQuotes)
        return "\\\"" + argument + "\\\""; 
      if (wrapExistingQuotes)
        return '"' + argument + '"'; 
    } else if (argument.contains(" ")) {
      if (escapeDoubleQuotes)
        return "\\\"" + argument + "\\\""; 
      return '"' + argument + '"';
    } 
    return argument;
  }
  
  public static String toString(String[] line) {
    if (line == null || line.length == 0)
      return ""; 
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < line.length; i++) {
      if (i > 0)
        result.append(' '); 
      try {
        result.append(StringUtils.quoteAndEscape(line[i], '"'));
      } catch (Exception e) {
        System.err.println("Error quoting argument: " + e.getMessage());
      } 
    } 
    return result.toString();
  }
}
