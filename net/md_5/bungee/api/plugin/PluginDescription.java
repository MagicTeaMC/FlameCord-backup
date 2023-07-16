package net.md_5.bungee.api.plugin;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PluginDescription {
  private String name;
  
  private String main;
  
  private String version;
  
  private String author;
  
  private Set<String> depends;
  
  private Set<String> softDepends;
  
  private File file;
  
  private String description;
  
  private List<String> libraries;
  
  public void setName(String name) {
    this.name = name;
  }
  
  public void setMain(String main) {
    this.main = main;
  }
  
  public void setVersion(String version) {
    this.version = version;
  }
  
  public void setAuthor(String author) {
    this.author = author;
  }
  
  public void setDepends(Set<String> depends) {
    this.depends = depends;
  }
  
  public void setSoftDepends(Set<String> softDepends) {
    this.softDepends = softDepends;
  }
  
  public void setFile(File file) {
    this.file = file;
  }
  
  public void setDescription(String description) {
    this.description = description;
  }
  
  public void setLibraries(List<String> libraries) {
    this.libraries = libraries;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof PluginDescription))
      return false; 
    PluginDescription other = (PluginDescription)o;
    if (!other.canEqual(this))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$main = getMain(), other$main = other.getMain();
    if ((this$main == null) ? (other$main != null) : !this$main.equals(other$main))
      return false; 
    Object this$version = getVersion(), other$version = other.getVersion();
    if ((this$version == null) ? (other$version != null) : !this$version.equals(other$version))
      return false; 
    Object this$author = getAuthor(), other$author = other.getAuthor();
    if ((this$author == null) ? (other$author != null) : !this$author.equals(other$author))
      return false; 
    Object<String> this$depends = (Object<String>)getDepends(), other$depends = (Object<String>)other.getDepends();
    if ((this$depends == null) ? (other$depends != null) : !this$depends.equals(other$depends))
      return false; 
    Object<String> this$softDepends = (Object<String>)getSoftDepends(), other$softDepends = (Object<String>)other.getSoftDepends();
    if ((this$softDepends == null) ? (other$softDepends != null) : !this$softDepends.equals(other$softDepends))
      return false; 
    Object this$file = getFile(), other$file = other.getFile();
    if ((this$file == null) ? (other$file != null) : !this$file.equals(other$file))
      return false; 
    Object this$description = getDescription(), other$description = other.getDescription();
    if ((this$description == null) ? (other$description != null) : !this$description.equals(other$description))
      return false; 
    Object<String> this$libraries = (Object<String>)getLibraries(), other$libraries = (Object<String>)other.getLibraries();
    return !((this$libraries == null) ? (other$libraries != null) : !this$libraries.equals(other$libraries));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof PluginDescription;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $main = getMain();
    result = result * 59 + (($main == null) ? 43 : $main.hashCode());
    Object $version = getVersion();
    result = result * 59 + (($version == null) ? 43 : $version.hashCode());
    Object $author = getAuthor();
    result = result * 59 + (($author == null) ? 43 : $author.hashCode());
    Object<String> $depends = (Object<String>)getDepends();
    result = result * 59 + (($depends == null) ? 43 : $depends.hashCode());
    Object<String> $softDepends = (Object<String>)getSoftDepends();
    result = result * 59 + (($softDepends == null) ? 43 : $softDepends.hashCode());
    Object $file = getFile();
    result = result * 59 + (($file == null) ? 43 : $file.hashCode());
    Object $description = getDescription();
    result = result * 59 + (($description == null) ? 43 : $description.hashCode());
    Object<String> $libraries = (Object<String>)getLibraries();
    return result * 59 + (($libraries == null) ? 43 : $libraries.hashCode());
  }
  
  public String toString() {
    return "PluginDescription(name=" + getName() + ", main=" + getMain() + ", version=" + getVersion() + ", author=" + getAuthor() + ", depends=" + getDepends() + ", softDepends=" + getSoftDepends() + ", file=" + getFile() + ", description=" + getDescription() + ", libraries=" + getLibraries() + ")";
  }
  
  public PluginDescription() {
    this.depends = new HashSet<>();
    this.softDepends = new HashSet<>();
    this.file = null;
    this.description = null;
    this.libraries = new LinkedList<>();
  }
  
  public PluginDescription(String name, String main, String version, String author, Set<String> depends, Set<String> softDepends, File file, String description, List<String> libraries) {
    this.depends = new HashSet<>();
    this.softDepends = new HashSet<>();
    this.file = null;
    this.description = null;
    this.libraries = new LinkedList<>();
    this.name = name;
    this.main = main;
    this.version = version;
    this.author = author;
    this.depends = depends;
    this.softDepends = softDepends;
    this.file = file;
    this.description = description;
    this.libraries = libraries;
  }
  
  public String getName() {
    return this.name;
  }
  
  public String getMain() {
    return this.main;
  }
  
  public String getVersion() {
    return this.version;
  }
  
  public String getAuthor() {
    return this.author;
  }
  
  public Set<String> getDepends() {
    return this.depends;
  }
  
  public Set<String> getSoftDepends() {
    return this.softDepends;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public String getDescription() {
    return this.description;
  }
  
  public List<String> getLibraries() {
    return this.libraries;
  }
}
