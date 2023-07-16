package org.eclipse.aether.repository;

import java.io.File;
import java.util.List;
import org.eclipse.aether.artifact.Artifact;

public interface WorkspaceReader {
  WorkspaceRepository getRepository();
  
  File findArtifact(Artifact paramArtifact);
  
  List<String> findVersions(Artifact paramArtifact);
}
