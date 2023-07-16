package org.apache.maven.model.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Map;
import org.apache.maven.model.Model;

public interface ModelReader {
  public static final String IS_STRICT = "org.apache.maven.model.io.isStrict";
  
  public static final String INPUT_SOURCE = "org.apache.maven.model.io.inputSource";
  
  Model read(File paramFile, Map<String, ?> paramMap) throws IOException, ModelParseException;
  
  Model read(Reader paramReader, Map<String, ?> paramMap) throws IOException, ModelParseException;
  
  Model read(InputStream paramInputStream, Map<String, ?> paramMap) throws IOException, ModelParseException;
}
