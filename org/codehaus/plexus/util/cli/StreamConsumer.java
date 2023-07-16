package org.codehaus.plexus.util.cli;

import java.io.IOException;

public interface StreamConsumer {
  void consumeLine(String paramString) throws IOException;
}
