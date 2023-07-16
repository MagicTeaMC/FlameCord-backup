package org.apache.maven.repository.internal;

import javax.inject.Named;
import javax.inject.Singleton;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.installation.InstallRequest;

@Named("snapshot")
@Singleton
public class SnapshotMetadataGeneratorFactory implements MetadataGeneratorFactory {
  public MetadataGenerator newInstance(RepositorySystemSession session, InstallRequest request) {
    return new LocalSnapshotMetadataGenerator(session, request);
  }
  
  public MetadataGenerator newInstance(RepositorySystemSession session, DeployRequest request) {
    return new RemoteSnapshotMetadataGenerator(session, request);
  }
  
  public float getPriority() {
    return 10.0F;
  }
}
