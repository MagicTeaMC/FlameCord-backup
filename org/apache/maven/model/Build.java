package org.apache.maven.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Build extends BuildBase implements Serializable, Cloneable {
  private String sourceDirectory;
  
  private String scriptSourceDirectory;
  
  private String testSourceDirectory;
  
  private String outputDirectory;
  
  private String testOutputDirectory;
  
  private List<Extension> extensions;
  
  public void addExtension(Extension extension) {
    getExtensions().add(extension);
  }
  
  public Build clone() {
    try {
      Build copy = (Build)super.clone();
      if (this.extensions != null) {
        copy.extensions = new ArrayList<>();
        for (Extension item : this.extensions)
          copy.extensions.add(item.clone()); 
      } 
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
  
  public List<Extension> getExtensions() {
    if (this.extensions == null)
      this.extensions = new ArrayList<>(); 
    return this.extensions;
  }
  
  public String getOutputDirectory() {
    return this.outputDirectory;
  }
  
  public String getScriptSourceDirectory() {
    return this.scriptSourceDirectory;
  }
  
  public String getSourceDirectory() {
    return this.sourceDirectory;
  }
  
  public String getTestOutputDirectory() {
    return this.testOutputDirectory;
  }
  
  public String getTestSourceDirectory() {
    return this.testSourceDirectory;
  }
  
  public void removeExtension(Extension extension) {
    getExtensions().remove(extension);
  }
  
  public void setExtensions(List<Extension> extensions) {
    this.extensions = extensions;
  }
  
  public void setOutputDirectory(String outputDirectory) {
    this.outputDirectory = outputDirectory;
  }
  
  public void setScriptSourceDirectory(String scriptSourceDirectory) {
    this.scriptSourceDirectory = scriptSourceDirectory;
  }
  
  public void setSourceDirectory(String sourceDirectory) {
    this.sourceDirectory = sourceDirectory;
  }
  
  public void setTestOutputDirectory(String testOutputDirectory) {
    this.testOutputDirectory = testOutputDirectory;
  }
  
  public void setTestSourceDirectory(String testSourceDirectory) {
    this.testSourceDirectory = testSourceDirectory;
  }
}
