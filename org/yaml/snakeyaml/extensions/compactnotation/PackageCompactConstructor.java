package org.yaml.snakeyaml.extensions.compactnotation;

import org.yaml.snakeyaml.LoaderOptions;

public class PackageCompactConstructor extends CompactConstructor {
  private final String packageName;
  
  public PackageCompactConstructor(String packageName) {
    super(new LoaderOptions());
    this.packageName = packageName;
  }
  
  protected Class<?> getClassForName(String name) throws ClassNotFoundException {
    if (name.indexOf('.') < 0)
      try {
        Class<?> clazz = Class.forName(this.packageName + "." + name);
        return clazz;
      } catch (ClassNotFoundException classNotFoundException) {} 
    return super.getClassForName(name);
  }
}
