package org.apache.maven.model.building;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.ModelReader;
import org.apache.maven.model.locator.ModelLocator;
import org.eclipse.sisu.Typed;

@Named("core-default")
@Singleton
@Typed({ModelProcessor.class})
public class DefaultModelProcessor implements ModelProcessor {
  @Inject
  private ModelLocator locator;
  
  @Inject
  private ModelReader reader;
  
  public DefaultModelProcessor setModelLocator(ModelLocator locator) {
    this.locator = locator;
    return this;
  }
  
  public DefaultModelProcessor setModelReader(ModelReader reader) {
    this.reader = reader;
    return this;
  }
  
  public File locatePom(File projectDirectory) {
    return this.locator.locatePom(projectDirectory);
  }
  
  public Model read(File input, Map<String, ?> options) throws IOException {
    return this.reader.read(input, options);
  }
  
  public Model read(Reader input, Map<String, ?> options) throws IOException {
    return this.reader.read(input, options);
  }
  
  public Model read(InputStream input, Map<String, ?> options) throws IOException {
    return this.reader.read(input, options);
  }
}
