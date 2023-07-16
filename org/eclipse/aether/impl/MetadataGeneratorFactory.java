package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.installation.InstallRequest;

public interface MetadataGeneratorFactory {
  MetadataGenerator newInstance(RepositorySystemSession paramRepositorySystemSession, InstallRequest paramInstallRequest);
  
  MetadataGenerator newInstance(RepositorySystemSession paramRepositorySystemSession, DeployRequest paramDeployRequest);
  
  float getPriority();
}
