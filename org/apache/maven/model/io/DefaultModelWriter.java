package org.apache.maven.model.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;
import java.util.Objects;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.WriterFactory;

@Named
@Singleton
public class DefaultModelWriter implements ModelWriter {
  public void write(File output, Map<String, Object> options, Model model) throws IOException {
    Objects.requireNonNull(output, "output cannot be null");
    Objects.requireNonNull(model, "model cannot be null");
    output.getParentFile().mkdirs();
    write((Writer)WriterFactory.newXmlWriter(output), options, model);
  }
  
  public void write(Writer output, Map<String, Object> options, Model model) throws IOException {
    Objects.requireNonNull(output, "output cannot be null");
    Objects.requireNonNull(model, "model cannot be null");
    try (Writer out = output) {
      (new MavenXpp3Writer()).write(out, model);
    } 
  }
  
  public void write(OutputStream output, Map<String, Object> options, Model model) throws IOException {
    Objects.requireNonNull(output, "output cannot be null");
    Objects.requireNonNull(model, "model cannot be null");
    String encoding = model.getModelEncoding();
    if (encoding == null || encoding.length() <= 0)
      encoding = "UTF-8"; 
    try (Writer out = new OutputStreamWriter(output, encoding)) {
      write(out, options, model);
    } 
  }
}
