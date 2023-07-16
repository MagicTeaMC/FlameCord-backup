package org.eclipse.aether.transport.http;

import java.util.Objects;
import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.transport.Transporter;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transfer.NoTransporterException;

@Named("http")
public final class HttpTransporterFactory implements TransporterFactory {
  private float priority = 5.0F;
  
  public float getPriority() {
    return this.priority;
  }
  
  public HttpTransporterFactory setPriority(float priority) {
    this.priority = priority;
    return this;
  }
  
  public Transporter newInstance(RepositorySystemSession session, RemoteRepository repository) throws NoTransporterException {
    Objects.requireNonNull("session", "session cannot be null");
    Objects.requireNonNull("repository", "repository cannot be null");
    return (Transporter)new HttpTransporter(repository, session);
  }
}
