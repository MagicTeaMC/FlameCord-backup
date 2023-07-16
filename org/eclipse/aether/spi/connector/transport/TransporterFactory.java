package org.eclipse.aether.spi.connector.transport;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.transfer.NoTransporterException;

public interface TransporterFactory {
  Transporter newInstance(RepositorySystemSession paramRepositorySystemSession, RemoteRepository paramRemoteRepository) throws NoTransporterException;
  
  float getPriority();
}
