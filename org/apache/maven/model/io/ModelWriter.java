package org.apache.maven.model.io;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;
import org.apache.maven.model.Model;

public interface ModelWriter {
  void write(File paramFile, Map<String, Object> paramMap, Model paramModel) throws IOException;
  
  void write(Writer paramWriter, Map<String, Object> paramMap, Model paramModel) throws IOException;
  
  void write(OutputStream paramOutputStream, Map<String, Object> paramMap, Model paramModel) throws IOException;
}
