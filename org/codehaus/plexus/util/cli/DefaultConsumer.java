package org.codehaus.plexus.util.cli;

import java.io.IOException;

public class DefaultConsumer implements StreamConsumer {
  public void consumeLine(String line) throws IOException {
    System.out.println(line);
    if (System.out.checkError())
      throw new IOException(String.format("Failure printing line '%s' to stdout.", new Object[] { line })); 
  }
}
