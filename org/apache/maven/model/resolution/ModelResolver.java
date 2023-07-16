package org.apache.maven.model.resolution;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Repository;
import org.apache.maven.model.building.ModelSource;

public interface ModelResolver {
  ModelSource resolveModel(String paramString1, String paramString2, String paramString3) throws UnresolvableModelException;
  
  ModelSource resolveModel(Parent paramParent) throws UnresolvableModelException;
  
  ModelSource resolveModel(Dependency paramDependency) throws UnresolvableModelException;
  
  void addRepository(Repository paramRepository) throws InvalidRepositoryException;
  
  void addRepository(Repository paramRepository, boolean paramBoolean) throws InvalidRepositoryException;
  
  ModelResolver newCopy();
}
