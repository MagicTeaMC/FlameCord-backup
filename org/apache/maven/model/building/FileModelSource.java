package org.apache.maven.model.building;

import java.io.File;
import java.net.URI;
import org.apache.maven.building.FileSource;

public class FileModelSource extends FileSource implements ModelSource2 {
  public FileModelSource(File pomFile) {
    super(pomFile);
  }
  
  @Deprecated
  public File getPomFile() {
    return getFile();
  }
  
  public ModelSource2 getRelatedSource(String relPath) {
    relPath = relPath.replace('\\', File.separatorChar).replace('/', File.separatorChar);
    File relatedPom = new File(getFile().getParentFile(), relPath);
    if (relatedPom.isDirectory())
      relatedPom = new File(relatedPom, "pom.xml"); 
    if (relatedPom.isFile() && relatedPom.canRead())
      return new FileModelSource(new File(relatedPom.toURI().normalize())); 
    return null;
  }
  
  public URI getLocationURI() {
    return getFile().toURI();
  }
  
  public boolean equals(Object obj) {
    if (this == obj)
      return true; 
    if (!(obj instanceof FileModelSource))
      return false; 
    FileModelSource other = (FileModelSource)obj;
    return getFile().equals(other.getFile());
  }
  
  public int hashCode() {
    return getFile().hashCode();
  }
}
