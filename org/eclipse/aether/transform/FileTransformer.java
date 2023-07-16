package org.eclipse.aether.transform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.eclipse.aether.artifact.Artifact;

public interface FileTransformer {
  Artifact transformArtifact(Artifact paramArtifact);
  
  InputStream transformData(File paramFile) throws IOException, TransformException;
}
