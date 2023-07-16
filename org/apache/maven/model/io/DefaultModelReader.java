package org.apache.maven.model.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import java.util.Objects;
import javax.inject.Named;
import javax.inject.Singleton;
import org.apache.maven.model.InputSource;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3ReaderEx;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.XmlStreamReader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

@Named
@Singleton
public class DefaultModelReader implements ModelReader {
  public Model read(File input, Map<String, ?> options) throws IOException {
    Objects.requireNonNull(input, "input cannot be null");
    Model model = read(new FileInputStream(input), options);
    model.setPomFile(input);
    return model;
  }
  
  public Model read(Reader input, Map<String, ?> options) throws IOException {
    Objects.requireNonNull(input, "input cannot be null");
    try (Reader in = input) {
      return read(in, isStrict(options), getSource(options));
    } 
  }
  
  public Model read(InputStream input, Map<String, ?> options) throws IOException {
    Objects.requireNonNull(input, "input cannot be null");
    try (XmlStreamReader in = ReaderFactory.newXmlReader(input)) {
      return read((Reader)in, isStrict(options), getSource(options));
    } 
  }
  
  private boolean isStrict(Map<String, ?> options) {
    Object value = (options != null) ? options.get("org.apache.maven.model.io.isStrict") : null;
    return (value == null || Boolean.parseBoolean(value.toString()));
  }
  
  private InputSource getSource(Map<String, ?> options) {
    Object value = (options != null) ? options.get("org.apache.maven.model.io.inputSource") : null;
    return (InputSource)value;
  }
  
  private Model read(Reader reader, boolean strict, InputSource source) throws IOException {
    try {
      if (source != null)
        return (new MavenXpp3ReaderEx()).read(reader, strict, source); 
      return (new MavenXpp3Reader()).read(reader, strict);
    } catch (XmlPullParserException e) {
      throw new ModelParseException(e.getMessage(), e.getLineNumber(), e.getColumnNumber(), e);
    } 
  }
}
