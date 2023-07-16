package net.md_5.bungee.module;

import java.io.File;

public class ModuleSpec {
  private final String name;
  
  private final File file;
  
  private final ModuleSource provider;
  
  public ModuleSpec(String name, File file, ModuleSource provider) {
    this.name = name;
    this.file = file;
    this.provider = provider;
  }
  
  public boolean equals(Object o) {
    if (o == this)
      return true; 
    if (!(o instanceof ModuleSpec))
      return false; 
    ModuleSpec other = (ModuleSpec)o;
    if (!other.canEqual(this))
      return false; 
    Object this$name = getName(), other$name = other.getName();
    if ((this$name == null) ? (other$name != null) : !this$name.equals(other$name))
      return false; 
    Object this$file = getFile(), other$file = other.getFile();
    if ((this$file == null) ? (other$file != null) : !this$file.equals(other$file))
      return false; 
    Object this$provider = getProvider(), other$provider = other.getProvider();
    return !((this$provider == null) ? (other$provider != null) : !this$provider.equals(other$provider));
  }
  
  protected boolean canEqual(Object other) {
    return other instanceof ModuleSpec;
  }
  
  public int hashCode() {
    int PRIME = 59;
    result = 1;
    Object $name = getName();
    result = result * 59 + (($name == null) ? 43 : $name.hashCode());
    Object $file = getFile();
    result = result * 59 + (($file == null) ? 43 : $file.hashCode());
    Object $provider = getProvider();
    return result * 59 + (($provider == null) ? 43 : $provider.hashCode());
  }
  
  public String toString() {
    return "ModuleSpec(name=" + getName() + ", file=" + getFile() + ", provider=" + getProvider() + ")";
  }
  
  public String getName() {
    return this.name;
  }
  
  public File getFile() {
    return this.file;
  }
  
  public ModuleSource getProvider() {
    return this.provider;
  }
}
