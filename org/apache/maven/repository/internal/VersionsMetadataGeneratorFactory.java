package org.apache.maven.repository.internal;

import javax.inject.Named;
import javax.inject.Singleton;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.installation.InstallRequest;

@Named("versions")
@Singleton
public class VersionsMetadataGeneratorFactory implements MetadataGeneratorFactory {
  public MetadataGenerator newInstance(RepositorySystemSession session, InstallRequest request) {
    return new VersionsMetadataGenerator(session, request);
  }
  
  public MetadataGenerator newInstance(RepositorySystemSession session, DeployRequest request) {
    return new VersionsMetadataGenerator(session, request);
  }
  
  public float getPriority() {
    return 5.0F;
  }
}
