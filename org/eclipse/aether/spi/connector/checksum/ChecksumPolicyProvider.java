package org.eclipse.aether.spi.connector.checksum;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.TransferResource;

public interface ChecksumPolicyProvider {
  ChecksumPolicy newChecksumPolicy(RepositorySystemSession paramRepositorySystemSession, RemoteRepository paramRemoteRepository, TransferResource paramTransferResource, String paramString);
  
  String getEffectiveChecksumPolicy(RepositorySystemSession paramRepositorySystemSession, String paramString1, String paramString2);
}
