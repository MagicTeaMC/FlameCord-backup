package org.apache.maven.model.path;

import java.io.File;
import javax.inject.Named;
import javax.inject.Singleton;

@Named
@Singleton
public class DefaultPathTranslator implements PathTranslator {
  public String alignToBaseDirectory(String path, File basedir) {
    String result = path;
    if (path != null && basedir != null) {
      path = path.replace('\\', File.separatorChar).replace('/', File.separatorChar);
      File file = new File(path);
      if (file.isAbsolute()) {
        result = file.getPath();
      } else if (file.getPath().startsWith(File.separator)) {
        result = file.getAbsolutePath();
      } else {
        result = (new File((new File(basedir, path)).toURI().normalize())).getAbsolutePath();
      } 
    } 
    return result;
  }
}
