package org.eclipse.aether.internal.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Named;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.RepositoryConnectorProvider;
import org.eclipse.aether.repository.Authentication;
import org.eclipse.aether.repository.Proxy;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.spi.connector.RepositoryConnector;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.locator.Service;
import org.eclipse.aether.spi.locator.ServiceLocator;
import org.eclipse.aether.transfer.NoRepositoryConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Named
public class DefaultRepositoryConnectorProvider implements RepositoryConnectorProvider, Service {
  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultRepositoryConnectorProvider.class);
  
  private Collection<RepositoryConnectorFactory> connectorFactories = new ArrayList<>();
  
  public DefaultRepositoryConnectorProvider() {}
  
  @Inject
  DefaultRepositoryConnectorProvider(Set<RepositoryConnectorFactory> connectorFactories) {
    setRepositoryConnectorFactories(connectorFactories);
  }
  
  public void initService(ServiceLocator locator) {
    this.connectorFactories = locator.getServices(RepositoryConnectorFactory.class);
  }
  
  public DefaultRepositoryConnectorProvider addRepositoryConnectorFactory(RepositoryConnectorFactory factory) {
    this.connectorFactories.add(Objects.requireNonNull(factory, "repository connector factory cannot be null"));
    return this;
  }
  
  public DefaultRepositoryConnectorProvider setRepositoryConnectorFactories(Collection<RepositoryConnectorFactory> factories) {
    if (factories == null) {
      this.connectorFactories = new ArrayList<>();
    } else {
      this.connectorFactories = factories;
    } 
    return this;
  }
  
  public RepositoryConnector newRepositoryConnector(RepositorySystemSession session, RemoteRepository repository) throws NoRepositoryConnectorException {
    Objects.requireNonNull(repository, "remote repository cannot be null");
    PrioritizedComponents<RepositoryConnectorFactory> factories = new PrioritizedComponents<>(session);
    for (RepositoryConnectorFactory factory : this.connectorFactories)
      factories.add(factory, factory.getPriority()); 
    List<NoRepositoryConnectorException> errors = new ArrayList<>();
    for (PrioritizedComponent<RepositoryConnectorFactory> factory : factories.getEnabled()) {
      try {
        RepositoryConnector connector = ((RepositoryConnectorFactory)factory.getComponent()).newInstance(session, repository);
        if (LOGGER.isDebugEnabled()) {
          StringBuilder stringBuilder = new StringBuilder(256);
          stringBuilder.append("Using connector ").append(connector.getClass().getSimpleName());
          Utils.appendClassLoader(stringBuilder, connector);
          stringBuilder.append(" with priority ").append(factory.getPriority());
          stringBuilder.append(" for ").append(repository.getUrl());
          Authentication auth = repository.getAuthentication();
          if (auth != null)
            stringBuilder.append(" with ").append(auth); 
          Proxy proxy = repository.getProxy();
          if (proxy != null) {
            stringBuilder.append(" via ").append(proxy.getHost()).append(':').append(proxy.getPort());
            auth = proxy.getAuthentication();
            if (auth != null)
              stringBuilder.append(" with ").append(auth); 
          } 
          LOGGER.debug(stringBuilder.toString());
        } 
        return connector;
      } catch (NoRepositoryConnectorException e) {
        errors.add(e);
      } 
    } 
    if (LOGGER.isDebugEnabled() && errors.size() > 1)
      for (Exception e : errors)
        LOGGER.debug("Could not obtain connector factory for {}", repository, e);  
    StringBuilder buffer = new StringBuilder(256);
    if (factories.isEmpty()) {
      buffer.append("No connector factories available");
    } else {
      buffer.append("Cannot access ").append(repository.getUrl());
      buffer.append(" with type ").append(repository.getContentType());
      buffer.append(" using the available connector factories: ");
      factories.list(buffer);
    } 
    throw new NoRepositoryConnectorException(repository, buffer.toString(), (errors.size() == 1) ? (NoRepositoryConnectorException)errors.get(0) : null);
  }
}
