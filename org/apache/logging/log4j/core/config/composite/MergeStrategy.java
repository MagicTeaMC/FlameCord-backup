package org.apache.logging.log4j.core.config.composite;

import org.apache.logging.log4j.core.config.AbstractConfiguration;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.util.PluginManager;

public interface MergeStrategy {
  void mergeRootProperties(Node paramNode, AbstractConfiguration paramAbstractConfiguration);
  
  void mergConfigurations(Node paramNode1, Node paramNode2, PluginManager paramPluginManager);
}
