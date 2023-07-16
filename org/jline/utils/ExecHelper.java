package org.jline.utils;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.util.Objects;

public final class ExecHelper {
  public static String exec(boolean redirectInput, String... cmd) throws IOException {
    Objects.requireNonNull(cmd);
    try {
      Log.trace(new Object[] { "Running: ", cmd });
      ProcessBuilder pb = new ProcessBuilder(cmd);
      if (redirectInput)
        pb.redirectInput(ProcessBuilder.Redirect.INHERIT); 
      Process p = pb.start();
      String result = waitAndCapture(p);
      Log.trace(new Object[] { "Result: ", result });
      if (p.exitValue() != 0) {
        if (result.endsWith("\n"))
          result = result.substring(0, result.length() - 1); 
        throw new IOException("Error executing '" + String.join(" ", (CharSequence[])cmd) + "': " + result);
      } 
      return result;
    } catch (InterruptedException e) {
      throw (IOException)(new InterruptedIOException("Command interrupted")).initCause(e);
    } 
  }
  
  public static String waitAndCapture(Process p) throws IOException, InterruptedException {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    InputStream in = null;
    InputStream err = null;
    OutputStream out = null;
    try {
      in = p.getInputStream();
      int c;
      while ((c = in.read()) != -1)
        bout.write(c); 
      err = p.getErrorStream();
      while ((c = err.read()) != -1)
        bout.write(c); 
      out = p.getOutputStream();
      p.waitFor();
    } finally {
      close(new Closeable[] { in, out, err });
    } 
    return bout.toString();
  }
  
  private static void close(Closeable... closeables) {
    for (Closeable c : closeables) {
      if (c != null)
        try {
          c.close();
        } catch (Exception exception) {} 
    } 
  }
}
