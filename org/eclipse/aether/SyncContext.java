package org.eclipse.aether;

import java.io.Closeable;
import java.util.Collection;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.metadata.Metadata;

public interface SyncContext extends Closeable {
  void acquire(Collection<? extends Artifact> paramCollection, Collection<? extends Metadata> paramCollection1);
  
  void close();
}
