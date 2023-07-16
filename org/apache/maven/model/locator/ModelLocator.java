package org.apache.maven.model.locator;

import java.io.File;

public interface ModelLocator {
  File locatePom(File paramFile);
}
