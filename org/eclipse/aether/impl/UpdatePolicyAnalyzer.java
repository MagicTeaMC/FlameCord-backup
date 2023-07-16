package org.eclipse.aether.impl;

import org.eclipse.aether.RepositorySystemSession;

public interface UpdatePolicyAnalyzer {
  String getEffectiveUpdatePolicy(RepositorySystemSession paramRepositorySystemSession, String paramString1, String paramString2);
  
  boolean isUpdatedRequired(RepositorySystemSession paramRepositorySystemSession, long paramLong, String paramString);
}
