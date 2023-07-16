package org.apache.maven.repository.internal;

import org.apache.maven.model.Model;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.repository.WorkspaceReader;

public interface MavenWorkspaceReader extends WorkspaceReader {
  Model findModel(Artifact paramArtifact);
}
