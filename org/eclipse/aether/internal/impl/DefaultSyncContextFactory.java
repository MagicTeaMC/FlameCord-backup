package org.eclipse.aether.internal.impl;

import java.util.Collection;
import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.SyncContext;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.impl.SyncContextFactory;
import org.eclipse.aether.metadata.Metadata;

@Named
public class DefaultSyncContextFactory implements SyncContextFactory {
  public SyncContext newInstance(RepositorySystemSession session, boolean shared) {
    return new DefaultSyncContext();
  }
  
  static class DefaultSyncContext implements SyncContext {
    public void acquire(Collection<? extends Artifact> artifact, Collection<? extends Metadata> metadata) {}
    
    public void close() {}
  }
}
