package org.apache.maven.model.profile;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ProfileActivationContext {
  List<String> getActiveProfileIds();
  
  List<String> getInactiveProfileIds();
  
  Map<String, String> getSystemProperties();
  
  Map<String, String> getUserProperties();
  
  File getProjectDirectory();
  
  Map<String, String> getProjectProperties();
}
