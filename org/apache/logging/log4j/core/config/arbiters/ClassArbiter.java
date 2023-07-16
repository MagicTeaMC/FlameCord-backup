package org.apache.logging.log4j.core.config.arbiters;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginBuilderFactory;
import org.apache.logging.log4j.util.LoaderUtil;

@Plugin(name = "ClassArbiter", category = "Core", elementType = "Arbiter", printObject = true, deferChildren = true)
public class ClassArbiter implements Arbiter {
  private final String className;
  
  private ClassArbiter(String className) {
    this.className = className;
  }
  
  public boolean isCondition() {
    return LoaderUtil.isClassAvailable(this.className);
  }
  
  @PluginBuilderFactory
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public static class Builder implements org.apache.logging.log4j.core.util.Builder<ClassArbiter> {
    public static final String ATTR_CLASS_NAME = "className";
    
    @PluginBuilderAttribute("className")
    private String className;
    
    public Builder setClassName(String className) {
      this.className = className;
      return asBuilder();
    }
    
    public Builder asBuilder() {
      return this;
    }
    
    public ClassArbiter build() {
      return new ClassArbiter(this.className);
    }
  }
}
