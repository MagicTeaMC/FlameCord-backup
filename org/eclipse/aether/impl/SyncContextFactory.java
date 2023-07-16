package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.SyncContext;

public interface SyncContextFactory {
  SyncContext newInstance(RepositorySystemSession paramRepositorySystemSession, boolean paramBoolean);
}
