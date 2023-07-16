package org.eclipse.aether.internal.impl;

import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicy;
import org.eclipse.aether.spi.connector.checksum.ChecksumPolicyProvider;
import org.eclipse.aether.transfer.TransferResource;

@Named
public final class DefaultChecksumPolicyProvider implements ChecksumPolicyProvider {
  private static final int ORDINAL_IGNORE = 0;
  
  private static final int ORDINAL_WARN = 1;
  
  private static final int ORDINAL_FAIL = 2;
  
  public ChecksumPolicy newChecksumPolicy(RepositorySystemSession session, RemoteRepository repository, TransferResource resource, String policy) {
    if ("ignore".equals(policy))
      return null; 
    if ("fail".equals(policy))
      return new FailChecksumPolicy(resource); 
    return new WarnChecksumPolicy(resource);
  }
  
  public String getEffectiveChecksumPolicy(RepositorySystemSession session, String policy1, String policy2) {
    if (policy1 != null && policy1.equals(policy2))
      return policy1; 
    int ordinal1 = ordinalOfPolicy(policy1);
    int ordinal2 = ordinalOfPolicy(policy2);
    if (ordinal2 < ordinal1)
      return (ordinal2 != 1) ? policy2 : "warn"; 
    return (ordinal1 != 1) ? policy1 : "warn";
  }
  
  private static int ordinalOfPolicy(String policy) {
    if ("fail".equals(policy))
      return 2; 
    if ("ignore".equals(policy))
      return 0; 
    return 1;
  }
}
