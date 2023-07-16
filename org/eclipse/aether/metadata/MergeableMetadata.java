package org.eclipse.aether.metadata;

import java.io.File;
import org.eclipse.aether.RepositoryException;

public interface MergeableMetadata extends Metadata {
  void merge(File paramFile1, File paramFile2) throws RepositoryException;
  
  boolean isMerged();
}
