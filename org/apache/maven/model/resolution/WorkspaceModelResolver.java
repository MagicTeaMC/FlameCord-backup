package org.apache.maven.model.resolution;

import org.apache.maven.model.Model;

public interface WorkspaceModelResolver {
  Model resolveRawModel(String paramString1, String paramString2, String paramString3) throws UnresolvableModelException;
  
  Model resolveEffectiveModel(String paramString1, String paramString2, String paramString3) throws UnresolvableModelException;
}
