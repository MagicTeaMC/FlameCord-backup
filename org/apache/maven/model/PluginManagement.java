package org.apache.maven.model;

import java.io.Serializable;

public class PluginManagement extends PluginContainer implements Serializable, Cloneable {
  public PluginManagement clone() {
    try {
      PluginManagement copy = (PluginManagement)super.clone();
      return copy;
    } catch (Exception ex) {
      throw (RuntimeException)(new UnsupportedOperationException(getClass().getName() + " does not support clone()"))
        .initCause(ex);
    } 
  }
}
