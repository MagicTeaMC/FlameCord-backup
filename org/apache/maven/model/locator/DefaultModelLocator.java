package org.apache.maven.model.locator;

import java.io.File;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class DefaultModelLocator implements ModelLocator {
  public File locatePom(File projectDirectory) {
    return new File(projectDirectory, "pom.xml");
  }
}
